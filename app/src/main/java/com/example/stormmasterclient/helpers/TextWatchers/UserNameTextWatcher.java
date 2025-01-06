package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class UserNameTextWatcher implements TextWatcher {
    private final TextInputLayout userNameLayout;

    public UserNameTextWatcher(TextInputLayout userNameLayout) {
        this.userNameLayout = userNameLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String username = editable.toString();
        if (username.isEmpty()) {
            userNameLayout.setError("Поле не может быть пустым");
            return;
        }
        if (username.length() < 4) {
            userNameLayout.setError("Должно быть не менее 4 символов");
            return;
        }
        if (username.length() > 12) {
            userNameLayout.setError("Должно быть не более 12 символов");
            return;
        }
        if (!username.matches("[a-zA-Z0-9_]+")) {
            userNameLayout.setError("Допустимы только латинские буквы, цифры и символ _");
            return;
        }
        userNameLayout.setError(null);
    }
}