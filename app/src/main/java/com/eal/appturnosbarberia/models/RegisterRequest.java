package com.eal.appturnosbarberia.models;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String telefono;
    private String password;

    public RegisterRequest(String nombre, String email, String telefono, String password) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getPassword() { return password; }
}