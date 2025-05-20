package com.example.login4;

public class Usuario {
    private String nombre;
    private String email;
    private String tipo;
    private int intentos;

    public Usuario(String nombre, String email, String tipo, int intentos) {
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.intentos = intentos;
    }

    // Getters (necesarios para Retrofit)
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public int getIntentos() { return intentos; }
}