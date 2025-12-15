package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class Estado {
  @SerializedName("estadoId")
  private int estadoId;
  @SerializedName("nombre")
  private String nombre;

  public Estado() {
  }

  public int getEstadoId() {
    return estadoId;
  }
  public void setEstadoId(int id) {
    this.estadoId = id;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
