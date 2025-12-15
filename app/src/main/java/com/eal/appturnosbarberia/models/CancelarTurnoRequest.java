package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class CancelarTurnoRequest {
  @SerializedName("observacion")
  private String observacion;
  public CancelarTurnoRequest(String observacion) {
    this.observacion = observacion;
  }
  public String getObservacion() {
    return observacion;
  }
  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }
}
