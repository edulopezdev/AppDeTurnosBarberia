package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class OlvideContrasenaRequest {
  @SerializedName("email")
  private String email;

  public OlvideContrasenaRequest(String email) {
    this.email = email;
  }
  public String getEmail() {
    return email;
  }
}
