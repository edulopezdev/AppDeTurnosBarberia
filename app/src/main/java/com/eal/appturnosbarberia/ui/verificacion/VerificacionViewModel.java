package com.eal.appturnosbarberia.ui.verificacion;

import androidx.lifecycle.ViewModel;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class VerificacionViewModel extends AndroidViewModel {

  private ApiClient.BarberiaServicio apiService;
  private MutableLiveData<String> error = new MutableLiveData<>();
  private final MutableLiveData<Integer> errorVisibility = new MutableLiveData<>(View.GONE);
  private final MutableLiveData<String> emailText = new MutableLiveData<>();

  private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();
  private final MutableLiveData<String> toastMessage = new MutableLiveData<>(null);
  private final MutableLiveData<Void> backEvent = new MutableLiveData<>();

  public VerificacionViewModel(@NonNull Application application) {
    super(application);
    apiService = ApiClient.getBarberiaServicio();
  }

  public LiveData<String> getError() {
    return error;
  }

  public LiveData<Integer> getErrorVisibility() {
    return errorVisibility;
  }

  public LiveData<String> getEmailText() {
    return emailText;
  }

  public LiveData<NavigationEvent> getNavigationEvent() {
    return navigationEvent;
  }

  public LiveData<String> getToastMessage() {
    return toastMessage;
  }

  public LiveData<Void> getBackEvent() {
    return backEvent;
  }

  public void init(Bundle args, Bundle intentExtras) {
    String email = null;
    if (args != null) {
      email = args.getString("email");
    }
    if (email == null && intentExtras != null) {
      email = intentExtras.getString("email");
    }
    if (email != null) {
      emailText.setValue(email);
    }
  }

  // este metodo verifica el código ingresado por el usuario
  public void verificarCodigo(String code, String email) {
    error.setValue(null);
    errorVisibility.setValue(View.GONE);

    if (code == null || code.trim().length() != 4) {
      error.setValue("El código debe tener 4 dígitos");
      errorVisibility.setValue(View.VISIBLE);
      return;
    }

    // Realizar la llamada a la API para verificar el código
    com.eal.appturnosbarberia.models.VerifyRequest req = new com.eal.appturnosbarberia.models.VerifyRequest(code.trim(),
        email);
    apiService.verificarCodigo(req).enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
          toastMessage.setValue("Cuenta verificada correctamente. Ya podés iniciar sesión.");
          navigationEvent.setValue(new NavigationEvent(
              LoginActivity.class,
              null,
              Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
          String msg = "Error en la verificación: " + response.code();
          if (response.code() == 400)
            msg = "Código incorrecto o inválido";
          else if (response.code() == 404)
            msg = "Usuario no encontrado";
          else if (response.code() == 410)
            msg = "El código ha expirado";

          error.setValue(msg);
          errorVisibility.setValue(View.VISIBLE);
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        error.setValue("Error de conexión: " + t.getMessage());
        errorVisibility.setValue(View.VISIBLE);
      }
    });
  }

  // este metodo reenvía el código de verificación al email del usuario
  public void reenviarCodigo(String email) {
    error.setValue(null);
    errorVisibility.setValue(View.GONE);

    if (email == null || email.trim().isEmpty()) {
      error.setValue("Email requerido para reenviar código");
      errorVisibility.setValue(View.VISIBLE);
      return;
    }

    // Realizar la llamada a la API para reenviar el código
    com.eal.appturnosbarberia.models.ReenviarCodigoRequest req = new com.eal.appturnosbarberia.models.ReenviarCodigoRequest(
        email.trim());
    apiService.reenviarCodigo(req).enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
          toastMessage.setValue("Se ha reenviado el código a tu email");
        } else {
          error.setValue("No se pudo reenviar el código: " + response.code());
          errorVisibility.setValue(View.VISIBLE);
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        error.setValue("Error de conexión: " + t.getMessage());
        errorVisibility.setValue(View.VISIBLE);
      }
    });
  }

  public void onBackClicked() {
    backEvent.setValue(null); // Disparar el evento de retroceso
  }

  public void doneNavigating() {
    navigationEvent.setValue(null); // Limpiar el evento de navegación después de manejarlo
  }

  public static class NavigationEvent {
    public final Class<?> destination;
    public final Bundle extras;
    public final int flags;

    public NavigationEvent(Class<?> destination, Bundle extras, int flags) {
      this.destination = destination;
      this.extras = extras != null ? extras : new Bundle();
      this.flags = flags;
    }
  }
}
