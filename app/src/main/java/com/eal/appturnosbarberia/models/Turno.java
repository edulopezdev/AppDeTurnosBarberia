package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class Turno {
  @SerializedName("id")
  private int id;

  @SerializedName("fechaHora")
  private String fechaHora;
  @SerializedName("clienteId")
  private int clienteId;
  @SerializedName("barberoId")
  private int barberoId;
  @SerializedName("barbero")
  private Barbero barbero;
  @SerializedName("estadoId")
  private int estadoId;
  @SerializedName("estado")
  private Estado estado;
  @SerializedName("cliente")
  private Cliente cliente;
  @SerializedName("cantidadAtenciones")
  private int cantidadAtenciones;

  public Turno() {
  }

  public int getId() {
    return id;
  }
  public String getFechaHora() {
    return fechaHora;
  }
  public int getClienteId() {
    return clienteId;
  }
  public int getBarberoId() {
    return barberoId;
  }
  public Barbero getBarbero() {
    return barbero;
  }
  public int getEstadoId() {
    return estadoId;
  }
  public Estado getEstado() {
    return estado;
  }
  public Cliente getCliente() {
    return cliente;
  }
  public int getCantidadAtenciones() {
    return cantidadAtenciones;
  }
  public void setId(int id) {
    this.id = id;
  }
  public void setFechaHora(String fechaHora) {
    this.fechaHora = fechaHora;
  }
  public void setClienteId(int clienteId) {
    this.clienteId = clienteId;
  }
  public void setBarberoId(int barberoId) {
    this.barberoId = barberoId;
  }
  public void setEstadoId(int estadoId) {
    this.estadoId = estadoId;
  }
  public void setCantidadAtenciones(int cantidadAtenciones) {
    this.cantidadAtenciones = cantidadAtenciones;
  }
}
