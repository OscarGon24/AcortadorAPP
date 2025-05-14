package com.example.login4;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api.php")
    Call<UrlResponse> shortenUrl(@Body UrlRequest urlRequest);
}