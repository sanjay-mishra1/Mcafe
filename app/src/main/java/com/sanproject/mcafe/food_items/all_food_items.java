package com.sanproject.mcafe.food_items;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.Food;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.adapter_allorders;
import com.sanproject.mcafe.cakeActivity;
import com.sanproject.mcafe.food_info;
import com.sanproject.mcafe.mainactiv;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class all_food_items extends Fragment {
     public  int count=0;
    ProgressBar progressBar;
    RecyclerviewAdapter recycler;
    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
     private List<_Food_> list;
    private adapter_allorders adapter;
    private boolean check;

    public all_food_items(){

    }
    RecyclerView recyclerView;//= view.findViewById(R.id.snaks);
     View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
          view =inflater.inflate(R.layout.all_food_items,container,false);
        count=0;
        Log.e("Canteen","Canteen is "+Canteen);
        progressBar=view.findViewById(R.id.progressbar);
          LoadFirebase();
         return view;

    }


   void firebase1(RecyclerView recyclerView, int size){
           RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
       // if (check)
        {recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,size, true));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+ "/Snaks");
        FirebaseRecyclerAdapter <Food, mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                 mDatabase


        ) {
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }


                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity(), model.getFood_Image());
                final String price = viewHolder.setDiscount(String.valueOf(model.getDiscount()), String.valueOf(model.getPrice()));


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if (!model.getFood_name().toUpperCase().contains("CAKE"))
                        {
                            Intent intent = new Intent(getContext(), food_info.class);
                            intent.putExtra("Image", model.getFood_Image());
                            intent.putExtra("Name", model.getFood_name());
                            intent.putExtra("Price", String.valueOf(model.getPrice()));
                            intent.putExtra("Discount", String.valueOf(model.getDiscount()));
                            intent.putExtra("Favorite", model.getFavorite());
                            intent.putExtra("Description", model.getDescription());
                            intent.putExtra("Type", model.getType());
                            intent.putExtra("Hide",false);

                            intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                            intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                            intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation
                                            (getActivity(), viewHolder.getImageId(), "My_Animation");
                            startActivity(intent, optionsCompat.toBundle());

                        }
                    }
                });



                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View view) {
                           {
                             viewHolder.mView.setBackgroundColor(Color.RED);
                          Handler handler=new Handler();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                    viewHolder.mView
                                             .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                 }
                             },3000);
                             foodcart(model.getFood_name(),Integer.parseInt(price), model.getType(), model.getFood_Image());
                         }
                    }
                });


            }

            @Override
            public int getItemCount() {
                if (super.getItemCount()>0){
                    getView().findViewById(R.id.snacksTitle).setVisibility(View.VISIBLE);
                }
                return super.getItemCount();
            }
        };

        recyclerView.setAdapter(FBRA);

    }
    void firebase2(RecyclerView recyclerView,String Address,int size){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        // if (check)
        {recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,size, true));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+"/"+Address);
        FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                mDatabase


        ) {
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }


                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity(), model.getFood_Image());
                final String price = viewHolder.setDiscount(String.valueOf(model.getDiscount()), String.valueOf(model.getPrice()));


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!model.getFood_name().toUpperCase().contains("CAKE")) {
                            Intent intent = new Intent(getContext(), food_info.class);
                            intent.putExtra("Image", model.getFood_Image());
                            intent.putExtra("Name", model.getFood_name());
                            intent.putExtra("Price", String.valueOf(model.getPrice()));
                            intent.putExtra("Discount", String.valueOf(model.getDiscount()));
                            intent.putExtra("Favorite", model.getFavorite());
                            intent.putExtra("Description", model.getDescription());
                            intent.putExtra("Type", model.getType());
                            intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                            intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                            intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation
                                            (getActivity(), viewHolder.getImageId(), "My_Animation");
                            startActivity(intent, optionsCompat.toBundle());

                        }else{
                            startActivity(new Intent(getActivity(),cakeActivity.class));
                        }
                    }
                });



                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getFood_name().toUpperCase().contains("CAKE")) {
                            startActivity(new Intent(getActivity(), cakeActivity.class));
                        }else {
                            viewHolder.mView.setBackgroundColor(Color.RED);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.mView
                                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                }
                            },3000);
                            foodcart(model.getFood_name(),Integer.parseInt(price), model.getType(), model.getFood_Image());
                        }
                    }
                });


            }
            @Override
            public int getItemCount() {
                if (super.getItemCount()>0){
                    getView().findViewById(R.id.beveragesTitle).setVisibility(View.VISIBLE);
                }
                return super.getItemCount();
            }
        };

        recyclerView.setAdapter(FBRA);

    }
    void firebase3(RecyclerView recyclerView,String Address,int size){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        // if (check)
        {recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,size, true));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+"/"+Address);
        FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                mDatabase


        ) {
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }


                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity(), model.getFood_Image());
                final String price = viewHolder.setDiscount(String.valueOf(model.getDiscount()), String.valueOf(model.getPrice()));


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!model.getFood_name().toUpperCase().contains("CAKE")) {
                            Intent intent = new Intent(getContext(), food_info.class);
                            intent.putExtra("Image", model.getFood_Image());
                            intent.putExtra("Name", model.getFood_name());
                            intent.putExtra("Price", String.valueOf(model.getPrice()));
                            intent.putExtra("Discount", String.valueOf(model.getDiscount()));
                            intent.putExtra("Favorite", model.getFavorite());
                            intent.putExtra("Description", model.getDescription());
                            intent.putExtra("Type", model.getType());
                            intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                            intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                            intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation
                                            (getActivity(), viewHolder.getImageId(), "My_Animation");
                            startActivity(intent, optionsCompat.toBundle());

                        }else{
                            startActivity(new Intent(getActivity(),cakeActivity.class));
                        }
                    }
                });



                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getFood_name().toUpperCase().contains("CAKE")) {
                            startActivity(new Intent(getActivity(),cakeActivity.class));
                        }else {
                            viewHolder.mView.setBackgroundColor(Color.RED);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.mView
                                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                }
                            },3000);
                            foodcart(model.getFood_name(),Integer.parseInt(price), model.getType(), model.getFood_Image());
                        }
                    }
                });


            }
            @Override
            public int getItemCount() {
                if (super.getItemCount()>0){
                    getView().findViewById(R.id.mealTitle).setVisibility(View.VISIBLE);
                }
                return super.getItemCount();
            }
        };

        recyclerView.setAdapter(FBRA);

    }
    void firebase4(RecyclerView recyclerView,String Address,int size){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        // if (check)
        {recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,size, true));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+"/"+Address);
        FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                mDatabase


        ) {
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }


                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity(), model.getFood_Image());
                final String price = viewHolder.setDiscount(String.valueOf(model.getDiscount()), String.valueOf(model.getPrice()));


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!model.getFood_name().toUpperCase().contains("CAKE")) {
                            Intent intent = new Intent(getContext(), food_info.class);
                            intent.putExtra("Image", model.getFood_Image());
                            intent.putExtra("Name", model.getFood_name());
                            intent.putExtra("Price", String.valueOf(model.getPrice()));
                            intent.putExtra("Discount", String.valueOf(model.getDiscount()));
                            intent.putExtra("Favorite", model.getFavorite());
                            intent.putExtra("Description", model.getDescription());
                            intent.putExtra("Type", model.getType());
                            intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                            intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                            intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation
                                            (getActivity(), viewHolder.getImageId(), "My_Animation");
                            startActivity(intent, optionsCompat.toBundle());

                        }else{
                            startActivity(new Intent(getActivity(),cakeActivity.class));
                        }
                    }
                });



                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getFood_name().toUpperCase().contains("CAKE")) {
                            startActivity(new Intent(getActivity(),cakeActivity.class));
                        }else {
                            viewHolder.mView.setBackgroundColor(Color.RED);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.mView
                                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                }
                            },3000);
                            foodcart(model.getFood_name(),Integer.parseInt(price), model.getType(), model.getFood_Image());
                        }
                    }
                });


            }
            @Override
            public int getItemCount() {
                if (super.getItemCount()>0){
                    getView().findViewById(R.id.specialTitle).setVisibility(View.VISIBLE);
                }
                return super.getItemCount();
            }
        };

        recyclerView.setAdapter(FBRA);

    }
        void LoadFirebase() {
          //  recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
             int size=dpToPx(10);
            firebase1((RecyclerView) view.findViewById(R.id.snaks), size);
            firebase2((RecyclerView)view.findViewById(R.id.beverages),"Beverages",size);
            firebase3((RecyclerView) view.findViewById(R.id.meal),"Meal",size);
           firebase4((RecyclerView) view.findViewById(R.id.special),"Special",size);

        }
  public    void foodcart(final String Food, final int Price, final String Type, final String Image) {
        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/FoodCart/Food");
        mDatabase.child(Food).child("Food_name").setValue(Food);
        mDatabase.child(Food).child("price").setValue(Price);

        mDatabase.child(Food).child("Type").setValue(Type);

        mDatabase.child(Food).child("Food_Image").setValue(Image);

        mDatabase.child(Food).child("Quantity").setValue("1");

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onStart() {
        super.onStart();
       // list = new ArrayList<>();

       //  SingleAdapter_recycler();
       // recycler = new RecyclerviewAdapter(list);
    }

    void SingleAdapter_recycler(){
        Log.e("All Food items" ,"Inside singleadapter");
        list = new ArrayList<>();
     //   duplicateList = new ArrayList<>();

        setdata((RecyclerView) view.findViewById(R.id.snaks),"Snaks");
         setdata((RecyclerView)view.findViewById(R.id.snaks),"Beverages");
         setdata((RecyclerView) view.findViewById(R.id.snaks),"Meal");
        setdata((RecyclerView) view.findViewById(R.id.snaks),"Special");
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
         Log.e("All food items","List size "+ String.valueOf(list.size()));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

    }
     void setdata( final RecyclerView recyclerView,String Address){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Food Items/"
                +Address);
        Log.e("All Food items" ,"Inside setdata");
         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (view.findViewById(R.id.progressbar).getVisibility()==View.VISIBLE)
                   view.findViewById(R.id.progressbar).setVisibility(View.GONE);

                Log.e("All Food items" ,"Inside datasnapshot");


                 for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                     Food model = dataSnapshot1.getValue(Food.class);
                    _Food_ listdata = new _Food_();

                     assert model != null;
//                     duplicateList.add(model.getFood_name());
                     listdata.setFood_name(model.getFood_name());
                    listdata.setPrice(model.getPrice());
//                    Log.e("Type",model.getType());
                    listdata.setDescription(model.getDescription());
                    listdata.setFood_Image(model.getFood_Image());
                    listdata.setDiscount(model.getDiscount());
                    listdata.setType(model.getType());
                    listdata.setFavorite(model.getFavorite());
                    listdata.setSum_of_Ratings(model.getSum_of_Ratings());
                    listdata.setTotal_NoOfTime_Rated(model.getTotal_NoOfTime_Rated());
                    listdata.setTotal_orders(model.getTotal_orders());
                     list.add(listdata);

                }
                recyclerView.setAdapter(recycler);

             }


            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("All Food items","Error "+databaseReference.toString());
            }
        });

    }


    void child_listener(){
    FirebaseDatabase.getInstance().getReference("Food Items/Snaks").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
         /*   for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                Log.e("All food items","Inside data added s is "+s+" datasnapshot is "+dataSnapshot.child("Food_name").getValue());

                Food model = dataSnapshot1.getValue(Food.class);
                _Food_ listdata = new _Food_();

                assert model != null;
                listdata.setFood_name(model.getFood_name());
                listdata.setPrice(model.getPrice());
//                    Log.e("Type",model.getType());
                listdata.setDescription(model.getDescription());
                listdata.setFood_Image(model.getFood_Image());
                listdata.setDiscount(model.getDiscount());
                listdata.setType(model.getType());
                listdata.setFavorite(model.getFavorite());
                listdata.setSum_of_Ratings(model.getSum_of_Ratings());
                listdata.setTotal_NoOfTime_Rated(model.getTotal_NoOfTime_Rated());
                listdata.setTotal_orders(model.getTotal_orders());
                list.add(listdata);*/

            }      //   }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String name=(String)dataSnapshot.child("Food_name").getValue();
         //   Log.e("All food items","Inside data change s is "+s+" datasnapshot is "+dataSnapshot.child("Food_name").getValue()+" position in list is "+duplicateList.indexOf(name));
            _Food_ listdata = new _Food_();
            Food model = dataSnapshot.getValue(Food.class);

            assert model != null;
            listdata.setFood_name(model.getFood_name());
            listdata.setPrice(model.getPrice());
//                    Log.e("Type",model.getType());
            listdata.setDescription(model.getDescription());
            listdata.setFood_Image(model.getFood_Image());
            listdata.setDiscount(model.getDiscount());
            listdata.setType(model.getType());
            listdata.setFavorite(model.getFavorite());
            listdata.setSum_of_Ratings(model.getSum_of_Ratings());
            listdata.setTotal_NoOfTime_Rated(model.getTotal_NoOfTime_Rated());
            listdata.setTotal_orders(model.getTotal_orders());
        //    list.add(duplicateList.indexOf(name),listdata);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
       //     SingleAdapter_recycler();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //    SingleAdapter_recycler();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    }
}
  class _Food_ {

    public String Food_name;
    public int price;
    public String Description;
    public String Food_Image;
    public int discount;
    public String Type;
      public int Favorite;
      public int Total_NoOfTime_Rated;
      public int Sum_of_Ratings;
      public int Total_orders;

      public void setType(String type) {
          Type = type;
      }

      public void setFavorite(int favorite) {
          Favorite = favorite;
      }

      public void setTotal_NoOfTime_Rated(int total_NoOfTime_Rated) {
          Total_NoOfTime_Rated = total_NoOfTime_Rated;
      }

      public void setSum_of_Ratings(int sum_of_Ratings) {
          Sum_of_Ratings = sum_of_Ratings;
      }

      public void setTotal_orders(int total_orders) {
          Total_orders = total_orders;
      }

      public int getTotal_orders() {
          return Total_orders;
      }

      public int getFavorite() {
          return Favorite;
      }

      public int getTotal_NoOfTime_Rated() {
          return Total_NoOfTime_Rated;
      }

      public int getSum_of_Ratings() {
          return Sum_of_Ratings;
      }

      public String getType() {
          return Type;
      }

      public String getFood_name() {
        return Food_name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return Description;
    }

    public String getFood_Image() {
        return Food_Image;
    }

      public int getDiscount() {
          return discount;
      }

      public void setFood_name(String food_name) {
          Food_name = food_name;
      }

      public void setPrice(int price) {
          this.price = price;
      }

      public void setDescription(String description) {
          Description = description;
      }

      public void setFood_Image(String food_Image) {
          Food_Image = food_Image;
      }

      public void setDiscount(int discount) {
          this.discount = discount;
      }
  }

         class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder>{

        List<_Food_> listdata;

          RecyclerviewAdapter(List<_Food_> listdata) {
            this.listdata = listdata;
            setHasStableIds(true);
        }

             private View view;
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
              view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card,parent,false);

            return new MyHolder(view);
        }
                  private void foodcart(final String Food, final int Price, final String Type, final String Image) {
            FirebaseAuth     mAuth=FirebaseAuth.getInstance();
           DatabaseReference      mDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/FoodCart/Food");
                 mDatabase.child(Food).child("Food_name").setValue(Food);
                 mDatabase.child(Food).child("price").setValue(Price);

                 mDatabase.child(Food).child("Type").setValue(Type);

                 mDatabase.child(Food).child("Food_Image").setValue(Image);

                 mDatabase.child(Food).child("Quantity").setValue("1");

             }


        public void onBindViewHolder(final MyHolder holder, int position) {
            final _Food_ data = listdata.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!data.getFood_name().toUpperCase().contains("CAKE")) {
                        Intent intent = new Intent(  holder.itemView.getContext() , food_info.class);
                        intent.putExtra("Image", data.getFood_Image());
                        intent.putExtra("Name", data.getFood_name());
                        intent.putExtra("Price", String.valueOf(data.getPrice()));
                        intent.putExtra("Discount", String.valueOf(data.getDiscount()));
                        intent.putExtra("Favorite", data.getFavorite());
                        intent.putExtra("Description", data.getDescription());
                        intent.putExtra("Type", data.getType());
                        intent.putExtra("Total_orders",String.valueOf(data.getTotal_orders()));
                        intent.putExtra("No_of_Ratings",String.valueOf(data.getTotal_NoOfTime_Rated()));
                        intent.putExtra("Ratings",String.valueOf(data.getSum_of_Ratings()));
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation
                                        ((Activity) holder.itemView.getContext(), holder.imageView , "My_Animation");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.itemView.getContext().startActivity(intent, optionsCompat.toBundle());
                        }else{
                            holder.itemView.getContext().startActivity(intent);
                        }

                    }else{
                        holder.itemView.getContext().startActivity(new Intent( holder.itemView.getContext(),cakeActivity.class));
                    }
                }
            });
           try {
               holder.name.setText(data.getFood_name());
           }catch (Exception ignored){}
           try {
               holder.description.setText(data.getDescription());
           }catch (Exception ignored){}
           try {
               holder.discount.setText(data.getDiscount());
           }catch (Exception ignored){}
         try {
               holder.price.setText(String.format("₹%s", String.valueOf(data.getPrice())));
         }catch (Exception e){
               Log.e("All food items","price is"+e.toString());
         }
         setDiscount(String.valueOf(data.getDiscount()),String.valueOf(data.getPrice()),holder.itemView);
          try {
               Glide.with(view.getContext()).load(data.getFood_Image()).into(holder.imageView);
          }catch (Exception ignored){}
          holder.imageView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (data.getFood_name().toUpperCase().contains("CAKE")) {
                      holder.itemView.getContext().startActivity(new Intent(  holder.itemView.getContext(),cakeActivity.class));
                  }else {
                      holder.itemView.setBackgroundColor(Color.RED);
                      Handler handler=new Handler();
                      handler.postDelayed(new Runnable() {
                          @Override
                          public void run() {
                           holder.itemView
                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                          }
                      },3000);
                      foodcart(data.getFood_name(), data.getDiscount(), data.getType(), data.getFood_Image());
                  }
              }
          });
        }
             private String setDiscount(String discount, String price, View mView) {
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

             @Override
             public long getItemId(int position) {
                 return super.getItemId(position);
             }


             @Override
        public int getItemCount() {
            return listdata.size();
        }


        class MyHolder extends RecyclerView.ViewHolder{
            TextView name , price,discount,description;
            ImageView imageView;

            MyHolder(final View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.title);
                price = (TextView) itemView.findViewById(R.id.CakePrice);
                discount = (TextView) itemView.findViewById(R.id.cakeoriginalPrice);
                description = (TextView) itemView.findViewById(R.id.Desc);
                imageView=itemView.findViewById(R.id.thumbnail);
            }
        }


    }
