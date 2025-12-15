package com.eal.appturnosbarberia.models;

public class VerifyRequest {
  private String Code;
  private String Email;

  public VerifyRequest(String code, String email) {
    this.Code = code;
    this.Email = email;
  }

  public String getCode() {
    return Code;
  }
  public String getEmail() {
    return Email;
  }
}
