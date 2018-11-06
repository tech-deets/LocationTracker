package com.example.satnamsingh.locationtracker;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class MyContactActivity extends AppCompatActivity {
    private String phoneNumber;
    private ListView lv;
    private ArrayList<Users> contactList;
    private ArrayList<Users> databaseList;
    private ArrayList<Users> filteredList;
    private ArrayList<GroupList>  groupList;
    private ArrayList<String> members;
    GroupData groupData;private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView rcv1,rcv2;
    private MyRecyclerAdapter myRecyclerAdapter;
    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        this.setTitle("Create Group");

        rcv2=(RecyclerView)(findViewById(R.id.rcv2));
        groupListAdapter =new GroupListAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv2.setAdapter(groupListAdapter);
        rcv2.setLayoutManager(layoutManager);

        contactList = new ArrayList<>();
         databaseList = new ArrayList<>();
         filteredList = new ArrayList<>();
          groupList =new ArrayList<>();
         members=new ArrayList<>();
        filteredList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNumber(getApplicationContext().getContentResolver());
                getDatabaseList();
            }
        }).start();
    }///////////////////////on create ends

    public void getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int count = 0;
            for (int i = 0; i < contactList.size(); i++) {
                String phone = contactList.get(i).getPhoneNumber();
                if (phone.equals(phoneNumber)) {
                    count = 1;
                    break;
                }
            }
            if (count == 0) {
                contactList.add(new Users(name, phoneNumber, "", ""));
            }
        }
        phones.close();// close cursor
    }

    public void getDatabaseList() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDS : dataSnapshot.getChildren()) {
                    Users user = singleDS.getValue(Users.class);
                  //  Log.d("MYCONTACTAVTIVITY",GlobalData.phoneNumber);
                    if(user.getPhoneNumber().contains(GlobalData.phoneNumber))
                        continue;
                    databaseList.add(new Users(user.getName(), user.getPhoneNumber(), user.getEmail(), user.getPhoto()));
                }
                int size = databaseList.size();
 /////////////// following two lines are used to get common contacts///////////////////
                filteredList = databaseList;
                filteredList.retainAll(contactList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showList();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MYMSG","error while fetching");
            }
        });
    }

    public void showList(){
        RecyclerView rcv;
        rcv=(RecyclerView)(findViewById(R.id.rcv1));
        MyRecyclerAdapter myad=new MyRecyclerAdapter();
        rcv.setAdapter(myad);
        LinearLayoutManager simpleverticallayout= new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
    }
    /////// Inner Class  ////////
    // Create ur own RecyclerAdapter
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
    public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position)
    {
        CardView localcardview = holder.singlecardview;
        TextView contactName_tv,contactPhone_tv;
        CheckBox contactInvite_cb;
        final ImageView contactPhoto_iv;


        contactName_tv=(TextView)(localcardview.findViewById(R.id.contactName_tv));
        contactPhone_tv=(TextView) (localcardview.findViewById(R.id.contactPhone_tv));
        contactInvite_cb=(CheckBox) (localcardview.findViewById(R.id.contactInvite_cb));
        contactPhoto_iv=(ImageView)(localcardview.findViewById(R.id.contactPhoto_iv));

        Users i=filteredList.get(position);

        contactName_tv.setText(i.getName());
        contactPhone_tv.setText(i.getPhoneNumber());
        Glide.with(getApplicationContext()).load(i.getPhoto()).apply(RequestOptions.circleCropTransform()).thumbnail(0.3f).into(contactPhoto_iv);
        localcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();
                if (contactInvite_cb.isChecked()) {
                    contactInvite_cb.setChecked(false);
                }else
                {
                    contactInvite_cb.setChecked(true);

                }
            }
        });
        contactInvite_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String removePhone;
                if(isChecked){
                    String name=filteredList.get(position).getName();
                    String photo=filteredList.get(position).getPhoto();
                    String phoneNumber=filteredList.get(position).getPhoneNumber();
                    groupList.add(new GroupList(name,photo,phoneNumber));
//                           showList1();
                    groupListAdapter.notifyDataSetChanged();
                }else{
                    //removePhone=filteredList.get(position).getPhoneNumber();
                    int count=0;
                    for(GroupList gp :groupList){

                        if(gp.getPhoneNumber().equals(filteredList.get(position).getPhoneNumber())) {
                            groupList.remove(count);
                            groupListAdapter.notifyDataSetChanged();
                            break;
                        }
                        count++;
                    }
                }
            }
        });
    }
        @Override
    public int getItemCount() {
        Log.d("MYMESSAGE","get Item Count Called");
        return filteredList.size();
    }
    ////////////////////////////
    }
    /////// Inner Class  ////////
    // Create ur own RecyclerAdapter
    class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder1>
    {
        // Define ur own View Holder (Refers to Single Row)
        class MyViewHolder1 extends RecyclerView.ViewHolder
        {
            CardView singlecardview;
            // We have Changed View (which represent single row) to CardView in whole code
            public MyViewHolder1(CardView itemView) {
                super(itemView);
                singlecardview = (itemView);
            }
        }
        // Inflate ur Single Row / CardView from XML here
        @Override
        public GroupListAdapter.MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.cardviewgroup,parent,false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview2));
            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            Log.d("MYMESSAGE","On CreateView Holder Done");
            return new MyViewHolder1(cardView);
        }

        @Override
        public void onBindViewHolder(GroupListAdapter.MyViewHolder1 holder, final int position)
        {
            CardView localcardview = holder.singlecardview;
            localcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();
                }
            });
            TextView contactName_tv;

            final ImageView contactPhoto_iv;


            contactName_tv=(TextView)(localcardview.findViewById(R.id.contactName1_tv));
            contactPhoto_iv=(ImageView)(localcardview.findViewById(R.id.contactPhoto1_iv));

            GroupList i=groupList.get(position);

            contactName_tv.setText(i.getName());
            Glide.with(getApplicationContext()).load(i.getPhoto()).apply(RequestOptions.circleCropTransform()).thumbnail(0.3f).into(contactPhoto_iv);
            Log.d("MYMESSAGE","On Bind Of View Holder Called");
        }

        @Override
        public int getItemCount() {

            Log.d("MYMESSAGE","get Item Count Called");

            return groupList.size();
        }

        ////////////////////////////

    }

    public void createGroup(View v){
        EditText groupName_et=findViewById(R.id.groupName_et);
        final String groupName =groupName_et.getText().toString();
        boolean emptyFields;
        if (groupName.trim().equals("")|| groupList.size()==0) {
            emptyFields=true;
            if(groupName.trim().equals(""))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Provide a Group Name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(groupList.size()==0)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "group clicked name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }

        }else {
            emptyFields = false;
        }
        if (!emptyFields) {

            FirebaseDatabase firebaseDatabase;
            final DatabaseReference databaseReference;
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Groups");
            String owner = GlobalData.phoneNumber;

            members.add(GlobalData.phoneNumber);

            DatabaseReference pushData = databaseReference.push();
            final String pushId = pushData.getKey();
            Toast.makeText(getApplicationContext(), "the group is created", Toast.LENGTH_SHORT).show();
            Log.d("thepushidforthegroupis", pushId);
            groupData = new GroupData(pushId, groupName, owner, members);
            databaseReference.child(pushId).setValue(groupData);

            final DatabaseReference groupcodedb = FirebaseDatabase.getInstance().getReference("Users")
                    .child(GlobalData.phoneNumber).child("GroupCode");

            groupcodedb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ArrayList<String> grpCodes = (ArrayList<String>) dataSnapshot.getValue();
                    if(grpCodes == null){
                        grpCodes = new ArrayList<>();
                    }
                    grpCodes.add(pushId);
                    groupcodedb.setValue(grpCodes);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            final DatabaseReference groupNamedb = FirebaseDatabase.getInstance().getReference("Users")
                    .child(GlobalData.phoneNumber).child("GroupName");
            groupNamedb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> groupNameal =(ArrayList<String>)dataSnapshot.getValue();
                    if(groupNameal==null)
                        groupNameal = new ArrayList<>();
                    groupNameal.add(groupName);
                    groupNamedb.setValue(groupNameal);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            for(int i = 0; i<groupList.size();i++)
            {
                final DatabaseReference memberref_db = FirebaseDatabase.getInstance().getReference("Users").
                        child(groupList.get(i).getPhoneNumber()).child("Invitations");
                memberref_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> invitational = (ArrayList<String>)dataSnapshot.getValue();
                        if (invitational==null)
                            invitational =new  ArrayList<>();
                        invitational.add(pushId);
                        memberref_db.setValue(invitational);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
            this.finish();
        }
    }

}
