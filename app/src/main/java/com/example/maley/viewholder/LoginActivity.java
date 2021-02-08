package com.example.maley.viewholder;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Toast;
import com.example.maley.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

import com.example.maley.model.User;
import com.example.maley.utils.BaseActivity;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 12345;

    private Context mContext = LoginActivity.this;

    private FirebaseDatabase mFirebaseDatabase; // entry point for app to access the database
    private DatabaseReference mUsersReference; // reference to the users node
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){ // user is signed in
                    hideProgressDialog();
                    writeNewUser(user.getDisplayName(), user.getEmail(),user.getUid());
                    startActivity(new Intent(mContext, HomeActivity.class));
                    finish();
                } else { // user is not signed in
                    System.out.println("User is not signed in. Launching sign in flow.");
                    createSignInIntent();
                }
            }
        };

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            } else if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                Toast.makeText(mContext, "Poor internet connection!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void createSignInIntent(){
        // authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        // create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.ic_logo_foreground)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void writeNewUser(final String name, final String email, final String userId){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                User user = new User(name, email, deviceToken);

                mUsersReference.child(userId).setValue(user);
            }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthStateListener != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mAuthStateListener = null;
    }

}