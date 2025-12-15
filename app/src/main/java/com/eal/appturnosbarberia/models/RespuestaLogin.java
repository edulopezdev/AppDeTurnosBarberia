package com.eal.appturnosbarberia.models;

public class RespuestaLogin {
  private int status;
  private String message;
  private String token;
  private Usuario usuario;

  public int obtenerEstado() {
    return status;
  }
  public void establecerEstado(int estado) {
    this.status = estado;
  }
  public String obtenerMensaje() {
    return message;
  }
  public void establecerMensaje(String mensaje) {
    this.message = mensaje;
  }
  public String obtenerToken() {
    return token;
  }
  public void establecerToken(String token) {
    this.token = token;
  }
  public Usuario obtenerUsuario() {
    return usuario;
  }
  public void establecerUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
