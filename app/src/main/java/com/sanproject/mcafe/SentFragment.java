package com.sanproject.mcafe;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SentFragment extends AppCompatActivity {


    public static   String MYPREFERENCES="User_Credentials";

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
         this.setFinishOnTouchOutside(false);
        final SharedPreferences shared= getApplicationContext().
                getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String img=shared.getString("UserImg","");
     try {
         if (img.isEmpty()|| img.equals("")){
             TextView textView=findViewById(R.id.name_img);
             textView.setText(Name_for_Image(shared.getString("UserName","")));
         } else
         {Glide.with(getApplicationContext()).
                 applyDefaultRequestOptions(RequestOptions.circleCropTransform()).
                 load(img).into((ImageView) findViewById(R.id.profile_img));
             findViewById(R.id.name_img).setVisibility(View.INVISIBLE);
         }
     } catch (Exception e){
         TextView textView=findViewById(R.id.name_img);
         textView.setText(Name_for_Image(shared.getString("UserName","")));
     }
        Log.e("User credentials","Name "+shared.getString("UserName","")
        +" Mobile "+shared.getString("MobileNumber","")+
                        " Img "+shared.getString("UserImg","")
        );
        TextView name=findViewById(R.id.username);
            name.setText(shared.getString("UserName",""));
        TextView mobile=findViewById(R.id.mobile_number);
        mobile.setText(shared.getString("MobileNumber",""));



        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendOnclick();
              // finish();

            }
        });

        super.onStart();


    }
    private CharSequence Name_for_Image(String s1) {
        int j = 0, flag = 0, loc2 = 0;
        String Name = "";
        try {

            char[] ch = s1.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] != ' ' && flag == 0) {
                    flag = 1;
                    j = i;
                }
            }
            String name23;
            Name = s1.substring(j, s1.length());
            loc2 = Name.indexOf(" ");
            if (j == -1)
                j = 1;
            name23 = Name.substring(j, loc2);
            Name = Name.replace(name23, "");
            int j2 = 0;
            char[] ch1 = Name.toCharArray();
            System.out.println(" Name=-" + Name);
            int k = 0;
            while (k < Name.length()) {
                if (ch1[k] == ' ') {

                    j2 = k;
                }
                k++;
            }
            String second;

            if (j2 == Name.length() - 1)
                second = (Character.toString(ch[j + 1]));
            else second = (Character.toString(ch1[j2 + 1]));

            Name = (Character.toString(ch[j])) + second;
            Name = Name.toUpperCase();

            System.out.println("After loop ch1= -" + "-" + " Name=" + Name);


            // System.out.println("After loop ch1= -"+ch1[j2+1]+"-"+" Name="+Name );
        } catch (Exception NullPointerException) {
        }
        return check_credentials(Name);
    }
    String check_credentials(String name){
        if (name.length()>2){
            char[] ch = name.toCharArray();
            name=Character.toString(ch[0])+Character.toString(ch[1]);
        }
        return name.toUpperCase();
    }
    String rating(){
        RatingBar ratingBar=findViewById(R.id.stars);
        String ratings=String.valueOf(ratingBar.getRating());
        Log.e("Ratings", ratings);
      return ratings;
    }

    public void sendOnclick() {
        String rating= rating() ;

        EditText message=findViewById(R.id.feedback_edit);
        String feedback=message.getText().toString().trim();
        if (rating.isEmpty() ||rating.equals("0.0")||rating.equals("0")){
            Toast.makeText(this,"Please select star",Toast.LENGTH_SHORT).show();
            return;
        }
        String time=String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference=
                FirebaseDatabase.getInstance().getReference().child("Messages").child("Feedbacks").
                        child(time);

        databaseReference.child("UId").
                setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!feedback.isEmpty()) {
            databaseReference.child("Feedback").setValue(feedback);
        }
        databaseReference.child("Rating").setValue(rating);

        Toast.makeText(this,"Sending feedback...",Toast.LENGTH_SHORT).show();
         finish();
       // Intent intent=new Intent(SentFragment.this,MainActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // startActivity(intent);
    }




}
