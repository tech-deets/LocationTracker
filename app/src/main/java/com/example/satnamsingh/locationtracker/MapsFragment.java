package com.example.satnamsingh.locationtracker;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
private String phoneNumber;
    private GoogleMap mMap;
    public MapsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_maps, container, false);
        Bundle bd=new Bundle();
        phoneNumber=bd.getString("phone");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragmentv= (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragmentv.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {



        FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber)
                .child("LastLocation");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    final double[] latitude = new double[1];
                final double[] longitude = new double[1];
                final DatabaseReference[] db_latitude = {databaseReference.child("Latitude")};
                final DatabaseReference[] db_longitude = {databaseReference.child("Longitude")};
                db_latitude[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        latitude[0] = (double) dataSnapshot.getValue();
                        Log.d("LOCATION",latitude[0]+"");
                        db_longitude[0].addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                longitude[0] = (double) dataSnapshot.getValue();
                                mMap = googleMap;
                                mMap.clear();
                                LatLng lastLocation = new LatLng(latitude[0],longitude[0]);
                                Log.d("MYLOCATIONONMAP",latitude[0]+"    "+longitude[0]);
                                mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,16));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Add a marker in Sydney and move the camera
                    //LatLng mymarker = new LatLng(latitude[0], longitude[0]);

                  //  MarkerOptions markerOptions =  new MarkerOptions().position(mymarker).title("Welcome to Amritsar");

                    //mMap.addMarker(markerOptions);

//                  //  mMap.addMarker(markerOptions);
//                    //mMap.addMarker(new MarkerOptions().position(lastLocation).title("Last Location "));
//
////                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
