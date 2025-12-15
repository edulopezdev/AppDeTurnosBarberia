package com.eal.appturnosbarberia.ui.resetearContrasena;

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

import com.eal.appturnosbarberia.databinding.FragmentResetearContrasenaBinding;

public class ResetearContrasenaFragment extends Fragment {

  private ResetearContrasenaViewModel vm; // ViewModel
  private FragmentResetearContrasenaBinding binding; // View Binding

  // onCreateView: Infla el layout usando View Binding
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentResetearContrasenaBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  // onViewCreated: Configura ViewModel, listeners y observadores
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    vm = new ViewModelProvider(this).get(ResetearContrasenaViewModel.class);

    // aca lo q hacemos es setear los listeners con clases anonimas
    binding.btEnviarCodigo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        vm.solicitarCodigo(binding.etEmailReset.getText());
      }
    });

    // aca lo q hacemos es setear los listeners con clases anonimas
    binding.btResetear.setOnClickListener(new View.OnClickListener() { // Clase Anónima
      @Override
      public void onClick(View v) {
        vm.resetearContrasena(
            binding.etCodigo.getText(),
            binding.etNewPass.getText(),
            binding.etConfirmPass.getText());
      }
    });

    // Observadores con Clases Anónimas
    vm.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean loading) {
        // Sin chequeo de nulos explícito (unboxing)
        binding.btEnviarCodigo.setEnabled(!loading);
        binding.btResetear.setEnabled(!loading);
      }
    });

    vm.getError().observe(getViewLifecycleOwner(), binding.tvResetError::setText);
    vm.getErrorVisibility().observe(getViewLifecycleOwner(), binding.tvResetError::setVisibility);

    vm.getInfo().observe(getViewLifecycleOwner(), binding.tvResetInfo::setText);
    vm.getInfoVisibility().observe(getViewLifecycleOwner(), binding.tvResetInfo::setVisibility);

    vm.getCodigoVisibility().observe(getViewLifecycleOwner(), binding.tilCodigo::setVisibility);
    vm.getNewPassVisibility().observe(getViewLifecycleOwner(), binding.tilNewPass::setVisibility);
    vm.getConfirmPassVisibility().observe(getViewLifecycleOwner(), binding.tilConfirmPass::setVisibility);

    vm.getBtResetearVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer visibility) {
        binding.btResetear.setVisibility(visibility);
      }
    });

    vm.getNavigationEvent().observe(getViewLifecycleOwner(),
        new Observer<ResetearContrasenaViewModel.NavigationEvent>() {
          @Override
          public void onChanged(ResetearContrasenaViewModel.NavigationEvent event) {
            if (event == null)
              return;

            Intent i = new Intent(requireActivity(), event.destination);
            i.putExtras(event.extras);
            i.addFlags(event.flags);
            startActivity(i);
            requireActivity().finish();
            vm.doneNavigating();
          }
        });

    vm.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String msg) {
        if (msg == null)
          return;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
      }
    });

    setHasOptionsMenu(false);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}