package com.example.satnamsingh.locationtracker;

import android.content.Intent;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private ArrayList<String> groupCode;
    private  ArrayList<String> groupName;
    private Spinner groups_spinner ;
    private ArrayList<String> members;
    private ArrayList<Users> userData;
    private SelectGroup groupListAdapter;
    private RecyclerView rcv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
         groupCode =new ArrayList<>();
        groupName =new ArrayList<>();
        members=new ArrayList<>();
        userData=new ArrayList<>();
        groups_spinner=(Spinner)(findViewById(R.id.groups_spinner));
        Intent intent=new Intent(this, LocationTrackerService.class);
        intent.setAction("START SIGNAL");
        startService(intent);

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
        profilePhone.setText(""+GlobalData.phoneNumber);
Bundle mybd=new Bundle();
mybd.putString("phone",phoneNumber);
new Thread(new Runnable() {
    @Override
    public void run() {
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(mybd);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.ll1,mapsFragment).commit();

    }
}).start();

//        MapsFragment mapsFragment = new MapsFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.ll1,mapsFragment).commit();

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
                }else if(menuItem.getItemId()==R.id.m4) {

                }else if (menuItem.getItemId()==R.id.menu_logout)
                    {
                        AlertDialog.Builder logOutDialog = new AlertDialog.Builder(UserHomeActivity.this);
                        logOutDialog.setTitle("Log Out");
                        logOutDialog.setMessage("Are you sure to Log out?");
                        logOutDialog.setPositiveButton("Log Out",(dialog, which) ->{
                            Intent stopServiceIntent = new Intent(UserHomeActivity.this,LocationTrackerService.class);
                            stopServiceIntent.setAction("STOP SIGNAL");
                            startService(stopServiceIntent);
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
        showGroupListSpinner();
        rcv=(RecyclerView)(findViewById(R.id.group_rcv));
        groupListAdapter =new SelectGroup();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv.setAdapter(groupListAdapter);
        rcv.setLayoutManager(layoutManager);


        ////////////////////////Spinner Code//////////////////////////////////////////////


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




public void showGroupListSpinner(){
    DatabaseReference fGroupReference1 = FirebaseDatabase.getInstance().getReference("Users").
            child(GlobalData.phoneNumber).child("GroupCode");
    fGroupReference1.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            groupCode=(ArrayList<String>)dataSnapshot.getValue();
            groupCode.add(0,"--Select Group--");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    DatabaseReference fGroupReference = FirebaseDatabase.getInstance().getReference("Users").
            child(GlobalData.phoneNumber).child("GroupName");
    fGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot==null){
                System.out.println("referring gnull");
                Log.d("MYMSG","----reffering null");

            }
            Log.d("MYMSG","not null=----");

            groupName=(ArrayList<String>)dataSnapshot.getValue();
            groupName.add(0,"--Select Group--");

            Log.d("MYMSG","--"+groupName.toString());

            ArrayAdapter<String> ad=
                    new ArrayAdapter<>(getApplicationContext(),R.layout.spinner,R.id.spinner_tv,groupName);
            groups_spinner.setAdapter(ad);

            groups_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    userData.clear();
                    members.clear();
                    if (position!= 0) {
                        Log.d("MYMSG","---"+groupName.get(position));

                        //Toast.makeText(UserHomeActivity.this, "Please selecct a group", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=firebaseDatabase.getReference("Groups").
                                child(groupCode.get(position)).child("groupMembers");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                members = (ArrayList<String>) dataSnapshot.getValue();
                                if(members == null){
                                    members = new ArrayList<>();
                                }
                                Log.d("MYMSG: Size: ", members.size()+"");

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    Log.d("MYMSG",GlobalData.phoneNumber);

   }
public void showGroupMembers(View v){
        userData.clear();
        Log.d("MYMSG","-----inside the select executrion");

    for(int i=0; i<members.size(); i++){

        String  member= members.get(i);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(member);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);

                userData.add(new Users(users.getName(), users.getPhoto()));

                groupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
 class SelectGroup extends RecyclerView.Adapter<SelectGroup.MyViewHolder> {
                   // Define ur own View Holder (Refers to Single Row)
                class MyViewHolder extends RecyclerView.ViewHolder {
                       CardView singlecardview;
           
                       public MyViewHolder(CardView itemView) {
                           super(itemView);
                           singlecardview = (itemView);
                       }
                   }
           
                   // Inflate ur Single Row / CardView from XML here
                   @Override
                   public SelectGroup.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                       LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                       View viewthatcontainscardview = inflater.inflate(R.layout.selected_group_cardview, parent, false);
                       CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.selectedgroup_cardview));
                       // This will call Constructor of MyViewHolder, which will further copy its reference
                       // to customview (instance variable name) to make its usable in all other methods of class
                       return new MyViewHolder(cardView);
                   }
           
                   @Override
                   public void onBindViewHolder(SelectGroup.MyViewHolder holder, final int position) {
                       CardView localcardview = holder.singlecardview;

                       Users u = userData.get(position);
                       ImageView image;
                       TextView name;
                       name= (TextView) localcardview.findViewById(R.id.contactName111_tv);
                       image=(ImageView)localcardview.findViewById(R.id.contactPhoto111_iv);
                       Glide.with(getApplicationContext()).load(u.getPhoto()).apply(RequestOptions.circleCropTransform())
                               .thumbnail(0.3f).into(image);
                       name.setText(u.getName());
                   }
                   @Override
                   public int getItemCount() {
                       Log.d("MYMESSAGE", "get Item Count Called");
                       return userData.size();
                   }
               }

    }

