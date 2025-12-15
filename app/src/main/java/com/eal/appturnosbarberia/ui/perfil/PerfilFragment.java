package com.eal.appturnosbarberia.ui.perfil;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

  private FragmentPerfilBinding binding;
  private PerfilViewModel viewModel;
  private ActivityResultLauncher<String> pickImageLauncher;

  // Inflar el layout del fragment
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentPerfilBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  // Configurar ViewModel, listeners y observers
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

    // Registrar launcher para seleccionar imagen
    pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
          @Override
          public void onActivityResult(Uri uri) {
            viewModel.onImageSelected(uri);
          }
        });

    setupListeners(); // Configurar listeners de UI
    setupObservers(); // Configurar observers de LiveData

    // Cargar datos iniciales
    viewModel.cargarPerfil();
  }

  private void setupListeners() {
    binding.ivAddImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) { // click en agregar imagen
        viewModel.onAddImageClicked();
      }
    });

    binding.ivDeleteImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) { // click en eliminar imagen
        viewModel.onDeleteImageClicked();
      }
    });

    binding.btEditarGuardar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) { // click en editar/guardar
        viewModel.onBotonAccionClicked(
            binding.etNombre.getText().toString().trim(),
            binding.etEmail.getText().toString().trim(),
            binding.etPhone.getText().toString().trim(),
            binding.etPassword.getText().toString()
        );
      }
    });
  }

  // Configurar observers para LiveData del ViewModel
  private void setupObservers() {
    // Datos del usuario
    viewModel.getNombre().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        // Solo actualizamos si el texto es diferente para no interrumpir la escritura si fuera two-way
        if (!binding.etNombre.getText().toString().equals(s)) {
          binding.etNombre.setText(s);
        }
      }
    });

    // Email (siempre deshabilitado)
    viewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        binding.etEmail.setText(s);
      }
    });

    // Teléfono
    viewModel.getTelefono().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (!binding.etPhone.getText().toString().equals(s)) {
          binding.etPhone.setText(s);
        }
      }
    });

    // Contraseña
    viewModel.getAvatarUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String url) {
        try {
          Glide.with(requireContext())
              .load(url != null && !url.isEmpty() ? url : R.drawable.profile)
              .circleCrop()
              .placeholder(R.drawable.profile)
              .error(R.drawable.profile)
              .into(binding.ivAvatarPerfil);
        } catch (Exception ignored) {}
      }
    });

    // Estado de la UI
    viewModel.getIsEditable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean editable) {
        boolean isEdit = Boolean.TRUE.equals(editable);
        binding.etNombre.setEnabled(isEdit);
        binding.etPhone.setEnabled(isEdit);
        binding.etPassword.setEnabled(isEdit);
        
        float alpha = isEdit ? 1.0f : 0.65f;
        binding.etNombre.setAlpha(alpha);
        binding.etPhone.setAlpha(alpha);
        binding.etPassword.setAlpha(alpha);
        
        // Email siempre deshabilitado
        binding.etEmail.setEnabled(false);
        binding.etEmail.setAlpha(0.65f);
      }
    });

    viewModel.getButtonText().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String text) {
        binding.btEditarGuardar.setText(text);
      }
    });

    // Cargando
    viewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean loading) {
        boolean isLoading = Boolean.TRUE.equals(loading);
        binding.btEditarGuardar.setEnabled(!isLoading);
        binding.ivAddImage.setEnabled(!isLoading);
        binding.ivDeleteImage.setEnabled(!isLoading);
      }
    });

    // Visibilidad icono eliminar imagen
    viewModel.getDeleteIconVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer visibility) {
        binding.ivDeleteImage.setVisibility(visibility);
      }
    });

    // Eventos
    viewModel.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String msg) {
        if (msg != null) {
          Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
      }
    });

    // Evento para seleccionar imagen
    viewModel.getPickImageEvent().observe(getViewLifecycleOwner(), new Observer<Void>() {
      @Override
      public void onChanged(Void unused) {
        pickImageLauncher.launch("image/*");
      }
    });

    // Evento para mostrar diálogo de confirmación al eliminar imagen
    viewModel.getShowDeleteDialogEvent().observe(getViewLifecycleOwner(), new Observer<Void>() {
      @Override
      public void onChanged(Void unused) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Eliminar avatar")
            .setMessage("¿Estás seguro que querés eliminar tu avatar?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar", (d, i) -> viewModel.confirmarEliminarAvatar())
            .show();
      }
    });
  }

  // Limpiar binding al destruir la vista
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}