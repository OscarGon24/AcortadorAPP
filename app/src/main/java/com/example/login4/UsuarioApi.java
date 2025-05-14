package com.example.login4;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UsuarioApi {
    @POST("insert_user.php")
    Call<JsonObject> crearUsuario(@Body Usuario usuario);

    @GET("get_user.php")
    Call<JsonObject> obtenerIntentos(@Query("email") String email);

    @POST("update_intentos.php")
    Call<JsonObject> actualizarIntentos(@Body JsonObject body);

    @PUT("update_user.php")
    Call<JsonObject> actualizarUsuario(@Body Usuario usuario);

}
