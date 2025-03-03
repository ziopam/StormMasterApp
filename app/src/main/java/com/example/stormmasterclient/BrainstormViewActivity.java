package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;
import com.example.stormmasterclient.helpers.dialogs.DeleteBrainStormDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

/**
 * Activity for viewing a specific brainstorm.
 */
public class BrainstormViewActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     * @see DeleteBrainStormDialog
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brainstorm_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            // Set up the back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Initialize the TextView elements
        MaterialTextView titleTextView = findViewById(R.id.brainstormTitle);
        MaterialTextView dateTextView = findViewById(R.id.brainstormDate);
        MaterialTextView participantsTextView = findViewById(R.id.brainstormParticipants);
        MaterialTextView detailsTextView = findViewById(R.id.brainstormDetails);

        // Set the text of the TextView elements
        Intent intent = getIntent();
        titleTextView.setText(intent.getStringExtra("title"));
        dateTextView.setText(intent.getStringExtra("date"));
        participantsTextView.setText(intent.getStringExtra("participants"));
        String htmlText = intent.getStringExtra("details");
        htmlText = htmlText.replace("\n", "<br>");
        detailsTextView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));

        // Set up the delete button
        MaterialButton deleteButton = findViewById(R.id.deleteBrainstormButton);
        boolean isCreator = intent.getBooleanExtra("isCreator", false);
        if(!isCreator){
            // Hide the delete button if the user is not the creator of the brainstorm
            deleteButton.setVisibility(MaterialButton.GONE);
            LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
            buttonsLayout.setWeightSum(4); // Change for better appearance
        }

        // Set up the delete button click listener
        deleteButton.setOnClickListener(v -> {
            DeleteBrainStormDialog dialog = new DeleteBrainStormDialog(intent.getIntExtra("id", 0), this);
            dialog.show();
        });
    }

    /**
     * Called when an options item is selected.
     *
     * @param item The selected menu item.
     * @return true if the item is selected successfully.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}