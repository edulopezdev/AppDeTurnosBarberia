package com.eal.appturnosbarberia.ui.seleccionarTurno;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eal.appturnosbarberia.R;

import java.util.ArrayList;
import java.util.List;

public class DiasAdapter extends RecyclerView.Adapter<DiasAdapter.VH> {

  public interface OnDiaClickListener {
    void onDiaClick(String isoDate);
  }

  private List<DiaItem> dias = new ArrayList<>();
  private OnDiaClickListener listener;
  private String selectedDate = null;

  public DiasAdapter(OnDiaClickListener listener) {
    this.listener = listener;
  }

  public void setItems(List<DiaItem> dias) {
    this.dias = dias != null ? dias : new ArrayList<>();
    notifyDataSetChanged();
  }

  public void setSelectedDate(String isoDate) {
    String oldSelected = selectedDate;
    selectedDate = isoDate;

    // Notificar cambios solo en las posiciones afectadas
    for (int i = 0; i < dias.size(); i++) {
      DiaItem item = dias.get(i);
      if (item.getFechaIso().equals(oldSelected) || item.getFechaIso().equals(selectedDate)) {
        notifyItemChanged(i);
      }
    }
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dia, parent, false);
    return new VH(view);
  }

  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    DiaItem dayItem = dias.get(position);

    holder.tvDiaNumero.setText(String.valueOf(dayItem.getDiaNumero()));
    holder.tvDiaSemana.setText(dayItem.getDiaSemana());

    // Marcar como seleccionado si corresponde
    boolean isSelected = dayItem.getFechaIso().equals(selectedDate);
    holder.containerDia.setSelected(isSelected);

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        setSelectedDate(dayItem.getFechaIso());
        listener.onDiaClick(dayItem.getFechaIso());
      }
    });
  }

  @Override
  public int getItemCount() {
    return dias.size();
  }

  static class VH extends RecyclerView.ViewHolder {
    TextView tvDiaSemana, tvDiaNumero;
    View containerDia;

    VH(@NonNull View itemView) {
      super(itemView);
      containerDia = itemView.findViewById(R.id.containerDia);
      tvDiaSemana = itemView.findViewById(R.id.tvDiaSemana);
      tvDiaNumero = itemView.findViewById(R.id.tvDiaNumero);
    }
  }
}
