package com.example.login4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoTarjeta extends AppCompatActivity {

    EditText numeroTarjeta, fechaVencimiento, codigoSeguridad, titular;
    Button btnPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago_tarjeta);

        numeroTarjeta = findViewById(R.id.editNumeroTarjeta);
        fechaVencimiento = findViewById(R.id.editFechaVencimiento);
        codigoSeguridad = findViewById(R.id.editCVV);
        titular = findViewById(R.id.editNombreTitular);
        btnPagar = findViewById(R.id.btnPagar);

        btnPagar.setOnClickListener(v -> actualizarUsuarioAPremium());

    }

    private void actualizarUsuarioAPremium() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = account.getEmail();
        String nombre = account.getDisplayName();
        String tipo = "Premium";

        Usuario usuarioActualizado = new Usuario(nombre, email, tipo, 5);

        UsuarioApi usuarioApi = UsuarioServicio.getUsuarioApi();
        Call<JsonObject> call = usuarioApi.actualizarUsuario(usuarioActualizado);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PagoTarjeta.this, "Usuario actualizado a Premium", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PagoTarjeta.this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PagoTarjeta.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}