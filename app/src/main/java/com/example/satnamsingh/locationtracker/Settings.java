package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {
    private ImageView settingPhoto;
    private TextView settingName;
    private String photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingPhoto = findViewById(R.id.photo_settings);
        settingName = findViewById(R.id.name_settings);
        setUserInfo();
    }

    void setUserInfo()
    {
        DatabaseReference dbName = FirebaseDatabase.getInstance().getReference("Users").
            child(GlobalData.phoneNumber).child("name");
        dbName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null)
                {
                    String name = dataSnapshot.getValue(String.class);
                    settingName.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference dbPhoto = FirebaseDatabase.getInstance().getReference("Users").
                child(GlobalData.phoneNumber).child("photo");
        dbPhoto.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null)
                {
                    photo = dataSnapshot.getValue(String.class);
                    Glide.with(getApplicationContext()).load(photo).
                            apply(RequestOptions.circleCropTransform()).into(settingPhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void userLogout(View v)
    {
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
    }
    void changeName(View v)
    {
        Intent changeNameDialog = new Intent(getApplicationContext(),ChangeUserName.class);
        startActivity(changeNameDialog);
    }
}
