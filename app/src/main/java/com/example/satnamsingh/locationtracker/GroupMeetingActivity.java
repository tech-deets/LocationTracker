package com.example.satnamsingh.locationtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import java.util.ArrayList;
import java.util.Calendar;

public class GroupMeetingActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment meetingPlaceMapFragment;
    LatLng meetingPlace;
    GoogleMap mMap;
    boolean markerUsed=false;
    private ArrayList<String> groupCode;
    private  ArrayList<String> groupName;
    private Spinner groups_spinner ;
    private ArrayList<String> members;
    public ArrayList<Users> userData;
    private ArrayList<Locations> usersLastLocation;
    int userDataIndex=0;
    private GroupMembers groupListAdapter;
    RecyclerView rcv;
    LinearLayout date_Lout;
    private Calendar calendar;
    private int day,month1,year1;
    private DatePickerDialog datePickerDialog;
    TextView meetingDate_tv;
    LinearLayout time_Lout;
    TextView meetingTime_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_meeting);
        usersLastLocation=new ArrayList<>();
        groupCode =new ArrayList<>();
        groupName =new ArrayList<>();
        members=new ArrayList<>();
        userData=new ArrayList<>();
        date_Lout = findViewById(R.id.date_Lout);
        calendar = Calendar.getInstance();
        year1=calendar.get(Calendar.YEAR);
        month1 = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        meetingDate_tv=findViewById(R.id.meetingDate_tv);
        time_Lout=findViewById(R.id.time_Lout);
        meetingTime_tv=findViewById(R.id.meetingTime_tv);

        groups_spinner=(Spinner)(findViewById(R.id.groups_spinner_meetingPlace));
        showGroupListSpinner();
        Log.d("MYMESSAGE","after calling spinner function");

        rcv=(RecyclerView)(findViewById(R.id.group_rcv_meetingPlace));
        groupListAdapter =new GroupMembers();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv.setAdapter(groupListAdapter);
        rcv.setLayoutManager(layoutManager);

        meetingPlaceMapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.meetingPlaceMap);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        time_Lout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(GroupMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        boolean timeFormat_12Hr=true;

                        if(selectedHour<=12){

                            meetingTime_tv.setText( selectedHour + ":" + selectedMinute+" AM");

                        }
                        else {
                            selectedHour=selectedHour-12;
                            meetingTime_tv.setText( selectedHour + ":" + selectedMinute+" PM");

                        }
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");


                mTimePicker.show();

            }
        });


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

        date_Lout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "inside button click listener", Toast.LENGTH_SHORT).show();
                datePickerDialog = new DatePickerDialog(GroupMeetingActivity.this,R.style.DatePickerDialog,listener,year1,month1,day);
                if(calendar!=null){
                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                }
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(markerUsed){
            mMap.clear();
        }
        markerUsed=true;
        mMap=googleMap;
        mMap.addMarker(new MarkerOptions().position(meetingPlace).title("Meeting place"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(meetingPlace, 16));

    }
    class GroupMembers extends RecyclerView.Adapter<GroupMembers.MyViewHolder> {
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
        public GroupMembers.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.selected_group_cardview, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.selectedgroup_cardview));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(GroupMembers.MyViewHolder holder, final int position) {
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


        }
        @Override
        public int getItemCount() {
            Log.d("MYMESSAGE", "get Item Count Called");
            return userData.size();
        }
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
                Log.d("MYMESSAGE","FETCHING THE GROUP NAMES");
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
        //  Log.d("MYMSG",GlobalData.phoneNumber);

    }
    public void showGroupMembersForMeeting(View v){

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

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        groupListAdapter.notifyDataSetChanged();

                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
        userDataIndex=0;




    }
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //textView.setText("");
            day=dayOfMonth;
            month1 = month+1;
            year1=year;
            meetingDate_tv.setText(day+"/"+month1+"/"+year);

        }

    };

}
