package com.sanproject.mcafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class logIN extends Activity implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    SharedPreferences shared;
    public static final String MYPREFERENCES="MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsignin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);
        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

    }
    void savePreferences(String mobile,String canteen,String mail,String Password,String canImage,
                         boolean isMobileVerified) {
        SharedPreferences shared = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        progressBar.setVisibility(View.GONE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("Email", mail);
        editor.putString("Password", Password);
        editor.putString("CanteenImage", canImage);
        if (!isMobileVerified)
        editor.putBoolean("verify_phone", true);
        Log.e("Canteen","login "+canteen);
        editor.putString("Canteen", canteen);
        editor.putString("MobileNumber", mobile);
        Canteen=canteen;
        editor.apply();
        finish();
        Intent intent = new Intent(logIN.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void extractData(final String pwd){
        FirebaseDatabase.getInstance().getReference("User Informations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMobileVerified=true;
                try {
                    isMobileVerified= (boolean) dataSnapshot.child("is_mobile_verified").getValue();
                }catch (Exception e){e.printStackTrace();}
                String mobileNumber= (String) dataSnapshot.child("Mobile_number").getValue();
                savePreferences(mobileNumber,(String) dataSnapshot.child("Canteen").getValue(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),pwd
                ,(String) dataSnapshot.child("CanteenImage").getValue(),isMobileVerified);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void extract_emailAddress(final String Mobile){
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
        Log.e("mobile verification","started");
        FirebaseAuth.getInstance().signInWithEmailAndPassword("9425161744@gmail.com","sanjay@9425")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e("MobileAuth","Inside extrating email");
                        if (task.isSuccessful())
                        {   Log.e("MobileAuth","Extract email started");
                            FirebaseDatabase.getInstance().getReference("User Informations")
                                    .orderByChild("Mobile_number").equalTo(Mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        if (!dataSnapshot.exists())
                                        {findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                                            editTextEmail.setError("Invalid mobile number");
                                            editTextEmail.requestFocus();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(logIN.this,"Mobile not found",Toast.LENGTH_LONG).show();
                                        }
                                        for (  DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                        {
                                            Log.e("MobileAuth","Inside datachange ");
                                            if (dataSnapshot.exists()){
                                                userLogin((String)dataSnapshot1.child("Email").getValue());
                                                Log.e("MobileAuth","Found mobile number and email "+dataSnapshot1.child("Email").getValue()+dataSnapshot1.child("Mobile_number").getValue());
                                            }else{
                                                mAuth.signOut();
                                                Log.e("MobileAuth","mobile not found ");
                                            }
                                        }
                                    }catch (Exception e){}finally {
                                        mAuth.signOut();
                                    }
                                }



                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                                    Log.e("MobileAuth","Error "+databaseError.toString());
                                    mAuth.signOut();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        else{
                            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                            Toast.makeText(logIN.this,"Login failed. Try login using email address or try again after some time.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e("MobileAuth","Failed->"+task.getException().getMessage());
                        }
                    }
                });

}
 void typeLogin(){
         final String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()){
             findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
            editTextEmail.setError("Please enter a valid email or mobile number");
            editTextEmail.requestFocus();

        }else if (editTextPassword.getText().toString().trim().isEmpty()){
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
            editTextPassword.setError("Please enter the password");
            editTextPassword.requestFocus();
        }else if (editTextPassword.getText().toString().trim().length() < 6) {
             findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
             editTextPassword.setError("Invalid password");
             editTextPassword.requestFocus();
        }
         else if (isEmail(email)){
             Log.e("login","->"+editTextEmail.getText().toString().trim());
            userLogin(editTextEmail.getText().toString().trim());
         }
         else
        if (isPhone(email)){
            Log.e("login","mobile->"+editTextEmail.getText().toString().trim());
            extract_emailAddress(email);
         }else {
            Log.e("login","nothing ->"+editTextEmail.getText().toString().trim());
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        }
      }
     boolean isEmail(String email){
         return Patterns.EMAIL_ADDRESS.matcher(email).matches();
     }
    boolean isPhone(String email){
        return Patterns.PHONE.matcher("+91"+email).matches();

    }
    private void userLogin(String email) {
        final String password = editTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);

            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);

            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    extractData(password);
                } else {
                    findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                //finish();
                  startActivity(new Intent(this, newlogin.class));
                break;
            case R.id.buttonLogin:
                findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
                //userLogin();
                typeLogin();
                break;
        }
    }
private String getemail(){
       String email= editTextEmail.getText().toString().trim();
    if (email.isEmpty()) {
        editTextEmail.setError("Email is required");
        editTextEmail.requestFocus();
        return "n";
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        editTextEmail.setError("Please enter a valid email");
        editTextEmail.requestFocus();
        return "n";
    }
    return email;

}
    public void onclickforget_Email(View view) {
   // if (!getemail().contains("n"))
    {

        final ProgressDialog progressDialog=new ProgressDialog(logIN.this);

        Toast.makeText(logIN.this, "Checking your email address", Toast.LENGTH_SHORT).show();
        if (isEmail(getemail())) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(getemail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(logIN.this, "Email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(logIN.this, "Email doesn't exist", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(logIN.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }else  Toast.makeText(logIN.this, "Enter email address to proceed with password reset", Toast.LENGTH_SHORT).show();

    }

    }


}
