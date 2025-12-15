package com.eal.appturnosbarberia.ui.resetearContrasena;

import android.app.Application;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.ui.login.LoginActivity;
import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.models.OlvideContrasenaRequest;
import com.eal.appturnosbarberia.models.ResetearContrasenaRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetearContrasenaViewModel extends AndroidViewModel {

  private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
  private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
  private final MutableLiveData<Integer> errorVisibility = new MutableLiveData<>(View.GONE);

  private final MutableLiveData<String> infoMessage = new MutableLiveData<>("");
  private final MutableLiveData<Integer> infoVisibility = new MutableLiveData<>(View.GONE);

  private final MutableLiveData<Integer> codigoVisibility = new MutableLiveData<>(View.GONE);
  private final MutableLiveData<Integer> newPassVisibility = new MutableLiveData<>(View.GONE);
  private final MutableLiveData<Integer> confirmPassVisibility = new MutableLiveData<>(View.GONE);
  private final MutableLiveData<Integer> btResetearVisibility = new MutableLiveData<>(View.GONE);

  private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();

  private final MutableLiveData<String> toastMessage = new MutableLiveData<>(null);

  public ResetearContrasenaViewModel(@NonNull Application application) {
    super(application);
  }

  public LiveData<Boolean> getLoading() {
    return loading;
  }

  public LiveData<String> getError() {
    return errorMessage;
  }

  public LiveData<Integer> getErrorVisibility() {
    return errorVisibility;
  }

  public LiveData<String> getInfo() {
    return infoMessage;
  }

  public LiveData<Integer> getInfoVisibility() {
    return infoVisibility;
  }

  public LiveData<Integer> getCodigoVisibility() {
    return codigoVisibility;
  }

  public LiveData<Integer> getNewPassVisibility() {
    return newPassVisibility;
  }

  public LiveData<Integer> getConfirmPassVisibility() {
    return confirmPassVisibility;
  }

  public LiveData<Integer> getBtResetearVisibility() {
    return btResetearVisibility;
  }

  public LiveData<NavigationEvent> getNavigationEvent() {
    return navigationEvent;
  }

  public LiveData<String> getToastMessage() {
    return toastMessage;
  }

  private void setError(String msg) {
    errorMessage.setValue(msg);
    errorVisibility.setValue(View.VISIBLE);
  }

  private void clearError() {
    errorMessage.setValue("");
    errorVisibility.setValue(View.GONE);
  }

  private void setInfo(String msg) {
    infoMessage.setValue(msg);
    infoVisibility.setValue(View.VISIBLE);
  }

  private void clearInfo() {
    infoMessage.setValue("");
    infoVisibility.setValue(View.GONE);
  }

  // lo q hacemos aca es llamar a la api para solicitar el codigo
  public void solicitarCodigo(CharSequence emailCs) {
    String email = emailCs != null ? emailCs.toString().trim() : "";
    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      setError("Ingresá un email válido");
      return;
    }

    clearError();
    clearInfo();
    loading.setValue(true);

    ApiClient.BarberiaServicio api = ApiClient.getBarberiaServicio();
    OlvideContrasenaRequest body = new OlvideContrasenaRequest(email);
    api.olvideContrasena(body).enqueue(new Callback<okhttp3.ResponseBody>() {
      @Override
      public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
        loading.setValue(false);
        try {
          String raw = response.body() != null ? response.body().string()
              : (response.errorBody() != null ? response.errorBody().string() : null);
          boolean ok = false;
          String msg = null;
          String err = null;
          if (raw != null && !raw.isEmpty()) {
            JsonElement parsed = JsonParser.parseString(raw);
            if (parsed.isJsonObject()) {
              JsonObject root = parsed.getAsJsonObject();
              if (root.has("success")) {
                ok = root.get("success").getAsBoolean();
              }
              if (root.has("message")) {
                msg = root.get("message").getAsString();
              }
              if (root.has("error")) {
                err = root.get("error").getAsString();
              }
            }
          }
          if (response.isSuccessful() && ok) {
            setInfo(msg != null ? msg : "Se envió el código al email.");
            // Mostrar el resto del formulario
            codigoVisibility.setValue(View.VISIBLE);
            newPassVisibility.setValue(View.VISIBLE);
            confirmPassVisibility.setValue(View.VISIBLE);
            btResetearVisibility.setValue(View.VISIBLE);
          } else {
            setError(err != null ? err : (msg != null ? msg : "No se pudo enviar el código."));
          }
        } catch (Exception e) {
          setError("Error al procesar la respuesta.");
        }
      }

      @Override
      public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
        loading.setValue(false);
        setError("Error de conexión: " + (t != null ? t.getMessage() : ""));
      }
    });
  }

  // Paso 2: validar código y resetear contraseña
  public void resetearContrasena(CharSequence tokenCs, CharSequence nuevaCs, CharSequence confirmarCs) {
    String token = tokenCs != null ? tokenCs.toString().trim() : "";
    String nueva = nuevaCs != null ? nuevaCs.toString() : "";
    String confirmar = confirmarCs != null ? confirmarCs.toString() : "";

    if (!token.matches("\\d{4}")) {
      setError("El código debe tener 4 dígitos numéricos");
      return;
    }
    if (nueva.length() < 8) {
      setError("La contraseña debe tener al menos 8 caracteres");
      return;
    }
    if (!nueva.equals(confirmar)) {
      setError("Las contraseñas no coinciden");
      return;
    }

    clearError();
    clearInfo();
    loading.setValue(true);

    ApiClient.BarberiaServicio api = ApiClient.getBarberiaServicio();
    ResetearContrasenaRequest body = new ResetearContrasenaRequest(token, nueva, confirmar);
    api.resetearContrasena(body).enqueue(new Callback<okhttp3.ResponseBody>() {
      @Override
      public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
        loading.setValue(false);
        try {
          String raw = response.body() != null ? response.body().string()
              : (response.errorBody() != null ? response.errorBody().string() : null);
          boolean ok = false;
          String msg = null;
          String err = null;
          if (raw != null && !raw.isEmpty()) {
            JsonElement parsed = JsonParser.parseString(raw);
            if (parsed.isJsonObject()) {
              JsonObject root = parsed.getAsJsonObject();
              if (root.has("success")) {
                ok = root.get("success").getAsBoolean();
              }
              if (root.has("message")) {
                msg = root.get("message").getAsString();
              }
              if (root.has("error")) {
                err = root.get("error").getAsString();
              }
            }
          }
          if (response.isSuccessful() && ok) {
            // Emitir toast antes de navegar
            toastMessage.setValue(msg != null ? msg : "Contraseña actualizada correctamente.");
            // Opcional: también mantener infoMessage si tu UI lo usa en pantalla actual
            setInfo(msg != null ? msg : "Contraseña actualizada correctamente.");
            // Navegar al login
            navigationEvent.setValue(new NavigationEvent(
                LoginActivity.class,
                null,
                android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP));
          } else {
            setError(err != null ? err : (msg != null ? msg : "No se pudo resetear la contraseña."));
          }
        } catch (Exception e) {
          setError("Error al procesar la respuesta.");
        }
      }

      @Override
      public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
        loading.setValue(false);
        setError("Error de conexión: " + (t != null ? t.getMessage() : ""));
      }
    });
  }

  public void doneNavigating() {
    navigationEvent.setValue(null);
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