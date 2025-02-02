package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.TextWatchers.BrainstormNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Dialog for creating a brainstorm.
 *
 * @see BrainstormNameTextWatcher
 * @see ApiRoomClient
 */
public class CreateBrainstormDialog {

    private final Context context;
    private final ApiRoomClient apiRoomClient;

    /**
     * Constructor for CreateBrainstormDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public CreateBrainstormDialog(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        this.apiRoomClient = new ApiRoomClient(context, token);
    }

    /**
     * Shows the create brainstorm dialog.
     *
     * @see BrainstormNameTextWatcher
     * @see com.example.stormmasterclient.helpers.API.ApiRoomClient
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

        // Set up the create button
        MaterialButton createButton = dialog.findViewById(R.id.createRoomButton);
        if(createButton != null){
            createButton.setOnClickListener(v -> {
                // Check if the brainstorm name is valid
                if(brainstormNameLayout != null && brainstormNameLayout.getError() != null){
                    Toast.makeText(context, "Проверьте правильность введенных данных",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Send request to create a room if name is valid
                    if (brainstormName != null) {
                        apiRoomClient.createRoom(brainstormName.getText().toString());
                    }
                    dialog.dismiss();
                }
            });
        }
    }
}
