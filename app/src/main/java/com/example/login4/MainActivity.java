package com.example.login4;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    //Variables para Google Sign-In
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verifica si ya hay una sesión activa
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Ya hay usuario autenticado, redirige a HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("nuevo_login", false);
            startActivity(intent);
            return;
        }

        // Si no hay sesión iniciada, continúa con la vista de login
        setContentView(R.layout.activity_main);

        //Variables para Firebase
        mAuth = FirebaseAuth.getInstance();
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Usa el ID de Firebase
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Botón para iniciar sesión con Google
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    //Metodo para iniciar sesión con Google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Metodo para saber que nos responde el  sesión con Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si el requestCode es el correcto, obtiene la cuenta de Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Metodo para autenticar con Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Redirigir a la pantalla principal
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("nuevo_login", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error en autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}