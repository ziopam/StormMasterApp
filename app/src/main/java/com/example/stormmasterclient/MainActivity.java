package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("USER_DATA", 0);
        String token = preferences.getString("token", "null");
        if (token.equals("null")) {
            LoggerOut loggerOut = new LoggerOut(this);
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            loggerOut.logOut();
            return;
        }
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationRailView navigationRail = findViewById(R.id.navigationRail);

        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(this::setupNavigation);
        } else if (navigationRail != null) {
            navigationRail.setOnItemSelectedListener(this::setupNavigation);
        }
    }

    private boolean setupNavigation(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.history){
            selectedFragment = new HistoryFragment();
        } else if (itemId == R.id.profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
        }
        return true;
    }
}