package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class AuthRegisterRequest {
  @SerializedName("Nombre")
  private String nombre;
  @SerializedName("Email")
  private String email;
  @SerializedName("Password")
  private String password;
  @SerializedName("ConfirmarPassword")
  private String confirmarPassword;
  @SerializedName("Telefono")
  private String telefono;
  @SerializedName("RecaptchaToken")
  private String recaptchaToken;

  public AuthRegisterRequest(String nombre, String email, String password, String confirmarPassword, String telefono,
      String recaptchaToken) {
    this.nombre = nombre;
    this.email = email;
    this.password = password;
    this.confirmarPassword = confirmarPassword;
    this.telefono = telefono;
    this.recaptchaToken = recaptchaToken;
  }

  public String getNombre() {
    return nombre;
  }
  public String getEmail() {
    return email;
  }
  public String getPassword() {
    return password;
  }
  public String getConfirmarPassword() {
    return confirmarPassword;
  }
  public String getTelefono() {
    return telefono;
  }
  public String getRecaptchaToken() {
    return recaptchaToken;
  }
}
