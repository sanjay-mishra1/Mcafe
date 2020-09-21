package com.sanproject.mcafe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.constant.constants.Canteen;
import static com.sanproject.mcafe.logIN.MYPREFERENCES;

public class orderFood extends AppCompatActivity {
     ArrayList<String> names;
    ArrayList<Integer> prices;
    ArrayList<Integer> Quantity;
    boolean pop=false;
 int Final_Total=0;
private int records=0;
PopupWindow popupWindow,popupWindow1;
String Times;
     private  TextView  TOtal;
private Button fifty,hundred;
private FirebaseAuth auth;
     RelativeLayout relativeLayout;
    private AlbumsAdapter adapter;
    private DatabaseReference data;
    RadioButton rlunch,rlater;
    private String popup="not";
    private String day;
    ArrayList<String> img ;
    private TextView totalfood;
    private ArrayList<String> foodtype;
    private ArrayList<String> food_Times;
    private String Address;
    private String Other="0";
    private String Tax="0";
    private String DeliveryTime="";
    private Dialog progressDialog;
    private String Type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        Times= "";
        setPaymentCredentials();
        receive();
        names=new ArrayList<String>(30);
        foodtype=new ArrayList <String> (30 );
        rlater=findViewById(R.id.deliverlater);
        img=new ArrayList <String> (30 );
        food_Times=new ArrayList <String> (30 );
        Load();
findViewById(R.id.deliveryformat).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        view.setEnabled(false);
        newDeliverypopup();
     }
});
findViewById(R.id.backpopupweight).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        view.setEnabled(false);
        onBackPressed();
    }
});
          Quantity=new ArrayList <Integer> (  );
        prices=new ArrayList <Integer> (  );

        relativeLayout=findViewById(R.id.relative);
        TOtal=findViewById(R.id.total_final);
           fifty=findViewById(R.id.pay50);
         auth=FirebaseAuth.getInstance();
        setCheckFor_Lunch();

        fifty.setOnClickListener(new View.OnClickListener() {
     @Override
    public void onClick(View view) {
         if (Times != null&&!Times.isEmpty()) {
             String Time_Instant = DateFormat.getDateTimeInstance().format(new Date());
             String OrderNo = getUOrderNumber();
             try {
                 for (int i = 0; i < records; i++) {
                     String Foodname = names.get(i);
                     if (foodtype.get(i).equals("Cake")) {
                         Foodname = Foodname.toLowerCase();
                         Foodname = names.get(i).substring(0, Foodname.indexOf("cake") + 4);


                     }
                     if (names.get(i).contains("Shaped Candles")) {
                         Foodname = names.get(i).substring(0, names.get(i).indexOf("Candles") + 7);
                     }
                     DatabaseReference data = FirebaseDatabase.getInstance().getReference().
                             child("Food Items/"+Canteen+"/" + foodtype.get(i) + "/" + Foodname);
                      data.child("Total_orders").setValue((Integer.parseInt(food_Times.get(i)) + 1));
                 }
             } catch (Exception IndexOutOfBoundsException) {
                 Toast.makeText(orderFood.this, "Index out of bound exception", Toast.LENGTH_SHORT).show();
             }
             SharedPreferences preferences=getSharedPreferences(MYPREFERENCES,MODE_PRIVATE);
             if (preferences.getBoolean("verify_phone",false)){
                 MobileVerificationFragment fragment = new MobileVerificationFragment();
                 Bundle intent=new Bundle();
                 intent.putString("Order", OrderNo);
                 intent.putString("TodaysTime", Time_Instant);
                 intent.putString("DeliveryTime", Times);
                 intent.putString("Status", "Pending");
                 intent.putString("Total_Amount", Integer.toString(Final_Total));
                 intent.putString("UId", Objects.requireNonNull(auth.getCurrentUser()).getUid());
                 intent.putString("Total_Food", Integer.toString(records));
                 intent.putString("Image0", img.get(0));
                 intent.putString("Address", Address);
                 intent.putString("Tax", Tax);
                 intent.putString("From", "OrderFood");
                 intent.putString("OtherPayment", Other);
                 try {
                     intent.putString("Image1", img.get(1));
                     intent.putString("Image2", img.get(2));
                     intent.putString("Image3", img.get(3));
                 } catch (Exception e) {
                 e.printStackTrace();
                 }
                fragment.setArguments(intent);
                 fragment.showNow(getSupportFragmentManager(), "Verify mobile");
             }else {
                 showProgress_dialog();
                 view.setEnabled(false);
                 Intent intent = new Intent(orderFood.this, payment.class);
                 intent.putExtra("Order", OrderNo);
                 intent.putExtra("TodaysTime", Time_Instant);
                 intent.putExtra("DeliveryTime", Times);
                 intent.putExtra("Status", "Pending");
                 intent.putExtra("Total_Amount", Integer.toString(Final_Total));
                 intent.putExtra("UId", auth.getCurrentUser().getUid());
                 intent.putExtra("Total_Food", Integer.toString(records));
                 intent.putExtra("Image0", img.get(0));
                 intent.putExtra("Address", Address);
                 intent.putExtra("Tax", Tax);
                 intent.putExtra("From", "OrderFood");
                 intent.putExtra("OtherPayment", Other);
                 try {
                     intent.putExtra("Image1", img.get(1));
                     intent.putExtra("Image2", img.get(2));
                     intent.putExtra("Image3", img.get(3));
                 } catch (Exception IndexOutOfBoundsException) {
                 }

                 startActivity(intent);
             }
//here finish finish();

         }else{
             Toast.makeText(orderFood.this,"Please select delivery time",Toast.LENGTH_SHORT).show();;
             view.setEnabled(true);
         }
     }
});

    }





    void setCheckFor_Lunch() {
        Log.e("Lunch time","set check for lunch");

        Calendar c = Calendar.getInstance();
        int now = Integer.parseInt((new SimpleDateFormat("mm").format(c.getTime())));
        int hour = Integer.parseInt((new SimpleDateFormat("HH").format(c.getTime())));
        String  day =  (new SimpleDateFormat("EEEE").format(c.getTime()));
        RadioButton radioButton = findViewById(R.id.lunchtime);
        if (day.toUpperCase().contains("SUNDAY")){
            radioButton.setText("Tomorrow Lunch (12:50)");

        }
        else if (day.toUpperCase().contains("SATURDAY")){
         if (hour == 12) {
            if (now > 30) {
                radioButton.setText("Monday Lunch (12:50)");
            }
        }
         else if (hour > 12) {
            radioButton.setText("Monday Lunch (12:50)");

        }
        }else {
            if (hour == 12) {
                if (now > 30) {
                    radioButton.setText("Tomorrow Lunch (12:50)");
                }
            } else if (hour > 12) {
                radioButton.setText("Tomorrow Lunch (12:50)");
            }
        }


        if (Type.toUpperCase().contains("CAKE")) {
            Log.e("Lunch time","Inside contains cake");
            if (day.toUpperCase().contains("SATURDAY")) {
                radioButton.setText("Monday Lunch (12:50)");
                Log.e("Lunch time","Inside contains cake today is saturday");
            } else
            {                Log.e("Lunch time","Inside contains cake today is not saturday");

                radioButton.setText("Tomorrow Lunch (12:50)");
            }
        }
    }


    void setStyle(){
          RelativeLayout.LayoutParams  params;
       // params  = new RelativeLayout.LayoutParams(2,viewHeight);
        // findViewById(R.id.vsiew).setLayoutParams(params[0]);
        RelativeLayout relativeLayout=findViewById(R.id.main);
        params=new RelativeLayout.LayoutParams(2,pxtodp(relativeLayout.getHeight()));
       // findViewById( R.id.vsiew).setLayoutParams(params);
        Log.e("Set style ",String.valueOf(pxtodp(relativeLayout.getHeight()))+" Height view00 "+String.valueOf(pxtodp(findViewById(R.id.view00).getHeight())));

    }
  private  int pxtodp(int px){
        return (int) (px/ Resources.getSystem().getDisplayMetrics().density);
    }
     void initial_total(final Dialog dialog){
        names=new ArrayList<String>(30);
        food_Times=new ArrayList<String>(30);

        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child(Address);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int t=0;
                 for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Food food=dataSnapshot1.getValue(Food.class);
                    DatabaseReference d=   FirebaseDatabase.getInstance().getReference().child("Food Items/"+Canteen+"/"+food.getType()
                            +"/"+food.getFood_name());
                    d.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Food food=dataSnapshot.getValue(Food.class);
                             try {
                        if (food.getTotal_orders()==0) {
                                food_Times.add("0");
                        }
                        else  food_Times.add(String.valueOf(food.getTotal_orders()));
                            }catch (Exception NullPointerException){
                                  food_Times.add("0");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    try {
                         t  = Integer.parseInt(String.valueOf(food.getPrice()))*Integer.parseInt(food.getQuantity()) + t ;

                        Final_Total=t;
                     Log.e("InitialTotal", String.valueOf(t));
                    }catch (Exception NumberFormatException){}
                    TextView textView=findViewById(R.id.total_final);
                    textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(Final_Total)));

                    records= (int) dataSnapshot.getChildrenCount();
                    totalfood=findViewById(R.id.total_quantity);
                    totalfood.setText(Integer.toString(records));
                    foodtype.add(food.getType());
                     img.add(food.getFood_Image());
                     names.add(food.getFood_name());
                }
                TextView textView;
                textView=findViewById(R.id.tax);
                textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), Tax));
                textView=findViewById(R.id.TotalAmount);

              //  Final_Total=Final_Total+Integer.parseInt(Tax)+Integer.parseInt(Other);
                Final_Total=Final_Total+Integer.parseInt(Tax);
                Log.e("Tax And other","Final_total when f+tax "+Final_Total);
                Other=String.valueOf ((Final_Total*Integer.parseInt(Other))/100);
                Log.e("Tax And other","Final_total other "+Other);

                Final_Total=Final_Total+Integer.parseInt(Other);
                Log.e("Tax And other","Final_total when f+other "+Final_Total);

                textView.setText(String.format("%s %s.00",
                        getString(R.string.rupeesSymbol), String.valueOf(Final_Total)));
                textView=findViewById(R.id.OtherPayment);
                textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), Other));
              try {
                  dialog.dismiss();
              }catch (Exception e){}
              }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    void setPaymentCredentials(){
        final Dialog dialog=showProgress_dialog_PRICE();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                assert map != null;
                try {
                    Tax = ((String) map.get("Tax"));
                    Other = ((String) map.get("Other"));
                    Log.e("Tax And other", Tax + "&" + Other);
                   initial_total(dialog);
                } catch (Exception NullPointerException) {
                    Tax = "0";
                    Other = "0";
                    Log.e("Tax And other", "Exception Tax="+Tax + "&" + Other);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    String getUOrderNumber(){

        Date date=Calendar.getInstance().getTime();
        SimpleDateFormat day=new SimpleDateFormat("yy");
        int year=24-Integer.parseInt(day.format(date));
        day=new SimpleDateFormat("MM");
        int month=12-Integer.parseInt(day.format(date));
        day=new SimpleDateFormat("dd");
        int Monthdays=31-Integer.parseInt(day.format(date));
        day=new SimpleDateFormat("HH");
        int hour=24-Integer.parseInt(day.format(date));

        day=new SimpleDateFormat("mmss");

        String[] orderno=new String[4];
        if (year<=9) {
            orderno[0]="0"+Integer.toString(year);
        }else
            orderno[0]=Integer.toString( year);
        if (month<=9) {
            orderno[1]="0"+Integer.toString(month);
        }else
            orderno[1]= Integer.toString(month);

        if (Monthdays<=9) {
            orderno[2]="0"+Integer.toString(Monthdays);
        }else
            orderno[2]= Integer.toString(Monthdays);
        if (hour<=9) {
            orderno[3]="0"+Integer.toString(hour);
        }else
            orderno[3]= Integer.toString(hour);

        Random random=new Random();
        //String ran= String.valueOf(random.nextInt(1000))+String.valueOf(random.nextInt(100));

         return orderno[0]+orderno[1]+orderno[2]+orderno[3]+day.format(date)+
                 String.valueOf(random.nextInt(100));

    }


    public   String dayName(String inputDate, String format){
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEEE").format(date);
    }
    public   String change(String dates){
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date date=null;
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

    @Override
    protected void onStart() {
        super.onStart();
        try {
          //  progressDialog.dismiss();
        }catch (Exception ignored){}
        findViewById(R.id.pay50).setEnabled(true);
    }

    void Load() {

        RecyclerView recyclerView=findViewById(R.id.recycler_orderfood);
        RecyclerView recyclerView1=findViewById(R.id.foodrecycler);

          LinearLayoutManager horizontal1=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView1.setLayoutManager(horizontal1);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager horizontal=new LinearLayoutManager(orderFood.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(horizontal);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
         setStyle();
        firebase( Address,recyclerView,R.layout.layoutorderfood_retry,1);
          horizontal=new LinearLayoutManager(orderFood.this,LinearLayoutManager.HORIZONTAL,false);
          recyclerView1=findViewById(R.id.foodrecycler);
        recyclerView1.setLayoutManager(horizontal);
        recyclerView1.setAdapter(adapter);
        //initial_total();

        firebase(Address,recyclerView1,R.layout.fororderfood,2);
    }
void firebase(String address, final RecyclerView recyclerView, int layout, final int type){
      DatabaseReference mDatabase1= FirebaseDatabase.getInstance().getReference().child(address);
      FirebaseRecyclerAdapter<Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
            Food.class,
            layout,
            mainactiv.FoodViewHolder.class,
            mDatabase1

    ) {
         @Override
        protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
            if(type==1)
            {

                Type=model.getType();
            if (Type.toUpperCase().equals("CAKE")){
                setCheckFor_Lunch();
            }
            viewHolder.setName(model.getFood_name());
            viewHolder.setPrice(String.valueOf(model.getPrice()));
                 try {
                    viewHolder.setQuantity(model.getQuantity());
                 }catch (Exception ignored){}
             }
             else {

                viewHolder.setImageID(model.getFood_Image());
                if (!model.getType().toUpperCase().contains("CAKE"))
                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(orderFood.this, food_info.class);
                        intent.putExtra("Image", model.getFood_Image());
                        intent.putExtra("Name", model.getFood_name());
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        intent.putExtra("Discount",String.valueOf(model.getPrice()));
                        // intent.putExtra("Favorite",model.getFavorite());
                        intent.putExtra("Hide",true);

                        intent.putExtra("Description", model.getDescription());
                        intent.putExtra("Type", model.getType());
                        //  intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                        //  intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                        // intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation
                                        (orderFood.this, viewHolder.getImageId(), "My_Animation");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(intent, optionsCompat.toBundle());
                        }
                    }
                });
              viewHolder.ruppes();

            }
        }
    };

    recyclerView.setAdapter(FBRA);


}

    void receive(){
        try {
            Intent intent=getIntent();
            Address=intent.getStringExtra("Address");
        String     DeliveryTime;
      DeliveryTime=  intent.getStringExtra("DeliveryTime");
          try {
              if (!DeliveryTime.isEmpty() || !DeliveryTime.equals("")){
                  Times=DeliveryTime;
                  deliveryFormat();
              }
          }catch (Exception ignored){}
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onBackPressed() {

         if (pop){
             popupWindow.dismiss();
          }
         else {
             if (!Address.toUpperCase().contains("CAKE"))
             {//startActivity(new Intent(orderFood.this,cartFragments.class));
                 super.onBackPressed();
           // herefinish finish();
             }else //finish
            super.onBackPressed();
         }
    }


    public void DeliveryTimeOnclick(View view) {
        view.setEnabled(false);
        RadioButton radioButton=findViewById(R.id.lunchtime);
        Times="";
        radioButton.setChecked(false);
        newDeliverypopup();

    }

    /** New layout for delivery time */
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
         }
        return Times;

    }
    public void deliveryFormat() {
        findViewById(R.id.deliveryformat).setVisibility(View.VISIBLE);

        //findViewById(R.id.RelativeDelivery).setVisibility(View.VISIBLE);
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

        } catch (Exception NullPointerException) {
        }

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
        pop=true;
        LayoutInflater layoutInflater = (LayoutInflater) orderFood.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View custom = layoutInflater.inflate(R.layout.deliverytime_popup, null);
          relativeLayout = findViewById(R.id.relative);
        popupWindow = new PopupWindow(custom, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
              findViewById(R.id.deliveryFormat).setEnabled(true);
            }
        });
        custom.findViewById(R.id.backpopupweight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        newdeliverypopup(custom);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pop=false;
                RadioButton  radioButton=findViewById(R.id.deliverlater);
                radioButton.setChecked(false);
                radioButton.setEnabled(true);
                findViewById(R.id.deliveryformat).setEnabled(true);
            }
        });

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
        //rlunch=findViewById(R.id.lunchtime);

        if (todaysdy.contains("Sunday")) {
            rlunch.setText("Tomorrow Lunch  ");
        }else{  if (Integer.parseInt(hour)>=11 && Integer.parseInt(hour)<=12) {
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
                    {   rlunch.setText("Monday Lunch");}
                    else rlunch.setText("Tomorrow Lunch");
                }else
                    Custom.findViewById(R.id.radioGroupLunch_fromLunch11).setVisibility(View.GONE);

            }else if (Integer.parseInt(hour)>=13){
                if (todaysdy.equals("Saturday"))
                    rlunch.setText("Monday Lunch");
                else rlunch.setText("Tomorrow Lunch");

            }else{


            }

        }

         ((RadioGroup) Custom.findViewById(R.id.radioGroupLunch)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                 pop=false;
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
        if (Type.toUpperCase().contains("CAKE")) {
            /*if (todaysdy.toUpperCase().contains("SATURDAY")) {
                rlunch.setText("Monday Lunch");
            } else
            {
                rlunch.setText("Tomorrow Lunch");
            }*/
            if (todaysdy.contains("Saturday")) {
                rlunch.setText("Monday Lunch");
            }else if  (todaysdy.contains("Sunday")) {
                rlunch.setText("Tomorrow Lunch  ");
            }else{
                rlunch.setText("Tomorrow Lunch");

            }

            Custom.findViewById(R.id.radioGroupLunch_fromLunch11).setVisibility(View.VISIBLE);
            Custom.findViewById(R.id.radioGroupLunch_fromLunch12).setVisibility(View.VISIBLE);
            Custom.findViewById(R.id.TodayText).setVisibility(View.GONE);
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
      //  popup="nn";

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
                pop=false;
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
                            pop=false;

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
                pop=false;

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
                            pop=false;

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
                pop=false;

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
                            pop=false;

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
                            pop=false;

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
                pop=false;

                RadioGroup radioGroup1=custom.findViewById(R.id.RadioGroupFourth);
                ExtractDeliveryTime(radioGroup1.
                        getChildAt(radioGroup1.indexOfChild(radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId()))).
                        toString().trim(),custom,fourth_date,"no","5", "30");
            }
        });



    }

    public void LunchOnclick(View view) {
        RadioButton radioButton1= findViewById(R.id.deliverlater);
        radioButton1.setChecked(false);
       RadioButton radioButton=findViewById(R.id.lunchtime);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        Calendar calendar=Calendar.getInstance();
        String time=date.format(calendar.getTime());
        if (radioButton.getText().toString().trim().contains("Tomorrow")){
          Times= "12:50 ("+change(time)+")";
        }else if (radioButton.getText().toString().trim().toUpperCase().contains("MONDAY"))
        {
            Times= "12:50 ("+change(change(time))+")";
        }else
        {
            Times= "12:50 ("+ time+")";
        }
    }

    Dialog showProgress_dialog_PRICE( ){
        final View dialogView = View.inflate(orderFood.this, R.layout.progress_layout, null);

        Dialog  progressDialog1 = new Dialog(orderFood.this);
        progressDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog1.setContentView(dialogView);

        progressDialog1.setCanceledOnTouchOutside(false);

        progressDialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    finish();
                    return true;
                }

                return false;
            }
        });


        progressDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog1.show();

    return progressDialog1;
    }

    void showProgress_dialog( ){
        final View dialogView = View.inflate(orderFood.this, R.layout.progress_layout, null);

         progressDialog = new Dialog(orderFood.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(dialogView);

        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    finish();
                    return true;
                }

                return false;
            }
        });


        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            progressDialog.dismiss();
        }catch (Exception ignored){}

    }

 }
