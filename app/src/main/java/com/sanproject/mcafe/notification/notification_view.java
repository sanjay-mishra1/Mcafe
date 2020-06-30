package com.sanproject.mcafe.notification;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.AlbumsAdapter;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.album_allorders;
import com.sanproject.mcafe.mainactiv;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class notification_view extends Activity {
    AlbumsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

         check_notifications( );

    }
    void check_notifications(){
        FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications").orderByChild("Server_Time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()||!dataSnapshot.exists()){
                    {findViewById(R.id.progressbar).setVisibility(View.GONE);
                        findViewById(R.id.no_notification).setVisibility(View.VISIBLE);
                    }
                }else{
                    RecyclerView recyclerView=findViewById(R.id.recyclerview);
                    LinearLayoutManager horizontal=new LinearLayoutManager(notification_view.this
                            ,LinearLayoutManager.VERTICAL,true);
                    recyclerView.setLayoutManager(horizontal);
                    horizontal.setStackFromEnd(true);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    firebase(recyclerView);
                }
                findViewById(R.id.progressbar).setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                findViewById(R.id.no_notification).setVisibility(View.VISIBLE);
            }
        });
    }
    void firebase(RecyclerView recyclerView){

        FirebaseRecyclerAdapter <album_allorders,mainactiv.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder>(
                album_allorders.class,
                R.layout.notification_small,
                mainactiv.FoodViewHolder.class,
                FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications").orderByChild("Server_Time")) {
            @Override
            protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, album_allorders model, int position) {
                viewHolder.set_time_from_firebase(model.getServer_Time());
                viewHolder.setMessage_notification(model.getMessage());
                viewHolder.setDesc(model.getFrom());
             }
        };
recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void backtomenu(View view) {
        finish();
    }
}
