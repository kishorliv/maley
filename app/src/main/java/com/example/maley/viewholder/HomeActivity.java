package com.example.maley.viewholder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.example.maley.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatButton;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.maley.ui.HomeFragment;
import com.example.maley.ui.CameraFragment;
import com.example.maley.ui.ListFragment;
//import com.example.maley.ui.SettingsFragment;
import com.example.maley.utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    final Fragment homeFragment = new HomeFragment();
    final Fragment listFragment = new ListFragment();
    final Fragment cameraFragment = new CameraFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    private ImageButton mSignOut;

    private Context mContext = HomeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSignOut = findViewById(R.id.image_dots);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view_bar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        fm.beginTransaction().add(R.id.main_container, cameraFragment, getString(R.string.fragment_camera)).hide(cameraFragment).commit();
        fm.beginTransaction().add(R.id.main_container, listFragment, getString(R.string.fragment_list)).hide(listFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, getString(R.string.fragment_home)).commit();

        if(!isConnected(mContext)){
            showNoInternetDialog();
        }

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    return true;

                case R.id.menu_list:
                    fm.beginTransaction().hide(active).show(listFragment).commit();
                    active = listFragment;
                    return true;

                case R.id.menu_add:
                    startActivity(new Intent(mContext, AddFoodActivity.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_camera:
                    fm.beginTransaction().hide(active).show(cameraFragment).commit();
                    active = cameraFragment;
                    return true;
            }
            return false;
        }
    };

    private void showNoInternetDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected(mContext)){
            showNoInternetDialog();
        }
    }

}
