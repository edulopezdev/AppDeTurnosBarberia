package com.eal.appturnosbarberia.ui.seleccionarBarbero;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.eal.appturnosbarberia.R;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import com.eal.appturnosbarberia.MainActivityViewModel;
import androidx.navigation.Navigation;
import com.eal.appturnosbarberia.databinding.FragmentSeleccionarBarberoBinding;

public class SeleccionarBarberoFragment extends Fragment {

  private FragmentSeleccionarBarberoBinding binding;
  // inicializamos los ViewModels
  private SeleccionarBarberoViewModel mViewModel;
  private MainActivityViewModel mainViewModel;

  // este metodo crea una nueva instancia del fragment
  public static SeleccionarBarberoFragment newInstance() {
    return new SeleccionarBarberoFragment();
  }

  // aca vamos a inflar el layout del fragment
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentSeleccionarBarberoBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  // este metodo se llama despues de que la vista ha sido creada
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Inicializar ViewModels
    mViewModel = new ViewModelProvider(this).get(SeleccionarBarberoViewModel.class);
    mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

    // Observar usuario
    mainViewModel.getUsuario().observe(getViewLifecycleOwner(), mViewModel::setUsuario);

    // Configurar adapter
    BarberosAdapter adapter = new BarberosAdapter(mViewModel::onBarberoClicked);
    binding.rvBarberos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    binding.rvBarberos.setAdapter(adapter);

    // Listeners con Clases Anónimas
    binding.swipeRefreshSeleccionar.setOnRefreshListener(mViewModel::loadBarberos);

    binding.btElegirBarbero.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mViewModel.confirmarSeleccion();
      }
    });

    binding.ivClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mViewModel.onCloseClicked();
      }
    });

    // Observadores
    mViewModel.getBarberos().observe(getViewLifecycleOwner(), adapter::setItems);
    mViewModel.getRvVisibility().observe(getViewLifecycleOwner(), binding.rvBarberos::setVisibility);
    mViewModel.getTvNoBarberosVisibility().observe(getViewLifecycleOwner(), binding.tvNoBarberos::setVisibility);
    mViewModel.getLoading().observe(getViewLifecycleOwner(), binding.swipeRefreshSeleccionar::setRefreshing);
    mViewModel.getGreeting().observe(getViewLifecycleOwner(), binding.tvGreeting::setText);

    mViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
      }
    });

    mViewModel.getCloseEvent().observe(getViewLifecycleOwner(), new Observer<Void>() {
      @Override
      public void onChanged(Void unused) {
        Navigation.findNavController(requireView()).popBackStack();
      }
    });

    mViewModel.getGoToSeleccionarTurno().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean shouldNavigate) {
        if (Boolean.TRUE.equals(shouldNavigate)) {
          Navigation.findNavController(requireView())
              .navigate(
                  R.id.seleccionarTurnoFragment,
                  mViewModel.getSeleccionArgs());
          mViewModel.doneNavigating();
        }
      }
    });

    // Finalmente, llamamos al metodo onStart del ViewModel para cargar los datos
    // iniciales
    mViewModel.onStart();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }
}