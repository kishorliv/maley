package com.example.maley.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maley.R;
import com.example.maley.utils.DateAndTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int TARGET_CALORIE = 2000; // standard calorie intake australia

    private FirebaseUser mUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFoodsDatabaseReference;

    private ImageView mGoLeft, mGoRight;
    private ProgressBar mCalorieProgress;
    private TextView mCalorieValue, mDateText, tv_calTotal, tv_calRem, tv_Protein;
    private String mTodayKey; // todays date, to be used as a child key in database to represent daily food records
    private String mCurrentKey;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTodayKey = DateAndTime.currentDate();
        mCurrentKey = mTodayKey;

        mGoLeft = view.findViewById(R.id.go_left);
        mGoRight = view.findViewById(R.id.go_right);
        mDateText = view.findViewById(R.id.text_date);
        mCalorieValue = view.findViewById(R.id.tv_circle_progress);
        mCalorieProgress = view.findViewById(R.id.custom_circle_progress);
        tv_calTotal = view.findViewById(R.id.tv_total);
        tv_calRem = view.findViewById(R.id.tv_remaining);
        tv_Protein = view.findViewById(R.id.tv_protein);

        mGoLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = DateAndTime.yesterdayDate(mCurrentKey);
                mCurrentKey = key;
                renderProgress(key);
            }
        });

        mGoRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = DateAndTime.tomorrowDate(mCurrentKey);
                mCurrentKey = key;
                renderProgress(key);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFoodsDatabaseReference = mFirebaseDatabase.getReference().child("daily-foods");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mUser.getUid();

        DatabaseReference dailyTotalobj = mFoodsDatabaseReference.child(userId).child(mTodayKey);

        dailyTotalobj.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalCal = 0.0;
                double totalProtein = 0.0;
                for(DataSnapshot snap : snapshot.getChildren()){
                    Map<String, Object> valueObj = (Map<String, Object>) snap.getValue();

                    for(Map.Entry<String, Object> obj : valueObj.entrySet()){
                        Log.d(TAG, "############## key: " + obj.getKey());
                        Log.d(TAG, "############## value: " + obj.getValue());
                        if(obj.getKey().equals("calorie")){
                            totalCal += Double.parseDouble(obj.getValue().toString());
                        }
                        if(obj.getKey().equals("protein")){
                            totalProtein += Double.parseDouble(obj.getValue().toString());
                        }

                    }
                }
                Log.d(TAG, "############## Total Home: " + totalCal);
                Log.d("TAG", "######################## obj daily total: " + snapshot.toString());


                int percent = (int)((totalCal * 100)/TARGET_CALORIE);
                SpannableStringBuilder str = new SpannableStringBuilder(Integer.toString(percent));
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mCalorieValue.setText(str+"%");
                mCalorieProgress.setProgress((int)((totalCal * 100)/TARGET_CALORIE));
                tv_calTotal.setText(Double.toString(totalCal));
                tv_calRem.setText(Double.toString(TARGET_CALORIE - totalCal));
                tv_Protein.setText(Double.toString(totalProtein));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: could not fetch total cals!",Toast.LENGTH_SHORT).show();
            }

        });

    }

    // shows progress circle with new data based on different daily keys
    private void renderProgress(String dateKey){
        String userId = mUser.getUid();
        DatabaseReference dailyTotalobj = mFoodsDatabaseReference.child(userId).child(dateKey);

        dailyTotalobj.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalCal = 0.0;
                double totalProtein = 0.0;

                for(DataSnapshot snap : snapshot.getChildren()){
                    Map<String, Object> valueObj = (Map<String, Object>) snap.getValue();

                    for(Map.Entry<String, Object> obj : valueObj.entrySet()){
                        Log.d(TAG, "############## key: " + obj.getKey());
                        Log.d(TAG, "############## value: " + obj.getValue());
                        if(obj.getKey().equals("calorie")){
                            totalCal += Double.parseDouble(obj.getValue().toString());
                        }
                        if(obj.getKey().equals("protein") && !obj.getValue().toString().isEmpty()){
                            totalProtein += Double.parseDouble(obj.getValue().toString());
                        }

                    }
                }
                Log.d(TAG, "############## Total Home: " + totalCal);
                Log.d("TAG", "######################## obj daily total: " + snapshot.toString());

                int percent = (int)((totalCal * 100)/TARGET_CALORIE);
                SpannableStringBuilder str = new SpannableStringBuilder(Integer.toString(percent));
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mCalorieValue.setText(str+"%");

                if(mTodayKey.equals(dateKey)){
                    mDateText.setText("     Today     ");
                }
                else{
                    mDateText.setText(dateKey);
                }

                //mCalorieValue.setText(Double.toString(TARGET_CALORIE - total) + " cal \n  remaining");
                mCalorieProgress.setProgress((int)((totalCal * 100)/TARGET_CALORIE));
                tv_calTotal.setText(Double.toString(totalCal));
                tv_calRem.setText(Double.toString(TARGET_CALORIE - totalCal));
                tv_Protein.setText(Double.toString(totalProtein));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: could not fetch total cals!",Toast.LENGTH_SHORT).show();
            }

        });
    }

}
