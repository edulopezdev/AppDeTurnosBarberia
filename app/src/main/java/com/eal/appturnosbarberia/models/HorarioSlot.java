package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class HorarioSlot {
  @SerializedName("hora")
  private String hora;
  @SerializedName("disponible")
  private boolean disponible;
  @SerializedName("barberoId")
  private int barberoId;

  public HorarioSlot() {
  }

  public HorarioSlot(String hora, boolean disponible) {
    this.hora = hora;
    this.disponible = disponible;
  }

  public String getHora() {
    return hora;
  }
  public void setHora(String hora) {
    this.hora = hora;
  }
  public boolean isDisponible() {
    return disponible;
  }
  public void setDisponible(boolean disponible) {
    this.disponible = disponible;
  }
  public int getBarberoId() {
    return barberoId;
  }
  public void setBarberoId(int barberoId) {
    this.barberoId = barberoId;
  }
}
