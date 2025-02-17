package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * TextWatcher for idea number validation. Checks if the idea number is integer in range [1, 1000].
 */
public class IdeaNumberTextWatcher implements TextWatcher {
    private final TextInputLayout ideaLayout;

    /**
     * Constructor for PasswordTextWatcher.
     *
     * @param layout The TextInputLayout to be watched. Error message will be displayed here.
     */
    public IdeaNumberTextWatcher(TextInputLayout layout) {
        this.ideaLayout = layout;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // No action needed while text is being changed
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // No action needed while text is being changed
    }

    /**
     * Called after the text has been changed. Checks if the idea number is integer in range [1, 1000].
     *
     * @param editable The editable text.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        if (text.isEmpty()) {
            ideaLayout.setError("Поле не может быть пустым");
            return;
        }

        try {
            int number = Integer.parseInt(text);
            if (number < 1 || number > 1000) {
                ideaLayout.setError("Номер должен быть в диапазоне от 1 до 1000");
            } else {
                ideaLayout.setError(null);
            }
        } catch (NumberFormatException e) {
            ideaLayout.setError("Должно быть целое число");
        }
    }
}
