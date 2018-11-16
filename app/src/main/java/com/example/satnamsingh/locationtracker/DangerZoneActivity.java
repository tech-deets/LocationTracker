package com.example.satnamsingh.locationtracker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DangerZoneActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener ,GoogleMap.OnMyLocationButtonClickListener {
    SupportMapFragment danger_zone_map_fragment;
    private static final double DEFAULT_RADIUS_METERS = 1000000;
    private static final int MAX_WIDTH_PX = 50;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;
    EditText danger_zone_comment_et;
    private String zoneName="Unknown Location";
    Geocoder geocoder;
private LatLng dangerZone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_zone);
        geocoder = new Geocoder(this, Locale.getDefault());
        danger_zone_comment_et=findViewById(R.id.danger_zone_comment_et);
        danger_zone_map_fragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.danger_zone_map_fragment);
        danger_zone_map_fragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap=googleMap;

        //mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

      //boolean s=onMyLocationButtonClick();
        //PlaceDetectionClient.getCurrentPlace();
        // //mMap.setOnMapClickListener(this);
        FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber)
                .child("LastLocation");
        DatabaseReference latitude_db=databaseReference.child("latitude");
        DatabaseReference longitude_db=databaseReference.child("longitude");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                  Locations location=dataSnapshot.getValue(Locations.class);
                  mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                          new LatLng(location.getLatitude(),location.getLongitude()),15));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg)
            { dangerZone = arg;
                AlertDialog.Builder builder = new AlertDialog.Builder(DangerZoneActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Do you really want to add the tapped  location as danger zone");
                Marker marker= mMap.addMarker(new MarkerOptions().position(dangerZone));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dangerZone,15));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Address> addresses = new ArrayList<>();

                        try {
                            addresses = geocoder.getFromLocation(dangerZone.latitude, dangerZone.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Address address = addresses.get(0);
                        if (address != null) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb.append(address.getAddressLine(i) + "\n");
                            }
                            zoneName = sb.toString();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dangerZone, 15));
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(dangerZone).fillColor(Color.argb(70, 50, 50, 156)).strokeColor(Color.BLUE).strokeWidth(15f)
                                    .radius(300);
                            Circle circle = mMap.addCircle(circleOptions);


                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
                ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

            }




    @Override
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, "hello thers----:\n" + location, Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onMyLocationButtonClick() {

        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    //@Override
//    public void onMapClick(LatLng latLng) {
//        dangerZone=latLng;
//        Log.d("MAP_CLICK_EVENT",dangerZone.toString());
//
//
//    }
    public void  addDangerZone(View view){
        String comment=danger_zone_comment_et.getText().toString();
        if(comment==null){
            Toast.makeText(DangerZoneActivity.this, "Please enter ", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("DangerZones");
            DatabaseReference pushKeyReference=databaseReference.push();
            final String pushKey=pushKeyReference.getKey();
            DangerZoneData dangerZoneData=new DangerZoneData(pushKey,GlobalData.phoneNumber,
                    dangerZone.latitude,dangerZone.longitude,zoneName,comment);
           databaseReference.child(pushKey).setValue(dangerZoneData);
           Log.d("DANGER_ZONE","THE ZONE IS ADDED");
        }

    }
}
