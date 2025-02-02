package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/**
 * TextWatcher for validating repeated password input.
 */
public class RepeatPasswordTextWatcher implements TextWatcher {

    private final TextInputLayout passwordLayout;
    private final EditText passwordToCheckText;

    /**
     * Constructor for RepeatPasswordTextWatcher.
     *
     * @param passwordLayout The TextInputLayout to be watched. Error message will be displayed here.
     * @param passwordToCheckText The EditText containing the original password to check against.
     */
    public RepeatPasswordTextWatcher(TextInputLayout passwordLayout, EditText passwordToCheckText) {
        this.passwordLayout = passwordLayout;
        this.passwordToCheckText = passwordToCheckText;
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
     * Called after the text has been changed. Checks if the repeated password matches the original password.
     *
     * @param editable The editable text.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.toString().equals(passwordToCheckText.getText().toString())) {
            passwordLayout.setError(null);
        } else {
            passwordLayout.setError("Пароли не совпадают");
        }
    }
}
