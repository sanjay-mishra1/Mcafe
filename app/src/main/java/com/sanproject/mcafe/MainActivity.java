package com.sanproject.mcafe;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.carts.cartFragments;
import com.sanproject.mcafe.food.fav_food;
import com.sanproject.mcafe.food.food_fragment;
import com.sanproject.mcafe.food_items.all_food_items;
import com.sanproject.mcafe.food_items.food_type_filter;
import com.sanproject.mcafe.message.help_activity;
import com.sanproject.mcafe.notification.notification_view;
import com.sanproject.mcafe.order.order;
import com.sanproject.mcafe.update.update;
import com.sanproject.mcafe.update.webview;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.sanproject.mcafe.constant.constants.Canteen;
import static com.sanproject.mcafe.constant.constants.Name;
import static com.sanproject.mcafe.food_items.food_type_filter.ADDRESS;
import static com.sanproject.mcafe.food_items.food_type_filter.QUERY;


public class MainActivity extends AppCompatActivity {
    public FragmentManager FM;
     Snackbar mSnakbar;
         public Button foodcart;
    SharedPreferences shared;
      public static   String MYPREFERENCES="MyPrefs";
     private String cartno;
    public   String img="";
    RelativeLayout cartR;
    public   String user_cred="User_Credentials";
      Dialog progressDialog;
      boolean Reload=false;
      String old_address="";
      String old_query="";
      boolean backtomenu=false;
    private View dialogView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        LoadSearch();
        if (Canteen==null||Canteen.isEmpty()){
             Canteen = shared.getString("Canteen", "not found");
        }
        Log.e("Canteen","Canteen oncreate is "+Canteen);
        load_firebase();
        NavigationView navigationView;

        findViewById(R.id.cake_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,cakeActivity.class));
            }
        });
        setColoredDrawable_For_food(R.drawable.all_included,"Show All");
        foodItems();
        FragmentTransaction FT;
        final DrawerLayout drawerLayout;
        setNotificaion();
     try {        img=shared.getString("User_Img", "");


         Glide.with(getApplicationContext()) .load(img).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.profile_img));
     }catch (Exception ignored){}

        setUserCredentials();


        cartR=findViewById(R.id.cartRelative);
        foodcart=findViewById(R.id.foodcart);
        shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
         drawerLayout =  findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.shitstuff);

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        isNetworkAvailable(getApplicationContext());
        cart(uid);
        cake_cart(uid);
        foodcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foodcart.isEnabled()) {
                    foodcart.setEnabled(false);
                    showProgress_dialog();
                    Intent intent = new Intent(MainActivity.this, cartFragments.class);
                    startActivity(intent);
                }
            }
        });

        mSnakbar = Snackbar.make(drawerLayout, "press back again to exit", Snackbar.LENGTH_SHORT);

        FM = getSupportFragmentManager();
        FT = FM.beginTransaction();
          FT.replace(R.id.containerView, new all_food_items()).commit();


        FirebaseAuth.AuthStateListener mAuthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, logIN.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);

        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                 if (item.getItemId() == R.id.Orders) {
                     startActivity(new Intent(MainActivity.this, order.class));
                }

                if (item.getItemId() == R.id.Menu) {
                  try {
                      if (Integer.parseInt(cartno) > 0)
                          cartR.setVisibility(View.VISIBLE);
                       AppBarLayout appBarLayout=findViewById(R.id.appbar);
                      appBarLayout.setExpanded(true,true);
                      findViewById(R.id.cake_activity).setVisibility(View.VISIBLE);
                      findViewById(R.id.fooditems_clicked).setVisibility(View.VISIBLE);
                 //      findViewById(R.id.activity_name).setVisibility(View.GONE);
                      start_Fragment_for_filters(old_query,old_address.toUpperCase());
                   }catch (Exception e){
                      if (Integer.parseInt(cartno) > 0)
                          cartR.setVisibility(View.VISIBLE);
                       AppBarLayout appBarLayout=findViewById(R.id.appbar);
                       appBarLayout.setExpanded(true,true);
                      findViewById(R.id.fooditems_clicked).setVisibility(View.VISIBLE);
                      findViewById(R.id.cake_activity).setVisibility(View.VISIBLE);
                     //  findViewById(R.id.activity_name).setVisibility(View.GONE);
                      start_Fragment_for_filters(old_query,old_address.toUpperCase());
                   }
                }
                if (item.getItemId() == R.id.settings) {
                     startActivity(new Intent(MainActivity.this,account_settings.class));
                }
                if (item.getItemId() == R.id.logout) {

                     shared= getApplicationContext().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor=shared.edit();
                    editor.clear();
                    editor.apply();
                    shared= getApplicationContext().getSharedPreferences( user_cred,MODE_PRIVATE);
                    editor=shared.edit();
                    editor.clear();
                    editor.apply();
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Canteen=null;
                    Intent loginIntent = new Intent(MainActivity.this, logIN.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
                if (item.getItemId() == R.id.feedback) {
                      startActivity(new Intent(MainActivity.this,SentFragment.class));
                }
                if (item.getItemId() == R.id.notification) {

                    startActivity(new Intent(MainActivity.this, notification_view.class));

                }
                if (item.getItemId() == R.id.help) {
                     startActivity(new Intent(MainActivity.this, help_activity.class));
                }

                if (item.getItemId() == R.id.about_app) {
                    Intent intent=new Intent(MainActivity.this, webview.class);
                    intent.putExtra("URL","https://mit-cafe.web.app/aboutus.html");
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.terms_conditions) {
                    Intent intent=new Intent(MainActivity.this,webview.class);
                    intent.putExtra("URL","https://mit-cafe.web.app/terms_conditions.html");
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.privacy_policy) {
                    Intent intent=new Intent(MainActivity.this,webview.class);
                    intent.putExtra("URL","https://mit-cafe.web.app/privacy.html");
                    startActivity(intent);
                }
                return false;
            }
        });





    }
    void LoadSearch(){
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            findViewById(R.id.searchbar).setOnClickListener(new View.OnClickListener() {
////                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
////                @Override
////                public void onClick(View v) {
////                    constants. animation(false,findViewById(R.id.searchbar),  findViewById(R.id.search));
////
////                }
////            });
////            findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
////                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
////                @Override
////                public void onClick(View v) {
////                    constants. ani
/// mation(true,findViewById(R.id.searchbar),  findViewById(R.id.search));
////                }
////            });
//
//        }
    }
    void setUserCredentials(){


         DatabaseReference  database = FirebaseDatabase.getInstance().getReference();
        database.child("User Informations").
                child((FirebaseAuth.getInstance().getCurrentUser().getUid())).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
    NavigationView   navigationView = findViewById(R.id.shitstuff);

                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        try {View header = navigationView.getHeaderView(0);
                            TextView nav_name, nav_imgName, nav_phone;
                            nav_name = header.findViewById(R.id.username_nav);
                            nav_imgName = header.findViewById(R.id.name_image);
                            nav_phone = header.findViewById(R.id.phone_nav);
                            nav_name.setText((String) map.get("Name"));
                            String mobile=(String) map.get("Mobile_number");
                            nav_phone.setText(mobile);

                            nav_phone.setText((String) map.get("Mobile_number"));
                             img=((String) map.get("User_Img"));
                           if (img!=null)
                           {findViewById(R.id.profile_img).setVisibility(View.VISIBLE);
                           nav_imgName.setVisibility(View.INVISIBLE);
                           Glide.with(getApplicationContext()) .load(img).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.profile_img));}
                           else     nav_imgName.setText(Name_for_Image(nav_name.getText().toString().trim()));
                            Log.e("help activity","img is from mainactivity "+img);
                            shared= getApplicationContext().getSharedPreferences( user_cred,MODE_PRIVATE);
                            SharedPreferences.Editor editor=shared.edit();

                            editor.putString("UserImg", img);
                            editor.putString("UserName",(String) map.get("Name"));
                            Name=(String) map.get("Name");
                            editor.putString("MobileNumber",nav_phone.getText().toString().trim());
                              editor.apply();
                         }catch (Exception ignored){}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    private String getFormatedAmount(long amount){
        return "Pay "+getString(R.string.rupeesSymbol)+" "+ NumberFormat.getNumberInstance(Locale.UK).format(amount)+".00";
    }

    void totalamount(DatabaseReference data ){
        final int[] t = new int[1];
        cartR.setVisibility(View.VISIBLE);
         final int[] i = {0};
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    i[0]++;
                    Food food=dataSnapshot1.getValue(Food.class);

                    try {
                        t[0] = Integer.parseInt(String.valueOf(food.getPrice()))*Integer.parseInt(food.getQuantity()) + t[0];
                        TextView amount=findViewById(R.id.amount);


                        amount.setText(getFormatedAmount((long)(t[0])));

                    }catch (Exception ignored){} //   Toast.makeText(MainActivity.this, i[0]+" Price is " + food.getPrice(), Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    void LoadForNoNetwork(){
      if (!isNetworkAvailable(getApplicationContext())){
          findViewById(R.id.No_network).setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).asGif().load(R.drawable.no_internet).into((ImageView) findViewById(R.id.gifimage));
      }else{
          findViewById(R.id.No_network).setVisibility(View.GONE);

      }
    }
    void system_notification(DataSnapshot dataSnapshot){

        int priority;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            priority=NotificationManager.IMPORTANCE_HIGH;
        }else
            priority=PRIORITY_HIGH;
        String CHANNEL_ID="101";
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "Mcafe";
            String description =  "Mcafe" ;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
            Intent intent;// = new Intent(this, help_activity.class);
            String from=(String)dataSnapshot.child("From").getValue();
        if (from != null ) {
            if (from.equals("Message")) {
                intent = new Intent(this, help_activity.class);
                intent.putExtra("From", "Notification");

            } else {
                intent = new Intent(this, editOrder.class);
                String message = from;
                message = message.substring(message.indexOf("(") + 1, message.indexOf(")"));
                Log.e("Notification check", message);
                intent.putExtra("OrderNo", message);
                intent.putExtra("From", "Orders");

            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (from.contains("Message")) {
                String txt=(String)dataSnapshot.child("Message").getValue();
                txt= txt != null ? txt.substring(0, txt.indexOf("Time")) : "";
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                         .setSmallIcon(R.drawable.chat)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon_round))
                        .setContentTitle("Mcafe")
                        .setContentText("New Message received")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(txt))
                        .setPriority(priority)
                        .setContentIntent(pendingIntent)
                        .setColor(getResources().getColor(R.color.app_blue))
                        .setDefaults(DEFAULT_SOUND|DEFAULT_VIBRATE)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                 notificationManager.notify(9425, mBuilder.build());
            } else {
                String message = (String) ((String) dataSnapshot.child("Message").getValue());
                if (message != null) {
                  try {
                      message = message.substring(message.lastIndexOf("You"), message.lastIndexOf("Time"));
                  }catch (Exception e){

                  }

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    priority=NotificationManager.IMPORTANCE_HIGH;
                }else  priority=PRIORITY_HIGH;
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.cart_img)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon_round))
                        .setContentTitle("Order no " + from.substring(from.indexOf("(") + 1, from.indexOf(")")))
                        .setContentText((String) dataSnapshot.child("Message").getValue())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(priority)
                        .setContentIntent(pendingIntent)
                        .setColor(getResources().getColor(R.color.app_blue))
                        .setDefaults(DEFAULT_SOUND|DEFAULT_VIBRATE)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(9425, mBuilder.build());

            }
        }
    }
    void snakbar( ){
        try {
            FirebaseDatabase.getInstance().getReference("User Informations/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Notifications")
                    .orderByChild("Status").startAt("UNSEEN").limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    {  Log.e("Notification check","inside child added sending to notification");
                        system_notification(dataSnapshot);}
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.e("Notification check","inside child changed sending to notification");
                    system_notification(dataSnapshot);

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
        }catch (Exception ignored){}

    }
    void cart(String uid){
        final DatabaseReference  databaseReference=  FirebaseDatabase.getInstance().getReference().
                child("User Informations").
                child(uid).child("FoodCart/Food");
        databaseReference.
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {     // TextView cart=findViewById(R.id.cart);
                            Food food=dataSnapshot.getValue(Food.class);
                            cartno=Long.toString ((int) dataSnapshot.getChildrenCount());
                            //cart.setText(cartno);
                            if (Integer.parseInt(cartno)>0)
                            {
                                cartR.setVisibility(View.VISIBLE);
                                TextView fooditems=findViewById(R.id.totalfood);
                                fooditems.setText(cartno);
                                foodcart.setText(" items in the FoodCart");
                                totalamount(databaseReference);
                            }
                          //  else  cartR.setVisibility(View.GONE);
                            int price=0;
                            TextView amount=findViewById(R.id.amount);

                            for (int i=0;i<Integer.parseInt(cartno);i++){
                                //price= Integer.parseInt(price)+food.getPrice();
                                price=  price + food.getPrice();
                            }
                            amount.setText(Integer.toString(price));


                        }catch (Exception NullPointerException){}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    void cake_cart(String uid){

        final DatabaseReference  databaseReference=  FirebaseDatabase.getInstance().getReference().child("User Informations").
                child(( uid)).child("FoodCart/Cake");
        databaseReference.
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {     // TextView cart=findViewById(R.id.cart);
                            Food food=dataSnapshot.getValue(Food.class);
                            cartno=Long.toString ((int) dataSnapshot.getChildrenCount());
                            //cart.setText(cartno);
                            if (Integer.parseInt(cartno)>0)
                            {
                                cartR.setVisibility(View.VISIBLE);
                                TextView fooditems=findViewById(R.id.totalfood);
                                fooditems.setText(cartno);
                                foodcart.setText(" items in the CakeCart");
                                totalamount(databaseReference);
                            }
                          //  else  cartR.setVisibility(View.GONE);
                            int price=0;
                            TextView amount=findViewById(R.id.amount);

                            for (int i=0;i<Integer.parseInt(cartno);i++){
                                //price= Integer.parseInt(price)+food.getPrice();
                                price=  price + food.getPrice();
                            }
                            amount.setText(Integer.toString(price));


                        }catch (Exception NullPointerException){}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private CharSequence Name_for_Image(String s1) {
        int j = 0, flag = 0, loc2 = 0;
        String Name = "";
        try {

            char[] ch = s1.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] != ' ' && flag == 0) {
                    flag = 1;
                    j = i;
                }
            }
            String name23;
            Name = s1.substring(j, s1.length());
            loc2 = Name.indexOf(" ");
            if (j == -1)
                j = 1;
            name23 = Name.substring(j, loc2);
            Name = Name.replace(name23, "");
            int j2 = 0;
            char[] ch1 = Name.toCharArray();
             int k = 0;
            while (k < Name.length()) {
                if (ch1[k] == ' ') {

                    j2 = k;
                }
                k++;
            }
            String second;

            if (j2 == Name.length() - 1)
                second = (Character.toString(ch[j + 1]));
            else second = (Character.toString(ch1[j2 + 1]));

            Name = (Character.toString(ch[j])) + second;
            Name = Name.toUpperCase();

            System.out.println("After loop ch1= -" + "-" + " Name=" + Name);


            // System.out.println("After loop ch1= -"+ch1[j2+1]+"-"+" Name="+Name );
        } catch (Exception NullPointerException) {
        }
        return check_credentials(Name);
    }
    String check_credentials(String name){
        if (name.length()>2){
            char[] ch = name.toCharArray();
            name=Character.toString(ch[0])+Character.toString(ch[1]);
         }
         return name.toUpperCase();
    }
    void automatic_auth(final String email, final String password){
       FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("Email", email);
                    editor.putString("Password", password);
                     editor.apply();

                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        SharedPreferences.Editor editor=shared.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, logIN.class));
                        finish();
                    }else
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        try {
            progressDialog.dismiss();
        }catch (Exception e){e.printStackTrace();}
      //  cart();
        TextView textView=findViewById(R.id.amount);
        if (textView.getText().toString().trim().equals("0"))
        cartR.setVisibility(View.GONE);
        //cake_cart();
     //   cart();
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setVisibility(View.VISIBLE);
        AppBarLayout appBarLayout=findViewById(R.id.appbar);
        appBarLayout.setExpanded(true,true);
//        SharedPreferences.Editor editor=shared.edit();
        foodcart.setEnabled(true);
        findViewById(R.id.fooditems_clicked).setEnabled(true);
       // editor.clear();
        Log.e("Main activity state","start");

            setUserCredentials();


    shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String email = shared.getString("Email", "");
        String password = shared.getString("Password", "");
        Canteen = shared.getString("Canteen", "");
       try {
           shared=this.getSharedPreferences(user_cred, Context.MODE_PRIVATE);
           Glide.with(getApplicationContext()) .load(shared.getString("User_Img", "")).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.profile_img));
       }catch (Exception ignored){}


        if (!email.isEmpty()) {
            automatic_auth(email,password);
        }
        //snakbar();

    }

    private static boolean isNetworkAvailable(Context con){
        try{
            ConnectivityManager cm=(ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo networkInfo=cm.getActiveNetworkInfo();
            if (networkInfo!=null&& networkInfo.isConnected()){

                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void onBackPressed(){
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){e.printStackTrace();}
        if (backtomenu){
            try {
                if (Integer.parseInt(cartno) > 0)
                    cartR.setVisibility(View.VISIBLE);
                 findViewById(R.id.cake_activity).setVisibility(View.VISIBLE);
                findViewById(R.id.fooditems_clicked).setVisibility(View.VISIBLE);
                backtomenu=false;
              //  findViewById(R.id.activity_name).setVisibility(View.GONE);
                start_Fragment_for_filters(old_query,old_address.toUpperCase());
                //FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
                //fragmentTransaction1.replace(R.id.containerView, new all_food_items()).commit();
            }catch (Exception e){
                 if (Integer.parseInt(cartno) > 0)
                    cartR.setVisibility(View.VISIBLE);
                findViewById(R.id.fooditems_clicked).setVisibility(View.VISIBLE);
                findViewById(R.id.cake_activity).setVisibility(View.VISIBLE);
                backtomenu=false;
              //  findViewById(R.id.activity_name).setVisibility(View.GONE);
                start_Fragment_for_filters(old_query,old_address.toUpperCase());

                // FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
               // fragmentTransaction1.replace(R.id.containerView, new all_food_items()).commit();
            }
        }else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (mSnakbar.isShown()) {
                super.onBackPressed();

            } else {
                mSnakbar.show();
            }
        }
    }

     public void setColoredDrawable_For_food( int drawable,String type){
        ImageView imageView=findViewById(R.id.food_icon);
         Glide.with(getApplicationContext()).load(drawable).into(imageView);
       // imageView.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white));
         // TextView textView=findViewById(R.id.name);
     // textView.setText(type);

    }
      void start_Fragment_for_filters(String query,String Address){
     try {
         Log.e("Main activity state", "inside start fragment");
         if (Address.contains("SPECIAL")) {
             Bundle bundle = new Bundle();
             bundle.putString("Address", "Food Items/"+Canteen+"/Special");
             bundle.putString("Query", query);
             food_special food = new food_special();
             food.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, food).commit();
             setColoredDrawable_For_food(R.drawable.cake, "Special");

         } else if (Address.contains("SNAKS")) {
             Bundle bundle = new Bundle();
             bundle.putString("Address", "Food Items/"+Canteen+"/Snaks");
             bundle.putString("Query", query);
             food_fragment food = new food_fragment();
             food.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, food).commit();
             setColoredDrawable_For_food(R.drawable.snaks, "Snacks");

         } else if (Address.contains("MEAL")) {
             Bundle bundle = new Bundle();
             bundle.putString("Address", "Food Items/"+Canteen+"/Meal");
             bundle.putString("Query", query);
             food_fragment food = new food_fragment();
             food.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, food).commit();
             setColoredDrawable_For_food(R.drawable.meal, "Meal");

         } else if (Address.contains("BEVERAGES")) {
             Bundle bundle = new Bundle();
             bundle.putString("Address", "Food Items/"+Canteen+"/Beverages");
             bundle.putString("Query", query);
             food_fragment food = new food_fragment();
             food.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, food).commit();
             setColoredDrawable_For_food(R.drawable.tea, "Beverages");
         } else if (Address.contains("SHOW")||Address.isEmpty()) {
             Bundle bundle = new Bundle();
             bundle.putString("Query", query);
             all_food_items food = new all_food_items();
             food.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
             fragmentTransaction1.replace(R.id.containerView, food).commit();
             setColoredDrawable_For_food(R.drawable.all_included, "Show All");

         } else if (Address.contains("FAVORITE")) {
              Bundle bundle = new Bundle();
              bundle.putString("Query", query);
              fav_food food = new fav_food();
              food.setArguments(bundle);
              FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
              fragmentTransaction1.replace(R.id.containerView, food).commit();
              setColoredDrawable_For_food(R.drawable.heart_white, "Favorites");
          } else {
             Log.e("Main activity state", "inside start fragment nothing found address" + Address + " query " + query);

         }
     }catch (Exception ignored){

     }
    }

  void foodItems(){

  findViewById(R.id.fooditems_clicked)
       .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              //showDiag();
              findViewById(R.id.fooditems_clicked).setEnabled(false);
              Reload=true;
               startActivity(new Intent(MainActivity.this, food_type_filter.class));
          }
      });


  }
void showProgress_dialog(){
     if (dialogView==null) {
         dialogView = View.inflate(MainActivity.this, R.layout.progress_layout, null);
         progressDialog = new Dialog(MainActivity.this);
         progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         progressDialog.setContentView(dialogView);
         progressDialog.setCanceledOnTouchOutside(false);

         progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
             @Override
             public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                 if (i == KeyEvent.KEYCODE_BACK) {

                     return true;
                 }

                 return false;
             }
         });


         progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         progressDialog.show();
     }else progressDialog.show();
}
    @Override
    protected void onStop() {
        super.onStop();
        try {
            progressDialog.dismiss();
        }catch (Exception ignored){}
        snakbar();
        Log.e("Main activity state","Stop");
    }

    boolean send_Change=true;

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            progressDialog.dismiss();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            progressDialog.dismiss();
        }catch (Exception e){e.printStackTrace();}
        findViewById(R.id.fooditems_clicked).setEnabled(true);
         {

            send_Change = true;
            if (ADDRESS != null && !ADDRESS.equals(old_address)) {
                if (!ADDRESS.equals("") || !ADDRESS.isEmpty()) {
                    Log.e("Main activity state", "Address is not null sending to start fragment");
                    if (Reload) {
                        if (!ADDRESS.equals(old_address)) {
                            old_address = ADDRESS;
                            QUERY = "";
                            start_Fragment_for_filters("", old_address.toUpperCase());
                            //  ADDRESS = null;
                        }
                        Reload = false;
                    }
                } else {
                    Log.e("Main activity state", "Address is   null not sending to start fragment");
                }

            }
            else if (QUERY != null) {
                if (!QUERY.equals("") || !QUERY.isEmpty()) {
                    Log.e("Main activity state", "Query is not null sending to start fragment");
                    if (Reload) {
                        Log.e("Main activity state", "Query is not null and reload is true sending to start fragment");
                        if (!QUERY.equals(old_query)) {
                            Log.e("Main activity state", "Query is not null and not equal to old query to start fragment");
                            old_query = QUERY;
                            start_Fragment_for_filters(old_query, old_address.toUpperCase());
                        }
                        Reload = false;
                    } else {

                        old_query = QUERY;
                        //here start_Fragment_for_filters(" ", old_address.toUpperCase());
                    }
                } else {
                    Log.e("Main activity state", "Query is null not sending to start fragment");
                }

                Log.e("Main activity state", "Resume1 Reload is " + Reload);
                Log.e("Main activity state", "Resume2 Query is " + QUERY);

            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       //  ADDRESS=null;
        // QUERY=null;
    }
    void setNotificaion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(!isDestroyed()) {
                final Query query = FirebaseDatabase.getInstance().getReference("User Informations/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Notifications")
                        .orderByChild("Status").startAt("UNSEEN").limitToLast(1);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.e("notifications", "Inside child changed " + s);
                        if (send_Change) {
                            send_Change = false;
                            //  startActivity(new Intent(MainActivity.this, notification_dialog.class));
                            Notificaion_dialog();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.e("notifications", "Inside child changed " + s);

                        if (send_Change) {
                            send_Change = false;
                            Notificaion_dialog();
                            // startActivity(new Intent(MainActivity.this, notification_dialog.class));
                        }
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
            }else{
                Log.e("Notification check","sending to snakbar");
               // snakbar();
            }
        }
    }
    void Notificaion_dialog(){

            final View dialogView = View.inflate(MainActivity.this, R.layout.notification_big, null);
         // Context context=getp
         final Dialog   notifications = new Dialog(MainActivity.this);
        notifications.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notifications.setContentView(dialogView);

        notifications.setCanceledOnTouchOutside(true);

        notifications.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        notifications.dismiss();
                        return true;
                    }

                    return false;
                }
            });

        notifications.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                change_seen();
            }
        });
        notifications.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!isDestroyed())
            notifications.show();
        }
        firebase(dialogView,notifications);
        }

    void check_firebase_notification(final Dialog dialog){
        Query query= FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications")
                .orderByChild("Status") .startAt("UNSEEN");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void firebase(final View view, final Dialog dialog){
        RecyclerView recyclerView=view.findViewById(R.id.notification_recycler);
        LinearLayoutManager horizontal=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(horizontal);
        AlbumsAdapter adapter=new AlbumsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        check_firebase_notification(dialog);
        Query query= FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications")
                .orderByChild("Status") .startAt("UNSEEN");
        FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder> Fbra=new FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder>(
                album_allorders.class,
                R.layout.notification_small,
                mainactiv.FoodViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, album_allorders model, int position) {
              //  viewHolder.setTime(String.valueOf(model.getServer_Time()));
              if (model.getMessage()==null){
                  dialog.dismiss();
              }
              viewHolder.set_time_from_firebase(model.getServer_Time());
                viewHolder.setMessage_notification(model.getMessage());
                viewHolder.setDesc(model.getFrom());
                TextView totalchild=view.findViewById(R.id.total_notifications);
                totalchild.setText(String.format("%d %s", getItemCount(), getString(R.string.total_new_notifications)));


            }
        };

        recyclerView.setAdapter(Fbra);
    }



    void change_seen(){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications");
        databaseReference.orderByChild("Status").startAt("UNSEEN").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        try {
                            databaseReference.child((String) Objects.requireNonNull(dataSnapshot1.child("key").getValue())).child("Status").setValue("SEEN");
                            Log.e("Notification_dialog","Key is "+dataSnapshot1.child("key").getValue());
                        }catch (Exception e){
                            Log.e("Notification_dialog","inside catch Key not found ");//+dataSnapshot1.child("key").child("Status").getValue());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Notification_dialog","inside oncancelled Key not found "+databaseError.toString());
            }
        });
    }
    void load_firebase(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Extras/App");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Integer.parseInt((String)dataSnapshot.child("App_New_Version").getValue())> Integer.parseInt( getString(R.string.app_version))){

                        Intent intent=new Intent(MainActivity.this, update.class);
                        intent.putExtra("Link",(String)dataSnapshot.child("UPDATE_LINK").getValue());
                        intent.putExtra("Force",(String)dataSnapshot.child("Force_Update").getValue());
                        startActivity(intent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



