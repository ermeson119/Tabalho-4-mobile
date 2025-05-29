package com.example.tabalho4.ui.estatisticaEstudantes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tabalho4.R;
import com.example.tabalho4.databinding.FragmentEstatisticaEstudantesBinding;
import com.example.tabalho4.modelView.TurmaViewModel;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.EstudanteAdapter;

import java.util.ArrayList;

public class EstatisticaEstudantesFragment extends Fragment {

    private FragmentEstatisticaEstudantesBinding binding;
    private TurmaViewModel viewModel;
    private TextView textMediaTurma, textMediaIdade, textMaior, textMenor;
    private EstudanteAdapter adapterAprovados, adapterReprovados;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEstatisticaEstudantesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(TurmaViewModel.class);

        // Initialize UI elements
        textMediaTurma = root.findViewById(R.id.textMediaTurma);
        textMediaIdade = root.findViewById(R.id.textMediaIdade);
        textMaior = root.findViewById(R.id.textMaior);
        textMenor = root.findViewById(R.id.textMenor);

        // Set up RecyclerViews
        RecyclerView recyclerAprovados = root.findViewById(R.id.recyclerAprovados);
        recyclerAprovados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterAprovados = new EstudanteAdapter(new ArrayList<>(), null); // No click listener needed
        recyclerAprovados.setAdapter(adapterAprovados);

        RecyclerView recyclerReprovados = root.findViewById(R.id.recyclerReprovados);
        recyclerReprovados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterReprovados = new EstudanteAdapter(new ArrayList<>(), null); // No click listener needed
        recyclerReprovados.setAdapter(adapterReprovados);

        // Observe LiveData from TurmaViewModel
        viewModel.getMediaTurma().observe(getViewLifecycleOwner(), media -> {
            if (media != null && media != 0.0) {
                textMediaTurma.setText(String.format("Média Geral da Turma: %.2f", media));
            } else {
                textMediaTurma.setText("Média Geral da Turma: N/A");
            }
        });

        viewModel.getMediaIdade().observe(getViewLifecycleOwner(), idade -> {
            if (idade != null && idade != 0.0) {
                textMediaIdade.setText(String.format("Média de Idade: %.2f", idade));
            } else {
                textMediaIdade.setText("Média de Idade: N/A");
            }
        });

        viewModel.getMaiorMedia().observe(getViewLifecycleOwner(), maior -> {
            if (maior != null && !maior.equals("N/A")) {
                textMaior.setText(String.format("Maior Média: %s", maior));
            } else {
                textMaior.setText("Maior Média: N/A");
            }
        });

        viewModel.getMenorMedia().observe(getViewLifecycleOwner(), menor -> {
            if (menor != null && !menor.equals("N/A")) {
                textMenor.setText(String.format("Menor Média: %s", menor));
            } else {
                textMenor.setText("Menor Média: N/A");
            }
        });

        // Fix: Observe the List<Estudante> for approved students
        viewModel.getAprovados().observe(getViewLifecycleOwner(), aprovados -> {
            if (aprovados != null && !aprovados.isEmpty()) {
                adapterAprovados.updateEstudantes(aprovados);
            } else {
                adapterAprovados.updateEstudantes(new ArrayList<>());
            }
        });

        // Fix: Observe the List<Estudante> for reproved students
        viewModel.getReprovados().observe(getViewLifecycleOwner(), reprovados -> {
            if (reprovados != null && !reprovados.isEmpty()) {
                adapterReprovados.updateEstudantes(reprovados);
            } else {
                adapterReprovados.updateEstudantes(new ArrayList<>());
            }
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                textMediaTurma.setText("Erro ao carregar dados");
                textMediaIdade.setText("Erro ao carregar dados");
                textMaior.setText("Erro ao carregar dados");
                textMenor.setText("Erro ao carregar dados");
                adapterAprovados.updateEstudantes(new ArrayList<>());
                adapterReprovados.updateEstudantes(new ArrayList<>());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}