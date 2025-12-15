package com.eal.appturnosbarberia.ui.seleccionarTurno;

import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.models.HorarioSlot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionarTurnoViewModel extends AndroidViewModel {

  // LiveData para la UI
  private final MutableLiveData<String> nombreBarbero = new MutableLiveData<>();
  private final MutableLiveData<String> avatarBarbero = new MutableLiveData<>();
  private final MutableLiveData<String> textoMes = new MutableLiveData<>();
  private final MutableLiveData<List<DiaItem>> listaDias = new MutableLiveData<>();
  private final MutableLiveData<List<HorarioSlot>> listaHorarios = new MutableLiveData<>();
  private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();

  // Eventos (Toast y Navegación)
  private final MutableLiveData<String> mensajeToast = new MutableLiveData<>();
  private final MutableLiveData<Boolean> goToAppointments = new MutableLiveData<>();

  // Variables internas para guardar el estado (Lógica)
  private final Calendar calendarioActual = Calendar.getInstance();
  private int barberoId = -1;
  private String fechaSeleccionada = null;
  private MainActivityViewModel mainActivityViewModel;
  private HorarioSlot horarioElegido = null;

  // Constructor
  public SeleccionarTurnoViewModel(@NonNull Application application) {
    super(application);
    // Inicializo listas vacías para que no explote nada
    listaDias.setValue(new ArrayList<>());
    listaHorarios.setValue(new ArrayList<>());
    cargando.setValue(false);
    actualizarTextoMes();
  }

  // este es el setter para el MainActivityViewModel
  public void setMainActivityViewModel(MainActivityViewModel mainVM) {
    this.mainActivityViewModel = mainVM;
  }

  // estos son los getters para los LiveData
  public LiveData<String> getNombreBarbero() { // este metodo es para el nombre del barbero
    return nombreBarbero;
  }
  public LiveData<String> getAvatarBarbero() { // este metodo es para el avatar del barbero
    return avatarBarbero;
  }
  public LiveData<String> getTextoMes() {// este metodo es para el texto del mes
    return textoMes;
  }
  public LiveData<List<DiaItem>> getListaDias() { // este metodo es para la lista de dias
    return listaDias;
  }
  public LiveData<List<HorarioSlot>> getListaHorarios() { // este metodo es para la lista de horarios
    return listaHorarios;
  }
  public LiveData<String> getMensajeToast() { //este metodo es para los mensajes toast
    return mensajeToast;
  }
  public LiveData<Boolean> getGoToAppointments() { // este metodo es para navegar
    return goToAppointments;
  }

  // aca leo los argumentos pasados desde el Fragment y los proceso
  public void leerArgumentos(Bundle args) {
    if (args == null) { // si no hay argumentos, muestro un error
      mensajeToast.setValue("Error al cargar datos del barbero");
      return;
    }
    barberoId = args.getInt("barberoId", -1); // leo el id del barbero
    String nombre = args.getString("barberoNombre"); // leo el nombre del barbero
    String avatar = args.getString("barberoAvatar"); // leo el avatar del barbero

    nombreBarbero.setValue(nombre != null ? nombre : "Barbero"); // seteo el nombre del barbero
    avatarBarbero.setValue(avatar != null ? avatar : ""); // seteo el avatar del barbero

    // Genero los días del mes actual y selecciono el primero
    generarDiasYSeleccionarPrimero();
  }

  // esta es la logica para retroceder mes
  public void mesAnterior() {
    Calendar hoy = Calendar.getInstance();
    calendarioActual.add(Calendar.MONTH, -1);

    // validamos para q no se pueda ir a meses pasados
    if (calendarioActual.get(Calendar.YEAR) < hoy.get(Calendar.YEAR) ||
        (calendarioActual.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
            calendarioActual.get(Calendar.MONTH) < hoy.get(Calendar.MONTH))) {

      // Lo devuelvo al mes actual porque no se puede ir atrás
      calendarioActual.add(Calendar.MONTH, 1);
      mensajeToast.setValue("No puedes ver fechas pasadas");
      return;
    }
    actualizarTextoMes(); // actualizo el texto del mes
    generarDiasYSeleccionarPrimero(); // genero los dias y selecciono el primero
  }

  // aca la logica para avanzar mes
  public void mesSiguiente() {
    calendarioActual.add(Calendar.MONTH, 1);
    actualizarTextoMes(); // actualizo el texto del mes
    generarDiasYSeleccionarPrimero(); // genero los dias y selecciono el primero
  }

  // aca lo q hacemos es actualizar el texto del mes con formato "MMMM yyyy"
  private void actualizarTextoMes() {
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    String texto = sdf.format(calendarioActual.getTime());
    // Pongo la primera letra en mayúscula
    texto = texto.substring(0, 1).toUpperCase() + texto.substring(1);
    textoMes.setValue(texto);
  }

  // aca generamos los dias del mes actual y seleccionamos el primero
  private void generarDiasYSeleccionarPrimero() {
    List<DiaItem> dias = new ArrayList<>();
    Calendar hoy = Calendar.getInstance();
    Calendar calMes = (Calendar) calendarioActual.clone();
    calMes.set(Calendar.DAY_OF_MONTH, 1);
    int maxDias = calMes.getActualMaximum(Calendar.DAY_OF_MONTH);

    for (int i = 1; i <= maxDias; i++) {
      calMes.set(Calendar.DAY_OF_MONTH, i);
      // Solo agrego días que no sean pasados
      if (!calMes.before(hoy)) {
        DiaItem item = new DiaItem();
        item.setDiaNumero(i);
        item.setDiaSemana(obtenerDiaSemanaCorto(calMes.get(Calendar.DAY_OF_WEEK)));
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        item.setFechaIso(isoFormat.format(calMes.getTime()));
        dias.add(item);
      }
    }
    listaDias.setValue(dias); // actualizo la lista de dias

    // Si hay días, selecciono el primero automáticamente
    if (!dias.isEmpty()) {
      seleccionarDia(dias.get(0).getFechaIso());
    } else {
      fechaSeleccionada = null;
      listaHorarios.setValue(new ArrayList<>());
    }
  }

  // aca convierto el dia de la semana a su forma corta en español
  private String obtenerDiaSemanaCorto(int diaSemana) {
    switch (diaSemana) {
      case Calendar.SUNDAY:
        return "Dom";
      case Calendar.MONDAY:
        return "Lun";
      case Calendar.TUESDAY:
        return "Mar";
      case Calendar.WEDNESDAY:
        return "Mié";
      case Calendar.THURSDAY:
        return "Jue";
      case Calendar.FRIDAY:
        return "Vie";
      case Calendar.SATURDAY:
        return "Sáb";
      default:
        return "";
    }
  }

  // este metodo se llama cuando seleccionan un dia
  public void seleccionarDia(String fechaIso) {
    this.fechaSeleccionada = fechaIso;
    // Reseteo la selección de horario al cambiar de día
    this.horarioElegido = null;
    buscarHorariosEnApi(fechaIso);
  }

  // a este lo llamamos cuando seleccionan un horario
  public void seleccionarHorario(HorarioSlot horario) {
    if (horario == null || !horario.isDisponible()) {
      mensajeToast.setValue("Ese horario no está disponible");
      return;
    }
    // lo q hacemos es guardar el horario elegido
    this.horarioElegido = horario;
    mensajeToast.setValue("Seleccionaste: " + horario.getHora());
  }

  // este metodo intenta reservar el turno y maneja la logica
  public void intentarReservar() {
    // Acá validamos la lógica, no en el Fragment
    if (horarioElegido == null) {
      mensajeToast.setValue("Por favor, seleccioná un horario primero");
      return;
    }
    if (fechaSeleccionada == null || barberoId == -1) {
      mensajeToast.setValue("Faltan datos para la reserva");
      return;
    }

    cargando.setValue(true); // muestro el loading

    // aca armo la fecha y hora en formato ISO
    String hora = horarioElegido.getHora();
    String fechaHoraIso = fechaSeleccionada;
    if (!hora.contains("T")) {
      fechaHoraIso = fechaSeleccionada + "T" + hora + ":00";
    } else {
      fechaHoraIso = hora;
    }

    // obtengo el token de autenticacion del cliente
    String token = com.eal.appturnosbarberia.request.ApiClient.leerToken(getApplication());
    if (token == null || token.isEmpty()) {
      cargando.setValue(false);
      mensajeToast.setValue("Debés iniciar sesión");
      return;
    }

    // aca es donde hacemos la llamada a la API para crear el turno
    com.eal.appturnosbarberia.request.ApiClient.BarberiaServicio servicio = com.eal.appturnosbarberia.request.ApiClient
        .getBarberiaServicio(); // obtengo el servicio de la API

    com.eal.appturnosbarberia.models.CreateTurnoRequest req = new com.eal.appturnosbarberia.models.CreateTurnoRequest(
        fechaHoraIso, barberoId, null);

    // hago la llamada asincrona para crear el turno en el backend
    servicio.crearTurno("Bearer " + token, req)
        .enqueue(new Callback<com.eal.appturnosbarberia.models.CrearTurnoResponse>() {
          @Override
          public void onResponse(Call<com.eal.appturnosbarberia.models.CrearTurnoResponse> call,
              Response<com.eal.appturnosbarberia.models.CrearTurnoResponse> response) {
            cargando.setValue(false);
            if (response.isSuccessful()) {
              mensajeToast.setValue("¡Turno reservado con éxito!");
              // Refrescar los turnos en el MainActivityViewModel
              if (mainActivityViewModel != null) {
                mainActivityViewModel.refrescarTurnos();
              }
              // Aviso al Fragment que hay que navegar
              goToAppointments.setValue(true);
            } else {
              // El backend nos dice qué salió mal
              String errorMsg = extraerMensajeError(response.errorBody());
              mensajeToast.setValue(errorMsg);
            }
          }

          // aca manejamos el fallo de la llamada
          @Override
          public void onFailure(Call<com.eal.appturnosbarberia.models.CrearTurnoResponse> call, Throwable t) {
            cargando.setValue(false); // oculto el loading
            mensajeToast.setValue("Error de conexión");
          }
        });
  }

  // este metodo lo llamamos cuando terminamos de navegar
  public void doneNavigating() {
    goToAppointments.setValue(false); // lo q hacemos es resetear el LiveData
  }

  // este metodo extrae el mensaje de error del cuerpo de la respuesta
  private String extraerMensajeError(okhttp3.ResponseBody errorBody) {
    try {
      if (errorBody != null) {
        String json = errorBody.string();
        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        if (obj.has("message")) {
          return obj.get("message").getAsString();
        }
        if (obj.has("error")) {
          return obj.get("error").getAsString();
        }
      }
    } catch (Exception ignored) {
    }
    return "No se pudo completar la reserva";
  }

  // ahora buscamos los horarios en la API
  private void buscarHorariosEnApi(String fechaIso) {
    if (barberoId < 0) { // si no tenemos barberoId, no hacemos nada
      listaHorarios.setValue(generarSlotsVacios()); // pongo slots vacíos
      return;
    }

    com.eal.appturnosbarberia.request.ApiClient.BarberiaServicio servicio = com.eal.appturnosbarberia.request.ApiClient
        .getBarberiaServicio(); // obtengo el servicio de la API

    servicio.getHorariosDisponibles(barberoId, fechaIso)
        .enqueue(new Callback<com.eal.appturnosbarberia.models.HorariosResponse>() {
          @Override
          public void onResponse(Call<com.eal.appturnosbarberia.models.HorariosResponse> call,
              Response<com.eal.appturnosbarberia.models.HorariosResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
              List<HorarioSlot> ocupados = response.body().getData();
              List<HorarioSlot> todos = generarSlotsVacios();

              // Cruzo los datos para ver cuál está libre
              if (ocupados != null) {
                for (HorarioSlot ocupado : ocupados) {
                  for (HorarioSlot slot : todos) {
                    // Si coincide la hora, lo marco disponible (asumiendo lógica de tu backend)
                    if (slot.getHora().equals(ocupado.getHora())) {
                      slot.setDisponible(true);
                      break;
                    }
                  }
                }
              }
              listaHorarios.setValue(todos);
            } else {
              listaHorarios.setValue(generarSlotsVacios());
            }
          }

          // aca manejamos el fallo de la llamada
          @Override
          public void onFailure(Call<com.eal.appturnosbarberia.models.HorariosResponse> call, Throwable t) {
            listaHorarios.setValue(generarSlotsVacios());
            mensajeToast.setValue("No se pudieron cargar horarios");
          }
        });
  }

  // aca basicamente lo q hcemos es generar una lista de horarios vacíos para el dia y hora
  private List<HorarioSlot> generarSlotsVacios() {
    List<HorarioSlot> lista = new ArrayList<>();
    for (int hora = 10; hora <= 20; hora++) {
      HorarioSlot h = new HorarioSlot();
      h.setHora(String.format(Locale.US, "%02d:00", hora));
      h.setDisponible(false);
      lista.add(h);
    }
    return lista;
  }
}