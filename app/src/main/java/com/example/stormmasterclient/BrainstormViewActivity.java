package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;
import com.example.stormmasterclient.helpers.dialogs.DeleteBrainStormDialog;
import com.google.android.material.button.MaterialButton;
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

        MaterialButton deleteButton = findViewById(R.id.deleteBrainstormButton);
        boolean isCreator = intent.getBooleanExtra("isCreator", false);
        if(!isCreator){
            deleteButton.setVisibility(MaterialButton.GONE);
            LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
            buttonsLayout.setWeightSum(4);
        }

        deleteButton.setOnClickListener(v -> {
            DeleteBrainStormDialog dialog = new DeleteBrainStormDialog(intent.getIntExtra("id", 0), this);
            dialog.show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}