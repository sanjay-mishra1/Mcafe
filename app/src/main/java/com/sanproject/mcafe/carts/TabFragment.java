package com.sanproject.mcafe.carts;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.sanproject.mcafe.MyAdapter;
import com.sanproject.mcafe.R;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    public  static TabLayout tabLayout;
    public  static ViewPager viewPager;
    public  static int int_items= 4;



    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_main,null);
        tabLayout=(TabLayout)v.findViewById(R.id.tabs);
        viewPager=(ViewPager)v.findViewById(R.id.viewpager);
       // PagerAdapter pagerAdapter=new PagerAdapter(()) ;
        //set an adpater
      //  tabLayout.getTabAt(0).setIcon(R.drawable.cancel);
        viewPager.setAdapter(new MyAdapter( getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
              //  int[] ar={R.drawable.cake,R.drawable.tea,R.drawable.snaks,R.drawable.meal};
                setupTabIcons();
              /* for(int i=0;i<int_items;i++){
                  try {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageResource(ar[i]);
                     tabLayout.getTabAt(i).setIcon(ar[i]);
                      tabLayout.getTabAt(i).setCustomView(imageView);
                  }catch (Exception NullPointerException){}
               }*/
                   }
        });
        return v;
    }
    private  void setupTabIcons(){
        int[] ar={R.drawable.cake,R.drawable.tea,R.drawable.snaks,R.drawable.meal};

         tabLayout.getTabAt(0).setIcon(ar[0]);
        tabLayout.getTabAt(1).setIcon(ar[1]);
        tabLayout.getTabAt(2).setIcon(ar[2]);
        tabLayout.getTabAt(3).setIcon(ar[3]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    tab.getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                }catch (Exception NullPointerException){}
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                try {tab.getIcon().setColorFilter(Color.parseColor("#a8a8a8"),PorterDuff.Mode.SRC_IN);
                }catch (Exception NullPointerException){}

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
