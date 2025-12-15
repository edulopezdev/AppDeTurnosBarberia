package com.eal.appturnosbarberia.ui.logout;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {

  private LogoutViewModel mViewModel; // ViewModel asociado

  // este metodo crea una nueva instancia del fragmento
  public static LogoutFragment newInstance() {
    return new LogoutFragment();
  }

  // infla el layout del fragmento
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_logout, container, false);
  }

  // una vez que la vista ha sido creada, se configura el ViewModel y se establecen los observadores
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
        .get(LogoutViewModel.class);

    // Observar mensajes para mostrar feedback
    mViewModel.obtenerMensaje().observe(getViewLifecycleOwner(), msg -> {
      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    });

    // estos son los extras pendientes para la navegación
    final Bundle[] pendingExtras = new Bundle[1];
    mViewModel.obtenerNavegarExtras().observe(getViewLifecycleOwner(), b -> pendingExtras[0] = b);

    // aca lo q hacemos es observar el evento de navegación
    mViewModel.obtenerNavegarEvento().observe(getViewLifecycleOwner(), destino -> {
      Intent intent = new Intent(requireContext(), destino); // crear intent de navegación
      Bundle extras = pendingExtras[0]; // obtener extras pendientes
      intent.putExtras(extras); // agregar extras al intent
      intent.addFlags(extras.getInt("flags", 0));
      pendingExtras[0] = new Bundle(); // limpiar local
      startActivity(intent); // iniciar actividad de destino
    });
  }

}