package com.eal.appturnosbarberia.ui.verificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.eal.appturnosbarberia.databinding.FragmentVerificacionBinding;

public class VerificacionFragment extends Fragment {

  private FragmentVerificacionBinding binding;
  private VerificacionViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentVerificacionBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(VerificacionViewModel.class);

    // Inicializar ViewModel con argumentos
    viewModel.init(getArguments(), requireActivity().getIntent().getExtras());

    // Listeners con Clases Anónimas
    binding.btVerify.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String code = binding.etCode.getText().toString().trim();
        String emailInput = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : null;
        viewModel.verificarCodigo(code, emailInput); // click del botón Verificar
      }
    });

    binding.btResend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String emailInput = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : null;
        viewModel.reenviarCodigo(emailInput); // click del botón Reenviar Código
      }
    });

    binding.ivBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        viewModel.onBackClicked(); // click del ícono de volver
      }
    });

    // Observers con Clases Anónimas
    viewModel.getEmailText().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String email) {
        binding.etEmail.setText(email); // actualizar el campo de email si cambia en el ViewModel
      }
    });

    viewModel.getError().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String err) {
        binding.tvVerifError.setText(err); // actualizar el mensaje de error si cambia en el ViewModel
      }
    });

    viewModel.getErrorVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer visibility) {
        binding.tvVerifError.setVisibility(visibility); // actualizar la visibilidad del mensaje de error
      }
    });

    viewModel.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String msg) {
        if (msg == null)
          return;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
      }
    });

    viewModel.getNavigationEvent().observe(getViewLifecycleOwner(),
        new Observer<VerificacionViewModel.NavigationEvent>() {
          @Override
          public void onChanged(VerificacionViewModel.NavigationEvent event) {
            if (event == null)
              return;

            Intent i = new Intent(requireActivity(), event.destination);
            i.putExtras(event.extras);
            i.addFlags(event.flags);
            startActivity(i);
            requireActivity().finish();
            viewModel.doneNavigating();
          }
        });

    viewModel.getBackEvent().observe(getViewLifecycleOwner(), new Observer<Void>() {
      @Override
      public void onChanged(Void unused) {
        requireActivity().onBackPressed();
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
