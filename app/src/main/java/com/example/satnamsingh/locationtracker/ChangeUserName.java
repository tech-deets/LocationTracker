package com.example.satnamsingh.locationtracker;

import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        ok.setOnClickListener((view)->{
            if (name==null||name.equals(""))
                Toast.makeText(this, "Please ENTER Name first", Toast.LENGTH_SHORT).show();
            else
            {

            }
        });
        cancel.setOnClickListener((view)->
        {
            this.finish();
        });
    }
}
