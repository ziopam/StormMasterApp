package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * TextWatcher for checking if a text field is empty.
 */
public class EmptyTextWatcher implements TextWatcher {
    private final TextInputLayout textInputLayout;

    /**
     * Constructor for EmptyTextWatcher.
     *
     * @param textInputLayout The TextInputLayout to be watched. Error message will be displayed here.
     */
    public EmptyTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
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
     * Called after the text has been changed. Checks if the text is empty.
     *
     * @param editable The editable text.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        if (text.isEmpty()) {
            textInputLayout.setError("Поле не может быть пустым");
            return;
        }
        textInputLayout.setError(null);
    }
}
