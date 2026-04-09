package com.example.smartattendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class DashboardActivity extends AppCompatActivity {

    private EditText edtName, edtRoll;
    private ImageView imgQR;
    private Button btnGenerate, btnSave, btnScan; // Added btnScan
    private DatabaseReference databaseReference;

    // Modern way to handle activity results for scanning
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(DashboardActivity.this, "Scan Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    // The scanned QR code content is in result.getContents()
                    Toast.makeText(DashboardActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    // You can process the scanned data here (e.g., mark attendance)
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtRoll = findViewById(R.id.edtRoll);
        imgQR = findViewById(R.id.imgQR);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnSave = findViewById(R.id.btnSave);
        btnScan = findViewById(R.id.btnScan); // Initialize the new button

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // Set OnClickListener for the Generate QR button
        btnGenerate.setOnClickListener(v -> generateQRCode());

        // Set OnClickListener for the Save to Firebase button
        btnSave.setOnClickListener(v -> saveStudentData());

        // Set OnClickListener for the Scan QR button
        btnScan.setOnClickListener(this::openQRScanner);
    }

    /**
     * Generates a QR code from the name and roll number and displays it.
     */
    private void generateQRCode() {
        String name = edtName.getText().toString().trim();
        String roll = edtRoll.getText().toString().trim();

        if (name.isEmpty() || roll.isEmpty()) {
            Toast.makeText(this, "Please enter all details to generate a QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        String data = name + "," + roll;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            imgQR.setImageBitmap(bitmap);
            Toast.makeText(this, "QR Code Generated", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the student's name and roll number to Firebase Realtime Database.
     */
    private void saveStudentData() {
        String name = edtName.getText().toString().trim();
        String roll = edtRoll.getText().toString().trim();

        if (name.isEmpty() || roll.isEmpty()) {
            Toast.makeText(this, "Name and Roll Number cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(name, roll);
        databaseReference.child(roll).setValue(student)
                .addOnSuccessListener(aVoid -> Toast.makeText(DashboardActivity.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DashboardActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method is called when the "Scan QR" button is clicked.
     * It launches the QR code scanner.
     * @param view The view that was clicked (the button).
     */
    public void openQRScanner(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR code for attendance");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class); // Use our custom scanner activity
        barcodeLauncher.launch(options);
    }
}
