package com.example.maley.viewholder;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.maley.R;
import com.example.maley.model.FoodInfo;
import com.example.maley.model.User;
import com.example.maley.utils.BaseActivity;
import com.example.maley.utils.DateAndTime;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AddFoodActivity extends BaseActivity {
    private static final String TAG = "AddFoodActivity";
    private static final String REQUIRED = "Required";
    private Context mContext = AddFoodActivity.this;
    private EditText mFoodName, mCalorie, mFat, mCarbs, mProtein;
    private ImageView mCloseSign;
    private FloatingActionButton mAddFood;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mFoodsDatabaseReference;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mFoodName = findViewById(R.id.text_food_name);
        mCalorie = findViewById(R.id.text_calorie);
        mFat = findViewById(R.id.text_fat);
        mCarbs = findViewById(R.id.text_carbs);
        mProtein = findViewById(R.id.text_protein);
        mCloseSign = findViewById(R.id.image_close);
        mAddFood = findViewById(R.id.fab_add_food);

        Intent intent = getIntent();
        String nutritionData = intent.getStringExtra("nutritionData");
        // check if this intent came after calling the api from camera
        if(nutritionData != null){
            JsonObject nutritionObj = (JsonObject) new JsonParser().parse(nutritionData);
            Log.d("TAG", "********************* Nutrition data: " + nutritionData);
            JsonElement food_name = nutritionObj.get("food_name");
            JsonElement cal = nutritionObj.get("nf_calories");
            JsonElement fat = nutritionObj.get("nf_total_fat");
            JsonElement carbs = nutritionObj.get("nf_total_carbohydrate");
            JsonElement protein = nutritionObj.get("nf_protein");

            mFoodName.setText(food_name.toString().replace("\"", ""));
            mCalorie.setText(cal.toString().replace("\"", ""));
            mFat.setText(fat.toString().replace("\"", ""));
            mCarbs.setText(carbs.toString().replace("\"", ""));
            mProtein.setText(protein.toString().replace("\"", ""));
        }

        mCloseSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mFoodsDatabaseReference = mFirebaseDatabase.getReference().child("daily-foods");

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        enableSubmit();

        // Push the data to the database and return to the HomeActivity
        mAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked add food fab.");
                submitFood();
                Toast.makeText(getApplicationContext(), "Food added.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    // Push object to the database
    private void addFood(){
        Log.d(TAG, "addFood: pushing add food object to the database");

        if (mUser != null){
            String key = mFoodsDatabaseReference.push().getKey();
            String dailykey = DateAndTime.currentDate();

            String foodName = mFoodName.getText().toString().trim();
            String calorie = mCalorie.getText().toString().trim();
            String carbs = mCarbs.getText().toString().trim();
            String fat = mFat.getText().toString().trim();
            String protein = mProtein.getText().toString().trim();
            String userId = mUser.getUid();
            String email  = mUser.getEmail();
            String date = DateAndTime.currentTime();

            FoodInfo addFood = new FoodInfo(foodName, calorie, protein, fat, carbs, userId, email, date);
            Log.d(TAG, "addFood: Food added " + addFood.toString());

            Map<String, Object> foodValues = addFood.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/foods/" + key, foodValues);
            childUpdates.put("/user-foods/" + userId + "/" + key, foodValues);
            childUpdates.put("/daily-foods/" + userId + "/" + dailykey + "/" + key, foodValues);

            DatabaseReference dailyTotalobj = mFoodsDatabaseReference.child(userId).child(dailykey);

            dailyTotalobj.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double total = 0.0;
                    for(DataSnapshot snap : snapshot.getChildren()){
                        Map<String, Object> valueObj = (Map<String, Object>) snap.getValue();

                        for(Map.Entry<String, Object> obj : valueObj.entrySet()){
                            Log.d(TAG, "############## key: " + obj.getKey());
                            Log.d(TAG, "############## value: " + obj.getValue());
                            if(obj.getKey().equals("calorie")){
                                total += Double.parseDouble(obj.getValue().toString());
                            }
                        }
                    }
                    Log.d(TAG, "############## Total: " + total);
                    Log.d("TAG", "######################## obj daily total: " + snapshot.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext, "Error: could not fetch total cals!",Toast.LENGTH_SHORT).show();
                }

            });
            Log.d("TAG", "######################## daily total: " + dailyTotalobj.toString());
            mFirebaseDatabase.getReference().updateChildren(childUpdates);
        }
    }

    public void submitFood(){
        final String foodName = mFoodName.getText().toString().trim();
        final String calorie = mCalorie.getText().toString().trim();

        if (TextUtils.isEmpty(foodName)){
            mFoodName.setError(REQUIRED);
            return;
        }
        if(TextUtils.isEmpty(calorie)) {
            mCalorie.setError(REQUIRED);
            return;
        }

        mUsersDatabaseReference.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //User user = dataSnapshot.getValue(User.class);

                if (mUser == null) {
                    Toast.makeText(mContext, "Error: could not fetch user!",Toast.LENGTH_SHORT).show();
                } else {
                    addFood();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    public void enableSubmit(){
        mAddFood.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(mFoodName.getText().toString().trim()) &&
                        !TextUtils.isEmpty(mCalorie.getText().toString().trim())){
                    mAddFood.setEnabled(true);
                } else {
                    mAddFood.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        mFoodName.addTextChangedListener(watcher);
        mCalorie.addTextChangedListener(watcher);
    }

}