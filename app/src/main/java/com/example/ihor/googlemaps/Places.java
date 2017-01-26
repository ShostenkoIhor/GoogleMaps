package com.example.ihor.googlemaps;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Places {
    @GET("/api/get")
    Call<List<JSONObject>> getData(@Query("name") String resourceName, @Query("num") int count);
}