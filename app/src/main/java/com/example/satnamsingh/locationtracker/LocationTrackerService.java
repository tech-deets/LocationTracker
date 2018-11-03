package com.example.satnamsingh.locationtracker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationTrackerService extends Service {
    boolean runningflag;

    public LocationTrackerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MYMESAGE","ON Start Command Called");
        Log.d("MYMESSAGE",intent.getAction());

        if(intent.getAction().trim().equals("START SIGNAL") && runningflag==false)
        {
            runningflag=true;
            startForegroundService();
        }
        else if(intent.getAction().trim().equals("STOP SIGNAL")&&runningflag==true)
        {
            runningflag=false;
            stopForegroundService(intent);
        }


        return super.onStartCommand(intent, flags, startId);
    }
    void startForegroundService()
    {
        //Logic to create a Foreground Service

      /*  Notification mynotif = simpleNotification("hello","Foreground Notification Running");

        startForeground(1,mynotif);*/

        // Logic to be run in Service
        new Thread(new myjob()).start();

    }
    void stopForegroundService(Intent intent)
    {
        Log.d("MYMESSAGE","STOP Service Called");
        runningflag=false;
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

        // DONT Notify here
        // It will be done by ForegroundService
        //notificationManager.notify(20,notification);

        //notificationManager.cancel(20);

        return notification;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    class myjob implements Runnable
    {
        @Override
        public void run() {


               // Log.d("MYMESSAGE", i+"");

                Notification mynotif = simpleNotification("Location sharing" ,"updating location");

                startForeground(1,mynotif);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        }
    }
}
