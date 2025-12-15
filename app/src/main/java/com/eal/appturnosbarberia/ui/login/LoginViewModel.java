package com.eal.appturnosbarberia.ui.login;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.MainActivity;
import com.eal.appturnosbarberia.models.LoginRequest;
import com.eal.appturnosbarberia.models.LoginResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.ui.register.RegisterActivity;
import com.eal.appturnosbarberia.ui.verificacion.VerificacionActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

  private Context contexto; // Contexto de la aplicación
  private MutableLiveData<String> errorMutable; // Mensaje de error
  private MutableLiveData<Integer> errorVisibility; // Visibilidad del mensaje de error
  private MutableLiveData<Class<?>> navegarEvento; // Evento de navegación
  private MutableLiveData<android.os.Bundle> navegarExtras = new MutableLiveData<>(new android.os.Bundle()); // Extras de navegación

  // Constructor
  public LoginViewModel(@NonNull Application aplicacion) {
    super(aplicacion);
    contexto = getApplication();
    errorMutable = new MutableLiveData<>("");
    errorVisibility = new MutableLiveData<>(View.GONE);
    navegarEvento = new MutableLiveData<>();
  }
  public LiveData<String> obtenerMensajeError() { return errorMutable; } // Obtener LiveData de visibilidad de errores
  public LiveData<Integer> obtenerMensajeErrorVisibility() {
    return errorVisibility;
  } // Obtener LiveData de visibilidad de errores

  public LiveData<Class<?>> obtenerNavegarARegistro() {
    return navegarEvento;
  } // Obtener LiveData de evento de navegación

  public LiveData<android.os.Bundle> obtenerNavegarExtras() {
    return navegarExtras;
  } // Obtener LiveData de extras de navegación

  // metodo para establecer error
  private void setError(String mensaje) {
    errorMutable.setValue(mensaje);
    errorVisibility.setValue(View.VISIBLE);
  }

  // metodo para limpiar error
  private void clearError() {
    errorMutable.setValue("");
    errorVisibility.setValue(View.GONE);
  }

  // metodo para navegar a crear cuenta
  public void alClickCrearCuenta() {
    navegarEvento.setValue(RegisterActivity.class);
  }

  // Acción: recuperar contraseña
  public void alClickRecuperarContrasena(CharSequence emailCs) {
    String email = emailCs != null ? emailCs.toString().trim() : "";
    // La lógica de validación/formatting se realiza aquí en el ViewModel
    android.os.Bundle extras = new android.os.Bundle();
    extras.putString("screen", "reset");
    if (!email.isEmpty()) {
      extras.putString("email", email);
    }
    extras.putInt("flags", 0);
    // Emitir evento de navegación a MainActivity (UI lo manejará)
    navegarExtras.setValue(extras);
    navegarEvento.setValue(VerificacionActivity.class);
  }

  // este metodo lo q hace es iniciar sesion con los datos proporcionados
  public void iniciarSesion(CharSequence correoCs, CharSequence contrasenaCs) {
    String correo = correoCs != null ? correoCs.toString().trim() : "";
    String contrasena = contrasenaCs != null ? contrasenaCs.toString() : "";

    // Validar campos
    if (correo.isEmpty() || contrasena.isEmpty()) {
      setError("Todos los campos son obligatorios");
      return;
    }

    clearError();
    // Crear solicitud de login
    LoginRequest solicitudLogin = new LoginRequest(correo, contrasena);
    ApiClient.BarberiaServicio servicioBarber = ApiClient.getBarberiaServicio();

    // Llamada asíncrona a la API
    servicioBarber.loginForm(solicitudLogin).enqueue(new Callback<LoginResponse>() {
      @Override
      public void onResponse(Call<LoginResponse> llamada, Response<LoginResponse> respuesta) {
        if (respuesta.isSuccessful() && respuesta.body() != null) {
          LoginResponse respuestaLogin = respuesta.body();

          if (respuestaLogin.getStatus() == 200) {
            // Login exitoso
            String token = respuestaLogin.getToken();
            ApiClient.guardarToken(contexto, token);

            Usuario usuario = respuestaLogin.getUsuario();
            ApiClient.guardarUsuario(contexto, usuario);

            // Emitir evento de navegación a MainActivity (UI lo manejará)
            navegarEvento.setValue(MainActivity.class);
          } else {
            // Error del servidor
            String mensaje = respuestaLogin.getMessage();
            setError(mensaje != null ? mensaje : "Error en autenticación");
          }
        } else {
          // Error en respuesta
          String mensajeError;
          switch (respuesta.code()) {
            case 400:
              mensajeError = "Usuario o contraseña incorrectos";
              break;
            case 401:
              mensajeError = "No autorizado. Verifique sus credenciales";
              break;
            case 403:
              mensajeError = "Acceso denegado";
              break;
            case 404:
              mensajeError = "Servicio no disponible";
              break;
            case 500:
              mensajeError = "Error en el servidor. Intente más tarde";
              break;
            default:
              mensajeError = "Error de conexión. Inténtelo de nuevo";
          }

          Log.d("LoginVM", "Error: " + respuesta.code() + " - " + mensajeError);
          setError(mensajeError);
        }
      }

      // Manejo de fallo en la llamada
      @Override
      public void onFailure(Call<LoginResponse> llamada, Throwable t) {
        String mensajeError = "Error de conexión. Compruebe su red e intente nuevamente";
        Log.e("LoginVM", "Error de conexión: " + t.getMessage(), t);
        setError(mensajeError);
      }
    });
  }
}