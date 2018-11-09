package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

public class LiveTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment liveTrackingMapFragment;
    Locations userLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        liveTrackingMapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.liveTrackingMap);
        Intent in=getIntent();
        String user=in.getStringExtra("member");
        Log.d("LIVELOCATIONMEMBER2","------"+user);
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

                liveTrackingMapFragment.getMapAsync(LiveTrackingActivity.this::onMapReady);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap=googleMap;
        LatLng locate=new LatLng(userLocation.getLatitude(),userLocation.getLongitude());
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()),
                        locate)
                .width(5)
                .color(Color.RED));
        line.setStartCap(new RoundCap());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locate, 11));


    }
}
