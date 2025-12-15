package com.eal.appturnosbarberia.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.eal.appturnosbarberia.models.LoginRequest;
import com.eal.appturnosbarberia.models.LoginResponse;
import com.eal.appturnosbarberia.models.PerfilResponse;
import com.eal.appturnosbarberia.models.RegisterRequest;
import com.eal.appturnosbarberia.models.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class ApiClient {

  // esta es la URL base de la API
  public static final String BASE_URL = "http://192.168.0.108:5000/api/";

  // metodo para crear el servicio de Retrofit con Gson
  public static BarberiaServicio getBarberiaServicio() {
    Gson gson = new GsonBuilder().setLenient().create();// aca creamos el objeto Gson para manejar JSON

    // Logging interceptor para ver requests/responses (headers + body)
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Loguear cuerpo de requests y responses

    // aca lo q hacemos es configurar el cliente OkHttp con tiempos de espera y el interceptor
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();

    // ahora creamos la instancia de Retrofit con la configuracion anterior
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    return retrofit.create(BarberiaServicio.class);
  }

  // definimos la interfaz para los endpoints de la API
  public interface BarberiaServicio {
    @POST("auth/login") // endpoint de login
    Call<LoginResponse> loginForm(@retrofit2.http.Body LoginRequest loginRequest);
    @GET("Usuarios") // endpoint para obtener datos del usuario
    Call<Usuario> getUsuario(@Header("Authorization") String token);
    @GET("Turnos/mis-turnos") // endpoint para obtener los turnos del cliente logueado
    Call<com.eal.appturnosbarberia.models.TurnosResponse> getMisTurnos(@Header("Authorization") String token);
    @GET("Turnos/barberos") // endpoint para obtener la lista de barberos
    Call<com.eal.appturnosbarberia.models.BarberosResponse> getBarberos(@Header("Authorization") String token,
        @retrofit2.http.Query("onlyActive") boolean onlyActive, // solo filtramos los activos
        @retrofit2.http.Query("limit") int limit); // limitamos la cantidad de resultados
    @POST("Turnos/{id}/cancelar") // endpoint para cancelar un turno por ID, solo para clientes logueados
    retrofit2.Call<okhttp3.ResponseBody> cancelarTurno(@Header("Authorization") String token,
        @retrofit2.http.Path("id") int turnoId, // ID del turno a cancelar
        @retrofit2.http.Body com.eal.appturnosbarberia.models.CancelarTurnoRequest body); // cuerpo con motivo de cancelación, opcional
    @POST("auth/register") // endpoint de registro (deprecado porque actualmente se usa el endpoint nuevo que es /Autenticacion/Registro)
    Call<LoginResponse> register(@retrofit2.http.Body RegisterRequest registerRequest);
    @POST("public/Autenticacion/Registro") // Nuevo endpoint de registro
    retrofit2.Call<okhttp3.ResponseBody> registerAuth(
        @retrofit2.http.Body com.eal.appturnosbarberia.models.AuthRegisterRequest request);
    @POST("public/Autenticacion/Verificar") // endpoint para verificar código de registro
    retrofit2.Call<okhttp3.ResponseBody> verificarCodigo(
        @retrofit2.http.Body com.eal.appturnosbarberia.models.VerifyRequest request);
    @POST("public/Autenticacion/ReenviarCodigo") // endpoint para reenviar código de verificación
    retrofit2.Call<okhttp3.ResponseBody> reenviarCodigo(
        @retrofit2.http.Body com.eal.appturnosbarberia.models.ReenviarCodigoRequest request);
    @GET("Turnos/horarios-disponibles") // endpoint para obtener horarios disponibles en una fecha para un barbero
    Call<com.eal.appturnosbarberia.models.HorariosResponse> getHorariosDisponibles(
        @Query("barberoId") int barberoId, // ID del barbero
        @Query("fecha") String fecha); // fecha en formato AAAA-MM-DD
    @POST("Turnos") // endpoint para crear un nuevo turno
    Call<com.eal.appturnosbarberia.models.CrearTurnoResponse> crearTurno(
            @Header("Authorization") String token,
            @Body com.eal.appturnosbarberia.models.CreateTurnoRequest body
    );
    @GET("Usuarios/perfil") // endpoint para obtener el perfil del usuario autenticado
    Call<com.eal.appturnosbarberia.models.PerfilResponse> getPerfil(
        @retrofit2.http.Header("Authorization") String token);
    // Actualizar perfil del usuario autenticado
    @Multipart
    @PUT("Usuarios/perfil") // endpoint para actualizar el perfil del usuario autenticado
    Call<PerfilResponse> actualizarPerfil(
            @Header("Authorization") String token,
            @Part("Nombre") RequestBody nombre,
            @Part("Email") RequestBody email,
            @Part("Telefono") RequestBody telefono,
            @Part("Password") RequestBody password,
            @Part("EliminarAvatar") RequestBody eliminarAvatar,
            @Part MultipartBody.Part file // usamos multipart porque puede incluir archivo
    );
    // Solicitar código de reseteo (Olvidé mi contraseña)
    @POST("public/Autenticacion/OlvideContrasena") // endpoint para solicitar reseteo de contraseña
    retrofit2.Call<okhttp3.ResponseBody> olvideContrasena(
        @retrofit2.http.Body com.eal.appturnosbarberia.models.OlvideContrasenaRequest request);
    @POST("public/Autenticacion/ResetearContrasena") // endpoint para resetear la contraseña
    retrofit2.Call<okhttp3.ResponseBody> resetearContrasena(
        @retrofit2.http.Body com.eal.appturnosbarberia.models.ResetearContrasenaRequest request);
    @GET("dashboard/cliente") // endpoint para obtener datos del dashboard del cliente
    Call<com.eal.appturnosbarberia.models.DashboardClienteResponse> getDashboardCliente(
        @Header("Authorization") String token);
  }

  // este metodo lo q hace es guardar el token en las SharedPreferences
  public static void guardarToken(Context context, String token) {
    SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString("token", token);
    editor.apply();
  }

  // este metodo lo q hace es leer el token de las SharedPreferences
  public static String leerToken(Context context) {
    SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
    return sp.getString("token", null);
  }

  // este metodo lo q hace es guardar el usuario en las SharedPreferences
  public static void guardarUsuario(Context context, Usuario usuario) {
    SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    Gson gson = new Gson();
    String usuarioJson = gson.toJson(usuario);
    editor.putString("usuario", usuarioJson);
    editor.apply();
  }

  // este metodo lo q hace es leer el usuario de las SharedPreferences
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