package com.example.stormmasterclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.dialogs.ExitDialog;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.MaterialFadeThrough;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        setEnterTransition(materialFadeThrough);
        setExitTransition(materialFadeThrough);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = requireContext().getSharedPreferences("USER_DATA", 0);
        String username = preferences.getString("username", "Никнейм");

        if(username.equals("Никнейм")){
            Toast.makeText(requireContext(), "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            LoggerOut loggerOut = new LoggerOut(requireContext());
            loggerOut.logOut();
            return;
        }

        TextView usernameView = view.findViewById(R.id.userNameTextView);
        usernameView.setText(username);

        MaterialButton changePasswordButton = view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        MaterialButton logOutButton = view.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(v -> {
            new ExitDialog(requireContext()).show();
        });
    }
}