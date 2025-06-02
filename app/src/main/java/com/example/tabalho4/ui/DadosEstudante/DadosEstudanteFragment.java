package com.example.tabalho4.ui.DadosEstudante;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.example.tabalho4.ui.home.HomeFragmentViewModel;

public class DadosEstudanteFragment extends Fragment {
    private FragmentDadosEstudanteBinding binding;
    private DadosEstudanteViewModel dadosEstudanteViewModel;
    private HomeFragmentViewModel homeViewModel;
    private TextView textNome, textIdade, textMedia, textFrequencia, textSituacao;
    private Button buttonAdicionarNota, buttonAdicionarFrequencia, buttonDeletar;
    private int estudanteId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDadosEstudanteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);
        dadosEstudanteViewModel = new ViewModelProvider(this).get(DadosEstudanteViewModel.class);
        dadosEstudanteViewModel.setViewModelStoreOwner(requireActivity()); // Configurar HomeFragmentViewModel

        textNome = root.findViewById(R.id.textNome);
        textIdade = root.findViewById(R.id.textIdade);
        textMedia = root.findViewById(R.id.textMedia);
        textFrequencia = root.findViewById(R.id.textFrequencia);
        textSituacao = root.findViewById(R.id.textSituacao);
        buttonAdicionarNota = root.findViewById(R.id.buttonAdicionarNota);
        buttonAdicionarFrequencia = root.findViewById(R.id.buttonAdicionarFrequencia);
        buttonDeletar = root.findViewById(R.id.buttonDeletar);

        if (getArguments() != null) {
            estudanteId = getArguments().getInt("estudanteId", -1);
            if (estudanteId != -1) {
                dadosEstudanteViewModel.carregarEstudante(estudanteId);
            } else {
                Toast.makeText(requireContext(), "Nenhum estudante selecionado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(root).navigate(R.id.nav_home);
                return root;
            }
        }

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
                    dadosEstudanteViewModel.adicionarNota(estudanteId, nota);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Nota inválida", Toast.LENGTH_SHORT).show();
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
                dadosEstudanteViewModel.adicionarFrequencia(estudanteId, true);
            });
            builder.setNegativeButton("Não", (dialog, which) -> {
                dadosEstudanteViewModel.adicionarFrequencia(estudanteId, false);
            });
            builder.show();
        });

        buttonDeletar.setOnClickListener(v -> {
            if (estudanteId != -1) {
                dadosEstudanteViewModel.deletarEstudante(estudanteId);
            }
        });

        dadosEstudanteViewModel.getEstudante().observe(getViewLifecycleOwner(), estudante -> {
            if (estudante != null) {
                textNome.setText("Nome: " + (estudante.getNome() != null ? estudante.getNome() : ""));
                textIdade.setText("Idade: " + (estudante.getIdade() != null ? estudante.getIdade() : ""));
            }
        });

        dadosEstudanteViewModel.getMedia().observe(getViewLifecycleOwner(), media -> {
            textMedia.setText("Média: " + (media != null && media != 0.0 ? String.format("%.2f", media) : ""));
        });

        dadosEstudanteViewModel.getFrequencia().observe(getViewLifecycleOwner(), freq -> {
            textFrequencia.setText("Frequência: " + (freq != null && freq != 0.0 ? String.format("%.2f%%", freq) : ""));
        });

        dadosEstudanteViewModel.getSituacao().observe(getViewLifecycleOwner(), situacao -> {
            textSituacao.setText("Situação: " + (situacao != null ? situacao : ""));
        });

        dadosEstudanteViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        dadosEstudanteViewModel.getCadastroSucesso().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso != null && sucesso) {
                dadosEstudanteViewModel.getOperacaoTipo().observe(getViewLifecycleOwner(), tipo -> {
                    if (tipo != null) {
                        switch (tipo) {
                            case "adicionar_nota":
                                Toast.makeText(requireContext(), "Nota adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                break;
                            case "adicionar_frequencia":
                                Toast.makeText(requireContext(), "Frequência adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                break;
                            case "deletar":
                                Toast.makeText(requireContext(), "Estudante deletado", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(root).navigate(R.id.action_dados_estudante_to_home);
                                break;
                        }
                    }
                });
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