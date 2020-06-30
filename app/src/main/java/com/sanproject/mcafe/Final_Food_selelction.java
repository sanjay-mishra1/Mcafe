package com.sanproject.mcafe;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Final_Food_selelction extends AppCompatActivity {
     private AlbumsAdapter adapter;
Button next;
//private EditText editTime;
   // int[] Quantitys = new int[7];
    ImageButton Back;
    TextView am;
int Total_amount=0;
    public DatabaseReference mDatabase;
     DatabaseReference databaseReference;
    FirebaseAuth mAuth;
RecyclerView recyclerView1;
     ArrayList<String> food ;
    ArrayList<Integer> foodQ ;
    ArrayList<Integer> Price ;
    ArrayList<String> del ;
   // ArrayList<Integer> img ;

    public int foodcount=0;
    private ArrayList<String> foodtype;
    //  public int[] foodQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final__food_selelction);
      //  recyclerView = (RecyclerView) findViewById(R.id.horizontalview);
        recyclerView1 = (RecyclerView) findViewById(R.id.food);
        am=findViewById(R.id.total_price2);
        foodcount=0;
        food=new ArrayList <String> (30 );
        foodQ=new ArrayList <Integer> ( 30);
        Price=new ArrayList <Integer> (30 );
        foodtype=new ArrayList <String> (30 );
        del=new ArrayList<String>( 30);
//        horizontal();
     //   shared=this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
        toolbar.setBackgroundColor(Color.WHITE);
         setSupportActionBar(toolbar);

       mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart");
         initial_total();

         findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
              @Override
            public void onClick(View view) {
                  //Total_amount=0;

                     final int[] success = new int[1];
                  try {

                      for (int i = 0; i < foodQ.size(); i++)
                      {        databaseReference.child(food.get(i)).child("Quantity").setValue(Integer.toString(foodQ.get(i))).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              success[0]++;

                          }
                      });
                      }


                  }catch (Exception ArrayIndexOutOfBoundsException){}

                      Intent intent =new Intent(Final_Food_selelction.this,orderFood.class);
                      intent.putExtra("Type",foodtype);
                       startActivity(intent);
                      finish();
             }
        });
        Back = (ImageButton) findViewById(R.id.backpopupweight);
        Back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                 Intent intent=new Intent(Final_Food_selelction.this,MainActivity.class);
                 startActivity(intent);


            }
        });

    }


    void initial_total(){

        //final int[] t = new int[1];
        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               int t=0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Food foods=dataSnapshot1.getValue(Food.class);
                    try {
                     //   t  = Integer.parseInt(foods.getPrice())*Integer.parseInt(foods.getQuantity()) + t ;

                        try {food.add(foods.getFood_name());
                       //     Price.add(Integer.parseInt(foods.getPrice()));
                            foodQ.add(Integer.parseInt(foods.getQuantity()));
                            foodtype.add(foods.getType());
                         }catch(Exception  IndexOutOfBoundsException){}

                        am.setText("₹"+Integer.toString(t));
                     }catch (Exception NumberFormatException){}

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    void horizontal(){
    LinearLayoutManager horizontal=new LinearLayoutManager(Final_Food_selelction.this,LinearLayoutManager.HORIZONTAL,false);
   // recyclerView.setLayoutManager(horizontal);
    //recyclerView.setAdapter(adapter);
    mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/Beverages");
    //recyclerView.setAdapter(adapter);

}

 @Override
protected void onStart() {
    super.onStart();
      LinearLayoutManager horizontal=new LinearLayoutManager(Final_Food_selelction.this, LinearLayoutManager.VERTICAL,false);
    recyclerView1.setLayoutManager(horizontal);
    recyclerView1.setAdapter(adapter);
   // recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView1.setItemAnimator(new DefaultItemAnimator());
   //totalamount( );
    Firebase("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart",recyclerView1,R.layout.foodcart, "1");
   // Firebase("Food Items/Beverages",recyclerView,R.layout.horizontallist ,"2");
    //SetTotal();
}
private void addNewTAB(){
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();
  //  fragmentTransaction.replace(R.id.FrameCart, new cakeDetailView.orderFragment1()).commit();
}

void CheckforCakeAvailability( ){
    DatabaseReference data1=FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
     data1.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             try{if (dataSnapshot.getChildrenCount()!=0){
                 findViewById(R.id.appbarSelection).setVisibility(View.GONE);
                 addNewTAB();
             }}catch (Exception NullPointerException){

             }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
      }

 public void Firebase(String address, RecyclerView recyclerView, int layout,   final String Type){

        Total_amount=0;
        final String[] sa = new String[1];
         final DatabaseReference mDatabase1= FirebaseDatabase.getInstance().getReference().child(address);
        FirebaseRecyclerAdapter<Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                layout,
                mainactiv.FoodViewHolder.class,
                mDatabase1

        ) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.setName(model.getFood_name());
                //viewHolder.setPrice(model.getPrice());
                viewHolder.setDescVisibility();


              //  am.setText(Integer.toString(Total_amount));

                if (Objects.equals(Type, "1")) {viewHolder.setImageID(model.getFood_Image());
                     try {
                         viewHolder.setQuantity(String.valueOf(foodQ.get(food.indexOf(model.getFood_name()))));
                     }catch (Exception ArrayIndexOutOfBoundsException){
                         viewHolder.setQuantity("1");

                     }
                       viewHolder.setListnercancel().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDatabase1.child(model.getFood_name()).setValue(null);
                            Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
                            delete(model.getFood_name());
                            SetTotal( );
                        }
                    });
                    viewHolder.setListnerforplus().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sa[0] = String.valueOf(storeQunatity(model.getFood_name(), 'a'));
                            viewHolder.setQuantity(sa[0]);
                            SetTotal( );
                         }
                    });
                    viewHolder.setListnerforminus().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sa[0] = String.valueOf(storeQunatity(model.getFood_name(), 's'));
                            viewHolder.setQuantity(sa[0]);
                            SetTotal( );
                        }
                    });

                    //SetTotal();
                    //foodcount++;
                }
                else  viewHolder.setImage(getApplicationContext(), model.getFood_Image());


            }
        };

        recyclerView.setAdapter(FBRA);

  // storeQunatity("nothing");
    }

    private void delete(String food_name) {
        try{int loc=food.indexOf(food_name);
            food.remove(loc);
          foodQ.remove(loc);
        Price.remove(loc);}catch (Exception NullPointerException){}
        foodcount--;
       // SetTotal();

    }

     int storeQunatity(String name,char s)
    {
        int j=  food.indexOf(name);
             if ( s=='a'){
                foodQ.set(j,foodQ.get(j)+1);
                if (foodQ.get( j) ==15)
                foodQ.set(j,1);

             }
            else {                 foodQ.set(j,foodQ.get(j)-1);

                if (foodQ.get( j)==0)
                    foodQ.set(j,1);

             }
     return foodQ.get(j);
    }

    void SetTotal(){
        Total_amount=0;
        try {
            for (int i = 0; i < foodQ.size(); i++) {
                Total_amount = Total_amount + Price.get(i) * foodQ.get(i);
            }
         }catch (Exception  ArrayIndexOutOfBoundsException){}
     //TextView am=findViewById(R.id.total_price2);
     am.setText("₹"+Integer.toString(Total_amount ));
     }

}
