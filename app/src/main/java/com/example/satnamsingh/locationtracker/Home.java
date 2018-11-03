package com.example.satnamsingh.locationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check If Permissions are already granted, otherwise show Ask Permission Dialog
            if (checkPermission()) {
                SharedPreferences sharedPreferences = getSharedPreferences("LocationTrackerUser.txt", MODE_PRIVATE);
                String phoneNumber = sharedPreferences.getString("phoneNumber", "");
                if (phoneNumber == null || phoneNumber == "")
                {
                    //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent in = new Intent(this, UserHomeActivity.class);
                    in.putExtra("phone", phoneNumber);
                    GlobalData.phoneNumber=phoneNumber;
                    startActivity(in);
                    finish();
                }
//                Toast.makeText(this, "Contacts read write permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contacts Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }


    public void signIn(View v) {
        Intent sign_up = new Intent(this, LoginScreen.class);
        startActivity(sign_up);
        this.finish();
    }

    public void signUp(View v) {
        Intent sign_up = new Intent(this, SignUpActivity.class);
        startActivity(sign_up);
    }

    public boolean checkPermission() {
        boolean result1 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED;
        boolean result2 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }
    public void requestPermission() {
        //Show ASK FOR PERSMISSION DIALOG (passing array of permissions that u want to ask)
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS}, 1);
    }
    // After User Selects Desired Permissions, thid method is automatically called
    // It has request code, permissions array and corresponding grantresults array

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Contacts read write PERMISSON GRANTED", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)
                {

                }
            }
        }
    }
}
