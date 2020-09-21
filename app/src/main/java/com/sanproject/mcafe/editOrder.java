package com.sanproject.mcafe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.constant.constants.Canteen;


public class editOrder extends AppCompatActivity {
    private adapter_allorders adapter;
    boolean delivery_change=false;
    boolean cart=false;
    Dialog progressDialog;
    TextView order, delivery;
    private String Status;
    private String Total_Food;
    private String OrderNo;
    ProgressBar progressBar;
    String checksum;
    String OriginalDelivery;
    //public String Mid = "MITThe83163602940912";
   // public String Mid = "";
  //  public String Industry = "Retail";
   // public String Industry = "";
  //  public String ChannelId = "WAP";
  //  public String ChannelId = " ";
    public String Amount;
   // public String Callback = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
  //  public String Callback = "";
    private byte Today;
    private String type="";
   // public String WEBSITE = "APPSTAGING";
  //  public String WEBSITE = "";


     Button save, cancel, remainPay;
    private FirebaseAuth auth;

    private DatabaseReference data;
    private ArrayList<String> foodname;
    private ArrayList<String> foodQ;
    private int Total_price;
    private ArrayList<String> foodprice;
    private int pay;
    private String Deliver;
    private TextView Time;
    private String From;
    private PopupWindow popupWindow;
    private String popup="";
    private String day;
    private PopupWindow popupWindow1;
    private String Times;
    private ArrayList<String> saveData;
    private int tax;
    private int other;
    private int tax_per;

    public editOrder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        showProgress_dialog(true);
        saveData = new ArrayList<String>();
         save = findViewById(R.id.save_food);
        cancel = findViewById(R.id.cancel2);
        progressBar = findViewById(R.id.payProgress);
        progressBar.setVisibility(View.GONE);

        remainPay = findViewById(R.id.remainingPay);

        remainPay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        foodname = new ArrayList<String>();
        foodQ = new ArrayList<String>();
        foodprice = new ArrayList<String>();
        Time = findViewById(R.id.time);
        ReceiveData();
        ////
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference foodDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/Orders/All orders/" + OrderNo);
        extractData();
        extract_Data_from_Database_(foodDatabase,true);
        ////
        data = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        findViewById(R.id.deliveryFormat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliverypopup();
                checkForVisibility_save();
            }
        });
        findViewById(R.id.backpopupweight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTaskRoot())
                {   startActivity(new Intent(editOrder.this,MainActivity.class));
                }
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view, "cancel");


            }
        });
        remainPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (cancel.getVisibility()!=View.VISIBLE){
                    Load_Data_from_server(false);
                }else  Load_Data_from_server(true);
                cancel.setVisibility(View.INVISIBLE);
                view.setVisibility(View.INVISIBLE);
                //initializePaytmPayment();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(  isNetworkAvailable(getApplicationContext()))
                open(view, "save");
            else Toast.makeText(editOrder.this,"Failed to update the Order. Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });
        setchage_From_Database();
        check_notification();
    }
    void check_notification(){
        FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications/"+OrderNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                 if (!Objects.equals(dataSnapshot.child("Status").getValue(), "SEEN")){
                        FirebaseDatabase.getInstance().getReference("User Informations/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                .getUid()+"/Notifications/"+OrderNo).child("Status").setValue("SEEN");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void cancel() {
         TextView textView=findViewById(R.id.PaymentStatus1);

        data.child("Orders").child(Canteen).child("New Orders").child(OrderNo).child("Pay_Return_OnCancel").setValue("no "+ExtractNumber(textView.getText().toString().trim()));
        data.child("Orders").child(Canteen).child("New Orders").child(OrderNo).child("Status").setValue("Cancel_user");
        data.child("Orders").child(Canteen).child("All Orders").child(OrderNo)
                .child("Status").setValue("Cancel_user");
        data.child("Orders").child(Canteen).child("All Orders").child(OrderNo)
                .child("Pay_Return_OnCancel").setValue("no "+ExtractNumber(textView.getText().toString().trim()));


        data.child("User Informations").child(auth.getCurrentUser().getUid()).
                child("Orders/All orders").child(OrderNo).child("Status").setValue("Cancel_user");

        data.child("User Informations").child(auth.getCurrentUser().getUid()).
                child("Orders/Pending").child(OrderNo).child("Status").setValue("Cancel_user");


        data.child("User Informations").child(auth.getCurrentUser().getUid()).
                child("Orders/All orders").child(OrderNo).child("Pay_Return_OnCancel").
                setValue("no "+ExtractNumber(textView.getText().toString().trim()));


        store_extra_info("You have cancelled this order");
        sendToTransaction("0",FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/"+"Pending_Payments").child(OrderNo),
                FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/" + OrderNo),true);

        Toast.makeText(editOrder.this, " Order cancelled successfully", Toast.LENGTH_SHORT).show();

        finish();
    }
    void check_Payment_(DatabaseReference data,int totalAmount ){
       /* */

    }
   /* void save_tax(){
        FirebaseDatabase.getInstance().getReference("Tax/"+OrderNo).child("Tax").setValue(other);
    }*/

    void save() {
        Total_price = 0;
        Log.e("Check_Save()_edit","Outside Check payment inside save Total_food"+Total_Food+" Q"+foodQ.get(0)+" price "+foodprice.get(0)+" Total price "+Total_price);
    int size=foodprice.size();

        {
            try {
                for (int i = 0; i < size; i++) {
                    Total_price = Total_price + Integer.parseInt(foodQ.get(i)) * Integer.parseInt(foodprice.get(i));
                    Log.e("Check_Save()_edit", "Check payment inside save Total_food" + Total_Food + " Q" + foodQ.get(i) + " price " + foodprice.get(i) + " Total price " + Total_price);
                }
            } catch (Exception ignored) {
            }
            Total_price = Total_price + tax + other;
            for (int i = 0; i < size; i++) {

                data.child("Orders/"+Canteen+"/"+"New Orders/" + OrderNo + "/Food/" + foodname.get(i) + "/Quantity").setValue(foodQ.get(i));
                data.child("Orders/"+Canteen+"/"+"All Orders/" + OrderNo + "/Food/" + foodname.get(i) + "/Quantity").setValue(foodQ.get(i));
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/All orders/"
                        + OrderNo + "/Food/" + foodname.get(i) + "/Quantity").setValue(foodQ.get(i));
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/Pending/"
                        + OrderNo + "/Food/" + foodname.get(i) + "/Quantity").setValue(foodQ.get(i));
            }
            data.child("Orders/"+Canteen+"/"+"New Orders/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
            data.child("Orders/"+Canteen+"/"+"All Orders/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
            data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/All orders/"
                    + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
            data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/Pending/"
                    + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));

            data.child("Orders/"+Canteen+"/"+"New Orders/" + OrderNo + "/OtherPayment").setValue(Integer.toString(other));
            data.child("Orders/"+Canteen+"/"+"All Orders/" + OrderNo + "/OtherPayment").setValue(Integer.toString(other));
            data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/All orders/"
                    + OrderNo + "/OtherPayment").setValue(Integer.toString(other));
            data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/Pending/"
                    + OrderNo + "/OtherPayment").setValue(Integer.toString(other));

            check_Payment_(data, Total_price);
            if (OriginalDelivery.equals(Times)) {
                data.child("Orders/"+Canteen+"/"+"New Orders/" + OrderNo + "/Delivery").setValue(Times);
                data.child("Orders/"+Canteen+"/"+"All Orders/" + OrderNo + "/Delivery").setValue(Times);
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/All orders/"
                        + OrderNo + "/Delivery").setValue(Times);
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/Pending/"
                        + OrderNo + "/Delivery").setValue(Times);
            } else {
                data.child("Orders/"+Canteen+"/"+"New Orders/" + OrderNo + "/Delivery").setValue(Times);
                data.child("Orders/"+Canteen+"/"+"All Orders/" + OrderNo + "/Delivery").setValue(Times);
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/All orders/"
                        + OrderNo + "/Delivery").setValue(Times);
                data.child("User Informations/" + auth.getCurrentUser().getUid() + "/Orders/Pending/"
                        + OrderNo + "/Delivery").setValue(Times);
            }
         /*   if (delivery_change) {
                showProgress_dialog(true);
                Log.e("Store Extra Info","Delivery change");
                store_extra_info_del("Delivery time changed from "+OriginalDelivery+" to "+Times);
                delivery_change=false;
            }else{
                Log.e("Store Extra Info","Delivery not change "+delivery_change);

            }*/
          //  save_tax();
            if (cart && delivery_change) {
                showProgress_dialog(true);

                Log.e("Store Extra Info","cart change");
                store_extra_info("Food items changed"+DateFormat.getDateTimeInstance().
                        format(new Date())+"\n\u2022  Delivery time changed from "+OriginalDelivery+" to "+Times);
                cart=false;

            }else if (cart){

                showProgress_dialog(true);

                store_extra_info("Cart items changed");
            }else if (delivery_change){
                showProgress_dialog(true);

                store_extra_info("Delivery time changed from "+OriginalDelivery+" to "+Times);
                delivery_change=false;

            }


            else{
                Log.e("Store Extra Info","cart not change "+cart);

            }
            findViewById(R.id.save_food).setVisibility(View.GONE);

        }
    }
   void calculate_tax_per(int other,int remainpay){

try {
    tax_per=remainpay/other;
}catch (Exception e){
    tax_per=0;
}
        Log.e("Tax","other "+other+" total Price "+remainpay+" tax_per "+tax_per);
    }
    void updateTrasaction(DatabaseReference d,String TransactionNO,String bank,String date){
        d.child("OrderNo").setValue(OrderNo);
        d.child("Time").setValue(date);

        d.child("Transactions/"+TransactionNO).child("TransactionNumber").setValue(TransactionNO);
        d.child("Transactions/"+TransactionNO).child("OrderNo").setValue(OrderNo);
        d.child("Transactions/"+TransactionNO).child("Payment").setValue(Amount);
        d.child("Transactions/"+TransactionNO).child("Mode").setValue("Paytm");
        d.child("Transactions/"+TransactionNO).child("Bank").setValue(bank);
        d.child("Transactions/"+TransactionNO).child("Time").setValue(date);
        d.child("Transactions/"+TransactionNO).child("ADMINSTATUS").setValue(false);
    }
    void sendToTransaction(final String Tax, final DatabaseReference to, DatabaseReference from, final boolean delete){
        from.
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        {
                            to.
                                    setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                            if (firebaseError != null) {
                                                Log.e("Moving node", "Move not moved ");
                                            } else {
                                                to.  child("OtherPayment").setValue(Tax).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (delete)
                                                            FirebaseDatabase.getInstance().
                                                                    getReference("Transactions/"+Canteen+"/" + OrderNo).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendToTransaction(String.valueOf(other),FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/"+"Payments").child(OrderNo),
                                                                            FirebaseDatabase.
                                                                                    getInstance().getReference("Transactions/"+Canteen+"/"+"Pending_Payments").child(OrderNo)
                                                                            ,false);

                                                                }
                                                            });

                                                    //    finish();
                                                    }
                                                });


                                            }
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Moving node", "Move gives an error " + databaseError.toString());
                    }
                });
    }

    void storeTransaction(final String OrderNo, final String Amount, final String date, final String TransactionNO, final String bank) {
          DatabaseReference da = FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/" + OrderNo );
        da.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()||dataSnapshot.exists()){
                     DatabaseReference d = FirebaseDatabase.getInstance()
                             .getReference("Transactions/"+Canteen+"/" + OrderNo + "/Transactions/" + TransactionNO);
                    d.child("TransactionNumber").setValue(TransactionNO);
                    d.child("OrderNo").setValue(OrderNo);
                    d.child("Payment").setValue(Amount);
                    d.child("Mode").setValue("Paytm");
                    d.child("Bank").setValue(bank);
                    d.child("Time").setValue(date);
                    data.child("Transactions/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
                }else {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                        getReference("Transactions/"+Canteen+"/"+"Pending_Payments/" + OrderNo);
                       updateTrasaction(databaseReference,TransactionNO,bank,date);
                    data.child("Transactions/Pending_Payments/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
                    updateTrasaction(FirebaseDatabase.getInstance().
                            getReference("Transactions/"+Canteen+"/"+"Payments/" + OrderNo),TransactionNO,bank,date);
                    data.child("Transactions/"+Canteen+"/"+"Payments/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void showProgress_dialog(boolean enable){
        ;// = new Dialog(editOrder.this);

      try {
          if (enable) {
              progressDialog = new Dialog(editOrder.this);
              final View dialogView = View.inflate(editOrder.this, R.layout.progress_layout, null);

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
              Log.e("Progress_dialog1","Inside if to show");

              progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
              progressDialog.show();
          }else{  Log.e("Progress_dialog1","Inside else to dismiss");
              progressDialog.dismiss();
          }
      }catch (Exception e){}

    }
    private void ReceiveData() {
        try {
            Intent intent = getIntent();
            //Payment= intent.getStringExtra("Payment");
            //Status = intent.getStringExtra("Status");
           // Total_Food = intent.getStringExtra("Total_Food");
            //Delivery = intent.getStringExtra("Delivery");
            OrderNo = intent.getStringExtra("OrderNo");
            From = intent.getStringExtra("From");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void open(View view, final String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to " + Message + " the order");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
             @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 if (Message.equals  (  "save")) {
                    save();
                } else cancel();
            }
        });
        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //   Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        switch (popup) {
            case "y":
                popupWindow.dismiss();
                popup = "n";
                break;
            case "nn":
                popupWindow1.dismiss();
                popup = "y";
                break;
            default:
                if (From.contentEquals("Orders")) {
                    finish();
                    //    super.onBackPressed();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }


    }

    public void setStatus(String status) {
        Status = status;
        int drawable;
        if (status.contains("Accepted")) {
            drawable = R.drawable.acceptedstatus_background;
            findViewById(R.id.cancel2).setVisibility(View.INVISIBLE);
        } else if (status.contains("Completed")) {
            findViewById(R.id.cancel2).setVisibility(View.INVISIBLE);

            drawable = R.drawable.completedstatus_background;
        } else if (status.contains("Delivered")) {
            findViewById(R.id.cancel2).setVisibility(View.INVISIBLE);

            drawable = R.drawable.deliveredstatus_background;
        } else if (status.contains("Cancel")) {
            Status="Cancel";
            remainPay.setVisibility(View.VISIBLE);
            drawable = R.drawable.cancelledstatus_background;
            findViewById(R.id.cancel2).setVisibility(View.INVISIBLE);
         } else {
            drawable = R.drawable.statusbackground;
        }
        TextView food_name = (TextView) findViewById(R.id.Orderstatus1);
        food_name.setBackgroundResource(drawable);

        food_name.setText(Status);
        setExtraInfo(status);
    }
    private int ExtractNumber(String text){
        if (text.contains(".")){
            text=text.substring(0,text.indexOf("."));
        }
        String number=text.replaceAll("[^0-9]","");
        if (number.equals(""))
            number="0";
         return Integer.parseInt(number);
    }

    void extractData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Informations/" + mAuth.getCurrentUser().getUid() + "/Orders/All orders/" + OrderNo + "/Food");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodQ=new ArrayList<>();
                foodname=new ArrayList<>();
                foodprice=new ArrayList<>();
                saveData=new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Food food = dataSnapshot1.getValue(Food.class);
                    assert food != null;
                 try {
                     saveData.add(food.getQuantity());


                    foodname.add(food.getFood_name());
                     foodprice.add(String.valueOf(food.getPrice()));
                     foodQ.add(food.getQuantity());
                     Log.e("Edit Order","foodname "+food.getFood_name()+" foodprice"+food.getPrice());
                }catch (Exception ignored){
                     Toast.makeText(editOrder.this,"Error occured. Please retry",Toast.LENGTH_SHORT).show();
                     finish();
                 }
                }

//                SetTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void setExtraInfo(String status){
        TextView textView=findViewById(R.id.extraInfo);
        if (status.toUpperCase().contains("CANCEL") && status.toUpperCase().contains("CANTEEN"))
            textView.setText(R.string.order_cancelled_By_canteen);
            else if (status.toUpperCase().contains("CANCEL") && status.toUpperCase().contains("USER"))
                textView.setText(R.string.order_cancel_byClint);
        else if (status.toUpperCase().contains("CANCEL"))
            textView.setText(R.string.order_cancel_byClint);
            else if (status.toUpperCase().contains("PENDING"))
                textView.setText(R.string.order_status_pending);
            else if (status.toUpperCase().contains("ACCEPTED"))
            textView.setText(R.string.order_status_accepted);
        else if (status.toUpperCase().contains("COMPLETED"))
            textView.setText(R.string.order_status_completed) ;

        else if (status.toUpperCase().contains("DELIVERED"))
            textView.setText(R.string.order_status_deliverd);

    }
    void extract_Data_from_Database_(final DatabaseReference FoodDatabase, final boolean load){
        final RecyclerView recyclerView = findViewById(R.id.recycler_editorder);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        FoodDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //showProgress_dialog(false);
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Total_Food = (String) map.get("TotalFood");
                Log.e("Check_Save()_edit","Total_Food is "+Total_Food);
                order = findViewById(R.id.orderno1);
                int totalpay=Integer.parseInt((String) map.get("TotalAmount"));
                pay = (totalpay) -
                        Integer.parseInt((String) map.get("Payment"));
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    remainPay.setCompoundDrawables(null,null,null,null);

                }
                    if (pay == 0) {
                    remainPay.setVisibility(View.INVISIBLE);
                } else if (pay < 0) {
                    remainPay.setText(String.format(Locale.ENGLISH,"%s%d from the counter", getString(R.string.collect_extra_payment), pay * (-1)));
                    remainPay.setCompoundDrawables(null,null,null,null);


                    remainPay.setClickable(false);
                } else remainPay.setText(String.format(Locale.ENGLISH,"%s ₹%d", getString(R.string.pay_remaining), pay));
                order.setText((String) map.get(getString(R.string.OrderNo)));
                order = findViewById(R.id.PaymentStatus1);
                order.setText(String.format("%s %s.00", getString(R.string.remaining_), map.get("Payment")));
                 OriginalDelivery=(String) map.get("Delivery");
                Times=(String) map.get("Delivery");
                deliveryFormat();
                try {
                    Time.setText((String) map.get("Time"));
                }catch (Exception ignored){}
                Status = (String) map.get("Status");
                if (!Status.toUpperCase().contains("PENDING")){
                    findViewById(R.id.deliveryFormat).setClickable(false);

                }
                if (Status.contains("Cancel")) {
                    try {String IsPayReturn=(String) map.get("Pay_Return_OnCancel");
                        if (IsPayReturn.toUpperCase().contains("YES")) {
                            remainPay.setText(String.format("You have received" +
                                    " %s%s", getString(R.string.rupeesSymbol), map.get("Payment")));
                        } else {remainPay.setVisibility(View.VISIBLE);
                            remainPay.setText(String.format("%s%s from counter",
                                    getString(R.string.collect_extra_payment), map.get("Payment")));
                        }
                    }catch (Exception NullPointerException){
                        remainPay.setText(String.format("%s %s from counter",
                                getString(R.string.collect_extra_payment), map.get("Payment")));
                    }
                    remainPay.setCompoundDrawables(null,null,null,null);
                    remainPay.setClickable(false);
                }
                setStatus(Status);
                order = findViewById(R.id.total_final2);
                int TotalFinal=Integer.parseInt((String) map.get("TotalAmount"));
                order.setText(String.format("%s %s.00",getString(R.string.rupeesSymbol),String.valueOf(TotalFinal)));
              try {
                  tax=Integer.parseInt((String)map.get("Tax"));
              }catch (Exception e){
                  tax=0;
              }
               try {
                  other=Integer.parseInt((String)map.get("OtherPayment"));
               }catch (Exception e){
                  other=0;
               }
                order=findViewById(R.id.PayAmount);
                try {
                    TextView textView=findViewById(R.id.Infos_for_order);
                    String message=(String)map.get("Extra_Info");
                    if (!message.isEmpty()){
                        textView.setText(message);
                        textView.setVisibility(View.VISIBLE);

                    }

                }catch (Exception ignored){

                }
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(TotalFinal - tax - other)));
                order=findViewById(R.id.tax);
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(tax)));
                order=findViewById(R.id.OtherPayment);
                order.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(other)));
                try{
                    check_Return_payment((String) dataSnapshot.child("Extra_Payment_Return").getValue().toString().trim().toUpperCase(),Status);
                }catch (Exception ignored){}
              if (load){ LoadFirebase(FoodDatabase,recyclerView);}
                calculate_tax_per(other,totalpay-tax);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            private void check_Return_payment(String pay,String status) {
                try {status=status.toUpperCase();
                    if(pay.contains("YES")) {
                        Log.e( "Retrun payment" ,"Inside YES status"+status+" pay "+pay);
                        TextView textView = findViewById(R.id.remainingPay);
                        if ( (!status.equals("PENDING"))) {
                            Log.e( "Retrun payment" ,"Inside not pending status"+status+" pay "+pay);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.format(Locale.ENGLISH,"You have received %s%d from the canteen"
                                    , getString(R.string.rupeesSymbol), ExtractNumber(pay)));
                        }  else{Log.e( "Retrun payment" ,"Inside   pending status"+status+" pay "+pay);
                            TextView textView1=findViewById(R.id.extraInfo);
                            textView1.append(String.format(Locale.ENGLISH,"\n%s You have received %s%d from the canteen"
                                    ,getString(R.string.pointer), getString(R.string.rupeesSymbol), ExtractNumber(pay)));
                        }
                    }else if (pay.contains("NO")){remainPay.setVisibility(View.VISIBLE);
                        remainPay.setText(String.format("%s%s from counter",
                                getString(R.string.collect_extra_payment), ExtractNumber(pay)));
                        remainPay.setCompoundDrawables(null,null,null,null);
                        remainPay.setClickable(false);
                    }
                }catch (Exception e){Log.e( "Retrun payment" ,
                        "Inside catch   status"+status+" pay "+pay+" Error"+e.toString());

                }
            }

        });

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

    void LoadFirebase(DatabaseReference FoodDatabase,RecyclerView recyclerView){
        recyclerView.setNestedScrollingEnabled(false);

        FoodDatabase = FoodDatabase.child("Food");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.layouteditorder_retry,
                mainactiv_allorders.FoodViewHolder.class,
                FoodDatabase
        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
                 Log.e("Parsing exception check","Inside firebase");
            try {
                showProgress_dialog(false);
            }catch (Exception ignored){}
                type=model.getType();
                if (!Status.contains("Pending")) {
                    viewHolder.visibilityfor_Buttons();
                 }
                 if (model.getType().toUpperCase().contains("CAKE"))
                {
                     if (!model.getType().toUpperCase().contains("EXTRAS"))
                {             viewHolder.mView.findViewById(R.id.multiply).setVisibility(View.VISIBLE);

                    viewHolder.inc().setVisibility(View.GONE);
                    viewHolder.dec().setVisibility(View.GONE);}
                }
                 if (model.getFood_name().toUpperCase().contains("SHAPE")&&model.getFood_name().toUpperCase().contains("CANDLE"))
                {
                    viewHolder.inc().setVisibility(View.GONE);
                    viewHolder.dec().setVisibility(View.GONE);
                }
                 viewHolder.setFood_name(model.getFood_name());
                 viewHolder.setQuantity(model.getQuantity());
                 viewHolder.setprice(getString(R.string.rupeesSymbol)+" "+String.valueOf(model.getPrice()));
                 viewHolder.inc().setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View view) {
                         viewHolder.setQuantity(storeQunatity(model.getFood_name(), 'a'));
                         checkForVisibility_save();
                         SetTotal();
                     }
                });

                viewHolder.dec().setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View view) {

                        viewHolder.setQuantity(storeQunatity(model.getFood_name(), 's'));
                        checkForVisibility_save();
                        SetTotal();

                    }


                });
                Log.e("Parsing exception check","13.");
            }

        };
        recyclerView.setAdapter(FBRA);
    }
    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.payProgress).setVisibility(View.GONE);// payProgress
        progressBar.setVisibility(View.GONE);
      //  remainPay.setVisibility(View.VISIBLE);


    //    FirebaseAuth mAuth = FirebaseAuth.getInstance();
     //   FoodDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/Orders/All orders/" + OrderNo);
    //    extractData();
      //   extract_Data_from_Database_(FoodDatabase,true);

    }
    void undo_notification(){
        FirebaseDatabase.getInstance().getReference("User Informations/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Notifications/"+OrderNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference("User Informations/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Notifications")
                                    .child(OrderNo+"/Status").setValue("SEEN");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void setchage_From_Database(){
         FirebaseDatabase.getInstance().getReference().child("User Informations/" +
                 FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Orders/All orders/" + OrderNo)
                 .addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            extractData();
            extract_Data_from_Database_(FirebaseDatabase.getInstance().getReference().child("User Informations/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Orders/All orders/" + OrderNo),false);
            undo_notification();
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


    private void SetTotal() {
        Total_price = 0;
        for (int i = 0; i < foodprice.size(); i++) {
            Log.e("Edit Order","Total_price price "+foodprice.get(i)+" foodQ "+foodQ.get(i));
            Total_price = Total_price + Integer.parseInt(foodprice.get(i)) * Integer.parseInt(foodQ.get(i));
        }

        TextView textView=findViewById(R.id.PayAmount);
        Total_price=Total_price+tax;
        other=(Total_price*tax_per)/100;
        Total_price=Total_price+other;
        textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(Total_price - tax - other)));
          textView=findViewById(R.id.total_final2);
        textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(Total_price)));
        textView=findViewById(R.id.OtherPayment);
        textView.setText(String.format("%s %s.00", getString(R.string.rupeesSymbol), String.valueOf(other)));
    }

     private String storeQunatity(String food_name, char s) {
        int j = foodname.indexOf(food_name);
        if ( s =='a'){
            foodQ.set(j, Integer.toString(Integer.parseInt(foodQ.get(j)) + 1));
            if (foodQ.get(j).equals("15"))
                foodQ.set(j, "1");

        } else {
            foodQ.set(j, Integer.toString(Integer.parseInt(foodQ.get(j)) - 1));

            if (foodQ.get(j).contains("0"))
                foodQ.set(j, "1");

        }
        return foodQ.get(j);


    }
   void Extract_payment_Status_from_trasaction(final String TransactionNo, final String bank){

       FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/"+OrderNo+"/Transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pay=0;
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    pay =Integer.parseInt((String) dataSnapshot1.child("Payment").getValue());

                }
                payment(TransactionNo,bank,pay,false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
   }
    void payment(String TrasactionNo, String Bank,int payment,boolean status) {
        Total_price = 0;
        for (int i = 0; i <foodprice.size(); i++)
            Total_price = Total_price + Integer.parseInt(foodprice.get(i)) * Integer.parseInt(foodQ.get(i));

        TextView textView=findViewById(R.id.PaymentStatus1);


     try {
         if (status)
         {
             //payment=Integer.parseInt(ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),"")))+payment;
             payment=ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),""))+payment;

             Total_price=  payment;
         }else{
        Total_price=     payment;
         }
     }catch (Exception e){
         Extract_payment_Status_from_trasaction(TrasactionNo,Bank);

     }
        data.child("User Informations").child(auth.getCurrentUser().
                getUid()).child("Orders/All orders").child(OrderNo).child("Payment")
                .setValue(Integer.toString(Total_price));

        data.child("User Informations").child(auth.getCurrentUser().getUid()).
                child("Orders/Pending").child(OrderNo).child("Payment").
                setValue(Integer.toString(Total_price));

        data.child("Orders").child(Canteen).child("New Orders").child(OrderNo).
                child("Payment").setValue(Integer.toString(Total_price));
        data.child("Orders").child(Canteen).child("All Orders").child(OrderNo).child("Payment").
                setValue(Integer.toString(Total_price));

      //  data.child("Transactions/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));

        storeTransaction(OrderNo, String.valueOf(pay), DateFormat.getDateTimeInstance().format(new Date()), TrasactionNo, Bank);
        store_extra_info("Payment of ₹"+String.valueOf(pay)+" is received by the canteen");
        showProgress_dialog(true);
        Toast.makeText(editOrder.this, "Payment Successful", Toast.LENGTH_SHORT).show();

    }
    void store_extra_info(final String message){
        Log.e("Store Extra Info","Inside store extra info "+message);
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Orders/"+Canteen+"/"+"All Orders/"+OrderNo);
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.child("Extra_Info").exists()){
                databaseReference.child("Extra_Info").setValue(dataSnapshot.child("Extra_Info").getValue()+"\n\u2022  "+message+" "+DateFormat.getDateTimeInstance().format(new Date()));
              FirebaseDatabase.getInstance().getReference("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Orders/All orders/"+OrderNo+"/Extra_Info")
                      .setValue(dataSnapshot.child("Extra_Info").getValue()+"\n\u2022  "+message+" "+
                          DateFormat.getDateTimeInstance().format(new Date()));
                  Log.e("Store Extra Info","Extra info exists stored new info");
              }else{
                  databaseReference.child("Extra_Info").setValue("\u2022  "+message+" "+
                          DateFormat.getDateTimeInstance().format(new Date()));
                  FirebaseDatabase.getInstance().getReference("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Orders/All orders/"+OrderNo+"/Extra_Info")
                          .setValue("\u2022  "+message+" "+
                          DateFormat.getDateTimeInstance().format(new Date()));
                  Log.e("Store Extra Info","Extra info not exists stored new info");
              }
              showProgress_dialog(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Store Extra Info","Inside store extra info cancelled "+databaseError.toString());
            }
        });
    }

    void Load_Data_from_server(final boolean IscancelVisible){
        initializePayment();

//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm");
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChildren()) {
//                String    Callback = (String) dataSnapshot.child("Callback").getValue();
//                 String   Mid = (String) dataSnapshot.child("Mid").getValue();
//                 String   WEBSITE = (String) dataSnapshot.child("WEBSITE").getValue();
//                 String   ChannelId = (String) dataSnapshot.child("ChannelId").getValue();
//                 String   Industry = (String) dataSnapshot.child("Industry").getValue();
//                    boolean isStaging = (boolean) dataSnapshot.child("Type").getValue();
//
//                    Log.e("Paytem_Initializations", "Callback " + Callback + "Mid" + Mid + "Website" + WEBSITE + "Industry" + Industry);
//                    initializePaytmPayment(isStaging,IscancelVisible,Callback,Mid,WEBSITE,ChannelId,Industry);
//                }else{
//                    finish();
//                    Toast.makeText(editOrder.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//              //  finish();
//                progressBar.setVisibility(View.INVISIBLE);
//                remainPay.setVisibility(View.VISIBLE);
//                if (IscancelVisible)
//                    cancel.setVisibility(View.VISIBLE);
//
//                Toast.makeText(editOrder.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    String CreateCheckSum(final boolean PaytmScreen, final String orderno, final boolean isstaging, final boolean IscancelVisible,
                          final String Callback, final String Mid, final String WEBSITE, final String ChannelId, final String Industry){
        Map<String, String> paramMap = new HashMap<String, String>();
        String CustId = auth.getCurrentUser().getUid();

        paramMap.put("MID",Mid );
        paramMap.put("ORDER_ID", orderno);
        paramMap.put("CUST_ID",  CustId);
        paramMap.put("INDUSTRY_TYPE_ID", Industry);
        paramMap.put("CHANNEL_ID", ChannelId);
        paramMap.put("TXN_AMOUNT", Amount);
        paramMap.put("WEBSITE",      WEBSITE);
        paramMap.put("CALLBACK_URL",  Callback+orderno);
      SharedPreferences shared= getApplicationContext().getSharedPreferences( "User_Credentials",MODE_PRIVATE);
       String mobile=shared.getString("MobileNumber","");
       if (mobile.isEmpty()){
           mobile="NoNumber";
       }
        paramMap.put("MOBILE_NO", mobile);

        String Url="https://sanjay9425sm.000webhostapp.com/Paytm/checksum.php";
        //  String Url="https://fir-a-1fd3e.firebaseapp.com/Paytm/node/router.js";
        JSONObject param=new JSONObject(paramMap);



        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.DEPRECATED_GET_OR_POST, Url, param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(JSONObject response) {
                checksum=response.optString("CHECKSUMHASH");
                if (PaytmScreen){
                    if (checksum.trim().length()!=0){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!isDestroyed())
                                onStartTransaction(orderno,isstaging,IscancelVisible,
                                        Callback,Mid,WEBSITE,ChannelId,Industry );
                        }
                    }else{
//                        finish();
                        progressBar.setVisibility(View.INVISIBLE);
                        remainPay.setVisibility(View.VISIBLE);
                       if (IscancelVisible)
                           cancel.setVisibility(View.VISIBLE);
                        Toast.makeText(editOrder.this,"Loading failed , please retry",Toast.LENGTH_SHORT).show();
                        //   Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

                    }
                    Log.e("Paytm Debugging", "Checksum received starting transaction");

                }else{
                    transactionStatusAPI(orderno,isstaging,Mid);
                }
                Log.e("getresponse",String.valueOf(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.INVISIBLE);

                remainPay.setVisibility(View.VISIBLE);
          if (IscancelVisible)
              cancel.setVisibility(View.VISIBLE);
                //  finish();
                 Toast.makeText(editOrder.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
                Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

            }

        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
        return null;

    }
    private void initializePaytmPayment(boolean isstaging, boolean IscancelVisible, String callback,
                                        String mid, String WEBSITE, String channelId, String industry){
        Intent intent=new Intent(this,payment.class);

//        Random random = new Random();
//        Amount = String.valueOf(pay);
//
//        final String OrderN = OrderNo + "Repay" + random.nextInt(1000);
//        CreateCheckSum(true,OrderN,isstaging,IscancelVisible,callback,mid,WEBSITE,channelId,industry);

    }

    @Override
    protected void onResume() {
        super.onResume();
        remainPay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        remainPay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    void initializePayment(){
        String Time_Instant = DateFormat.getDateTimeInstance().format(new Date());
        Intent intent = new Intent(this, payment.class);
                Random random = new Random();
        final String OrderN = OrderNo + "Repay" + random.nextInt(1000);
        intent.putExtra("Order", OrderN);
        intent.putExtra("DeliveryTime", Times);
//        SetTotal();
        int payment;
        TextView textView=findViewById(R.id.total_final2);
        //payment=Integer.parseInt(ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),"")))+payment;
        payment=ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),""));

        if (payment==0){
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            remainPay.setVisibility(View.VISIBLE);
            return;
        }

        Log.e("Total_Amount","->"+payment);
        intent.putExtra("Total_Amount",String.valueOf(payment));
        intent.putExtra("Status", Status);
        intent.putExtra("TodaysTime", Time_Instant);
        intent.putExtra("Main_Order_No", OrderNo);
        intent.putExtra("From", "Edit");
        intent.putExtra("Pay_Amount", Integer.toString((pay)));
        intent.putExtra("Repay",true);
        startActivity(intent);
    }

    void CreateTransactionStatusCheckSum(String orderNo, boolean issatging, boolean IscancelVisible, String callback, String mid, String WEBSITE, String channelId, String industry){
        //initializePayment();
        //CreateCheckSum(false,orderNo,issatging,IscancelVisible, callback, mid, WEBSITE, channelId, industry);
    }
    void StartOrderStatusActivity(JSONObject response){
        String Payment_Status=response.optString("RESPMSG");
        String ResponseCode=response.optString("RESPCODE");

    }
    void transactionStatusAPI(String orderno, boolean isstaging, String mid){
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID",mid );
        paramMap.put("ORDERID", orderno);
        paramMap.put("CHECKSUMHASH",  checksum);
        String Url;
        if (isstaging)
            Url="https://securegw-stage.paytm.in/merchant-status/getTxnStatus";
        else
            Url="https://securegw.paytm.in/merchant-status/getTxnStatus";
        final JSONObject param=new JSONObject(paramMap);



        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Url, param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(JSONObject response) {
                Log.e("PaytmTxnStatusAPI","Request "+param);
                Log.e("PaytmTxnStatusAPI","Response "+String.valueOf(response));
                StartOrderStatusActivity(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //   finish();
                Toast.makeText(editOrder.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
                Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

            }

        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
    /*private void initializePaytmPayment() {

        String CustId = auth.getCurrentUser().getUid();
        Random random = new Random();

        final String OrderN = OrderNo + "Repay" + random.nextInt(1000);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", Mid);
        paramMap.put("ORDER_ID", OrderN);
        paramMap.put("CUST_ID", CustId);
        paramMap.put("INDUSTRY_TYPE_ID", Industry);
        paramMap.put("CHANNEL_ID", ChannelId);
        paramMap.put("TXN_AMOUNT", Amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", Callback+OrderN);
        paramMap.put("MOBILE_NO", "7746896454");

        String Url = " https://sanjay9425sm.000webhostapp.com/Paytm/checksum.php";
        JSONObject param = new JSONObject(paramMap);


          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DEPRECATED_GET_OR_POST, Url, param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(JSONObject response) {

                checksum = response.optString("CHECKSUMHASH");
                if (!isDestroyed()) {
                    if (checksum.trim().length() != 0) {
                        onStartTransaction(OrderN);


                    } else {
                        remainPay.setVisibility(View.VISIBLE);
                        Toast.makeText(editOrder.this, "Failed to load paytm", Toast.LENGTH_SHORT).show();

                    }
                    Log.e("getresponse", String.valueOf(response));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });
        Volley.newRequestQueue(editOrder.this).add(jsonObjectRequest);

    }*/

    public void onStartTransaction(final String OrderN, final boolean isstaging, final boolean Iscacel, final String callback, final String mid, final String WEBSITE, final String channelId, final String industry) {
        String CustId = auth.getCurrentUser().getUid();
        PaytmPGService Service;
        Map<String, String> paramMap = new HashMap<String, String>();
        if (isstaging)
            Service = PaytmPGService.getStagingService();
        else Service = PaytmPGService.getProductionService();

        // these are mandatory parameters

        paramMap.put("MID", mid);
        paramMap.put("ORDER_ID", OrderN);
        paramMap.put("CUST_ID", CustId);
        paramMap.put("INDUSTRY_TYPE_ID", industry);
        paramMap.put("CHANNEL_ID", channelId);
        paramMap.put("TXN_AMOUNT", Amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", callback+OrderN);
        paramMap.put("MOBILE_NO", "7746896454");
        paramMap.put("CHECKSUMHASH", checksum);


        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);


        Service.initialize(Order, null);

        Service.startPaymentTransaction(editOrder.this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Toast.makeText(editOrder.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        CreateTransactionStatusCheckSum(OrderN,isstaging,Iscacel,callback,mid,WEBSITE,channelId,industry);
                         if (inResponse.toString().contains("RESPMSG=Txn Success")) {
                            String result = inResponse.toString();
                            Intent intent = new Intent(editOrder.this, orderstatus.class);
                            intent.putExtra("Message", "Success");
                            intent.putExtra("Order", OrderNo);
                            intent.putExtra("DeliveryTime", Deliver);

                            intent.putExtra("Pay_Amount", Amount);
                            intent.putExtra("From", "Edit");

                            intent.putExtra("Transaction", inResponse.toString().
                                    substring(result.indexOf("TXNID=") + 6, result.indexOf(", RESPCODE")));
                            intent.putExtra("Bank", inResponse.toString().
                                    substring(result.indexOf("BANKNAME=") + 9, result.indexOf(", ORDERID")));

                            payment(inResponse.toString().
                                    substring(result.indexOf("TXNID=") + 6, result.indexOf(", RESPCODE")), inResponse.toString().
                                    substring(result.indexOf("BANKNAME=") + 9, result.indexOf(", ORDERID")),Integer.parseInt(Amount),true);

                            startActivity(intent);
                            finish();

                        } else {
                            Intent intent = new Intent(editOrder.this, orderstatus.class);
                            intent.putExtra("Message", "Failed");
                            intent.putExtra("Order", OrderNo);
                            intent.putExtra("From", "Edit");

                            intent.putExtra("Pay_Amount", Integer.toString(pay));
                            intent.putExtra("ExtraInfo", "It looks like " + "some error occurred");
                            startActivity(intent);
                            finish();


                        }
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Intent intent = new Intent(editOrder.this, orderstatus.class);
                        intent.putExtra("Message", "Failed");
                        intent.putExtra("Order", OrderNo);
                        intent.putExtra("From", "Edit");

                        intent.putExtra("Pay_Amount", Integer.toString(pay));
                        intent.putExtra("ExtraInfo", "Network not available");

                        startActivity(intent);
                        finish();


                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Intent intent = new Intent(editOrder.this, orderstatus.class);
                        intent.putExtra("Message", "Failed");
                        intent.putExtra("Order", OrderNo);
                        intent.putExtra("From", "Edit");

                        intent.putExtra("Pay_Amount", Integer.toString(pay));
                        intent.putExtra("ExtraInfo", "It looks like authentication failed ");


                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Intent intent = new Intent(editOrder.this, orderstatus.class);
                        intent.putExtra("Message", "Failed");
                        intent.putExtra("Order", OrderNo);
                        intent.putExtra("Pay_Amount", Integer.toString(pay));
                        intent.putExtra("From", "Edit");

                        intent.putExtra("ExtraInfo", "It looks like paytm failed to load may be due to some technical error please try again");

                        startActivity(intent);
                        finish();

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Intent intent = new Intent(editOrder.this, orderstatus.class);
                        intent.putExtra("Message", "Failed");
                        intent.putExtra("Order", OrderNo);
                        intent.putExtra("Pay_Amount", Integer.toString(pay));
                        intent.putExtra("From", "Edit");

                        intent.putExtra("ExtraInfo", "It looks like you have press the back button");

                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Intent intent = new Intent(editOrder.this, orderstatus.class);
                        intent.putExtra("Message", "Failed");
                        intent.putExtra("Order", OrderNo);
                        intent.putExtra("From", "Edit");

                        intent.putExtra("Pay_Amount", Integer.toString(pay));
                        intent.putExtra("ExtraInfo", "It lools like you have cancel the transaction");

                        startActivity(intent);
                        finish();
                    }

                });

    }


    private void deliverypopup() {
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
            today = change(today);
            tomm = change(tomm);
            day_after = change(day_after);
            fourth_day = change(fourth_day);
            day1 = "Monday";
            day2 = "Tuesday";
            day3 = "Wednesday";
            day4 = "Thursday";
        } else if (day2.contains("Sunday")) {

            tomm = change(tomm);
            day_after = change(day_after);
            fourth_day = change(fourth_day);
            day2 = "Monday";
            day3 = "Tuesday";
            day4 = "Wednesday";
        } else if (day3.contains("Sunday")) {
            day_after = change(day_after);
            fourth_day = change(fourth_day);
            day3 = "Monday";
            day4 = "Tuesday";
        } else if (day4.contains("Sunday")) {
            fourth_day = change(fourth_day);
            day4 = "Monday";
        }
        //else System.out.println("No need");
        deliveryTime(today, tomm, day_after, fourth_day, day1, day2, day3, day4, Todaysday);

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

    void deliveryTime(final String today, final String tomm, final String dayafter, final String fourth_date, final String day1, final String day2, final String day3, final String day4, final String todaysdy) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("HH");
        final String hour = date.format(c.getTime());
        // final String hour="18" ;
        date = new SimpleDateFormat("mm");
        final String min = date.format(c.getTime());

        LayoutInflater layoutInflater = (LayoutInflater) editOrder.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Custom = layoutInflater.inflate(R.layout.popupfortime, null);
        final TextView head = Custom.findViewById(R.id.del);
        RelativeLayout relativeLayout = findViewById(R.id.relative);

        popupWindow = new PopupWindow(Custom, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);


        head.setText("Select Day");
        popup = "y";

        Custom.findViewById(R.id.RadioGroupTomorrow_from12to1).setVisibility(View.GONE);
        Custom.findViewById(R.id.RadioGroupTomorrow_from1to2).setVisibility(View.GONE);
        Custom.findViewById(R.id.RadioGroupTomorrow_from2to3).setVisibility(View.GONE);
        Custom.findViewById(R.id.from3to4).setVisibility(View.GONE);
        Button first = Custom.findViewById(R.id.from9to10);
        Button second = Custom.findViewById(R.id.RadioGroupTomorrow_from10to11);
        Button third = Custom.findViewById(R.id.RadioGroupTomorrow_from11to12);
        Button forth = Custom.findViewById(R.id.RadioGroupTomorrow_from12to1);
        if (todaysdy.contains("Sunday")) {
            first.setText("Tomorrow");
            second.setText(day2);
            third.setText(day3);
            forth.setText(day4);
        } else if (todaysdy.contains("Saturday")) {
            first.setText("Today");
            second.setText(day2);
            third.setText(day3);
            forth.setText(day4);
        } else {
            first.setText("Today");
            second.setText("Tomorrow");
            third.setText(day3);
            forth.setText(day4);
        }

        if (todaysdy.contains("Sunday")) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            third.setVisibility(View.VISIBLE);

        } else {
            if (Integer.parseInt(hour) >= 15 && Integer.parseInt(hour) <= 16) {
                if (Integer.parseInt(min) < 30) {
                    first.setVisibility(View.VISIBLE);
                    forth.setVisibility(View.GONE);
                }

            } else if (Integer.parseInt(hour) >= 18) {
                first.setVisibility(View.GONE);
                forth.setVisibility(View.VISIBLE);
            } else {
                first.setVisibility(View.VISIBLE);
                forth.setVisibility(View.GONE);
            }
        }
        if (type.toUpperCase().contains("CAKE")||type.toUpperCase().contains("EXTRAS"))
        { Log.e("Delivery for cake ","type contains going today for hiding  "+type);
            if (first.getText().toString().trim().toUpperCase().contains("TODAY"))
            {   if (!OriginalDelivery.contains(today)) {
                forth.setVisibility(View.VISIBLE);
                first.setVisibility(View.GONE);
                Log.e("Delivery for cake ", "first contains today not today hiding successful today"+today+" original del"+OriginalDelivery);
            }else  Log.e("Delivery for cake ", "first contains today + contain today hiding unsuccessful today"+today+" original del"+OriginalDelivery);
            }else Log.e("Delivery for cake ", "first not contains today hiding unsuccessful "+first.getText().toString().trim());


        }else Log.e("Delivery for cake ","type not contains not going today for hiding  "+type);
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = "(" + today + ")";
                Today = 1;
                if (todaysdy.contains("Sunday")) {
                    pop2("not", Integer.parseInt(hour), Integer.parseInt(min));
                } else pop2("Today", Integer.parseInt(hour), Integer.parseInt(min));

            }
        });
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = "(" + tomm + ")";
                Today = 0;

                pop2("not", Integer.parseInt(hour), Integer.parseInt(min));
            }
        });
        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = "(" + dayafter + ")";
                Today = 0;

                pop2("not", Integer.parseInt(hour), Integer.parseInt(min));
            }
        });
        forth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = "(" + fourth_date + ")";
                Today = 0;
                pop2("not", Integer.parseInt(hour), Integer.parseInt(min));
            }
        });
        Custom.findViewById(R.id.popuprelative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup = "n";
                popupWindow.dismiss();

            }
        });

    }


    void pop2(String today, int Time, int Min) {
        popup = "nn";
        LayoutInflater layoutInflater = (LayoutInflater) editOrder.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Custom = layoutInflater.inflate(R.layout.popupfortime, null);
        RelativeLayout relativeLayout = findViewById(R.id.relative);

        popupWindow1 = new PopupWindow(Custom, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow1.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        popupWindow1.setOutsideTouchable(true);
        TextView head = Custom.findViewById(R.id.del);
        head.setText("Select Delivery Slot");
        final int idstime[] = {R.id.from9to10, R.id.RadioGroupTomorrow_from10to11, R.id.RadioGroupTomorrow_from11to12, R.id.RadioGroupTomorrow_from12to1, R.id.RadioGroupTomorrow_from1to2, R.id.RadioGroupTomorrow_from2to3, R.id.from3to4};
        final String time[] = {"9-10", "10-11", "11-12", "12-13", "13-14", "14-15", "15-16"};
        byte loc = 0;
        Button b;

        loc = (byte) Arrays.asList(time).indexOf(Time + "-" + (Time + 1));
        if (Min < 30)
            loc -= 1;
        if (today.contains("Today")) {
            for (int i = 0; i < loc + 1; i++) {
                b = Custom.findViewById(idstime[i]);
                b.setVisibility(View.GONE);
                //////condition for time///
            }
        }

        Custom.findViewById(R.id.from9to10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "9:00 - 10:00 Hrs" + " " + day;
                popupWindow.dismiss();
                popup = "n";
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.RadioGroupTomorrow_from10to11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "10:00 - 11:00 Hrs" + " " + day;
                popupWindow.dismiss();
                popup = "n";
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.RadioGroupTomorrow_from11to12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "11:00 - 12:00 Hrs" + " " + day;
                popupWindow.dismiss();
                popup = "n";
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.RadioGroupTomorrow_from12to1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "12:00 - 13:00 Hrs" + " " + day;
                popup = "n";
                popupWindow.dismiss();
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.RadioGroupTomorrow_from1to2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "13:00 - 14:00 Hrs" + " " + day;
                popup = "n";
                popupWindow.dismiss();
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.RadioGroupTomorrow_from2to3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "14:00 - 15:00 Hrs" + " " + day;
                popup = "n";

                popupWindow.dismiss();
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();

            }
        });
        Custom.findViewById(R.id.from3to4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Times = "15:00 - 15:30 Hrs" + " " + day;
                popup = "n";

                popupWindow.dismiss();
                popupWindow1.dismiss();
                deliveryFormat();
                checkForVisibility_save();
            }
        });
        Custom.findViewById(R.id.popuprelative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup = "nn";
                popupWindow.dismiss();
                popupWindow1.dismiss();

            }
        });
    }
    public String displayMonth(String time) {

        return getThreeInitials(getMonthName(Integer.
                parseInt(time.substring(time.indexOf("/") + 1, time.length() - 4)) - 1)).toUpperCase();
    }

    public void deliveryFormat() {

        findViewById(R.id.deliveryFormat).setVisibility(View.VISIBLE);
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
        } catch (Exception NullPointerException) {
        }

    }

    public String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public String getThreeInitials(String month) {
        return month.substring(0, 3);
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

    void checkForVisibility_save() {
        boolean isChanged = false;
         for (int i = 0; i < foodQ.size(); i++) {
            if (!foodQ.get(i).equals(saveData.get(i))) {
                Toast.makeText(editOrder.this,"Quantity modified",Toast.LENGTH_SHORT).show();
                isChanged = true;
                cart=true;
            }//else cart=false;
        }
        Log.e("Delivery date ","Original "+OriginalDelivery+" Times"+Times);

        if (!OriginalDelivery.equals(Times)) {
            isChanged = true;
             delivery_change=true;
        }else delivery_change=false;
        if (isChanged){
             findViewById(R.id.save_food).setVisibility(View.VISIBLE);}
        else{

            findViewById(R.id.save_food).setVisibility(View.GONE);

        }
    }
}
