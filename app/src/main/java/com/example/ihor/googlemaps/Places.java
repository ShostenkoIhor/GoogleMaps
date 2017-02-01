package com.example.ihor.googlemaps;

import com.example.ihor.googlemaps.placesJson.PlacesJson;
import com.example.ihor.googlemaps.placesJson.Prediction;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Places {
    @GET("maps/api/place/autocomplete/json")
    Call<PlacesJson> getData(@Query("input") String resourceInput, @Query("types") String resourceTypes, @Query("key") String resourceKey);
}