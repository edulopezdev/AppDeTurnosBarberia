package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HorariosResponse {
  @SerializedName("status")
  private int status;
  @SerializedName("message")
  private String message;
  @SerializedName("data")
  private List<HorarioSlot> data;

  public int getStatus() {
    return status;
  }
  public String getMessage() {
    return message;
  }
  public List<HorarioSlot> getData() {
    return data;
  }
}
