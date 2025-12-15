package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class Barbero {
  @SerializedName("id")
  private int id;
  @SerializedName("nombre")
  private String nombre;
  @SerializedName("avatar")
  private String avatar;
  @SerializedName("telefono")
  private String telefono;
  @SerializedName("avatarUrl")
  private String avatarUrl;
  public Barbero() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  public int getBarberoId() {
    return id;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getAvatar() {
    return avatar;
  }
  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
  public String getTelefono() {
    return telefono;
  }
  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }
  public String getAvatarUrl() {
    return avatarUrl;
  }
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
