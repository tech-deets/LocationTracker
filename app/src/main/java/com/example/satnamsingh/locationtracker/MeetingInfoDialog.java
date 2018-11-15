package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MeetingInfoDialog extends AppCompatActivity implements OnMapReadyCallback {
    private String meetingId;
    private ArrayList<String> members;
    Meetings meetingInfo;
    TextView title;
    TextView date;
    TextView time;
    RecyclerView rcv;
    MeetingGroup meetingGroupAdapter;
    SupportMapFragment meeting_location_map_fragment;
    LatLng meetingLocation;


    private FloatingActionButton close_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info_dialog);
        close_bt = findViewById(R.id.close_meeting_info_dialog_fbt);
        members=new ArrayList<>();
        Intent in=getIntent();
        meetingInfo=new Meetings();
        meeting_location_map_fragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.meeting_location_map_fragment);
         meetingId=in.getStringExtra("meeting");
        title=findViewById(R.id.title_meeting_dialog_tv);
        date=findViewById(R.id.date_meeting_dialog_tv);
        time=findViewById(R.id.time_meeting_dialog_tv);
        getMeetingInfo();

        rcv=(RecyclerView)(findViewById(R.id.meeting_members_rcv));
        meetingGroupAdapter =new MeetingGroup();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv.setAdapter(meetingGroupAdapter);
        rcv.setLayoutManager(layoutManager);
         close_bt.setOnClickListener((view)->{
            MeetingInfoDialog.this.finish();
        });
    }
    public void getMeetingInfo(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Meetings").child(meetingId);
        Log.d("MEETING_ID_INFO",meetingId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot!=null){

                    meetingInfo=dataSnapshot.getValue(Meetings.class);

                    title.setText(meetingInfo.getMeetingAgenda());
                    date.setText(meetingInfo.getMeetingDate());
                    time.setText(meetingInfo.getMeetingTime());

                    members=meetingInfo.getGroupMembers();
                    if(members==null){
                        members=new ArrayList<>();
                    }
                    meetingGroupAdapter.notifyDataSetChanged();
                    meetingLocation=new LatLng(meetingInfo.getLatitude(),meetingInfo.getLongitude());
                    meeting_location_map_fragment.getMapAsync(MeetingInfoDialog.this::onMapReady);

               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap=googleMap;
        mMap.addMarker(new MarkerOptions().position(meetingLocation).title(meetingInfo.getMeetingLocation()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(meetingLocation,14));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

                return false;
            }
        });
    }

    class MeetingGroup extends RecyclerView.Adapter<MeetingGroup.MyViewHolder> {
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
        public MeetingGroup.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.selected_group_cardview, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.selectedgroup_cardview));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(MeetingGroup.MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;
            localcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            ImageView image;
            TextView name;
            name= (TextView) localcardview.findViewById(R.id.contactName111_tv);
            image=(ImageView)localcardview.findViewById(R.id.contactPhoto111_iv);
           // for(String member:members){
             String member=members.get(position);
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(member);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            Users user=dataSnapshot.getValue(Users.class);
                            Glide.with(getApplicationContext()).load(user.getPhoto()).apply(RequestOptions.circleCropTransform())
                                    .thumbnail(0.3f).into(image);
                            name.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
          //  }



        }
        @Override
        public int getItemCount() {
            Log.d("MYMESSAGE", "get Item Count Called");
            return members.size();
        }

    }

}
