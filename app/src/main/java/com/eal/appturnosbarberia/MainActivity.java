package com.eal.appturnosbarberia;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.eal.appturnosbarberia.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_appointments, R.id.nav_profile, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Inicializar el ViewModel
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Configurar el header del Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombreUsuario = headerView.findViewById(R.id.tvNombreUsuario);
        TextView tvEmailUsuario = headerView.findViewById(R.id.tvEmailUsuario);
        ImageView ivAvatar = headerView.findViewById(R.id.imageView);

        // Observar los datos del usuario y actualizar el sidebar
        mViewModel.getUsuarioData().observe(this, usuario -> {
            if (usuario != null) {
                Log.d("DatosUsuario", "MainActivity observa usuario: " + usuario.getNombre() + " - " + usuario.getEmail());
                String nombreCompleto = usuario.getNombre();
                tvNombreUsuario.setText(nombreCompleto);
                tvEmailUsuario.setText(usuario.getEmail());
            } else {
                Log.d("DatosUsuario", "MainActivity observa usuario: null");
            }
        });

        // Solicitar los datos del usuario
        Log.d("MainActivity", "Solicitando datos del usuario...");
        mViewModel.obtenerDatosUsuario();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}