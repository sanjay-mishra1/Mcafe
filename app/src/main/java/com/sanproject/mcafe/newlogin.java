package com.sanproject.mcafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.update.webview;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by sanjay on 26/06/2018.
 */

public class newlogin extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText editTextPassword;
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    SharedPreferences shared;
    public static final String MYPREFERENCES = "MyPrefs";
    private EditText editTextphone;
    private EditText editchekpwd;
    private String phone;
    private String Password;
    String next = "1";
    private String mail;
    private String Col;
    private String User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        setContentView(R.layout.newlogin);
        editTextphone = (EditText) findViewById(R.id.mobile);
        editTextPassword = (EditText) findViewById(R.id.password);
        editchekpwd = (EditText) findViewById(R.id.repassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.textViewSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(newlogin.this,logIN.class));
                finish();
            }
        });

    }

     int signup() {
        String Phone = editTextphone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cpwd = editchekpwd.getText().toString().trim();
        if (Phone.isEmpty()) {
            editTextphone.setError("Phone is required");
            editTextphone.requestFocus();
            return 0;
        }

        if (!Patterns.PHONE.matcher(Phone).matches()) {
            editTextphone.setError("Please enter a valid Phone number");
            editTextphone.requestFocus();
            return 0;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return 0;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return 0;
        }
        if (cpwd.isEmpty()) {
            editchekpwd.setError("Reenter your password");
            editchekpwd.requestFocus();
            return 0;
        }

        if (!password.equals ( cpwd)) {
            editchekpwd.setError("Password doesn't match");
             editchekpwd.requestFocus();
            return 0;
        }
        if (!findViewById(R.id.terms).isEnabled()) {
            findViewById(R.id.buttonLogin).setClickable(false);
        }

        phone = Phone;
        Password = password;
        return 1;
    }
    void checkMobileNumber(final String Mobile){
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);

        Log.e("MobileAuth","Inside extrating email");
        mAuth.signInWithEmailAndPassword("9425161744@gmail.com","sanjay@9425").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseDatabase.getInstance().getReference("User Informations").orderByChild("Mobile_number").equalTo(Mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count=0;
                        if (dataSnapshot.exists()|| dataSnapshot.hasChildren()){
                            editTextphone.setError("Phone number already registered");
                            editTextphone.requestFocus();
                            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                            mAuth.signOut();
                            progressBar.setVisibility(View.INVISIBLE);
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                                        count++;
                                        if (count==1)
                                        {mAuth.signOut();
                                        }
                                        Log.e("Auth","new login Signing out ");
                            loadsecondScreen();
                        }

                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                        Log.e("MobileAuth","Error "+databaseError.toString());
                         Toast.makeText(newlogin.this,"Error occured...",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        mAuth.signOut();

                    }
                });

            }
        });

    }
        void loadsecondScreen(){
            next = "2";
            editTextphone.setVisibility(View.GONE);
            editTextPassword.setVisibility(View.GONE);
            editchekpwd.setVisibility(View.GONE);

            EditText email = findViewById(R.id.email);


            email.setVisibility(View.VISIBLE);
            EditText college = findViewById(R.id.Username);
            college.setVisibility(View.VISIBLE);
            EditText username = findViewById(R.id.collegeid);
            username.setVisibility(View.VISIBLE);
            Button register = findViewById(R.id.buttonLogin);
            register.setText("Register");
            findViewById(R.id.terms).setVisibility(View.GONE);
            //     findViewById(R.id.textViewSignup).setVisibility(View.GONE);
//            findViewById(R.id.forgetemail).setVisibility(View.GONE);
            findViewById(R.id.terms_text).setVisibility(View.INVISIBLE);
        }
    public void Onnext(View view) {
        if (next.equals( "2")) {
            if (final_credentials() == 1) {
                register();
            }

        }else if (next.equals("1")){
            if (signup() == 1) {
                RadioButton radioButton = findViewById(R.id.terms);
                if (!radioButton.isChecked())
                    Toast.makeText(this, "Accept our terms and conditions to register ", Toast.LENGTH_SHORT).show();
                else {
                    checkMobileNumber(editTextphone.getText().toString().trim());
                }
            }
        }
    }

    private void register() {
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
        mAuth.createUserWithEmailAndPassword(mail, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                     Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid());
//                    databaseReference.child("Name").setValue(User);
//                    databaseReference.child("Mobile_number").setValue(phone);
//                    databaseReference.child("College_id").setValue(Col);
//                    databaseReference.child("Email").setValue(mail);
//                    databaseReference.child("Uid").setValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                     //savePreferences();

                    Intent intent=new Intent(newlogin.this, selectcanteen.class);

                    intent.putExtra("Name",User);
                    intent.putExtra("Phone",phone);
                    intent.putExtra("College",Col);
                    intent.putExtra("Password",Password);
                    intent.putExtra("Email",mail);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.e("Auth","login "+mAuth.getCurrentUser());
                    startActivity(intent);

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                    //    finish();
                      //  startActivity(new Intent(newlogin.this, newlogin.class));
                       // finish();

                    } else {
                        //   finish();
                        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    private int final_credentials() {

        EditText email = findViewById(R.id.email);
        email.setVisibility(View.VISIBLE);
        EditText college = findViewById(R.id.collegeid);
        college.setVisibility(View.VISIBLE);
        EditText username = findViewById(R.id.Username);
        username.setVisibility(View.VISIBLE);
        mail = email.getText().toString().trim();

        Col = college.getText().toString().trim();
        User = username.getText().toString().trim();
        if (mail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return 0;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Please enter a valid email address ");
            email.requestFocus();
            return 0;
        }

        if (Col.isEmpty()) {
            college.setError("College id  is required");
            college.requestFocus();
            return 0;
        }

        if (User.isEmpty()) {
            username.setError("Please enter your name");
            username.requestFocus();
            return 0;
        }
        RadioButton r = findViewById(R.id.terms);
        if (r.isChecked()) {
            findViewById(R.id.buttonLogin).setClickable(true);
            findViewById(R.id.buttonLogin).setEnabled(true);
        }

        return 1;
    }

    void savePreferences() {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("Email", mail);
        editor.putString("Password", Password);
        editor.apply();
    }
    public void show_terms(View view) {
       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://fir-a-1fd3e.firebaseapp.com/terms_conditions.html")));
       Intent intent=new Intent(this, webview.class);
       intent.putExtra("URL","https://mit-cafe.web.app/terms_conditions.html");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Button button=findViewById(R.id.buttonLogin);

        if (!button.getText().toString().trim().equalsIgnoreCase("next")){
            editTextphone.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);
            editchekpwd.setVisibility(View.VISIBLE);
            findViewById(R.id.terms).setVisibility(View.VISIBLE);
              findViewById(R.id.email).setVisibility(View.GONE);
              findViewById(R.id.Username).setVisibility(View.GONE);
              findViewById(R.id.collegeid).setVisibility(View.GONE);
              button.setText("NEXT");
        }else{
            super.onBackPressed();

        }
    }
}