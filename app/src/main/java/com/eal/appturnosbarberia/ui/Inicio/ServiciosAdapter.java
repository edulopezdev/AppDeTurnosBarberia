package com.eal.appturnosbarberia.ui.Inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.models.DashboardClienteResponse;

import java.util.ArrayList;
import java.util.List;

public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.VH> {

  private List<DashboardClienteResponse.Servicio> items = new ArrayList<>();

  public void establecerElementos(List<DashboardClienteResponse.Servicio> lista) {
    items = lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    notifyDataSetChanged();
  }

  public void establecerServiciosCliente(List<DashboardClienteResponse.Servicio> lista) {
    items = lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    notifyDataSetChanged();
  }

  public void establecerServicios(List<? extends DashboardClienteResponse.Servicio> servicios) {
    this.items = servicios != null ? new ArrayList<>(servicios) : new ArrayList<>();
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
    return new VH(v);
  }

  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    DashboardClienteResponse.Servicio s = items.get(position);
    holder.tvNombre.setText(s.getNombre());
    holder.tvPrecio.setText("$" + String.format("%,d", s.getPrecio()));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  static class VH extends RecyclerView.ViewHolder {
    CardView card;
    TextView tvNombre;
    TextView tvPrecio;

    VH(@NonNull View itemView) {
      super(itemView);
      card = (CardView) itemView;
      tvNombre = itemView.findViewById(R.id.tvServicioNombre);
      tvPrecio = itemView.findViewById(R.id.tvServicioPrecio);
    }
  }
}
