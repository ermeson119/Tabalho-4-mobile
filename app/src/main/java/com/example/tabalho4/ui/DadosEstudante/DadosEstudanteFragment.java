package com.example.tabalho4.ui.DadosEstudante;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.tabalho4.R;
import com.example.tabalho4.databinding.FragmentDadosEstudanteBinding;
import com.example.tabalho4.modelView.EstudanteViewModel;
import com.example.tabalho4.models.entity.Estudante;

public class DadosEstudanteFragment extends Fragment {

    private FragmentDadosEstudanteBinding binding;
    private EstudanteViewModel viewModel;
    private TextView textNome, textIdade, textMedia, textFrequencia, textSituacao;
    private Button buttonAdicionarNota, buttonAdicionarFrequencia, buttonDeletar;
    private int estudanteId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDadosEstudanteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(EstudanteViewModel.class);

        // Inicializar elementos UI
        textNome = root.findViewById(R.id.textNome);
        textIdade = root.findViewById(R.id.textIdade);
        textMedia = root.findViewById(R.id.textMedia);
        textFrequencia = root.findViewById(R.id.textFrequencia);
        textSituacao = root.findViewById(R.id.textSituacao);
        buttonAdicionarNota = root.findViewById(R.id.buttonAdicionarNota);
        buttonAdicionarFrequencia = root.findViewById(R.id.buttonAdicionarFrequencia);
        buttonDeletar = root.findViewById(R.id.buttonDeletar);

        // Obter ID do aluno dos argumentos
        if (getArguments() != null) {
            estudanteId = getArguments().getInt("estudanteId", -1);
            if (estudanteId != -1) {
                viewModel.estudanteSelecionadoId(estudanteId);
            } else {
                Toast.makeText(requireContext(), "Nenhum aluno selecionado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(root).navigate(R.id.nav_home);
                return root;
            }
        }

        // Configurar botões
        buttonAdicionarNota.setOnClickListener(v -> {
            // Lógica de adicionar nota (a ser implementada conforme necessário)
            Toast.makeText(requireContext(), "Funcionalidade de adicionar nota", Toast.LENGTH_SHORT).show();
        });

        buttonAdicionarFrequencia.setOnClickListener(v -> {
            // Lógica de adicionar frequência (a ser implementada conforme necessário)
            Toast.makeText(requireContext(), "Funcionalidade de adicionar frequência", Toast.LENGTH_SHORT).show();
        });

        buttonDeletar.setOnClickListener(v -> {
            if (estudanteId != -1) {
                viewModel.deletarEstudante(estudanteId);
                Toast.makeText(requireContext(), "Estudante deletado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });

        // Observar LiveData
        viewModel.getSelectedEstudante().observe(getViewLifecycleOwner(), estudante -> {
            if (estudante != null) {
                textNome.setText("Nome: " + (estudante.getNome() != null ? estudante.getNome() : "N/A"));
                textIdade.setText("Idade: " + (estudante.getIdade() != null ? estudante.getIdade() : "N/A"));
            }
        });

        viewModel.getMedia().observe(getViewLifecycleOwner(), media -> {
            textMedia.setText("Média: " + (media != null && media != 0.0 ? String.format("%.2f", media) : "N/A"));
        });

        viewModel.getFrequencia().observe(getViewLifecycleOwner(), freq -> {
            textFrequencia.setText("Frequência: " + (freq != null && freq != 0.0 ? String.format("%.2f%%", freq) : "N/A"));
        });

        viewModel.getSituacao().observe(getViewLifecycleOwner(), situacao -> {
            textSituacao.setText("Situação: " + (situacao != null ? situacao : "N/A"));
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
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