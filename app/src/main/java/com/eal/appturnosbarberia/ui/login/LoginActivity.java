package com.eal.appturnosbarberia.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eal.appturnosbarberia.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

  private LoginViewModel viewModel;
  private ActivityLoginBinding binding;

  // Configuración inicial de la Activity
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Configurar View Binding
    binding = ActivityLoginBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Configurar ViewModel
    viewModel = ViewModelProvider.AndroidViewModelFactory
        .getInstance(getApplication())
        .create(LoginViewModel.class);

    // Aplicar mensaje y visibilidad provistos por el VM (sin lógica en la Activity)
    viewModel.obtenerMensajeError().observe(this, binding.tvError::setText);
    viewModel.obtenerMensajeErrorVisibility().observe(this, binding.tvError::setVisibility);

    final android.os.Bundle[] pendingExtras = new android.os.Bundle[1];
    viewModel.obtenerNavegarExtras().observe(this, bundle -> pendingExtras[0] = bundle);

    viewModel.obtenerNavegarARegistro().observe(this, destino -> {
      Intent intent = new Intent(LoginActivity.this, destino);
      android.os.Bundle extras = pendingExtras[0];
      intent.putExtras(extras);
      intent.addFlags(extras.getInt("flags", 0));
      pendingExtras[0] = null;
      startActivity(intent);
    });

    // Configurar listeners de botones
    binding.btLogin.setOnClickListener(v -> { //click iniciar sesion
      viewModel.iniciarSesion(binding.etEmail.getText(), binding.etPassword.getText());
    });

    // Configurar listeners de botones
    binding.btCrearCuenta.setOnClickListener(v -> viewModel.alClickCrearCuenta()); //click crear cuenta
    binding.tvForgotPassword.setOnClickListener(v -> viewModel.alClickRecuperarContrasena(binding.etEmail.getText())); //click recuperar contraseña
  }
}