package com.example.liew.ideliveryserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinates {
    @GET("maps/api/geocode/json?key=yourkey")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("maps/api/directions/json?key=yourkey")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);
}
