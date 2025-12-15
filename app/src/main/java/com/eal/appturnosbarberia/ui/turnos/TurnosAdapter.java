package com.eal.appturnosbarberia.ui.turnos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.eal.appturnosbarberia.MainActivityViewModel;
import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.models.Turno;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TurnosAdapter extends RecyclerView.Adapter<TurnosAdapter.VH> {

  public interface OnCancelListener {
    void onCancel(Turno t, String obs, MainActivityViewModel.CancelCallback cb);
  }

  private List<Turno> items = new ArrayList<>();
  private final OnCancelListener listener;

  public TurnosAdapter(OnCancelListener listener) {
    this.listener = listener;
  }

  public void setItems(List<Turno> list) {
    items = list != null ? list : new ArrayList<>();
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new VH(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_turno, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull VH h, int pos) {
    Turno t = items.get(pos);

    h.tvBarbero.setText("✂️ " + t.getBarbero().getNombre());
    h.tvFecha.setText(formatDate(t.getFechaHora()));
    h.tvHora.setText(formatTime(t.getFechaHora()));

    // Mostrar el estado del turno dinámicamente
    if (t.getEstado() != null) {
      String nombreEstado = t.getEstado().getNombre();
      h.chipEstado.setText(nombreEstado != null ? nombreEstado : "Desconocido");

      // Cambiar el color del fondo según el estado
      int estadoId = t.getEstado().getEstadoId();
      if (estadoId == 1) {
        // Pendiente
        h.chipEstado.setBackgroundResource(R.drawable.bg_status_pending_tonal);
      } else if (estadoId == 2) {
        // Confirmado
        h.chipEstado.setBackgroundResource(R.drawable.bg_status_confirmed_tonal);
      } else if (estadoId == 3) {
        // Cancelado
        h.chipEstado.setBackgroundResource(R.drawable.bg_status_canceled_tonal);
      }
    }

    int estado = t.getEstado() != null ? t.getEstado().getEstadoId() : -1;
    h.btnCancelar.setVisibility(estado == 3 ? View.GONE : View.VISIBLE);

    h.btnCancelar.setOnClickListener(v -> showCancelDialog(h.itemView.getContext(), t));
  }

  private void showCancelDialog(Context ctx, Turno t) {
    EditText input = new EditText(ctx);

    new AlertDialog.Builder(ctx)
        .setTitle("Cancelar turno")
        .setView(input)
        .setPositiveButton("Confirmar", (d, i) -> listener.onCancel(t, input.getText().toString(), success -> {
        }))
        .setNegativeButton("Volver", null)
        .show();
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  static class VH extends RecyclerView.ViewHolder {
    TextView tvBarbero, tvFecha, tvHora, chipEstado;
    MaterialButton btnCancelar;

    VH(@NonNull View v) {
      super(v);
      tvBarbero = v.findViewById(R.id.tvBarberoNombre);
      tvFecha = v.findViewById(R.id.tvFecha);
      tvHora = v.findViewById(R.id.tvHora);
      chipEstado = v.findViewById(R.id.chipEstado);
      btnCancelar = v.findViewById(R.id.btnCancelar);
    }
  }

  private String formatDate(String iso) {
    try {
      Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(iso);
      return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
    } catch (Exception e) {
      return "";
    }
  }

  private String formatTime(String iso) {
    try {
      Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(iso);
      return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(d);
    } catch (Exception e) {
      return "";
    }
  }
}
