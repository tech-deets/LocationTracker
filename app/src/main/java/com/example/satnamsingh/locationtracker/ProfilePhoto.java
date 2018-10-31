package com.example.satnamsingh.locationtracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfilePhoto extends AppCompatActivity {
    private ImageView imv1;
    private Uri uri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String phoneNumber;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilephoto);
        imv1=(ImageView)(findViewById(R.id.imv1));
        imv1.setImageResource(R.drawable.default_pic);
        Intent in= getIntent();
        phoneNumber=in.getStringExtra("phone");
        pd = new ProgressDialog(this);
        pd.setTitle("Uploading profile photo");
        pd.setMessage("please wait while picture is being uploaded\nDo not close the app!!");
        pd.setCancelable(false);
        findViewById(R.id.skipbt).setOnClickListener((view)-> {
                firebaseDatabase=FirebaseDatabase.getInstance();
                databaseReference=firebaseDatabase.getReference("Users");
                databaseReference.child(phoneNumber).child("photo").
                        setValue("https://firebasestorage.googleapis.com/v0/b/locationtracker-28250.appspot.com/o/default_pic.jpg?alt=media&token=7ed5b932-09bf-422e-aa5e-77eca30f378f");
                Intent uin = new Intent(getApplicationContext(),UserHomeActivity.class);
                startActivity(uin);
                finish();
        }
        );

    }
    //Launch Camera Activity
    public void openCamera(View v)
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (checkPermission()) {
//                Toast.makeText(this, "All Permissions Already Granted", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(in, 100);
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }
    //Launch Gallery
    public void openGallery(View v)
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (checkPermission()) {
//                Toast.makeText(this, "All Permissions Already Granted", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Intent.ACTION_PICK);
                //Filter for image type
                in.setType("image/*");
                startActivityForResult(in, 110);
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent backintent)
    {
        if(requestCode==100)  //back from camera
        {
            if(resultCode==RESULT_OK)
            {
                imv1.setImageBitmap(null);
                Bitmap bmp = (Bitmap) (backintent.getExtras().get("data"));
                uri = getImageUri(getApplicationContext(),bmp);
               imv1.setImageURI(uri);

            }
        }
        else if(requestCode==110)  //back from gallery
        {
            if(resultCode==RESULT_OK)
            {
                imv1.setImageBitmap(null);
                uri = backintent.getData();
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 400, 400, true);
                uri = getImageUri(getApplicationContext(),bitmap);
                imv1.setImageURI(uri);
            }
        }
    }


    public void uploadPhoto(View v){
        // String phoneNumber="9780338031";
        Log.d("phone number",phoneNumber);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference userReference = storageReference.child(phoneNumber+".jpeg");
        if(uri==null){
            Log.d("uri passed is : ","null");
        }
        else {
            Log.d("uri passed",uri.toString());

            UploadTask photoUploadTask = userReference.putFile(uri);
            photoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference =storageReference.child(phoneNumber+".jpeg");
                    Task<StorageMetadata> mytask =storageReference.getMetadata();
                    mytask.addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            String photo=storageMetadata.getDownloadUrl().toString();
                            Log.d("url of the file",photo);
                            firebaseDatabase=FirebaseDatabase.getInstance();
                            databaseReference=firebaseDatabase.getReference("Users");
                            databaseReference.child(phoneNumber).child("photo").setValue(photo);
                            pd.dismiss();
                            Toast.makeText(ProfilePhoto.this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(),UserHomeActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
                }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ProfilePhoto.this, "Something wrong,\nUpload Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.show();
                        }
                    });

        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
/////////////   Following Functions are for run time permission on button click  /////////////////////

    public boolean checkPermission()
    {
        boolean result1 =
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
        return result1;
    }
    public void requestPermission()
    {
        //Show ASK FOR PERSMISSION DIALOG (passing array of permissions that u want to ask)
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    // After User Selects Desired Permissions, thid method is automatically called
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0)
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
//                    Toast.makeText(this, "Write External permission granted ", Toast.LENGTH_SHORT).show();
                }
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
//                    Toast.makeText(this,"Write External Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // It has request code, permissions array and corresponding grantresults array
    //////////////////////////////////////////////////////////////////////////////////////////////////////

}
