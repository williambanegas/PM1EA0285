package com.example.pm1ea0285;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm1ea0285.adapter.ContactAdapter;
import com.example.pm1ea0285.dao.ContactDao;
import com.example.pm1ea0285.model.Contact;

import java.io.File;
import java.util.List;

public class ContactListActivity extends AppCompatActivity implements ContactAdapter.OnContactSelectedListener {

    private RecyclerView recyclerViewContacts;
    private EditText etSearch;
    private Button btnBackToRegister;
    private Button btnListCall, btnListShare, btnListPhoto, btnListEdit, btnListDelete;

    private ContactDao contactDao;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactDao = new ContactDao(this);
        contactDao.open();

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        etSearch = findViewById(R.id.etSearch);
        btnBackToRegister = findViewById(R.id.btnBackToRegister);

        btnListCall = findViewById(R.id.btnListCall);
        btnListShare = findViewById(R.id.btnListShare);
        btnListPhoto = findViewById(R.id.btnListPhoto);
        btnListEdit = findViewById(R.id.btnListEdit);
        btnListDelete = findViewById(R.id.btnListDelete);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        loadContacts();

        btnBackToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("clear_form", true);
            startActivity(intent);
        });

        btnListCall.setOnClickListener(v -> {
            Contact selected = contactAdapter.getSelectedContact();
            if (selected == null) {
                Toast.makeText(this, "Por favor, seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            onCallClick(selected);
        });

        btnListShare.setOnClickListener(v -> {
            Contact selected = contactAdapter.getSelectedContact();
            if (selected == null) {
                Toast.makeText(this, "Por favor, seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            onShareClick(selected);
        });

        btnListPhoto.setOnClickListener(v -> {
            Contact selected = contactAdapter.getSelectedContact();
            if (selected == null) {
                Toast.makeText(this, "Por favor, seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            onViewPhotoClick(selected);
        });

        btnListEdit.setOnClickListener(v -> {
            Contact selected = contactAdapter.getSelectedContact();
            if (selected == null) {
                Toast.makeText(this, "Por favor, seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            onEditClick(selected);
        });

        btnListDelete.setOnClickListener(v -> {
            Contact selected = contactAdapter.getSelectedContact();
            if (selected == null) {
                Toast.makeText(this, "Por favor, seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            onDeleteClick(selected);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (contactDao != null) {
            loadContacts();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDao != null) {
            contactDao.close();
        }
    }

    private void loadContacts() {
        contactList = contactDao.getAllContacts();
        if (contactAdapter == null) {
            contactAdapter = new ContactAdapter(contactList, this);
            recyclerViewContacts.setAdapter(contactAdapter);
        } else {
            contactAdapter.updateList(contactList);
        }
    }

    private void filterContacts(String query) {
        if (query.isEmpty()) {
            contactList = contactDao.getAllContacts();
        } else {
            contactList = contactDao.searchContacts(query);
        }
        contactAdapter.updateList(contactList);
    }

    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(this, "Seleccionado: " + contact.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onCallClick(Contact contact) {
        Intent intent = new Intent(ContactListActivity.this, CallActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
        Toast.makeText(this, "Abriendo pantalla de llamada para " + contact.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onShareClick(Contact contact) {
        String shareText = "Contacto:\nNombre: " + contact.getName() +
                "\nTeléfono: " + contact.getFullPhoneNumber() +
                "\nNota: " + contact.getNote();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Compartir Contacto");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        if (contact.getPhotoPath() != null && !contact.getPhotoPath().isEmpty()) {
            File photoFile = new File(contact.getPhotoPath());
            if (photoFile.exists()) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        startActivity(Intent.createChooser(shareIntent, "Compartir contacto vía"));
        Toast.makeText(this, "Compartiendo contacto: " + contact.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onViewPhotoClick(Contact contact) {
        if (contact.getPhotoPath() == null || contact.getPhotoPath().isEmpty() || !new File(contact.getPhotoPath()).exists()) {
            Toast.makeText(this, "El contacto no tiene fotografía registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageView imageView = new ImageView(this);
        imageView.setImageURI(Uri.fromFile(new File(contact.getPhotoPath())));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        new AlertDialog.Builder(this)
                .setTitle("Foto de " + contact.getName())
                .setView(imageView)
                .setPositiveButton("Cerrar", null)
                .show();

        Toast.makeText(this, "Visualizando foto de " + contact.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onEditClick(Contact contact) {
        Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
        intent.putExtra("contact_id", contact.getId());
        startActivity(intent);
        Toast.makeText(this, "Modificando contacto: " + contact.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Está seguro de eliminar a " + contact.getName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    contactDao.deleteContact(contact.getId());
                    loadContacts();
                    Toast.makeText(this, "Contacto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
