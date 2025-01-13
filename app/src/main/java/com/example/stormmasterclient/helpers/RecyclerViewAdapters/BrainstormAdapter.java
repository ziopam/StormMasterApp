package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.BrainstormViewActivity;
import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormEntity;

/**
 * Adapter for displaying brainstorms in a RecyclerView.
 */
public class BrainstormAdapter extends ListAdapter<BrainstormEntity, BrainstormAdapter.BrainstormViewHolder> {

    /**
     * Constructor for BrainstormAdapter.
     */
     public BrainstormAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil.ItemCallback for comparing items in the RecyclerView
    private static final DiffUtil.ItemCallback<BrainstormEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<BrainstormEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull BrainstormEntity oldItem, @NonNull BrainstormEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull BrainstormEntity oldItem, @NonNull BrainstormEntity newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getCompletionDate().equals(newItem.getCompletionDate()) &&
                    oldItem.isCreator() == newItem.isCreator();
        }
    };

    /**
     * Creates a new ViewHolder for a brainstorm item.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new BrainstormViewHolder.
     */
    @NonNull
    @Override
    public BrainstormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brain_storm_list_item, parent, false);
        return new BrainstormViewHolder(itemView);
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull BrainstormViewHolder holder, int position) {
        // Fill the ViewHolder with the data
        BrainstormEntity currentBrainstorm = getItem(position);
        String isOwner = currentBrainstorm.isCreator() ? "Создатель" : "Участник";
        String title = currentBrainstorm.getTitle() + " · " + isOwner;
        holder.titleTextView.setText(title);
        String formattedDate = formatDate(currentBrainstorm.getCompletionDate());
        holder.dateTextView.setText(formattedDate);

        // Process the click on the item
        holder.itemView.setOnClickListener(v -> {
            // Start the BrainstormViewActivity with the data of the clicked brainstorm
            Intent intent = new Intent(v.getContext(), BrainstormViewActivity.class);
            intent.putExtra("id", currentBrainstorm.getId());
            intent.putExtra("title", currentBrainstorm.getTitle());
            intent.putExtra("date", formattedDate);
            intent.putExtra("participants", currentBrainstorm.getParticipants());
            intent.putExtra("details", currentBrainstorm.getDetails());
            intent.putExtra("isCreator", currentBrainstorm.isCreator());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Formats a date string from YYYY-MM-DD to DD.MM.YYYY.
     *
     * @param originalDate The original date string.
     * @return The formatted date string.
     */
    private String formatDate(String originalDate) {
        if (originalDate == null || originalDate.isEmpty()) {
            return "";
        }
        String[] parts = originalDate.split("-");
        if (parts.length == 3) {
            return parts[2] + "." + parts[1] + "." + parts[0]; // DD.MM.YYYY
        }
        return originalDate;
    }

    /**
     * ViewHolder for a brainstorm item.
     */
    class BrainstormViewHolder extends RecyclerView.ViewHolder {
        final private TextView titleTextView;
        final private TextView dateTextView;

        /**
         * Constructor for BrainstormViewHolder.
         *
         * @param itemView The view of the item.
         */
        public BrainstormViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_brainstorm_title);
            dateTextView = itemView.findViewById(R.id.tv_brainstorm_date);
        }
    }
}

