package com.example.covid_19infotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
                    break;

                case R.id.menu_compare:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new CompareFragment()).commit();
                    break;

                case R.id.menu_help:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HelpFragment()).commit();
                    break;
            }
            return true;
        });
    }
}