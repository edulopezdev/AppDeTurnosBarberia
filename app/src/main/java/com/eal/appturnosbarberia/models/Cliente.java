package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class Cliente {
  @SerializedName("clienteId")
  private int clienteId;
  @SerializedName("nombre")
  private String nombre;

  public Cliente() {
  }

  public int getClienteId() {
    return clienteId;
  }
  public String getNombre() {
    return nombre;
  }
  public void setClienteId(int clienteId) {
    this.clienteId = clienteId;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
