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

/**
 * Fragment for displaying user profile.
 */
public class ProfileFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
    }

    /**
     * Called when the fragment is created.
     *
     * @param savedInstanceState If the fragment is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up transitions (animations) for the fragment
        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        setEnterTransition(materialFadeThrough);
        setExitTransition(materialFadeThrough);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *                  attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but
     * before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the username from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("USER_DATA", 0);
        String username = preferences.getString("username", "Никнейм");

        // If the user is not authorized, log them out
        if(username.equals("Никнейм")){
            Toast.makeText(requireContext(), "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            LoggerOut loggerOut = new LoggerOut(requireContext());
            loggerOut.logOut();
            return;
        }

        // Set the username in the TextView
        TextView usernameView = view.findViewById(R.id.userNameTextView);
        usernameView.setText(username);

        // Set up the change password button
        MaterialButton changePasswordButton = view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(v -> {
            // Navigate to ChangePasswordActivity
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Set up the log out button
        MaterialButton logOutButton = view.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(v -> {
            new ExitDialog(requireContext()).show();
        });
    }
}