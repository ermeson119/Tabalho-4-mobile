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
import com.example.tabalho4.models.entity.EstudanteAdapter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeFragmentViewModel homeViewModel;
    private EstudanteAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstudanteAdapter(new ArrayList<>(), estudante -> {
            Bundle bundle = new Bundle();
            bundle.putInt("estudanteId", estudante.getId());
            Navigation.findNavController(root).navigate(R.id.action_home_to_dados_estudante, bundle);
        });
        recyclerView.setAdapter(adapter);

        homeViewModel.getEstudantes().observe(getViewLifecycleOwner(), estudantes -> {
            if (estudantes != null) {
                adapter.updateEstudantes(estudantes);
            } else {
                adapter.updateEstudantes(new ArrayList<>());
            }
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
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