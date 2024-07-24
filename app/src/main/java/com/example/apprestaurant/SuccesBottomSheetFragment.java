package com.example.apprestaurant;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SuccesBottomSheetFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    public static SuccesBottomSheetFragment newInstance(String message) {
        SuccesBottomSheetFragment fragment = new SuccesBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_bottom_sheet, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView messageTextView = view.findViewById(R.id.message_text_view);

        if (getArguments() != null) {
            String message = getArguments().getString(ARG_MESSAGE);
            messageTextView.setText(message);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            int widthInDp = 350;
            int heightInDp = 300;
            float density = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * density);
            int heightInPx = (int) (heightInDp * density);
            getDialog().getWindow().setLayout(widthInPx, heightInPx);
        }
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getDialog() != null) {
                    // Adaugă animația de fade-out
                    getDialog().getWindow().setWindowAnimations(android.R.style.Animation_Translucent);
                    getDialog().dismiss();
                }
            }
        }, 2000);
    }
}
