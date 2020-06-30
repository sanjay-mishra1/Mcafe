package com.sanproject.mcafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.sanproject.mcafe.payment_wallets.PayUWalletActivity;
import com.sanproject.mcafe.payment_wallets.RazorpayPayment;
import com.sanproject.mcafe.payment_wallets.paytm_payments;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import gifLoader.GifImageView;

public class payment extends AppCompatActivity {
    Button pay;
RadioButton half,full;
public String Amount;

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
    GifImageView gifImageView;
    private String from;
    private int fullPrice;
    private int halfPrice;
    private int payAmount=0;
    private String final_pay_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Glide.with(getApplicationContext()).load(null).into((ImageView) findViewById(R.id.food_icon));
        pay=findViewById(R.id.paybutton);
        half=findViewById(R.id.halfpay);
        full=findViewById(R.id.payuPay);
        receive();
        if (!getIntent().getBooleanExtra("Repay",false))
        initializePriceSelectorDialog();
        else{
            TextView textView=findViewById(R.id.name);
            payAmount= Integer.parseInt(final_pay_amount);
            textView.setText(String.format(Locale.UK,"₹ %d", payAmount));
            textView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
        CustId=Uid;
        gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingbackground);
        final RadioButton razor=findViewById(R.id.razorpay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Total_Amount!=null && !Objects.equals(Total_Amount, "0")) {
                    if (payAmount == 0) {
                        Toast.makeText(payment.this, "Please Select Price", Toast.LENGTH_SHORT).show();
                    } else if (!half.isChecked()&&!full.isChecked()&& !razor.isChecked())
                    {
                        Snackbar.make(findViewById(R.id.parent),"Select A Payment Option",Snackbar.LENGTH_SHORT).show();
                    }else
                    {   Log.e("Pay","->"+payAmount);
                        view.setEnabled(false);
                        if (half.isChecked()) {
                            gotopaytm();
                        } else if (full.isChecked()) {
                            gotoPayU();
                        } else gotoRazorPay();
                        pay.setVisibility(View.GONE);
                    }
                }else Snackbar.make(findViewById(R.id.parent),"An error occurred",Snackbar.LENGTH_SHORT).show();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pay.setVisibility(View.VISIBLE);
        pay.setEnabled(true);
        pay.setClickable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pay.setVisibility(View.VISIBLE);

    }

    private void gotoRazorPay() {
        Intent intent=new Intent(this, RazorpayPayment.class);
        intent.putExtra("Repay",getIntent().getBooleanExtra("Repay",false));
        intent.putExtra("Order",orderno);
        intent.putExtra("TodaysTime",TodaysDate);
        intent.putExtra("DeliveryTime",DeliveryTime);
        intent.putExtra("Main_Order_No", getIntent().getStringExtra("Main_Order_No"));

        intent.putExtra("Status","Pending");
//        intent.putExtra("Pay_Amount",Amount);
        intent.putExtra("Pay_Amount",String.valueOf(payAmount));
        intent.putExtra("Total_Amount",Total_Amount);
        intent.putExtra("UId",Uid);
        intent.putExtra("Total_Food",total_food);
        intent.putExtra("From", from);
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
        // finish();
    }


    private void initializePriceSelectorDialog() {
        halfPrice=Integer.parseInt(Total_Amount)/ 2 ;
        payAmount=halfPrice;
        fullPrice=Integer.parseInt(Total_Amount) ;
        TextView textView=findViewById(R.id.name);
        textView.setText(String.format(Locale.UK,"₹ %d", halfPrice));
        TextView textView1=findViewById(R.id.top_text);
        textView1.setText(String.format(Locale.UK,"Pay ₹ %d", halfPrice));
        TextView textView2=findViewById(R.id.bottom_text);
        textView2.setText(String.format(Locale.UK,"Pay ₹ %d", fullPrice));
        final CardView view1= findViewById(R.id.cart_type_selector);
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
    void change_cart_type(boolean top){
        if (top){
            TextView textView=findViewById(R.id.top_text);
            if (textView.getText().toString().trim().toUpperCase().contains(String.valueOf(fullPrice))){
                findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
                payAmount=fullPrice;
                Log.e("PayChange","Full->"+payAmount);
            }else{
                payAmount=halfPrice;
                Log.e("PayChange","Half->"+payAmount);
                findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
            }
        }
        if (!top){
            TextView textView=findViewById(R.id.bottom_text);
            if (textView.getText().toString().trim().toUpperCase().contains(String.valueOf(fullPrice))){
                change_type("Pay ₹ "+fullPrice,"Pay  ₹ "+halfPrice+"      ");
                payAmount=fullPrice;

                Log.e("PayChange","Half->"+payAmount);

                findViewById(R.id.cart_type_selector).setVisibility(View.GONE);
            }else{
                change_type("Pay ₹ "+halfPrice+"      ","Pay ₹ "+fullPrice);
                Log.e("PayChange","Full->"+payAmount);
                payAmount=halfPrice;
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
        if (top.toUpperCase().contains(String.valueOf(fullPrice))){
//            img_top=R.drawable.cake;
            top="₹ "+fullPrice;
            payAmount=fullPrice;
        }else{
//            img_top=R.drawable.ic_50_percent;
            top="₹ "+halfPrice;
            payAmount=halfPrice;
        }
        bottom.toUpperCase();
        String.valueOf(fullPrice);//            img_bottom=R.drawable.cake;
        textView=findViewById(R.id.name);
        textView.setText(top);

    }
    private void gotoPayU() {
        Intent intent=new Intent(this, PayUWalletActivity.class);
        intent.putExtra("Repay",getIntent().getBooleanExtra("Repay",false));
        intent.putExtra("Order",orderno);
        intent.putExtra("TodaysTime",TodaysDate);
        intent.putExtra("DeliveryTime",DeliveryTime);
        intent.putExtra("Main_Order_No", getIntent().getStringExtra("Main_Order_No"));

        intent.putExtra("Status","Pending");
//        intent.putExtra("Pay_Amount",Amount);
        intent.putExtra("Pay_Amount",String.valueOf(payAmount));
        intent.putExtra("Total_Amount",Total_Amount);
        intent.putExtra("UId",Uid);
        intent.putExtra("Total_Food",total_food);
        intent.putExtra("From", from);
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
        // finish();
    }
    void gotopaytm(){
        Intent intent=new Intent(this, paytm_payments.class);
         intent.putExtra("Order",orderno);
         intent.putExtra("Repay",getIntent().getBooleanExtra("Repay",false));
        intent.putExtra("TodaysTime",TodaysDate);
        intent.putExtra("DeliveryTime",DeliveryTime);
        intent.putExtra("Status","Pending");
//        intent.putExtra("Pay_Amount",Amount);
        intent.putExtra("Pay_Amount",String.valueOf(payAmount));
        intent.putExtra("Total_Amount",Total_Amount);
        intent.putExtra("UId",Uid);
        intent.putExtra("Total_Food",total_food);
        intent.putExtra("From", from);
        intent.putExtra("Address",Address);
        intent.putExtra("Tax",Tax);
        intent.putExtra("OtherPayment",Other);
        intent.putExtra("Main_Order_No", getIntent().getStringExtra("Main_Order_No"));
        intent.putExtra("Image0",Img0);
        try {
            intent.putExtra("Image1", Img1);
            intent.putExtra("Image2", Img2 );
            intent.putExtra("Image3", Img3);
        }catch (Exception IndexOutOfBoundsException){}
startActivity(intent);
       // finish();
        }
    @Override
    protected void onStart() {

        super.onStart();
        findViewById(R.id.paybutton).setEnabled(true);
        findViewById(R.id.paybutton).setVisibility(View.VISIBLE);

         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }



    private void receive(){
    try {
        Intent intent = getIntent();
        orderno = intent.getStringExtra("Order");
       TodaysDate=   intent.getStringExtra("TodaysTime");
       DeliveryTime= intent.getStringExtra("DeliveryTime");
     Status=   intent.getStringExtra("Status");
      Total_Amount=  intent.getStringExtra("Total_Amount");
      final_pay_amount=  intent.getStringExtra("Pay_Amount");
        Uid= intent.getStringExtra("UId");
        from= intent.getStringExtra("From");
        total_food=intent.getStringExtra("Total_Food");
        Address=intent.getStringExtra("Address");
        Tax=intent.getStringExtra("Tax");
        Other=intent.getStringExtra("OtherPayment");
        Log.e("Address","Payment "+Address);

      try {
      Img0=     intent.getStringExtra("Image0");
       Img1=   intent.getStringExtra("Image1" );
        Img2=  intent.getStringExtra("Image2");
        Img3=  intent.getStringExtra("Image3" );
      }catch (Exception NullPointerException){NullPointerException.printStackTrace();}
    }catch (Exception e){e.printStackTrace();}
        Log.e("Received At payment","order "+orderno+"Todays date "+TodaysDate+" delivery "+DeliveryTime
                +" Status "+Status+" TotalAmount "+Total_Amount+" Amount "+Amount+" UID "+Uid+" total_food "+total_food+" Address"+
                Address+" Tex "+Tax+" Other "+Other);
//    full.setText(String.format("%s %s %s", getString(R.string.pay), getString(R.string.rupeesSymbol),Total_Amount));
//    half.setText(String.format("%s %s %s", getString(R.string.pay),getString(R.string.rupeesSymbol), String.valueOf(Integer.parseInt(Total_Amount) / 2)));
    half.setText(R.string.pay_by_paytm);
    }

    public void onbackclicked(View view) {
        view.setEnabled(false);
        super.onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

