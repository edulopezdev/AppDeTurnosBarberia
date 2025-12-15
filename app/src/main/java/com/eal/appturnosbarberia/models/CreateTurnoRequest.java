package com.eal.appturnosbarberia.models;

public class CreateTurnoRequest {
  private String FechaHora;
  private int BarberoId;
  private Integer ClienteId; // opcional

  public CreateTurnoRequest(String fechaHora, int barberoId, Integer clienteId) {
    this.FechaHora = fechaHora;
    this.BarberoId = barberoId;
    this.ClienteId = clienteId;
  }

  public String getFechaHora() {
    return FechaHora;
  }
  public int getBarberoId() {
    return BarberoId;
  }
  public Integer getClienteId() {
    return ClienteId;
  }
  public void setFechaHora(String fechaHora) {
    this.FechaHora = fechaHora;
  }
  public void setBarberoId(int barberoId) {
    this.BarberoId = barberoId;
  }
  public void setClienteId(Integer clienteId) {
    this.ClienteId = clienteId;
  }
}
