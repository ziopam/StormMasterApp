package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.R;
import com.google.android.material.textview.MaterialTextView;
import com.example.stormmasterclient.helpers.RoomDatabase.MessageEntity;


/**
 * An adapter for the messages RecyclerView.
 */
public class MessagesAdapter extends ListAdapter<MessageEntity, MessagesAdapter.MessageHolder> {

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
                    oldItem.getIdeaNumber() == newItem.getIdeaNumber();
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

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.cardView.getContext(), R.layout.message_layout);

        // If the message is from the current user, change the layout
        if(message.getIsThisUser()){
            // Hide the nickname
            holder.nicknameTextView.setVisibility(View.GONE);
            Log.d("MessagesAdapter", "onBindViewHolder: " + message.getUsername() + " " + message.getMessage()
             + " " + message.getIsThisUser() + " " + message.getIdeaNumber());

            // Move message to the right side
            constraintSet.clear(holder.cardView.getId(), ConstraintSet.START);
            constraintSet.connect(holder.cardView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END, 15);
        } else {
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
        } else {
            holder.ideaLayout.setVisibility(View.GONE);
        }

        constraintSet.applyTo(holder.messageConstraintLayout);
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout messageConstraintLayout;
        private final CardView cardView;
        private final MaterialTextView nicknameTextView;
        private final MaterialTextView messageTextView;
        private final LinearLayout ideaLayout;
        private final MaterialTextView ideaTextView;
        private final MaterialTextView votesTextView;


        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageConstraintLayout = itemView.findViewById(R.id.messageConstraintLayout);
            cardView = itemView.findViewById(R.id.messageCardView);
            nicknameTextView = itemView.findViewById(R.id.messageNickname);
            messageTextView = itemView.findViewById(R.id.messageText);
            ideaLayout = itemView.findViewById(R.id.ideaLayout);
            ideaTextView = itemView.findViewById(R.id.messageIdeaNumber);
            votesTextView = itemView.findViewById(R.id.messageVotes);
        }
    }
}
