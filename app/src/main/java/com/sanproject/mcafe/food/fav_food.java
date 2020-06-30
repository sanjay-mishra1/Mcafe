package com.sanproject.mcafe.food;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.AlbumsAdapter;
import com.sanproject.mcafe.Food;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.food_info;
import com.sanproject.mcafe.mainactiv;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class fav_food extends Fragment {
    public  String[] Selected_foodName;
    public  long[] Selected_foodPrice;
    public String Name;
    public  String[] Image;
    ImageView next;
    public  int count=0;
    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    FirebaseAuth auth;
    SharedPreferences shared;
    DatabaseReference databaseReference;
    public static final String MYPREFERENCES="FoodCart";

    public fav_food(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.main,container,false);
        shared=getActivity().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        count=0;

        Selected_foodName=new String[10];
        Selected_foodPrice=new long[10];
        Image=new String[10];
        progressBar=view.findViewById(R.id.progressFood);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

          mDatabase= FirebaseDatabase.getInstance().getReference().child("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Favorite");

         mAuth=FirebaseAuth.getInstance();
        auth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen);
        firebase();

        return view;

    }
    private void set_food_info(final View foodView, final String name, final TextView priceView, final TextView desc, final String Type){

        databaseReference.child(Type).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          try {
              long price = (long) dataSnapshot.child("price").getValue();
              long discount = (long) dataSnapshot.child("discount").getValue();
              String des = (String) dataSnapshot.child("Description").getValue();
              String img = (String) dataSnapshot.child("Food_Image").getValue();
              Log.e("PriceData","->"+price+"->"+discount);
              priceView.setText(String.valueOf(price));
              ImageView imageView = foodView.findViewById(R.id.thumbnail);
              setDiscount(foodView, String.valueOf(discount), String.valueOf(price));
              desc.setText(des);
              Glide.with(getView()).load(img).into(imageView);
              imageListner(foodView, imageView, name, discount, Type, img);
              long fav=(long) dataSnapshot.child("Favorite").getValue();
              long total_order=0;
              try {
                  total_order = (long) dataSnapshot.child("Total_orders").getValue();
              }catch (Exception ignored){}
              long total_time_rated=0;
              try {
               total_time_rated=(long) dataSnapshot.child("Total_NoOfTime_Rated").getValue();
          }catch (Exception ignored){}
              long sum_of_rating=0;
              try {
               sum_of_rating=(long) dataSnapshot.child("Sum_of_Ratings").getValue();
          }catch (Exception ignored){}


              loadInfoListener(foodView,img, name, String.valueOf(price), String.valueOf(discount),
                      String.valueOf(fav),
                      des, Type, String.valueOf(total_order),
                      String.valueOf(total_time_rated),
                      String.valueOf(sum_of_rating), imageView);
          }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void imageListner(final View view, ImageView imageView, final String name, final long price, final String type, final String img){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View views) {
                {
                    view.setBackgroundColor(Color.RED);
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                    },3000);
                    foodcart(name,price, type, img);
                }
            }
        });
    }
    private void loadInfoListener(View foodview, final String img, final String name, final String price, final String discount, final String fav, final String des, final String type, final String totalOrders, final String rateed, final String sumRating, final ImageView imageView)
    {
            foodview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
            Intent intent = new Intent(getContext(), food_info.class);
            intent.putExtra("Image", img);
            intent.putExtra("Name", name);
            intent.putExtra("Price", String.valueOf(price));
            intent.putExtra("Discount", String.valueOf(discount));
            intent.putExtra("Favorite", fav);
            intent.putExtra("Description", des);
            intent.putExtra("Type", type);
            intent.putExtra("Hide", false);
            intent.putExtra("Total_orders", String.valueOf(totalOrders));
            intent.putExtra("No_of_Ratings", String.valueOf(rateed));
            intent.putExtra("Ratings", String.valueOf(sumRating));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation
                            (getActivity(), imageView, "My_Animation");
            startActivity(intent, optionsCompat.toBundle());
                    }catch (Exception e){e.printStackTrace();}

                }
            });
    }
    public String setDiscount(View mView,String discount, String price) {
        TextView t = mView.findViewById(R.id.cakeoriginalPrice);
        TextView t2 = mView.findViewById(R.id.price);
        try {

            if (discount.isEmpty() || discount.equals(price)) {
                t.setVisibility(View.GONE);
                return price;
            } else {
                t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                t.setText("₹" + price);
                t2.setVisibility(View.VISIBLE);
                mView.findViewById(R.id.CakePrice).setVisibility(View.GONE);
                t.setVisibility(View.VISIBLE);
                t2.setText("₹" + discount);
                return discount;
            }
        } catch (Exception NullPointerException) {
            mView.findViewById(R.id.CakePrice).setVisibility(View.VISIBLE);

            t.setVisibility(View.GONE);
            return price;
        }
    }

    void firebase() {

             progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                mDatabase


        )
        {
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                progressBar.setVisibility(View.GONE);
                {
                    viewHolder.setName(model.getFood_name());
                    set_food_info(viewHolder.mView,model.getFood_name(),(TextView) viewHolder.mView.findViewById(R.id.CakePrice),(TextView) viewHolder.mView.findViewById(R.id.Desc),model.getType());

                }
            }

        };
        recyclerView.setAdapter(FBRA);

    }

    public    void foodcart(final String Food, final long Price, final String Type, final String Image) {
        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/FoodCart/Food");
        mDatabase.child(Food).child("Food_name").setValue(Food);
        mDatabase.child(Food).child("price").setValue(Price);

        mDatabase.child(Food).child("Type").setValue(Type);

        mDatabase.child(Food).child("Food_Image").setValue(Image);

        mDatabase.child(Food).child("Quantity").setValue("1");

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

    /**
     * Converting dp to pixel
     */
    public  int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }




}

