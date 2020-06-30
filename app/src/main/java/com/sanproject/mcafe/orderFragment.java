package com.sanproject.mcafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by sanjay on 08/06/2018.
 */

public class orderFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    public  static int int_items= 2;


    public orderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tab,null);
        tabLayout=(TabLayout)v.findViewById(R.id.tabs);
        viewPager=(ViewPager)v.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter( getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);

            }
        });
        return v;
    }

    public class MyAdapter  extends FragmentPagerAdapter {
        //Context context=null;
        int count=0;
        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new pendingorders();
                case 1:
                    return new allOrders();



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
                    return "Active Orders";
                case 1:
                    return "All Orders";

            }

            return null;
        }

    }


    }
