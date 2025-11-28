package com.eal.appturnosbarberia.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.eal.appturnosbarberia.models.RegisterRequest;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.models.LoginResponse;
import com.eal.appturnosbarberia.models.LoginRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.0.109:5000/api/";

    // Método para crear el servicio de Retrofit con Gson
    public static BarberiaServicio getBarberiaServicio() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(BarberiaServicio.class);
    }

    // Interfaz con los endpoints de la API
    public interface BarberiaServicio {
        @POST("auth/login")
        Call<LoginResponse> loginForm(@retrofit2.http.Body LoginRequest loginRequest);
        @GET("Usuarios")
        Call<Usuario> getUsuario(@Header("Authorization") String token);
        @POST("auth/register")
        Call<LoginResponse> register(@retrofit2.http.Body RegisterRequest registerRequest);
    }

    public static void guardarToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    public static void guardarUsuario(Context context, Usuario usuario) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String usuarioJson = gson.toJson(usuario);
        editor.putString("usuario", usuarioJson);
        editor.apply();
    }

    public static Usuario leerUsuario(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String usuarioJson = sp.getString("usuario", null);
        if (usuarioJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(usuarioJson, Usuario.class);
        }
        return null;
    }
}