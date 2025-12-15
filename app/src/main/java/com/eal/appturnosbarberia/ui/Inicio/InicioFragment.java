package com.eal.appturnosbarberia.ui.Inicio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.Navigation;

import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.databinding.FragmentInicioBinding;
import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.models.DashboardClienteResponse;
import com.eal.appturnosbarberia.request.ApiClient;

import java.util.List;

public class InicioFragment extends Fragment {

  // Variable para manejar el binding con la UI (fragment_inicio.xml)
  private FragmentInicioBinding binding;

  // ViewModels
  private InicioViewModel viewModel;
  private MainActivityViewModel mainViewModel;

  // Adaptador
  private ServiciosAdapter serviciosAdapter;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // Inflamos la vista usando View Binding (conecta el layout con el código)
    binding = FragmentInicioBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Crear o recuperar los ViewModels asociados a este fragmento
    viewModel = new ViewModelProvider(this).get(InicioViewModel.class);
    mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

    // Configurar RecyclerView
    serviciosAdapter = new ServiciosAdapter();
    binding.rvServicios
        .setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    binding.rvServicios.setAdapter(serviciosAdapter);

    // Configurar observadores
    configurarObservadores();

    // Obtener usuario y token para inicializar ViewModel
    mainViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
      String token = ApiClient.leerToken(requireContext());
      viewModel.inicializar(usuario, token);
    });
  }

  private void configurarObservadores() {
    // Saludo (DINÁMICO)
    viewModel.obtenerTextoSaludo().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String texto) {
        binding.tvWelcomeInicio.setText(texto);
      }
    });

    // Visibilidades (CONDICIONALES)
    viewModel.obtenerVisibilidadTarjetaProximoTurno().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer visibilidad) {
        binding.cardProximoTurno.setVisibility(visibilidad);
      }
    });

    viewModel.obtenerVisibilidadTarjetaSinTurno().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer visibilidad) {
        binding.cardSinTurno.setVisibility(visibilidad);
      }
    });

    // Aqui lo q hacemos es observar los LiveData del ViewModel y actualizar la UI en consecuencia
    viewModel.obtenerTituloProximoTurno().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String titulo) {
        binding.tvNextTurnoTitle.setText(titulo);
      }
    });

    viewModel.obtenerEstadoProximoTurno().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String estado) {
        binding.chipEstado.setText(estado);
      }
    });

    viewModel.obtenerBarberoProximoTurno().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String barbero) {
        binding.tvNextTurnoBarbero.setText(barbero);
      }
    });

    // Aqui actualizamos los datos de la barbería
    viewModel.obtenerNombreBarberia().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String nombre) {
        binding.tvBarberiaNombre.setText(nombre);
      }
    });

    viewModel.obtenerDireccionBarberia().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String direccion) {
        binding.tvBarberiaDireccion.setText(direccion);
      }
    });

    viewModel.obtenerUrlMaps().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String urlMaps) {
        binding.btComoLlegar.setOnClickListener(v -> {
          Intent intento = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMaps));
          startActivity(intento);
        });
      }
    });

    // Aqui actualizamos la lista de servicios en el RecyclerView
    viewModel.obtenerServicios().observe(getViewLifecycleOwner(),
        new Observer<List<DashboardClienteResponse.Servicio>>() {
          @Override
          public void onChanged(List<DashboardClienteResponse.Servicio> listaServicios) {
            serviciosAdapter.establecerServicios(listaServicios);
          }
        });

    // Aqui manejamos los errores
    viewModel.obtenerError().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String error) {
        // La capa de lógica debe garantizar que `error` no sea nulo/empty.
        binding.tvWelcomeInicio.setText("Error");
        binding.tvNextTurnoTitle.setText(error);
      }
    });

  }
}