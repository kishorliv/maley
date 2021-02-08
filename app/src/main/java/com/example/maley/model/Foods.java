package com.example.maley.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Foods {
    private ArrayList<NutritionInfo> foods;

    public Foods(ArrayList<NutritionInfo> foods) {
        this.foods = foods;
    }

    public List<NutritionInfo> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<NutritionInfo> foods) {
        this.foods = foods;
    }

    @Override
    public String toString() {
        NutritionInfo nuinfo = null;
        String fat = "", carbs="", calorie="";
        for(NutritionInfo nu : foods){
            nuinfo = nu;
            Log.d("TAG", "****************************** Nuinfo: "+ nuinfo.toString());
            calorie = nu.getNf_calories();
            carbs = nu.getNf_total_carbohydrate();
            fat = nu.getNf_total_fat();
            break;
        }
        return nuinfo.toString();
    }
}
