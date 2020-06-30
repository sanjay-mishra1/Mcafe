package com.sanproject.mcafe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class orderstatus  extends AppCompatActivity {

    private String orderno;
    private String TodaysTime;
    private String DelTime;
 //   private String Status="Pending";
    private String Total_Amount;
    private String Uid;
    private String Img0,Img1,Img2,Img3;
     private String Total_Food;
    private String Pay_amount;
    private String back;
    private String from="";
    private String Address;
    private String Other;
    private String Tax;
    private int check=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_status);

         receive();

        if (from.contains("Edit")){
            TextView textView2=findViewById(R.id.PaymentStatus1);
            textView2.setText(String.format(Locale.UK,"Transaction successful for the Amount of" +
                    " ₹ %s", Pay_amount));
            TextView textView=findViewById(R.id.textview);
            orderno=getIntent().getStringExtra("Order");
            textView.setText(String.format(Locale.UK,"Remaining ₹ %s is paid for the \norder no." +
                    " %s", Pay_amount, orderno));
            findViewById(R.id.gotoorders).setVisibility(View.GONE);
            Button n=findViewById(R.id.tomenu);
            n.setText(R.string.bac_orders);
            findViewById(R.id.loading).setVisibility(View.GONE);
            findViewById(R.id.goback).setVisibility(View.GONE);

        }

        findViewById(R.id.goteditorders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.contains("Edit")){

                    findViewById(R.id.goteditorders).setVisibility(View.GONE);
                }else{

                        empty_cart();

                    Intent intent = new Intent(orderstatus.this, editOrder.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("OrderNo", orderno);
                    intent.putExtra("From", "orderStatus");
                   // intent.putExtra("Total_Food", Total_Food);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

  /*  void save_tax(){
        FirebaseDatabase.getInstance().getReference("Tax/"+orderno).child("Tax").setValue(Other);
    }*/

     void receive(){
        back="false";
        Intent intent=getIntent();
        TextView toolbar=findViewById(R.id.toolbartext);
        from=intent.getStringExtra("From");
        Address=intent.getStringExtra("Address");
        Log.e("Address","Order status "+Address);
        Other=intent.getStringExtra("OtherPayment");
        Tax=intent.getStringExtra("Tax");
         TodaysTime= intent.getStringExtra("TodaysTime" );
         DelTime= intent.getStringExtra("DeliveryTime" );
         Total_Amount=  intent.getStringExtra("Total_Amount" );
         Pay_amount=intent.getStringExtra("Pay_Amount" );
         String bank = intent.getStringExtra("Bank");
         String transaction = intent.getStringExtra("Transaction");
         Total_Food=  intent.getStringExtra("Total_Food");
         Uid=  intent.getStringExtra("UId" );
         Img0=  intent.getStringExtra("Image0" );
         try {

             Img1= intent.getStringExtra("Image1" );
             Img2=intent.getStringExtra("Image2"  );
             Img3= intent.getStringExtra("Image3" );
         }catch (Exception ignored){}
         Log.e("Message_OrderStatus",intent.getStringExtra("Message")+" From"+from);
         if (intent.getStringExtra("Message").contains("Success")
                 &&intent.getStringExtra("From").contains("Edit") )
            {
                GifImageView gifImageView=findViewById(R.id.imageView);
                gifImageView.setImageResource(R.drawable.successimage);
                 RelativeLayout r=findViewById(R.id.sucessfulTransaction);
                r.setVisibility(View.VISIBLE);
                RelativeLayout r1=findViewById(R.id.TransactionFailed);
                r1.setVisibility(View.GONE);
                toolbar.setText(R.string.sucessful);
                TextView textView;

                textView=findViewById(R.id.orderid );
                 textView.setText( intent.getStringExtra("Order" ));


                textView=findViewById(R.id.deliverytime);
                textView.setText(intent.getStringExtra("DeliveryTime" ));


                textView=findViewById(R.id.amount1);
                Pay_amount=intent.getStringExtra("Pay_Amount" );
                textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), Pay_amount));

                textView=findViewById(R.id.bank);
                bank =intent.getStringExtra("Bank");
                textView.setText(bank);


                textView=findViewById(R.id.transactionid);
                transaction =intent.getStringExtra("Transaction" );
                textView.setText(transaction);
                back="true";

            }
            else  if (intent.getStringExtra("Message").contains("Success") &&intent.getStringExtra("From").contains("OrderFood") )
            {
                GifImageView gifImageView=findViewById(R.id.imageView);
                gifImageView.setImageResource(R.drawable.successimage);
            RelativeLayout r=findViewById(R.id.sucessfulTransaction);
        r.setVisibility(View.VISIBLE);
        RelativeLayout r1=findViewById(R.id.TransactionFailed);
        r1.setVisibility(View.GONE);
        toolbar.setText(R.string.sucessful);
        TextView textView;
        textView=findViewById(R.id.orderid );
        orderno=  intent.getStringExtra("Order" );
        textView.setText(orderno);
        TodaysTime= intent.getStringExtra("TodaysTime" );
        DelTime= intent.getStringExtra("DeliveryTime" );
        textView=findViewById(R.id.deliverytime);
        textView.setText(DelTime);
       // Status=  "Pending";
        Total_Amount=  intent.getStringExtra("Total_Amount" );
        textView=findViewById(R.id.amount1);
        Pay_amount=intent.getStringExtra("Pay_Amount" );
        textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), Pay_amount));
        textView=findViewById(R.id.bank);
        bank =intent.getStringExtra("Bank");
        textView.setText(bank);
            textView=findViewById(R.id.transactionid);
            transaction =intent.getStringExtra("Transaction" );
            textView.setText(transaction);

        Total_Food=  intent.getStringExtra("Total_Food");
                Uid=  intent.getStringExtra("UId" );
          Img0=  intent.getStringExtra("Image0" );
            try {

                Img1= intent.getStringExtra("Image1" );
                Img2=intent.getStringExtra("Image2"  );
                Img3= intent.getStringExtra("Image3" );
            }catch (Exception NullPointerException){}
           // contentProgrss.setVisibility(View.VISIBLE);
            store();
        }
        else{            findViewById(R.id.loading).setVisibility(View.GONE);

            toolbar.setText("Transaction failed");
            findViewById(R.id.goback).setVisibility(View.VISIBLE);

            Uid=intent.getStringExtra("UId");
            orderno=  intent.getStringExtra("Order" );
            Total_Food=intent.getStringExtra("Total_Food");
            RelativeLayout r=findViewById(R.id.TransactionFailed);
            r.setVisibility(View.VISIBLE);
            RelativeLayout r1=findViewById(R.id.sucessfulTransaction);
            r1.setVisibility(View.GONE);

            TextView textView=findViewById(R.id.orderId);
            textView.setText(intent.getStringExtra("Order" ));
             textView=findViewById(R.id.ExtraInfo);
            textView.setText(intent.getStringExtra("ExtraInfo"));
            textView=findViewById(R.id.amount);
            textView.setText(String.format("%s%s", getString(R.string.rupeesSymbol), intent.getStringExtra("Pay_Amount")));
            Total_Amount=intent.getStringExtra("Total_Amount");
            back="true";

        }


    }


    void makeInvisible(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loading).setVisibility(View.GONE);
                back="true";
                Log.e("Check","check is "+"Saved successfully");
            }
        },2000);
    }

    public void backtomenu(View view) {
        view.setEnabled(false);
        if (from.contains("Edit")){
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
            //startActivity(new Intent(this,editOrder.class));
//            finish();
        }else{Intent intent1=getIntent();
            if (intent1.getStringExtra("Message").contains("Success") && intent1.getStringExtra("From").contains("OrderFood"))
            {
                empty_cart();
            }
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        }
    }
        void empty_cart(){
             FirebaseDatabase.getInstance().getReference(Address).setValue(null);
        }
    public void gobackTopayment(View view) {
        view.setEnabled(false);
        Intent intent=new Intent(this,payment.class);
            intent.putExtra("Order", orderno);
            intent.putExtra("TodaysTime", TodaysTime);
            intent.putExtra("DeliveryTime", DelTime);
            intent.putExtra("Status", "Pending");
            intent.putExtra("Total_Amount", Total_Amount);
            intent.putExtra("UId", Uid);
            intent.putExtra("Total_Food", Total_Food);
            intent.putExtra("Tax", Tax);
            intent.putExtra("OtherPayment", Other);
            intent.putExtra("Image0", Img0);
            intent.putExtra("From",from);
            intent.putExtra("Address", Address);
        Log.e("Received At orderstatus","order "+orderno+"Todays date "+TodaysTime+" delivery "+DelTime
                +" Status "+"Pending"+" TotalAmount "+Total_Amount+" Amount "+Pay_amount+" UID "+Uid+" total_food "+Total_Food+" Address"+
                Address+" Tex "+Tax+" Other "+Other);
            //Log.e("Addre")
            try {
                intent.putExtra("Image1", Img1);
                intent.putExtra("Image2", Img2);
                intent.putExtra("Image3", Img3);
            } catch (Exception IndexOutOfBoundsException) {
            }
            startActivity(intent);
      //  finish();

     }

    @Override
    public void onBackPressed() {
        if (back.contains("true")) {
            finish();
            //startActivity(new Intent(this, MainActivity.class));
            if (from.contains("Edit")){
                 //startActivity(new Intent(this,editOrder.class));
//                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{//here finish
                Intent intent1=getIntent();
                    if (intent1.getStringExtra("Message").contains("Success") && intent1.getStringExtra("From").contains("OrderFood"))
                    {
                       empty_cart();
                    }
                        Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                        finish();

            }
        }
    }
    private void store() {
        String formetted_duration= DateFormat.getDateTimeInstance().format(new Date());
        String OrderN=orderno;
        newStore(OrderN,formetted_duration,"Orders/"+Canteen+"/"+"New Orders/"+OrderN, Pay_amount,
                Address);
        newStore(OrderN,formetted_duration,"Orders/"+Canteen+"/"+"All Orders/"+OrderN,Pay_amount,
                Address);
        newStore(OrderN,formetted_duration,"User Informations/"+Uid+"/Orders"+
                "/All orders/"+OrderN,Pay_amount, Address);
        //save_tax();
        newStore(OrderN,formetted_duration,"User Informations/"+Uid+"/Orders"+
                "/Pending/"+OrderN,Pay_amount,Address);



     }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.goback).setEnabled(true);
        findViewById(R.id.tomenu).setEnabled(true);
    }

    public void newStore(final String Order, final String Time, String Address, final String PaymentStatus, String sourceAddress){
        final DatabaseReference d1= FirebaseDatabase.getInstance().getReference().child(Address);

        final DatabaseReference source=FirebaseDatabase.getInstance().getReference().child(sourceAddress);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                d1.child("Food").setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        d1.child("OrderNo").setValue(Order);
                        d1.child("Delivery").setValue(DelTime);
                        d1.child("Tax").setValue(Tax);
                        d1.child("OtherPayment").setValue(Other);
                        d1.child("Time").setValue(Time);
                        d1.child("Status").setValue("Pending");
                        d1.child("Payment").setValue(PaymentStatus);
                        d1.child("UserId").setValue(Uid);
                        d1.child("Image0").setValue(Img0);
                        d1.child("TotalFood").setValue(Total_Food);

                        source.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                d1.child("TotalFood").setValue(Long.toString ((int) dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        d1.child("TotalFood").setValue(Total_Food);
                        d1.child("TotalAmount").setValue( Total_Amount);
                        try {
                            if (!Img1.isEmpty())
                                d1.child("Image1").setValue(Img1);
                            if (!Img2.isEmpty())
                                d1.child("Image2").setValue(Img2);
                            if (!Img3.isEmpty())
                                d1.child("Image3").setValue(Img3);

                        }catch (Exception ignored){}

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        check();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        source.addListenerForSingleValueEvent(valueEventListener);

    }
    void check(){
        check++;
        Log.e("Check","check is "+check);

        if (check>=4){
            makeInvisible();

        }



    }


}

