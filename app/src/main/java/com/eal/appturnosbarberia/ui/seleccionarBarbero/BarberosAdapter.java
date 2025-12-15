package com.eal.appturnosbarberia.ui.seleccionarBarbero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eal.appturnosbarberia.R;
import com.eal.appturnosbarberia.databinding.ItemBarberoBinding;
import com.eal.appturnosbarberia.models.Barbero;

import java.util.ArrayList;
import java.util.List;

public class BarberosAdapter extends RecyclerView.Adapter<BarberosAdapter.BarberoViewHolder> {

  // esta es la interfaz para manejar clicks en los barberos
  public interface OnBarberoClickListener {
    void onBarberoClick(Barbero barbero);
  }

  private List<Barbero> barberos = new ArrayList<>(); // esta es la lista de barberos a mostrar
  private OnBarberoClickListener listener; // este es el listener para manejar clicks
  private int selectedPosition = -1; // aca lo q digo es q no hay ningun barbero seleccionado inicialmente

  // este metodo es el constructor del adapter, sirve para inicializar el listener
  public BarberosAdapter(OnBarberoClickListener listener) {
    this.listener = listener;
  }

  // este metodo sirve para actualizar la lista de barberos, de modo q   cuando cambie la lista en el ViewModel, se actualice el RecyclerView
  public void setItems(List<Barbero> barberos) {
    this.barberos = barberos != null ? barberos : new ArrayList<>();
    selectedPosition = -1;
    notifyDataSetChanged();
  }

  // este metodo devuelve el barbero seleccionado actualmente, o null si no hay ninguno seleccionado
  public Barbero getSelected() {
    return selectedPosition >= 0 && selectedPosition < barberos.size()
        ? barberos.get(selectedPosition)
        : null;
  }

  // este metodo crea el ViewHolder para cada item del RecyclerView, inflando el layout correspondiente
  @NonNull
  @Override
  public BarberoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemBarberoBinding binding = ItemBarberoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new BarberoViewHolder(binding);
  }

  // este metodo vincula los datos del barbero con las vistas del ViewHolder
  @Override
  public void onBindViewHolder(@NonNull BarberoViewHolder holder, int position) {
    Barbero barbero = barberos.get(position); // aca obtenemos el barbero correspondiente a la posicion
    holder.binding.tvNombreBarbero.setText(barbero.getNombre()); // y lo seteamos en el TextView

    // lo q hacemos con Glide para cargar la imagen del avatar
    Glide.with(holder.binding.ivAvatarBarbero.getContext()).clear(holder.binding.ivAvatarBarbero); // limpiar cualquier carga previa
    String avatarUrl = barbero.getAvatarUrl(); // obtener la URL del avatar en caso de que exista
    Glide.with(holder.binding.ivAvatarBarbero.getContext())// le damos el contexto al Glide
        .load(avatarUrl != null && !avatarUrl.trim().isEmpty() ? avatarUrl : R.drawable.profile) // cargar la URL o imagen por defecto
        .placeholder(R.drawable.profile) //mostramos una imagen por defecto mientras carga
        .error(R.drawable.profile) //si falla tenemos otra imagen por defecto
        .circleCrop() // esto lo q hace es recortar la imagen en forma circular
        .into(holder.binding.ivAvatarBarbero); // por ultimo lo q hacemos es poner la imagen en el ImageView

    // ahora lo q hacemos es actualizar el estado del checkbox segun si este item esta seleccionado o no
    boolean isSelected = position == selectedPosition; // vemos si esta seleccionado, comparando posiciones
    holder.binding.cbSelected.setImageResource(isSelected ? R.drawable.cb_checked : R.drawable.cb_unchecked); // cambiar imagen del checkbox segun estado

    // cambiar color de fondo segun si esta seleccionado o no
    holder.binding.cbSelected.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int oldPos = selectedPosition;
        int adapterPos = holder.getAdapterPosition();
        if (adapterPos < 0)
          return;

        if (selectedPosition == adapterPos) {
          // si ya estaba seleccionado, deseleccionamos
          selectedPosition = -1;
          // actualizar solo el antiguo item
          notifyItemChanged(adapterPos);
        } else {
          // seleccionar nuevo
          selectedPosition = adapterPos;
          if (oldPos >= 0)
            notifyItemChanged(oldPos);
          notifyItemChanged(selectedPosition);
        }

        // Notificar al listener solo cuando se selecciona (no al deseleccionar)
        if (listener != null && selectedPosition == adapterPos) {
          listener.onBarberoClick(barbero); // notificar al listener del click
        }
      }
    });

    // aca evitamos q el itemView tenga su propio listener para evitar conflictos con el checkbox
    holder.itemView.setOnClickListener(null);
  }

  // este metodo devuelve la cantidad de items en la lista de barberos
  @Override
  public int getItemCount() {
    return barberos.size();
  }

  // aca lo q hacemos es definir el ViewHolder para los barberos del RecyclerView
  static class BarberoViewHolder extends RecyclerView.ViewHolder {
    final ItemBarberoBinding binding;

    // este es el constructor del ViewHolder, sirve para inicializar las vistas del item
    BarberoViewHolder(ItemBarberoBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }
}
