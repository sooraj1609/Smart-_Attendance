package com.example.smartattendance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button btnDashboard, btnScanner, btnViewAttendance;

    // --- Recommended: Use the Activity Result Launcher for scanning ---
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    // This happens if the user presses the back button
                    Toast.makeText(MainActivity.this, "Scan cancelled", Toast.LENGTH_LONG).show();
                } else {
                    // The content of the scanned QR code is available here
                    // For example, you can show it in a Toast
                    String scannedData = result.getContents();
                    Toast.makeText(MainActivity.this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();

                    // You could also pass this data to another activity or process it here.
                    // For example, finding the student in Firebase and marking them present.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnScanner = findViewById(R.id.btnScanner);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);

        // This is correct
        btnDashboard.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DashboardActivity.class)));

        // --- Correct way to launch the scanner ---
        btnScanner.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan a QR code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(QRScannerActivity.class); // Use our custom class
            barcodeLauncher.launch(options);
        });

        // This is correct
        btnViewAttendance.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewAttendanceActivity.class)));
    }
}
