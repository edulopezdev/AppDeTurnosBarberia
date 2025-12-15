package com.eal.appturnosbarberia.ui.perfil;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.models.PerfilResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

  private static final String TAG = "PerfilViewModel";
  private static final int MAX_AVATAR_BYTES = 2 * 1024 * 1024; // 2 MB

  // Estado de la UI
  private final MutableLiveData<Boolean> isEditable = new MutableLiveData<>(false);
  private final MutableLiveData<String> buttonText = new MutableLiveData<>("Editar");
  private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
  private final MutableLiveData<Integer> deleteIconVisibility = new MutableLiveData<>(View.GONE);

  // Datos del usuario
  private final MutableLiveData<String> nombre = new MutableLiveData<>("");
  private final MutableLiveData<String> email = new MutableLiveData<>("");
  private final MutableLiveData<String> telefono = new MutableLiveData<>("");
  private final MutableLiveData<String> avatarUrl = new MutableLiveData<>("");

  // Eventos
  private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
  private final MutableLiveData<Void> pickImageEvent = new MutableLiveData<>();
  private final MutableLiveData<Void> showDeleteDialogEvent = new MutableLiveData<>();

  // Estado interno
  private Uri selectedImageUri = null;
  private boolean eliminarAvatarFlag = false;

  public PerfilViewModel(@NonNull Application application) {
    super(application);
  }

  // Getters
  public LiveData<Boolean> getIsEditable() { return isEditable; }
  public LiveData<String> getButtonText() { return buttonText; }
  public LiveData<Boolean> getLoading() { return loading; }
  public LiveData<Integer> getDeleteIconVisibility() { return deleteIconVisibility; }
  public LiveData<String> getNombre() { return nombre; }
  public LiveData<String> getEmail() { return email; }
  public LiveData<String> getTelefono() { return telefono; }
  public LiveData<String> getAvatarUrl() { return avatarUrl; }
  public LiveData<String> getToastMessage() { return toastMessage; }
  public LiveData<Void> getPickImageEvent() { return pickImageEvent; }
  public LiveData<Void> getShowDeleteDialogEvent() { return showDeleteDialogEvent; }

   // Cargar perfil desde API
  public void cargarPerfil() {
    String token = ApiClient.leerToken(getApplication());
    if (token == null) {
      toastMessage.setValue("Iniciá sesión para ver tu perfil");
      return;
    }

    loading.setValue(true);

    // Llamada API para obtener perfil
    ApiClient.getBarberiaServicio().getPerfil("Bearer " + token).enqueue(new Callback<PerfilResponse>() {
      @Override
      public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
        loading.setValue(false);
        if (response.isSuccessful() && response.body() != null) {
          Usuario u = response.body().getUsuario();
          if (u != null) {
            actualizarDatosUI(u);
          }
        } else {
          toastMessage.setValue("No se pudo obtener el perfil");
        }
      }

      @Override
      public void onFailure(Call<PerfilResponse> call, Throwable t) {
        loading.setValue(false);
        toastMessage.setValue("Error de conexión: " + t.getMessage());
      }
    });
  }

  // este metodo actualiza los LiveData con los datos del usuario
  private void actualizarDatosUI(Usuario u) {
    nombre.setValue(u.getNombre());
    email.setValue(u.getEmail());
    telefono.setValue(u.getTelefono());
    
    String url = u.getAvatarUrl();
    avatarUrl.setValue(url);

    boolean hasAvatarReal = url != null && !url.isEmpty() && !url.endsWith("/avatars/no_avatar.jpg");
    deleteIconVisibility.setValue(hasAvatarReal ? View.VISIBLE : View.GONE);
    
    // Resetear estado de edición
    selectedImageUri = null;
    eliminarAvatarFlag = false;
    setEditMode(false);
  }

  private void setEditMode(boolean editable) {
    isEditable.setValue(editable);
    buttonText.setValue(editable ? "Actualizar" : "Editar");
    if (editable) {
      // Al editar, mostramos el icono de borrar si hay imagen o si seleccionó una nueva
      deleteIconVisibility.setValue(View.VISIBLE);
    }
  }

  // Acción del botón principal (Editar / Guardar)
  public void onBotonAccionClicked(String nombreInput, String emailInput, String phoneInput, String passInput) {
    if (Boolean.TRUE.equals(isEditable.getValue())) {
      // Estamos en modo edición -> Guardar
      guardarCambios(nombreInput, emailInput, phoneInput, passInput);
    } else {
      // Estamos en modo lectura -> Activar edición
      setEditMode(true);
    }
  }

  public void onAddImageClicked() {
    pickImageEvent.setValue(null);
  }

  public void onImageSelected(Uri uri) {
    if (uri != null) {
      selectedImageUri = uri;
      eliminarAvatarFlag = false;
      // Actualizar UI localmente (preview)
      avatarUrl.setValue(uri.toString());
      deleteIconVisibility.setValue(View.VISIBLE);
      
      // Si no estábamos editando, activar modo edición
      if (!Boolean.TRUE.equals(isEditable.getValue())) {
        setEditMode(true);
      }
    }
  }

  public void onDeleteImageClicked() {
    showDeleteDialogEvent.setValue(null);
  }

  public void confirmarEliminarAvatar() {
    // Ejecutar borrado inmediato en el servidor
    deleteAvatarImmediate();
  }

  private void guardarCambios(String nombreIn, String emailIn, String phoneIn, String passIn) {
    String token = ApiClient.leerToken(getApplication());
    if (token == null) return;

    if (passIn != null && !passIn.isEmpty() && passIn.length() < 6) {
      toastMessage.setValue("La contraseña debe tener al menos 6 caracteres");
      return;
    }

    loading.setValue(true);

    RequestBody rbNombre = RequestBody.create(MediaType.parse("text/plain"), nombreIn != null ? nombreIn : "");
    RequestBody rbEmail = RequestBody.create(MediaType.parse("text/plain"), emailIn != null ? emailIn : "");
    RequestBody rbTelefono = RequestBody.create(MediaType.parse("text/plain"), phoneIn != null ? phoneIn : "");
    RequestBody rbPassword = RequestBody.create(MediaType.parse("text/plain"), passIn != null ? passIn : "");
    RequestBody rbEliminarAvatar = RequestBody.create(MediaType.parse("text/plain"), eliminarAvatarFlag ? "true" : "false");

    MultipartBody.Part avatarPart = null;

    if (!eliminarAvatarFlag && selectedImageUri != null) {
      byte[] data = procesarImagen(selectedImageUri);
      if (data == null) {
        loading.setValue(false);
        return; // Error ya notificado en procesarImagen
      }
      RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), data);
      avatarPart = MultipartBody.Part.createFormData("file", "avatar.jpg", reqFile);
    }

    ApiClient.getBarberiaServicio()
        .actualizarPerfil("Bearer " + token, rbNombre, rbEmail, rbTelefono, rbPassword, rbEliminarAvatar, avatarPart)
        .enqueue(new Callback<PerfilResponse>() {
          @Override
          public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
            loading.setValue(false);
            if (response.isSuccessful() && response.body() != null) {
              toastMessage.setValue("Perfil actualizado correctamente");
              if (response.body().getUsuario() != null) {
                ApiClient.guardarUsuario(getApplication(), response.body().getUsuario());
                actualizarDatosUI(response.body().getUsuario());
              }
            } else {
              manejarErrorServidor(response);
            }
          }

          @Override
          public void onFailure(Call<PerfilResponse> call, Throwable t) {
            loading.setValue(false);
            toastMessage.setValue("Error de conexión: " + t.getMessage());
          }
        });
  }

  private void deleteAvatarImmediate() {
    String token = ApiClient.leerToken(getApplication());
    if (token == null) return;

    // Usamos los valores actuales de los LiveData para rellenar los campos obligatorios
    String nombreVal = nombre.getValue() != null ? nombre.getValue() : "";
    String emailVal = email.getValue() != null ? email.getValue() : "";
    String telVal = telefono.getValue() != null ? telefono.getValue() : "";

    RequestBody rbNombre = RequestBody.create(MediaType.parse("text/plain"), nombreVal);
    RequestBody rbEmail = RequestBody.create(MediaType.parse("text/plain"), emailVal);
    RequestBody rbTelefono = RequestBody.create(MediaType.parse("text/plain"), telVal);
    RequestBody rbPassword = RequestBody.create(MediaType.parse("text/plain"), "");
    RequestBody rbEliminarAvatar = RequestBody.create(MediaType.parse("text/plain"), "true");

    loading.setValue(true);

    ApiClient.getBarberiaServicio()
        .actualizarPerfil("Bearer " + token, rbNombre, rbEmail, rbTelefono, rbPassword, rbEliminarAvatar, null)
        .enqueue(new Callback<PerfilResponse>() {
          @Override
          public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
            loading.setValue(false);
            if (response.isSuccessful() && response.body() != null) {
              toastMessage.setValue("Avatar eliminado correctamente");
              if (response.body().getUsuario() != null) {
                ApiClient.guardarUsuario(getApplication(), response.body().getUsuario());
                actualizarDatosUI(response.body().getUsuario());
              }
            } else {
              manejarErrorServidor(response);
            }
          }

          @Override
          public void onFailure(Call<PerfilResponse> call, Throwable t) {
            loading.setValue(false);
            toastMessage.setValue("Error de conexión: " + t.getMessage());
          }
        });
  }

  private void manejarErrorServidor(Response<?> response) {
    String err = "Error al procesar solicitud";
    if (response.code() == 413) err = "El archivo supera el límite permitido (2 MB).";
    else if (response.code() == 415) err = "Tipo de archivo no soportado.";
    else if (response.code() == 500) err = "Error interno del servidor.";
    
    toastMessage.setValue(err);
  }

  // --- Procesamiento de Imagen ---

  private byte[] procesarImagen(Uri uri) {
    try {
      InputStream is = getApplication().getContentResolver().openInputStream(uri);
      if (is == null) {
        toastMessage.setValue("No se pudo abrir el archivo");
        return null;
      }
      ByteArrayOutputStream rawBaos = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int len;
      while ((len = is.read(buffer)) != -1) {
        rawBaos.write(buffer, 0, len);
      }
      byte[] originalBytes = rawBaos.toByteArray();
      rawBaos.close();
      is.close();

      if (originalBytes.length == 0) {
        toastMessage.setValue("El archivo está vacío");
        return null;
      }

      Bitmap original = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.length);
      if (original == null) {
        toastMessage.setValue("No se pudo procesar la imagen");
        return null;
      }

      // Rotar
      try {
        ExifInterface exif = new ExifInterface(new ByteArrayInputStream(originalBytes));
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        original = rotateBitmap(original, orientation);
      } catch (Exception e) {
        Log.w(TAG, "Error EXIF: " + e.getMessage());
      }

      // Recortar y comprimir
      Bitmap resized = centerCropBitmap(original, 240, 240);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
      byte[] data = baos.toByteArray();
      baos.close();

      if (data.length > MAX_AVATAR_BYTES) {
        toastMessage.setValue("El archivo supera el límite de 2MB");
        return null;
      }
      return data;

    } catch (Exception e) {
      Log.e(TAG, "Error procesando imagen", e);
      toastMessage.setValue("Error procesando imagen");
      return null;
    }
  }

  private Bitmap centerCropBitmap(Bitmap src, int targetW, int targetH) {
    if (src == null) return null;
    int srcW = src.getWidth();
    int srcH = src.getHeight();
    float scale = Math.max((float) targetW / srcW, (float) targetH / srcH);
    int scaledW = Math.round(scale * srcW);
    int scaledH = Math.round(scale * srcH);
    Bitmap scaled = Bitmap.createScaledBitmap(src, scaledW, scaledH, true);
    int x = Math.max(0, (scaledW - targetW) / 2);
    int y = Math.max(0, (scaledH - targetH) / 2);
    return Bitmap.createBitmap(scaled, x, y, targetW, targetH);
  }

  private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
    Matrix matrix = new Matrix();
    switch (orientation) {
      case ExifInterface.ORIENTATION_ROTATE_90: matrix.postRotate(90); break;
      case ExifInterface.ORIENTATION_ROTATE_180: matrix.postRotate(180); break;
      case ExifInterface.ORIENTATION_ROTATE_270: matrix.postRotate(270); break;
      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: matrix.postScale(-1, 1); break;
      case ExifInterface.ORIENTATION_FLIP_VERTICAL: matrix.postScale(1, -1); break;
      default: return bitmap;
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  }
}