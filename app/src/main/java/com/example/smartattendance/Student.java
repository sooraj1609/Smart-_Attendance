package com.example.smartattendance;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties // This is good practice for Firebase models
public class Student {

    public String name;
    public String roll;

    // A public, no-argument constructor is REQUIRED for Firebase to deserialize data
    public Student() {
    }

    public Student(String name, String roll) {
        this.name = name;
        this.roll = roll;
    }
}
