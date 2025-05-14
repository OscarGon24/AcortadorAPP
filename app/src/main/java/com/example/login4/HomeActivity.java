package com.example.login4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    ImageView profileImage;
    TextView txvUsuario, txvIntentos;
    Button btnGenerar;
    EditText txvLinkoriginal;
    TextView txvLinkNuevo;
    Button btnCopiar;


    public  int intentos = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String nombre = account.getDisplayName();
            String email = account.getEmail();
            String tipo = "Free";
            Uri personPhoto = account.getPhotoUrl();

            // Configuración de la UI
            txvUsuario = findViewById(R.id.txvUsuario);
            ImageView profileImage = findViewById(R.id.imageViewProfile);
            txvIntentos = findViewById(R.id.txvIntentos);

            txvUsuario.setText("Hola, " + nombre);
            if (personPhoto != null) {
                Picasso.get().load(personPhoto).into(profileImage);
            }

            //Cargar intentos
            UsuarioApi usuarioApi = UsuarioServicio.getUsuarioApi();
            usuarioApi.obtenerIntentos(email).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body().get("success").getAsBoolean()) {
                        intentos = response.body().get("intentos").getAsInt();

                        if (intentos == -1) {
                            // Usuario premium: ocultamos los intentos
                            txvIntentos.setVisibility(View.GONE);
                        } else {
                            txvIntentos.setText(String.valueOf(intentos));
                            txvIntentos.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Toast.makeText(HomeActivity.this, "No se pudieron cargar los intentos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Error al obtener intentos", Toast.LENGTH_SHORT).show();
                }
            });


            // Configurar el clic en la imagen de perfil
            profileImage.setOnClickListener(v -> {
                Modal modal = new Modal();
                modal.setModalListener(HomeActivity.this::signOut);
                modal.show(getSupportFragmentManager(), "modal");
            });

            if (getIntent().getBooleanExtra("nuevo_login", true)) {
                Usuario nuevoUsuario = new Usuario(nombre, email, tipo, 5);
                enviarUsuarioAlServidor(nuevoUsuario);
            }

            Log.d("DEBUG", "Nombre: " + nombre);
            Log.d("DEBUG", "Email: " + email);
            Log.d("DEBUG", "Tipo: " + tipo);
            Log.d("DEBUG", "Intentos: " + intentos);
        }

        generarAcortador();
    }

    private void enviarUsuarioAlServidor(Usuario usuario) {
        UsuarioApi usuarioApi = UsuarioServicio.getUsuarioApi();
        Call<JsonObject> call = usuarioApi.crearUsuario(usuario);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "Usuario creado correctamente");
                } else {
                    try {
                        Log.e("TAG", "Error del servidor: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("TAG", "Error al leer respuesta: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TAG", "Error de conexión: " + t.getMessage());
            }
        });
    }


    private void signOut() {
        // Cerrar sesión en Firebase
        mAuth.signOut();

        // Cerrar sesión en Google
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Redirigir a MainActivity después de cerrar ambas sesiones
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void generarAcortador(){
        txvLinkoriginal = findViewById(R.id.txvLinkoriginal);
        txvLinkNuevo = findViewById(R.id.txvLinkNuevo);
        btnGenerar = findViewById(R.id.btnGenerar);

        btnGenerar.setOnClickListener(v -> {
            if (intentos == 0){
                txvLinkNuevo.setText("Ya no tienes intentos");
                return;
            }

            //Actualizar intentos
            UsuarioApi usuarioApi = UsuarioServicio.getUsuarioApi();
            String email = mAuth.getCurrentUser().getEmail();
            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            usuarioApi.actualizarIntentos(json).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("TAG", "Intentos actualizados");
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("TAG", "Error al actualizar intentos: " + t.getMessage());
                }
            });


            String originalUrl = txvLinkoriginal.getText().toString().trim();
            if (originalUrl.isEmpty()) {
                txvLinkNuevo.setText("Introduce una URL válida");
                return;
            }

            // Llama a la API
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            UrlRequest urlRequest = new UrlRequest(originalUrl);

            Call<UrlResponse> call = apiService.shortenUrl(urlRequest);
            call.enqueue(new Callback<UrlResponse>() {
                @Override
                public void onResponse(Call<UrlResponse> call, Response<UrlResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UrlResponse result = response.body();
                        txvLinkNuevo.setText("http://acortadorphp-production.up.railway.app/" + result.getSlug());


                        intentos--;
                        txvIntentos.setText(String.valueOf(intentos));
                    } else {
                        txvLinkNuevo.setText("Error al acortar: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UrlResponse> call, Throwable t) {
                    txvLinkNuevo.setText("Error de conexión: " + t.getMessage());
                }
            });
        });

        btnCopiar = findViewById(R.id.btnCopiar);
        btnCopiar.setOnClickListener(view -> {
            String textoACopiar = txvLinkNuevo.getText().toString();

            if (!textoACopiar.isEmpty()) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Link", textoACopiar);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "Link copiado al portapapeles", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nada que copiar", Toast.LENGTH_SHORT).show();
            }
        });

    }
}