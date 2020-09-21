package com.sanproject.mcafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class change_account_info extends AppCompatActivity {
private Button Update;
ImageView cancel;
Button update;
String names,mobile,eid,cid;
EditText user,phone,email,collegeid;
FirebaseAuth auth;
DatabaseReference databaseReference,databaseReference1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account_info);
        user=findViewById(R.id.user_name);
        phone=findViewById(R.id.Phone_id);
        email=findViewById(R.id.emailid_id);
        findViewById(R.id.emailid_id).setVisibility(View.INVISIBLE);
        collegeid=findViewById(R.id.collegeid_id);
        cancel=findViewById(R.id.cancel);
        update=findViewById(R.id.update);

       receive_data();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(change_account_info.this,MainActivity.class);
                startActivity(intent);
            }
        });
   auth=FirebaseAuth.getInstance();
   databaseReference=  FirebaseDatabase.getInstance().getReference();
        databaseReference1=  FirebaseDatabase.getInstance().getReference();

             update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().updateEmail(email.getText().toString().trim());
                String u=user.getText().toString().trim();
                String p=phone.getText().toString().trim();
                String c=collegeid.getText().toString().trim();
                if (u.isEmpty()){
                    user.setError("User name cannot be empty");
                    user.requestFocus();
                    return;
                }else if (p.length() != 10){
                    phone.setError("invalid Phone number  ");
                    phone.requestFocus();
                    return;
                }
                SharedPreferences shared;
                shared=view.getContext().getSharedPreferences("User_Credentials", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=shared.edit();
                editor.putString("UserName",u);
                editor.putString("MobileNumber",p);
                editor.apply();
                SharedPreferences preferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
                SharedPreferences.Editor editor1=preferences.edit();
                if (!p.equals(mobile))
                    editor1.putBoolean("verify_phone",true);
                editor1.putString("MobileNumber",p);
                editor1.putString("UserName",u);
                editor1.apply();
                databaseReference1.child("User Informations").child(auth.getCurrentUser().getUid()).child("Name").setValue(u);
                    databaseReference1.child("User Informations").child(auth.getCurrentUser().getUid()).child("Mobile_number").setValue(p);
                    databaseReference1.child("User Informations").child(auth.getCurrentUser().getUid()).child("College_id").setValue(c);
                    Toast.makeText(change_account_info.this, "Details Modified", Toast.LENGTH_SHORT).show();

                   finish();

               }
        });


    }
    private void receive_data() {

        try {
            Intent intent = getIntent();
            collegeid.setText(intent.getStringExtra("CollegeId"));
            user.setText(intent.getStringExtra("User"));
            email.setText(intent.getStringExtra("Email"));
            mobile=intent.getStringExtra("Mobile");
            phone.setText(mobile);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
