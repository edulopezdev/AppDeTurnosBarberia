package com.eal.appturnosbarberia.ui.register;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;

import com.eal.appturnosbarberia.models.RegisterRequest;
import com.eal.appturnosbarberia.models.LoginResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.eal.appturnosbarberia.request.ApiClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends AndroidViewModel {

  private MutableLiveData<String> errorMessage = new MutableLiveData<>("");
  private MutableLiveData<Integer> errorVisibility = new MutableLiveData<>(View.GONE);
  private MutableLiveData<String> successMessage = new MutableLiveData<>("");
  // Nuevo: LiveData dedicado para mostrar toasts desde la UI (solo se emite
  // cuando hay texto)
  private MutableLiveData<String> toastMessage = new MutableLiveData<>();

  private MutableLiveData<Class<?>> navegarEvento = new MutableLiveData<>();
  private MutableLiveData<Bundle> navegarExtras = new MutableLiveData<>(new Bundle());
  private ApiClient.BarberiaServicio apiService;

  public RegisterViewModel(@NonNull Application application) {
    super(application);
    apiService = ApiClient.getBarberiaServicio();
  }

  public LiveData<String> getErrorMessage() {
    return errorMessage;
  }

  public LiveData<Integer> getErrorVisibility() {
    return errorVisibility;
  }

  public LiveData<String> getSuccessMessage() {
    return successMessage;
  }

  public LiveData<String> getToastMessage() {
    return toastMessage;
  }

  public LiveData<Class<?>> getNavegarEvento() {
    return navegarEvento;
  }

  public LiveData<Bundle> getNavegarExtras() {
    return navegarExtras;
  }

  // Helper para establecer error (mensaje + visibilidad)
  private void setError(String mensaje) {
    errorMessage.setValue(mensaje);
    errorVisibility.setValue(View.VISIBLE);
    // Emitir toast solo cuando hay mensaje real
    toastMessage.setValue(mensaje);
  }

  // Helper para establecer éxito (mensaje y ocultar error)
  private void setSuccess(String mensaje) {
    successMessage.setValue(mensaje);
    errorMessage.setValue("");
    errorVisibility.setValue(View.GONE);
    // Emitir toast de éxito
    toastMessage.setValue(mensaje);
  }

  // Cambiado: recibir CharSequence y normalizar/validar aquí (sin lógica en la
  // Activity)
  public void register(CharSequence nombreCs, CharSequence emailCs, CharSequence telefonoCs, CharSequence passwordCs,
      CharSequence confirmPasswordCs) {
    String nombre = nombreCs != null ? nombreCs.toString().trim() : "";
    String email = emailCs != null ? emailCs.toString().trim() : "";
    String telefono = telefonoCs != null ? telefonoCs.toString().trim() : "";
    String password = passwordCs != null ? passwordCs.toString() : "";
    String confirmPassword = confirmPasswordCs != null ? confirmPasswordCs.toString() : "";

    if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
      setError("Todos los campos son obligatorios");
      return;
    }

    if (!password.equals(confirmPassword)) {
      setError("Las contraseñas no coinciden");
      return;
    }

    // Llamada a la API
    String recaptchaToken = "";
    com.eal.appturnosbarberia.models.AuthRegisterRequest req = new com.eal.appturnosbarberia.models.AuthRegisterRequest(
        nombre, email, password, confirmPassword, telefono, recaptchaToken);

    // Log request body
    try {
      Gson gson = new Gson();
      Log.d("RegisterVM", "Register request JSON: " + gson.toJson(req));
    } catch (Exception e) {
      Log.w("RegisterVM", "No se pudo serializar request para log", e);
    }

    apiService.registerAuth(req).enqueue(new Callback<okhttp3.ResponseBody>() {
      @Override
      public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
        Log.d("RegisterVM", "registerAuth onResponse - code: " + response.code());
        Log.d("RegisterVM", "Headers: " + response.headers().toString());
        try {
          if (response.isSuccessful()) {
            String bodyString = null;
            if (response.body() != null) {
              bodyString = response.body().string();
            }
            Log.d("RegisterVM", "registerAuth success body: " + bodyString);
            // Esperamos 201 Created o 200 OK según backend
            successMessage.setValue("Cuenta creada exitosamente");
            // Emitir navegación a Verificación con extras (email + flags por defecto)
            Bundle extras = new Bundle();
            extras.putString("email", email);
            extras.putInt("flags", 0);
            navegarExtras.setValue(extras);
            navegarEvento.setValue(com.eal.appturnosbarberia.ui.verificacion.VerificacionActivity.class);
          } else {
            String err = null;
            if (response.errorBody() != null) {
              err = response.errorBody().string();
            }
            Log.e("RegisterVM", "registerAuth error code=" + response.code() + ", errorBody=" + err);
            if (response.code() == 409) {
              setError("El usuario ya está registrado");
            } else if (response.code() == 400) {
              // Intentar extraer un mensaje claro del body JSON (campos comunes:
              // "error","message","errors")
              String limpio = extractErrorMessage(err);
              setError(limpio != null && !limpio.isEmpty() ? limpio : "Datos inválidos, revise los campos");
            } else if (response.code() == 404) {
              setError("Endpoint no encontrado (404). Verifica la URL del servidor.");
            } else {
              setError("Error al crear la cuenta: código " + response.code());
            }
          }
        } catch (Exception e) {
          Log.e("RegisterVM", "Error leyendo body de la respuesta", e);
          setError("Error al procesar la respuesta del servidor");
        }
      }

      @Override
      public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
        Log.e("RegisterVM", "registerAuth onFailure", t);
        setError("Error de conexión: " + t.getMessage());
      }
    });
  }

  // Extrae un mensaje legible desde el body de error JSON.
  // Maneja formatos: {"error":"..."} , {"message":"..."} , {"errors":{ field: [
  // "msg" ] }}, o devuelve el texto crudo.
  private String extractErrorMessage(String err) {
    if (err == null || err.isEmpty())
      return null;
    try {
      JsonElement parsed = JsonParser.parseString(err);
      if (parsed.isJsonObject()) {
        JsonObject root = parsed.getAsJsonObject();
        if (root.has("error") && root.get("error").isJsonPrimitive()) {
          return root.get("error").getAsString();
        }
        if (root.has("message") && root.get("message").isJsonPrimitive()) {
          return root.get("message").getAsString();
        }
        if (root.has("errors") && root.get("errors").isJsonObject()) {
          JsonObject errors = root.getAsJsonObject("errors");
          StringBuilder sb = new StringBuilder();
          for (String key : errors.keySet()) {
            try {
              JsonElement arr = errors.get(key);
              if (arr.isJsonArray() && arr.getAsJsonArray().size() > 0) {
                String msg = arr.getAsJsonArray().get(0).getAsString();
                if (sb.length() > 0)
                  sb.append(" \n");
                sb.append(msg);
              }
            } catch (Exception ex) {
              // ignorar mensaje específico si algo falla
            }
          }
          if (sb.length() > 0)
            return sb.toString();
        }
      }
    } catch (Exception e) {
      // no JSON válido, caerá a retorno del texto crudo
    }
    return err;
  }
}