package com.example.smartattendance;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAttendanceActivity extends AppCompatActivity {

    private ListView lvAttendanceRecords;
    private DatabaseReference databaseReference;
    private ArrayList<String> studentList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        // 1. Initialize Views and Firebase Reference
        lvAttendanceRecords = findViewById(R.id.listView);
        // Point to the "Students" node where your data is saved
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // 2. Initialize the ArrayList and ArrayAdapter
        studentList = new ArrayList<>();
        // The adapter will take data from the studentList and display it in the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        lvAttendanceRecords.setAdapter(adapter);

        // 3. Attach a listener to read the data from Firebase
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Clear the list before adding new data to avoid duplicates
                studentList.clear();

                // 4. Iterate through all the children (students) in the "Students" node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming you have a Student class with 'name' and 'roll' fields
                    Student student = snapshot.getValue(Student.class);

                    if (student != null) {
                        // Create a formatted string to display in the list
                        String studentRecord = "Name: " + student.name + "\nRoll No: " + student.roll;
                        studentList.add(studentRecord);
                    }
                }

                // 5. Notify the adapter that the data has changed, so it can update the UI
                adapter.notifyDataSetChanged();

                // Optional: Show a message if no records are found
                if (studentList.isEmpty()) {
                    Toast.makeText(ViewAttendanceActivity.this, "No attendance records found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method is called if there is an error reading the data
                Toast.makeText(ViewAttendanceActivity.this, "Failed to load records: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
