package com.eal.appturnosbarberia.ui.perfil;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.models.PerfilResponse;
import com.eal.appturnosbarberia.models.Usuario;
import com.eal.appturnosbarberia.request.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Matrix;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Base64;

import androidx.exifinterface.media.ExifInterface;

public class PerfilFragment extends Fragment {

  private static final String TAG = "PerfilFragment";
  private ImageView ivAvatar;
  private ImageView ivAddImage;
  private ImageView ivDeleteImage;
  private TextInputEditText etNombre, etEmail, etPhone, etPassword;
  private MaterialButton btEditarGuardar;
  private ActivityResultLauncher<String> pickImageLauncher;
  private Uri selectedImageUri = null;
  private boolean editMode = false;
  private boolean eliminarAvatarFlag = false;
  private static final int MAX_AVATAR_BYTES = 2 * 1024 * 1024; // 2 MB

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_perfil, container, false);

    ivAvatar = root.findViewById(R.id.ivAvatarPerfil);
    ivAddImage = root.findViewById(R.id.ivAddImage);
    ivDeleteImage = root.findViewById(R.id.ivDeleteImage); // <-- enlazado
    // Aseguramos que el icono esté visible y habilitado por defecto
    ivDeleteImage.setVisibility(View.VISIBLE);
    ivDeleteImage.setEnabled(true);
    ivDeleteImage.setClickable(true);

    etNombre = root.findViewById(R.id.etNombre);
    etEmail = root.findViewById(R.id.etEmail);
    etPhone = root.findViewById(R.id.etPhone);
    etPassword = root.findViewById(R.id.etPassword);
    btEditarGuardar = root.findViewById(R.id.btEditarGuardar);

    // Email siempre readonly
    etEmail.setEnabled(false);

    // Registrar launcher para seleccionar imagen desde galería (GetContent)
    pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
      if (uri != null) {
        selectedImageUri = uri;
        eliminarAvatarFlag = false; // si eligió imagen, no eliminar
        Log.d(TAG, "Imagen seleccionada: " + uri);
        // Mostrar preview con Glide
        try {
          Glide.with(requireContext()).load(uri).circleCrop().into(ivAvatar);
        } catch (Exception ex) {
          Log.w(TAG, "Glide preview falló: " + ex.getMessage());
        }
        ivDeleteImage.setVisibility(View.VISIBLE);
        // Indicar al usuario que debe actualizar para guardar cambios
        if (!editMode) {
          setEditable(true);
          editMode = true;
        }
        btEditarGuardar.setText("Actualizar");
      }
    });

    // Clicks
    ivAddImage.setOnClickListener(v -> {
      // Abrir selector de imágenes (MIME types image/*)
      Log.d(TAG, "Abrir selector de imagen");
      pickImageLauncher.launch("image/*");
    });

    ivDeleteImage.setOnClickListener(v -> {
      new androidx.appcompat.app.AlertDialog.Builder(requireContext())
          .setTitle("Eliminar avatar")
          .setMessage("¿Estás seguro que querés eliminar tu avatar?")
          .setNegativeButton("Cancelar", (d, i) -> d.dismiss())
          .setPositiveButton("Eliminar", (d, i) -> {
            // Ejecutar borrado inmediato en el servidor
            deleteAvatarImmediate();
          })
          .show();
    });

    btEditarGuardar.setOnClickListener(v -> {
      if (!editMode) {
        setEditable(true);
        editMode = true;
        btEditarGuardar.setText("Actualizar");
      } else {
        actualizarPerfil();
      }
    });

    setEditable(false); // inicial: modo lectura
    cargarPerfil();
    return root;
  }

  private void setEditable(boolean editable) {
    // Email siempre readonly
    etEmail.setEnabled(false);

    etNombre.setEnabled(editable);
    etPhone.setEnabled(editable);
    etPassword.setEnabled(editable);

    // Visual: alpha para indicar bloqueo/edición
    float alphaEnabled = editable ? 1.0f : 0.65f;
    etNombre.setAlpha(alphaEnabled);
    etPhone.setAlpha(alphaEnabled);
    etPassword.setAlpha(editable ? 1.0f : 0.65f);
    etEmail.setAlpha(0.65f); // siempre se ve bloqueado

    // Iconos avatar: añadir siempre visible; X visible siempre (no lo ocultamos
    // aquí)
    ivAddImage.setVisibility(View.VISIBLE);
    ivDeleteImage.setVisibility(View.VISIBLE); // <-- siempre visible

    // Botón apariencia (si está en modo edición mostrar "Actualizar")
    if (editable) {
      btEditarGuardar.setText("Actualizar");
    } else {
      btEditarGuardar.setText("Editar");
    }
  }

  // Helper: comprobar si el usuario tiene avatar real guardado localmente
  // (fallback)
  private boolean getStoredAvatarPresent() {
    Usuario u = ApiClient.leerUsuario(requireContext());
    if (u == null)
      return false;
    String avatarUrl = u.getAvatarUrl();
    if (avatarUrl == null || avatarUrl.isEmpty())
      return false;
    return !avatarUrl.endsWith("/avatars/no_avatar.jpg");
  }

  private void cargarPerfil() {
    String token = ApiClient.leerToken(requireContext());
    if (token == null) {
      Toast.makeText(requireContext(), "Iniciá sesión para ver tu perfil", Toast.LENGTH_SHORT).show();
      return;
    }
    ApiClient.getBarberiaServicio().getPerfil("Bearer " + token).enqueue(new Callback<PerfilResponse>() {
      @Override
      public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          Usuario usuario = response.body().getUsuario();
          if (usuario != null) {
            etNombre.setText(usuario.getNombre());
            etEmail.setText(usuario.getEmail());
            etPhone.setText(usuario.getTelefono());
            String avatarUrl = usuario.getAvatarUrl();
            Log.d(TAG, "perfil cargado avatarUrl=" + avatarUrl);
            try {
              Glide.with(requireContext())
                  .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.profile)
                  .circleCrop()
                  .placeholder(R.drawable.profile)
                  .into(ivAvatar);
            } catch (Exception ignored) {
            }
            boolean hasAvatarReal = avatarUrl != null && !avatarUrl.isEmpty()
                && !avatarUrl.endsWith("/avatars/no_avatar.jpg");
            ivDeleteImage.setVisibility(hasAvatarReal ? View.VISIBLE : View.GONE);
            selectedImageUri = null;
            eliminarAvatarFlag = false;
            setEditable(false);
            editMode = false;
          }
        } else {
          Toast.makeText(requireContext(), "No se pudo obtener el perfil", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<PerfilResponse> call, Throwable t) {
        Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void actualizarPerfil() {
    String token = ApiClient.leerToken(requireContext());
    if (token == null) {
      Toast.makeText(requireContext(), "Iniciá sesión para actualizar", Toast.LENGTH_SHORT).show();
      return;
    }
    String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
    String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    String telefono = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
    String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

    if (password != null && !password.isEmpty() && password.length() < 6) {
      Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
      return;
    }

    Log.d(TAG, "Iniciando actualizarPerfil nombre=" + nombre + " email=" + email + " telefono=" + telefono +
        " eliminarAvatar=" + eliminarAvatarFlag + " selectedImageUri=" + selectedImageUri);

    RequestBody rbNombre = RequestBody.create(MediaType.parse("text/plain"), nombre != null ? nombre : "");
    RequestBody rbEmail = RequestBody.create(MediaType.parse("text/plain"), email != null ? email : "");
    RequestBody rbTelefono = RequestBody.create(MediaType.parse("text/plain"), telefono != null ? telefono : "");
    RequestBody rbPassword = RequestBody.create(MediaType.parse("text/plain"), password != null ? password : "");
    RequestBody rbEliminarAvatar = RequestBody.create(MediaType.parse("text/plain"),
        eliminarAvatarFlag ? "true" : "false");

    MultipartBody.Part avatarPart = null;

    // Si el usuario solicitó eliminar avatar, ignoramos file (backend exige eso).
    if (!eliminarAvatarFlag && selectedImageUri != null) {
      try {
        Uri uri = selectedImageUri;
        InputStream is = requireContext().getContentResolver().openInputStream(uri);
        if (is == null) {
          Toast.makeText(requireContext(), "No se pudo abrir el archivo seleccionado", Toast.LENGTH_SHORT).show();
          Log.e(TAG, "InputStream null para uri=" + uri);
          return;
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

        String mime = requireContext().getContentResolver().getType(uri);
        Log.d(TAG, "Selected image mime=" + mime + " originalBytes=" + originalBytes.length);

        if (originalBytes.length == 0) {
          Toast.makeText(requireContext(), "El archivo seleccionado está vacío", Toast.LENGTH_SHORT).show();
          return;
        }

        Bitmap original = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.length);
        if (original == null) {
          Toast.makeText(requireContext(), "No se pudo procesar la imagen seleccionada", Toast.LENGTH_SHORT).show();
          Log.e(TAG, "Bitmap decode failed for uri=" + uri);
          return;
        }

        // Rotar según EXIF si aplica
        try {
          ExifInterface exif = new ExifInterface(new ByteArrayInputStream(originalBytes));
          int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
          Log.d(TAG, "EXIF orientation=" + orientation);
          original = rotateBitmap(original, orientation);
        } catch (Exception exifEx) {
          Log.w(TAG, "No se pudo leer EXIF: " + exifEx.getMessage());
        }

        // CENTER-CROP a 240x240 para evitar deformación (preserva aspect ratio y
        // recorta centrado)
        Bitmap resized = centerCropBitmap(original, 240, 240);

        // Comprimir a JPEG (calidad 80)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();
        baos.close();

        Log.d(TAG, "Compressed avatar bytes=" + data.length);

        if (data.length > MAX_AVATAR_BYTES) {
          Toast.makeText(requireContext(), "El archivo supera el límite permitido (2 MB).", Toast.LENGTH_LONG).show();
          return;
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), data);
        avatarPart = MultipartBody.Part.createFormData("file", "avatar.jpg", reqFile);

      } catch (IOException ex) {
        Log.e(TAG, "Error procesando imagen: " + ex.getMessage(), ex);
        Toast.makeText(requireContext(), "Error procesando la imagen seleccionada", Toast.LENGTH_SHORT).show();
        return;
      } catch (Exception ex) {
        Log.e(TAG, "Error inesperado procesando imagen: " + ex.getMessage(), ex);
        Toast.makeText(requireContext(), "Error procesando la imagen", Toast.LENGTH_SHORT).show();
        return;
      }
    }

    // LOG previo al envío: listar qué partes vamos a enviar
    Log.d(TAG, "Lanzando request actualizarPerfil con partes: Nombre(" + (nombre.isEmpty() ? "empty" : "ok") +
        "), Email(" + (email.isEmpty() ? "empty" : "ok") + "), Telefono(" + (telefono.isEmpty() ? "empty" : "ok") +
        "), Password(" + (password.isEmpty() ? "empty" : "ok") + "), EliminarAvatar=" + eliminarAvatarFlag +
        ", filePartPresent=" + (avatarPart != null));

    // Llamada a la API
    btEditarGuardar.setEnabled(false);
    ApiClient.getBarberiaServicio()
        .actualizarPerfil("Bearer " + token, rbNombre, rbEmail, rbTelefono, rbPassword, rbEliminarAvatar, avatarPart)
        .enqueue(new Callback<PerfilResponse>() {
          @Override
          public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
            btEditarGuardar.setEnabled(true);
            Log.d(TAG, "actualizarPerfil onResponse code=" + response.code());
            if (response.isSuccessful() && response.body() != null) {
              Log.d(TAG, "actualizarPerfil success body=" + response.body());
              Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
              setEditable(false);
              editMode = false;
              btEditarGuardar.setText("Editar");
              eliminarAvatarFlag = false;
              selectedImageUri = null;
              if (response.body().getUsuario() != null) {
                ApiClient.guardarUsuario(requireContext(), response.body().getUsuario());
                cargarPerfil();
              }
            } else {
              String err = "Error al actualizar perfil";
              String raw = null;
              try {
                if (response.errorBody() != null)
                  raw = response.errorBody().string();
              } catch (Exception ignored) {
              }
              Log.w(TAG, "actualizarPerfil failed code=" + response.code() + " rawError=" + raw);
              if (response.code() == 413) {
                err = "El archivo supera el límite permitido (2 MB).";
              } else if (response.code() == 415) {
                err = "Tipo de archivo no soportado. Se permiten: image/jpeg, image/png.";
              } else if (response.code() == 500) {
                // Mostrar mensaje más informativo y pedir revisar logs backend
                err = "Error interno del servidor al procesar la imagen. Se ha enviado info de depuración (revisar logs).";
                // También logueamos el body completo para diagnóstico
                Log.e(TAG, "Servidor devolvió 500 con body: " + raw);
              } else if (raw != null && !raw.isEmpty()) {
                err = raw;
              }
              Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
            }
          }

          @Override
          public void onFailure(Call<PerfilResponse> call, Throwable t) {
            btEditarGuardar.setEnabled(true);
            Log.e(TAG, "actualizarPerfil onFailure: " + t.getMessage(), t);
            Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  // helper: center-crop preservando aspect ratio y recorta centrado a targetWidth
  // x targetHeight
  private Bitmap centerCropBitmap(Bitmap src, int targetW, int targetH) {
    if (src == null)
      return null;
    int srcW = src.getWidth();
    int srcH = src.getHeight();
    float scale = Math.max((float) targetW / srcW, (float) targetH / srcH);
    int scaledW = Math.round(scale * srcW);
    int scaledH = Math.round(scale * srcH);
    Bitmap scaled = Bitmap.createScaledBitmap(src, scaledW, scaledH, true);
    int x = Math.max(0, (scaledW - targetW) / 2);
    int y = Math.max(0, (scaledH - targetH) / 2);
    Bitmap cropped = Bitmap.createBitmap(scaled, x, y, targetW, targetH);
    if (scaled != cropped && !scaled.isRecycled()) {
      try {
        scaled.recycle();
      } catch (Exception ignored) {
      }
    }
    if (src != cropped && !src.isRecycled()) {
      try {
        src.recycle();
      } catch (Exception ignored) {
      }
    }
    return cropped;
  }

  // Helper: rota el bitmap según orientation EXIF; recicla el original si crea
  // uno nuevo
  private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
    if (orientation == ExifInterface.ORIENTATION_UNDEFINED || orientation == ExifInterface.ORIENTATION_NORMAL) {
      return bitmap;
    }
    Matrix matrix = new Matrix();
    switch (orientation) {
      case ExifInterface.ORIENTATION_ROTATE_90:
        matrix.postRotate(90);
        break;
      case ExifInterface.ORIENTATION_ROTATE_180:
        matrix.postRotate(180);
        break;
      case ExifInterface.ORIENTATION_ROTATE_270:
        matrix.postRotate(270);
        break;
      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
        matrix.postScale(-1, 1);
        break;
      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        matrix.postScale(1, -1);
        break;
      default:
        return bitmap;
    }
    try {
      Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
      if (rotated != bitmap) {
        try {
          bitmap.recycle();
        } catch (Exception ignored) {
        }
      }
      return rotated;
    } catch (OutOfMemoryError oom) {
      Log.e(TAG, "OOM rotando bitmap: " + oom.getMessage());
      return bitmap;
    }
  }

  // Nuevo método: borra avatar en servidor enviando EliminarAvatar=true (sin
  // archivo)
  private void deleteAvatarImmediate() {
    String token = ApiClient.leerToken(requireContext());
    if (token == null || token.isEmpty()) {
      Toast.makeText(requireContext(), "Debés iniciar sesión para eliminar el avatar", Toast.LENGTH_SHORT).show();
      return;
    }

    // Obtener valores actuales del formulario
    String nombreForm = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
    String emailForm = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    String telefonoForm = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

    // Si alguno está vacío, intentar rellenar desde el usuario guardado localmente
    com.eal.appturnosbarberia.models.Usuario stored = ApiClient.leerUsuario(requireContext());
    if ((nombreForm == null || nombreForm.isEmpty()) && stored != null && stored.getNombre() != null) {
      nombreForm = stored.getNombre();
    }
    if ((emailForm == null || emailForm.isEmpty()) && stored != null && stored.getEmail() != null) {
      emailForm = stored.getEmail();
    }
    if ((telefonoForm == null || telefonoForm.isEmpty()) && stored != null && stored.getTelefono() != null) {
      telefonoForm = stored.getTelefono();
    }

    // Email no puede quedar vacío: abortar si no hay email
    if (emailForm == null || emailForm.isEmpty()) {
      Toast.makeText(requireContext(),
          "No se puede eliminar el avatar: falta información del usuario. Iniciá sesión nuevamente.", Toast.LENGTH_LONG)
          .show();
      return;
    }

    // Construir RequestBody con valores válidos (no vacíos si es posible)
    RequestBody rbNombre = RequestBody.create(okhttp3.MediaType.parse("text/plain"), nombreForm);
    RequestBody rbEmail = RequestBody.create(okhttp3.MediaType.parse("text/plain"), emailForm);
    RequestBody rbTelefono = RequestBody.create(okhttp3.MediaType.parse("text/plain"), telefonoForm);
    RequestBody rbPassword = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
    RequestBody rbEliminarAvatar = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "true");

    // Deshabilitar UI pertinente mientras se procesa
    btEditarGuardar.setEnabled(false);
    ivDeleteImage.setEnabled(false);
    ivDeleteImage.setClickable(false);

    Log.d(TAG, "deleteAvatarImmediate: enviando EliminarAvatar=true al backend (nombre/email/telefono incluidos)");
    ApiClient.getBarberiaServicio()
        .actualizarPerfil("Bearer " + token, rbNombre, rbEmail, rbTelefono, rbPassword, rbEliminarAvatar, null)
        .enqueue(new retrofit2.Callback<com.eal.appturnosbarberia.models.PerfilResponse>() {
          @Override
          public void onResponse(retrofit2.Call<com.eal.appturnosbarberia.models.PerfilResponse> call,
              retrofit2.Response<com.eal.appturnosbarberia.models.PerfilResponse> response) {
            // Re-habilitar UI
            btEditarGuardar.setEnabled(true);
            ivDeleteImage.setEnabled(true);
            ivDeleteImage.setClickable(true);

            if (response.isSuccessful() && response.body() != null) {
              Toast.makeText(requireContext(), "Avatar eliminado correctamente", Toast.LENGTH_SHORT).show();
              if (response.body().getUsuario() != null) {
                ApiClient.guardarUsuario(requireContext(), response.body().getUsuario());
              }
              cargarPerfil();
            } else {
              String err = "Error al eliminar avatar";
              String raw = null;
              try {
                if (response.errorBody() != null)
                  raw = response.errorBody().string();
              } catch (Exception ignored) {
              }
              Log.w(TAG, "deleteAvatarImmediate failed code=" + response.code() + " body=" + raw);
              // Mostrar mensaje del servidor si viene, sino mensaje genérico
              if (raw != null && !raw.isEmpty()) {
                Toast.makeText(requireContext(), raw, Toast.LENGTH_LONG).show();
              } else {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
              }
            }
          }

          @Override
          public void onFailure(retrofit2.Call<com.eal.appturnosbarberia.models.PerfilResponse> call, Throwable t) {
            btEditarGuardar.setEnabled(true);
            ivDeleteImage.setEnabled(true);
            ivDeleteImage.setClickable(true);
            Log.e(TAG, "deleteAvatarImmediate onFailure: " + t.getMessage(), t);
            Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }
}