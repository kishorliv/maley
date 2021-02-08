package com.example.maley.model;

public class FoodPostBody {
    private String query;

    public FoodPostBody(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
