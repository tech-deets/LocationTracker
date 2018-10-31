package com.example.satnamsingh.locationtracker;

import android.media.Image;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.CheckBox;
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

public class MyInvitations extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InvitationListRecycler myRecyclerAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> invitational;
    private ArrayList<GroupData> groupal;
    private ArrayList<Users> adminal;
    private TextView total_invites_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invitations);

        adminal = new ArrayList<>();
        total_invites_tv = findViewById(R.id.total_invites_tv);
        recyclerView = (RecyclerView) (findViewById(R.id.invites_rcv));
        myRecyclerAdapter = new InvitationListRecycler();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        invitational = new ArrayList<>();
        groupal = new ArrayList<>();
        adminal = new ArrayList<>();

        new Thread(new GetData()).start();
    }

    public class GetData implements Runnable {

        @Override
        public void run() {

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber).child("Invitations");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    invitational = (ArrayList<String>) dataSnapshot.getValue();
                    if (invitational == null) {
                        invitational = new ArrayList<>();
                        Log.d("\n\nMYMSG", "list is empty---------------");
                    }
                    Log.d("\n\nMYMSG", "-----list is full---------------");
//                    Log.d("\n\nMYMSG", invitational.get(0) + "\n the size of invitation list is  :" + invitational.size() + "\n");

                    for (int i = 0; i < invitational.size(); i++) {
                        Log.d("Invites: ", invitational.get(i));
                        String invite = invitational.get(i);
                        total_invites_tv.setText(""+invitational.size());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference inviteRef = firebaseDatabase.getReference("Groups").child(invite);

                        inviteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                GroupData groupData = dataSnapshot.getValue(GroupData.class);
                                Log.d("GroupName: ", groupData.getGroupName());
                                groupal.add(groupData);
                                myRecyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

//                    for (int i = 0; i < groupal.size(); i++) {
//                        Log.d("MYMSG","group al is cancelled");
//
//                        final DatabaseReference user_db = FirebaseDatabase.getInstance().getReference("Users").
//                                child(groupal.get(i).getGroupOwner());
//                        user_db.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Users admin = dataSnapshot.getValue(Users.class);
//                                if (adminal == null)
//                                    adminal = new ArrayList<>();
//                                adminal.add(admin);
//                                Log.d("MYMSG INVITE Admin", admin + "\n");
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.d("MYMSG","admin al is cancelled");
//
//                            }
//                        });
//                      }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("MYMSG", "error while fetching");
                }
            });


        }
    }

    public void getGroups(View view) {



    }

    class InvitationListRecycler extends RecyclerView.Adapter<InvitationListRecycler.MyViewHolder> {
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
        public InvitationListRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.invites_cardview, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.invites_cardview));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(InvitationListRecycler.MyViewHolder holder, final int position) {

            CardView localcardview = holder.singlecardview;
            TextView groupName_tv, ownerName_tv, ownerPhone_tv;
            FloatingActionButton accept_bt, decline_bt;
            ImageView owner_photo_iv;

            owner_photo_iv = (ImageView)localcardview.findViewById(R.id.owner_photo_iv);
            groupName_tv = (TextView)localcardview.findViewById(R.id.groupName_tv);
            ownerName_tv = (TextView)localcardview.findViewById(R.id.ownerName_tv);
            ownerPhone_tv = (TextView)localcardview.findViewById(R.id.ownerPhone_tv);
            accept_bt = (FloatingActionButton) localcardview.findViewById(R.id.accept_bt);
            decline_bt = (FloatingActionButton) localcardview.findViewById(R.id.decline_bt);

            GroupData groupData = groupal.get(position);
            String groupName = groupData.getGroupName();
            groupName_tv.setText("Group: "+groupName);
            String no = groupData.getGroupOwner();

            databaseReference = firebaseDatabase.getReference("Users").child(no);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    String ownerName = users.getName();
                    ownerName_tv.setText(ownerName);
                    Glide.with(getApplicationContext()).load(users.getPhoto()).apply(RequestOptions.circleCropTransform())
                            .thumbnail(0.3f).into(owner_photo_iv);
                    ownerPhone_tv.setText(users.getPhoneNumber());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            accept_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            decline_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            localcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();

                }
            });


        }

        @Override
        public int getItemCount() {
            Log.d("MYMESSAGE", "get Item Count Called");
            return groupal.size();
        }
        ////////////////////////////
    }
}
