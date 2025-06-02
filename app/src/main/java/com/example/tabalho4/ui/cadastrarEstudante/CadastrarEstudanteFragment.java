package com.example.tabalho4.ui.cadastrarEstudante;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.tabalho4.R;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.modelView.EstudanteViewModel;

import java.util.ArrayList;


public class CadastrarEstudanteFragment extends Fragment {

    private EditText editNome, editIdade;
    private Button buttonCadastrar;
    private EstudanteViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_cadastrar_estudante, container, false);

        editNome = root.findViewById(R.id.editNome);
        editIdade = root.findViewById(R.id.editIdade);
        buttonCadastrar = root.findViewById(R.id.buttonCadastrar);

        viewModel = new ViewModelProvider(requireActivity()).get(EstudanteViewModel.class);

        buttonCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String idadeStr = editIdade.getText().toString().trim();

            if (nome.isEmpty() || idadeStr.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int idade = Integer.parseInt(idadeStr);
                Estudante estudante = new Estudante();
                estudante.setNome(nome);
                estudante.setIdade(idade);
                estudante.setNotas(new ArrayList<>());
                estudante.setPresenca(new ArrayList<>());

                viewModel.criarEstudante(estudante);

                viewModel.getCadastroSucesso().observe(getViewLifecycleOwner(), sucesso -> {
                    if (sucesso != null) {
                        if (sucesso) {
                            Toast.makeText(requireContext(), "Estudante cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).navigate(R.id.nav_home); // Volta para a listagem
                        } else {
                            Toast.makeText(requireContext(), "Falha ao cadastrar estudante", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Idade deve ser um número válido", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}
