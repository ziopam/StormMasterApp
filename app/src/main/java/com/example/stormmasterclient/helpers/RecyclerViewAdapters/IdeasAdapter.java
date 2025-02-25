package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.IdeaViewActivity;
import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView that displays the ideas.
 */
public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeasViewHolder> {

    public List<Integer> ideaNumbers = new ArrayList<>();
    private final MessagesRepository messagesRepository;
    private final String roomDetails;

    /**
     * Constructor for IdeasAdapter.
     *
     * @param messagesRepository The repository for the messages database to get data about ideas.
     */
    public IdeasAdapter(MessagesRepository messagesRepository, String roomDetails) {
        this.messagesRepository = messagesRepository;
        this.roomDetails = roomDetails;
    }

    @Override
    public IdeasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.idea_item_layout, parent, false);
        return new IdeasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeasViewHolder holder, int position) {
        int ideaNumber = ideaNumbers.get(position);
        Intent intent = new Intent(holder.ideaCardView.getContext(), IdeaViewActivity.class);

        // Check if this is an idea or a theme
        if(ideaNumber != -1){
            String ideaNumberString = holder.ideaNumberTextView.getContext().getString(R.string.idea_number_text, ideaNumber);
            holder.ideaNumberTextView.setText(ideaNumberString);
            holder.ideaNumberTextView.setTypeface(holder.ideaNumberTextView.getTypeface(), Typeface.NORMAL);

            // Put data in the intent
            intent.putExtra("title", ideaNumberString);
            intent.putExtra("details", String.join("\n\n",
                    messagesRepository.getTextMessagesByIdeaNumber(ideaNumber)));
        } else{
            holder.ideaNumberTextView.setText(R.string.theme_text);
            holder.ideaNumberTextView.setTypeface(holder.ideaNumberTextView.getTypeface(), Typeface.BOLD);

            // Put data in the intent
            String themeText = holder.ideaNumberTextView.getContext().getString(R.string.theme_text);
            intent.putExtra("title", themeText);
            intent.putExtra("details", roomDetails);
            intent.putExtra("isTitleBold", false);
        }

        // Open the IdeaViewActivity when the card is clicked
        holder.ideaCardView.setOnClickListener(v -> {
            holder.ideaCardView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ideaNumbers.size();
    }

    public class IdeasViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView ideaNumberTextView;
        CardView ideaCardView;
        public IdeasViewHolder(View itemView) {
            super(itemView);
            ideaNumberTextView = itemView.findViewById(R.id.ideaNumberTextView);
            ideaCardView = itemView.findViewById(R.id.ideaCardView);
        }
    }
}
