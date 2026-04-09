package com.example.smartattendance;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRScannerActivity extends AppCompatActivity {

    DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance");

        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan student QR code");
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivity.class);

        qrLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String[] data = result.getContents().split(",");
                    String name = data[0];
                    String roll = data[1];

                    attendanceRef.child(roll).setValue(name);
                    Toast.makeText(this, "Attendance marked for " + name, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                }
                finish();
            });
}
