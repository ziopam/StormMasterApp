package com.example.stormmasterclient.helpers.TextWatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A class that represents a text watcher for the message input.
 */
public class MessageTextWatcher implements TextWatcher {
    private FloatingActionButton sendButton;
    private MaterialCardView messageInputCardView;

    public MessageTextWatcher(FloatingActionButton sendButton, MaterialCardView messageInputCardView){
        this.sendButton = sendButton;
        this.messageInputCardView = messageInputCardView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        sendButton.setEnabled(s.toString().trim().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

        // Fix the height of the message input card view
        if (editable.toString().trim().isEmpty()) {
            messageInputCardView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            messageInputCardView.requestLayout();
        }
    }
}
