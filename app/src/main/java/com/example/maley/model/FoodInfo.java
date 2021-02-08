package com.example.maley.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FoodInfo {

    private String foodname;
    private String calorie;
    private String protein;
    private String fat;
    private String carbs;

    private String userId;
    private String email;
    private String currentTime;

    public FoodInfo() { }

    public FoodInfo(String foodname, String calorie, String protein, String fat, String carbs, String userId, String email, String currentTime) {
        this.foodname = foodname;
        this.calorie = calorie;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.userId = userId;
        this.email = email;
        this.currentTime = currentTime;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "FoodInfo{" +
                "foodname='" + foodname + '\'' +
                ", calorie='" + calorie + '\'' +
                ", protein='" + protein + '\'' +
                ", fat='" + fat + '\'' +
                ", carbs='" + carbs + '\'' +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", currentTime=" + currentTime +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foodname", foodname);
        result.put("calorie", calorie);
        result.put("protein", protein);
        result.put("fat", fat);
        result.put("carbs", carbs);
        result.put("userId", userId);
        result.put("email", email);
        result.put("currentTime", currentTime);

        return result;
    }
}
