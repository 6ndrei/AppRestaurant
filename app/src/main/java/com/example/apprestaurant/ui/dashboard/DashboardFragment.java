package com.example.apprestaurant.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.apprestaurant.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Accesează elementele din layout direct.
      //  binding.ScaneazaButon2.setOnClickListener(view -> startQRScanner());
        final TextView textView = binding.ScaneazaButon2;
        textView.setText("Scanează");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
