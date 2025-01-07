package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiAuthorizedClient;
import com.example.stormmasterclient.helpers.API.ApiClient;
import com.example.stormmasterclient.helpers.TextWatchers.EmptyTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.PasswordTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.RepeatPasswordTextWatcher;
import com.example.stormmasterclient.helpers.TextWatchers.UserNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        TextInputLayout oldPasswordLayout = findViewById(R.id.oldPasswordView);
        EditText oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        oldPasswordEditText.addTextChangedListener(new EmptyTextWatcher(oldPasswordLayout));

        TextInputLayout newPasswordLayout = findViewById(R.id.newPasswordView);
        EditText newPasswordEditText = findViewById(R.id.newPasswordEditText);
        newPasswordEditText.addTextChangedListener(new PasswordTextWatcher(newPasswordLayout));

        TextInputLayout repeatPasswordLayout = findViewById(R.id.repeatPasswordView);
        EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        repeatPasswordEditText.addTextChangedListener(new RepeatPasswordTextWatcher(repeatPasswordLayout,
                newPasswordEditText));

        ApiAuthorizedClient apiClient = new ApiAuthorizedClient(ChangePasswordActivity.this,
                getSharedPreferences("USER_DATA", 0).getString("token", ""));

        MaterialButton changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(view -> {
            if(oldPasswordLayout.getError() == null && newPasswordLayout.getError() == null &&
                    repeatPasswordLayout.getError() == null) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                apiClient.userChangePassword(oldPassword, newPassword, this);
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Проверьте правильность введенных данных",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}