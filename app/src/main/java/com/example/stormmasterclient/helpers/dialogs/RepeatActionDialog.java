package com.example.stormmasterclient.helpers.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Layout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog for repeating an action. Can return the user to the main menu.
 */
public class RepeatActionDialog {
    private final IRepeatable repeatable;
    private final Activity activity;
    private final ApiProblemsHandler problemHandler;
    private final WebSocketClient webSocketClient;

    /**
     * Constructor for RepeatActionDialog.
     *
     * @param repeatable The action to repeat.
     * @param activity The context in which the dialog should be displayed.
     */
    public RepeatActionDialog(IRepeatable repeatable, Activity activity, WebSocketClient webSocketClient) {
        this.repeatable = repeatable;
        this.activity = activity;
        this.problemHandler = new ApiProblemsHandler(activity);
        this.webSocketClient = webSocketClient;
    }

    /**
     * Shows the dialog.
     */
    @SuppressLint("WrongConstant")
    public void show() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setTitle("Ошибка")
                .setMessage("Похоже вы не подключены к интернету. Вы можете вернуться в главное меню " +
                        "или повторить попытку подключения. Если вы вернетесь в главное меню сейчас, " +
                        "то остальные участники мозгового штурма не увидят сделанные вами изменения.")

                .setNegativeButton("В главное меню", (dialogInterface, i) -> {
                    problemHandler.returnToMain();
                    webSocketClient.closeWebSocket();
                    dialogInterface.dismiss();
                })
                .setPositiveButton("Повторить", (dialogInterface, i) -> {
                    repeatable.action();
                    dialogInterface.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set justification mode for the dialog message for better readability
        TextView messageView = dialog.findViewById(android.R.id.message);
        assert messageView != null;
        messageView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
    }
}
