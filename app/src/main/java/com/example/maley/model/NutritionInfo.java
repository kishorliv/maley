package com.example.maley.model;

import com.google.gson.annotations.SerializedName;

public class NutritionInfo {
//    @SerializedName("foodName")
    private String food_name;
//    @SerializedName("servingUnit")
    private String serving_unit;
//    @SerializedName("calorie")
    private String nf_calories;
//    @SerializedName("fat")
    private String nf_total_fat;
//    @SerializedName("carbs")
    private String nf_total_carbohydrate;
//    @SerializedName("protein")
    private String nf_protein;

    public NutritionInfo(String food_name, String serving_unit, String nf_calories, String nf_total_fat, String nf_total_carbohydrate, String nf_protein) {
        this.food_name = food_name;
        this.serving_unit = serving_unit;
        this.nf_calories = nf_calories;
        this.nf_total_fat = nf_total_fat;
        this.nf_total_carbohydrate = nf_total_carbohydrate;
        this.nf_protein = nf_protein;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getServing_unit() {
        return serving_unit;
    }

    public void setServing_unit(String serving_unit) {
        this.serving_unit = serving_unit;
    }

    public String getNf_calories() {
        return nf_calories;
    }

    public void setNf_calories(String nf_calories) {
        this.nf_calories = nf_calories;
    }

    public String getNf_total_fat() {
        return nf_total_fat;
    }

    public void setNf_total_fat(String nf_total_fat) {
        this.nf_total_fat = nf_total_fat;
    }

    public String getNf_total_carbohydrate() {
        return nf_total_carbohydrate;
    }

    public void setNf_total_carbohydrate(String nf_total_carbohydrate) {
        this.nf_total_carbohydrate = nf_total_carbohydrate;
    }

    public String getNf_protein() {
        return nf_protein;
    }

    public void setNf_protein(String nf_protein) {
        this.nf_protein = nf_protein;
    }

    @Override
    public String toString() {
        return "{" +
                "food_name:'" + food_name + '\'' +
                ", serving_unit:'" + serving_unit + '\'' +
                ", nf_calories:'" + nf_calories + '\'' +
                ", nf_total_fat:'" + nf_total_fat + '\'' +
                ", nf_total_carbohydrate:'" + nf_total_carbohydrate + '\'' +
                ", nf_protein:'" + nf_protein + '\'' +
                '}';
    }
}
