package com.example.tabalho4.models.entity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tabalho4.R;
import java.util.ArrayList;
import java.util.List;

public class EstudanteAdapter extends RecyclerView.Adapter<EstudanteAdapter.EstudanteViewHolder> {

    private List<Estudante> estudantes;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Estudante estudante);
    }

    public EstudanteAdapter(List<Estudante> estudantes, OnItemClickListener listener) {
        this.estudantes = estudantes != null ? new ArrayList<>(estudantes) : new ArrayList<>();
        this.listener = listener;
        Log.d("EstudanteAdapter", "Inicializado com " + this.estudantes.size() + " estudantes");
    }

    public void updateEstudantes(List<Estudante> newEstudantes) {
        this.estudantes.clear();
        if (newEstudantes != null) {
            this.estudantes.addAll(newEstudantes);
        }
        Log.d("EstudanteAdapter", "Lista atualizada com " + this.estudantes.size() + " estudantes");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EstudanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudante, parent, false);
        return new EstudanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudanteViewHolder holder, int position) {
        Estudante estudante = estudantes.get(position);
        holder.textNome.setText(estudante.getNome() != null ? estudante.getNome() : "N/A");
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(estudante));
        }
        Log.d("EstudanteAdapter", "Vinculando estudante: " + estudante.getNome() + " na posição " + position);
    }

    @Override
    public int getItemCount() {
        return estudantes.size();
    }

    static class EstudanteViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;

        public EstudanteViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
        }
    }
}