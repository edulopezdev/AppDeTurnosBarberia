package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CortesMesResponse {
  @SerializedName("status")
  private int status;
  @SerializedName("message")
  private String message;
  @SerializedName("data")
  private Data data;

  public int getStatus() {
    return status;
  }
  public String getMessage() {
    return message;
  }
  public Data getData() {
    return data;
  }

  public static class Data {
    @SerializedName("year")
    private int year;
    @SerializedName("month")
    private int month;
    @SerializedName("totalCortes")
    private int totalCortes;
    @SerializedName("porServicio")
    private List<ServicioCortes> porServicio;

    public int getYear() {
      return year;
    }
    public int getMonth() {
      return month;
    }
    public int getTotalCortes() {
      return totalCortes;
    }
    public List<ServicioCortes> getPorServicio() {
      return porServicio;
    }
  }

  public static class ServicioCortes {
    @SerializedName("productoServicioId")
    private int productoServicioId;

    @SerializedName("nombre")
    private String nombre;
    @SerializedName("cantidad")
    private int cantidad;
    public int getProductoServicioId() {
      return productoServicioId;
    }
    public String getNombre() {
      return nombre;
    }
    public int getCantidad() {
      return cantidad;
    }
  }
}