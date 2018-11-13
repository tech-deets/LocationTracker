package com.example.satnamsingh.locationtracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MemberHistoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    String user;                     //contain the mobile number of the user of which history need to be shown. Recieve through intent
    private TextView date_tv;
    private int day,month1,year1;
    private Button show_bt;
    ArrayList<LatLng> userLocation_al;
    SupportMapFragment historyMapFragment;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_history);
        userLocation_al=new ArrayList<>();
        date_tv = findViewById(R.id.date_tv);
        historyMapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.historyMap);
        calendar = Calendar.getInstance();
        year1=calendar.get(Calendar.YEAR);
        month1 = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        show_bt = findViewById(R.id.show_bt);
        Intent in=getIntent();
        user=in.getStringExtra("member");

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "inside button click listener", Toast.LENGTH_SHORT).show();
                datePickerDialog = new DatePickerDialog(MemberHistoryActivity.this,R.style.DatePickerDialog,listener,year1,month1,day);
                if(calendar!=null){
                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                }
                datePickerDialog.show();
            }
        });
        show_bt.setOnClickListener((view)->{
            userLocation_al.clear();
            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
//            DatabaseReference databaseReference1=firebaseDatabase.getReference("Users").child(user)
//                    .child("Location").orderByChild("date").equals(""+day+"/"+month+"/"+year);
            DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(user);
            Query locations=databaseReference.child("Locations").orderByChild("date").endAt(""+day+"/"+month1+"/"+year1);
            Log.d("HISTORYLOCATION",day+" "+month1+" "+year1+"--------");
            locations.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot==null){
                        Log.d("LOCATIONHISTORY","no location available");
                    }
                    else{
                        userLocation_al.clear();
                        Log.d("LOCATIONHISTORYCOUNT",""+dataSnapshot.getChildrenCount());
                        Log.d("LOCATIONHISTORY",""+dataSnapshot.toString());
                        int i=0;
                        for(DataSnapshot location:dataSnapshot.getChildren()){
                            Locations userLocation=location.getValue(Locations.class);
                            Log.d("LOCATIONHIDTORY",""+userLocation.getLatitude());

                            userLocation_al.add(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()));
                            Log.d("LOCATIONHISTORY",""+userLocation_al.get(i).latitude);
                            Log.d("LOCATIONHISTORY","location added");
                            Log.d("LOCATIONHISTORY","----i="+ i++);

                        }
                        historyMapFragment.getMapAsync(MemberHistoryActivity.this::onMapReady);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });
    }
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //textView.setText("");
            day=dayOfMonth;
            month1 = month+1;
            year1=year;
             date_tv.setText(day+"/"+month1+"/"+year);

        }

    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("HISTORYHISTORY","in the onmap ready");
        mMap=googleMap;
        mMap.addPolyline((new PolylineOptions().addAll(userLocation_al).color(Color.RED).width(20)));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation_al.get(0).latitude, userLocation_al.get(0).longitude)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation_al.get(0).latitude, userLocation_al.get(0).longitude), 16));
    }
}
