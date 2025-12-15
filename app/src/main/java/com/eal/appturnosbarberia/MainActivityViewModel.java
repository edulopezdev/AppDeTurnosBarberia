package com.eal.appturnosbarberia;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.models.CancelarTurnoRequest;
import com.eal.appturnosbarberia.models.Turno;
import com.eal.appturnosbarberia.models.TurnosResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.ui.common.DialogRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {

  // Usuario
  private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();

  // Mensajes simples
  private final MutableLiveData<String> mensaje = new MutableLiveData<>();

  // Turnos
  private final MutableLiveData<List<Turno>> turnos = new MutableLiveData<>(new ArrayList<>());
  private final MutableLiveData<Boolean> tieneTurnos = new MutableLiveData<>(false);
  private final MutableLiveData<Integer> cantidadTurnos = new MutableLiveData<>(0);

  // UI / navegación
  private final MutableLiveData<DialogRequest> solicitudDialogo = new MutableLiveData<>();
  private final MutableLiveData<Class<?>> eventoNavegarLogin = new MutableLiveData<>();
  private final MutableLiveData<Integer> eventoNavegarDestino = new MutableLiveData<>();
  private final MutableLiveData<Integer> eventoNavegar = new MutableLiveData<>();

  // Header
  private final MutableLiveData<String> nombreUsuario = new MutableLiveData<>("");
  private final MutableLiveData<String> emailUsuario = new MutableLiveData<>("");
  private final MutableLiveData<String> avatarUrl = new MutableLiveData<>("");

  // Logout menú
  private final MutableLiveData<Object> eventoResetMenu = new MutableLiveData<>();

  // API
  private final ApiClient.BarberiaServicio apiService;

  public MainActivityViewModel(@NonNull Application application) {
    super(application);
    apiService = ApiClient.getBarberiaServicio();
  }

  // Getters

  public LiveData<Usuario> getUsuario() {
    return usuario;
  }

  public LiveData<String> getMensaje() {
    return mensaje;
  }

  public LiveData<List<Turno>> getTurnos() {
    return turnos;
  }

  public LiveData<Boolean> getTieneTurnos() {
    return tieneTurnos;
  }

  public LiveData<Integer> getCantidadTurnos() {
    return cantidadTurnos;
  }

  public LiveData<DialogRequest> getSolicitudDialogo() {
    return solicitudDialogo;
  }

  public LiveData<Class<?>> getEventoNavegarLogin() {
    return eventoNavegarLogin;
  }

  public LiveData<Integer> getEventoNavegarDestino() {
    return eventoNavegarDestino;
  }

  public LiveData<String> getNombreUsuario() {
    return nombreUsuario;
  }

  public LiveData<String> getEmailUsuario() {
    return emailUsuario;
  }

  public LiveData<String> getAvatarUrl() {
    return avatarUrl;
  }

  public LiveData<Object> getEventoResetMenu() {
    return eventoResetMenu;
  }

  public LiveData<Integer> getEventoNavegar() {
    return eventoNavegar;
  }

  // Sesión

  public void solicitarCerrarSesion() {
    DialogRequest dr = new DialogRequest(
        "Cerrar sesión",
        "¿Deseás cerrar sesión?",
        "Cerrar sesión",
        "Cancelar",
        "LOGOUT_CONFIRM");
    solicitudDialogo.setValue(dr);
  }

  public void cancelarCerrarSesion() {
    eventoResetMenu.setValue(new Object());
  }

  public void cerrarSesion() {
    ApiClient.guardarToken(getApplication(), null);
    ApiClient.guardarUsuario(getApplication(), null);
    eventoNavegarLogin.setValue(
        com.eal.appturnosbarberia.ui.login.LoginActivity.class);
  }

  public void limpiarSolicitudDialogo() {
    // No emitir null, simplemente no emitir nada
  }

  public void manejarSeleccionMenu(int itemId) {
    if (itemId == com.eal.appturnosbarberia.R.id.nav_logout) {
      solicitarCerrarSesion();
    } else {
      eventoNavegar.setValue(itemId);
    }
  }

  // Usuario

  public void obtenerDatosUsuario() {

    Usuario local = ApiClient.leerUsuario(getApplication());
    if (local != null) {
      usuario.setValue(local);
      nombreUsuario.setValue(local.getNombre() != null ? local.getNombre() : "");
      emailUsuario.setValue(local.getEmail() != null ? local.getEmail() : "");
      actualizarAvatar(local);
    }

    String token = ApiClient.leerToken(getApplication());
    if (token == null)
      return;

    apiService.getUsuario("Bearer " + token)
        .enqueue(new Callback<Usuario>() {
          @Override
          public void onResponse(Call<Usuario> call, Response<Usuario> response) {
            if (response.isSuccessful() && response.body() != null) {
              Usuario u = response.body();
              usuario.setValue(u);
              nombreUsuario.setValue(u.getNombre() != null ? u.getNombre() : "");
              emailUsuario.setValue(u.getEmail() != null ? u.getEmail() : "");
              actualizarAvatar(u);
              ApiClient.guardarUsuario(getApplication(), u);
            }
          }

          @Override
          public void onFailure(Call<Usuario> call, Throwable t) {
            mensaje.postValue("Error al obtener datos del usuario");
          }
        });

    solicitarTurnos(token);
  }

  // metodo para actualizar el avatar del usuario
  private void actualizarAvatar(Usuario usuario) {
    if (usuario != null && usuario.getAvatar() != null && !usuario.getAvatar().isEmpty()) {
      String avatarPath = usuario.getAvatar();

      // Si es URL relativa, convertir a absoluta usando la URL base del servidor
      if (!avatarPath.startsWith("http://") && !avatarPath.startsWith("https://")) {
        // Extraer el host de la BASE_URL de ApiClient
        String baseUrl = "http://192.168.0.108:5000"; // Host + puerto sin /api/
        avatarPath = baseUrl + avatarPath;
      }

      avatarUrl.setValue(avatarPath);
    } else {
      avatarUrl.setValue("");
    }
  }

  // Turnos

  // este metodo es para refrescar los turnos del usuario
  public void refrescarTurnos() {
    String token = ApiClient.leerToken(getApplication());
    if (token != null) {
      solicitarTurnos(token);
    }
  }

  //este metodo es para solicitar los turnos del usuario
  private void solicitarTurnos(String token) {
    apiService.getMisTurnos("Bearer " + token)
        .enqueue(new Callback<TurnosResponse>() {
          @Override
          public void onResponse(Call<TurnosResponse> call,
              Response<TurnosResponse> response) {

            if (response.isSuccessful() && response.body() != null) {

              List<Turno> lista = response.body().getTurnos();
              if (lista == null)
                lista = new ArrayList<>();

              turnos.setValue(lista);
              tieneTurnos.setValue(!lista.isEmpty());
              // El backend valida la cantidad de turnos
              cantidadTurnos.setValue(lista.size());

            } else {
              limpiarTurnos("Error al obtener turnos");
            }
          }

          @Override
          public void onFailure(Call<TurnosResponse> call, Throwable t) {
            limpiarTurnos("Error de conexión al obtener turnos");
          }
        });
  }

  //metodo para limpiar los turnos del usuario
  private void limpiarTurnos(String msg) {
    turnos.setValue(new ArrayList<>());
    tieneTurnos.setValue(false);
    cantidadTurnos.setValue(0);
    mensaje.postValue(msg);
  }

  // este metodo es para cancelar un turno del usuario
  public void cancelarTurno(int turnoId,
      String observacion,
      CancelCallback callback) {

    String token = ApiClient.leerToken(getApplication()); // leemos el token
    if (token == null) {
      mensaje.postValue("Sesión expirada");
      if (callback != null)
        callback.onComplete(false);
      return;
    }

    CancelarTurnoRequest req = new CancelarTurnoRequest(observacion == null ? "" : observacion);

    apiService.cancelarTurno("Bearer " + token, turnoId, req) // hacemos la llamada a la API
        .enqueue(new Callback<ResponseBody>() {
          @Override
          public void onResponse(Call<ResponseBody> call,
              Response<ResponseBody> response) {

            if (response.isSuccessful()) {
              mensaje.postValue("Turno cancelado correctamente");
              refrescarTurnos();
              if (callback != null)
                callback.onComplete(true);
            } else {
              mensaje.postValue("No se pudo cancelar el turno");
              if (callback != null)
                callback.onComplete(false);
            }
          }

          @Override
          public void onFailure(Call<ResponseBody> call, Throwable t) {
            mensaje.postValue("Error de conexión al cancelar turno");
            if (callback != null)
              callback.onComplete(false);
          }
        });
  }

  // metodo para obtener el estadoId de un turno de forma segura
  private int getEstadoIdSeguro(Turno t) {
    if (t == null)
      return 0;
    try {
      Object estado = t.getEstado();
      if (estado != null) {
        return (int) estado.getClass()
            .getMethod("getEstadoId")
            .invoke(estado);
      }
    } catch (Exception ignored) {
    }
    return 0;
  }

  // interfaz para el callback de cancelación de turno
  public interface CancelCallback {
    void onComplete(boolean success);
  }
}
