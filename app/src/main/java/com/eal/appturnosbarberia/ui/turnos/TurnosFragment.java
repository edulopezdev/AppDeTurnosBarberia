package com.eal.appturnosbarberia.ui.turnos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TurnosFragment extends Fragment {

  private TurnosViewModel viewModel;
  private MainActivityViewModel mainViewModel;

  private SwipeRefreshLayout swipe;
  private RecyclerView rvTurnos;
  private TextView tvNoTurnos;
  private FloatingActionButton fab;
  private TurnosAdapter adapter;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_turnos, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
    viewModel = new ViewModelProvider(this).get(TurnosViewModel.class);

    initViews(view);
    initAdapter();
    observeMainViewModel();
    observeViewModel();
    initListeners();
  }

  private void initViews(View v) {
    swipe = v.findViewById(R.id.swipeRefrescarTurnos);
    rvTurnos = v.findViewById(R.id.rvTurnos);
    tvNoTurnos = v.findViewById(R.id.tvNoTurnos);
    fab = v.findViewById(R.id.fabCrearTurno);
  }

  private void initAdapter() {
    adapter = new TurnosAdapter(viewModel.getCancelListener(mainViewModel));
    rvTurnos.setLayoutManager(new LinearLayoutManager(requireContext()));
    rvTurnos.setAdapter(adapter);
  }

  private void observeMainViewModel() {

    mainViewModel.getTurnos().observe(getViewLifecycleOwner(), turnos -> {
      viewModel.onTurnosUpdated(turnos);
      adapter.setItems(turnos); // Aseguramos que el adaptador se actualice
    });
  }

  private void observeViewModel() {

    viewModel.getTurnos().observe(getViewLifecycleOwner(), adapter::setItems);

    viewModel.getRecyclerVisibility().observe(getViewLifecycleOwner(),
        v -> rvTurnos.setVisibility(v));

    viewModel.getNoTurnosVisibility().observe(getViewLifecycleOwner(),
        v -> tvNoTurnos.setVisibility(v));

    viewModel.getFabVisibility().observe(getViewLifecycleOwner(),
        v -> fab.setVisibility(v));

    viewModel.getRefreshing().observe(getViewLifecycleOwner(),
        swipe::setRefreshing);

    viewModel.getToast().observe(getViewLifecycleOwner(),
        msg -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show());
  }

  private void initListeners() {

    swipe.setOnRefreshListener(() -> mainViewModel.refrescarTurnos());

    fab.setOnClickListener(v -> Navigation.findNavController(v)
        .navigate(R.id.seleccionarBarberoFragment));

    getParentFragmentManager().setFragmentResultListener(
        "BARBERO_SELECTED",
        getViewLifecycleOwner(),
        (k, b) -> viewModel.onBarberoSelected(b));
  }
}
