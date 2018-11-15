package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class MyMeetingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MeetingsListRecycler myMeetingsAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> meetings;
    private ArrayList<Meetings> meetingsData;
    //private ArrayList<Users> adminal;
    private TextView my_total_meetings_tv;
    private int meetingIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meetings);
            this.setTitle("My Meetings");
            meetings=new ArrayList<>();
            meetingsData=new ArrayList<>();

            Intent in=getIntent();
            my_total_meetings_tv = findViewById(R.id.my_total_meetings_tv);
            recyclerView = (RecyclerView) (findViewById(R.id.my_meetings_rcv));
            myMeetingsAdapter = new MeetingsListRecycler();
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myMeetingsAdapter);
        showMeetingsList();
        }



    public void showMeetingsList(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber)
                .child("Meetings");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetings=(ArrayList<String>)dataSnapshot.getValue();
                if(meetings==null){
                    meetings=new ArrayList<>();
                }
                myMeetingsAdapter.notifyDataSetChanged();
//                        meetingIndex=0;
//                        meetingsData.add(new Meetings(ds.getValue(Meetings.class)));
//                        Log.d("MEETINGS_DATA","data got for index :_"+meetingIndex);
//                        myMeetingsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        class MeetingsListRecycler extends RecyclerView.Adapter<MeetingsListRecycler.MyViewHolder> {
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
            public MeetingsListRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View viewthatcontainscardview = inflater.inflate(R.layout.my_meetings_cardview, parent, false);
                CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.my_meetings));
                // This will call Constructor of MyViewHolder, which will further copy its reference
                // to customview (instance variable name) to make its usable in all other methods of class
                return new MyViewHolder(cardView);
            }

            @Override
            public void onBindViewHolder(MeetingsListRecycler.MyViewHolder holder, final int position) {

                CardView localcardview = holder.singlecardview;
                TextView my_meeting_title_tv, my_meeting_date_tv, my_meeting_time_tv;

                ImageView my_meeting_info_iv;

               my_meeting_title_tv =  localcardview.findViewById(R.id.my_meeting_title_tv);
                my_meeting_date_tv = (TextView) localcardview.findViewById(R.id.my_meeting_date_tv);
                my_meeting_time_tv= (TextView) localcardview.findViewById(R.id.my_meeting_time_tv);
                my_meeting_info_iv=localcardview.findViewById(R.id.my_meeting_info_iv);

//                databaseReference = firebaseDatabase.getReference("Users").child(no);
//                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        //Log.d("THEUsERIDIS",dataSnapshot.getValue(Users.class).toString());
//                        if (dataSnapshot.getValue(Users.class) == null) {
//
//                        } else {
//                            Users users = dataSnapshot.getValue(Users.class);
////                        Log.d("MYMSG",users.getPhoneNumber());
//                            String ownerName = users.getName();
//                            ownerName_tv.setText(ownerName);
//                            Glide.with(getApplicationContext()).load(users.getPhoto()).apply(RequestOptions.circleCropTransform())
//                                    .thumbnail(0.3f).into(owner_photo_iv);
//                            ownerPhone_tv.setText(users.getPhoneNumber());
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
                for(int i=0;i<meetings.size();i++){
                    String meeting=meetings.get(position);
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference=firebaseDatabase.getReference("Meetings").child(meeting);
                            Log.d("MY_MEETING","onBINDVIEWHOLDER CALLED FOR TIMES :-"+i);
//                            final int meetingIndex=i;
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Meetings meetingData=dataSnapshot.getValue(Meetings.class);
                                    meetingsData.add(new Meetings(meetingData));
                                    my_meeting_title_tv.setText(meetingData.getMeetingAgenda());
                                    my_meeting_date_tv.setText(meetingData.getMeetingDate());
                                    my_meeting_time_tv.setText(meetingData.getMeetingTime());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                }
                my_meeting_info_iv.setOnClickListener((view)->
                {
                    Intent meetingInfoIntent = new Intent(getApplicationContext(),MeetingInfoDialog.class);
                    meetingInfoIntent.putExtra("meeting",meetingsData.get(position).getMeetingId());
                    Log.d("MEETING_CODE",meetingsData.get(position).getMeetingId());
                    startActivity(meetingInfoIntent);
                });

                localcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public int getItemCount() {
                Log.d("MYMESSAGE", "get Item Count Called");
                return meetings.size();
            }


        }


}
