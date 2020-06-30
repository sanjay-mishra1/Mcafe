package com.sanproject.mcafe.payment_wallets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.orderstatus;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class RazorpayPayment extends AppCompatActivity implements PaymentResultListener {
    private String orderno;
    private String TodaysDate;
    private String DeliveryTime;
    private String Status;
    private String Total_Amount;
    private String Img0;
    private String Img1;
    private String Img2;
    private String Img3;
    private String Uid;
    public String CustId ;
    private String total_food;
    private String Address;
    private String Tax;
    private String Other;
    private String from;
    private String mobile;
    private String Amount;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        GifImageView gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingpage);
        receive();
        startPayment();

    }

    private void receive(){
        try {
            Intent intent = getIntent();
            orderno = intent.getStringExtra("Order");
            TodaysDate=   intent.getStringExtra("TodaysTime");
            from=intent.getStringExtra("From");
            DeliveryTime= intent.getStringExtra("DeliveryTime");
            Status=   intent.getStringExtra("Status");
            Total_Amount=  intent.getStringExtra("Total_Amount");
            Amount=intent.getStringExtra("Pay_Amount");
            Uid= intent.getStringExtra("UId");
            total_food=intent.getStringExtra("Total_Food");
            Address=intent.getStringExtra("Address");
            Tax=intent.getStringExtra("Tax");
            Other=intent.getStringExtra("OtherPayment");
            Log.e("Received paytm","order "+orderno+"Todays date "+TodaysDate+" delivery "+DeliveryTime
                    +" Status "+Status+" TotalAmount "+Total_Amount+" Amount "+Amount+" UID "+Uid+" total_food "+total_food+" Address"+
                    Address+" Tex "+Tax+" Other "+Other);

            try {
                Img0=     intent.getStringExtra("Image0");
                Img1=   intent.getStringExtra("Image1" );
                Img2=  intent.getStringExtra("Image2");
                Img3=  intent.getStringExtra("Image3" );
            }catch (Exception NullPointerException){}
        }catch (Exception e){e.printStackTrace();}

        if (!getIntent().getBooleanExtra("Repay",false))
            orderno=getUOrderNumber();
    }
    String getUOrderNumber(){

        Date date= Calendar.getInstance().getTime();
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

        return orderno[0]+orderno[1]+orderno[2]+orderno[3]+day.format(date)+
                String.valueOf(random.nextInt(100));

    }

    public void startPayment(){
        Activity activity = this;
        Checkout co = new Checkout();
        SharedPreferences shared= getApplicationContext().getSharedPreferences( "MyPrefs",MODE_PRIVATE);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "MCafe");
            options.put("description", "Payment for order no "+orderno);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://mit-cafe.web.app/icon.png");
            options.put("currency", "INR");
            double total = Double.parseDouble(Amount);
            total = total * 100;
            options.put("amount", total);
            JSONObject preFill = new JSONObject();
            mobile=shared.getString("MobileNumber","");
            email=shared.getString("Email","");
            Log.e("Razor","Email->"+email+" ->"+mobile);
            preFill.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            preFill.put("contact", mobile);
            options.put("prefill", preFill);
            co.open(activity, options);
        }catch (Exception e){
            Log.e("error" , " error "+e.toString());
        }
    }
    void storeTransaction(String OrderNo ,String date,String Transaction,String Pay_amount,String Bank){
        if (OrderNo.toUpperCase().contains("REPAY")){
            OrderNo=OrderNo.toUpperCase().substring(0,OrderNo.indexOf("REPAY")-1);
        }
        DatabaseReference d= FirebaseDatabase.getInstance().getReference().
                child("Transactions/"+Canteen+"/"+OrderNo);

        d.child("UId").setValue(Uid).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //check();
            }
        });
        d.child("OrderNo").setValue(OrderNo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // check();
            }
        });
        d.child("TotalAmount").setValue(Total_Amount).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // check();
            }
        });
        d.child("Time").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //   check();
            }
        });
        DatabaseReference d1=FirebaseDatabase.getInstance().getReference().
                child("Transactions/"+Canteen+"/"+OrderNo+"/Transactions/"+ Transaction);
        d1.child("TransactionNumber").setValue(Transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //   check();
            }
        });
        d1.child("Payment").setValue(Pay_amount).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  check();
            }
        });
        d1.child("Mode").setValue("Razor Pay").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  check();
            }
        });
        d1.child("Bank").setValue("NA").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  check();
            }
        });
        d1.child("Time").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  check();
                // contentProgrss.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
    try {
        Log.e("Payment Response->", "Success->" + s);
//        Map<String,Object> retMap=new Gson().fromJson(s,new TypeToken<HashMap<String,Object>>(){}.getType());
//        Log.e("->",""+retMap);
        Intent intent = new Intent(this, orderstatus.class);
        intent.putExtra("Message", "Success");
        intent.putExtra("TodaysTime", TodaysDate);
        intent.putExtra("DeliveryTime", DeliveryTime);
        intent.putExtra("Status", "Pending");
        intent.putExtra("Pay_Amount", Amount);
        intent.putExtra("Total_Amount", Total_Amount);
        intent.putExtra("UId", Uid);
        intent.putExtra("Total_Food", total_food);
        intent.putExtra("From", from);
        intent.putExtra("Address", Address);
        intent.putExtra("Tax", Tax);
        intent.putExtra("OtherPayment", Other);
        String transid = s;
        intent.putExtra("Transaction", transid);
        String bank = "NA";
        intent.putExtra("Bank", bank);
        if (!getIntent().getBooleanExtra("Repay",false))
        {        intent.putExtra("Order", orderno);

            storeTransaction(orderno,TodaysDate,transid,Amount,
                    bank);
        }
        else{
            intent.putExtra("Order", getIntent().getStringExtra("Main_Order_No"));
            intent.putExtra("From","Edit");
//            storeUpdateTransaction(orderno,Amount,TodaysDate,transid,bank);
            payment(transid,
                    bank,
                    Integer.parseInt(Amount),true);
        }
        startActivity(intent);
    }catch (Exception e){e.printStackTrace();}
    }

    void payment(String TrasactionNo, String Bank,int payment,boolean status) {
//        int Total_price = 0;
//        for (int i = 0; i <foodprice.size(); i++)
//            Total_price = Total_price + Integer.parseInt(foodprice.get(i)) * Integer.parseInt(foodQ.get(i));

        //      TextView textView=findViewById(R.id.PaymentStatus1);

        final DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        try {
            if (status)
            {
                //payment=Integer.parseInt(ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),"")))+payment;
                //payment=ExtractNumber(textView.getText().toString().trim().replace(getString(R.string.remaining_),""))+payment;

                //Total_price=  payment;
            }else{
                //Total_price=     payment;
            }
        }catch (Exception e){
//            Extract_payment_Status_from_trasaction(TrasactionNo,Bank);

        }
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String OrderNo=getIntent().getStringExtra("Main_Order_No");
        data.child("User Informations").child(uid).child("Orders/All orders")
                .child(OrderNo).child("Payment")
                .setValue((Total_Amount));

        data.child("User Informations").child(uid).
                child("Orders/Pending").child(OrderNo).child("Payment").
                setValue((Total_Amount));

        data.child("Orders").child(Canteen).child("New Orders").child(OrderNo).
                child("Payment").setValue((Total_Amount));
        data.child("Orders").child(Canteen).child("All Orders").child(OrderNo).child("Payment").
                setValue(Total_Amount);

        //  data.child("Transactions/" + OrderNo + "/TotalAmount").setValue(Integer.toString(Total_price));

        storeUpdateTransaction(OrderNo, String.valueOf(payment), DateFormat.getDateTimeInstance().format(new Date()), TrasactionNo, Bank);
        store_extra_info("Payment of ₹"+ payment +" is received by the canteen");

    }


    void store_extra_info(final String message){
        final String mainOrderno=getIntent().getStringExtra("Main_Order_No");
        Log.e("Store Extra Info","Inside store extra info "+message);
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Orders/"+Canteen+"/"+"All Orders/"+mainOrderno);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Extra_Info").exists()){
                    databaseReference.child("Extra_Info").setValue(dataSnapshot.child("Extra_Info").getValue()+"\n\u2022  "+message+" "+ DateFormat.getDateTimeInstance().format(new Date()));
                    FirebaseDatabase.getInstance().getReference("User Informations/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Orders/All orders/"+mainOrderno+"/Extra_Info")
                            .setValue(dataSnapshot.child("Extra_Info").getValue()+"\n\u2022  "+message+" "+
                                    DateFormat.getDateTimeInstance().format(new Date()));
                    Log.e("Store Extra Info","Extra info exists stored new info");
                }else{
                    databaseReference.child("Extra_Info").setValue("\u2022  "+message+" "+
                            DateFormat.getDateTimeInstance().format(new Date()));
                    FirebaseDatabase.getInstance().getReference("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Orders/All orders/"+mainOrderno+"/Extra_Info")
                            .setValue("\u2022  "+message+" "+
                                    DateFormat.getDateTimeInstance().format(new Date()));
                    Log.e("Store Extra Info","Extra info not exists stored new info");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Store Extra Info","Inside store extra info cancelled "+databaseError.toString());
            }
        });
    }

    void storeUpdateTransaction(final String OrderNo, final String Amount, final String date, final String TransactionNO, final String bank) {
        store_extra_info("Payment of ₹"+Amount+" is received by the canteen");
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        DatabaseReference da = FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/" + OrderNo );
        da.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()||dataSnapshot.exists()){
                    DatabaseReference d = FirebaseDatabase.getInstance().getReference("Transactions/"+Canteen+"/" + OrderNo + "/Transactions/" + TransactionNO);
                    d.child("TransactionNumber").setValue(TransactionNO);
                    d.child("OrderNo").setValue(OrderNo);
                    d.child("Payment").setValue(Amount);
                    d.child("Mode").setValue("Paytm");
                    d.child("Bank").setValue(bank);
                    d.child("Time").setValue(date);
                    data.child("Transactions/" + OrderNo + "/TotalAmount").
                            setValue((Total_Amount));
                }else {
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                            getReference("Transactions/"+Canteen+"/"+"Pending_Payments/" + OrderNo);
                    updateTrasaction(databaseReference,TransactionNO,bank,date);
                    data.child("Transactions/Pending_Payments/" + OrderNo + "/TotalAmount").setValue(Total_Amount);
                    updateTrasaction(FirebaseDatabase.getInstance().
                            getReference("Transactions/"+Canteen+"/"+"Payments/" + OrderNo),TransactionNO,bank,date);
                    data.child("Transactions/"+Canteen+"/"+"Payments/" + OrderNo + "/TotalAmount").setValue(Total_Amount);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    void updateTrasaction(DatabaseReference d,String TransactionNO,String bank,String date){
        d.child("OrderNo").setValue(orderno);
        d.child("Time").setValue(date);
        d.child("Transactions/"+TransactionNO).child("TransactionNumber").setValue(TransactionNO);
        d.child("Transactions/"+TransactionNO).child("OrderNo").setValue(orderno);
        d.child("Transactions/"+TransactionNO).child("Payment").setValue(Amount);
        d.child("Transactions/"+TransactionNO).child("Mode").setValue("PayU");
        d.child("Transactions/"+TransactionNO).child("Bank").setValue(bank);
        d.child("Transactions/"+TransactionNO).child("Time").setValue(date);
        d.child("Transactions/"+TransactionNO).child("ADMINSTATUS").setValue(false);
    }


    @Override
    public void onPaymentError(int i, String s) {
        try {
            Log.e("Payment Response->", "Fail->" + s + " i->" + i);
            Map<String, Object> retMap = new Gson().fromJson(s, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            Log.e("->", "" + retMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
