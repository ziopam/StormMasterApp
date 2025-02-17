package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.RoomDatabase.MessageEntity;
import com.example.stormmasterclient.helpers.TextWatchers.IdeaNumberTextWatcher;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Dialog for setting an idea.
 *
 */
public class SetIdeaDialog {
    private final Context context;
    private TextInputLayout ideaLayout;
    private EditText ideaEditText;
    private MaterialButton setIdeaButton;

    private final WebSocketClient webSocketClient;
    private final MessageEntity message;

    /**
     * Constructor for JoinBrainstormDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public SetIdeaDialog(Context context, WebSocketClient webSocketClient, MessageEntity message) {
        this.context = context;
        this.webSocketClient = webSocketClient;
        this.message = message;
    }

    /**
     * Shows the join brainstorm dialog.
     */
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.set_idea_dialog)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        ideaLayout = dialog.findViewById(R.id.ideaNumberInputLayout);
        ideaEditText = dialog.findViewById(R.id.ideaNumberEditText);
        setIdeaButton = dialog.findViewById(R.id.setIdeaButton);

        if (ideaLayout != null && ideaEditText != null && setIdeaButton != null) {
            ideaEditText.addTextChangedListener(new IdeaNumberTextWatcher(ideaLayout));

            setIdeaButton.setOnClickListener(v -> { setIdea(dialog);});
            ideaEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setIdea(dialog);
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Sets the idea.
     *
     * @param dialog The dialog which is going to be closed after successful idea setting.
     */
    private void setIdea(AlertDialog dialog){
        String idea = ideaEditText.getText().toString();
        if (!idea.isEmpty() && ideaLayout.getError() == null) {
            webSocketClient.sendMessage("{\"type\": \"set_idea\", \"message_id\": " + message.getId() +
                    ", \"idea_number\": " + idea + "}");
            dialog.dismiss();
        } else {
            Toast.makeText(context, "Проверьте корректность ввода", Toast.LENGTH_SHORT).show();
        }
    }
}
