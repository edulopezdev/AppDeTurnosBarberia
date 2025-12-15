package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardClienteResponse {
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
    @SerializedName("proximoTurno")
    private ProximoTurno proximoTurno;
    @SerializedName("servicios")
    private List<Servicio> servicios;
    @SerializedName("barberia")
    private Barberia barberia;

    public ProximoTurno getProximoTurno() {
      return proximoTurno;
    }
    public List<Servicio> getServicios() {
      return servicios;
    }
    public Barberia getBarberia() {
      return barberia;
    }
  }

  public static class ProximoTurno {
    @SerializedName("id")
    private int id;
    @SerializedName("fechaHora")
    private String fechaHora;
    @SerializedName("estadoId")
    private int estadoId;
    @SerializedName("barberoId")
    private int barberoId;
    @SerializedName("clienteId")
    private int clienteId;
    @SerializedName("barberoNombre")
    private String barberoNombre;
    @SerializedName("estadoNombre")
    private String estadoNombre;

    public int getId() {
      return id;
    }
    public String getFechaHora() {
      return fechaHora;
    }
    public int getEstadoId() {
      return estadoId;
    }
    public int getBarberoId() {
      return barberoId;
    }
    public int getClienteId() {
      return clienteId;
    }
    public String getBarberoNombre() {
      return barberoNombre;
    }
    public String getEstadoNombre() {
      return estadoNombre;
    }
  }

  public static class Servicio {
    @SerializedName("id")
    private int id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("precio")
    private int precio;
    public int getId() {
      return id;
    }
    public String getNombre() {
      return nombre;
    }
    public int getPrecio() {
      return precio;
    }
  }

  public static class Barberia {
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("direccion")
    private String direccion;
    @SerializedName("mapsUrl")
    private String mapsUrl;
    @SerializedName("whatsapp")
    private String whatsapp;
    @SerializedName("email")
    private String email;
    @SerializedName("instagram")
    private String instagram;

    @SerializedName("horarioMatutinoInicio")
    private String horarioMatutinoInicio;
    @SerializedName("horarioMatutinoFin")
    private String horarioMatutinoFin;
    @SerializedName("horarioVespertinoInicio")
    private String horarioVespertinoInicio;
    @SerializedName("horarioVespertinoFin")
    private String horarioVespertinoFin;

    public String getNombre() {
      return nombre;
    }
    public String getDireccion() {
      return direccion;
    }
    public String getMapsUrl() {
      return mapsUrl;
    }
    public String getWhatsapp() {
      return whatsapp;
    }
    public String getEmail() {
      return email;
    }
    public String getInstagram() {
      return instagram;
    }
    public String getHorarioMatutinoInicio() {
      return horarioMatutinoInicio;
    }
    public String getHorarioMatutinoFin() {
      return horarioMatutinoFin;
    }
    public String getHorarioVespertinoInicio() {
      return horarioVespertinoInicio;
    }
    public String getHorarioVespertinoFin() {
      return horarioVespertinoFin;
    }
  }
}
