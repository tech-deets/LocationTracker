package com.example.satnamsingh.locationtracker;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class UserHomeActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String phoneNumber;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    TextView profileName,profilePhone;
    ImageView profilePhoto;
    private View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navigationView =(NavigationView)findViewById(R.id.nav_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.lines);
        }
        headerView = navigationView.getHeaderView(0);
        profilePhoto = headerView.findViewById(R.id.image_profile);
        profileName = headerView.findViewById(R.id.textview_name);
        profilePhone = headerView.findViewById(R.id.textview_phonenumber);
        Intent in =getIntent();
        phoneNumber=in.getStringExtra("phone");
        fetchData();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.m1)
                {
                    Intent in = new Intent(getApplicationContext(),MyContactActivity.class);
                    startActivity(in);
                    Toast.makeText(UserHomeActivity.this, "Item one is selected", Toast.LENGTH_LONG).show();
                }
                else if(menuItem.getItemId()==R.id.m2)
                {
                    Intent in = new Intent(getApplicationContext(),MyInvitations.class);
                    startActivity(in);
                    Toast.makeText(UserHomeActivity.this, "Item two is selected", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(UserHomeActivity.this, "Another Item is selected", Toast.LENGTH_LONG).show();
                }
                //Close Drawer after logic is executed
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //////////////////// NavigationView listener ends/////////////////////////////////
    }

    public void fetchData(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        DatabaseReference reference = databaseReference.child(GlobalData.phoneNumber);
     //   Log.d("phone number")
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    Toast.makeText(getApplicationContext(), "No data Found", Toast.LENGTH_LONG).show();

                }else {

                    Users user =dataSnapshot.getValue(Users.class);
                   GlobalData.name=user.getName();
                   GlobalData.photo=user.getPhoto();
                   GlobalData.email=user.getEmail();

                    Log.d("MYMSG", "data saved in gloabl class\n");
                    Log.d("MYMSG", GlobalData.phoneNumber+"\n");
                    Log.d("MYMSG", GlobalData.name+"\n");
                    Log.d("MYMSG", GlobalData.photo+"\n");
                    Log.d("MYMSG", GlobalData.email+"\n");

                    Glide.with(getApplicationContext()).load(GlobalData.photo).apply(RequestOptions.circleCropTransform()).thumbnail(0.3f).into(profilePhoto);
                    profileName.setText(""+GlobalData.name);
                    profilePhone.setText(""+GlobalData.phoneNumber);

                    //to open the new activity to get the details of the user
                    // getDetails();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawer.openDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.m1)
        {

        }
        else if(item.getItemId()==R.id.m2)
        {

        }
        else // if(item.getItemId()==android.R.id.home)
        {
            //Logic for Toolbar home button
        }
        return true;
    }
}
