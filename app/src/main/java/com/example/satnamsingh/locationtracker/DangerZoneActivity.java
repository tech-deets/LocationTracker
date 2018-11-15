package com.example.satnamsingh.locationtracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DangerZoneActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment danger_zone_map_fragment;
    private static final double DEFAULT_RADIUS_METERS = 1000000;
    private static final int MAX_WIDTH_PX = 50;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;
private LatLng dangerZone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_zone);
        danger_zone_map_fragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.danger_zone_map_fragment);
        danger_zone_map_fragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap=googleMap;
      

        //mMap.setOnMapClickListener(this);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg)
            {dangerZone=arg;
                Marker marker= mMap.addMarker(new MarkerOptions().position(dangerZone));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dangerZone,15));
//                CircleOptions circleOptions = new CircleOptions()
//                        .center(dangerZone).fillColor(Color.BLUE).strokeColor(Color.BLUE)
//                        .radius(300);
//                Circle circle = mMap.addCircle(circleOptions);

            }
        });

    }

    //@Override
//    public void onMapClick(LatLng latLng) {
//        dangerZone=latLng;
//        Log.d("MAP_CLICK_EVENT",dangerZone.toString());
//
//
//    }
}
