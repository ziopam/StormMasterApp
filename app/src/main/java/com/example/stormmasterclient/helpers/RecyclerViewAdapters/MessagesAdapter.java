package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for the messages RecyclerView.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder>{
    public List<MessageEntity> messagesList = new ArrayList<>();

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.message_layout, null);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageEntity message = messagesList.get(position);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.messageConstraintLayout);

        // If the message is from the current user, change the layout
        if(message.isThisUser){
            // Hide the nickname
            holder.nicknameTextView.setVisibility(View.GONE);

            // Move message to the right side
            constraintSet.clear(holder.cardView.getId(), ConstraintSet.START);
            constraintSet.connect(holder.cardView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END, 15);
        } else {
            holder.nicknameTextView.setText(message.username);
        }

        holder.messageTextView.setText(message.message);

        // If the message is an idea, show the idea layout
        if(message.isIdea){
            holder.ideaLayout.setVisibility(View.VISIBLE);
        } else {
            holder.ideaLayout.setVisibility(View.GONE);
        }

        constraintSet.applyTo(holder.messageConstraintLayout);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
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
