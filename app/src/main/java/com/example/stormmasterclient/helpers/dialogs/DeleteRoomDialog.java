package com.example.stormmasterclient.helpers.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.Layout;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog for deleting a room.
 */
public class DeleteRoomDialog {

    private final Activity activity;
    private final String roomCode;
    private final WebSocketClient webSocketClient;
    private final ApiRoomClient apiRoomClient;

    /**
     * Constructor for DeleteBrainStormDialog.
     *
     * @param roomCode The code of the room to delete.
     * @param webSocketClient The WebSocketClient to close after deleting the room.
     * @param activity The activity in which the dialog should be displayed.
     */
    public DeleteRoomDialog(String roomCode, WebSocketClient webSocketClient, ApiRoomClient apiRoomClient,
                            Activity activity)
    {
        this.roomCode = roomCode;
        this.webSocketClient = webSocketClient;
        this.activity = activity;
        this.apiRoomClient = apiRoomClient;
    }

    /**
     * Shows the delete room dialog.
     */
    @SuppressLint("WrongConstant")
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setTitle("Удалить комнату")
                .setMessage("Вы уверены, что хотите удалить эту комнату?")

                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Удалить", (dialogInterface, i) -> {
                    apiRoomClient.deleteRoom(roomCode, webSocketClient, activity);
                    dialogInterface.dismiss();
                })

                .setIcon(R.drawable.delete_icon)
                .setCancelable(true);
        AlertDialog dialog = builder.show();

        // Set justification mode for the dialog message for better readability
        TextView messageView = dialog.findViewById(android.R.id.message);
        assert messageView != null;
        messageView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        // Set the color of the delete button to red for better visibility
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(activity.getResources().getColor(R.color.beautiful_red, null));
    }
}
