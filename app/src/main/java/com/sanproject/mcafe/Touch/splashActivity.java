package com.sanproject.mcafe.Touch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.sanproject.mcafe.MainActivity;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.logIN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class splashActivity extends AppCompatActivity {
    SharedPreferences shared;
    public static final String MYPREFERENCES="MyPrefs";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        shared = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        Check();
        //FirebaseAuth mAuth=FirebaseAuth.getInstance();

        }

        void Check(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
                   // finish();
                    startActivity(new Intent(splashActivity.this, MainActivity.class));
                    finish();
                } else {
                    String email = shared.getString("Email", "");
                    String password = shared.getString("Password", "");
                    if (email.isEmpty()) {
                        //userLogin();
                        startActivity(new Intent(splashActivity.this, logIN.class));
                        finish();
                    } else {
                        automaticLogin(email, password);
                    }
                }
            }
        },2000);
        }
    void automaticLogin(final String em, final String pwd){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor=shared.edit();
                    editor.putString("Email",em);
                    editor.putString("Password",pwd);
                    editor.apply();
                    finish();
                    Intent intent = new Intent(splashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        SharedPreferences.Editor editor=shared.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(splashActivity.this, logIN.class));
                        finish();
                    }
                    //Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }


