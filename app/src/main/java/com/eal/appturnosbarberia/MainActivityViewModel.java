package com.eal.appturnosbarberia;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.request.ApiClient; // Cambia por tu cliente real
import com.eal.appturnosbarberia.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Usuario> usuarioData;
    private ApiClient.BarberiaServicio apiService;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        usuarioData = new MutableLiveData<>();
        apiService = ApiClient.getBarberiaServicio();
    }

    public void obtenerDatosUsuario() {
        // Primero mostrar el usuario guardado localmente
        Usuario usuarioLocal = ApiClient.leerUsuario(getApplication());
        if (usuarioLocal != null) {
            Log.d("DatosUsuario", "Usuario local leído: " + usuarioLocal.getNombre() + " - " + usuarioLocal.getEmail());
            usuarioData.setValue(usuarioLocal);
        } else {
            Log.d("DatosUsuario", "Usuario local es null");
        }
        // Luego intentar refrescar desde la API si hay token
        String token = ApiClient.leerToken(getApplication());
        if (token != null) {
            Log.d("DatosUsuario", "Token encontrado, solicitando usuario a la API...");
            Call<Usuario> usuarioCall = apiService.getUsuario("Bearer " + token);
            usuarioCall.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    Usuario usuarioApi = response.body();
                    if (response.isSuccessful() && usuarioApi != null) {
                        Log.d("DatosUsuario", "Usuario recibido de la API: " + usuarioApi.getNombre() + " - " + usuarioApi.getEmail());
                        if (usuarioApi.getNombre() != null && !usuarioApi.getNombre().isEmpty() && usuarioApi.getEmail() != null && !usuarioApi.getEmail().isEmpty()) {
                            usuarioData.setValue(usuarioApi);
                            // Actualizar usuario local si cambia
                            ApiClient.guardarUsuario(getApplication(), usuarioApi);
                        } else {
                            Log.e("DatosUsuario", "Usuario recibido de la API es inválido (nombre/email null o vacío), se ignora y NO se actualiza el usuario local ni la UI");
                        }
                    } else {
                        Log.e("DatosUsuario", "Error al obtener los datos de la API: Código " + response.code() + ", body: " + usuarioApi);
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("DatosUsuario", "Error de conexión al obtener usuario: " + t.getMessage());
                }
            });
        } else {
            Log.d("DatosUsuario", "Token no encontrado, no se solicita usuario a la API");
        }
    }

    public LiveData<Usuario> getUsuarioData() {
        return usuarioData;
    }
}