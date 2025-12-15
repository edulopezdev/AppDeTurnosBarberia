package com.eal.appturnosbarberia.models;

public class SolicitudLogin {
  private String email;
  private String password;

  public SolicitudLogin(String correo, String contrasena) {
    this.email = correo;
    this.password = contrasena;
  }

  public String obtenerCorreo() {
    return email;
  }
  public String obtenerContrasena() {
    return password;
  }
}
