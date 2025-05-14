package com.example.login4;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioServicio {

    private static final String BASE_URL = "https://acortadorphp-production.up.railway.app/";

    private static Retrofit retrofit = null;

    public static UsuarioApi getUsuarioApi(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UsuarioApi.class);
    }
}
