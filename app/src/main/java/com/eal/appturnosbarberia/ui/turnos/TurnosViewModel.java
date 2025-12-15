package com.eal.appturnosbarberia.ui.turnos;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.models.Turno;

import java.util.List;

public class TurnosViewModel extends ViewModel {

  private final MutableLiveData<List<Turno>> turnos = new MutableLiveData<>();
  private final MutableLiveData<Integer> rvVisibility = new MutableLiveData<>(View.GONE);
  private final MutableLiveData<Integer> noTurnosVisibility = new MutableLiveData<>(View.VISIBLE);
  private final MutableLiveData<Integer> fabVisibility = new MutableLiveData<>(View.VISIBLE);
  private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>(false);
  private final MutableLiveData<String> toast = new MutableLiveData<>();

  // ---------- Entrada desde Fragment ----------

  public void onTurnosUpdated(List<Turno> list) {
    turnos.setValue(list);
    refreshing.setValue(false);

    boolean has = list != null && !list.isEmpty();

    rvVisibility.setValue(has ? View.VISIBLE : View.GONE);
    noTurnosVisibility.setValue(has ? View.GONE : View.VISIBLE);
    // El backend se encarga de validar si se puede crear más turnos
    fabVisibility.setValue(View.VISIBLE);
  }

  public void setRefreshing(boolean value) {
    refreshing.setValue(value);
  }

  public void onBarberoSelected(Bundle bundle) {
    if (bundle == null)
      return;
    toast.setValue("Barbero seleccionado: " + bundle.getString("barberoNombre"));
  }

  // ---------- Adapter ----------

  public TurnosAdapter.OnCancelListener getCancelListener(MainActivityViewModel mainVM) {
    return (turno, obs, cb) -> mainVM.cancelarTurno(turno.getId(), obs, cb);
  }

  // ---------- Getters ----------

  public LiveData<List<Turno>> getTurnos() {
    return turnos;
  }

  public LiveData<Integer> getRecyclerVisibility() {
    return rvVisibility;
  }

  public LiveData<Integer> getNoTurnosVisibility() {
    return noTurnosVisibility;
  }

  public LiveData<Integer> getFabVisibility() {
    return fabVisibility;
  }

  public LiveData<Boolean> getRefreshing() {
    return refreshing;
  }

  public LiveData<String> getToast() {
    return toast;
  }
}
