package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class RepeatPasswordTextWatcher implements TextWatcher {

    private final TextInputLayout passwordLayout;
    private final EditText passwordToCheckText;

    public RepeatPasswordTextWatcher(TextInputLayout passwordLayout, EditText passwordToCheckText) {
        this.passwordLayout = passwordLayout;
        this.passwordToCheckText = passwordToCheckText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.toString().equals(passwordToCheckText.getText().toString())) {
            passwordLayout.setError(null);
        } else {
            passwordLayout.setError("Пароли не совпадают");
        }
    }
}
