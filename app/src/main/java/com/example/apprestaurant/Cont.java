package com.example.apprestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Cont extends Fragment {
    private Button logOutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_cont, container, false);

        logOutButton = root.findViewById(R.id.LogOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ContFragment", "onClick: Logout button clicked");
                // Șterge sesiunea utilizatorului și navighează înapoi la LoginActivity.
                SharedPreferences preferences = getActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE);
        String username = preferences.getString("username", "Vizitator");
        if("Vizitator".equals(username)) logOutButton.setVisibility(View.GONE);

        return root;
    }
}
