package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog for joining a brainstorm.
 *
 * @see ApiRoomClient
 */
public class JoinBrainstormDialog {

    private final Context context;
    private final ApiRoomClient apiRoomClient;

    /**
     * Constructor for JoinBrainstormDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public JoinBrainstormDialog(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        this.apiRoomClient = new ApiRoomClient(context, token);
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

        // Set up the join button
        MaterialButton joinButton = dialog.findViewById(R.id.joinButton);
        EditText roomCode = dialog.findViewById(R.id.roomCodeEditText);
        if (joinButton != null && roomCode != null){
            joinButton.setOnClickListener(v -> {
                String roomCodeText = roomCode.getText().toString();

                // Check if the room code is not empty to avoid error 400 in response
                if(!roomCodeText.isEmpty()){
                    apiRoomClient.joinRoom(roomCodeText);
                } else {
                    Toast.makeText(context, "Введите код комнаты", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
