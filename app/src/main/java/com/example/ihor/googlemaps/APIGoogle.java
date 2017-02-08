package com.example.ihor.googlemaps;


import com.example.ihor.googlemaps.placesJson.PlacesJson;
import com.example.ihor.googlemaps.placesJsonСoordinates.PlacesJsonСoordinates;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface APIGoogle {
    @GET("maps/api/place/autocomplete/json")
    Call<PlacesJson> getPlaces(@Query("input") String resourceInput, @Query("types") String resourceTypes, @Query("key") String resourceKey);

    @GET("maps/api/place/details/json")
    Call<PlacesJsonСoordinates> getPlacesСoordinates(@Query("placeid") String resourcePlaceid, @Query("key") String resourceKey);
}