package com.eal.appturnosbarberia.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {
  @SerializedName("id")
  private int id;
  @SerializedName("nombre")
  private String nombre;
  @SerializedName("email")
  private String email;
  @SerializedName("telefono")
  private String telefono;
  @SerializedName("avatar")
  private String avatar;
  @SerializedName("avatarUrl")
  private String avatarUrl;
  @SerializedName("rolId")
  private int rolId;
  @SerializedName("rolNombre")
  private String rolNombre;
  @SerializedName("accedeAlSistema")
  private boolean accedeAlSistema;
  @SerializedName("activo")
  private boolean activo;
  @SerializedName("passwordHash")
  private String passwordHash;
  @SerializedName("fechaRegistro")
  private String fechaRegistro;
  @SerializedName("idUsuarioCrea")
  private int idUsuarioCrea;
  @SerializedName("idUsuarioModifica")
  private Integer idUsuarioModifica;
  @SerializedName("fechaModificacion")
  private String fechaModificacion;

  // Constructor vacío requerido para Gson
  public Usuario() {
  }

  // Constructor completo
  public Usuario(int id, String nombre, String email, String telefono, String avatar, String avatarUrl, int rolId,
      String rolNombre, boolean accedeAlSistema, boolean activo, String passwordHash, String fechaRegistro,
      int idUsuarioCrea, Integer idUsuarioModifica, String fechaModificacion) {
    this.id = id;
    this.nombre = nombre;
    this.email = email;
    this.telefono = telefono;
    this.avatar = avatar;
    this.avatarUrl = avatarUrl;
    this.rolId = rolId;
    this.rolNombre = rolNombre;
    this.accedeAlSistema = accedeAlSistema;
    this.activo = activo;
    this.passwordHash = passwordHash;
    this.fechaRegistro = fechaRegistro;
    this.idUsuarioCrea = idUsuarioCrea;
    this.idUsuarioModifica = idUsuarioModifica;
    this.fechaModificacion = fechaModificacion;
  }

  // Getters y setters
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getTelefono() {
    return telefono;
  }
  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }
  public String getAvatar() {
    return avatar;
  }
  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
  public String getAvatarUrl() {
    return avatarUrl;
  }
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
  public int getRolId() {
    return rolId;
  }
  public void setRolId(int rolId) {
    this.rolId = rolId;
  }
  public String getRolNombre() {
    return rolNombre;
  }
  public void setRolNombre(String rolNombre) {
    this.rolNombre = rolNombre;
  }
  public boolean isAccedeAlSistema() {
    return accedeAlSistema;
  }
  public void setAccedeAlSistema(boolean accedeAlSistema) {
    this.accedeAlSistema = accedeAlSistema;
  }

  public boolean isActivo() {
    return activo;
  }
  public void setActivo(boolean activo) {
    this.activo = activo;
  }
  public String getPasswordHash() {
    return passwordHash;
  }
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
  public String getFechaRegistro() {
    return fechaRegistro;
  }
  public void setFechaRegistro(String fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }
  public int getIdUsuarioCrea() {
    return idUsuarioCrea;
  }
  public void setIdUsuarioCrea(int idUsuarioCrea) {
    this.idUsuarioCrea = idUsuarioCrea;
  }
  public Integer getIdUsuarioModifica() {
    return idUsuarioModifica;
  }
  public void setIdUsuarioModifica(Integer idUsuarioModifica) {
    this.idUsuarioModifica = idUsuarioModifica;
  }

  public String getFechaModificacion() {
    return fechaModificacion;
  }
  public void setFechaModificacion(String fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
  }
}