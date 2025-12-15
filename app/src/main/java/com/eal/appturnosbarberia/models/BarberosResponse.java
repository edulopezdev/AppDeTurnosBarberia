package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BarberosResponse {
  @SerializedName("status")
  private int status;
  @SerializedName("message")
  private String message;
  @SerializedName("data")
  private List<Barbero> data;
  @SerializedName("total")
  private int total;

  public int getStatus() {
    return status;
  }
  public String getMessage() {
    return message;
  }
  public List<Barbero> getData() {
    return data;
  }
  public int getTotal() {
    return total;
  }
}
