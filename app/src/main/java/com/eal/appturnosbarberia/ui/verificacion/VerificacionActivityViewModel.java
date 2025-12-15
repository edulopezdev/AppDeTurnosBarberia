package com.eal.appturnosbarberia.ui.verificacion;

import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eal.appturnosbarberia.ui.resetearContrasena.ResetearContrasenaFragment;

public class VerificacionActivityViewModel extends AndroidViewModel {

  private final MutableLiveData<NavigationState> navigationState = new MutableLiveData<>();

  public VerificacionActivityViewModel(@NonNull Application application) {
    super(application);
  }

  public LiveData<NavigationState> getNavigationState() {
    return navigationState;
  }

  public void procesarIntent(Bundle extras) {
    // Si ya tenemos un estado, no reprocesamos (útil si se llama múltiples veces
    // por error)
    if (navigationState.getValue() != null)
      return;

    String screen = null;
    String email = null;
    if (extras != null) {
      screen = extras.getString("screen");
      email = extras.getString("email");
    }

    // Lógica de decisión movida aquí
    Class<? extends Fragment> fragmentClass;
    if ("reset".equalsIgnoreCase(screen)) {
      fragmentClass = ResetearContrasenaFragment.class;
    } else {
      fragmentClass = VerificacionFragment.class;
    }

    // Preparación de argumentos movida aquí
    Bundle args = new Bundle();
    if (email != null && !email.isEmpty()) {
      args.putString("email", email);
    }

    navigationState.setValue(new NavigationState(fragmentClass, args));
  }

  public static class NavigationState {
    public final Class<? extends Fragment> fragmentClass;
    public final Bundle args;

    public NavigationState(Class<? extends Fragment> fragmentClass, Bundle args) {
      this.fragmentClass = fragmentClass;
      this.args = args;
    }
  }
}
