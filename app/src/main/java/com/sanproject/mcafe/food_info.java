package com.sanproject.mcafe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class food_info extends AppCompatActivity {
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
     String Receivedimage;
    Toolbar toolbar;
    private String ReceivedName;
    TextView desc,price;
    ImageView image;
    private String Receivedtype;
    private String Selected_stars="0";
    int ReceivedNoOfRatings;
    int ReceivedRatings;
    FirebaseAuth mauth;
    private String Receivedprice;
    String ratingsum="1";
    String ratingno="1";
    private int Favorite;
    boolean favstar=false;
    boolean anim_Status=true;
    private String price_to_save="";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_foodinfo);
            mauth=FirebaseAuth.getInstance();
        desc=findViewById(R.id.collapse_desc);

        imageView5=findViewById(R.id.Selectstar5);
        imageView4=findViewById(R.id.Selectstar4);
        imageView3=findViewById(R.id.Selectstar3);
        imageView2=findViewById(R.id.Selectstar2);
        imageView1=findViewById(R.id.Selectstar1);
        TextView orders=findViewById(R.id.totalOrders);
          ratingsum="1";
          ratingno="1";

             Intent intent=getIntent();
             Receivedimage= intent.getStringExtra ("Image");
             ReceivedName=intent.getStringExtra("Name");
            TextView name=findViewById(R.id.foodname);
              name.setText(ReceivedName);
        name=findViewById(R.id.foodname_expand);
        name.setText(ReceivedName);
             Receivedtype=intent.getStringExtra("Type");
             Receivedprice=intent.getStringExtra("Price");
            setprice(intent.getStringExtra("Discount"),intent.getStringExtra("Price"));
            ratingsum = intent.getStringExtra("Ratings");

            ratingno =  intent.getStringExtra("No_of_Ratings");
            if (ratingno==null){
                ratingno="0";
                ratingsum="0";
            }
            TextView totalratings=findViewById(R.id.totalratings);
            totalratings.setText(String.format("overall %s ratings", ratingno));

             desc.setText(intent.getStringExtra("Description"));
         TextView textView=findViewById(R.id.total_favs);

         Favorite=intent.getIntExtra("Favorite",0);
      //  intent.putExtra("Hide",false);
        if (  intent.getBooleanExtra("Hide",false)){
            Log.e("Hide savecart", "It is true");
           findViewById(R.id.addtocart).setVisibility(View.GONE);
        }
        try {  if (Favorite!=0)
         {
             textView.setText(String.format("  %s people loved this food",  Favorite ));}
             else{
             textView.setText(R.string.person_loves);
         }
         }catch (Exception e){ Log.e("Favorite","Inside oncreate catch "+Favorite);
             textView.setText("No one makes this food favorite");

         }
            check_fav_text(textView);
          try {
              if (!intent.getStringExtra("Total_orders").equals("null")||
                      intent.getStringExtra("Total_orders").equals(null)
                      ) {
                  orders.setText(String.format("%s %s", intent.getStringExtra("Total_orders"),

                          getString(R.string.total_order)));
              }else{
                  orders.setText(R.string.no_orders_food);
              }
          }catch (Exception e){
              orders.setText(R.string.no_orders_food);
          }
             image=findViewById(R.id.backdrop);
         Display  display = getWindowManager().getDefaultDisplay();
         LinearLayout.LayoutParams params;
         params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,  (display.getWidth()/2)+10 );
         image.setLayoutParams(params);
             Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(Receivedimage).into(image);
        load_data();
        setTotalRating(ratingno,ratingsum);
        settype(Receivedtype);
        try {
            ReceivedRatings= Integer.parseInt(ratingsum);
            ReceivedNoOfRatings= Integer.parseInt(ratingno);
         }catch (Exception ignored){

        }
          selectStars();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
         IsFood_in_Folder("FoodCart/Food");
         IsFood_in_Folder("Favorite");
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       ScrollView scrollView=findViewById(R.id.scrollview);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    if (findViewById(R.id.foodname_expand).getVisibility()==View.VISIBLE)
                  if (anim_Status )
                  {    animation(false,(TextView) findViewById(R.id.foodname_expand));
                       anim_Status=true;
                  }

                }

                return false;
            }

        });
        load_data_listener();
         }
         void set_Firebase_data(long rating, long Ratingsum, long Total_Orders, long Favs,String Description){
        try { ratingsum= String.valueOf(Ratingsum);
            ReceivedNoOfRatings=(int) rating;
            setTotalRating(String.valueOf(rating),String.valueOf(Ratingsum));
        }catch (Exception e){setTotalRating("0",String.valueOf(Ratingsum));}
        TextView textView=findViewById(R.id.total_favs);
             try {  if (Favs!=0)
             {
                 textView.setText(String.format("  %s people loved this food",  Favorite ));}
             else{
                 textView.setText(R.string.person_loves);
             }
             }catch (Exception e){ Log.e("Favorite","Inside oncreate catch "+Favorite);
                 textView.setText(R.string.person_loves);

             }
             textView=findViewById(R.id.totalOrders);
             try {
                 if (Total_Orders!=0) {
                     textView.setText(String.format("%s %s",Total_Orders,

                             getString(R.string.total_order)));
                 }else{
                     textView.setText(R.string.no_orders_food);
                 }
             }catch (Exception e){
                 textView.setText(R.string.no_orders_food);
             }
            try {
                 desc.setText(Description);
            }catch (Exception ignored){}
         }
         void setOrder(int Total_Orders){
             Log.e("Load Listener","Inside setorders "+Total_Orders);
          TextView textView=findViewById(R.id.totalOrders);
             try {
                 if (Total_Orders!=0) {
                     textView.setText(String.format("%s %s",Total_Orders,

                             getString(R.string.total_order)));
                 }else{
                     textView.setText(R.string.no_orders_food);
                 }
             }catch (Exception e){
                 textView.setText(R.string.no_orders_food);
             }
         }
         void setFavorite(int Favs){
             Log.e("Load Listener","Inside favs "+Favs);
             TextView textView=findViewById(R.id.total_favs);
              Favorite=Favs;
             try {  if (Favs!=0)
             {   if (favstar) {
                 if (Favs <= 1) {

                     textView.setText(String.format("%s",getString(R.string.you_loved) ));
                 }else{
                     textView.setText(String.format("You and %s people loved this food",  Favs-1 ));
                 }
             }else
                 textView.setText(String.format("  %s people loved this food",  Favs ));
                 Log.e("Load Listener","Inside favs "+Favs+" favstar is "+favstar);

             }
             else{
                 textView.setText(R.string.person_loves);
             }
             }catch (Exception e){ Log.e("Favorite","Inside oncreate catch "+Favs);
                 textView.setText(R.string.person_loves);

             }
         }
         void setrating_no(int value){
             Log.e("Load Listener","Setrating"+value);
             try {// ratingsum= String.valueOf(Ratingsum);
                 ReceivedNoOfRatings=(int) value;
                 setTotalRating(String.valueOf(value),ratingsum);
             }catch (Exception e){setTotalRating("0","0");}
         }
         void setRatingsum(int value){
             Log.e("Load Listener","Rating sum "+value);
             try { ratingsum= String.valueOf(value);
                // ReceivedNoOfRatings=(int) rating;
                 setTotalRating(String.valueOf(ReceivedNoOfRatings),String.valueOf(value));
             }catch (Exception e){setTotalRating("0","0");}
         }

         void set_firebase_Change(DataSnapshot dataSnapshot){
                Log.e("Load Listener","Datasnapshot "+dataSnapshot);
             try {
                 long value = (long) dataSnapshot.getValue() ;

                 switch (dataSnapshot.getKey()) {
                     case "Favorite":
                         setFavorite((int) value);
                         break;
                     case "Total_NoOfTime_Rated":
                         setrating_no((int) value);
                         break;
                     case "Sum_of_Ratings":
                         setRatingsum((int) value);
                         break;
                     case "Total_orders":
                         setOrder((int) value);
                         break;
                     default:Log.e("Load Listener","Default nothing found");
                 }
             }catch (Exception e){Log.e("Load Listener","Error "+e.toString());}
         }
         void load_data_listener( ){
             DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                     .getReference("Food Items/"+Canteen+"/"
                     +Receivedtype+"/"+ReceivedName);
             databaseReference.addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                     Log.e("Load Listener","Data added");
                     set_firebase_Change(dataSnapshot);
                 }

                 @Override
                 public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                     Log.e("Load Listener","Data change");

                     set_firebase_Change(dataSnapshot);

                 }

                 @Override
                 public void onChildRemoved(DataSnapshot dataSnapshot) {

                 }

                 @Override
                 public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
         void load_data(){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference("Food Items/"+Canteen+"/"
                +Receivedtype+"/"+ReceivedName);
         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long totRating=0;//(int)dataSnapshot.child("Total_NoOfTime_Rated").getValue();
                long sumRating=0;//(int)dataSnapshot.child("Sum_of_Ratings").getValue();
               long totalOrder=0;// (int)dataSnapshot.child("Total_orders").getValue();
               long fav=0;
               String Desc="";//(int)dataSnapshot.child("Favorite").getValue();
               if ( dataSnapshot.child("Total_NoOfTime_Rated").exists())
                   totRating=(long)dataSnapshot.child("Total_NoOfTime_Rated").getValue();
               if (dataSnapshot.child("Sum_of_Ratings").exists())
                   sumRating=(long)dataSnapshot.child("Sum_of_Ratings").getValue();
               if (dataSnapshot.child("Total_orders").exists())
                   totalOrder=(long)dataSnapshot.child("Total_orders").getValue();
               if ( dataSnapshot.child("Favorite").exists())
                   fav=(long)dataSnapshot.child("Favorite").getValue();
                if ( dataSnapshot.child("Description").exists())
                    Desc=(String)dataSnapshot.child("Description").getValue();
                set_Firebase_data(totRating,sumRating,
                        totalOrder,fav,Desc
                        );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
         }
    void check_fav_text(TextView textView){
        String message=textView.getText().toString().trim();
        Log.e("Favorite","Inside check fav text "+message);
        if (message.contains("-1")){
            if (message.contains("You"))
            {
                textView.setText(R.string.you_loved_it);
            }else{
            textView.setText(R.string.no_one);
            }}

            if (message.toUpperCase().contains("YOU")&&message.toUpperCase().contains("AND 0")){
                textView.setText(R.string.you_loved_it);
            }
            if (message.contains("-")){
                textView.setText(message.replace("-",""));
            }
    }
    @SuppressLint("DefaultLocale")
    private void setprice(String price, String originalPrice) {
     try {
         if (price.isEmpty() ||price.equals(originalPrice)) {
             price_to_save=originalPrice;
             TextView priceText = findViewById(R.id.Collapse_price);
             priceText.setText(String.format("%s%s", getString(R.string.rupeesSymbol), originalPrice));
             Log.e("Detailview", "  price is empty");
             findViewById(R.id.original_price).setVisibility(View.GONE);
             findViewById(R.id.cakeoriginalPrice).setVisibility(View.GONE);
         } else {
              Log.e("Detailview", "  price is not empty");
             price_to_save=price;
             TextView priceText = findViewById(R.id.Collapse_price);
             priceText.setText(String.format("%s%s", getString(R.string.rupeesSymbol), price));
             priceText = findViewById(R.id.original_price);
             priceText.setPaintFlags(priceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

             priceText.setText(String.format("%s %s", getString(R.string.rupeesSymbol), originalPrice));
             priceText = findViewById(R.id.cakeoriginalPrice);
             priceText.setText(String.format("%d%s %s%d", getDiscount(Float.parseFloat(price),
                     Float.parseFloat(originalPrice)), getString(R.string.discount_tag), getString(R.string.rupeesSymbol),
                     Integer.parseInt(originalPrice) - Integer.parseInt(price))
             );
         }
     }catch (Exception e){
         price_to_save=originalPrice;
         TextView priceText = findViewById(R.id.Collapse_price);
         priceText.setText(String.format("%s%s", getString(R.string.rupeesSymbol), originalPrice));
         Log.e("Detailview", "  price is empty");
         findViewById(R.id.original_price).setVisibility(View.GONE);
         findViewById(R.id.cakeoriginalPrice).setVisibility(View.GONE);
     }


    }
      int getDiscount(float price,float originalPrice){
        return (int) (((originalPrice-price)/originalPrice)*100);
}





    void settype(String receivedtype){
        try {
            receivedtype = receivedtype.toUpperCase();
            String type[] = {"SPECIAL", "BEVERAGES", "SNAKS", "MEAL"};

            int index = Arrays.asList(type).indexOf(receivedtype);
            int[] drawable = {R.drawable.cake, R.drawable.tea, R.drawable.snaks, R.drawable.meal};
            Glide.with(getApplicationContext()).load(drawable[index]).into((ImageView) findViewById(R.id.type_of_food));
        }catch (Exception e){

        }
         }



    void setTotalRating(String ratingno,String ratingsum){
     try {Log.e("Rating check ","rating no is "+ratingno);
         if (!ratingno.isEmpty()&& !ratingno.equals("0")) {
         Log.e("Rating check ","rating no is "+ratingno);
         TextView totalratings = findViewById(R.id.totalratings);
         totalratings.setText(String.format("overall %s ratings", ratingno));
         float ReceivedRatings = Float.parseFloat(ratingsum);
         float ReceivedNoOfRatings = Integer.parseInt(ratingno);
         RatingBar ratingbar = findViewById(R.id.rating);
         ratingbar.setRating(ReceivedRatings / ReceivedNoOfRatings);
         TextView ratingstar = findViewById(R.id.rating_star);
         ratingstar.setText(String.format("%.1f", ReceivedRatings / ReceivedNoOfRatings));
     }else{
         RatingBar ratingbar = findViewById(R.id.rating);
         ratingbar.setRating(0);
         TextView totalratings = findViewById(R.id.totalratings);
         totalratings.setText(String.format(getString(R.string.total_ratings), 0));
         TextView ratingstar = findViewById(R.id.rating_star);
         ratingstar.setText("0");
     }
     }catch (Exception e){
         Log.e("Rating check ","catch rating no is "+ratingno);
        RatingBar ratingbar = findViewById(R.id.rating);
          ratingbar.setRating(0);
         TextView totalratings = findViewById(R.id.totalratings);
         totalratings.setText(String.format(getString(R.string.total_ratings), 0));
         TextView ratingstar = findViewById(R.id.rating_star);
        ratingstar.setText("0");
     }

    }


    void foodcart(final String Food, final String Price, final String Type, final String Image, boolean add)
    { DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mauth.getCurrentUser().getUid() + "/FoodCart/Food");

        if (add) {

          databaseReference.child(Food).child("Food_name").setValue(Food);
try {
    if (!price_to_save.isEmpty())
        databaseReference.child(Food).child("price").setValue(Integer.parseInt(price_to_save));
}catch (Exception e){
    databaseReference.child(Food).child("price").setValue(Integer.parseInt(Price));
}

        databaseReference.child(Food).child("Type").setValue(Type);

        databaseReference.child(Food).child("Food_Image").setValue(Image);

        databaseReference.child(Food).child("Quantity").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.progress_cart).setVisibility(View.GONE);
                        changeDrawable("ADDED");
                    }
                }, 3000);
            }
        });
    }else{
            databaseReference.child(Food).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  changeDrawable("REMOVED");
                    findViewById(R.id.progress_cart).setVisibility(View.GONE);

                }
            });

        }

    }
    void changeDrawable(String folder){
         if (folder.toUpperCase().contains("FAVORITE")){
             ImageView i=findViewById(R.id.favstar);
             favstar=true;
           TextView  textView=findViewById(R.id.total_favs);
           if (Favorite!=0)
             textView.setText(String.format("You and %s people loved this food",  Favorite-1 ));
           else   textView.setText(String.format("You and %s people loved this food",  "one more" ));

             Glide.with(getApplicationContext()).load(R.drawable.heart).into(i);
         }else {
             final TextView textView = findViewById(R.id.addtocart);

              if (folder.toUpperCase().contains("ADDED")){
                 Handler handler=new Handler();
                 textView.setText(R.string.inthecart);
                 textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick, 0, 0, 0);

                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete, 0, 0, 0);
                         textView.setText(R.string.remove_food);
                         textView.setClickable(true);
                     }
                 },4000);


             }else if(folder .contains("REMOVED")){
                textView.setText(R.string.add_to_cart);
                 textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_shopping_cart_black_24dp, 0, 0, 0);

             }else{
                  textView.setText(R.string.remove_food);
                 textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete, 0, 0, 0);

             }



          }
          check_fav_text((TextView) findViewById(R.id.total_favs));
    }
    void IsFood_in_Folder(final String Folder){
        final DatabaseReference databaseReference=
         FirebaseDatabase.getInstance().getReference().child(String.format("User Informations/%s/%s/%s", mauth.getCurrentUser().getUid(), Folder, ReceivedName));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
              if (Folder.toUpperCase().contains("FAVORITE")){
                  if (dataSnapshot.exists()) {
                      changeDrawable("FAVORITE");
                      Log.e("Child", "Exist from exist() favorite "+databaseReference);
                  }else{
                      Log.e("Child", "not Exist  favorite "+databaseReference);

                  }
              }else {
                  if (dataSnapshot.exists()) {
                      changeDrawable("CART");
                      Log.e("Child", "Exist from exist() cart "+databaseReference);
                  }else{
                      Log.e("Child", "not Exist from exist() cart "+databaseReference);

                  }

              }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    @Override
    public void onBackPressed() {
        finish();


         try{if (Integer.parseInt(Selected_stars)>0 ){
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Food Items/"+Canteen+"/"+Receivedtype+"/"+ReceivedName+"/Sum_of_Ratings").
                    setValue( Integer.parseInt(ratingsum)+Integer.parseInt(Selected_stars)).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().
                            child("Food Items/"+Canteen+"/"+Receivedtype+"/"+ReceivedName+"/Total_NoOfTime_Rated");
                    databaseReference1.setValue((ReceivedNoOfRatings+1));

                 }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
   Toast.makeText(food_info.this,"An error occurred "+e.toString(),Toast.LENGTH_SHORT).show();
                  //  supportFinishAfterTransition();
                    finish();
                }
            });
             Log.e("Back debug","Stars not empty going back to main menu");

         }
        else{
             Log.e("Back debug","Stars empty going back to main menu");
          //   supportFinishAfterTransition();
                finish();
             //super.onBackPressed();
         }
        }catch (Exception e){
            if (ratingno==null) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().
                        child("Food Items/" +Canteen+"/"+ Receivedtype + "/" + ReceivedName);
                databaseReference1.child("Total_NoOfTime_Rated").setValue(1);
                databaseReference1.child("Sum_of_Ratings").
                        setValue(Integer.parseInt(Selected_stars) );
                Log.e("Back debug","catch empty going back to main menu"+e.toString());

                Log.e("Favorite","Backpressed "+e.toString());
             //   supportFinishAfterTransition();
                finish();
               //super.onBackPressed();
            }

        }

    }


  void selectStars(){


    imageView1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             ImageLoader((byte) 1,"Ratings");
             Selected_stars ="1";
         }
    });
    imageView2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             ImageLoader((byte) 2,"Ratings");
            Selected_stars= "2";

        }
    });imageView3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             ImageLoader((byte) 3,"Ratings");
            Selected_stars= "3";

        }
    });
    imageView4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             ImageLoader((byte) 4,"Ratings");
            Selected_stars    ="4";

        }
    });
    imageView5.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ImageLoader((byte) 5,"Ratings");
            Selected_stars  ="5";

        }
    });

 }

void ImageLoader(Byte size,String from) {
    ImageView stars[] = {imageView1, imageView2, imageView3, imageView4, imageView5};
 if(from.contains("Ratings")) {
     for (int i = 0; i < 5; i++) {
         try {
             if (i < size)
            Glide.with(getApplicationContext()).load(R.drawable.full_star).into(stars[i]);
        else Glide.with(getApplicationContext()).load(R.drawable.empty_star).into(stars[i]);


         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 }else {

     ImageView imageViewa = findViewById(R.id.star1);
     ImageView   imageViewb = findViewById(R.id.star2);
     ImageView  imageViewc = findViewById(R.id.star3);
     ImageView  imageViewd = findViewById(R.id.star4);
     ImageView  imageViewe = findViewById(R.id.star5);
     Toast.makeText(food_info.this,"Rating is rating"+(ReceivedRatings+"No is"+ReceivedNoOfRatings),Toast.LENGTH_SHORT).show();
     ImageView stars1[] = {imageViewa, imageViewb, imageViewc, imageViewd, imageViewe};
     //size= (byte) (ReceivedRatings / ReceivedNoOfRatings);
     for (int i = 0; i < 5; i++) {
         try {
             Glide.with(this).load(R.drawable.selectstar).into(stars1[i]);
             if (i < size )
                 Glide.with(this).load(R.drawable.star).into(stars1[i]);

         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 }
}

    public void onClickmake_favorite(View view) {
        if (favstar){
            ImageView i=findViewById(R.id.favstar);
            favstar=false;
            Log.e("Load Listener","Inside click fav "+favstar);

            // TextView textView=findViewById(R.id.total_favs);
           // textView.setText(String.format("%s people loved this food",  Favorite -1));
              Glide.with(getApplicationContext()).load(R.drawable.heart_empty).into(i);
            storefav(false);

        }else{

            ImageView i=findViewById(R.id.favstar);
           // TextView textView=findViewById(R.id.total_favs);
            Log.e("Favorite","Favorite is "+Favorite);
            //textView.setText(String.format("You & %s people loved this food",  Favorite));
            favstar=true;
            Log.e("Load Listener","Inside click fav "+favstar);

            Glide.with(getApplicationContext()).load(R.drawable.heart).into(i);
            storefav(true);
        }

 //   check_fav_text((TextView) findViewById(R.id.total_favs));


    }
    void storefav(boolean check){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().
                child("User Informations/"+mauth.getCurrentUser().getUid()+"/Favorite");


        if (check){
            databaseReference.child(ReceivedName).child("Food_name").setValue(ReceivedName);
            databaseReference.child(ReceivedName).child("Type").setValue(Receivedtype);
            store_favourite(true);

        }else{      databaseReference.child(ReceivedName).setValue(null);
          //  databaseReference.child(ReceivedName).child("Type").setValue(Receivedtype);
                       store_favourite(false);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show, final TextView actionButton) {
        final AppBarLayout cardView = findViewById(R.id.appbar);
        cardView .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
     //   int cx = (int) (actionButton.getX() + (cardView.getWidth() ));
        int cx = (int) (actionButton.getY() + (cardView.getWidth()/2 ));
        int cy = (int) (actionButton.getY()  ) + cardView.getHeight()/2  ;

        if (show) {
            Toolbar toolbar=findViewById(R.id.Toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.testcolorblue));
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();

             actionButton.setVisibility(View.VISIBLE);
             findViewById(R.id.foodname_expand).setVisibility(View.VISIBLE);

         } else {
            anim_Status=false;
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                     findViewById(R.id.foodname ).setVisibility(View.VISIBLE);
                    findViewById(R.id.foodname_expand ).setVisibility(View.GONE);
                     Toolbar toolbar=findViewById(R.id.Toolbar);
                     anim_Status=true;
                 toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                }


            });
            anim.setDuration(700);
            anim.start();
        }

    }

    void store_favourite(final boolean add){

      final DatabaseReference databaseReference=  FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+Receivedtype+"/"+ReceivedName);
           databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

         try {
             if (add){
                 Log.e("Favorite", "Inside try " + dataSnapshot.child("Favorite").getValue());

             databaseReference.child("Favorite").setValue((int) ((long)
                     dataSnapshot.child("Favorite").getValue()) + 1);
         }else{
                 databaseReference.child("Favorite").setValue((int) ((long)
                         dataSnapshot.child("Favorite").getValue()) - 1);
             }
         }catch (Exception e){
             Log.e("Favorite","Inside catch 1 "+e.toString());
             databaseReference.child("Favorite").setValue(1);
         }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

     }

    public void foodname_expand_clicked(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false,(TextView) findViewById(R.id.foodname_expand));
        }else{
            findViewById(R.id.foodname_expand).setVisibility(View.INVISIBLE);
            findViewById(R.id.foodname ).setVisibility(View.VISIBLE);
        }
    }

    public void foodname_clicked(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(true,(TextView) findViewById(R.id.foodname));
        }else{
            findViewById(R.id.foodname).setVisibility(View.INVISIBLE);
            findViewById(R.id.foodname_expand ).setVisibility(View.VISIBLE);
        }
    }

    public void addtocartClicked(View view) {
        TextView cart=findViewById(R.id.addtocart);
        if (cart.getText().toString().trim().toUpperCase().contains("IN THE")||
                cart.getText().toString().trim().toUpperCase().contains("REMOVE")
                ){
            findViewById(R.id.progress_cart).setVisibility(View.VISIBLE);
            foodcart(ReceivedName,Receivedprice,Receivedtype,Receivedimage,false);
        }else {
            findViewById(R.id.progress_cart).setVisibility(View.VISIBLE);
            foodcart(ReceivedName, Receivedprice, Receivedtype, Receivedimage, true);
        }

     }




}

