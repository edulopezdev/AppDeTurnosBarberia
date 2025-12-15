package com.eal.appturnosbarberia.models;

public class ReenviarCodigoRequest {
  private String Email;

  public ReenviarCodigoRequest(String email) {
    this.Email = email;
  }
  public String getEmail() {
    return Email;
  }
}
