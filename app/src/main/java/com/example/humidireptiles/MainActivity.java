package com.example.humidireptiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton FeedingSched, Dashboard, Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        FeedingSched = (ImageButton) findViewById(R.id.feedingSchedule);
        FeedingSched.setOnClickListener(this);

        Dashboard = (ImageButton) findViewById(R.id.DashboardBtn);
        Dashboard.setOnClickListener(this);

        Settings = (ImageButton) findViewById(R.id.Settings);
        Settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedingSchedule:
                startActivity(new Intent(this, FeedingSchedule.class));
                break;

            case R.id.DashboardBtn:
                startActivity(new Intent(this, Dashboard.class));
                break;

            case R.id.Settings:
                startActivity(new Intent(this, SettingsAct.class));
                break;
        }
    }
}