package com.sanproject.mcafe;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class personalpreferences extends Fragment {
    public personalpreferences() {

    }
    private AlbumsAdapter adapter;
final int speedscroll=1800;
final Handler handler=new Handler();
    Context context;
    RecyclerView recyclerView1,recyclerView2,recyclerView3;
    DatabaseReference mDatabase1,mDatabase2,mDatabase3;
    ProgressBar progressBar1,progressBar2,progressBar3;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prosonalpreferences, container, false);
        recyclerView1 = (RecyclerView) view.findViewById(R.id.mostrated);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.mostordered);
        recyclerView3 = (RecyclerView) view.findViewById(R.id.mostfavorites);
        LinearLayoutManager horizontal1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
     //   recyclerView1.setLayoutManager(new CustomLinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        LinearLayoutManager horizontal2=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView1.setLayoutManager(horizontal1);
        recyclerView2.setLayoutManager(horizontal2);

        recyclerView1.setAdapter(adapter);
        recyclerView2.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView3.setLayoutManager(mLayoutManager);
        recyclerView3.addItemDecoration(new food_snaks.GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(adapter);



        scroll(recyclerView1);
        return view;
    }


    public  int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    @Override
    public void onStart() {
        super.onStart();
progressBar1=view.findViewById(R.id.progressbar1);
        //progressBar1=view.findViewById(R.id.progressbar1);
        progressBar1=view.findViewById(R.id.progressbar1);
        progressBar2=view.findViewById(R.id.progressbar2);
        progressBar3=view.findViewById(R.id.progressbar3);

        Firebase("Snaks",recyclerView1,R.layout.horizontallist,progressBar1);
        Firebase("Beverages",recyclerView2,R.layout.horizontallist,progressBar2);
        Firebase("Meal",recyclerView3,R.layout.album_card,progressBar3);


    }
    void scroll(final RecyclerView recyclerView){
        final Runnable runnable=new Runnable() {
            int count=0;
            boolean flag =true;
            @Override
            public void run() {
         if(count<5){
             if(count==5-1){
                 flag=false;
                }
                else if (count==0){
                 flag=true;
                    }
                    if(flag) count++;
                else    count--;

                recyclerView.smoothScrollToPosition(count);
                handler.postDelayed(this,speedscroll);
         }

            }
        };
        handler.postDelayed(runnable,speedscroll);
    }
   void Firebase(String Address, RecyclerView recyclerView, int layout, final ProgressBar progressBar){

       progressBar.setVisibility(View.VISIBLE);
       mDatabase1= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Address);
     FirebaseRecyclerAdapter<Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
             Food.class,
             layout,
             mainactiv.FoodViewHolder.class,
             mDatabase1

     ) {
         @Override
         protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, Food model, int position) {
             viewHolder.setName(model.getFood_name());
             viewHolder.setPrice(String.valueOf(model.getPrice()));
             viewHolder.setDescVisibility();
             viewHolder.setImage(getActivity(), model.getFood_Image());
             progressBar.setVisibility(View.INVISIBLE);

         }
     };
       recyclerView.setAdapter(FBRA);

   }

}
