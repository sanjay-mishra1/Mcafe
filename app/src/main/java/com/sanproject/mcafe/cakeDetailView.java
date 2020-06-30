package com.sanproject.mcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanproject.mcafe.Cart.Cart;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static com.sanproject.mcafe.constant.constants.Canteen;


public class cakeDetailView extends AppCompatActivity {
    RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private String CakeName;
    private String CakePrice;
    private String bloopers = "";
    private String spray = "";
    private String egg = "Without Egg";
    public int currPosition;
    private PopupWindow popupWindow;
    private static String  weight = "Half Kg";
    private PopupWindow popupWindow1;
    private String Times;
    private String popup;
    private String day;
    private String originalName;
    private String cakeImage;
    private byte Today;
    private String shape = "";
    private String Extras = "Includes";
    private String simple = "";
    private GestureDetector gestureDetector = null;
    public int scrollPostion;
    private int total;
    public String check="main";
    private RecyclerView recyclerViewTop;
    private ArrayList<String> imageUrls;
    View cakePop;
    private Byte Firebasestatus=6;
    private Byte writeSucessful=0;
    private String spark="";
    public int back=0;
    public static String Cakeshape;
    public static String flavour;
   public static Display display;
   private ArrayList<String> cake_extrasName;
   private ArrayList<String> cake_extrasPrice;
   private ArrayList<String> cake_extrasImage;
   private ArrayList<String> cake_extrasQuantity;
     private String Selected_stars="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_viewofthe_cake);

        cake_extrasName =new ArrayList<>();
        cake_extrasPrice=new ArrayList<>();
        cake_extrasImage=new ArrayList<>();
        cake_extrasQuantity=new ArrayList<>();
          imageUrls=new ArrayList< >();
        recyclerView = findViewById(R.id.cakeRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(cakeDetailView.this, LinearLayoutManager.HORIZONTAL, false);
        TextView toolbar = findViewById(R.id.toolbar);
      //  ImageView imgtoBlue = findViewById(R.id.pointer1);
       // imgtoBlue.getDrawable().setColorFilter(Color.parseColor("#ff0092f4"), PorterDuff.Mode.SRC_IN);
         recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        morecake();
        Receive();
        toolbar.setText(originalName);
        setters();
        scrollPostion = 0;
        TotalPrice();
        gestureDetector = new GestureDetector(new MyGestureDetector());
        Tablayout();
           display = getWindowManager().getDefaultDisplay();
          LinearLayout.LayoutParams params;

             findTotal();
           params = new LinearLayout.LayoutParams( display.getWidth()/2, ViewGroup.LayoutParams.WRAP_CONTENT );
           findViewById(R.id.tocart).setLayoutParams(params);
        setcakeName();
        loadCakeImages();
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }
                return false;
            }
        });
    selectStars();
    }
    String replaceSpaces(String text){
        char str[]=text.toCharArray();
        for (char str1:str){
            if (str1==' '){
                text=text+"-";
            }else text=text+str1;
         }
     return  text;
    }
    void Receive() {
        Intent intent = getIntent();
        try {
            CakeName = intent.getStringExtra("CakeName");
            originalName = CakeName;
            CakePrice = intent.getStringExtra("CakePrice");
            cakeImage = intent.getStringExtra("CakeImage");
            flavour = intent.getStringExtra("Flavour");
            Cakeshape = intent.getStringExtra("Shape");
            setrating(intent.getStringExtra("Ratingsum"),intent.getStringExtra("Ratingno"));
            calculate_percentage(intent.getStringExtra("CakePrice"),intent.getStringExtra("d_gm500"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));
            Log.e("Cake detail view","Shape "+Cakeshape+" Flavour"+flavour);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setrating(String sum_of_ratings, String total_noOfTime_rated) {
        try {
            if ( !total_noOfTime_rated.equals("0")) {
                TextView textView =  findViewById(R.id.rating_star);
                textView.setText(String.format("%.1f",
                Float.parseFloat(sum_of_ratings) / Float.parseFloat(total_noOfTime_rated)));


            } else {
                findViewById(R.id.rating_star).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
             findViewById(R.id.rating_star).setVisibility(View.INVISIBLE);

        }
    }
void loadCakeImages(){
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+"Cake/" + originalName + "/Images");

    FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
            Food.class,
            R.layout.cake_images,
            mainactiv.FoodViewHolder.class,
            databaseReference
    ) {
        @Override
        protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, Food model, int position) {
            viewHolder.setImage2(getApplicationContext(),model.getFood_Image());
            // imagePointer(position);
            RelativeLayout.LayoutParams params;


            params = new RelativeLayout.LayoutParams( display.getWidth(),display.getWidth() );
            viewHolder.getImageId().setLayoutParams(params);
        }
    };
    recyclerView.setAdapter(firebaseRecyclerAdapter);
}
    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.cardview).setEnabled(true);
        findViewById(R.id.cardview2).setEnabled(true);
        findViewById(R.id.checkout).setEnabled(true);
        recyclerView.smoothScrollToPosition(0);
         currPosition = 0;
         imagePointer(0);
        changecolorof_ImagePointer(0, 0, "All");

    }
   void imagePointer(int position){
       int idspointer[] = {R.id.pointer1_blue, R.id.pointer2_blue, R.id.pointer3_blue, R.id.pointer4_blue, R.id.pointer5_blue, R.id.pointer6_blue, R.id.pointer7_blue, R.id.pointer8_blue, R.id.pointer9_blue, R.id.pointer10_blue,R.id.pointer11_blue};
      try {          findViewById(idspointer[position+1]).setVisibility(View.GONE);
            Log.e("ImagePointer","Position+1 is "+(position+1));
          findViewById(idspointer[position]).setVisibility(View.VISIBLE);
          findViewById(idspointer[position-1]).setVisibility(View.GONE);

      } catch (Exception e){
          Log.e("ImagePointer","Error Position is "+(position)+" Error is ");

      }
   }
    void setters() {
        TextView textview = findViewById(R.id.CakePrice);
        textview.setText(String.format("₹ %s", spacing(CakePrice)));
        TextView Name = findViewById(R.id.cakename);
        Name.setText(String.format("%s Half Kg", CakeName));

    }

    String spacing(String price) {
        try{
        char[] spaced = price.toCharArray();
            StringBuilder priceBuilder = new StringBuilder();
            for (char aSpaced : spaced) {
            priceBuilder.append(aSpaced).append(" ");
        }
            price = priceBuilder.toString();

        }catch (Exception NullPointerException){return "0";
        }
        return price;}
    int getDiscount(float price,float originalPrice){
        return (int) (((originalPrice-price)/originalPrice)*100);
    }
    int Removespacing(String price) {

        char[] spaced = price.toCharArray();
        price = "";
        for (char aSpaced : spaced) {
            if (aSpaced != ' '  ) {
                if ( aSpaced !='₹')
                 price = price + aSpaced;
            }

        }
        return Integer.parseInt(price);
    }
    void TotalPrice( ){
        int sh;
        TextView price=findViewById(R.id.CakePrice);
        TextView Total=findViewById(R.id.checkout);

        if  (!shape.matches(".*\\d+.*")){
               sh=0;
        }else if (shape.equals("0"))
        { sh=0;
        } else sh=40;
        int Total_price=0;
       try {
           for (int i=0;i<cake_extrasQuantity.size();i++){
               Total_price=Total_price+Integer.parseInt(cake_extrasPrice.get(i))*Integer.parseInt(cake_extrasQuantity.get(i));
               Log.e("Cake Total_Price","Total_price "+Total_price+" Price"+cake_extrasPrice.get(i)+" quantity "+cake_extrasQuantity.get(i));
           }
       }catch (Exception e){
         //  Total_price=0;
           Log.e("Cake Total_Price","Exception "+e.toString());
       }
        Total_price+=  Removespacing(price.getText().toString().trim());
        Log.e("Cake Total_Price","Total_price after loop and added to cake price is "+Total_price );
        Total.setText(String.format("CHECKOUT ( ₹ %s)",String.valueOf(Total_price)));
      /* Total.setText(String.format("CHECKOUT ( ₹ %s)", Integer.toString(Removespacing(price.getText().toString().trim()) +
               sh +
               ExtractNumber(simple) * 5 +
               ExtractNumber(bloopers) * 60 +
               ExtractNumber(spray) * 100+
               ExtractNumber(spark) *100
       )));*/
    }
    private int ExtractNumber(String text){
        String number=text.replaceAll("[^0-9]","");
        if (number.equals(""))
            number="0";
        return Integer.parseInt(number);
    }
    private void setExtras(String Shape, String Simple, String Bloopers, String Spray,String Spark) {
        if (!Shape.matches(".*\\d+.*") || ExtractNumber(Shape)==0){
            Shape="";
        }
        if (!Simple.matches(".*\\d+.*")|| ExtractNumber(Simple)==0){
            Simple="";
        }
        if (!Bloopers.matches(".*\\d+.*")|| ExtractNumber(Bloopers)==0){
            Bloopers="";
        }
        if (!Spray.matches(".*\\d+.*")|| ExtractNumber(Spray)==0){
            Log.e("Spray","Spray does not have value "+Spray);
            spray="";
            Log.e("Spray","Spray does not have value "+Spray);
        }
        if (!Spark.matches(".*\\d+.*")|| ExtractNumber(Spark)==0){
            Spark="";
        }
        Log.e("Spray","Spray   have value "+spray);

        Extras = "Includes" + Shape + Simple + Bloopers + spray+Spark;
        Log.e("Spray","Extras is  "+Extras);

        setcakeName();
    }

    void setcakeName() {
        String cake;

        if (Extras.equals("Includes")) {
            cake = originalName+" "+ weight+" " + egg;
        } else cake = originalName +" "+ weight +" "+ egg + " " + Extras;

        TextView textView = findViewById(R.id.cakename);
        textView.setText(cake);
    }

    public void onclickWithoutegg(View view) {
        egg = "Without Egg";
        setcakeName();
    }

    public void onclickWithegg(View view) {
        egg = "With Egg";
        setcakeName();
    }



    public void onclickWeight(View view) {
        back=1;
        view.setEnabled(false);
         popupWeight();
         TotalPrice();

    }

    public String displayMonth(String time) {

        return getThreeInitials(getMonthName(Integer.
                parseInt(time.substring(time.indexOf("/") + 1, time.length() - 4)) - 1)).toUpperCase();
    }

    public String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public String getThreeInitials(String month) {
        return month.substring(0, 3);
    }


    public void deliveryFormat() {
        findViewById(R.id.deliveryDisplay).setVisibility(View.GONE);

        findViewById(R.id.RelativeDelivery).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.month);
        try {
            textView.setText(String.format("%s\n%s", displayMonth(Times),

                    getThreeInitials(dayName(Times.substring(Times.indexOf("(") + 1, Times.length() - 1), "dd/MM/yy")).toUpperCase()));
        } catch (Exception NullPointerException) {
        }
        textView = findViewById(R.id.date);
        try {
            textView.setText(Times.substring(Times.indexOf("(") + 1, Times.indexOf("/")));
            textView = findViewById(R.id.expecteddelivery);
            textView.setText(String.format("%s%s", getString(R.string.earliest_delivery),
                    Times.substring(0, Times.indexOf("(") - 1)));
            textView = findViewById(R.id.deliveryToday);
            if (Today == 1) {
                textView.setText("Today");
            } else textView.setVisibility(View.GONE);
        } catch (Exception ignored) {
        }

    }

    public void onClickDelivery(View view) {
        back=1;
        view.setEnabled(false);
         newDeliverypopup();

    }
    void calculate_percentage(String price,String originalprice ,TextView originalPriceText,TextView discountText,View relativeLayout){
        {
            try {
                if (price.isEmpty() || price.equals("0")) {
                    relativeLayout.setVisibility(View.GONE);
                }


                Log.e("Debug discount in cake ", "Inside calculate price   OPrice" + originalprice + " price" + price);
                try {
                    if (!originalprice.isEmpty()) {
                        discountText.setText(String.format("%d%% OFF", getDiscount(Float.parseFloat(price), Float.parseFloat(originalprice))));
                        originalPriceText.setText(getString(R.string.rupeesSymbol) + originalprice);
                        originalPriceText.setPaintFlags(originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        Log.e("Debug discount in cake ", "Inside original price not empty OPrice" + originalprice + " price" + price);

                    } else {
                        Log.e("Debug discount in cake ", "Inside else original price   empty OPrice" + originalprice + " price" + price);
                        originalPriceText.setVisibility(View.INVISIBLE);
                        discountText.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    originalPriceText.setVisibility(View.INVISIBLE);
                    discountText.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                relativeLayout.setVisibility(View.GONE);
            }

        }
    }

    private void popupWeight() {
        LayoutInflater layoutInflater = (LayoutInflater) cakeDetailView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View custom = layoutInflater.inflate(R.layout.weight_popup, null);
        RelativeLayout relativeLayout = findViewById(R.id.relative);
        popupWindow = new PopupWindow(custom, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.cardview).setEnabled(true);
            }
        });
        //  Picasso.get().load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage1));
         Glide.with(custom).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage1));
      //  Picasso.get().load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage2));
      Glide.with(custom).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage2));
     //   Picasso.get().load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage3));
     Glide.with(custom).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(cakeImage).into((ImageView) custom.findViewById(R.id.cakeImage3));
        TextView textView ;
        final Intent intent=getIntent();
        try{
            textView = custom.findViewById(R.id.cakeprice);

            textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), intent.getStringExtra("gm200")));
            textView = custom.findViewById(R.id.cakeprice2);
            textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), CakePrice));
            textView = custom.findViewById(R.id.cakeprice3);
            textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), intent.getStringExtra("gm1000")));
            Log.e("Debug discount in cake ","200gm "+intent.getStringExtra("d_gm200")+" 500gm  "+intent.getStringExtra("d_gm500")+" 1000gm "+intent.getStringExtra("d_gm1000"));
            calculate_percentage( intent.getStringExtra("gm200"),intent.getStringExtra("d_gm200"),
                    (TextView)    custom.findViewById(R.id.cakeoriginalPrice),
                    (TextView)custom.findViewById(R.id.cakediscount),(RelativeLayout) custom.findViewById(R.id.relative1));
            calculate_percentage( CakePrice,intent.getStringExtra("d_gm500"),
                    (TextView)    custom.findViewById(R.id.cakeoriginalPrice2),
                    (TextView)custom.findViewById(R.id.cakediscount2),(RelativeLayout) custom.findViewById(R.id.relative2));
            calculate_percentage( intent.getStringExtra("gm1000"),intent.getStringExtra("d_gm1000"),
                    (TextView)    custom.findViewById(R.id.cakeoriginalPrice3),
                    (TextView)custom.findViewById(R.id.cakediscount3),(RelativeLayout) custom.findViewById(R.id.relative3));
            Log.e("Debug discount in cake ","200gm "+intent.getStringExtra("d_gm200")+" 500gm  "+intent.getStringExtra("d_gm500")+" 1000gm "+intent.getStringExtra("d_gm1000"));

        }catch (Exception e){e.printStackTrace();
            Log.e("Debug discount in cake "," Error inside exception " +e.toString());

        }
        textView = custom.findViewById(R.id.cakeName);
        textView.setText(String.format("%s small (200gm) ", originalName));
        textView = custom.findViewById(R.id.cakeName2);
        textView.setText(String.format("%s Half kg ", originalName));
        textView = custom.findViewById(R.id.cakeName3);
        textView.setText(String.format("%s One kg ", originalName));

        custom.findViewById(R.id.backpopupweight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back=0;
                 popupWindow.dismiss();
            }
        });
        custom.findViewById(R.id.relative1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "small(200gm)";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText(R.string.gm200);
                setcakeName();
                back=0;
                calculate_percentage(intent.getStringExtra("gm200"),intent.getStringExtra("d_gm200"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));
                Tablayout();
                popupWindow.dismiss();
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
        custom.findViewById(R.id.relative2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "Half kg";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText("1/2 Kg");
                setcakeName();
                back=0;
                Tablayout();
                calculate_percentage(intent.getStringExtra("CakePrice"),intent.getStringExtra("d_gm500"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));                popupWindow.dismiss();
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice2) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
        custom.findViewById(R.id.relative3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "one kg";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText(R.string.onekg);
                setcakeName();
                back=0;
                Tablayout();

                 popupWindow.dismiss();
                calculate_percentage(intent.getStringExtra("gm1000"),intent.getStringExtra("d_gm1000"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice3) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
        custom.findViewById(R.id.gm200Popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "small(200gm)";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText(R.string.gm200);
                setcakeName();
                back=0;
                calculate_percentage(intent.getStringExtra("gm200"),intent.getStringExtra("d_gm200"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));
                Tablayout();
                popupWindow.dismiss();
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
        custom.findViewById(R.id.gm500popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "Half kg";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText("1/2 Kg");
                setcakeName();
                back=0;
                Tablayout();
                calculate_percentage(intent.getStringExtra("CakePrice"),intent.getStringExtra("d_gm500"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));                popupWindow.dismiss();
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice2) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
        custom.findViewById(R.id.gm1000Popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = "one kg";
                TextView textView1 = findViewById(R.id.weight);
                textView1.setText(R.string.onekg);
                setcakeName();
                back=0;
                Tablayout();

                popupWindow.dismiss();
                calculate_percentage(intent.getStringExtra("gm1000"),intent.getStringExtra("d_gm1000"),(TextView)findViewById(R.id.cakeoriginalPrice),(TextView) findViewById(R.id.cakediscount),findViewById(R.id.prices));
                textView1=findViewById(R.id.CakePrice);
                TextView textView2=custom.findViewById(R.id.cakeprice3) ;
                textView1.setText(String.format("%s", spacing( textView2.getText().toString().trim())));
                TotalPrice();
            }
        });
    }
    private boolean CheckVisibility(RelativeLayout relativeLayout){
        if (relativeLayout.getVisibility()==View.VISIBLE)
        {  relativeLayout.setVisibility(View.GONE);

            return false;
         }
        else {relativeLayout.setVisibility(View.VISIBLE);
        return true;
        }

    }
    private String ExtractDeliveryTime(String id, final View radioview, String date, String nextdate, String from, String MIN){

     String radiobuttonId= id.substring(id.indexOf("id/")+3,id.length()-1);
         Log.d("RadioGroupId", id.substring(id.indexOf("id/")+3,id.indexOf("_from")));
          RadioButton radioButton=radioview.findViewById(this.getResources().getIdentifier( radiobuttonId,"id",this.getPackageName()));
         RadioGroup radioGroup=radioview.findViewById(this.getResources().getIdentifier(
                 id.substring(id.indexOf("id/")+3,id.indexOf("_from")),"id",this.getPackageName()));
        int hour;
         if (from.equals("1")){
          hour=radioGroup.indexOfChild(radioButton);
          if (hour==0)
              hour=2;
          else hour=3;
        }else {
             hour=radioGroup.indexOfChild(radioButton)/2;
         }
        if (!nextdate.equals("no")) {
            TextView textView = radioview.findViewById(R.id.OtherPayment);
            if (textView.getText().equals("Tomorrow Lunch") ||
                    textView.getText().equals("Monday Lunch")) {
                       date = nextdate;
                       Log.e("Lunch new date is ","Date is "+date);
            }
        }

        if (!checkSelectedDeliveryTime(date,30,giveHour( hour))){
            Log.e("Check Delivery Time ","Inside if case Date "+date+" Time "+giveHour(hour)+":"+"30 hour is "+hour);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("This delivery time is not available Please select another");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    popupWindow.dismiss();
                    newDeliverypopup();
                   // back=1;
                   // deliverypopup(radioview);

                    Log.e("Alert dialog","onclick***********************");
                }
            });
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                  //  deliverypopup(radioview);
                    popupWindow.dismiss();
                    newDeliverypopup();

                    Log.e("Alert dialog","Dismiss***********************");
                }
            });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }else {
            Log.e("Check Delivery Time ", "outside if case Date " + date + " Time " + giveHour(hour) + ":" + "30 hour "+hour);



            Times = radioButton.getText() + " (" + date + ")";
            Log.d("Extracted DeliveryTime ", radioButton.getText() + " (" + date + ") && next date" + nextdate);

            deliveryFormat();
            popupWindow.dismiss();
            back = 0;
        }
        return Times;

    }
     int giveHour(int key){
        int array[]={9,10,11,12,13,14,15};
        return array[key];
     }
    boolean checkSelectedDeliveryTime(String Date,int min,int hour){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        String today = date.format(c.getTime());
        int now=Integer.parseInt( (new SimpleDateFormat("mm").format(c.getTime())));
        int Hour=Integer.parseInt( (new SimpleDateFormat("HH").format(c.getTime())));
        if (today.equals(Date)){
            if (hour==Hour){
                return (now - min) <= 10;

            } else return (Hour - hour) <1;

        }


        return true;
    }


    private void newDeliverypopup(){
        LayoutInflater layoutInflater = (LayoutInflater) cakeDetailView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View custom = layoutInflater.inflate(R.layout.deliverytime_popup, null);
        RelativeLayout relativeLayout = findViewById(R.id.relative);
        popupWindow = new PopupWindow(custom, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        custom.findViewById(R.id.backpopupweight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.cardview2).setEnabled(true);
            }
        });
        newdeliverypopup(custom);


    }
     private void newdeliverypopup(View view ) {
        Calendar c = Calendar.getInstance();
         SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
         String today = date.format(c.getTime());

        date = new SimpleDateFormat("dd/MM/yy");
        c.add(Calendar.DAY_OF_WEEK, 1);
         String tomm = date.format(c.getTime());
         c.add(Calendar.DAY_OF_WEEK, 1);
        String day_after = date.format(c.getTime());
         c.add(Calendar.DAY_OF_WEEK, 1);
        String fourth_day = date.format(c.getTime());

        String day1 = (dayName(today, "dd/MM/yy"));
        String Todaysday = day1;
        String day2 = (dayName(tomm, "dd/MM/yy"));
        String day3 = (dayName(day_after, "dd/MM/yy"));
        String day4 = (dayName(fourth_day, "dd/MM/yy"));


        if (day1.contains("Sunday")) {
            Log.d("Time","Sunday");
             today =  change(today);

            tomm =  change(tomm);
            day_after =  change(day_after);
            fourth_day = change(fourth_day);

            day1 = "Monday";
            day2 = "Tuesday";
            day3 = "Wednesday";
            day4 = "Thursday";
        } else if (day2.contains("Sunday")) {
            Log.d("Time","day 2 sunday");

            tomm = change(tomm);
            day_after = change(day_after);
            fourth_day = change(fourth_day);

            day2 = "Monday";
            day3 = "Tuesday";
            day4 = "Wednesday";
        } else if (day3.contains("Sunday")) {
            Log.d("Time","day3 Sunday");

            day_after = change(day_after);
            fourth_day = change(fourth_day);

            day3 = "Monday";
            day4 = "Tuesday";
        } else if (day4.contains("Sunday")) {
            fourth_day = change(fourth_day);
            day4 = "Monday";
             Log.d("Time","day4 Sunday");

        }



        newdeliveryTime(view,today,tomm,day_after,fourth_day,day1,day2,day3,day4,Todaysday);

     }
    void newdeliveryTime(final View Custom, final String today, final String tomm, final String dayafter, final String fourth_date, final String day1, final String day2, final String day3, final String day4, final String todaysdy){
        Calendar c=Calendar.getInstance();
        SimpleDateFormat date=new SimpleDateFormat("HH");
        final String hour=date.format(c.getTime());
        // final String hour="18" ;
        date=new SimpleDateFormat("mm");
        final String min=date.format(c.getTime());

        TextView rlunch=Custom.findViewById(R.id.OtherPayment);

        if (todaysdy.contains("Saturday")) {
            rlunch.setText("Monday Lunch");
        }else if  (todaysdy.contains("Sunday")) {
            rlunch.setText("Tomorrow Lunch  ");
        }else{
            rlunch.setText("Tomorrow Lunch");

        }
        /* else
             {  if (Integer.parseInt(hour)>=11 && Integer.parseInt(hour)<=12) {
            if (Integer.parseInt(min) >= 30) {

                Custom.findViewById(R.id.radioGroupLunch_fromLunch11).setVisibility(View.GONE);
            }
        }
            if (Integer.parseInt(hour)>=12 && Integer.parseInt(hour)<=13) {
                if (Integer.parseInt(min)>=30){
               //     custom.findViewById(R.id.LunchTimeMainRelative).setVisibility(View.GONE);
                   // rlunch.setEnabled(false);
                   // rlunch.setClickable(false);
                    Custom.findViewById(R.id.radioGroupLunch_fromLunch11).setVisibility(View.VISIBLE);

                    if (todaysdy.equals("Saturday"))
                        rlunch.setText("Monday Lunch");
                    else rlunch.setText("Tomorrow Lunch");
                 }else
                 Custom.findViewById(R.id.radioGroupLunch_fromLunch11).setVisibility(View.GONE);

            }else if (Integer.parseInt(hour)>=13){
                 if (todaysdy.equals("Saturday"))
                    rlunch.setText("Monday Lunch");
                else rlunch.setText("Tomorrow Lunch");

             }else{


            }

        }*/
          /*  if (!todaysdy.equals("Saturday")){
                rlunch.setText("Tomorrow Lunch");
            }else{
                rlunch.setText("Monday Lunch");

            }*/
        ((RadioGroup) Custom.findViewById(R.id.radioGroupLunch)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //   CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,0);

                RadioGroup radioGroup1 = Custom.findViewById(R.id.radioGroupLunch);
            Times= ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),Custom,today,tomm,"1", "30");
            }
        });

                 TextView first = Custom.findViewById(R.id.TodayText);
        TextView second = Custom.findViewById(R.id.TomorrowText);
        TextView third = Custom.findViewById(R.id.ThirdDayText);
        TextView forth = Custom.findViewById(R.id.FourthDayText);
                if (todaysdy.contains("Sunday"))
                {first.setText("Tomorrow");
                    second.setText(day2);
                    third.setText(day3);
                    forth.setText(day4);
                }
                else if(todaysdy.contains("Saturday")){
                    first.setText("Today");
                    second.setText(day2);
                    third.setText(day3);
                    forth.setText(day4);
                }else{
                    first.setText("Today");
                    second.setText("Tomorrow");
                    third.setText(day3);
                    forth.setText(day4);
                }

                if (todaysdy.contains("Sunday")){
                    first.setVisibility(View.VISIBLE);
                    second.setVisibility(View.VISIBLE);
                    third.setVisibility(View.VISIBLE);

                }else {
                    if (Integer.parseInt(hour) >= 15 && Integer.parseInt(hour) <= 16) {
                        if (Integer.parseInt(min) < 30) {
                            first.setVisibility(View.VISIBLE);
                            forth.setVisibility(View.GONE);
                        }

                    }   else if (Integer.parseInt(hour)==17 ){
                        first.setVisibility(View.GONE);
                        forth.setVisibility(View.GONE);
                    }
                    else if (Integer.parseInt(hour) >= 18) {
                        first.setVisibility(View.GONE);
                        forth.setVisibility(View.VISIBLE);
                    } else {
                        first.setVisibility(View.VISIBLE);
                        forth.setVisibility(View.GONE);
                    }
                }

 if (!todaysdy.equals("Sunday" )) {
      if (hour.equals("15")){
          if (Integer.parseInt(min)>30){
              Custom.findViewById(R.id.MainRelativeToday).setVisibility(View.GONE);

          }
      }else if (Integer.parseInt(hour)>15){
          Custom.findViewById(R.id.MainRelativeToday).setVisibility(View.GONE);

      }
 }
                if (!todaysdy.equals("Sunday")) {
                    Custom.findViewById(R.id.MainRelativeToday).setVisibility(View.GONE);
                    Custom.findViewById(R.id.MainRelativeFourth).setVisibility(View.VISIBLE);


                }
                first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Custom.findViewById(R.id.todayRelative).setVisibility(View.VISIBLE);

                        day="("+today+")";
                        if (todaysdy.contains("Sunday")){
                           newpop2(Custom,"not", Integer.parseInt(hour), Integer.parseInt(min), today, tomm, dayafter, fourth_date);
                        }else newpop2(Custom,"Today", Integer.parseInt(hour), Integer.parseInt(min), today, tomm, dayafter, fourth_date);

                    }
                });
                second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Custom.findViewById(R.id.tommorrowRelative).setVisibility(View.VISIBLE);

                        day="("+tomm+")";
                        newpop2(Custom,"not", Integer.parseInt(hour), Integer.parseInt(min), today, tomm, dayafter, fourth_date);
                    }
                });
                third.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        day="("+dayafter+")";
                        Custom.findViewById(R.id.dayafter).setVisibility(View.VISIBLE);

                        newpop2(Custom,"not", Integer.parseInt(hour), Integer.parseInt(min), today, tomm, dayafter, fourth_date);
                    }
                });
                forth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Custom.findViewById(R.id.afterdayafter).setVisibility(View.VISIBLE);
                        day="("+fourth_date+")";
                        newpop2(Custom,"not", Integer.parseInt(hour), Integer.parseInt(min),today,tomm,dayafter,fourth_date);
                    }
                });



    }
    void newpop2(final View custom, final String today, int Time, int Min, final String s, final String tomm, final String dayafter, final String fourth_date )  {
        popup="nn";


          int idstime[]={R.id.RadioGroupToday_from9to10,R.id.RadioGroupToday_from10to11,
                R.id.RadioGroupToday_from11to12,R.id.RadioGroupToday_from12to1,R.id.RadioGroupToday_from1to2,
                R.id.RadioGroupToday_from2to3,R.id.RadioGroupToday_from3to4};
        String time[]={"9-10","10-11","11-12","12-13","13-14","14-15","15-16"};
        byte loc;

        loc=(byte) Arrays.asList(time).indexOf(Time+"-"+(Time+1));

          if (Min<30)
            loc-=1;

        RadioButton b;
        if(today.contains("Today")) {
            for(int i = 0; i<loc+1; i++){
                b=custom.findViewById(idstime[i]);
                b.setVisibility(View.GONE);
                //////condition for time///
            }
         }
         Log.d(" Date","1- "+s+" 2- "+tomm+" 3- "+dayafter+" 4- "+" 5-"+fourth_date+" loc "+loc);
        Log.d("Today contains ","outside if case - "+today);


        ((RadioGroup) custom.findViewById(R.id.RadioGroupToday)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioGroup radioGroup1 = custom.findViewById(R.id.RadioGroupToday);
                //     CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,1);

                ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),custom,s,"no","2","30");
            }
        });
        custom.  findViewById(R.id.TodayText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (  CheckVisibility((RelativeLayout) custom.findViewById(R.id.todayRelative))) {
                 ((RadioGroup) custom.findViewById(R.id.RadioGroupToday)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(RadioGroup radioGroup, int i) {
                         RadioGroup radioGroup1 = custom.findViewById(R.id.RadioGroupToday);
                       //     CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,1);

                         ExtractDeliveryTime(radioGroup1.
                            getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                            toString().trim(),custom,s,"no","2", "30");
                     }
                 });
             }
            }
        });
        ((RadioGroup) custom.findViewById(R.id.RadioGroupTomorrow)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,2);

                RadioGroup radioGroup1 = custom.findViewById(R.id.RadioGroupTomorrow);
                ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),custom,tomm,"no","3", "30");
            }
        });
        custom.   findViewById(R.id.TomorrowText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           if (CheckVisibility((RelativeLayout) custom.findViewById(R.id.tommorrowRelative))) {
               ((RadioGroup) custom.findViewById(R.id.RadioGroupTomorrow)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(RadioGroup radioGroup, int i) {
                       //CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,2);

                       RadioGroup radioGroup1 = custom.findViewById(R.id.RadioGroupTomorrow);
                       ExtractDeliveryTime(radioGroup1.
                               getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                               toString().trim(),custom,tomm,"no","3", "30");
                   }
               });
           }
            }
        });
        ((RadioGroup) custom.findViewById(R.id.RadioGroupThird)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //      CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,3);

                RadioGroup radioGroup1=custom.findViewById(R.id.RadioGroupThird);
                ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),custom,dayafter,"no","4", "30");

            }
        });
        custom.   findViewById(R.id.ThirdDayText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if ( CheckVisibility((RelativeLayout) custom.findViewById(R.id.dayafter))){
                ((RadioGroup) custom.findViewById(R.id.RadioGroupThird)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                  //      CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,3);

                        RadioGroup radioGroup1=custom.findViewById(R.id.RadioGroupThird);
                        ExtractDeliveryTime(radioGroup1.
                                getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                                toString().trim(),custom,dayafter,"no","4", "30");

                    }
                });}
            }
        });
        custom. findViewById(R.id.FourthDayText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          if (CheckVisibility((RelativeLayout) custom.findViewById(R.id.afterdayafter))){
                ((RadioGroup) custom.findViewById(R.id.RadioGroupFourth)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                      //  CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,4);

                        RadioGroup radioGroup1=custom.findViewById(R.id.RadioGroupFourth);
                        ExtractDeliveryTime(radioGroup1.
                                getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                                toString().trim(),custom,fourth_date,"no","5", "30");
                    }
                });}
            }
        });
        ((RadioGroup) custom.findViewById(R.id.RadioGroupFourth)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //  CheckRadioGroup((RadioGroup) custom.findViewById(R.id.RadioGroupCustomDelivery),custom,4);

                RadioGroup radioGroup1=custom.findViewById(R.id.RadioGroupFourth);
                ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),custom,fourth_date,"no","5", "30");
            }
        });



    }
    public String change(String dates) {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date date = null;
        try {

            date = format.parse(dates);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return format.format(calendar.getTime());
        //return date;
    }


    public String dayName(String inputDate, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEEE").format(date);
    }

    void visibility() {
         int idspointer[] = {R.id.pointer1, R.id.pointer2, R.id.pointer3, R.id.pointer4, R.id.pointer5, R.id.pointer6, R.id.pointer7, R.id.pointer8, R.id.pointer9, R.id.pointer10};
        for (int i = 0; i < total; i++) {
          findViewById(idspointer[i]).setVisibility(View.VISIBLE);
        }


    }

    public void checkoutClicked(View view) {
        view.setEnabled(false);
        //findViewById(R.id.progressBarHorizontal).setVisibility(View.VISIBLE);
        chekout();
    }

    public void addtocartClicked(View view) {
        view.setEnabled(false);
        Toast.makeText(cakeDetailView.this,"Sending this item into your CakeCart",Toast.LENGTH_SHORT).show();

        //findViewById(R.id.progressBarHorizontal).setVisibility(View.VISIBLE);
        storeTocart(false);
        view.setEnabled(true);
    }

    public void onclickback(View view) {
        finish();
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (check.equals("main"))
            { firebase1();
                 currPosition=0;
                check="not";
                back=1;
                CakeImage_popup();}
            return super.onSingleTapConfirmed(e);

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            if (e1.getX() < e2.getX()) {
                if (currPosition != 0) {
                    currPosition -= 1;
                    changecolorof_ImagePointer(currPosition + 1, currPosition, "No");
                imagePointer(currPosition);
                }

            } else if (e1.getX() == e2.getX()) {
                //currPosition = getVisibleViews("right");
                currPosition += 1;
            } else {
                if (currPosition < total - 1) {
                    currPosition += 1;
                //    changecolorof_ImagePointer(currPosition - 1, currPosition, "No");
                    imagePointer(currPosition);
                }

            }
            try {
                if (check.equals("main"))
                    recyclerView.smoothScrollToPosition(currPosition);
                else recyclerViewTop.smoothScrollToPosition(currPosition);
            } catch (Exception IllegalArgumentException) {
            }

            return true;
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {
             ImageView imageView;

            if (check.equals("not") ){
                cakePop.findViewById(R.id.zommview).setVisibility(View.VISIBLE);
                recyclerViewTop.setVisibility(View.INVISIBLE);
                imageView=cakePop.findViewById(R.id.ZoomimageView);
//                Picasso.get().load(imageUrls.get(currPosition)).into(imageView);
                Glide.with(cakeDetailView.this).load(imageUrls.get(currPosition)).into(imageView);
                check="zoom";
                //zoomImageFromThumb(imageView, String.valueOf(imageUrls.indexOf(currPosition)));

            }else
            if (check.equals("zoom")){
                cakePop.findViewById(R.id.zommview).setVisibility(View.GONE);
                recyclerViewTop.setVisibility(View.VISIBLE);
                check="not";

            }
            return super.onDoubleTap(e);
        }

    }

    void findTotal() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+"Cake/" + originalName + "/Images");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total = (int) dataSnapshot.getChildrenCount();
                visibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Tablayout() {
          FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new orderFragment1()).commit();
    }

    void changecolorof_ImagePointer(int backto_grey, int ToBlue, String all) {
     /*   try {
            int imgids[] = {R.id.pointer1, R.id.pointer2, R.id.pointer3, R.id.pointer4, R.id.pointer5, R.id.pointer6, R.id.pointer7, R.id.pointer8, R.id.pointer9, R.id.pointer10};
            if (all.equals("All")) {
                ImageView imgtoBlue = findViewById(imgids[0]);
                imgtoBlue.getDrawable().setColorFilter(Color.parseColor("#ff0092f4"), PorterDuff.Mode.SRC_IN);

                for (int i = 1; i < 10; i++) {
                    ImageView imgBacktogrey = findViewById(imgids[i]);
                    imgBacktogrey.getDrawable().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
                }
            } else {


                ImageView imgBacktogrey = findViewById(imgids[backto_grey]);
                ImageView imgtoBlue = findViewById(imgids[ToBlue]);


                imgBacktogrey.getDrawable().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
                imgtoBlue.getDrawable().setColorFilter(Color.parseColor("#ff0092f4"), PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception ignored) {
        }*/
    }

    public static class CakeFragment extends Fragment {


        public CakeFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View v = inflater.inflate(R.layout.descriptionof_cake, null);
            v.findViewById(R.id.descr).setVisibility(View.VISIBLE);
            v.findViewById(R.id.Deliveryinfo).setVisibility(View.GONE);
            v.findViewById(R.id.recyclerCart).setVisibility(View.GONE);
             TextView textView=v.findViewById(R.id.flavour);
            textView.setText(String.format("%s %s", getString(R.string.cake_flavour), flavour));
            textView=v.findViewById(R.id.weight);
            textView.setText(String.format("%s %s", getString(R.string.weight), weight));
            textView=v.findViewById(R.id.shape);
            textView.setText(String.format("%s %s", getString(R.string.shape_cake), Cakeshape));
            return v;
        }


    }

    public static class CakeFragment2 extends Fragment {


        public CakeFragment2() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View v = inflater.inflate(R.layout.descriptionof_cake, null);
            v.findViewById(R.id.descr).setVisibility(View.GONE);
            v.findViewById(R.id.Deliveryinfo).setVisibility(View.VISIBLE);
            TextView textView=v.findViewById(R.id.textviewpara);
            textView.setText(String.format("%s    %s%s    %s%s    %s", getString(R.string.pointer), getString(R.string.para1), getString(R.string.pointer), getString(R.string.para2), getString(R.string.pointer), getString(R.string.para3)));
            return v;
        }

    }
    public static class orderFragment1 extends Fragment {

        public TabLayout tabLayout;
        public ViewPager viewPager;
        public    int int_items= 2;



        public orderFragment1() {
            // Required empty public constructor
        }


         @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View v = inflater.inflate(R.layout.tab_cake,null);
            tabLayout=(TabLayout)v.findViewById(R.id.tabs);
            viewPager=(ViewPager)v.findViewById(R.id.viewpager);
            // PagerAdapter pagerAdapter=new PagerAdapter(()) ;
            //set an adpater
            //  tabLayout.getTabAt(0).setIcon(R.drawable.cancel);
            Log.e("Display size",String.valueOf(display.getWidth()));


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
                        return new CakeFragment();
                    case 1:
                        return new CakeFragment2();



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
                        return "Description";
                    case 1:
                        return "Delivery Info";

                }

                return null;
            }

        }

    }
    void CakeImage_popup(){
         LayoutInflater layoutInflater = (LayoutInflater) cakeDetailView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cakePop = layoutInflater.inflate(R.layout.popup_cakeimages, null);
        RelativeLayout relativeLayout = findViewById(R.id.relative);
          popupWindow = new PopupWindow(cakePop, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        recyclerViewTop=cakePop.findViewById(R.id.recyclerTop);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(cakeDetailView.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewTop.setLayoutManager(mLayoutManager);
        recyclerViewTop.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTop.setAdapter(adapter);
         firebase(recyclerViewTop,R.layout.cake_image2, (byte) 0,"Food Items/"+Canteen+"/"+"Cake/" + originalName + "/Images");
        recyclerViewTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }
                return false;
            }
        });
        RecyclerView recyclerViewbottom=cakePop.findViewById(R.id.bototmRecycler);
        mLayoutManager = new LinearLayoutManager(cakeDetailView.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewbottom.setLayoutManager(mLayoutManager);
        recyclerViewbottom.setItemAnimator(new DefaultItemAnimator());
        recyclerViewbottom.setAdapter(adapter);

        firebase(recyclerViewbottom,R.layout.cake_image3, (byte) 1,"Food Items/"+Canteen+"/"+"Cake/" + originalName + "/Images");

        cakePop.findViewById(R.id.cancelcakePOpup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check="main";
                back=0;
                popupWindow.dismiss();
                recyclerView.scrollToPosition(0);
                changecolorof_ImagePointer(0,0,"All");
                currPosition = 0;
            }
        });

            HorizontalScrollView horizontalScrollView=cakePop.findViewById(R.id.zommview);
         horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }
                return false;
            }
        });


    }
private void Cake_Extras(String Address,RecyclerView recyclerView){
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Address);
    final String[] sa = new String[1];

    FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
            Food.class,
            R.layout.layout_cake_extras,
            mainactiv.FoodViewHolder.class,
            databaseReference
    ) {
        @Override
        protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
            {
                viewHolder.setImage(getApplicationContext(),model.getFood_Image());

                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setName(model.getFood_name());
                viewHolder.setDiscount1(String.valueOf(model.getPrice()),String.valueOf(model.getDiscount()));
              /*  viewHolder.setListnerforminus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.setQuantity(storeQunatity(viewHolder.getQuantity(), 's',model.getFood_name()));
                        datainside_extra("MINUS");
                        TotalPrice();

                    }
                });
                viewHolder.setListnerforplus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.setQuantity( storeQunatity( viewHolder.getQuantity(), 'a',model.getFood_name()));
                        datainside_extra("PLUS");
                        TotalPrice();

                    }
                });*/
                viewHolder.setListnerforplus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sa[0] = String.valueOf(storeQunatity("",'a',model.getFood_name()));
                        viewHolder.setQuantity(sa[0]);
                        TotalPrice();

                    }
                });
                viewHolder.setListnerforminus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sa[0] = String.valueOf(storeQunatity("",'s',model.getFood_name()));
                        viewHolder.setQuantity(sa[0]);
                        TotalPrice();
                    }
                });
                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getFood_name().equals("Shaped Candles"))
                        {
                            if (viewHolder.returnAgeTexttext().getVisibility() != View.VISIBLE) {
                                if (viewHolder.returnAgeEdittext().getVisibility() != View.VISIBLE) {
                                    viewHolder.returnAgeTexttext().setVisibility(View.VISIBLE);
                                    //  cake_extrasImage.add(model.getFood_Image());
                                    datainside_extra("NEw data");

                                }
                            }
                        }
                        else {

                            if (viewHolder.setListnerforplus().getVisibility()!=View.VISIBLE) {
                                viewHolder.makeVisibletheADDbuttons();}
                                cake_extrasName.add(model.getFood_name());
                                cake_extrasQuantity.add("0");
                                cake_extrasPrice.add(String.valueOf(model.getPrice()));
                                cake_extrasImage.add(model.getFood_Image());


                        }
                    }
                });
                viewHolder.returnAgeTexttext().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.returnAgeTexttext().setVisibility(View.GONE);
                        viewHolder.OK().setVisibility(View.VISIBLE);
                        viewHolder.returnAgeEdittext().setVisibility(View.VISIBLE);
                    }
                });
                viewHolder.OK().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editText=viewHolder.returnAgeEdittext();
                        shape=" , Shaped Candles ( age "+editText.getText().toString().trim()+")";
                        setExtras(shape,simple,bloopers,spray,spark);
                        viewHolder.returnAgeTexttext().setVisibility(View.VISIBLE);
                        viewHolder.OK().setVisibility(View.GONE);
                        viewHolder.returnAgeEdittext().setVisibility(View.GONE);
                        TextView textView=viewHolder.returnAgeTexttext();
                        textView.setText(String.format("%s %d", getString(R.string.age_is), ExtractNumber(shape)));
                        int index=return_index_shape_candle();
                        if(index<0){
                             cake_extrasName.add("Shaped Candles ( age "+editText.getText().toString().trim()+")");
                            cake_extrasQuantity.add("1");
                            cake_extrasPrice.add(String.valueOf(model.getPrice()));
                        }else{

                             cake_extrasName.add(index,"Shaped Candles ( age "+editText.getText().toString().trim()+")");

                        }

                        Log.e("Cake Total_Price","Index of shape is "+cake_extrasName.indexOf(model.getFood_name()));
                        if (editText.getText().toString().trim().isEmpty()||editText.getText().toString().trim().equals("0"))
                        {   //int index=cake_extrasName.indexOf(cake_extrasName.contains("Name"));
                            //if (index>=0)
                            index=return_index_shape_candle();
                            cake_extrasQuantity.add(index,"0");

                        }/*else{
                            if (index <= 0) {
                                cake_extrasQuantity.add(0,"1");
                            }else
                                cake_extrasQuantity.add(index,"1");*/
                        //}
                        TotalPrice();


                    }
                });
            }
        }
    };
    recyclerView.setAdapter(firebaseRecyclerAdapter);
}
private void morecake(){
     RecyclerView recyclerView=findViewById(R.id.ExtrasRecycler);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( this,LinearLayoutManager.HORIZONTAL,false);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new food_beverages.GridSpacingItemDecoration(2, 2, true));
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    firebase(recyclerView,R.layout.layout_cake_extras, (byte) 3,"Food Items/"+Canteen+"/"+"Cake");
    Log.e("More cake","Inside function");
    recyclerView =findViewById(R.id.cakeExtrasRecycler);
    mLayoutManager = new LinearLayoutManager( this,LinearLayoutManager.HORIZONTAL,false);

    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    Cake_Extras("Food Items/"+Canteen+"/"+"Cake_Extras",recyclerView);

}
        void datainside_extra(String from){
        for (int i=0;i<cake_extrasName.size();i++){
            Log.e("Cake Total_Price",from+"Elements are "+cake_extrasName.get(i)+cake_extrasPrice.get(i)+" q"+cake_extrasQuantity.get(i));
        }
        }
    void firebase(RecyclerView recyclerView, int layout, final Byte from,String Address){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Address);

        FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                layout,
                mainactiv.FoodViewHolder.class,
                databaseReference.orderByChild("Sum_of_Rating")
        ) {
            @Override
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.setImage3(getApplicationContext(),model.getFood_Image());
                Log.e("More cake","Inside firebase outside from  "+from);

                if (from==3){
                    store_rating();
                  Log.e("More cake","Inside firebase from is 3");
                 viewHolder.setPrice(model.getGm500());
                 viewHolder.setName(model.getFood_name());
                    viewHolder.setrating(String.valueOf(model.getSum_of_Ratings()),String.valueOf(model.getTotal_NoOfTime_Rated()));
                    viewHolder.setDiscount2(model.getGm500_d(),model.getGm500());
                 viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent intent=new Intent(cakeDetailView.this,cakeDetailView.class);
                         intent.putExtra("CakeName",model.getFood_name());
                         intent.putExtra("CakePrice",model.getGm500());
                         intent.putExtra("d_gm500",model.getGm500_d());
                         intent.putExtra("CakeImage",model.getFood_Image());
                         intent.putExtra("gm1000",model.getGm1000());
                         intent.putExtra("d_gm1000",model.getGm1000_d() );
                         intent.putExtra("gm200",model.getGm200());
                         intent.putExtra("d_gm200",model.getGm200_d());
                         intent.putExtra("Ratingsum",model.getSum_of_Ratings());
                         intent.putExtra("Ratingno",model.getTotal_NoOfTime_Rated());
                         intent.putExtra("Shape",model.getShape());
                         intent.putExtra("Flavour",model.getFlavour());

                         startActivity(intent);
                     }
                 });
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(cakeDetailView.this,cakeDetailView.class);
                            intent.putExtra("CakeName",model.getFood_name());
                            intent.putExtra("CakePrice",model.getGm500());
                            intent.putExtra("d_gm500",model.getGm500_d());
                            intent.putExtra("CakeImage",model.getFood_Image());
                            intent.putExtra("gm1000",model.getGm1000());
                            intent.putExtra("d_gm1000",model.getGm1000_d() );
                            intent.putExtra("gm200",model.getGm200());
                            intent.putExtra("d_gm200",model.getGm200_d());
                            intent.putExtra("Ratingsum",model.getSum_of_Ratings());
                            intent.putExtra("Ratingno",model.getTotal_NoOfTime_Rated());
                            intent.putExtra("Shape",model.getShape());
                            intent.putExtra("Flavour",model.getFlavour());

                            startActivity(intent);
                        }
                    });
             }else
             if (from==1){
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageView imageView=cakePop.findViewById(R.id.ZoomimageView);
                            //Picasso.get().load(model.getFood_Image()).into(imageView);
                            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation())
                                    .load(model.getFood_Image()).into(imageView);
                            recyclerViewTop.smoothScrollToPosition(position);
                             currPosition=position;
                        }

                    });

                }


            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
        int return_index_shape_candle(){
            for (int i=0;i<cake_extrasName.size();i++){
                if (cake_extrasName.get(i).toUpperCase().contains("SHAPE")){
                    return i;
                 }
            }
            return -1;
        }
    private void sendtoExtras(String from,String quantity){

        switch (from) {
            case "Shaped Candles":
                shape = " , Shaped Candles (" + quantity + ")";
                setExtras(shape, simple, bloopers, spray, spark);
                break;
            case "Simple Candles":
                simple = " , Simple Candles (" + quantity + ")";
                setExtras(shape, simple, bloopers, spray, spark);

                break;
            case "Poppers":
                bloopers = " , Poppers (" + quantity + ")";
                setExtras(shape, simple, bloopers, spray, spark);

                break;
            case "Snow Spray":
                spray = " , Snow Spray (" + quantity + ")";
                setExtras(shape, simple, bloopers, " , Snow Spray (" + quantity + ")", spark);

                break;
            case "Sparklers":
                spark = " , Sparklers (" + quantity + ")";
                setExtras(shape, simple, bloopers, spray, spark);
                break;
        }
        Log.e("Spray",from+quantity);
    }
    private String storeQunatity(String quantity, char s,String from) {
      /*  int j=  cake_extrasName.indexOf(from);
        Log.e("Cake Total_Price","Inside store quantity"+ from+"index is "+j);
        Log.e("Cake Total_Price", String.valueOf(j));
         if ( s =='a'){
             String q=Integer.toString(Integer.parseInt(quantity) + 1);
            if ( q .equals("15"))
            {  sendtoExtras(from,"");
                cake_extrasQuantity.add(j,"1");
                return "0"; }
            else { cake_extrasQuantity.add(j,q);

                sendtoExtras(from,q);return q; }

         } else {
              String q=Integer.toString(Integer.parseInt(quantity) - 1);
            if ( q.equals("-1") || q.equals("0"))
            {                  cake_extrasQuantity.add(j,"0");

                sendtoExtras(from,"");

                return "0";}
            else {                cake_extrasQuantity.add(j,q);
                sendtoExtras(from,q); return q;}
         }*/
        int j=  cake_extrasName.indexOf(from);

        if ( s=='a'){int quantitys=Integer.parseInt(cake_extrasQuantity.get(j))+1;
            cake_extrasQuantity.set(j,String.valueOf(quantitys));
            if (cake_extrasQuantity.get( j) .equals("15"))
            {cake_extrasQuantity.set(j,"0");
            sendtoExtras(from,"");

            }else{
                sendtoExtras(from,cake_extrasQuantity.get(j));

            }

        }
        else {  cake_extrasQuantity.set(j,String.valueOf(Integer.parseInt(cake_extrasQuantity.get(j))-1));

            if (cake_extrasQuantity.get( j).equals("-1"))
            {               cake_extrasQuantity.set(j,"0");
                sendtoExtras(from,"");
            }else{
                sendtoExtras(from,cake_extrasQuantity.get(j));

            }

        }
        return cake_extrasQuantity.get(j);

    }

    void firebase1(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+"Cake/" + originalName + "/Images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Food food=dataSnapshot1.getValue(Food.class);
                    //String s=food.getFood_Image();
                      imageUrls.add(food.getFood_Image());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void chekout(){
        //if
                storeTocart(true);
        /*{
        Intent intent=new Intent(this,orderFood.class);
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
            intent.putExtra("DeliveryTime",Times);
            startActivity(intent);
        }*/

    }
    boolean isDataok(String data){
        //TextView textView=findViewById(R.id.textview);
        if (data.equals("") || !data.matches(".*\\d+.*")||data.equals("0"))
        {
            return false;
        }
        return true;

    }
    boolean storeTocart(boolean activity){
        FirebaseAuth auth=FirebaseAuth.getInstance();
          TextView textView=findViewById(R.id.CakePrice);
         if (!isNetworkAvailable(getApplicationContext())){
            Toast.makeText(cakeDetailView.this,"Not connected to internet",Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBarHorizontal).setVisibility(View.GONE);
            findViewById(R.id.checkout).setEnabled(true);
            return false;
        }else {

                String address="User Informations/"+auth.getCurrentUser().getUid()+"/FoodCart/Cake/";

             Cart cart;
            DatabaseReference database=FirebaseDatabase.getInstance().getReference();

            for (int i=0;i<cake_extrasName.size();i++)
            {
                if (isDataok(cake_extrasQuantity.get(i))){
                    if (!cake_extrasName.contains("Shaped"))
                    {
                      try {
                          cart = new Cart(cake_extrasImage.get(i)
                                  , cake_extrasName.get(i), "Cake_Extras", Integer.parseInt(cake_extrasPrice.get(i))
                                  , cake_extrasQuantity.get(i));

                          Log.e("Cake Cart", cake_extrasName.get(i) + cake_extrasQuantity.get(i) +
                                  cake_extrasPrice.get(i));

                          database=FirebaseDatabase.getInstance().getReference(address+cake_extrasName.get(i));

                          setCheck(cart,database);
                      }catch (Exception e){}
                    } else {
                        try {


                        cart = new Cart(cake_extrasImage.get(i)
                                , cake_extrasName.get(i), "Cake_Extras",Integer.parseInt(cake_extrasPrice.get(i))
                                ,"1");
                        Log.e("Cake Cart", "Contains shaped candles "+cake_extrasName.get(i)+cake_extrasQuantity.get(i)+
                                cake_extrasPrice.get(i));

                            database=FirebaseDatabase.getInstance().getReference(address+cake_extrasName.get(i));

                            setCheck(cart,database);
                    }catch (Exception e){}}


             //       database=FirebaseDatabase.getInstance().getReference(address+cake_extrasName.get(i));

               //     setCheck(cart,database);

                }
                else Firebasestatus--;

            }


            database=FirebaseDatabase.getInstance().
                    getReference(address+originalName+" "+egg+" "+weight);
            cart=new Cart(cakeImage,originalName+" "+egg+" "+weight,"Cake",
                Integer.parseInt(Integer.toString(Removespacing(textView.getText().toString().trim()))),"1");
            setCheck(cart,database);
            if (activity){
                Intent intent=new Intent(this,orderFood.class);
                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
                intent.putExtra("DeliveryTime",Times);
                startActivity(intent);
            }

        }
        return true;
    }
    private void setCheck(Cart cart, DatabaseReference databaseReference ){
        databaseReference.setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
            //public int count = 0;

            @Override
            public void onSuccess(Void aVoid) {
                writeSucessful++;
                Log.e("CakeDetialView","WriteSuccessful"+writeSucessful+" Firebasestatus"+Firebasestatus);
               // if (Firebasestatus.equals(writeSucessful)){
                    Log.e("CakeDetialView","Inside success WriteSuccessful"+writeSucessful+" Firebasestatus"+Firebasestatus);

                   // findViewById(R.id.progressBarHorizontal).setVisibility(View.GONE);
                    writeSucessful=0;
                    Toast.makeText(cakeDetailView.this,"added to the cart",Toast.LENGTH_SHORT).show();
              //  }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                findViewById(R.id.progressBarHorizontal).setVisibility(View.GONE);
                Toast.makeText(cakeDetailView.this,"Error occurred "+e.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*public boolean IsDatabaseConnected(){
        //DatabaseReference firebaseRef=new Fir
    }*/
    public static boolean isNetworkAvailable(Context con){
        try{
            ConnectivityManager cm=(ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=cm.getActiveNetworkInfo();
            if (networkInfo!=null&& networkInfo.isConnected()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    void ImageLoader(Byte size,String from) {
        ImageView imageView1=findViewById(R.id.Selectstar1);
        ImageView imageView2=findViewById(R.id.Selectstar2);
        ImageView imageView3=findViewById(R.id.Selectstar3);
        ImageView imageView4=findViewById(R.id.Selectstar4);
        ImageView imageView5=findViewById(R.id.Selectstar5);
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
             ImageView stars1[] = {imageViewa, imageViewb, imageViewc, imageViewd, imageViewe};
            //size= (byte) (ReceivedRatings / ReceivedNoOfRatings);
            for (int i = 0; i < 5; i++) {
                try {
//                    Picasso.get().load(R.drawable.selectstar).into(stars1[i]);
                    Glide.with(this).load(R.drawable.selectstar).into(stars1[i]);
                    if (i < size )
                        Glide.with(this).load(R.drawable.star).into(stars1[i]);
                        //    Picasso.get().load(R.drawable.star).into(stars1[i]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    void selectStars(){


        findViewById(R.id.Selectstar1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader((byte) 1,"Ratings");
                Selected_stars ="1";
            }
        });
        findViewById(R.id.Selectstar2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader((byte) 2,"Ratings");
                Selected_stars= "2";

            }
        });findViewById(R.id.Selectstar3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader((byte) 3,"Ratings");
                Selected_stars= "3";

            }
        });
        findViewById(R.id.Selectstar4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader((byte) 4,"Ratings");
                Selected_stars    ="4";

            }
        });
        findViewById(R.id.Selectstar5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader((byte) 5,"Ratings");
                Selected_stars  ="5";

            }
        });

    }
    void store_rating(){
        Intent intent=getIntent();
        String ratingsum=intent.getStringExtra("Ratingsum");
        final String ratingno=intent.getStringExtra("Ratingno");
        try{
            if (Integer.parseInt(Selected_stars)>0 ){

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Food Items/"+Canteen+"/"+"Cake"+"/"+originalName+"/Sum_of_Ratings").
                    setValue( (Integer.parseInt(ratingsum)+Integer.parseInt(Selected_stars))).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().
                                    child("Food Items/"+Canteen+"/"+"Cake"+"/"+originalName+"/Total_NoOfTime_Rated");
                            databaseReference1.setValue( (Integer.parseInt(ratingno)+1));

                            finish();
                            //     supportFinishAfterTransition();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(cakeDetailView.this,"An error occurred "+e.toString(),Toast.LENGTH_SHORT).show();
                  }
            });
            Log.e("Back debug","Stars not empty going back to main menu");

        }
        else{
            Log.e("Back debug","Stars empty going back to main menu");

        }
        }catch (Exception e){
            if (ratingno==null) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().
                        child("Food Items/"+Canteen+"/" + "Cake" + "/" + originalName);
                databaseReference1.child("Total_NoOfTime_Rated").setValue(1);
                databaseReference1.child("Sum_of_Ratings").
                        setValue(Integer.parseInt(Selected_stars));
                Log.e("Back debug","catch empty going back to main menu"+e.toString());

                Log.e("Favorite","Backpressed "+e.toString());
              }

        }
    }
    @Override
    public void onBackPressed() {
        if (back==2){
            popupWindow1.dismiss();
            back=1;
        }else if(back==1) {popupWindow.dismiss();back=0;
            check="main";
            recyclerView.scrollToPosition(0);
            changecolorof_ImagePointer(0,0,"All");
            currPosition = 0;

        }

        else  {
            store_rating();
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        store_rating();
        super.onDestroy();
    }
}

