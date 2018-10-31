package com.example.satnamsingh.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity2 extends AppCompatActivity {
    private String phoneNumber;
    private EditText name_et,email_et;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button signupbt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);

        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phone");
        Log.d("MYMSG", "Phone No "+phoneNumber);
        name_et = findViewById(R.id.name);
        email_et = findViewById(R.id.email);
        signupbt2 = findViewById(R.id.signupbt2);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        signupbt2.setOnClickListener((view)->
        {
            try{

                String phonenumber = phoneNumber;
                String name= name_et.getText().toString();
                String email= email_et.getText().toString();
                Log.d("MYMSG for name: ",name+"hello");
                if(name == null||name.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "User name cannot be empty\nPlease Enter the user name", Toast.LENGTH_LONG).show();
                    throw new Exception("Please Enter the user name");
                }else {
                    SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("phoneNumber",phoneNumber);
                    editor.commit();
                    Users user = new Users(phoneNumber, name, email);

                    //This will generate PUSHID and then setvalue
//                databaseReference.setValue(phonenumber);
                    databaseReference.child(phonenumber).setValue(user);

                    Toast.makeText(this, "Record Added", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(this, ProfilePhoto.class);
                    in.putExtra("phone", phonenumber);
                    startActivity(in);
                }
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

        });
    }
}
