package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

/**
 * Activity for viewing a specific idea.
 */
public class IdeaViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            // Set up the back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Get the title, details, and isTitleBold from the intent
        String title = getIntent().getStringExtra("title");
        String details = getIntent().getStringExtra("details");
        boolean isTitleBold = getIntent().getBooleanExtra("isTitleBold", false);

        MaterialTextView titleTextView = findViewById(R.id.titleTextView);
        MaterialTextView detailsTextView = findViewById(R.id.detailsTextView);

        titleTextView.setText(title);
        detailsTextView.setText(details);

        // Set the title text to bold if necessary
        if(isTitleBold){
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
        }

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