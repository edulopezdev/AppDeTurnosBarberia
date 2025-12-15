package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class ResetearContrasenaRequest {
  @SerializedName("token")
  private String token;
  @SerializedName("nuevaContrasena")
  private String nuevaContrasena;
  @SerializedName("confirmarContrasena")
  private String confirmarContrasena;

  public ResetearContrasenaRequest(String token, String nuevaContrasena, String confirmarContrasena) {
    this.token = token;
    this.nuevaContrasena = nuevaContrasena;
    this.confirmarContrasena = confirmarContrasena;
  }

  public String getToken() {
    return token;
  }
  public String getNuevaContrasena() {
    return nuevaContrasena;
  }
  public String getConfirmarContrasena() {
    return confirmarContrasena;
  }
}
