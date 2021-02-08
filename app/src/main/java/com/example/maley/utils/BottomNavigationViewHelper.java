package com.example.maley.utils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
    }
}

