package com.example.satnamsingh.locationtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.HttpURLConnection;
import java.net.URL;


// This class is used to receive notification, print Log.d  and generate custom notification
// This class does not work when activity is not visible
// In that case firebase itself generates notification , this code is ignored

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // THERE IS A CONDITION IN THIS
        // lOG.D DOES NOT PRINT IF ACTIVITY IS NOT VISIBLE

        String TAG = "MYMESSAGE";

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        String ans = "";

        //from is the app number in cloud
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // IF Simple Notification Body is Missing and We have only PAYLOAD
        // SPL CASE: where we can recieve with Picture Notifcation
        if (remoteMessage.getNotification() == null)
        {
            Log.d(TAG, "notification is null, BOTH title and body missing");


            // Check if message contains a data payload.
            // PayLoad Contains key/value pairs which can be set in ADVANCE OPTIONS in Console
            if (remoteMessage.getData().size() > 0)
            {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                //Here Photo Uri is Coming in Payload
                Uri myuri = Uri.parse(remoteMessage.getData().get("imageurl"));

                //ALSO we might receive extratitle and extramessage

                String extratitle = remoteMessage.getData().get("extratitle");
                String extramessage =  remoteMessage.getData().get("extramessage");

                displaycustomnotification(myuri,extratitle,extramessage);


            }

        }
        else
        {
            // Notification Body is Available, generate simple notification
            // since it will not be auto generated, since GUI will be open

            Log.d(TAG, "Message TITLE: " + remoteMessage.getNotification().getTitle());

            // message text
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            displaySimpleNotificationWhenGUIOpen(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

            //  **** NOT Compulsary, but may be useful in some cases  *****
            // ALSO IF PAYLOAD IS COMING along with simple notification
            // You can write Extra Code on how to use it.
            // ************************************************************
        }




        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }


    public void displaycustomnotification(Uri myuri, String extratitle, String extramessage)
    {

        String CHANNEL_ID=  "CHANNEL_VMM";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Notification Title");
        builder.setContentText("This is content text");
        builder.setContentInfo("Content Info");

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(ringtoneUri);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();

        try {
            Log.d("MYMESSAGE",myuri.toString());

            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(myuri.toString()).openConnection();

            Bitmap pic = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            bigPictureStyle.bigPicture(pic);
        } catch (Exception ex) {
            Log.d("MMMESSAGE", ex.getMessage());
        }

        bigPictureStyle.setSummaryText(extramessage);
        bigPictureStyle.setBigContentTitle(extratitle);
        builder.setStyle(bigPictureStyle);

        //Create Notification Object
        Notification n = builder.build();

        //Create Notification Manager Instance
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //////////// EXTRA CODE  to Handle Oreo Devices   ///////////
        ////// Since Oreo Devices uses Notification Channels    /////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            CharSequence name = "My Channel Name";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            // Register the channel with the system

            notificationManager.createNotificationChannel(channel);

        }


        ///send Notification using NotificationManager
        notificationManager.notify(20, n);


    }

    public void displaySimpleNotificationWhenGUIOpen(String mytitle, String mybody)
    {
        String CHANNEL_ID=  "CHANNEL_VMM";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(mytitle);
        builder.setContentText(mybody);
        builder.setContentInfo("");

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(ringtoneUri);
        Intent in=new Intent(this,MyMeetingsActivity.class);
        PendingIntent pin=PendingIntent.getActivity(this,0,in,0);

        builder.setContentIntent(pin);

        //Create Notification Object
        Notification n = builder.build();


        //Create Notification Manager Instance
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //////////// EXTRA CODE  to Handle Oreo Devices   ///////////
        ////// Since Oreo Devices uses Notification Channels    /////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            CharSequence name = "My Channel Name";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            // Register the channel with the system

            notificationManager.createNotificationChannel(channel);

        }

        ////// ---- Extra Code Ends here ---- /////


        ///send Notification using NotificationManager
        notificationManager.notify(20, n);
    }
}


