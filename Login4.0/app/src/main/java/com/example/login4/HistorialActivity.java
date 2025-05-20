package com.example.login4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class HistorialActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private HistorialAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerView = findViewById(R.id.recyclerHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        String email = account.getEmail();

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "No se proporcionó un email válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ojglez.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HistorialApi api = retrofit.create(HistorialApi.class);

        // Llamar a la API
        api.obtenerHistorial(email).enqueue(new Callback<List<HistorialItem>>() {
            @Override
            public void onResponse(Call<List<HistorialItem>> call, Response<List<HistorialItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistorialItem> items = response.body();
                    adapter = new HistorialAdapter(items);
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("Historial", "Error en respuesta: " + response.code());
                    Toast.makeText(HistorialActivity.this, "No se pudo cargar el historial", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistorialItem>> call, Throwable t) {
                Log.e("Historial", "Error de red: " + t.getMessage(), t);
                Toast.makeText(HistorialActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Interface de Retrofit
    interface HistorialApi {
        @GET("historial.php")
        Call<List<HistorialItem>> obtenerHistorial(@Query("email") String email);
    }
}