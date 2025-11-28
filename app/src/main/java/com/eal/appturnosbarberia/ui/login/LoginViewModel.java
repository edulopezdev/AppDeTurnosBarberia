package com.eal.appturnosbarberia.ui.login;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.MainActivity;
import com.eal.appturnosbarberia.models.LoginRequest;
import com.eal.appturnosbarberia.models.LoginResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> errorMutable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        context = getApplication(); // Contexto de la app
    }

    public LiveData<String> getErrorMutableLiveData() {
        if (errorMutable == null) {
            errorMutable = new MutableLiveData<>();
        }
        return errorMutable;
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            manejarError("Todos los campos son obligatorios");
            return;
        }

        // Crear el cuerpo de la solicitud
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Obtener servicio de la API
        ApiClient.BarberiaServicio barberiaServicio = ApiClient.getBarberiaServicio();

        // Llamada asíncrona al servicio de login
        Call<LoginResponse> call = barberiaServicio.loginForm(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.getStatus() == 200) {
                        String token = loginResponse.getToken();
                        ApiClient.guardarToken(context, token);

                        Usuario usuario = loginResponse.getUsuario();
                        ApiClient.guardarUsuario(context, usuario);
                        Log.d("LoginVM", "Usuario: " + usuario.getNombre() + ", Email: " + usuario.getEmail());

                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                    } else {
                        manejarError(loginResponse.getMessage());
                    }
                } else {
                    Log.e("LoginVM", "Error en la respuesta del servidor. Código: " + response.code());
                    Log.e("LoginVM", "Headers: " + response.headers().toString());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("LoginVM", "Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("LoginVM", "Error al leer el cuerpo de error", e);
                    }
                    manejarError("Error en la respuesta del servidor. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginVM", "Error de conexión: " + t.getMessage(), t);
                manejarError("Error de conexión. Compruebe su red e intente nuevamente");
            }
        });
    }

    private void manejarError(String mensaje) {
        if (errorMutable == null) {
            errorMutable = new MutableLiveData<>();
        }
        errorMutable.setValue(mensaje);
    }
}