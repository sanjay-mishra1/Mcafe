package com.sanproject.mcafe;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


//import static com.example.sanjay.tabcard.TabFragment.int_items;


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

                 return new food_special();
            case 1:
                return new food_beverages();

            case 2:
               return new food_snaks();
            case 3:
                return new Food_meal();


        }
        return null;
    }

    @Override
    public int getCount() {


        return 0;
    }
/*
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Special";
            case 1:
                return "Beverage";
            case 2:
                return "Snaks";
            case 3:
                return "Meal";


        }

        return null;
    } */

}
