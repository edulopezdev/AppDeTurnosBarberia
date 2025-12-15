package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TurnosResponse {
  @SerializedName("success")
  private boolean success;
  @SerializedName("turnos")
  private List<Turno> turnos;
  @SerializedName("total")
  private int total;
  public TurnosResponse() {
  }

  public boolean isSuccess() {
    return success;
  }
  public List<Turno> getTurnos() {
    return turnos;
  }
  public int getTotal() {
    return total;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }
  public void setTurnos(List<Turno> turnos) {
    this.turnos = turnos;
  }
  public void setTotal(int total) {
    this.total = total;
  }
}
