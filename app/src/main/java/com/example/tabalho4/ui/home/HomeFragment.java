package com.example.tabalho4.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tabalho4.R;
import com.example.tabalho4.databinding.FragmentHomeBinding;
import com.example.tabalho4.modelView.EstudanteViewModel;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.EstudanteAdapter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private EstudanteViewModel viewModel;
    private EstudanteAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(EstudanteViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstudanteAdapter(new ArrayList<>(), estudante -> {
            Bundle bundle = new Bundle();
            bundle.putInt("estudanteId", estudante.getId());
            Navigation.findNavController(root).navigate(R.id.nav_dados_estudante, bundle);
            viewModel.estudanteSelecionadoId(estudante.getId());
        });
        recyclerView.setAdapter(adapter);

        viewModel.getEstudantes().observe(getViewLifecycleOwner(), estudantes -> {
            if (estudantes != null) {
                adapter.updateEstudantes(estudantes);
            } else {
                adapter.updateEstudantes(new ArrayList<>());
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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