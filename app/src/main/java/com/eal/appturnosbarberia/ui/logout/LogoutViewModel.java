package com.eal.appturnosbarberia.ui.logout;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.ui.login.LoginActivity;

public class LogoutViewModel extends AndroidViewModel {

  private MutableLiveData<Class<?>> navegarEvento;
  private MutableLiveData<String> mensajeMutable;
  private MutableLiveData<Bundle> navegarExtras = new MutableLiveData<>(new Bundle());

  public LogoutViewModel(@NonNull Application application) {
    super(application);
    navegarEvento = new MutableLiveData<>();
    mensajeMutable = new MutableLiveData<>();
  }

  public LiveData<Class<?>> obtenerNavegarEvento() {
    return navegarEvento;
  }

  public LiveData<String> obtenerMensaje() {
    return mensajeMutable;
  }

  public LiveData<Bundle> obtenerNavegarExtras() {
    return navegarExtras;
  }

  // aqui se cierra la sesión del usuario actual
  public void cerrarSesion() {
    ApiClient.guardarToken(getApplication(), ""); // aca se borra el token
    ApiClient.guardarUsuario(getApplication(), null); // y aca se borra el usuario

    mensajeMutable.setValue("Sesión cerrada"); // mensaje de feedback al usuario
    // Emitir extras con flags para que la UI no tenga lógica de flags
    Bundle extras = new Bundle(); // crear nuevo bundle de extras
    extras.putInt("flags", Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // agregar flags
    navegarExtras.setValue(extras); // establecer los extras en el LiveData
    navegarEvento.setValue(LoginActivity.class); // navegar a la actividad de login
  }
}