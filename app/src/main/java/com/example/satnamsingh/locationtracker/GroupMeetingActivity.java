package com.example.satnamsingh.locationtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GroupMeetingActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment meetingPlaceMapFragment;
    LatLng meetingPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_meeting);
        meetingPlaceMapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.meetingPlaceMap);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
              // Log.i(TAG, "Place: " + place.getName());
               Log.d("PLACE_SELECTED",""+place.getName());
              meetingPlace= place.getLatLng();
              Log.d("PLACE_SELECTED",meetingPlace.latitude+" "+meetingPlace.longitude);
               meetingPlaceMapFragment.getMapAsync(GroupMeetingActivity.this::onMapReady);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("PLCAE_SELECTED","error encountered");

                Log.i("ONERRORCALLED", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap;
        mMap=googleMap;
        mMap.addMarker(new MarkerOptions().position(meetingPlace).title("Meeting place"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(meetingPlace, 16));

    }
}
