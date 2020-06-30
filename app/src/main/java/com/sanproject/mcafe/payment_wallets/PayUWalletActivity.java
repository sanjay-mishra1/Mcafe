package com.sanproject.mcafe.payment_wallets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
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


public class PayUWalletActivity extends AppCompatActivity {
    private static final String TAG = "PAYU";
    PayUmoneySdkInitializer.PaymentParam.Builder builder ;
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;

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
    public String Amount;
    private String Address;
    private String Tax;
    private String Other;
    private String from;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        GifImageView gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingpage);
        receive();
        configurePayU();
        startpay();
    }
    private boolean isDisableExitConfirmation = false;
    private void configurePayU() {
        PayUmoneyConfig payUmoneyConfig=PayUmoneyConfig.getInstance();
        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done Text");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Payment");

        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);
          builder= new PayUmoneySdkInitializer.PaymentParam.Builder();
    }

    public void startpay(){
        SharedPreferences shared= getApplicationContext().getSharedPreferences( "User_Credentials",MODE_PRIVATE);
        String mobile=shared.getString("MobileNumber","");
        email=shared.getString("Email","");

        if (mobile.isEmpty()){
            mobile="NoNumber";
        }
        builder.setAmount(Amount)                          // Payment amount
                .setTxnId(orderno)                     // Transaction ID
                .setPhone(mobile)                   // User Phone number
                .setProductName("MCafe")                   // Product Name or description
                .setFirstName("NA")                              // User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey("5Eup032F")                        // Merchant key
                .setMerchantId(mobile);


        try {

            paymentParam = builder.build();
            // generateHashFromServer(paymentParam );
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }

    }

    private void getHashkey() {
        String Url="https://mitcafe.000webhostapp.com/payments/payU/new_hash.php";
        HashMap<String, String> requestMap=new HashMap<>();
        requestMap.put("key","5Eup032F");
        requestMap.put("txnid",orderno);
        requestMap.put("amount",Amount);
        requestMap.put("productinfo","MCafe");
        requestMap.put("firstname","NA");
        requestMap.put("email",email);
        JSONObject param=new JSONObject(requestMap);
        Log.e("PayU","request->"+param);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Url,
                param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(JSONObject response) {
                String checksum = response.optString("CHECKSUMHASH");
                Log.e("PayU Debugging", "Checksum received starting transaction");
                    if (checksum.trim().length() != 0) {
                        if (!isDestroyed())
                            onStartTransaction(checksum);
                    } else {
                        finish();
                        Toast.makeText(PayUWalletActivity.this, "Loading failed , please retry", Toast.LENGTH_SHORT).show();
                        //   Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

                    }

                    Log.e("getresponse", String.valueOf(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                finish();
                Toast.makeText(PayUWalletActivity.this,"Loading failed ,please retry", Toast.LENGTH_SHORT).show();
                Log.e("Paytm Debugging", "Checksum not received error occur "+error.toString());

            }

        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                String payuResponse = transactionResponse.getPayuResponse();
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    paymentSuccess(payuResponse);
                    Log.e(TAG,"Transaction success");
                } else {
                    paymentFailed();
                    //Failure Transaction
                    Log.e(TAG,"Transaction failed");
                }

                // Response from Payumoney

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran " + payuResponse + "---" + merchantResponse);
            }else finish();
        }else finish();
    }

    private void paymentFailed() {
        Intent intent=new Intent(PayUWalletActivity.this, orderstatus.class);
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

    private void paymentSuccess(String payuResponse) {
//            tran {"status":0,"message":"payment status for :250315339","result":
//            {"postBackParamId":968742,"mihpayid":"9083895230","paymentId":250315339,
//            "mode":"CC","status":"success","unmappedstatus":"captured",
//            "key":"5Eup032F","txnid":"664",
//            "amount":"100.00","additionalCharges":"",
//            "addedon":"2020-06-11 00:50:07","createdOn":1591816826000,
//            "productinfo":"My Pharma","firstname":"Sanjay","lastname":"","address1":"","
//            address2":"","city":"","state":"","country":"","zipcode":"",
//            "email":"sanjay9425.sm@gmail.com","phone":"7746896454",
//            "udf1":"","udf2":"","udf3":"","udf4":"","udf5":"","udf6":"",
//            "udf7":"","udf8":"","udf9":"","udf10":"",
//            "hash":"08f2dfd37f64073bb62684c2b9ab224c5d421a4ab4c8500ba9f0ca4d47da85e736769c32f5481996aafcc2f2f2bb6deb60e84f329ff3ab7a79275c53bc6dcf41",
//            "field1":"889266177262","field2":"758114","field3":"707661498352361",
//            "field4":"TDRVRlUyZ0ZQOXB4V2Y2bkI4bUs=","field5":"02","field6":"",
//            "field7":"AUTHPOSITIVE","field8":"","field9":"","bank_ref_num":"707661498352361","bankcode":"MAST","error":"E000","error_Message":"No Error","cardToken":"","offer_key":"","offer_type":"","offer_availed":"","pg_ref_no":"","offer_failure_reason":"","name_on_card":"payu","cardnum":"555555XXXXXX4444","cardhash":"This field is no longer supported in postback params.","card_type":"","card_merchant_param":null,"version":"","postUrl":"https:\/\/www.payumoney.com\/mobileapp\/payumoney\/success.php",
//            "calledStatus":false,"additional_param":"","amount_split":"{\"PAYU\":\"100.00\"}","discount":"0.00","net_amount_debit":"100","fetchAPI":null,"paisa_mecode":"","meCode":"{\"tranportalid\":\"65506550\",\"pg_alias\":\"90000970\",\"pg_name\":\"hdfctraveltesting\",\"tranportalpwd\":\"password@123\"}","payuMoneyId":"250315339","encryptedPaymentId":null,"id":null,"surl":null,"furl":null,"baseUrl":null,"retryCount":0
//            ,"merchantid":null,"payment_source":null,"isConsentPayment":0,"pg_TYPE":"HDFCPG",
//            "s2SPbpFlag":false},"errorCode":null,"responseCode":null}---null
        Map<String,Object>retMap=new Gson().fromJson(payuResponse,new TypeToken<HashMap<String,Object>>(){}.getType());
        //String result=inResponse.toString();
        //CreateTransactionStatusCheckSum();
        Intent intent=new Intent(this, orderstatus.class);
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
        String transid=(String) ((LinkedTreeMap)retMap.get("result")).get("txnid");
        intent.putExtra("Transaction",transid);
        String bank=(String) ((LinkedTreeMap)retMap.get("result")).get("bankcode");
        intent.putExtra("Bank",bank);


        //        intent.putExtra("Transaction",inResponse.toString().
//                substring(result.indexOf("TXNID=")+6,result.indexOf(", RESPCODE")));
//        intent.putExtra("Bank",inResponse.toString().
//                substring(result.indexOf("BANKNAME=")+9,result.indexOf(", ORDERID")));

        intent.putExtra("Image0",Img0);
        try {
            intent.putExtra("Image1", Img1);
            intent.putExtra("Image2", Img2 );
            intent.putExtra("Image3", Img3);

        }catch (Exception ignored){}
        if (!getIntent().getBooleanExtra("Repay",false))
        {
            intent.putExtra("Order",orderno);

            storeTransaction(orderno,TodaysDate,transid,Amount,
                    bank);
        }
        else{
            intent.putExtra("Order",getIntent().getStringExtra("Main_Order_No"));
            intent.putExtra("From","Edit");
            //storeUpdateTransaction(orderno,Amount,TodaysDate,transid,bank);

            payment(transid,
                    bank,
                    Integer.parseInt(Amount),true);
        }
        startActivity(intent);
        finish();
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

    private void onStartTransaction(String checksum) {
        paymentParam.setMerchantHash(checksum);
        // Invoke the following function to open the checkout page.
        // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
//        if (AppPreference.selectedTheme != -1) {
//            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams
//                    , MainActivity.this,
//                    AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
//        } else {
//            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MainActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
//        }

      try {
          PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam,
                  this, R.style.PaymentTheme, true);
      }catch (Exception e){e.printStackTrace();}
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
        d1.child("Mode").setValue("PayU").addOnSuccessListener(new OnSuccessListener<Void>() {
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

}
