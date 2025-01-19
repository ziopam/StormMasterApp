package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.TextWatchers.BrainstormNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Dialog for creating a brainstorm.
 */
public class CreateBrainstormDialog {

    private final Context context;

    /**
     * Constructor for CreateBrainstormDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public CreateBrainstormDialog(Context context) {
        this.context = context;
    }

    /**
     * Shows the create brainstorm dialog.
     */
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.create_brainstorm_dialog)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set up the close button
        MaterialButton closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }

        // Set up the brainstorm name TextInputLayout
        TextInputLayout brainstormNameLayout = dialog.findViewById(R.id.brainstormNameInputLayout);
        EditText brainstormName = dialog.findViewById(R.id.brainstormNameEditText);
        if (brainstormNameLayout != null && brainstormName != null) {
            brainstormName.addTextChangedListener(new BrainstormNameTextWatcher(brainstormNameLayout));
        }

        // Set up the default brainstorm type
        AutoCompleteTextView brainstormType = dialog.findViewById(R.id.brainstormTypeAutoCompleteTextView);
        if (brainstormType != null) {
            brainstormType.setText(context.getResources().getStringArray(R.array.brainstorm_types)[0], false);
        }
    }
}
