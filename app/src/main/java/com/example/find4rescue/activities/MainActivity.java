package com.example.find4rescue.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.find4rescue.R;
import com.example.find4rescue.fragments.CameraFragment;
import com.example.find4rescue.fragments.MapFragment;
import com.example.find4rescue.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#D58258"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new SearchFragment();
                switch (item.getItemId()) {
                    case R.id.action_map:
                        fragment = new MapFragment();
                        Toast.makeText(MainActivity.this, "Map!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        Toast.makeText(MainActivity.this, "Search!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_camera:
                        fragment = new CameraFragment();
                        Toast.makeText(MainActivity.this, "Camera!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miLogout:
                logout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.d("MainActivity", "Current User: " + currentUser);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}