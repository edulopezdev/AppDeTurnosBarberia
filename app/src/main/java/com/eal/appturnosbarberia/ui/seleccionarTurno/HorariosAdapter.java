package com.eal.appturnosbarberia.ui.seleccionarTurno;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.models.HorarioSlot;

import java.util.ArrayList;
import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.VH> {

  public interface OnHorarioClickListener {
    void onHorarioClick(HorarioSlot horario);
  }

  private List<HorarioSlot> items = new ArrayList<>();
  private OnHorarioClickListener listener;
  private int selectedPos = -1;

  public HorariosAdapter(OnHorarioClickListener listener) {
    this.listener = listener;
  }

  public void updateData(List<HorarioSlot> list) {
    items = list != null ? list : new ArrayList<>();
    selectedPos = -1;
    notifyDataSetChanged();
  }

  public HorarioSlot getSelectedHorario() {
    return (selectedPos >= 0 && selectedPos < items.size()) ? items.get(selectedPos) : null;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
    return new VH(v);
  }

  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    HorarioSlot h = items.get(position);
    holder.tvHora.setText(h.getHora());

    // Estado visual según disponibilidad
    if (h.isDisponible()) {
      // disponible: fondo claro / texto oscuro
      holder.cardHorario
          .setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNeutralWhite));
      holder.tvHora.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNeutralBlack));
      holder.cardHorario.setAlpha(1f);
      holder.itemView.setEnabled(true);
    } else {
      // no disponible: atenuado
      holder.cardHorario
          .setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
      holder.tvHora.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
      holder.cardHorario.setAlpha(0.5f);
      holder.itemView.setEnabled(false);
    }

    // Selección: override visual si es el seleccionado
    boolean isSelected = (position == selectedPos);
    if (isSelected) {
      holder.cardHorario
          .setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
      holder.tvHora.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNeutralWhite));
    }

    holder.itemView.setOnClickListener(v -> {
      if (!h.isDisponible())
        return;
      int old = selectedPos;
      selectedPos = position;
      if (old >= 0)
        notifyItemChanged(old);
      notifyItemChanged(selectedPos);
      if (listener != null)
        listener.onHorarioClick(h);
    });
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  static class VH extends RecyclerView.ViewHolder {
    CardView cardHorario;
    TextView tvHora;

    VH(@NonNull View itemView) {
      super(itemView);
      cardHorario = itemView.findViewById(R.id.cardHorario);
      tvHora = itemView.findViewById(R.id.tvHora);
    }
  }
}
