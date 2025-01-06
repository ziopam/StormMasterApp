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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout usernameLayout = findViewById(R.id.userNameLoginView);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLoginView);
        EditText usernameEditText = findViewById(R.id.userNameLoginEditText);
        EditText passwordEditText = findViewById(R.id.passwordLoginEditText);

        usernameEditText.addTextChangedListener(new EmptyTextWatcher(usernameLayout));
        passwordEditText.addTextChangedListener(new EmptyTextWatcher(passwordLayout));

        ApiClient apiClient = new ApiClient(LoginActivity.this);

        MaterialButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
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

        MaterialButton goToRegisterButton = findViewById(R.id.goToRegistrationButton);
        goToRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}