package com.example.splititapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.splititapp.Accounts;
import com.example.splititapp.HistoryActivity;
import com.example.splititapp.PrivacyAndSupport;
import com.example.splititapp.R;

public class SettingsPage extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settingspage, container, false);

        Button btnAccount = view.findViewById(R.id.button2);
        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Accounts.class);
            startActivity(intent);
        });

        Button btnPrivacy = view.findViewById(R.id.button7);
        btnPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PrivacyAndSupport.class);
            startActivity(intent);
        });

        Button btnHistory = view.findViewById(R.id.btnHistory);
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), HistoryActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }
}