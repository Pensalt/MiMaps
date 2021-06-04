package com.shlompie.mimaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottm_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).commit(); // Map fragment shown on load.

    }

    // The following code has been attributed from:
    // Author: Coding in Flow
    // Available at: https://www.youtube.com/watch?v=tPV8xA7m-iw
    // Bottom Nav functionality. Code is done here to keep the onCreate method cleaner.
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_map: selectedFragment = new MapsFragment();
                            break;
                        case R.id.nav_landmarks: selectedFragment = new LandmarksFragment();
                            break;
                        case R.id.nav_settings: selectedFragment = new SettingsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                    return true;
                }
            };
}