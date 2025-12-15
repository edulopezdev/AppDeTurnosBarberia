package com.eal.appturnosbarberia.models;

public class PerfilResponse {
  private int status;
  private String message;
  private Usuario usuario;

  public int getStatus() {
    return status;
  }
  public String getMessage() {
    return message;
  }
  public Usuario getUsuario() {
    return usuario;
  }
  public void setStatus(int status) {
    this.status = status;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
