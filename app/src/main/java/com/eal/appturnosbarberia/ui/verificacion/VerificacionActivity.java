package com.eal.appturnosbarberia.ui.verificacion;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.eal.appturnosbarberia.databinding.ActivityVerificacionBinding;
import com.eal.appturnosbarberia.ui.login.LoginActivity;
import com.eal.appturnosbarberia.ui.resetearContrasena.ResetearContrasenaFragment;

public class VerificacionActivity extends AppCompatActivity {

  private ActivityVerificacionBinding binding;
  private VerificacionActivityViewModel viewModel;

  // este metodo crea la actividad y observa el estado de navegación del ViewModel
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityVerificacionBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    viewModel = new ViewModelProvider(this).get(VerificacionActivityViewModel.class);

    if (savedInstanceState == null) {
      viewModel.procesarIntent(getIntent().getExtras());
    }

    viewModel.getNavigationState().observe(this, new Observer<VerificacionActivityViewModel.NavigationState>() {
      @Override
      public void onChanged(VerificacionActivityViewModel.NavigationState state) {
        // Evitar recargar el fragmento si ya existe (ej. rotación)
        if (state != null && getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
          cargarFragmento(state);
        }
      }
    });
  }

  // este metodo carga el fragmento correspondiente según el estado de navegación
  private void cargarFragmento(VerificacionActivityViewModel.NavigationState state) {
    // Usamos FragmentFactory para instanciar sin try-catch explícito
    Fragment fragmentToLoad = getSupportFragmentManager().getFragmentFactory()
        .instantiate(getClassLoader(), state.fragmentClass.getName());

    fragmentToLoad.setArguments(state.args);

    getSupportFragmentManager().beginTransaction()
        .replace(android.R.id.content, fragmentToLoad)
        .commit();
  }

  // este metodo maneja la acción de retroceso para volver a LoginActivity
  @Override
  public void onBackPressed() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
  }
}
