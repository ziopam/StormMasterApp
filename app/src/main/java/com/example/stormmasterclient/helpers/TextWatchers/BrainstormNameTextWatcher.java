package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * TextWatcher for brainstorm name validation.
 */
public class BrainstormNameTextWatcher implements TextWatcher {

    private final TextInputLayout brainstormNameLayout;

    /**
     * Constructor for PasswordTextWatcher.
     *
     * @param layout The TextInputLayout to be watched. Error message will be displayed here.
     */
    public BrainstormNameTextWatcher(TextInputLayout layout) {
        this.brainstormNameLayout = layout;
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
     * Called after the text has been changed. Checks if the brainstorm name is valid.
     *
     * @param editable The editable text.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String brainstormName = editable.toString();
        if (brainstormName.trim().isEmpty()) {
            brainstormNameLayout.setError("Название не может состоять только из пробелов");
            return;
        }
        if (brainstormName.isEmpty()) {
            brainstormNameLayout.setError("Поле не может быть пустым");
            return;
        }
        if (brainstormName.length() > 25) {
            brainstormNameLayout.setError("Должно быть не более 25 символов");
            return;
        }
        if(!brainstormName.matches("[a-zA-Z\\dа-яA-я\\s]+")) {
            brainstormNameLayout.setError("Допустима только латиница, кириллица, арабские цифры и пробел");
            return;
        }
        brainstormNameLayout.setError(null);
    }
}
