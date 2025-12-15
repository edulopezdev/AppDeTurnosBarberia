package com.eal.appturnosbarberia.ui.seleccionarTurno;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.eal.appturnosbarberia.R;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.databinding.FragmentSeleccionarTurnoBinding;

public class SeleccionarTurnoFragment extends Fragment {

  private FragmentSeleccionarTurnoBinding binding; // View Binding
  private HorariosAdapter horariosAdapter;// Adapter para la lista de horarios
  private DiasAdapter diasAdapter;// Adapter para la lista de días
  private SeleccionarTurnoViewModel mViewModel;// ViewModel del Fragment
  private MainActivityViewModel mainViewModel;// ViewModel de la Activity

  // este metodo crea una nueva instancia del fragment
  public static SeleccionarTurnoFragment newInstance() {
    return new SeleccionarTurnoFragment();
  }

  // este metodo infla el layout del fragment usando view binding
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentSeleccionarTurnoBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  // este metodo se llama despues de que la vista ha sido creada
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // ViewModels
    mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
    mViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
        .get(SeleccionarTurnoViewModel.class);

    mViewModel.setMainActivityViewModel(mainViewModel);

    // aca leemos los argumentos pasados al fragment
    mViewModel.leerArgumentos(getArguments());

    // configuramos los RecyclerViews para dias y horarios
    binding.rvDias.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    diasAdapter = new DiasAdapter(mViewModel::seleccionarDia);
    binding.rvDias.setAdapter(diasAdapter);

    binding.rvHorarios.setLayoutManager(new GridLayoutManager(requireContext(), 4));
    horariosAdapter = new HorariosAdapter(mViewModel::seleccionarHorario);
    binding.rvHorarios.setAdapter(horariosAdapter);

    // establecemos los listeners para los botones
    binding.ivPrevMonth.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mViewModel.mesAnterior(); // ir al mes anterior
      }
    });

    binding.ivNextMonth.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mViewModel.mesSiguiente(); // ir al mes siguiente
      }
    });

    binding.btReservar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mViewModel.intentarReservar(); // intentar reservar el turno
      }
    });

    // escuchamos los LiveData del ViewModel para actualizar la UI
    mViewModel.getNombreBarbero().observe(getViewLifecycleOwner(), binding.tvBarberoNameHeader::setText);

    mViewModel.getAvatarBarbero().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String url) {
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .circleCrop()
            .into(binding.ivBarberoAvatar);
      }
    });

    // actualizamos el texto del mes, la lista de dias y la lista de horarios
    mViewModel.getTextoMes().observe(getViewLifecycleOwner(), binding.tvCurrentMonth::setText);
    mViewModel.getListaDias().observe(getViewLifecycleOwner(), diasAdapter::setItems);
    mViewModel.getListaHorarios().observe(getViewLifecycleOwner(), horariosAdapter::updateData);

    // mostramos mensajes toast cuando sea necesario
    mViewModel.getMensajeToast().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
      }
    });

    // navegamos a la pantalla de citas si es necesario
    mViewModel.getGoToAppointments().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean shouldNavigate) {
        if (Boolean.TRUE.equals(shouldNavigate)) {
          Navigation.findNavController(requireView()).navigate(R.id.nav_appointments);
          mViewModel.doneNavigating();
        }
      }
    });
  }

  // este metodo se llama cuando la vista es destruida
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}