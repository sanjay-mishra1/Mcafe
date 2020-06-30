package com.sanproject.mcafe.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sanproject.mcafe.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class update extends AppCompatActivity {
    String link;
    private String force;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);
        Receive();
        set_Listeners();

    }
    void Receive(){
        try {
            Intent intent=getIntent();
           link= intent.getStringExtra("Link");
           force= intent.getStringExtra("Force");
           if (force.toUpperCase().contains("YES")){
               findViewById(R.id.cancel_button).setVisibility(View.GONE);
           }
           if (force.toUpperCase().contains("EXIT")){
               finish();
           }
        }catch (Exception e){}
    }
    void set_Listeners(){
       try {


           findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
               }
           });
           findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                finish();
               }
           });

       }catch (Exception e){
           Toast.makeText(this,"Failed to update app",Toast.LENGTH_SHORT).show();
           finish();
       }
    }


    @Override
    public void onBackPressed() {

        if (!force.toUpperCase().contains("YES")) {
            super.onBackPressed();
        }else{
            Intent intent=new Intent(this,update.class);
            intent.putExtra("Force","Exit");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }
}
