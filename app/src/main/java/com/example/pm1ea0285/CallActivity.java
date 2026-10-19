package com.example.pm1ea0285;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pm1ea0285.model.Contact;

import java.io.File;

public class CallActivity extends AppCompatActivity {

    private ImageView ivCallPhoto;
    private TextView tvCallName, tvCallPhone;
    private Button btnConfirmCall, btnCancelCall;

    private Contact contact;
    private ActivityResultLauncher<String> callPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        ivCallPhoto = findViewById(R.id.ivCallPhoto);
        tvCallName = findViewById(R.id.tvCallName);
        tvCallPhone = findViewById(R.id.tvCallPhone);
        btnConfirmCall = findViewById(R.id.btnConfirmCall);
        btnCancelCall = findViewById(R.id.btnCancelCall);

        contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            tvCallName.setText(contact.getName());
            tvCallPhone.setText(contact.getFullPhoneNumber());
            if (contact.getPhotoPath() != null && !contact.getPhotoPath().isEmpty()) {
                File imgFile = new File(contact.getPhotoPath());
                if (imgFile.exists()) {
                    ivCallPhoto.setImageURI(Uri.fromFile(imgFile));
                } else {
                    ivCallPhoto.setImageResource(R.drawable.imagen_default);
                }
            } else {
                ivCallPhoto.setImageResource(R.drawable.imagen_default);
            }
        }

        setupCallPermissionLauncher();

        btnConfirmCall.setOnClickListener(v -> showCallConfirmationDialog());

        btnCancelCall.setOnClickListener(v -> finish());
    }

    private void setupCallPermissionLauncher() {
        callPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        makePhoneCall();
                    } else {
                        Toast.makeText(this, "Se requiere permiso de llamada (CALL_PHONE) para realizar la llamada", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void showCallConfirmationDialog() {
        if (contact == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Llamada")
                .setMessage("¿Desea llamar a " + contact.getName() + "?")
                .setPositiveButton("Llamar", (dialog, which) -> checkCallPermissionAndMakeCall())
                .setNegativeButton("Cancelar", (dialog, which) -> Toast.makeText(this, "Llamada cancelada", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void checkCallPermissionAndMakeCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makePhoneCall();
        } else {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void makePhoneCall() {
        if (contact == null) return;

        String phoneNumber = contact.getCountryCode() + contact.getPhone();
        // Clean spaces if any
        phoneNumber = phoneNumber.replaceAll("\\s+", "");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        try {
            startActivity(callIntent);
            Toast.makeText(this, "Marcando a " + contact.getName() + " (" + phoneNumber + ")", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de seguridad: Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
        }
    }
}
