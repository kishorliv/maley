package com.example.maley.api;

import com.example.maley.model.FoodPostBody;
import com.example.maley.model.Foods;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NutritionApiInterface {
    @Headers({
        "x-app-id: 28de08e3",
        "x-app-key: f1197724118dc8cabfc1f4d390b985bf",
        "x-remote-user-id: 0",
        "Content-type: application/json"
    })
    @POST("v2/natural/nutrients")
    Call<Foods> getNutritionResponse(@Body FoodPostBody foodPostBody);
}
