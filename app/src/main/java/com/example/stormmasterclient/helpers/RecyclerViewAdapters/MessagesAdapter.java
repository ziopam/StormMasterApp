package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.dialogs.SetIdeaDialog;
import com.google.android.material.textview.MaterialTextView;
import com.example.stormmasterclient.helpers.RoomDatabase.MessageEntity;


/**
 * An adapter for the messages RecyclerView.
 */
public class MessagesAdapter extends ListAdapter<MessageEntity, MessagesAdapter.MessageHolder> {

    private MessageEntity selectedMessage; // The message for the context menu

    /**
     * Constructor for BrainstormAdapter.
     */
    public MessagesAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil.ItemCallback for comparing items in the RecyclerView
    private static final DiffUtil.ItemCallback<MessageEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<MessageEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageEntity oldItem, @NonNull MessageEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageEntity oldItem, @NonNull MessageEntity newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getUsername().equals(newItem.getUsername()) &&
                    oldItem.getMessage().equals(newItem.getMessage()) &&
                    oldItem.getIsThisUser() == newItem.getIsThisUser() &&
                    oldItem.getIdeaNumber() == newItem.getIdeaNumber() && oldItem.getIdeaVotes() == newItem.getIdeaVotes();
        }
    };

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.message_layout, null);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageEntity message = getItem(position);
        holder.currentMessage = message;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.cardView.getContext(), R.layout.message_layout);

        Context context = holder.cardView.getContext();

        // If the message is from the current user, change the layout
        if(message.getIsThisUser()){
            // Hide the nickname
            holder.nicknameTextView.setVisibility(View.GONE);

            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.user_messages_color));

            // Move message to the right side
            constraintSet.clear(holder.cardView.getId(), ConstraintSet.START);
            constraintSet.connect(holder.cardView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END, 15);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.nicknameTextView.setVisibility(View.VISIBLE);
            holder.nicknameTextView.setText(message.getUsername());

            constraintSet.clear(holder.cardView.getId(), ConstraintSet.END);
            constraintSet.connect(holder.cardView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START, 15);

        }

        holder.messageTextView.setText(message.getMessage());

        // If the message is an idea, show the idea layout
        if(message.getIdeaNumber() != -1){
            holder.ideaLayout.setVisibility(View.VISIBLE);
            String ideaText = holder.ideaTextView.getContext().getString(R.string.idea_number_text, message.getIdeaNumber());
            holder.ideaTextView.setText(ideaText);
            holder.votesTextView.setText(String.valueOf(message.getIdeaVotes()));
        } else {
            holder.ideaLayout.setVisibility(View.GONE);
        }

        constraintSet.applyTo(holder.messageConstraintLayout);
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final ConstraintLayout messageConstraintLayout;
        private final CardView cardView;
        private final MaterialTextView nicknameTextView;
        private final MaterialTextView messageTextView;
        private final LinearLayout ideaLayout;
        private final MaterialTextView ideaTextView;
        private final MaterialTextView votesTextView;
        private MessageEntity currentMessage;


        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageConstraintLayout = itemView.findViewById(R.id.messageConstraintLayout);
            cardView = itemView.findViewById(R.id.messageCardView);
            nicknameTextView = itemView.findViewById(R.id.messageNickname);
            messageTextView = itemView.findViewById(R.id.messageText);
            ideaLayout = itemView.findViewById(R.id.ideaLayout);
            ideaTextView = itemView.findViewById(R.id.messageIdeaNumber);
            votesTextView = itemView.findViewById(R.id.messageVotes);


            itemView.setOnLongClickListener(v -> {
                selectedMessage = currentMessage; // Set the selected message for the context menu
                return false; // Return false to allow the context menu to be shown
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = ((android.app.Activity) v.getContext()).getMenuInflater();
            if (currentMessage.getIdeaNumber() != -1) {
                inflater.inflate(R.menu.idea_message_context_menu, menu);
            } else {
                inflater.inflate(R.menu.originary_message_context_menu, menu);
            }
        }
    }


    /**
     * Called when a context menu item is selected.
     *
     * @param item The selected menu item.
     * @param context The context in which the menu item was selected.
     * @param webSocketClient The WebSocketClient to set up the idea.
     * @return True if the menu item was handled, false otherwise.
     */
    public boolean onContextItemSelected(@NonNull MenuItem item, Context context, WebSocketClient webSocketClient) {
        if (selectedMessage == null) return false;

        if(item.getItemId() == R.id.make_idea_action){
            new SetIdeaDialog(context, webSocketClient ,selectedMessage).show();
            return true;
        } else if (item.getItemId() == R.id.remove_idea_action){
            webSocketClient.sendMessage("{\"type\": \"remove_idea\", \"message_id\": " + selectedMessage.getId() + "}");
            return true;
        } else if (item.getItemId() == R.id.vote_for_idea_action){
            webSocketClient.sendMessage("{\"type\": \"vote_for_idea\", \"idea_number\": " + selectedMessage.getIdeaNumber() + "}");
            return true;
        } else if (item.getItemId() == R.id.devote_for_idea_action){
            webSocketClient.sendMessage("{\"type\": \"devote_for_idea\", \"idea_number\": " + selectedMessage.getIdeaNumber() + "}");
            return true;
        }
        return false;
    }
}
