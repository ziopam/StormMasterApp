package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordTextWatcher  implements TextWatcher {

    private final TextInputLayout passwordLayout;

    public PasswordTextWatcher(TextInputLayout passwordLayout) {
        this.passwordLayout = passwordLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

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
