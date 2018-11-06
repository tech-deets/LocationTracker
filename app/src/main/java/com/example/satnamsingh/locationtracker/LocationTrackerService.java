package com.example.satnamsingh.locationtracker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LocationTrackerService extends Service {
    boolean runningflag;
    ArrayList<Locations> userLocations;
    mylocationlistener ml;
    LocationManager lm;
    public LocationTrackerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MYMESAGE","ON Start Command Called");
        Log.d("MYMESSAGE",intent.getAction());

        if(intent.getAction().trim().equals("START SIGNAL") && runningflag==false)
        {
            startForegroundService();
            runningflag=true;
        }
        else if(intent.getAction().trim().equals("STOP SIGNAL"))
        {
            stopForegroundService(intent);
        }


        return super.onStartCommand(intent, flags, startId);
    }
    void startForegroundService()
    {
        //Logic to create a Foreground Service
        // Logic to be run in Service
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastlcgps= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Location lastlcnw= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //---check if GPS_PROVIDER is enabled---
        boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //---check if NETWORK_PROVIDER is enabled---
        boolean networkStatus = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        ml=new mylocationlistener();

        if (gpsStatus==false && networkStatus==false)
        {
            Toast.makeText(this , "Both GPS and Newtork are disabled", Toast.LENGTH_LONG).show();
            //---display the "Location services" settings page---
            Intent in = new  Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(in);
        }

        if(gpsStatus==true)
        {
            //Toast.makeText(this, "GPS is Enabled, using it", Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,0, ml);
        }

        if(networkStatus==true)
        {
           // Toast.makeText(this, "Network Location is Enabled, using it", Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,0, ml);
        }


       // new Thread(new myjob()).start();

    }
    void stopForegroundService(Intent intent)
    {
        Log.d("MYMESSAGE","STOP Service Called");
        runningflag=false;
        lm.removeUpdates(ml);
        ml=null;
        stopSelf();
        stopService(intent);
    }
    public Notification simpleNotification(String title,String message)
    {
        String CHANNEL_ID=  "CHANNEL222";

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.location_icon);
        builder.setContentInfo("Con Info");

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        builder.setLargeIcon(bmp);

        // We can Specify Activity to be launched here
        Intent in=new Intent(this,UserHomeActivity.class);
        PendingIntent pin=PendingIntent.getActivity(this,0,in,0);

        builder.setContentIntent(pin);

        // Auto Cancel Notification after click (to launch activity)
        builder.setAutoCancel(true);

        // For Permanent Notification
        builder.setOngoing(true);


        // EXTRA Code needed (for devcies < 8.0), since we are creating channels
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManager notificationManager = (NotificationManager)(getSystemService(NOTIFICATION_SERVICE));


        Notification notification =  builder.build();

        //////////// EXTRA CODE  to Handle Oreo Devices   ///////////
        ////// Since Oreo Devices uses Notification Channels    /////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            CharSequence name = "My Channel Name";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_NONE;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
            Log.d("MYMESSAGE","NEW CODE Oreo");
        }

          return notification;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    class myjob implements Runnable
    {   Location location;
         myjob(Location location){
               this.location=location;
         }

        @Override
        public void run() {
            userLocations=new ArrayList<>();
            Notification mynotif = simpleNotification("Location sharing" ,"updating location");
            startForeground(1,mynotif);
            double latitude,longitude;
            SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
            String phoneNumber=sharedPreferences.getString("phoneNumber","");
            //to get the current time
            long time = System.currentTimeMillis();
            System.out.println(time+"=--------------------");
            //to get the curren date in specific format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());
            System.out.println(date+"=--------------------");
            latitude= location.getLatitude();
            longitude=  location.getLongitude();
            Locations userLocation =new Locations(latitude,longitude,date,time);
            System.out.println(userLocation.getLatitude());

// check if the below code for adding location need to be added to the code above to add last location
// . if execution flow is disturbed
            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
            DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(phoneNumber).
                    child("Locations").child(time+"");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()==null){
                        //System.out.println("---arraylist empty");
                    }else{
                        //userLocations=(ArrayList<Locations>)dataSnapshot.getValue();

                    }
                    Log.d("LOCATIONMSG","-----"+userLocations.size());
                    //userLocations.add(userLocation);
                    databaseReference.setValue(userLocation);
                    Log.d("LOCATIONMSG-AFTERADDING","-----"+userLocations.size());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDatabase lastLocationReference=FirebaseDatabase.getInstance();
            DatabaseReference lastLocation_db=lastLocationReference.getReference("Users").child(phoneNumber)
                    .child("LastLocation");
            lastLocation_db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference lastLocationLatitude =lastLocation_db.child("Latitude");
                    DatabaseReference lastLocationLongitude=lastLocation_db.child("Longitude");
                    lastLocationLatitude.setValue(latitude);
                    lastLocationLongitude.setValue(longitude);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Toast.makeText(getApplicationContext(), ""+date, Toast.LENGTH_SHORT).show();
            //   Toast.makeText(getApplicationContext(), "location changed"+lat+" \n"+lon, Toast.LENGTH_SHORT).show();
           //previous notification called

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


        }
    }
    class mylocationlistener implements LocationListener
    {
        public void onLocationChanged(Location location)
        {
            new Thread(new myjob(location)).start();

        }

        public void onProviderDisabled(String provider)
        {

        }

        public void onProviderEnabled(String provider)
        {

        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }


}
