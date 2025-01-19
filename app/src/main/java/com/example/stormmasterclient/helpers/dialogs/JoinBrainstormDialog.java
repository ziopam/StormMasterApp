package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog for joining a brainstorm.
 */
public class JoinBrainstormDialog {

    private final Context context;

    /**
     * Constructor for JoinBrainstormDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public JoinBrainstormDialog(Context context) {
        this.context = context;
    }

    /**
     * Shows the join brainstorm dialog.
     */
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.join_brainstorm_dialog)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set up the close button
        MaterialButton closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }
    }
}
