package com.example.stormmasterclient.helpers.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Layout;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class FinishBrainstormDialog {
    private final Activity activity;
    private final String roomCode;
    private final WebSocketClient webSocketClient;
    private final ApiRoomClient apiRoomClient;

    /**
     * Constructor for FinishBrainstormDialog.
     *
     * @param roomCode The code of the room to delete.
     * @param webSocketClient The WebSocketClient to close after deleting the room.
     * @param activity The activity in which the dialog should be displayed.
     */
    public FinishBrainstormDialog(String roomCode, WebSocketClient webSocketClient, ApiRoomClient apiRoomClient,
                            Activity activity)
    {
        this.roomCode = roomCode;
        this.webSocketClient = webSocketClient;
        this.activity = activity;
        this.apiRoomClient = apiRoomClient;
    }

    /**
     * Shows the finish brainstorm dialog.
     */
    @SuppressLint("WrongConstant")
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setTitle("Завершить мозговой штурм")
                .setMessage("Вы уверены, что хотите завершить мозговой штурм?")

                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Завершить", (dialogInterface, i) -> {
                    apiRoomClient.finishBrainStorm(roomCode, activity, webSocketClient);
                    dialogInterface.dismiss();
                })

                .setIcon(R.drawable.brainstorm_icon)
                .setCancelable(true);
        AlertDialog dialog = builder.show();

        // Set justification mode for the dialog message for better readability
        TextView messageView = dialog.findViewById(android.R.id.message);
        assert messageView != null;
        messageView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        // Set the color of the cancel button to red for better visibility
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(activity.getResources().getColor(R.color.beautiful_red, null));
    }
}
