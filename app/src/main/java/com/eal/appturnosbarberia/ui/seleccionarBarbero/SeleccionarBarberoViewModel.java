package com.eal.appturnosbarberia.ui.seleccionarBarbero;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.models.Barbero;
import com.eal.appturnosbarberia.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarBarberoViewModel extends AndroidViewModel {

  // Estos son los datos observables que la UI puede observar
  private final MutableLiveData<List<Barbero>> barberos = new MutableLiveData<>(); // Lista de barberos
  private final MutableLiveData<Integer> rvVisibility = new MutableLiveData<>(); // Visibilidad del RecyclerView
  private final MutableLiveData<Integer> tvNoBarberosVisibility = new MutableLiveData<>(); // Visibilidad del texto "No
                                                                                           // hay barberos"
  private final MutableLiveData<Boolean> loading = new MutableLiveData<>(); // Indicador de carga
  private final MutableLiveData<String> greeting = new MutableLiveData<>(); // Saludo personalizado
  private final MutableLiveData<Void> closeEvent = new MutableLiveData<>(); // Evento para cerrar el fragment
  private final MutableLiveData<Boolean> goToSeleccionarTurno = new MutableLiveData<>();

  // Estos son los eventos que la UI puede observar
  private final MutableLiveData<String> toast = new MutableLiveData<>(); // Mensajes de toast para la UI

  // inicialmente no hay barbero seleccionado
  private Barbero selectedBarbero = null;

  // Constructor del ViewModel
  public SeleccionarBarberoViewModel(@NonNull Application application) {
    super(application);
    barberos.setValue(new ArrayList<>());
    rvVisibility.setValue(View.GONE);
    tvNoBarberosVisibility.setValue(View.VISIBLE);
    loading.setValue(false);
    greeting.setValue("Seleccioná tu barbero");
  }

  // Getters para los datos observables y eventos
  public LiveData<List<Barbero>> getBarberos() {
    return barberos;
  }

  public LiveData<Integer> getRvVisibility() {
    return rvVisibility;
  }

  public LiveData<Integer> getTvNoBarberosVisibility() {
    return tvNoBarberosVisibility;
  }

  public LiveData<Boolean> getLoading() {
    return loading;
  }

  public LiveData<String> getGreeting() {
    return greeting;
  }

  public LiveData<String> getToast() {
    return toast;
  }

  public LiveData<Boolean> getGoToSeleccionarTurno() {
    return goToSeleccionarTurno;
  }

  // Este metodo establece el usuario y actualiza el saludo personalizado
  public void setUsuario(Usuario usuario) {
    if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
      greeting.setValue(usuario.getNombre() + ", seleccioná tu barbero");
    } else {
      greeting.setValue("Seleccioná tu barbero");
    }
  }

  // Este metodo carga la lista de barberos desde la API
  public void loadBarberos() {
    loading.setValue(true); // Iniciar indicador de carga

    String token = com.eal.appturnosbarberia.request.ApiClient.leerToken(getApplication());
    if (token == null || token.isEmpty()) {
      toast.setValue("No hay token disponible");
      barberos.setValue(new ArrayList<>()); // aca lo q hacemos es limpiar la lista
      rvVisibility.setValue(View.GONE); // aca ocultamos el recycler view
      tvNoBarberosVisibility.setValue(View.VISIBLE); // y aca mostramos el texto de "no hay barberos"
      loading.setValue(false); // Detener indicador de carga
      return; // salir del metodo
    }

    // Ahora vamos a llamar a la API para obtener los barberos mediante el metodo
    // getBarberos
    com.eal.appturnosbarberia.request.ApiClient.BarberiaServicio service = com.eal.appturnosbarberia.request.ApiClient
        .getBarberiaServicio();

    // llamamos a la API de forma asíncrona para no bloquear el hilo principal de la
    // UI
    service.getBarberos("Bearer " + token, true, 100) // llamamos al endpoint getBarberos
        .enqueue(new retrofit2.Callback<com.eal.appturnosbarberia.models.BarberosResponse>() {
          @Override
          public void onResponse(retrofit2.Call<com.eal.appturnosbarberia.models.BarberosResponse> call,
              retrofit2.Response<com.eal.appturnosbarberia.models.BarberosResponse> response) {
            loading.setValue(false);
            Log.d("SeleccionarBarberoVM", "Respuesta API: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
              List<Barbero> list = response.body().getData();
              if (list == null)
                list = new ArrayList<>();
              barberos.setValue(list);
              rvVisibility.setValue(list.isEmpty() ? View.GONE : View.VISIBLE);
              tvNoBarberosVisibility.setValue(list.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
              toast.setValue("Error al cargar barberos");
              barberos.setValue(new ArrayList<>());
              rvVisibility.setValue(View.GONE);
              tvNoBarberosVisibility.setValue(View.VISIBLE);
            }
          }

          @Override
          public void onFailure(retrofit2.Call<com.eal.appturnosbarberia.models.BarberosResponse> call,
              Throwable t) {
            loading.setValue(false);
            toast.setValue("Error de conexión");
            barberos.setValue(new ArrayList<>());
            rvVisibility.setValue(View.GONE);
            tvNoBarberosVisibility.setValue(View.VISIBLE);
          }
        });
  }

  public void onBarberoClicked(Barbero barbero) {
    selectedBarbero = barbero;
  }

  // este metodo confirma la seleccion del barbero y navega al siguiente fragment
  public void confirmarSeleccion() {
    if (selectedBarbero == null) {
      toast.setValue("Selecciona un barbero");
      return;
    }
    goToSeleccionarTurno.setValue(true);
  }

  public void doneNavigating() {
    goToSeleccionarTurno.setValue(false);
  }

  public LiveData<Void> getCloseEvent() {
    return closeEvent;
  }

  public void onCloseClicked() {
    closeEvent.setValue(null);
  }

  public void onStart() {
    loadBarberos();
  }

  public Bundle getSeleccionArgs() {
    Bundle args = new Bundle();
    args.putInt("barberoId", selectedBarbero.getId());
    args.putString("barberoNombre", selectedBarbero.getNombre());
    args.putString("barberoAvatar", selectedBarbero.getAvatarUrl());
    return args;
  }

}
