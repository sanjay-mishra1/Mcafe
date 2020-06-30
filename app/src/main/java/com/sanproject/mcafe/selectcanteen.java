package com.sanproject.mcafe;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;
import static com.sanproject.mcafe.newlogin.MYPREFERENCES;

public class selectcanteen extends AppCompatActivity {
    boolean login=false;
    private AlbumsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canteen_selection);
        GifImageView gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingpage);
        Log.e("Auth","Auth is"+FirebaseAuth.getInstance().getCurrentUser());
        loadFirebase();
    }
    void savePreferences(String phone,String canteen,String mail,String Password) {
       SharedPreferences shared = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("Email", mail);
        editor.putString("Password", Password);
        editor.putString("Canteen", canteen);
        editor.putString("MobileNumber", phone);
        editor.putBoolean("verify_phone",true);
        Canteen=canteen;
         editor.apply();
         login=true;
    }
    void loadFirebase(){
        FirebaseDatabase.getInstance().getReference("Canteen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==1)
                {
                 try {for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {save(dataSnapshot1.getKey());
                }
                }catch (Exception e)
                 {
                    Log.e("SelectCanteen","Exception "+e);
                    Toast.makeText(selectcanteen.this,"Failed to load Canteens", Toast.LENGTH_SHORT).show();
              FirebaseAuth auth=     FirebaseAuth.getInstance();
              if(auth.getCurrentUser().delete().isSuccessful())
                    finish();
                    startActivity(new Intent(selectcanteen.this, logIN.class));
                }


                }else{
                    Firebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Firebase(){
        findViewById(R.id.progressRelative).setVisibility(View.GONE);

        RecyclerView recyclerView1=findViewById(R.id.recycler_view);
        LinearLayoutManager horizontal=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView1.setLayoutManager(horizontal);
        recyclerView1.setAdapter( adapter);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        final DatabaseReference mDatabase1= FirebaseDatabase.getInstance().getReference().child("Canteen");

        FirebaseRecyclerAdapter<Food,mainactiv.FoodViewHolder> FBRA
        =new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.foodcart,
                mainactiv.FoodViewHolder.class,
                mDatabase1

        ) {
            @Override
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
//                viewHolder.setName();
//                viewHolder.setImageID();
                viewHolder.setCanteenData(model.getCanteenImage(),model.getCanteen());
                if (model.getCanteen().equalsIgnoreCase(Canteen)){
                    viewHolder.mView.setBackgroundColor(getResources().getColor(R.color.app_pink));
                }
             viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.selectbutton).setVisibility(View.VISIBLE);
                    relaodAdapter();
                 Canteen=model.getCanteen();
                }
            });


            }
        };

        recyclerView1.setAdapter(FBRA);

    }
    void relaodAdapter(){
     //   FBRA.cleanup();
        Firebase();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void save(String canteen) {
       if (canteen!=null) {
           Intent intent = getIntent();
            String name=intent.getStringExtra("Name");
          String key=  (name.trim());
           if (!key .isEmpty()) {
               String[] words = key.split("\\s");
               key = "";
               for (String w : words) {
                   key = key + String.valueOf(w.charAt(0)).toUpperCase() + w.substring(1).toLowerCase() + " ";
                   // Log.e("Searching","Data Original <"+w+"> modified <"+key+">");
               }
           }
           name=key;
           DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Informations/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
           databaseReference.child("Name").setValue(name.trim());
           databaseReference.child("Mobile_number").setValue(intent.getStringExtra("Phone"));
           databaseReference.child("College_id").setValue(intent.getStringExtra("College"));
           databaseReference.child("Email").setValue(intent.getStringExtra("Email"));
           databaseReference.child("Canteen").setValue(canteen);
           databaseReference.child("is_mobile_verified").setValue(false);
           databaseReference.child("Uid").setValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
           savePreferences(intent.getStringExtra("Phone"),canteen, intent.getStringExtra("Email"), intent.getStringExtra("Password"));
           Intent intent1 = new Intent(selectcanteen.this, MainActivity.class);
           intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent1);
       }

    }

    public void selectclicked(View view) {
        if (!Canteen.isEmpty()){
            save(Canteen);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
