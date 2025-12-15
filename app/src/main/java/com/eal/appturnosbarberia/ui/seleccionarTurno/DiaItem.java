package com.eal.appturnosbarberia.ui.seleccionarTurno;

public class DiaItem {
  private int diaNumero;
  private String diaSemana;
  private String fechaIso;

  public DiaItem() {
  }

  public DiaItem(int diaNumero, String diaSemana, String fechaIso) {
    this.diaNumero = diaNumero;
    this.diaSemana = diaSemana;
    this.fechaIso = fechaIso;
  }

  public int getDiaNumero() {
    return diaNumero;
  }

  public void setDiaNumero(int diaNumero) {
    this.diaNumero = diaNumero;
  }

  public String getDiaSemana() {
    return diaSemana;
  }

  public void setDiaSemana(String diaSemana) {
    this.diaSemana = diaSemana;
  }

  public String getFechaIso() {
    return fechaIso;
  }

  public void setFechaIso(String fechaIso) {
    this.fechaIso = fechaIso;
  }
}
