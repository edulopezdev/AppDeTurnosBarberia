package com.eal.appturnosbarberia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.eal.appturnosbarberia.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  private AppBarConfiguration appBarConfiguration;
  private MainActivityViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Binding
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // ViewModel
    viewModel = new ViewModelProvider(this)
        .get(MainActivityViewModel.class);

    // Toolbar
    setSupportActionBar(binding.appBarMain.toolbar);

    // Navigation
    appBarConfiguration = new AppBarConfiguration.Builder(
        R.id.nav_home,
        R.id.nav_appointments,
        R.id.nav_profile,
        R.id.nav_logout).setOpenableLayout(binding.drawerLayout)
        .build();

    NavController navController = Navigation.findNavController(
        this,
        R.id.nav_host_fragment_content_main);

    NavigationUI.setupActionBarWithNavController(
        this,
        navController,
        appBarConfiguration);

    NavigationUI.setupWithNavController(
        binding.navView,
        navController);

    // Menú
    binding.navView.setNavigationItemSelectedListener(item -> {
      binding.drawerLayout.closeDrawers();
      viewModel.manejarSeleccionMenu(item.getItemId());
      return false;
    });

    // Header
    NavigationView navView = binding.navView;
    var header = navView.getHeaderView(0);

    TextView tvNombre = header.findViewById(R.id.tvNombreUsuario);
    TextView tvEmail = header.findViewById(R.id.tvEmailUsuario);
    ImageView ivAvatarHeader = header.findViewById(R.id.ivAvatarHeader);

    // Observers
    viewModel.getNombreUsuario().observe(this, texto -> {
      tvNombre.setText(texto);
    });

    viewModel.getEmailUsuario().observe(this, texto -> {
      tvEmail.setText(texto);
    });

    viewModel.getAvatarUrl().observe(this, avatarUrl -> {
      if (avatarUrl != null && !avatarUrl.isEmpty()) {
        Glide.with(this)
            .load(avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.profile)
            .into(ivAvatarHeader);
      }
    });

    viewModel.getEventoResetMenu().observe(this, evento -> {
      binding.navView.setCheckedItem(R.id.nav_home);
    });

    viewModel.getEventoNavegar().observe(this, destinoId -> {
      NavigationUI.onNavDestinationSelected(binding.navView.getMenu().findItem(destinoId), navController);
    });

    viewModel.getSolicitudDialogo().observe(this, dialogo -> {
      new AlertDialog.Builder(this)
          .setTitle(dialogo.getTitle())
          .setMessage(dialogo.getMessage())
          .setPositiveButton(dialogo.getPositiveLabel(), (d, w) -> {
            d.dismiss();
            viewModel.cerrarSesion();
          })
          .setNegativeButton(dialogo.getNegativeLabel(), (d, w) -> {
            d.dismiss();
            viewModel.cancelarCerrarSesion();
          })
          .setOnDismissListener(d -> {
            viewModel.limpiarSolicitudDialogo();
            viewModel.cancelarCerrarSesion(); // Asegurar que se deseleccione el menú
          })
          .show();
    });

    viewModel.getEventoNavegarLogin()
        .observe(this, destino -> {
          Intent i = new Intent(this, destino);
          i.setFlags(
              Intent.FLAG_ACTIVITY_NEW_TASK |
                  Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(i);
        });

    // Datos
    viewModel.obtenerDatosUsuario();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(
        this,
        R.id.nav_host_fragment_content_main);
    return NavigationUI.navigateUp(
        navController,
        appBarConfiguration);
  }
}
