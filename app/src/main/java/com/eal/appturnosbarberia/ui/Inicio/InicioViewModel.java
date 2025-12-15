package com.eal.appturnosbarberia.ui.Inicio;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.models.DashboardClienteResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;
import com.eal.appturnosbarberia.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioViewModel extends AndroidViewModel {

  private static final String ETIQUETA = "InicioViewModel";

  // LiveData DINÁMICOS (necesarios)
  private MutableLiveData<String> textoSaludo = new MutableLiveData<>();
  private MutableLiveData<Integer> visibilidadTarjetaProximoTurno = new MutableLiveData<>(android.view.View.GONE);
  private MutableLiveData<Integer> visibilidadTarjetaSinTurno = new MutableLiveData<>(android.view.View.VISIBLE);
  private MutableLiveData<String> tituloProximoTurno = new MutableLiveData<>();
  private MutableLiveData<String> horaProximoTurno = new MutableLiveData<>();
  private MutableLiveData<String> estadoProximoTurno = new MutableLiveData<>();
  private MutableLiveData<String> barberoProximoTurno = new MutableLiveData<>();
  private MutableLiveData<String> detalleProximoTurno = new MutableLiveData<>();
  private MutableLiveData<String> nombreBarberia = new MutableLiveData<>();
  private MutableLiveData<String> direccionBarberia = new MutableLiveData<>();
  private MutableLiveData<String> urlMaps = new MutableLiveData<>();
  private MutableLiveData<java.util.List<DashboardClienteResponse.Servicio>> servicios = new MutableLiveData<>();
  private MutableLiveData<String> error = new MutableLiveData<>();
  private MutableLiveData<Integer> navegarASeleccionarBarbero = new MutableLiveData<>();
  private MutableLiveData<Boolean> navegarATurnos = new MutableLiveData<>();

  public InicioViewModel(@NonNull Application aplicacion) {
    super(aplicacion);
  }

  // Getters para todas las LiveData
  public LiveData<String> obtenerTextoSaludo() {
    return textoSaludo;
  }

  public LiveData<Integer> obtenerVisibilidadTarjetaProximoTurno() {
    return visibilidadTarjetaProximoTurno;
  }

  public LiveData<Integer> obtenerVisibilidadTarjetaSinTurno() {
    return visibilidadTarjetaSinTurno;
  }

  public LiveData<String> obtenerTituloProximoTurno() {
    return tituloProximoTurno;
  }

  public LiveData<String> obtenerHoraProximoTurno() {
    return horaProximoTurno;
  }

  public LiveData<String> obtenerEstadoProximoTurno() {
    return estadoProximoTurno;
  }

  public LiveData<String> obtenerBarberoProximoTurno() {
    return barberoProximoTurno;
  }

  public LiveData<String> obtenerDetalleProximoTurno() {
    return detalleProximoTurno;
  }

  public LiveData<String> obtenerNombreBarberia() {
    return nombreBarberia;
  }

  public LiveData<String> obtenerDireccionBarberia() {
    return direccionBarberia;
  }

  public LiveData<String> obtenerUrlMaps() {
    return urlMaps;
  }

  public LiveData<java.util.List<DashboardClienteResponse.Servicio>> obtenerServicios() {
    return servicios;
  }

  public LiveData<String> obtenerError() {
    return error;
  }

  public LiveData<Integer> obtenerNavegarASeleccionarBarbero() {
    return navegarASeleccionarBarbero;
  }

  public LiveData<Boolean> obtenerNavegarATurnos() {
    return navegarATurnos;
  }

  // Inicializar con usuario y token
  public void inicializar(Usuario usuario, String token) {
    Log.d(ETIQUETA, "inicializar llamado");

    // Actualizar saludo con usuario
    if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
      textoSaludo.setValue("¡Hola, " + usuario.getNombre() + "!");
    } else {
      textoSaludo.setValue("¡Hola!");
    }

    // Cargar dashboard si hay token
    if (token != null) {
      obtenerDashboard(token);
    } else {
      error.setValue("No autenticado");
    }
  }

  // Obtener dashboard desde API
  private void obtenerDashboard(String token) {
    ApiClient.getBarberiaServicio().getDashboardCliente("Bearer " + token)
        .enqueue(new Callback<DashboardClienteResponse>() {
          @Override
          public void onResponse(Call<DashboardClienteResponse> llamada, Response<DashboardClienteResponse> respuesta) {
            if (respuesta.isSuccessful() && respuesta.body() != null) {
              DashboardClienteResponse envoltorio = respuesta.body();
              if (envoltorio.getStatus() == 200 && envoltorio.getData() != null) {
                renderizarDashboard(envoltorio.getData());
              } else {
                String msg = envoltorio.getMessage() != null ? envoltorio.getMessage()
                    : "No se pudo obtener el dashboard";
                error.setValue(msg);
              }
            } else {
              error.setValue("No se pudo obtener el dashboard (código " + respuesta.code() + ")");
            }
          }

          @Override
          public void onFailure(Call<DashboardClienteResponse> llamada, Throwable t) {
            error.setValue("Error de conexión: " + t.getMessage());
          }
        });
  }

  // Procesar datos del dashboard y actualizar LiveData
  private void renderizarDashboard(DashboardClienteResponse.Data d) {
    try {
      // Próximo turno
      DashboardClienteResponse.ProximoTurno proximoTurno = d.getProximoTurno();
      if (proximoTurno != null) {
        visibilidadTarjetaProximoTurno.setValue(android.view.View.VISIBLE);
        visibilidadTarjetaSinTurno.setValue(android.view.View.GONE);
        tituloProximoTurno.setValue(formatearFechaLarga(proximoTurno.getFechaHora()));

        String hora = formatearHora(proximoTurno.getFechaHora());
        String nombreEstado = proximoTurno.getEstadoNombre() != null ? proximoTurno.getEstadoNombre() : "Por confirmar";
        String nombreBarbero = proximoTurno.getBarberoNombre() != null ? proximoTurno.getBarberoNombre()
            : "Barbero desconocido";

        // Separar los datos
        horaProximoTurno.setValue(hora);
        estadoProximoTurno.setValue(nombreEstado);
        barberoProximoTurno.setValue(nombreBarbero);

        // Mantener detalle para compatibilidad (aunque no se use)
        String detalle = hora + " • " + nombreEstado + " • Con " + nombreBarbero;
        detalleProximoTurno.setValue(detalle);
      } else {
        visibilidadTarjetaProximoTurno.setValue(android.view.View.GONE);
        visibilidadTarjetaSinTurno.setValue(android.view.View.VISIBLE);
      }

      // Servicios
      if (d.getServicios() != null && !d.getServicios().isEmpty()) {
        servicios.setValue(d.getServicios());
      }

      // Barbería
      if (d.getBarberia() != null) {
        DashboardClienteResponse.Barberia b = d.getBarberia();
        nombreBarberia.setValue(b.getNombre());
        direccionBarberia.setValue(b.getDireccion());

        // Agregar parámetro de zoom a la URL de maps
        String mapsUrl = b.getMapsUrl();
        if (mapsUrl != null && !mapsUrl.isEmpty() && !mapsUrl.contains("z=")) {
          mapsUrl = mapsUrl + (mapsUrl.contains("?") ? "&z=18" : "?z=18");
        }

        // Garantizar que urlMaps nunca sea null
        urlMaps.setValue(mapsUrl != null ? mapsUrl : "");
      }
    } catch (Exception ex) {
      Log.e(ETIQUETA, "Error en renderizarDashboard: " + ex.getMessage(), ex);
      error.setValue("Error al procesar datos del dashboard");
    }
  }

  // Métodos auxiliares para formateo
  public String formatearFechaLarga(String iso) {
    if (iso == null || iso.isEmpty())
      return "";
    try {
      SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
      parseador.setTimeZone(TimeZone.getDefault());
      Date d = parseador.parse(iso);
      SimpleDateFormat salida = new SimpleDateFormat("EEEE, d 'de' MMMM 'a las' HH:mm'h'", new Locale("es", "ES"));
      salida.setTimeZone(TimeZone.getDefault());
      String formateada = salida.format(d);
      return formateada.substring(0, 1).toUpperCase() + formateada.substring(1);
    } catch (Exception ex) {
      Log.w(ETIQUETA, "Error formateando fecha: " + ex.getMessage());
      return iso;
    }
  }

  public String formatearHora(String iso) {
    if (iso == null || iso.isEmpty())
      return "";
    try {
      SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
      parseador.setTimeZone(TimeZone.getDefault());
      Date d = parseador.parse(iso);
      SimpleDateFormat salida = new SimpleDateFormat("HH:mm", Locale.getDefault());
      return salida.format(d);
    } catch (Exception ex) {
      Log.w(ETIQUETA, "Error formateando hora: " + ex.getMessage());
      return iso;
    }
  }

}