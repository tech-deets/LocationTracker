package com.example.satnamsingh.locationtracker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyContactActivity extends AppCompatActivity {
    private String phoneNumber;
    private ListView lv;
    static private ArrayList<Users> contactList = new ArrayList<>();
    static private ArrayList<Users> databaseList = new ArrayList<>();
    static private ArrayList<Users> filteredList = new ArrayList<>();
    static private ArrayList<GroupList>  groupList =new ArrayList<>();

    // private ArrayList<Users>
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    RecyclerView rcv1,rcv2;
    MyRecyclerAdapter myad;
    MyRecyclerAdapter1 myad1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        this.setTitle("My Contacts");
        rcv2=(RecyclerView)(findViewById(R.id.rcv2));
        myad1=new MyRecyclerAdapter1();
        rcv2.setAdapter(myad1);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv2.setLayoutManager(layoutManager);
        filteredList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNumber(getApplicationContext().getContentResolver());
                printList();
                getDatabaseList();
//                runOnUiThread();
            }
        }).start();
//        rcv1.addOnItemTouchListener(new RecyclerItemClickListener(this, rcv1, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                }
//
//        }));
        rcv1.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }

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

    public void printList() {
        int size = contactList.size();
        Log.d("No. of Contacts", size + "");
        int i = 0;
        while (i < size) {
            Log.d("Name: ", contactList.get(i).getName()+"Phone: "+contactList.get(i).getPhoneNumber());
            i++;
        }
    }

    public void getDatabaseList() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        // final DatabaseReference myref=databaseReference.child(phoneNumber);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDS : dataSnapshot.getChildren()) {
                    Users user = singleDS.getValue(Users.class);
                    databaseList.add(new Users(user.getName(), "+91"+user.getPhoneNumber(), user.getEmail(), user.getPhoto()));
                }
                int size = databaseList.size();
                Log.d("No. of Contacts", size + "");
                int i = 0;
                while (i < size) {
                    Log.d("name", databaseList.get(i).getName());
                    Log.d("number", databaseList.get(i).getPhoneNumber());
                    Log.d("PhotoUrl", databaseList.get(i).getPhoto());
                    i++;
                }
                Toast.makeText(getApplicationContext(), "the datase list is fetched", Toast.LENGTH_SHORT).show();
                // FirebaseDatabase.getInstance().goOffline();
                getFilteredList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tv1.setText(x+"");
                        showList();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getFilteredList() {
        Log.d("inside filter list", "start list------------");
        filteredList = databaseList;
        Log.d("FilterList: ", filteredList.size()+"");
        filteredList.retainAll(contactList);
        int size = filteredList.size();
        Log.d("NoOfContacts: ", size + "");
        int i = 0;
        while (i < size) {
            Log.d("FilterName: ", filteredList.get(i).getName()+" Filter Number: "+filteredList.get(i).getPhoneNumber());
            Log.d("PhotoUrl", filteredList.get(i).getPhoto());
            i++;
        }
    }

    public void showList(){
        RecyclerView rcv;
        rcv=(RecyclerView)(findViewById(R.id.rcv1));
        MyRecyclerAdapter myad=new MyRecyclerAdapter();
        rcv.setAdapter(myad);
        //Specifying Layout Manager to RecyclerView is Compulsary for Proper Rendering
        LinearLayoutManager simpleverticallayout= new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
        Log.d("MYMESSAGE","On Create of RecyclerView Demo Called");
    }
    /////// Inner Class  ////////
    // Create ur own RecyclerAdapter
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>
    {
        // Define ur own View Holder (Refers to Single Row)
        class MyViewHolder extends RecyclerView.ViewHolder
        {
            CardView singlecardview;
            // We have Changed View (which represent single row) to CardView in whole code
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
        Log.d("MYMESSAGE","On CreateView Holder Done");
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position)
    {
        CardView localcardview = holder.singlecardview;
        localcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getApplicationContext(),position+" clicked",Toast.LENGTH_LONG).show();
            }
        });
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
        Glide.with(getApplicationContext()).load(i.getPhoto()).thumbnail(0.3f).into(contactPhoto_iv);
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
                    myad1.notifyDataSetChanged();
                }else{
                    //removePhone=filteredList.get(position).getPhoneNumber();
                    int count=0;
                    for(GroupList gp :groupList){

                        if(gp.getPhoneNumber().equals(filteredList.get(position).getPhoneNumber())) {
                            groupList.remove(count);
                            myad1.notifyDataSetChanged();
                            break;

                        }
                        count++;
                    }



                }
            }
        });
        Log.d("MYMESSAGE","On Bind Of View Holder Called");
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
    class MyRecyclerAdapter1 extends RecyclerView.Adapter<MyRecyclerAdapter1.MyViewHolder1>
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
        public MyRecyclerAdapter1.MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType)
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
        public void onBindViewHolder(MyRecyclerAdapter1.MyViewHolder1 holder, final int position)
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
            Glide.with(getApplicationContext()).load(i.getPhoto()).thumbnail(0.3f).into(contactPhoto_iv);
            Log.d("MYMESSAGE","On Bind Of View Holder Called");
        }

        @Override
        public int getItemCount() {

            Log.d("MYMESSAGE","get Item Count Called");

            return groupList.size();
        }

        ////////////////////////////

    }

}
