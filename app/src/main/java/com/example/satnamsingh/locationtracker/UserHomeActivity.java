package com.example.satnamsingh.locationtracker;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomeActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String phoneNumber;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView profileName,profilePhone;
    private ImageView profilePhoto;
    private View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        drawer=findViewById(R.id.drawer);
        navigationView =findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_user_home);
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
       // GlobalData.phoneNumber=phoneNumber;
        profilePhone.setText(""+phoneNumber);
        Intent intent=new Intent(this, LocationTrackerService.class);
        intent.setAction("START SIGNAL");
        startService(intent);
        fetchData();

        navigationView.setNavigationItemSelectedListener((menuItem)-> {
                if(menuItem.getItemId()==R.id.menu_create_group)
                {
                    Intent intet = new Intent(getApplicationContext(),MyContactActivity.class);
                    startActivity(intet);
                }
                else if(menuItem.getItemId()==R.id.menu_invites)
                {
                    Intent inte = new Intent(getApplicationContext(),MyInvitations.class);
                    startActivity(inte);
                }
                else if(menuItem.getItemId()==R.id.menu_groups)
                {
                    Toast.makeText(UserHomeActivity.this, "groups is clicked", Toast.LENGTH_LONG).show();
                    Intent locationService =new Intent(getApplicationContext(),MyGroupsActivity.class);
                    startActivity(locationService);
                }else if (menuItem.getItemId()==R.id.menu_logout)
                {
                    AlertDialog.Builder logOutDialog = new AlertDialog.Builder(UserHomeActivity.this);
                    logOutDialog.setTitle("Log Out");
                    logOutDialog.setMessage("Are you sure to Log out?");
                    logOutDialog.setPositiveButton("Log Out",(dialog, which) ->{
                            SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
                            SharedPreferences.Editor editor =sharedPreferences.edit();
                            editor.putString("phoneNumber","");
                            editor.commit();
                            Intent logoutintent = new Intent(getApplicationContext(),Home.class);
                            finishAffinity();
                            startActivity(logoutintent);
                    });
                    /////////////this is cancel button do nothing /////////////
                    logOutDialog.setNegativeButton("Cancel",(dialog,which)-> {

                    });
                    AlertDialog alert = logOutDialog.create();
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                //Close Drawer after logic is executed
                drawer.closeDrawer(GravityCompat.START);
                return true;
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

                if(dataSnapshot.getValue(Users.class)==null){
                    Toast.makeText(getApplicationContext(), "User Not Found\n It may be removed", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("phoneNumber","");
                    editor.commit();

                }else {

                    Users user =dataSnapshot.getValue(Users.class);
                    GlobalData.name=user.getName();
                    GlobalData.photo=user.getPhoto();
                    GlobalData.email=user.getEmail();
                    Log.d("MYMSG", "data saved in gloabl class\n");
                    Glide.with(getApplicationContext()).load(GlobalData.photo).
                            apply(RequestOptions.circleCropTransform()).thumbnail(0.3f).into(profilePhoto);
                    profileName.setText(""+GlobalData.name);
                    profilePhone.setText(""+GlobalData.phoneNumber);
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
        else if(item.getItemId()==R.id.menu_create_group)
        {
//
        }
        else if(item.getItemId()==R.id.menu_invites)
        {

        }
        else // if(item.getItemId()==android.R.id.home)
        {
            //Logic for Toolbar home button
        }
        return true;
    }

}
