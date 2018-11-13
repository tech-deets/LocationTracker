package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Settings extends AppCompatActivity {
    LinearLayout logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logout = findViewById(R.id.user_logout);
        logout.setOnClickListener((view)->{
            AlertDialog.Builder logOutDialog = new AlertDialog.Builder(Settings.this);
            logOutDialog.setTitle("Log Out");
            logOutDialog.setMessage("Are you sure to Log out?");
            logOutDialog.setPositiveButton("Log Out",(dialog, which) ->{
                Intent stopServiceIntent = new Intent(Settings.this,LocationTrackerService.class);
                stopServiceIntent.setAction("STOP SIGNAL");
                startService(stopServiceIntent);
                SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putString("phoneNumber","");
                editor.commit();
                Intent logoutintent = new Intent(getApplicationContext(),Home.class);
                finishAffinity();
                startActivity(logoutintent);
            });
            /////////////this is cancel button do nothing /////////////
            logOutDialog.setNegativeButton("Cancel",(dialog,which)-> {

            });
            AlertDialog alert = logOutDialog.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

        });

    }
}
