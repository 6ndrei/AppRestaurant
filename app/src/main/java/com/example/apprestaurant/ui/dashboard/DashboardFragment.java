package com.example.apprestaurant.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.R;
import com.example.apprestaurant.adapters.CategAdapter;
import com.example.apprestaurant.adapters.ItemCategAdapter;
import com.example.apprestaurant.databinding.FragmentDashboardBinding;
import com.example.apprestaurant.models.CategModel;
import com.example.apprestaurant.models.ItemCategModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private String qrContent = "";
    private RecyclerView CategRec, ItemCategRec;
    private List<CategModel> categModelList;
    private List<ItemCategModel> itemCategModelList;
    private CategAdapter categAdapter;
    private ItemCategAdapter itemCategAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CategRec = root.findViewById(R.id.RecViewCateg);
        ItemCategRec = root.findViewById(R.id.RecViewItemCateg);

        categModelList = new ArrayList<>();
        categModelList.add(new CategModel(R.drawable.pizza, "Pizza"));
        categModelList.add(new CategModel(R.drawable.non, "Non-Alcohol"));

        itemCategModelList = new ArrayList<>();
        itemCategModelList.add(new ItemCategModel(R.drawable.dinner, "Baclava", "10 minute", "50 RON"));


        categAdapter = new CategAdapter(getActivity(), categModelList);
        CategRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        CategRec.setHasFixedSize(true);
        CategRec.setAdapter(categAdapter);

        itemCategAdapter = new ItemCategAdapter(getActivity(), itemCategModelList);
        ItemCategRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        ItemCategRec.setHasFixedSize(true);
        ItemCategRec.setAdapter(itemCategAdapter);

        CategRec.setVisibility(View.GONE);
        ItemCategRec.setVisibility(View.GONE);

        binding.ScaneazaButon2.setOnClickListener(view -> startQRScanner());
        binding.ScaneazaButon2.setText("Scanează");

        sharedPreferences = requireActivity().getSharedPreferences("Table_Session", 0);
        qrContent = sharedPreferences.getString("qrContent", "");

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


                    CategRec.setVisibility(View.VISIBLE);
                    ItemCategRec.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Codul scanat nu este un QR", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
