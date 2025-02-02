package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiClient;
import com.example.stormmasterclient.helpers.dialogs.CreateBrainstormDialog;
import com.example.stormmasterclient.helpers.dialogs.JoinBrainstormDialog;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

/**
 * Main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise it is null.
     * @see ApiClient
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is authorized (here only existence of token is checked)
        SharedPreferences preferences = getSharedPreferences("USER_DATA", 0);
        String token = preferences.getString("token", "null");
        if (token.equals("null")) {
            LoggerOut loggerOut = new LoggerOut(this);
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            loggerOut.logOut();
            return;
        }

        // Set the layout of the activity (since user is authorized)
        setContentView(R.layout.activity_main);

        // Initialize and set up navigation elements
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationRailView navigationRail = findViewById(R.id.navigationRail);

        // Use element that is not null for this layout
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(this::setupNavigation);
        } else if (navigationRail != null) {
            navigationRail.setOnItemSelectedListener(this::setupNavigation);
        }
    }

    /**
     * Sets up navigation based on the selected item.
     *
     * @param item The selected menu item.
     * @return true if the navigation item is selected successfully.
     */
    private boolean setupNavigation(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Process the selected item
        int itemId = item.getItemId();
        if (itemId == R.id.history){
            selectedFragment = getSupportFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName());
            if (selectedFragment == null) {
                selectedFragment = new HistoryFragment();
            }
        } else if (itemId == R.id.brain_storm_create) {
            new CreateBrainstormDialog(this).show();
        } else if (itemId == R.id.brain_storm_join) {
            new JoinBrainstormDialog(this).show();
        } else if (itemId == R.id.profile) {
            selectedFragment = getSupportFragmentManager().findFragmentByTag(ProfileFragment.class.getSimpleName());
            if (selectedFragment == null) {
                selectedFragment = new ProfileFragment();
            }
        }

        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment, selectedFragment.getClass().getSimpleName())
                    .commit();
        }
        return true;
    }
}