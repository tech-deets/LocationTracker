package com.example.satnamsingh.locationtracker;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private MyRecyclerAdapter myRecyclerAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> invitational;
    private ArrayList<GroupData> groupal;
    private ArrayList<Users> adminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invitations);

        adminal = new ArrayList<>();

        recyclerView=(RecyclerView)(findViewById(R.id.invites_rcv));
        myRecyclerAdapter = new MyRecyclerAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(GlobalData.phoneNumber).child("Invitations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 invitational = (ArrayList<String>)dataSnapshot.getValue();
                if (invitational==null)
                    invitational =new  ArrayList<>();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MYMSG","error while fetching");
            }
        });
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>
    {
        // Define ur own View Holder (Refers to Single Row)
        class MyViewHolder extends RecyclerView.ViewHolder
        {
            CardView singlecardview;
            public MyViewHolder(CardView itemView) {
                super(itemView);
                singlecardview = (itemView);
            }
        }

        // Inflate ur Single Row / CardView from XML here
        @Override
        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.cardviewcontact,parent,false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardviewcontact));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;
            TextView contactName_tv, contactPhone_tv;
            CheckBox contactInvite_cb;
            final ImageView contactPhoto_iv;

            for (int i = 0; i < invitational.size(); i++) {
                final DatabaseReference memberref_db = FirebaseDatabase.getInstance().getReference("Groups").
                        child(invitational.get(i));
                memberref_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupData group = dataSnapshot.getValue(GroupData.class);
                        if (groupal == null)
                            groupal = new ArrayList<>();
                        groupal.add(group);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            for (int j=0;j<groupal.size();j++)
            {
                final DatabaseReference user_db = FirebaseDatabase.getInstance().getReference("Users").
                        child(groupal.get(i).getGroupOwner());
                user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users admin = dataSnapshot.getValue(Users.class);
                        if(adminal==null)
                            adminal = new ArrayList<>();
                        adminal.add(admin);
                        Log.d("MYMSG INVITE Admin",admin+"\n");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

//                contactName_tv = (TextView) (localcardview.findViewById(R.id.contactName_tv));
//                contactPhone_tv = (TextView) (localcardview.findViewById(R.id.contactPhone_tv));
//                contactInvite_cb = (CheckBox) (localcardview.findViewById(R.id.contactInvite_cb));
//                contactPhoto_iv = (ImageView) (localcardview.findViewById(R.id.contactPhoto_iv));
//
//                Users i = filteredList.get(position);
//
//                contactName_tv.setText(i.getName());
//                contactPhone_tv.setText(i.getPhoneNumber());
//                Glide.with(getApplicationContext()).load(i.getPhoto()).apply(RequestOptions.circleCropTransform()).thumbnail(0.3f).into(contactPhoto_iv);
                localcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();

                    }
                });

            }
        }
        @Override
        public int getItemCount() {
            Log.d("MYMESSAGE","get Item Count Called");
            return invitational.size();
        }
        ////////////////////////////
    }
}
