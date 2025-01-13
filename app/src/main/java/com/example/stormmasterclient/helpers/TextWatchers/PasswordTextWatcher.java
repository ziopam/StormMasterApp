package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * TextWatcher for validating password input.
 */
public class PasswordTextWatcher  implements TextWatcher {

    private final TextInputLayout passwordLayout;

    /**
     * Constructor for PasswordTextWatcher.
     *
     * @param passwordLayout The TextInputLayout to be watched. Error message will be displayed here.
     */
    public PasswordTextWatcher(TextInputLayout passwordLayout) {
        this.passwordLayout = passwordLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // No action needed before text is changed
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // No action needed while text is being changed
    }

    /**
     * Called after the text has been changed. Checks if the password is valid.
     *
     * @param editable The editable text.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String password = editable.toString();
        if (password.isEmpty()) {
            passwordLayout.setError("Поле не может быть пустым");
            return;
        }
        if (password.length() < 8) {
            passwordLayout.setError("Должно быть не менее 8 символов");
            return;
        }
        if (password.length() > 20) {
            passwordLayout.setError("Должно быть не более 20 символов");
            return;
        }
        if (!password.matches("[a-zA-Z0-9!@#$%^&*()\\-_+=;:,./?\\\\|`~\\[\\]{}]+")) {
            passwordLayout.setError("Допустимы только латинские буквы, цифры и специальные символы");
            return;
        }
        passwordLayout.setError(null);
    }
}
