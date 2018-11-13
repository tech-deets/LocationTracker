package com.example.satnamsingh.locationtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyGroupsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyGroupsRecycler myRecyclerAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> groupCodes_al;
    private ArrayList<GroupData> groupal;
    private ArrayList<Users> adminal;
    private TextView total_group_tv;
   // private TextView total_invites_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups_activity);
        this.setTitle("My Groups");
        adminal = new ArrayList<>();
        total_group_tv = findViewById(R.id.total_group_tv);

       // total_invites_tv = findViewById(R.id.total_invites_tv);
        recyclerView = (RecyclerView) (findViewById(R.id.mygroups_rcv));
        myRecyclerAdapter = new MyGroupsRecycler();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration
                (MyGroupsActivity.this,layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        groupCodes_al = new ArrayList<>();
        groupal = new ArrayList<>();
        adminal = new ArrayList<>();
        new Thread(new GetData()).start();
    }
    public class GetData implements Runnable {

        @Override
        public void run() {

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber).child("GroupCode");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupCodes_al.clear();
                    groupal.clear();

                    groupCodes_al = (ArrayList<String>) dataSnapshot.getValue();
                    if (groupCodes_al == null) {
                        groupCodes_al = new ArrayList<>();
                        myRecyclerAdapter.notifyDataSetChanged();
                        Log.d("\n\nMYMSG", "list is empty---------------");

                    }
                    total_group_tv.setText(""+ groupCodes_al.size());

                    Log.d("\n\nMYMSG", "-----list is full---------------");

                    for (int i = 0; i < groupCodes_al.size(); i++) {
                        Log.d("groups: ", groupCodes_al.get(i));
                        String groupId = groupCodes_al.get(i);
                        total_group_tv.setText(""+ groupCodes_al.size());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference inviteRef = firebaseDatabase.getReference("Groups").child(groupId);

                        inviteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GroupData groupData = dataSnapshot.getValue(GroupData.class);
                                if (groupData!=null) {
                                    Log.d("GroupName: ", groupData.getGroupName());
                                    groupal.add(groupData);
                                    myRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("MYMSG", "error while fetching");
                }
            });


        }
    }
    class MyGroupsRecycler extends RecyclerView.Adapter<MyGroupsRecycler.MyViewHolder> {
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
        public MyGroupsRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.mygroups_cardview, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.mygroups_cardview));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(MyGroupsRecycler.MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;

            TextView groupName_tv, ownerName_tv, ownerPhone_tv;
            ImageView owner_photo_iv,groupInfo_iv;
            String ownerNameGbl,ownerPhoto;

            owner_photo_iv = (ImageView) localcardview.findViewById(R.id.ownerPhoto_iv);
            groupName_tv = (TextView) localcardview.findViewById(R.id.groupName_tv);
            ownerName_tv = (TextView) localcardview.findViewById(R.id.ownerName_tv);
            ownerPhone_tv = (TextView) localcardview.findViewById(R.id.ownerPhone_tv);
            groupInfo_iv = (ImageView) localcardview.findViewById(R.id.groupInfo_iv);

            GroupData groupData = groupal.get(position);
            String groupName = groupData.getGroupName();
            groupName_tv.setText("Group: " + groupName);
            String no = groupData.getGroupOwner();
            Log.d("MYMSG NUMBER: ", no);
            databaseReference = firebaseDatabase.getReference("Users").child(no);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.d("THEUsERIDIS",dataSnapshot.getValue(Users.class).toString());
                    if (dataSnapshot.getValue(Users.class) == null) {

                    } else {
                        Users users = dataSnapshot.getValue(Users.class);
                         String ownerName = users.getName();
                        ownerName_tv.setText(ownerName);
                        Glide.with(getApplicationContext()).load(users.getPhoto()).apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.3f).into(owner_photo_iv);
                        ownerPhone_tv.setText(users.getPhoneNumber());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            groupInfo_iv.setOnClickListener(( view)-> {
                Intent in =new Intent(getApplicationContext(),GroupInfoDialogActivity.class);
                Toast.makeText(MyGroupsActivity.this, "card view clicked  "+position, Toast.LENGTH_LONG).show();
                in.putExtra("groupName",groupName);
                in.putExtra("groupId",groupData.getGroupId());
                startActivity(in);
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
          //  return groupal.size();
            int size=groupal.size();
            return size;
        }
    }
}
