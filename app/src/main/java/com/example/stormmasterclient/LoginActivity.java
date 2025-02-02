package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiClient;
import com.example.stormmasterclient.helpers.TextWatchers.EmptyTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.UserNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for user login.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     * @see ApiClient
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize and set up TextInput UI elements
        TextInputLayout usernameLayout = findViewById(R.id.userNameLoginView);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLoginView);
        EditText usernameEditText = findViewById(R.id.userNameLoginEditText);
        EditText passwordEditText = findViewById(R.id.passwordLoginEditText);

        usernameEditText.addTextChangedListener(new EmptyTextWatcher(usernameLayout));
        passwordEditText.addTextChangedListener(new EmptyTextWatcher(passwordLayout));

        // Create an instance of ApiClient for user login
        ApiClient apiClient = new ApiClient(LoginActivity.this);

        // Set up the login button
        MaterialButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Check for errors in input fields
            if(usernameLayout.getError() == null && passwordLayout.getError() == null &&
                    !usernameEditText.getText().toString().isEmpty() &&
                    !passwordEditText.getText().toString().isEmpty()) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                apiClient.userLogin(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Проверьте правильность введенных данных",
                        Toast.LENGTH_SHORT).show();
            }

        });

        // Set up the button to go to the registration screen
        MaterialButton goToRegisterButton = findViewById(R.id.goToRegistrationButton);
        goToRegisterButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}