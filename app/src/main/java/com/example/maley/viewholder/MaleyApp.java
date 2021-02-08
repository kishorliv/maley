package com.example.maley.viewholder;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MaleyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
