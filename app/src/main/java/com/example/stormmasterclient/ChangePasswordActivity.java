package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
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

/**
 * Activity for changing user password.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     * @see ApiAuthorizedClient
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Назад");
        }

        // Initialize and set up TextInput UI elements
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

        // Create an instance of ApiAuthorizedClient for changing user password
        ApiAuthorizedClient apiClient = new ApiAuthorizedClient(ChangePasswordActivity.this,
                getSharedPreferences("USER_DATA", 0).getString("token", ""));

        // Set up the change password button
        MaterialButton changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(view -> {
            // Check if all fields are filled correctly
            if(oldPasswordLayout.getError() == null && newPasswordLayout.getError() == null &&
                    repeatPasswordLayout.getError() == null && !oldPasswordEditText.getText().toString().isEmpty()
                    && !newPasswordEditText.getText().toString().isEmpty() && !repeatPasswordEditText.
                    getText().toString().isEmpty()){
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                apiClient.userChangePassword(oldPassword, newPassword, this);
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Проверьте правильность введенных данных",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when an options item is selected.
     *
     * @param item The selected menu item.
     * @return true if the item is selected successfully.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}