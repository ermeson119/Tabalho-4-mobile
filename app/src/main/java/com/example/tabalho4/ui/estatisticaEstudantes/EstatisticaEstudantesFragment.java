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
import com.example.tabalho4.ui.home.HomeFragmentViewModel;
import com.example.tabalho4.models.entity.EstudanteAdapter;
import java.util.ArrayList;

public class EstatisticaEstudantesFragment extends Fragment {
    private FragmentEstatisticaEstudantesBinding binding;
    private EstatisticaEstudantesViewModel estatisticaEstudantesViewModel;
    private HomeFragmentViewModel estudanteViewModel;
    private TextView textMediaTurma, textMediaIdade, textMaior, textMenor;
    private EstudanteAdapter adapterAprovados, adapterReprovados;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEstatisticaEstudantesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        estudanteViewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);
        estatisticaEstudantesViewModel = new ViewModelProvider(this).get(EstatisticaEstudantesViewModel.class);

        textMediaTurma = root.findViewById(R.id.textMediaTurma);
        textMediaIdade = root.findViewById(R.id.textMediaIdade);
        textMaior = root.findViewById(R.id.textMaior);
        textMenor = root.findViewById(R.id.textMenor);

        RecyclerView recyclerAprovados = root.findViewById(R.id.recyclerAprovados);
        recyclerAprovados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterAprovados = new EstudanteAdapter(new ArrayList<>(), null);
        recyclerAprovados.setAdapter(adapterAprovados);

        RecyclerView recyclerReprovados = root.findViewById(R.id.recyclerReprovados);
        recyclerReprovados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterReprovados = new EstudanteAdapter(new ArrayList<>(), null);
        recyclerReprovados.setAdapter(adapterReprovados);

        estudanteViewModel.getEstudantes().observe(getViewLifecycleOwner(), estudantes -> {
            if (estudantes != null && !estudantes.isEmpty()) {
                estatisticaEstudantesViewModel.atualizarEstatisticas(estudantes);
            } else {
                textMediaTurma.setText("Média Geral da Turma: ");
                textMediaIdade.setText("Média de Idade: ");
                textMaior.setText("Maior Média: ");
                textMenor.setText("Menor Média: ");
                adapterAprovados.updateEstudantes(new ArrayList<>());
                adapterReprovados.updateEstudantes(new ArrayList<>());
            }
        });

        estatisticaEstudantesViewModel.getMediaTurma().observe(getViewLifecycleOwner(), media -> {
            if (media != null && media != 0.0) {
                textMediaTurma.setText(String.format("Média Geral da Turma: %.2f", media));
            } else {
                textMediaTurma.setText("Média Geral da Turma: ");
            }
        });

        estatisticaEstudantesViewModel.getMediaIdade().observe(getViewLifecycleOwner(), idade -> {
            if (idade != null && idade != 0.0) {
                textMediaIdade.setText(String.format("Média de Idade: %.2f", idade));
            } else {
                textMediaIdade.setText("Média de Idade: ");
            }
        });

        estatisticaEstudantesViewModel.getMaiorMedia().observe(getViewLifecycleOwner(), maior -> {
            if (maior != null) {
                textMaior.setText(String.format("Maior Média: %s", maior.getNome()));
            } else {
                textMaior.setText("Maior Média: ");
            }
        });

        estatisticaEstudantesViewModel.getMenorMedia().observe(getViewLifecycleOwner(), menor -> {
            if (menor != null) {
                textMenor.setText(String.format("Menor Média: %s", menor.getNome()));
            } else {
                textMenor.setText("Menor Média: ");
            }
        });

        estatisticaEstudantesViewModel.getAprovados().observe(getViewLifecycleOwner(), aprovados -> {
            if (aprovados != null && !aprovados.isEmpty()) {
                adapterAprovados.updateEstudantes(aprovados);
            } else {
                adapterAprovados.updateEstudantes(new ArrayList<>());
            }
        });

        estatisticaEstudantesViewModel.getReprovados().observe(getViewLifecycleOwner(), reprovados -> {
            if (reprovados != null && !reprovados.isEmpty()) {
                adapterReprovados.updateEstudantes(reprovados);
            } else {
                adapterReprovados.updateEstudantes(new ArrayList<>());
            }
        });

        estatisticaEstudantesViewModel.getError().observe(getViewLifecycleOwner(), error -> {
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