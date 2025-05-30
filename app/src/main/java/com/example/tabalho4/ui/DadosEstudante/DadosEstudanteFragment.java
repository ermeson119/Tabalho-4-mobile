package com.example.tabalho4.ui.DadosEstudante;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private static final String TAG = "DadosEstudanteFragment";
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
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Adicionar Nota");
            final EditText input = new EditText(requireContext());
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                try {
                    double nota = Double.parseDouble(input.getText().toString());
                    if (nota < 0 || nota > 10) {
                        Toast.makeText(requireContext(), "Nota deve estar entre 0 e 10", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "Solicitando adição de nota: " + nota + " para estudante ID: " + estudanteId);
                    viewModel.adicionarNota(estudanteId, nota);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Nota inválida", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao parsear nota: " + e.getMessage());
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        buttonAdicionarFrequencia.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Adicionar Frequência");
            builder.setMessage("O estudante estava presente?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                Log.d(TAG, "Solicitando adição de frequência (presente) para estudante ID: " + estudanteId);
                viewModel.adicionarFrequencia(estudanteId, true);
            });
            builder.setNegativeButton("Não", (dialog, which) -> {
                Log.d(TAG, "Solicitando adição de frequência (ausente) para estudante ID: " + estudanteId);
                viewModel.adicionarFrequencia(estudanteId, false);
            });
            builder.show();
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
                Log.d(TAG, "Estudante atualizado na UI: " + estudante.getNome());
            }
        });

        viewModel.getMedia().observe(getViewLifecycleOwner(), media -> {
            textMedia.setText("Média: " + (media != null && media != 0.0 ? String.format("%.2f", media) : "N/A"));
            Log.d(TAG, "Média atualizada: " + media);
        });

        viewModel.getFrequencia().observe(getViewLifecycleOwner(), freq -> {
            textFrequencia.setText("Frequência: " + (freq != null && freq != 0.0 ? String.format("%.2f%%", freq) : "N/A"));
            Log.d(TAG, "Frequência atualizada: " + freq);
        });

        viewModel.getSituacao().observe(getViewLifecycleOwner(), situacao -> {
            textSituacao.setText("Situação: " + (situacao != null ? situacao : "N/A"));
            Log.d(TAG, "Situação atualizada: " + situacao);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro recebido: " + error);
            }
        });

        viewModel.getCadastroSucesso().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso != null) {
                if (sucesso) {
                    Toast.makeText(requireContext(), "Atualização realizada com sucesso", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Atualização bem-sucedida");
                } else {
                    Toast.makeText(requireContext(), "Falha ao atualizar estudante", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Falha na atualização");
                }
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