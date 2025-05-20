package com.example.login4;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api.php")
    Call<UrlResponse> shortenUrl(@Body UrlRequest urlRequest);
}