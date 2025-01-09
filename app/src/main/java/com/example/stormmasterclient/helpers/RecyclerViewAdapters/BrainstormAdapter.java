package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormEntity;

public class BrainstormAdapter extends ListAdapter<BrainstormEntity, BrainstormAdapter.BrainstormViewHolder> {

     public BrainstormAdapter() {
        super(DIFF_CALLBACK);
    }

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

    @NonNull
    @Override
    public BrainstormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brain_storm_list_item, parent, false);
        return new BrainstormViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrainstormViewHolder holder, int position) {
        BrainstormEntity currentBrainstorm = getItem(position);
        Log.d("BrainstormAdapter", "isCreator" + currentBrainstorm.isCreator());
        String isOwner = currentBrainstorm.isCreator() ? "Создатель" : "Участник";
        String title = currentBrainstorm.getTitle() + " · " + isOwner;
        holder.titleTextView.setText(title);
        holder.dateTextView.setText(currentBrainstorm.getCompletionDate());
    }

    class BrainstormViewHolder extends RecyclerView.ViewHolder {
        final private TextView titleTextView;
        final private TextView dateTextView;

        public BrainstormViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_brainstorm_title);
            dateTextView = itemView.findViewById(R.id.tv_brainstorm_date);
        }
    }
}

