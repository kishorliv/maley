package com.example.maley.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.example.maley.R;
import com.example.maley.viewholder.UserFoodsViewHolder;
import com.example.maley.model.FoodInfo;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ListFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<FoodInfo, UserFoodsViewHolder> mFirebaseAdapter;

    public ListFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_list,container,false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mRecyclerView = rootView.findViewById(R.id.rv_home_profile);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Query query = mDatabaseReference.child("user-foods").child(userId);

            FirebaseRecyclerOptions options
                    = new FirebaseRecyclerOptions.Builder<FoodInfo>()
                    .setQuery(query, FoodInfo.class)
                    .build();


            mFirebaseAdapter = new FirebaseRecyclerAdapter<FoodInfo, UserFoodsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull UserFoodsViewHolder holder, int position, @NonNull FoodInfo model) {
                    final DatabaseReference foodRef = getRef(position);
                    final String foodKey = foodRef.getKey();

                    holder.bindToPost(model, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference foodsRef = mDatabaseReference.child("foods").child(foodKey);
                            foodsRef.removeValue();
                            DatabaseReference userFoodsRef = mDatabaseReference.child("user-foods").child(user.getUid()).child(foodKey);
                            userFoodsRef.removeValue();
                        }
                    }, new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            DatabaseReference userFoodsRef = mDatabaseReference.child("user-foods").child(user.getUid()).child(foodKey);
                            // Read from the database
                            userFoodsRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    Map<String, Object> valueObj = (Map<String, Object>) dataSnapshot.getValue();

                                    Log.d(TAG, "##############Valuesss: " + valueObj.values());
                                    Log.d(TAG, "##############Value is: " + valueObj);

                                    for(Map.Entry<String, Object> obj : valueObj.entrySet()){
                                        Log.d(TAG, "############## key: " + obj.getKey());
                                        Log.d(TAG, "############## value: " + obj.getValue());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "#################Failed to read value.", error.toException());
                                }
                            });



                            // send to add food activity
//                            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
//                            intent.putExtra("nutritionData", nutritionString);
//                            startActivity(intent);

                            // delete after edit
                            //userFoodsRef.removeValue();
                        }

                    });
                }

                @NonNull
                @Override
                public UserFoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    return new UserFoodsViewHolder(inflater.inflate(R.layout.item_food_info_delete, parent, false));
                }
            };
            mRecyclerView.setAdapter(mFirebaseAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFirebaseAdapter != null){
            mFirebaseAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null){
            mFirebaseAdapter.stopListening();
        }
    }
}
