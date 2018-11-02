package com.example.satnamsingh.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class LoginScreen extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String VerificationId,loginPhone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private EditText loginPhone_et,code_et;
    private TextView login_tv1,login_tv2,login_tv3,login_timer;
    private Button verify_bt,login_bt;
    private boolean flag = false;
    private Thread otpThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        loginPhone_et=findViewById(R.id.loginPhone_et);
        login_bt = findViewById(R.id.login_bt);
        login_tv1 = findViewById(R.id.login_tv1);
        login_tv2 = findViewById(R.id.login_tv2);
        login_tv3 = findViewById(R.id.login_tv3);
        login_timer = findViewById(R.id.login_timer);
        mAuth = FirebaseAuth.getInstance();
        code_et=findViewById(R.id.code_et);
        verify_bt=findViewById(R.id.verify_bt);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                code_et.setText(credential.getSmsCode());
                loginPhone_et.setEnabled(false);
                code_et.setEnabled(false);
                userHomeActivity();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("MYMSG", "onVerificationFailed");
                Toast.makeText(LoginScreen.this, "Users Authentication failed\nPlease Check PhoneNumber or Internet connection"
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("MYMSG", "code sent " + verificationId);
                VerificationId = verificationId;
                code_et.setVisibility(View.VISIBLE);
                verify_bt.setVisibility(View.VISIBLE);
            }
        };
    }
    public void login(View v) {
        if (flag) {
            Toast.makeText(getApplicationContext(), "OTP is sent to your mobile number\nPlease wait for timer to resend OTP", Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
            login_tv1.setText("OTP is send to your mobile number\n");
            login_tv2.setText("Please wait");
            login_timer.setVisibility(View.VISIBLE);
            otpThread =new Thread(() ->
            {
                for (int i = 120; i > 0; i--) {
                    final int j = i;
                    runOnUiThread(() -> {
                        login_timer.setText(j + " sec");
                    });
                    try {

                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                flag = false;
            });
            otpThread.start();

            InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            loginPhone = loginPhone_et.getText().toString();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users");
            DatabaseReference reference = databaseReference.child(loginPhone);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(LoginScreen.this, "User does not exist\nplease SignUp first", Toast.LENGTH_LONG).show();
                        flag = false;
                        otpThread.stop();
                        login_timer.setVisibility(View.GONE);

                    } else {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(loginPhone, 120, TimeUnit.SECONDS, LoginScreen.this, mCallbacks);
                        Log.d("MYMSG", " Veification Started");
                        //to open the new activity to get the details of the user
                        // getDetails();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginScreen.this, "Error while fetching Cloud Data", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void verifyCode(View v) {
        if (!(code_et.getText() == null || code_et.getText().equals(""))) {
            String code = code_et.getText().toString();

            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
        else{
            Toast.makeText(this, "Please enter the OTP ", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            login_timer.setVisibility(View.GONE);
                            loginPhone_et.setEnabled(false);
                            code_et.setEnabled(false);
                            userHomeActivity();
                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginScreen.this, "Invalid code", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void userHomeActivity() {

        SharedPreferences sharedPreferences =getSharedPreferences("LocationTrackerUser.txt",MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString("phoneNumber",loginPhone);
        editor.commit();
        Toast.makeText(LoginScreen.this, "Data loaded to shared preferences", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UserHomeActivity.class);
        intent.putExtra("phone", loginPhone);
        finishAffinity();
        startActivity(intent);
    }
}

