package com.example.apprestaurant.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurant.R;
import com.example.apprestaurant.adapters.CategAdapter;
import com.example.apprestaurant.adapters.ItemCategAdapter;
import com.example.apprestaurant.databinding.FragmentDashboardBinding;
import com.example.apprestaurant.models.CategModel;
import com.example.apprestaurant.models.ItemCategModel;
import com.example.apprestaurant.models.UpdateItemCateg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements UpdateItemCateg {

    private FragmentDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private String qrContent = "";
    private RecyclerView CategRec, ItemCategRec;
    private ArrayList<CategModel> categModelList;
    private ArrayList<ItemCategModel> itemCategModelList;
    private CategAdapter categAdapter;
    private ItemCategAdapter itemCategAdapter;
    private FirebaseFirestore firestore;
    private AnimationDrawable animationDrawable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout constraintLayout = root.findViewById(R.id.dashboardConstraint);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2500);

        root.findViewById(R.id.loadingPanelDashboard).setVisibility(View.GONE);

        CategRec = root.findViewById(R.id.RecViewCateg);
        ItemCategRec = root.findViewById(R.id.RecViewItemCateg);

        categModelList = new ArrayList<>();
        itemCategModelList = new ArrayList<>();

        categAdapter = new CategAdapter(getActivity(), categModelList, this);
        CategRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        CategRec.setHasFixedSize(true);
        CategRec.setAdapter(categAdapter);

        itemCategAdapter = new ItemCategAdapter(getActivity(), itemCategModelList);
        ItemCategRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        ItemCategRec.setHasFixedSize(true);
        ItemCategRec.setAdapter(itemCategAdapter);

        firestore = FirebaseFirestore.getInstance();
        loadCategoriesFromFirebase();

        sharedPreferences = requireActivity().getSharedPreferences("Table_Session", 0);
        qrContent = sharedPreferences.getString("qrContent", "");
        boolean isQRScanned = sharedPreferences.getBoolean("isQRScanned", false);

        if (isQRScanned) {
            binding.qrCodeLayout.setVisibility(View.GONE);
            binding.loadingPanelDashboard.setVisibility(View.VISIBLE);
            binding.categLayout.setVisibility(View.VISIBLE);
            binding.loadingPanelDashboard.setVisibility(View.GONE);
        } else {
            binding.categLayout.setVisibility(View.GONE);
            binding.ScaneazaButon2.setOnClickListener(view -> startQRScanner());
            binding.ScaneazaButon2.setText("Scanează");
        }

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
                    qrContent = result.getContents();
                    try {
                        int qrNumber = Integer.parseInt(qrContent);
                        if (qrNumber >= 1 && qrNumber <= 15) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("qrContent", qrContent);
                            editor.putBoolean("isQRScanned", true);
                            editor.apply();
                            binding.loadingPanelDashboard.setVisibility(View.VISIBLE);
                            binding.qrCodeLayout.setVisibility(View.GONE);
                            binding.categLayout.setVisibility(View.VISIBLE);
                            binding.loadingPanelDashboard.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getActivity(), "Cod invalid", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Cod invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Codul scanat nu este un QR", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadCategoriesFromFirebase() {
        if (binding != null && binding.loadingPanelDashboard != null) {
            binding.loadingPanelDashboard.setVisibility(View.VISIBLE);
        }
        firestore.collection("categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (binding != null && binding.loadingPanelDashboard != null) {
                    binding.loadingPanelDashboard.setVisibility(View.GONE);
                }
                if (task.isSuccessful()) {
                    categModelList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        String image = document.getString("image");
                        String name = document.getString("name");
                        categModelList.add(new CategModel(id, image, name));
                    }
                    categAdapter.notifyDataSetChanged();
                    if (!categModelList.isEmpty()) {
                        int selectedIndex = 0;
                        DashboardFragment.this.callBack(selectedIndex, new ArrayList<>());
                        CategRec.smoothScrollToPosition(selectedIndex);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadItemsForCategory(String categoryId) {
        if (binding != null && binding.loadingPanelDashboard != null) {
            binding.loadingPanelDashboard.setVisibility(View.VISIBLE);
        }
        firestore.collection("items")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (binding != null && binding.loadingPanelDashboard != null) {
                            binding.loadingPanelDashboard.setVisibility(View.GONE);
                        }
                        if (task.isSuccessful()) {
                            itemCategModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String image = document.getString("image");
                                String name = document.getString("name");
                                String timing = document.getString("timing");
                                String price = document.getString("price");
                                itemCategModelList.add(new ItemCategModel(image, name, timing, price));
                            }
                            itemCategAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Failed to load items", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void callBack(int position, ArrayList<ItemCategModel> list) {
        if (position >= 0 && position < categModelList.size()) {
            String categoryId = categModelList.get(position).getId();
            loadItemsForCategory(categoryId);
        } else {
            Log.e("DashboardFragment", "Invalid position: " + position);
        }
    }

    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void clearPreferences() {
        clearSharedPreferences();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Pornește animația
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Oprește animația
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}



