package com.sanproject.mcafe.carts;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanproject.mcafe.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class cartFragments extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cart);

        show_dialog();

    }
    void show_dialog(){
        final CardView view1= findViewById(R.id.cart_type_selector);
        FragmentManager FM;
        FM = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
        fragmentTransaction1.replace(R.id.containerView, new FoodCart()).commit();

         findViewById(R.id.cart_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view1.getVisibility()==View.VISIBLE){
                     view1.setVisibility(View.GONE);

                 }else{
                     view1.setVisibility(View.VISIBLE);
                    Listener_for_food();
                }
            }
        });
    }
    void Listener_for_food(){
        findViewById(R.id.top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_cart_type(true);
              }
        });
        findViewById(R.id.bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_cart_type(false);
          }
        });
    }
 void change_cart_type(boolean place){
    if (place){
        TextView textView=findViewById(R.id.top_text);
      if (textView.getText().toString().trim().toUpperCase().contains("CAKE")){

          findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
      }else{

          findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
      }
    }
     if (!place){
         TextView textView=findViewById(R.id.bottom_text);
         if (textView.getText().toString().trim().toUpperCase().contains("CAKE")){
                change_type(" CakeCart","     Cart      ");
             FragmentManager FM;
             FM = getSupportFragmentManager();
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, new CakeCart()).commit();
             findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
         }else{
             change_type("  Cart      "," CakeCart");
             FragmentManager FM;
             FM = getSupportFragmentManager();
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, new FoodCart()).commit();
             findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
         }
     }
 }
    void change_type(String top,String bottom){
        int img_top, img_bottom;
        TextView textView=findViewById(R.id.top_text);
        textView.setText(top);
        textView=findViewById(R.id.bottom_text);
        textView.setText(bottom);
        if (top.toUpperCase().contains("CAKE")){
            img_top=R.drawable.cake;
            top="CakeCart";
        }else{
            img_top=R.drawable.ic_add_shopping_cart_black_24dp;
            top="Cart";
        }
        if (bottom.toUpperCase().contains("CAKE")){
            img_bottom=R.drawable.cake;
         }else{
            img_bottom=R.drawable.ic_add_shopping_cart_black_24dp;
         }


        textView=findViewById(R.id.name);
        textView.setText(top);
         Glide.with(getApplicationContext()).load(img_top).into((ImageView) findViewById(R.id.top_img));
        Glide.with(getApplicationContext()).load(img_bottom).into((ImageView) findViewById(R.id.bottom_img));
        Glide.with(getApplicationContext()).load(img_top).into((ImageView) findViewById(R.id.food_icon));
    }


    @Override
    public void onBackPressed() {
        if (findViewById(R.id.cart_type_selector).getVisibility()==View.VISIBLE){
            findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
        }else
        super.onBackPressed();


    }

    public void backtomenu(View view) {
        onBackPressed();
        view.setEnabled(false);
    }
}
