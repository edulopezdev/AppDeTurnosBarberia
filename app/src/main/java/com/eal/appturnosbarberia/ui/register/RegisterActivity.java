package com.eal.appturnosbarberia.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eal.appturnosbarberia.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

  private RegisterViewModel viewModel;
  private ActivityRegisterBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityRegisterBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    viewModel = ViewModelProvider.AndroidViewModelFactory
        .getInstance(getApplication())
        .create(RegisterViewModel.class);

    // Mostrar toasts tal cual los emite el VM (sin lógica en la Activity)
    viewModel.getToastMessage().observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

    // Pending extras pattern (VM garantiza Bundle no-nulo)
    final android.os.Bundle[] pendingExtras = new android.os.Bundle[1];
    viewModel.getNavegarExtras().observe(this, bundle -> pendingExtras[0] = bundle);

    // Navegación: usar el destino y el bundle emitidos por el VM (sin ifs)
    viewModel.getNavegarEvento().observe(this, destino -> {
      Intent intent = new Intent(RegisterActivity.this, destino);
      android.os.Bundle extras = pendingExtras[0];
      intent.putExtras(extras);
      intent.addFlags(extras.getInt("flags", 0));
      startActivity(intent);
      finish();
    });

    // Delegar inputs crudos (CharSequence) al ViewModel; el VM valida/normaliza
    binding.btCreateAccount.setOnClickListener(v -> {
      viewModel.register(
          binding.etName.getText(),
          binding.etEmail.getText(),
          binding.etPhone.getText(),
          binding.etPassword.getText(),
          binding.etConfirmPassword.getText()
      );
    });

    binding.ivBack.setOnClickListener(v -> finish());
  }
}
