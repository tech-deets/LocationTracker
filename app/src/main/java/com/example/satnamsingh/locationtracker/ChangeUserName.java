package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.TestLooperManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeUserName extends AppCompatActivity {
    private Button ok,cancel;
    private TextView name_tv;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
        ok = findViewById(R.id.change_name_bt);
        cancel = findViewById(R.id.cancel_name_bt);
        name_tv = findViewById(R.id.change_name_tv);
        ok.setOnClickListener((view)->{
            name = (String)name_tv.getText();
            if (name==null||name.equals(""))
                Toast.makeText(this, "Please ENTER Name first", Toast.LENGTH_SHORT).show();
            else
            {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(GlobalData.phoneNumber).child("name");
                AlertDialog.Builder nameChangeDialog = new AlertDialog.Builder(ChangeUserName.this);
                nameChangeDialog.setTitle("Change users name");
                nameChangeDialog.setMessage("Are you sure to Change your user name?");
                nameChangeDialog.setPositiveButton("Change",(dialog, which) -> {
                    databaseReference.setValue(name);
                    });
                /////////////this is cancel button do nothing /////////////
                nameChangeDialog.setNegativeButton("Cancel",(dialog,which)-> {

                });
                AlertDialog alert = nameChangeDialog.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        cancel.setOnClickListener((view)->
        {
            this.finish();
        });
    }
}
