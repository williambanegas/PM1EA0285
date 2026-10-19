package com.example.pm1ea0285;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pm1ea0285.dao.ContactDao;
import com.example.pm1ea0285.model.Contact;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView ivRegisterPhoto;
    private Button btnCapturePhoto, btnSave, btnClear, btnSavedContacts, btnAddCountry;
    private Spinner spinnerCountryCode;
    private TextInputEditText etName, etPhone, etNote;

    private String currentPhotoPath = "";
    private Uri photoUri;
    private ContactDao contactDao;
    private int editingContactId = -1;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactDao = new ContactDao(this);
        contactDao.open();

        ivRegisterPhoto = findViewById(R.id.ivRegisterPhoto);
        btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnAddCountry = findViewById(R.id.btnAddCountry);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        btnSavedContacts = findViewById(R.id.btnSavedContacts);

        loadCountriesSpinner();
        btnAddCountry.setOnClickListener(v -> showAddCountryDialog());
        setupCameraLauncher();

        btnCapturePhoto.setOnClickListener(v -> checkCameraPermissionAndOpen());

        btnSave.setOnClickListener(v -> validateAndSaveContact());

        btnClear.setOnClickListener(v -> clearForm());

        btnSavedContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            startActivity(intent);
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getBooleanExtra("clear_form", false)) {
            clearForm();
        } else if (intent.hasExtra("contact_id")) {
            editingContactId = intent.getIntExtra("contact_id", -1);
            if (editingContactId != -1) {
                loadContactForEditing(editingContactId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDao != null) {
            contactDao.close();
        }
    }

    private void loadCountriesSpinner() {
        List<String> countries = contactDao.getAllCountries();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        spinnerCountryCode.setAdapter(adapter);
    }

    private void showAddCountryDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        final TextInputEditText inputName = new TextInputEditText(this);
        inputName.setHint("Nombre del país (ej. México)");
        layout.addView(inputName);

        final TextInputEditText inputCode = new TextInputEditText(this);
        inputCode.setHint("Código de país (ej. +52)");
        inputCode.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(inputCode);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Nuevo País")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String name = inputName.getText() != null ? inputName.getText().toString().trim() : "";
                    String code = inputCode.getText() != null ? inputCode.getText().toString().trim() : "";

                    if (name.isEmpty() || code.isEmpty()) {
                        Toast.makeText(this, "Debe ingresar el nombre y el código del país", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!code.startsWith("+")) {
                        code = "+" + code;
                    }

                    contactDao.insertCountry(name, code);
                    loadCountriesSpinner();
                    Toast.makeText(this, "País agregado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Show confirmation dialog: Accept or Reject image
                        showImageConfirmationDialog();
                    } else {
                        currentPhotoPath = "";
                        Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(this, "Se requiere permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                cameraLauncher.launch(takePictureIntent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir la cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showImageConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Fotografía")
                .setMessage("¿Desea aceptar esta fotografía?")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    ivRegisterPhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                    Toast.makeText(this, "Fotografía aceptada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Rechazar (Repetir)", (dialog, which) -> {
                    currentPhotoPath = "";
                    ivRegisterPhoto.setImageResource(R.drawable.imagen_default);
                    Toast.makeText(this, "Fotografía rechazada", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void validateAndSaveContact() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";
        String selectedCountryFull = spinnerCountryCode.getSelectedItem().toString();
        // Extract country code e.g. "+504" from "Honduras (+504)"
        String countryCode = selectedCountryFull.substring(selectedCountryFull.indexOf("(")).replace("(", "").replace(")", "").trim();

        // Validations
        if (name.isEmpty()) {
            showAlert("Debe ingresar el nombre del contacto.");
            return;
        }
        if (!name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            showAlert("El nombre del contacto solo debe contener letras.");
            return;
        }
        if (phone.isEmpty()) {
            showAlert("Debe ingresar el número de teléfono del contacto.");
            return;
        }
        if (!phone.matches("^\\d{8}$")) {
            showAlert("El número de teléfono debe ser válido y contener exactamente 8 dígitos numéricos.");
            return;
        }
        if (note.isEmpty()) {
            showAlert("Debe ingresar una nota para el contacto.");
            return;
        }
        if (selectedCountryFull.isEmpty()) {
            showAlert("Debe seleccionar un país de origen.");
            return;
        }

        Contact contact = new Contact();
        contact.setName(name);
        contact.setCountryCode(countryCode);
        contact.setPhone(phone);
        contact.setNote(note);
        contact.setPhotoPath(currentPhotoPath);

        if (editingContactId != -1) {
            contact.setId(editingContactId);
            contactDao.updateContact(contact);
            Toast.makeText(this, "Contacto modificado exitosamente", Toast.LENGTH_SHORT).show();
            editingContactId = -1;
        } else {
            contactDao.insertContact(contact);
            Toast.makeText(this, "Contacto guardado exitosamente", Toast.LENGTH_SHORT).show();
        }

        clearForm();
    }

    private void clearForm() {
        etName.setText("");
        etPhone.setText("");
        etNote.setText("");
        spinnerCountryCode.setSelection(0);
        ivRegisterPhoto.setImageResource(R.drawable.imagen_default);
        currentPhotoPath = "";
        editingContactId = -1;
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Validación")
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void loadContactForEditing(int contactId) {
        Contact contact = contactDao.getContactById(contactId);
        if (contact != null) {
            etName.setText(contact.getName());
            etPhone.setText(contact.getPhone());
            etNote.setText(contact.getNote());
            currentPhotoPath = contact.getPhotoPath() != null ? contact.getPhotoPath() : "";
            if (!currentPhotoPath.isEmpty()) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    ivRegisterPhoto.setImageURI(Uri.fromFile(imgFile));
                } else {
                    ivRegisterPhoto.setImageResource(R.drawable.imagen_default);
                }
            } else {
                ivRegisterPhoto.setImageResource(R.drawable.imagen_default);
            }
            // Set spinner selection based on country code
            String code = contact.getCountryCode();
            for (int i = 0; i < spinnerCountryCode.getCount(); i++) {
                String item = spinnerCountryCode.getItemAtPosition(i).toString();
                if (item.contains(code)) {
                    spinnerCountryCode.setSelection(i);
                    break;
                }
            }
        }
    }
}
