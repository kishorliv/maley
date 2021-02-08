package com.example.maley.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maley.R;
import com.example.maley.model.FoodInfo;

public class UserFoodsViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "FoodsViewHolder";

    public TextView currentTime, calorie, fat, protein, carbs, foodName;
    public ImageView deleteIcon, editIcon;

    public UserFoodsViewHolder(final View itemView) {
        super(itemView);
        currentTime = itemView.findViewById(R.id.text_current_time);
        calorie = itemView.findViewById(R.id.text_calorie);
        fat = itemView.findViewById(R.id.text_fat);
        protein = itemView.findViewById(R.id.text_protein);
        carbs = itemView.findViewById(R.id.text_carbs);
        foodName = itemView.findViewById(R.id.text_food_name);
        deleteIcon = itemView.findViewById(R.id.image_delete_icon);
        editIcon = itemView.findViewById(R.id.image_edit_icon);
    }

    public void bindToPost(FoodInfo foodAdd, View.OnClickListener deleteClickListener, View.OnClickListener editClickListener){
        currentTime.setText(foodAdd.getCurrentTime());
        calorie.setText(foodAdd.getCalorie());
        fat.setText(foodAdd.getFat());
        protein.setText(foodAdd.getProtein());
        carbs.setText(foodAdd.getCarbs());
        foodName.setText(foodAdd.getFoodname());

        deleteIcon.setOnClickListener(deleteClickListener);
        editIcon.setOnClickListener(editClickListener);
    }
}

