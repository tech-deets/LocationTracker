package com.example.satnamsingh.locationtracker;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;

import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeActivity extends AppCompatActivity implements OnMapReadyCallback,BottomSheetGroup.BottomSheetListener {
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
    public ArrayList<Users> userData;
    private SelectGroup groupListAdapter;
    private ArrayList<Locations> usersLastLocation;
    int userDataIndex=0;
    private RecyclerView rcv;
    private NavigationMenuView navigationMenuView;
    String memberPhone;
    GoogleMap mMap;
    private boolean showLiveLocation=true;
   // ArrayList<LastLocations> lastLocation;
    SupportMapFragment mapFragmentv;
    private boolean mapStatus=false;
    private  boolean groupSelected=false;
    private int previousGroupSelected=0;
    private boolean loadingGroupMembers=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        //deleteEntries();
        Intent intent=new Intent(this, LocationTrackerService.class);
        intent.setAction("START SIGNAL");
        startService(intent);
                Log.d("MYMESSAGE","after calling service");
        usersLastLocation=new ArrayList<>();
        groupCode =new ArrayList<>();
        groupName =new ArrayList<>();
        members=new ArrayList<>();
        userData=new ArrayList<>();
        groups_spinner=(Spinner)(findViewById(R.id.groups_spinner));
        //start the service through new thread to cut off the load from main thread
        mapFragmentv= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragmentv.getMapAsync(UserHomeActivity.this::onMapReady);

        drawer=findViewById(R.id.drawer);
        navigationView =findViewById(R.id.nav_view);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration
                (UserHomeActivity.this,DividerItemDecoration.VERTICAL);
        navMenuView.addItemDecoration(dividerItemDecoration);
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
//    Bundle mybd=new Bundle();
//    mybd.putString("phone",phoneNumber);

//        MapsFragment mapsFragment = new MapsFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.ll1,mapsFragment).commit();

        Log.d("MYMESSAGE","before fetch function");
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
                }else if(menuItem.getItemId() == R.id.group_meeting)
                {
                    Intent groupMeeting = new Intent(getApplicationContext(),GroupMeetingActivity.class);
                    startActivity(groupMeeting);
                }else if (menuItem.getItemId() == R.id.menu_settings){
                    Intent settingsIntent = new Intent(getApplicationContext(),Settings.class);
                    startActivity(settingsIntent);
                }else if (menuItem.getItemId() == R.id.my_meetings_menu){
                    Intent settingsIntent = new Intent(getApplicationContext(),MyMeetingsActivity.class);
                    startActivity(settingsIntent);
                }else if (menuItem.getItemId() == R.id.danger_zone){
                    Intent dangerZoneIntent = new Intent(getApplicationContext(),DangerZoneActivity.class);
                    startActivity(dangerZoneIntent);
                }
                //Close Drawer after logic is executed
                drawer.closeDrawer(GravityCompat.START);
                return true;
        });
        //////////////////// NavigationView listener ends/////////////////////////////////
        Log.d("MYMESSAGE","before calling spinner function");

        showGroupListSpinner();
        Log.d("MYMESSAGE","after calling spinner function");

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
            if(groupCode==null)
                groupCode=new ArrayList<>();
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
            Log.d("MYMESSAGE","FETCHING THE GROUP NAMES");
            if(dataSnapshot==null){
              //  System.out.println("referring gnull");
                Log.d("MYMSG","----reffering null");

            }
            //Log.d("MYMSG","not null=----");

            groupName=(ArrayList<String>)dataSnapshot.getValue();
            if(groupName==null){
                groupName=new ArrayList<>();
            }
            groupName.add(0,"--Select Group--");

            Log.d("MYMSG","--"+groupName.toString());

            ArrayAdapter<String> ad=
                    new ArrayAdapter<>(getApplicationContext(),R.layout.spinner,R.id.spinner_tv,groupName);
            groups_spinner.setAdapter(ad);

            groups_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("MYMSG","PREVIOUS GROUP"+previousGroupSelected);
                    Log.d("MYMSG","current GROUP"+position);
                    Log.d("MYMSG","loadinggroupstatus"+loadingGroupMembers);


                 //   if(previousGroupSelected==position){

                 //   }
                   // else{
                        previousGroupSelected=position;
                         if(loadingGroupMembers==false){
                             loadingGroupMembers=true;
                             //userData.clear();
                             members.clear();
                             if (position!= 0) {
                                 Log.d("MYMESSAGE","---"+groupName.get(position));

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
                                         Log.d("MYMSG: Size: ", members.size()+"FOR THE POSITION "+position);
                                         showGroupMembers();


                                     }

                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {

                                     }
                                 });


                             }
                         }

                   // }


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
  //  Log.d("MYMSG",GlobalData.phoneNumber);

   }
    public void showGroupMembers(){

        userData.clear();
        usersLastLocation.clear();
        Log.d("MYMESSAGE","-----inside the select executrion");

    for(int i=0; i<members.size(); i++){

        String  member= members.get(i);
        Log.d("MYMESSAGEMEMBERS",member);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(member);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){

                    Users users = dataSnapshot.getValue(Users.class);
                    Log.d("LATITUDEANDLONG",users.getName()+"  "+users.getPhoneNumber());
                    userData.add(new Users(users));
        DatabaseReference datab=FirebaseDatabase.getInstance().getReference("Users")
                .child(member).child("LastLocation");
        datab.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
//                    for(double lc:dataSnapshot.get)
                    Log.d("LATITUDE",datab.toString());

                    Locations lc=dataSnapshot.getValue(Locations.class);
                                 Log.d("LATITUDE",lc.getLatitude()+"  "+lc.getLongitude());
                                 usersLastLocation.add(new Locations(lc.getLatitude(),lc.getLongitude()));
//                                 userDataIndex=0;
                    mapFragmentv.getMapAsync(UserHomeActivity.this::onMapReady);
                    groupListAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//                   groupListAdapter.notifyDataSetChanged();

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
        userDataIndex=0;




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // int i = userDataIndex++;

        if(mapStatus){
            mMap.clear();
        }
        mMap = googleMap;
        mapStatus=true;
        if(showLiveLocation){
            FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber)
                .child("LastLocation");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    final double[] latitude = new double[1];
                final double[] longitude = new double[1];
                final DatabaseReference[] db_latitude = {databaseReference.child("latitude")};
                final DatabaseReference[] db_longitude = {databaseReference.child("longitude")};
                db_latitude[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        latitude[0] = (double) dataSnapshot.getValue();
                        Log.d("LOCATION",latitude[0]+"");
                        db_longitude[0].addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                longitude[0] = (double) dataSnapshot.getValue();
//                                mMap = googleMap;
//                                mMap.clear();

                                LatLng lastLocation = new LatLng(latitude[0],longitude[0]);
                                Log.d("MYLOCATIONONMAP",latitude[0]+"    "+longitude[0]);
                                mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location"));
                                mMap.addMarker(new MarkerOptions().position(lastLocation).title("cc"));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,16));
                                showLiveLocation=false;
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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }else{
            Log.d("MYMESSAGE", "starting of onmap ready");
            Log.d("MYMESSAGE", members.size() + "==========");





                //   Log.d("MYLOCATIONONMAP",latitude[0]+"    "+longitude[0]);
//            new MyAsyncTask().execute("https://picsum.photos/200/300/?random");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=userDataIndex;i<usersLastLocation.size();i++){
                        userDataIndex++;
                        Log.d("LOOPCALLEDTIMES ", i + "-----======");
                        // Locations lastLocation=;
//                       Log.d("MYMESSAGE",userData.get(i).getName()+"\n");
//            Log.d("MYMESSAGE",userData.get(i).getName()+"\n");

                        double latitude = usersLastLocation.get(i).getLatitude();
                        double longitude = usersLastLocation.get(i).getLongitude();
                        Log.d("LOCATIONONMAPREADY", "=----" + i + "---- " + latitude + "\n" + longitude);

                        LatLng lastLocation = new LatLng(latitude, longitude);

                        try {
                            final int s = i;
                            URL url = new URL(userData.get(i).getPhoto());
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            runOnUiThread(new Runnable() {
                                @Override

                                public void run() {
                                    Marker marker;

                                    marker= mMap.addMarker(new MarkerOptions().position(lastLocation).title(userData.get(s).getName())
                                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getApplicationContext(),
                                                    image, userData.get(s).getName()))));
                                    marker.setTag(userData.get(s).getPhoneNumber());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 4));
                                    // final int index=i;
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            BottomSheetGroup bottomSheet = new BottomSheetGroup();

                                            bottomSheet.show(getSupportFragmentManager(), "BottomSheetGroup");
                                            //Intent in=new Intent(getApplicationContext(),BottomSheetGroup.class);
                                            memberPhone=marker.getTag().toString();
                                            Log.d("MARKERCLICKED","---value of "+marker.getTag());

                                            Log.d("MARKERCLICKED","---"+memberPhone);
                                            Toast.makeText(UserHomeActivity.this, "marker clicked"+marker.getTitle(), Toast.LENGTH_SHORT).show();
                                            return false;
                                        }
                                    });
                                }
                            });

                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }

                } //run ed here
                }).start();

            ////for loop end here
        }


    }
    //listener for bottomsheet's button clicked////////////////////
    @Override
    public void onButtonClicked(String text) {
            if(text.trim().equals("liveTrack_bt")){
                Intent in =new Intent(getApplicationContext(),LiveTrackingActivity.class);
                Log.d("LIVELOCATIONMEMBER",memberPhone);
                in.putExtra("member",memberPhone);
                startActivity(in);

            }
        if(text.trim().equals("history_bt")){
            Intent in =new Intent(getApplicationContext(),MemberHistoryActivity.class);
            Log.d("History",memberPhone);
            in.putExtra("member",memberPhone);
                startActivity(in);
        }


    }

    class SelectGroup extends RecyclerView.Adapter<SelectGroup.MyViewHolder> {
        int iteratingValue=1;
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
                       localcardview.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               double latitude=usersLastLocation.get(position).getLatitude();
                               double longitude =usersLastLocation.get(position).getLongitude();
                               LatLng lastLocation=new LatLng(latitude,longitude);
                              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,15));

                           }
                       });

                       Users u = userData.get(position);
                       ImageView image;
                       TextView name;
                       name= (TextView) localcardview.findViewById(R.id.contactName111_tv);
                       image=(ImageView)localcardview.findViewById(R.id.contactPhoto111_iv);
                       Glide.with(getApplicationContext()).load(u.getPhoto()).apply(RequestOptions.circleCropTransform())
                               .thumbnail(0.3f).into(image);
                       name.setText(u.getName());
                       if(iteratingValue==userData.size()){
                           loadingGroupMembers=false;
                           iteratingValue=0;

                       }
                       iteratingValue++;


                   }
                   @Override
                   public int getItemCount() {
                       Log.d("MYMESSAGE", "get Item Count Called");
                       return userData.size();
                   }

               }




    public  Bitmap createCustomMarker(Context context, Bitmap bitmap, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageBitmap(bitmap);
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap1 = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        marker.draw(canvas);

        return bitmap1;
    }

public void deleteEntries() {
    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users")
            .child(GlobalData.phoneNumber).child("Locations");
    db.setValue("");
}
}

