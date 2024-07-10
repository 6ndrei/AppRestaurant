package com.example.apprestaurant.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.apprestaurant.databinding.FragmentDashboardBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DashboardFragment extends Fragment {

    private TextView table;
    private SharedPreferences sharedPreferences;
    private String qrContent = "";

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = requireActivity().getSharedPreferences("Table_Session", 0);
        qrContent = sharedPreferences.getString("qrContent", "");
      /*  if (!qrContent.isEmpty()) {
            binding.Table.setText(qrContent);
        } else {
            binding.Table.setText("0");
        } */
        binding.ScaneazaButon2.setOnClickListener(view -> startQRScanner());
        binding.ScaneazaButon2.setText("Scanează");

        return root;
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("");
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scanare anulată", Toast.LENGTH_SHORT).show();
            } else {
                if (result.getFormatName().equals("QR_CODE")) {
                    qrContent = result.getContents(); // Update qrContent for the session
                    Toast.makeText(getActivity(), "Cod QR scanat: " + qrContent, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("qrContent", qrContent);
                    editor.apply();
                    binding.ScaneazaButon2.setVisibility(View.GONE);
                    binding.TextQR.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), "Codul scanat nu este un QR", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up binding
    }
    }