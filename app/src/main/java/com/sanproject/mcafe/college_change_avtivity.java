package com.sanproject.mcafe;

import android.content.Intent;
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

public class college_change_avtivity extends AppCompatActivity {
    private Button Update;
    ImageView cancel,imageview1,imageview2,imageview3;
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
        collegeid=findViewById(R.id.collegeid_id);
        cancel=findViewById(R.id.cancel);
        update=findViewById(R.id.update);
        imageview1=findViewById(R.id.imageView3);
        imageview2=findViewById(R.id.imageView4);
        imageview3=findViewById(R.id.imageView5);
        collegeid.setVisibility(View.VISIBLE);
        receive_data();
        user.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        imageview1.setVisibility(View.INVISIBLE);
        imageview2.setVisibility(View.INVISIBLE);
        imageview3.setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(college_change_avtivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        auth=FirebaseAuth.getInstance();
        databaseReference=  FirebaseDatabase.getInstance().getReference();
        databaseReference1=  FirebaseDatabase.getInstance().getReference();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c=collegeid.getText().toString().trim();
                if(c.isEmpty())
                {
                    Toast.makeText(college_change_avtivity.this,"Enter college id",Toast.LENGTH_LONG).show();
                    return;
                }
//                auth.getCurrentUser().updateEmail(email.getText().toString().trim());
                databaseReference1.child("User Informations").child(auth.getCurrentUser().getUid()).child("College_id").setValue(c);
                Toast.makeText(college_change_avtivity.this,"Details Modified",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(college_change_avtivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



    }

    private void receive_data() {



        try {
            Intent intent = getIntent();
           collegeid.setText(intent.getStringExtra("CollegeId"));

        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}
