package com.sanproject.mcafe.food_items;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.sanproject.mcafe.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


public class food_type_filter extends AppCompatActivity {
    public static String ADDRESS="";
    public static String QUERY="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_items_dialog);
        Tablayout();
         getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }
    private void Tablayout() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new  orderFragment1()).commit();
    }
    public static class orderFragment1 extends Fragment {

        public TabLayout tabLayout;
        public ViewPager viewPager;
        public    int int_items= 2;



        public orderFragment1() {
         }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.tab_food_filters,null);
            tabLayout=(TabLayout)v.findViewById(R.id.tabs);
            viewPager=(ViewPager)v.findViewById(R.id.viewpager);



            viewPager.setAdapter(new  MyAdapter( getChildFragmentManager()));
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);

                }
            });
            return v;
        }

        public class MyAdapter  extends FragmentPagerAdapter {
             public MyAdapter(FragmentManager fm)
            {
                super(fm);
            }
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return new Food_type();
                    case 1:
                        return new Filters();



                }
                return null;
            }

            @Override
            public int getCount() {


                return int_items;
            }

            public CharSequence getPageTitle(int position){
                switch (position){

                    case 0:
                        return "Food";
                    case 1:
                        return "Filters";

                }

                return null;
            }

        }

    }
    public static class Food_type extends Fragment {

View v;

        @Override
        public void onStart() {
            super.onStart();
            if (ADDRESS .contains("Snaks"))
            {   tick_visibility( R.id.snacks_tick);
                v.findViewById(R.id.show_tick).setVisibility(View.INVISIBLE);
            }
            else   if (ADDRESS .contains("Beverages"))
            {tick_visibility( R.id.beverages_tick);
                v.findViewById(R.id.show_tick).setVisibility(View.INVISIBLE);
            }
            else  if (ADDRESS .contains("Meal"))
            {   tick_visibility( R.id.meal_tick);
                v.findViewById(R.id.show_tick).setVisibility(View.INVISIBLE);
            }
             else  if (ADDRESS .contains("Special"))
            {   tick_visibility( R.id.specia_tick);
                v.findViewById(R.id.show_tick).setVisibility(View.INVISIBLE);
            }else if (ADDRESS.contains("Favor")){
                tick_visibility( R.id.fav_tick);
                v.findViewById(R.id.show_tick).setVisibility(View.INVISIBLE);
            }
            else
            {tick_visibility( R.id.show_tick);}

        }
        void tick_visibility(int ids){

                v.findViewById(ids).setVisibility(View.VISIBLE);
            }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

              v = inflater.inflate(R.layout.food_type, null);
setDialog_listners(v);
            return v;
        }
        void  setDialog_listners(View view ){
            view.findViewById(R.id.snacks).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                ADDRESS="Food Items/Snaks";
                 getActivity().finish();
                 }
            });
            view.findViewById(R.id.beverages).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ADDRESS="Food Items/Beverages";
                    getActivity().finish();

                }
            });
            view.findViewById(R.id.meal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ADDRESS="Food Items/Meal";
                    getActivity().finish();


                }
            });
            view.findViewById(R.id.special).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ADDRESS="Food Items/Special";
                    getActivity().finish();

                }
            });
            view.findViewById(R.id.show_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ADDRESS="Show All";
                    getActivity().finish();



                }
            });
            view.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ADDRESS=FirebaseAuth.getInstance().getCurrentUser().getUid() +"/Favorite";
                    getActivity().finish();
                }
            });
        }


    }

    public static class Filters extends Fragment {

        View v;
        public Filters() {
        }

        @Override
        public void onStart() {
            super.onStart();
            TextView textView=v.findViewById(R.id.Filtertitle);
            if(ADDRESS==null||ADDRESS.contains("Show")||ADDRESS.isEmpty()){
                Glide.with(getContext()).load(R.drawable.all_included).into((ImageView) v.findViewById(R.id.titleImg));

                textView.setText("Show All");
                v.findViewById(R.id.relative_filters).setVisibility(View.VISIBLE);
                v.findViewById(R.id.sort_none).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Price_high_low).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Price_low_high).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_favorite).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_ordered).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_rating).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Customer_reviews).setVisibility(View.INVISIBLE);
            }else if(ADDRESS.contains("Fav")){
                textView.setText("Favorite");
                Glide.with(getContext()).load(R.drawable.heart_blue).into((ImageView) v.findViewById(R.id.titleImg));
                v.findViewById(R.id.relative_filters).setVisibility(View.VISIBLE);
                v.findViewById(R.id.sort_none).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Price_high_low).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Price_low_high).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_favorite).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_ordered).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.most_rating).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.Customer_reviews).setVisibility(View.INVISIBLE);
            }
            if (QUERY .equals("discount"))
            tick_visibility( R.id.high_tick);
            else   if (QUERY .equals("discount_reverse"))
                tick_visibility(R.id.low_tick);
            else  if (QUERY .contains("Total_NoOfTime_Rated"))
                tick_visibility(R.id.review_tick);

            else  if (QUERY .contains("Sum_of_Rating"))
                tick_visibility(R.id.rating_tick);
            else  if (QUERY .contains("Total_orders"))
                tick_visibility(R.id.order_tick);
            else  if (QUERY .contains("Favorite"))
                tick_visibility(R.id.favorite_tick);
            else tick_visibility(R.id.none_tick);
        }
        void tick_visibility(int ids){
              v.findViewById(ids).setVisibility(View.VISIBLE);

        }

        void filter_Listeners(View view ){
            view.findViewById(R.id.sort_none).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QUERY= " " ;
                    getActivity().finish();
                }
            });
             view.findViewById(R.id.Price_high_low).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  QUERY= "discount" ;
                  getActivity().finish();
                  }
            });
            view.findViewById(R.id.Price_low_high).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     QUERY= "discount_reverse" ;
                     getActivity().finish();

                }
            });
            view.findViewById(R.id.Customer_reviews).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     QUERY= "Total_NoOfTime_Rated" ;
                    getActivity().finish();


                }
            });
            view.findViewById(R.id.most_rating).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     QUERY= "Sum_of_Rating" ;
                    getActivity().finish();


                }
            });
            view.findViewById(R.id.most_ordered).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     QUERY= "Total_orders" ;
                    getActivity().finish();


                }
            });
            view.findViewById(R.id.most_favorite).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QUERY= "Favorite" ;
                    getActivity().finish();


                }
            });

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

              v = inflater.inflate(R.layout.filters_layout, null);
                filter_Listeners(v);
            return v;
        }

    }

}
