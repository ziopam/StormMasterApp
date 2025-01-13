package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiClient;
import com.example.stormmasterclient.helpers.TextWatchers.PasswordTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.RepeatPasswordTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.UserNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for user registration.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise it is null.
     * @see ApiClient
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize and set up TextInput UI elements
        TextInputLayout usernameLayout = findViewById(R.id.userNameRegView);
        EditText usernameEditText = findViewById(R.id.userNameRegEditText);
        usernameEditText.addTextChangedListener(new UserNameTextWatcher(usernameLayout));

        TextInputLayout passwordLayout = findViewById(R.id.passwordRegView);
        EditText passwordEditText = findViewById(R.id.passwordRegEditText);
        passwordEditText.addTextChangedListener(new PasswordTextWatcher(passwordLayout));

        TextInputLayout repeatPasswordLayout = findViewById(R.id.repeatPasswordRegView);
        EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        repeatPasswordEditText.addTextChangedListener(new RepeatPasswordTextWatcher(repeatPasswordLayout,
                passwordEditText));

        // Create an instance of ApiClient for user registration
        ApiClient apiClient = new ApiClient(RegisterActivity.this);

        // Set up the registration button
        MaterialButton registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            // Check if all fields are filled correctly
            if(usernameLayout.getError() == null && passwordLayout.getError() == null
                    && repeatPasswordLayout.getError() == null) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                apiClient.userRegistration(username, password);
            } else {
                Toast.makeText(RegisterActivity.this, "Проверьте правильность введенных данных",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the button to go to the login screen
        MaterialButton goToLoginButton = findViewById(R.id.goToLoginButton);
        goToLoginButton.setOnClickListener(view -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }
}