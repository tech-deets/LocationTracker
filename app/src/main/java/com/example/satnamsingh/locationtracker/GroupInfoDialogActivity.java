package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

public class GroupInfoDialogActivity extends AppCompatActivity {
ArrayList<String> groupMembers ;
    GroupData groupData;
    TextView groupName_tv ,ownerName_tv,ownerPhone_tv;
    Users owner;
    ImageView ownerPhoto_iv;
    MemberListRecycler myRecyclerAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.CustomDialogTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info_dialog);
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Intent in=getIntent();
        String groupId=in.getStringExtra("groupId");
        String groupName = in.getStringExtra("groupName");
        Log.d("MYMSGGROUPID",groupId);

        ownerName_tv=findViewById(R.id.ownerName_tv);
        ownerPhone_tv = findViewById(R.id.ownerPhone_tv);
        ownerPhoto_iv=findViewById(R.id.ownerPhoto_iv);
        groupName_tv=findViewById(R.id.groupName_tv);
        recyclerView = (RecyclerView) (findViewById(R.id.group_memebers_rcv));
        groupName_tv=findViewById(R.id.groupName_tv);

        groupName_tv.setText("Group : "+groupName);
        myRecyclerAdapter = new MemberListRecycler();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        groupMembers=new ArrayList<>();
        getGroupData(groupId);
        findViewById(R.id.close_dialog_fbt).setOnClickListener((view)->{
            finish();
        });
   }
    public void getGroupData(String groupId){


        FirebaseDatabase firebaseDatabase1 =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1=firebaseDatabase1.getReference("Groups").child(groupId);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupData=dataSnapshot.getValue(GroupData.class);
                Log.d("MYMSG",groupData.getGroupOwner());
                getOwnerData(groupData.getGroupOwner());
                FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
                DatabaseReference databaseReference=firebaseDatabase.getReference("Groups").child(groupId)
                        .child("groupMembers");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // groupData=dataSnapshot.getValue(GroupData.class);
                        //groupName_tv.setText(groupData.getGroupName());
                        groupMembers=(ArrayList<String>)dataSnapshot.getValue();
                        if(groupMembers==null){
                            groupMembers=new ArrayList<>();
                        }
//
                        Log.d("MYMSG",groupMembers.toString());
                        //    Log.d("MYMSG",groupData.getGroupId());
                        int index=-1;
                        for(int i=0;i<groupMembers.size();i++){
                            if(groupMembers.get(i).equals(groupData.getGroupOwner())){
                                index=i;
                                break;
                            }
                        }
                        if(index!=-1){
                            groupMembers.remove(index);
                        }

                        myRecyclerAdapter.notifyDataSetChanged();
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

    public void getOwnerData(String groupOwner){
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(groupOwner);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user=dataSnapshot.getValue(Users.class);
                if(user!=null) {
                    ownerName_tv.setText(user.getName());
                    Log.d("onwerphone",user.getName());
                    ownerPhone_tv.setText(user.getPhoneNumber());


                    Glide.with(getApplicationContext()).load(user.getPhoto()).apply(RequestOptions.circleCropTransform())
                            .thumbnail(0.3f).into(ownerPhoto_iv);
                }
                else{
                        Log.d("MYMSG","else executed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    class MemberListRecycler extends RecyclerView.Adapter<MemberListRecycler.MyViewHolder> {
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
        public MemberListRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.member_cardview, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.members_cardview));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(MemberListRecycler.MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;

            TextView memberName_tv, memberPhone_tv;
            ImageView member_photo_iv;


            //owner_photo_iv = (ImageView) localcardview.findViewById(R.id.ownerPhoto_iv);
            //Name_tv = (TextView) localcardview.findViewById(R.id.groupName_tv);
            memberName_tv = (TextView) localcardview.findViewById(R.id.memberName_tv);
            memberPhone_tv = (TextView) localcardview.findViewById(R.id.memberPhone_tv);
            member_photo_iv = (ImageView) localcardview.findViewById(R.id.member_photo_iv);

            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
            DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(groupMembers.get(position));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user =dataSnapshot.getValue(Users.class);
                    Log.d("MYMSG",user.getPhoneNumber());
                    Log.d("MYMSG",user.getName());
                    memberName_tv.setText(user.getName());
                    memberPhone_tv.setText(user.getPhoneNumber());
                    Glide.with(getApplicationContext()).load(user.getPhoto()).apply(RequestOptions.circleCropTransform())
                            .thumbnail(0.3f).into(member_photo_iv);
//                    Log.d("MYMSG",user.getPhoneNumber());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        @Override
        public int getItemCount() {
            Log.d("MYMESSAGE", "get Item Count Called");
            //  return groupal.size();
           // int size=a.size();
            return groupMembers.size();
        }

    }
   

}
