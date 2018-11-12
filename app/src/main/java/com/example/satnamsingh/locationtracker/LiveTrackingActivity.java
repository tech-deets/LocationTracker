package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LiveTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment liveTrackingMapFragment;
    Locations userLocation;
    LatLng srcLocation;
    GoogleMap mMap;
    ArrayList<LatLng> allLocations;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        liveTrackingMapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.liveTrackingMap);
        Intent in=getIntent();
        String user=in.getStringExtra("member");
        Log.d("LIVELOCATIONMEMBER2","------"+user);
            allLocations=new ArrayList<>();
        trackUser(user);


    }
    public void trackUser(String user){


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users")
                .child(user).child("LastLocation");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 userLocation =dataSnapshot.getValue(Locations.class);
                 allLocations.add(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()));
                 Log.d("polyline points",allLocations.get(i).latitude+"");
                 Log.d("POLYLINECALLED","---"+ i +"---");
                Toast.makeText(LiveTrackingActivity.this, "Polyline called in listener", Toast.LENGTH_SHORT).show();

                liveTrackingMapFragment.getMapAsync(LiveTrackingActivity.this::onMapReady);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        i++;
        Toast.makeText(getApplicationContext(), "polyline called", Toast.LENGTH_SHORT).show();
        LatLng locate=new LatLng(userLocation.getLatitude(),userLocation.getLongitude());

//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()),
//                        locate)
//                .width(5)
//                .color(Color.RED));
//        line.setStartCap(new RoundCap());
        mMap.addPolyline((new PolylineOptions().addAll(allLocations).color(Color.RED).width(5)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locate, 16));


    }
}
