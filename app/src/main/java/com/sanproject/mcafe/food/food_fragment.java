package com.sanproject.mcafe.food;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.AlbumsAdapter;
import com.sanproject.mcafe.Food;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.food_info;
import com.sanproject.mcafe.mainactiv;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class food_fragment extends Fragment {
     FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
     ProgressBar progressBar;
    FirebaseAuth auth;
     DatabaseReference databaseReference;
    int card_size=0;
    ArrayList<String>cart;
    public food_fragment(){
     }
    void load_cart(){
        databaseReference=FirebaseDatabase.getInstance().getReference().
                child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.main,container,false);

           card_size=dpToPx(10);
         progressBar=view.findViewById(R.id.progressFood);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, card_size, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
         String address=this.getArguments().getString("Address");
        if (address!=null)
        { mDatabase= FirebaseDatabase.getInstance().getReference().child(address);
        }else{
             mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+"/Snaks");
        }
        databaseReference=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        auth=FirebaseAuth.getInstance();

        firebase();

        return view;

    }

    void firebase() {

        Query query;
        String addr=this.getArguments().getString("Query");
        if (addr != null && !addr.isEmpty() && !addr.equals(" ")) {
            query=mDatabase.orderByChild(addr);
        }else  query=mDatabase;

     /*   if (addr!=null){
            if (!addr.isEmpty()&& !addr.equals(" "))
            { if (addr.contains("reverse"))
            { addr="price";

            }
            else{
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL,true);
                recyclerView.setLayoutManager(mLayoutManager);
              //  recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, card_size, true));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
                query=mDatabase.orderByChild(addr);

            }
            else query=mDatabase;



        }else{
            query=mDatabase;
        }*/
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter <Food, mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                query


        )
        {
             protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                progressBar.setVisibility(View.GONE);
                {
                    viewHolder.setName(model.getFood_name());
                   // viewHolder.setPrice(String.valueOf(model.getPrice()));
                    viewHolder.setDesc(model.getDescription());
                    viewHolder.setImage(getActivity(), model.getFood_Image());
                    final String price= viewHolder.setDiscount(String.valueOf(model.getDiscount()),String.valueOf( model.getPrice()));
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), food_info.class);
                            intent.putExtra("Image", model.getFood_Image());
                            intent.putExtra("Name", model.getFood_name());
                            intent.putExtra("Price",String.valueOf(model.getPrice()));
                            intent.putExtra("Discount",String.valueOf(model.getDiscount()));
                            intent.putExtra("Favorite",model.getFavorite());
                            intent.putExtra("Hide",false);

                            intent.putExtra("Description", model.getDescription());
                            intent.putExtra("Type", model.getType());
                            intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                            intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                            intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation
                                            (getActivity(), viewHolder.getImageId(), "My_Animation");
                            startActivity(intent, optionsCompat.toBundle());

                        }
                    });
                    viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {                              //  totalFood.setVisibility(View.VISIBLE);

                            foodcart(model.getFood_name(),Integer.parseInt(price),model.getType(),model.getFood_Image(),viewHolder.mView);
                            viewHolder.mView.setBackgroundColor(Color.RED);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.itemView
                                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                }
                            },3000);

                        }
                    });
                }
            }

        };
        recyclerView.setAdapter(FBRA);

    }

    void foodcart(final String Food, final int Price, final String Type, final String Image, final View mView)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
        databaseReference.child(Food).child("Food_name").setValue( Food );
        databaseReference.child(Food).child("price").setValue( Price);

        databaseReference.child(Food).child("Type").setValue(  Type );

        databaseReference.child(Food).child("Food_Image").setValue( Image );

        databaseReference.child(Food).child("Quantity").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            }
            else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    public  int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }




}

