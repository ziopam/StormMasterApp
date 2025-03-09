package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.RecyclerViewAdapters.IdeasAdapter;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Dialog for showing ideas and theme.
 */
public class ShowIdeasAndThemeDialog {

    private final Context context;
    private final String roomDetails;
    private final MessagesRepository messagesRepository;
    private final IdeasAdapter ideasAdapter;

    /**
     * Constructor for ShowIdeasAndThemeDialog.
     *
     * @param context The context in which the dialog should be displayed.
     * @param roomDetails The details of the room (its theme).
     * @param messagesRepository The repository for the messages database.
     */
    public ShowIdeasAndThemeDialog(Context context, String roomDetails, MessagesRepository messagesRepository) {
        this.context = context;
        this.roomDetails = roomDetails;
        this.messagesRepository = messagesRepository;
        this.ideasAdapter = new IdeasAdapter(messagesRepository, roomDetails);
    }

    /**
     * Shows the ideas and theme dialog if there is something to show. Otherwise, shows a toast.
     */
    public void show(){
        ArrayList<Integer> ideaNumbers = (ArrayList<Integer>) messagesRepository.getIdeaNumbers();

        // If there are no ideas and no room details, show a toast
        if(ideaNumbers.isEmpty() && (roomDetails.equals("")|| roomDetails.isEmpty())){
            Toast.makeText(context, "Нет информации для демонстрации", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!roomDetails.equals("")) {
            ideaNumbers.add(0, -1);
        }

        // Create the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.show_ideas_dialog)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        RecyclerView ideasRecyclerView = dialog.findViewById(R.id.ideasRecyclerView);
        if (ideasRecyclerView != null) {
            ideasAdapter.ideaNumbers = ideaNumbers;
            ideasRecyclerView.setAdapter(ideasAdapter);
            ideasRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        // Set up the close button
        MaterialButton closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }

    }
}
