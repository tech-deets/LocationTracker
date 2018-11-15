package com.example.satnamsingh.locationtracker;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MeetingInfoDialog extends AppCompatActivity {

    private FloatingActionButton close_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info_dialog);
        close_bt = findViewById(R.id.close_meeting_info_dialog_fbt);
        close_bt.setOnClickListener((view)->{
            MeetingInfoDialog.this.finish();
        });
    }
}
