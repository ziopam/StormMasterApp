package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiClient;
import com.example.stormmasterclient.helpers.PasswordTextWatcher;
import com.example.stormmasterclient.helpers.RepeatPasswordTextWatcher;
import com.example.stormmasterclient.helpers.UserNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        ApiClient apiClient = new ApiClient(RegisterActivity.this);

        MaterialButton registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
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

    }


}