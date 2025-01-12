package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.textview.MaterialTextView;


public class BrainstormViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brainstorm_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        Intent intent = getIntent();

        MaterialTextView titleTextView = findViewById(R.id.brainstormTitle);
        MaterialTextView dateTextView = findViewById(R.id.brainstormDate);
        MaterialTextView participantsTextView = findViewById(R.id.brainstormParticipants);
        MaterialTextView detailsTextView = findViewById(R.id.brainstormDetails);

        titleTextView.setText(intent.getStringExtra("title"));
        dateTextView.setText(intent.getStringExtra("date"));
        participantsTextView.setText(intent.getStringExtra("participants"));
        detailsTextView.setText(intent.getStringExtra("details"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}