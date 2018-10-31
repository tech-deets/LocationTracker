package com.example.satnamsingh.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText et1, et2;
    private Button bt2,signup2,continuebt;
    private TextView tv111,tv1,tv2,timer;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String VerificationId,phoneNumber;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        et1 = (EditText) (findViewById(R.id.et1));
        et2 = (EditText) (findViewById(R.id.code));
        bt2 = (Button) (findViewById(R.id.bt2));
        signup2 = (Button)findViewById(R.id.signup2);
        continuebt = (Button)findViewById(R.id.continuebt);
        tv111 = (TextView) (findViewById(R.id.tv111));
        timer = (TextView)findViewById(R.id.timer);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        et2.setVisibility(View.INVISIBLE);
        bt2.setVisibility(View.INVISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // onVerificationCompleted is Auto Called if Auto Detection of SMS is done

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without neebbgxding to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                et2.setText(credential.getSmsCode());
                et2.setEnabled(false);
                timer.setVisibility(View.GONE);
                //et2.setVisibility(View.INVISIBLE);
                bt2.setVisibility(View.GONE);
                Log.d("MYMSG", "verification completed");
                tv111.setText("Auto Verification Completed");
//                tv111.setTextColor(new Color(255,63,81,181));
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                et1.setEnabled(false);
                signup2.setVisibility(View.VISIBLE);
                continuebt.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Code verified", Toast.LENGTH_SHORT).show();
//                GotoUserHomeActivity();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d("MYMSG", "onVerificationFailed");

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("MYMSG", "code sent " + verificationId);
                VerificationId = verificationId;
                et2.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.VISIBLE);

            }
        };
    }

    public void go(View view) {
        if(flag){
            Toast.makeText(getApplicationContext(), "OTP is sent to your mobile number\nPlease wait for timer to resend OTP", Toast.LENGTH_SHORT).show();
        }else{
            flag = true;
            tv1.setText("OTP is send to your mobile number\n");
            tv2.setText("Please wait");
            timer.setVisibility(View.VISIBLE);
            new Thread(()->
            {
                for(int i = 120;i>0;i--) {
                    final int j=i;
                    runOnUiThread(() -> {
                        timer.setText(j+" sec");
                    });
                    try{

                        Thread.sleep(1000);
                    }catch (InterruptedException ex ){
                        ex.printStackTrace();
                    }
                }
                flag = false;
            }).start();
            et2.setText("");
            tv111.setText("");

            InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            phoneNumber = et1.getText().toString();
//            if(!phoneNumber.startsWith("+91"))
//                phoneNumber = "+91"+phoneNumber;
            DatabaseReference reference = databaseReference.child(phoneNumber);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue()!=null){
                        Toast.makeText(SignUpActivity.this, "User already exists\nplease login", Toast.LENGTH_SHORT).show();

                    }else {
                        // We Register mCallbacks which are attached to verification process
                        // Which will try to Authenticate Automatically
                        // Otherwise we might need to fill code manually and Then Click Verify

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,120,TimeUnit.SECONDS,SignUpActivity.this,mCallbacks);

                        Log.d("MYMSG", "Phone No Veification Started");
                        //to open the new activity to get the details of the user
                        // getDetails();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void verify(View view) {
        String code = et2.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
        signInWithPhoneAuthCredential(credential);

    }


    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Phone Verified", Toast.LENGTH_SHORT).show();

                            et2.setText(credential.getSmsCode());
                            et2.setEnabled(false);
                            timer.setVisibility(View.GONE);
                            //et2.setVisibility(View.INVISIBLE);
                            bt2.setVisibility(View.GONE);
                            signup2.setVisibility(View.VISIBLE);
                            tv111.setText("Manual Verification Complete");
                            et2.setVisibility(View.GONE);
                            et1.setEnabled(false);
                            continuebt.setVisibility(View.GONE);
                            tv1.setVisibility(View.GONE);
                            tv2.setVisibility(View.GONE);
                            tv111.setTextColor(Color.BLUE);

//                            GotoUserHomeActivity();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUpActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void getDetails(View v) {
        Intent intent = new Intent(this, SignUpActivity2.class);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
    }

}
