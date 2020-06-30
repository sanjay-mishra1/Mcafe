package com.sanproject.mcafe.payment_wallets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class paytm_payments extends AppCompatActivity {
      String checksum;
    public String Mid;//="MITThe83163602940912";
    //public String Mid="MITThe83163602940912";
    //public String Industry="Retail";
    public String Industry;//="Retail";
    public String  ChannelId;//="WAP";
    //public String  ChannelId="WAP";
     public String Amount;
   // public String Callback="https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    public String Callback;//="https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    public String WEBSITE;//="APPSTAGING";
    //public String WEBSITE="APPSTAGING";
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        Log.e("Paytm Debugging","Activity start");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        receive();
        CustId=FirebaseAuth.getInstance().getUid();
        GifImageView gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingpage);
        if (Total_Amount==null )
        {
            Snackbar.make(findViewById(R.id.parent),"An error occurred",Snackbar.LENGTH_SHORT).show();
            finish();
        }else
         Load_Data_from_server();
    }
    @Override
    protected void onStart() {

        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    void Load_Data_from_server(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Extras/Extra_Payment_Credentials/Paytm");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Callback = (String) dataSnapshot.child("Callback").getValue();
                    Mid = (String) dataSnapshot.child("Mid").getValue();
                    WEBSITE = (String) dataSnapshot.child("WEBSITE").getValue();
                    ChannelId = (String) dataSnapshot.child("ChannelId").getValue();
                    Industry = (String) dataSnapshot.child("Industry").getValue();
                     boolean isStaging = (boolean) dataSnapshot.child("Type").getValue();

                    Log.e("Paytm_Initializations", "Callback " + Callback + "Mid" + Mid + "Website" + WEBSITE + "Industry" + Industry);
                    initializePaytmPayment(isStaging);
                }else{
                    finish();
                    Toast.makeText(paytm_payments.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
                Toast.makeText(paytm_payments.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
            }
        });
    }
    void CreateCheckSum(final boolean PaytmScreen, final boolean isStaging){
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("MID",Mid );
        paramMap.put("ORDER_ID", orderno);
        paramMap.put("CUST_ID",  CustId);
        paramMap.put("INDUSTRY_TYPE_ID", Industry);
        paramMap.put("CHANNEL_ID", ChannelId);
        paramMap.put("TXN_AMOUNT", Amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL",  Callback);
     //   paramMap.put("MOBILE_NO", "7746896454");
        SharedPreferences shared= getApplicationContext().getSharedPreferences( "User_Credentials",MODE_PRIVATE);
        mobile=shared.getString("MobileNumber","");
        if (mobile.isEmpty()){
            mobile="NoNumber";
        }
        paramMap.put("MOBILE_NO", mobile);

        String Url="https://mitcafe.000webhostapp.com/payments/generateChecksum.php";
        //  String Url="https://fir-a-1fd3e.firebaseapp.com/Paytm/node/router.js";
        JSONObject param=new JSONObject(paramMap);



        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.DEPRECATED_GET_OR_POST, Url, param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(JSONObject response) {
                checksum=response.optString("CHECKSUMHASH");
               if (PaytmScreen){
                if (checksum.trim().length()!=0){
                    if (!isDestroyed())
                        onStartTransaction(isStaging);
                }else{
                    finish();
                    Toast.makeText(paytm_payments.this,"Loading failed , please retry",Toast.LENGTH_SHORT).show();
                    //   Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());
                }
                    Log.e("Paytm Debugging", "Checksum received starting transaction");

                }else{
                   transactionStatusAPI(isStaging);
               }
                Log.e("getresponse",String.valueOf(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                finish();
                Toast.makeText(paytm_payments.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
                Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

            }

        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
    private void initializePaytmPayment(boolean type ){
         CreateCheckSum(true,type);

      }
    void CreateTransactionStatusCheckSum(boolean isstaging){
         CreateCheckSum(false,isstaging);
    }
    void StartOrderStatusActivity(JSONObject response){
        String Payment_Status=response.optString("RESPMSG");
        String ResponseCode=response.optString("RESPCODE");
      /**/

    }
    void transactionStatusAPI(boolean isstaging){
        Map<String, String> paramMap = new HashMap<String, String>();
         paramMap.put("MID",Mid );
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
                Log.e("PaytmTxnStatusAPI","Response "+ response);
                StartOrderStatusActivity(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
             //   finish();
                Toast.makeText(paytm_payments.this,"Loading failed ,please retry",Toast.LENGTH_SHORT).show();
                Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

            }

        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
    public void onStartTransaction(final boolean isstaging ) {
        PaytmPGService Service    ;
        Map<String, String> paramMap = new HashMap<String, String>();
       if (isstaging)
           Service = PaytmPGService.getStagingService();
       else Service = PaytmPGService.getProductionService();


        paramMap.put("MID",Mid );
        paramMap.put("ORDER_ID", orderno);
        paramMap.put("CUST_ID",  CustId);
        paramMap.put("INDUSTRY_TYPE_ID", Industry);
        paramMap.put("CHANNEL_ID", ChannelId);
        paramMap.put("TXN_AMOUNT", Amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL",  Callback);
        paramMap.put("MOBILE_NO", mobile);
        paramMap.put("CHECKSUMHASH",checksum);



        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                     @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                         Intent intent=new Intent(paytm_payments.this, orderstatus.class);
                         intent.putExtra("Message","Failed");
                         intent.putExtra("ExtraInfo","It looks like network is not available "  );
                         intent.putExtra("Total_Amount",Total_Amount);
                         intent.putExtra("Pay_Amount",Amount);
                         intent.putExtra("UId",Uid);
                         intent.putExtra("Order",orderno);
                         intent.putExtra("From", from);
                         intent.putExtra("TodaysTime",TodaysDate);
                         intent.putExtra("DeliveryTime",DeliveryTime);
                         intent.putExtra("Status","Pending");
                         intent.putExtra("Pay_Amount",Amount);
                         intent.putExtra("Total_Food",total_food);
                         intent.putExtra("Address",Address);
                         intent.putExtra("Tax",Tax);
                         intent.putExtra("OtherPayment",Other);
                         intent.putExtra("Image0",Img0);
                         try {
                             intent.putExtra("Image1", Img1);
                             intent.putExtra("Image2", Img2 );
                             intent.putExtra("Image3", Img3);

                         }catch (Exception ignored){}
                         startActivity(intent);
                         finish();

                     }


                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                         //Log.d("LOG", "Payment Transaction is successful " + inResponse);
                         Log.d("LOG", "Payment Transaction Status started " + inResponse);
                        CreateTransactionStatusCheckSum(isstaging);

                          if (inResponse.toString().contains("RESPMSG=Txn Success")){
                            String result=inResponse.toString();
                             //CreateTransactionStatusCheckSum();
                             Intent intent=new Intent(paytm_payments.this,orderstatus.class);
                            intent.putExtra("Message","Success");
                            intent.putExtra("TodaysTime",TodaysDate);
                            intent.putExtra("DeliveryTime",DeliveryTime);
                            intent.putExtra("Status","Pending");
                            intent.putExtra("Pay_Amount",Amount);
                            intent.putExtra("Total_Amount",Total_Amount);
                            intent.putExtra("UId",Uid);
                            intent.putExtra("Total_Food",total_food);
                            intent.putExtra("From", from);
                            intent.putExtra("Address",Address);
                            intent.putExtra("Tax",Tax);
                            intent.putExtra("OtherPayment",Other);
                              final String string = inResponse.toString().
                                      substring(result.indexOf("TXNID=")
                                                      + 6,
                                              result.indexOf(", RESPCODE"));
                              intent.putExtra("Transaction", string);
                              final String substring = inResponse.toString().
                                      substring(result.indexOf("BANKNAME=") + 9, result.indexOf(", ORDERID"));
                              intent.putExtra("Bank", substring);

                            intent.putExtra("Image0",Img0);
                            try {
                                intent.putExtra("Image1", Img1);
                                intent.putExtra("Image2", Img2 );
                                intent.putExtra("Image3", Img3);

                            }catch (Exception ignored){}
                              if (!getIntent().getBooleanExtra("Repay",false))
                              {
                                  intent.putExtra("Order",orderno);
                                  storeTransaction(orderno,TodaysDate, string,Amount,
                                          substring);
                              }
                              else {
                                  intent.putExtra("Order", getIntent().getStringExtra("Main_Order_No"));
                                  intent.putExtra("From","Edit");
//                                  storeUpdateTransaction(orderno,Amount,TodaysDate, inResponse.toString().
//                                                  substring(result.indexOf("TXNID=") + 6, result.indexOf(", RESPCODE")),
//                                          substring);
                                  payment(string,
                                          substring,
                                          Integer.parseInt(Amount),true);
                              }

                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent=new Intent( paytm_payments.this,orderstatus.class);

                            intent.putExtra("ExtraInfo","It looks like you have cancel the transaction");
                            intent.putExtra("Message","Failed");
                             intent.putExtra("Total_Amount",Total_Amount);
                            intent.putExtra("Pay_Amount",Amount);
                            intent.putExtra("UId",Uid);
                            intent.putExtra("Order",orderno);
                            intent.putExtra("From", from);
                            intent.putExtra("TodaysTime",TodaysDate);
                            intent.putExtra("DeliveryTime",DeliveryTime);
                            intent.putExtra("Status","Pending");
                            intent.putExtra("Pay_Amount",Amount);
                            intent.putExtra("Total_Food",total_food);
                            intent.putExtra("Address",Address);
                            intent.putExtra("Tax",Tax);
                            intent.putExtra("OtherPayment",Other);
                            intent.putExtra("Image0",Img0);
                            try {
                                intent.putExtra("Image1", Img1);
                                intent.putExtra("Image2", Img2 );
                                intent.putExtra("Image3", Img3);

                            }catch (Exception IndexOutOfBoundsException){}


                            startActivity(intent);
                            finish();

                        }
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Intent intent=new Intent(paytm_payments.this,orderstatus.class);
                        intent.putExtra("Message","Failed");
                        intent.putExtra("ExtraInfo","It looks like network is not available "  );
                        intent.putExtra("Total_Amount",Total_Amount);
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("UId",Uid);
                        intent.putExtra("Order",orderno);
                        intent.putExtra("From", from);
                        intent.putExtra("TodaysTime",TodaysDate);
                        intent.putExtra("DeliveryTime",DeliveryTime);
                        intent.putExtra("Status","Pending");
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("Total_Food",total_food);
                        intent.putExtra("Address",Address);
                        intent.putExtra("Tax",Tax);
                        intent.putExtra("OtherPayment",Other);
                        intent.putExtra("Image0",Img0);
                        try {
                            intent.putExtra("Image1", Img1);
                            intent.putExtra("Image2", Img2 );
                            intent.putExtra("Image3", Img3);

                        }catch (Exception IndexOutOfBoundsException){}
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
                        Intent intent=new Intent(paytm_payments.this,orderstatus.class);
                        intent.putExtra("Message","Failed");
                        intent.putExtra("ExtraInfo","It looks like authentication failed "  );
                        intent.putExtra("Total_Amount",Total_Amount);
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("UId",Uid);
                        intent.putExtra("Order",orderno);
                        intent.putExtra("From", from);
                        intent.putExtra("TodaysTime",TodaysDate);
                        intent.putExtra("DeliveryTime",DeliveryTime);
                        intent.putExtra("Status","Pending");
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("Total_Food",total_food);
                        intent.putExtra("Address",Address);
                        intent.putExtra("Tax",Tax);
                        intent.putExtra("OtherPayment",Other);
                        intent.putExtra("Image0",Img0);
                        try {
                            intent.putExtra("Image1", Img1);
                            intent.putExtra("Image2", Img2 );
                            intent.putExtra("Image3", Img3);

                        }catch (Exception IndexOutOfBoundsException){}
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Intent intent=new Intent(paytm_payments.this,orderstatus.class);
                        intent.putExtra("Message","Error loading webpage"+inErrorMessage+" "+inFailingUrl);
                        intent.putExtra("ExtraInfo", "It looks like paytm failed to load may be due to some technical error please try again" );
                        intent.putExtra("Order",orderno);
                        intent.putExtra("Total_Amount",Total_Amount);
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("UId",Uid);
                        intent.putExtra("From", from);
                        intent.putExtra("TodaysTime",TodaysDate);
                        intent.putExtra("DeliveryTime",DeliveryTime);
                        intent.putExtra("Status","Pending");
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("Total_Food",total_food);
                        intent.putExtra("Address",Address);
                        intent.putExtra("Tax",Tax);
                        intent.putExtra("OtherPayment",Other);
                        intent.putExtra("Image0",Img0);
                        try {
                            intent.putExtra("Image1", Img1);
                            intent.putExtra("Image2", Img2 );
                            intent.putExtra("Image3", Img3);

                        }catch (Exception IndexOutOfBoundsException){}
                        startActivity(intent);
                        finish();
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
//                        Intent intent=new Intent(paytm_payments.this,orderstatus.class);
//                        intent.putExtra("Message","Failed");
//                        intent.putExtra("ExtraInfo","It looks like you have press the back button" );
//                        intent.putExtra("Order",orderno);
//                        intent.putExtra("Total_Amount",Total_Amount);
//                        intent.putExtra("Pay_Amount",Amount);
//                        intent.putExtra("UId",Uid);
//                        intent.putExtra("From", from);
//                        intent.putExtra("TodaysTime",TodaysDate);
//                        intent.putExtra("DeliveryTime",DeliveryTime);
//                        intent.putExtra("Status","Pending");
//                        intent.putExtra("Pay_Amount",Amount);
//                        intent.putExtra("Total_Food",total_food);
//                        intent.putExtra("Address",Address);
//                        intent.putExtra("Tax",Tax);
//                        intent.putExtra("OtherPayment",Other);
//                        intent.putExtra("Image0",Img0);
//                        try {
//                            intent.putExtra("Image1", Img1);
//                            intent.putExtra("Image2", Img2 );
//                            intent.putExtra("Image3", Img3);
//
//                        }catch (Exception IndexOutOfBoundsException){}
//                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Intent intent=new Intent(paytm_payments.this,orderstatus.class);
                        intent.putExtra("Message","Failed");
                        intent.putExtra("ExtraInfo","It lools like you have cancel the transaction" );
                        intent.putExtra("Order",orderno);
                        intent.putExtra("Total_Amount",Total_Amount);
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("UId",Uid);
                        intent.putExtra("From", from);

                        intent.putExtra("TodaysTime",TodaysDate);
                        intent.putExtra("DeliveryTime",DeliveryTime);
                        intent.putExtra("Status","Pending");
                        intent.putExtra("Pay_Amount",Amount);
                        intent.putExtra("Total_Food",total_food);
                        intent.putExtra("Address",Address);
                        intent.putExtra("Tax",Tax);
                        intent.putExtra("OtherPayment",Other);
                        intent.putExtra("Image0",Img0);
                        try {
                            intent.putExtra("Image1", Img1);
                            intent.putExtra("Image2", Img2 );
                            intent.putExtra("Image3", Img3);

                        }catch (Exception IndexOutOfBoundsException){}
                        startActivity(intent);
                        finish();
                    }

                });
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
        //store_extra_info("Payment of ₹"+Amount+" is received by the canteen");
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
        d1.child("Mode").setValue("Paytm").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              //  check();
            }
        });
        d1.child("Bank").setValue(Bank).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            orderno[0]="0"+ year;
        }else
            orderno[0]=Integer.toString( year);
        if (month<=9) {
            orderno[1]="0"+ month;
        }else
            orderno[1]= Integer.toString(month);

        if (Monthdays<=9) {
            orderno[2]="0"+ Monthdays;
        }else
            orderno[2]= Integer.toString(Monthdays);
        if (hour<=9) {
            orderno[3]="0"+ hour;
        }else
            orderno[3]= Integer.toString(hour);

        Random random=new Random();

        return orderno[0]+orderno[1]+orderno[2]+orderno[3]+day.format(date)+
                random.nextInt(100);

    }

    @Override
    public void onBackPressed() {
        finish();
     }
}
