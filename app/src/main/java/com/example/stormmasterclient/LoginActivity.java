package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.stormmasterclient.helpers.API.ApiClient;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.userNameLoginEditText);
        EditText passwordEditText = findViewById(R.id.passwordLoginEditText);

        ApiClient apiClient = new ApiClient(LoginActivity.this);

        MaterialButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            apiClient.userLogin(username, password);
        });

        MaterialButton goToRegisterButton = findViewById(R.id.goToRegistrationButton);
        goToRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}