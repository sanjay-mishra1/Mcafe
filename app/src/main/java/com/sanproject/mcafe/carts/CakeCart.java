package com.sanproject.mcafe.carts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
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
import com.sanproject.mcafe.AlbumsAdapter;
import com.sanproject.mcafe.Food;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.mainactiv;
import com.sanproject.mcafe.orderFood;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class CakeCart extends Fragment {
    View view;
    private AlbumsAdapter adapter;
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
     public int foodcount=0;
    private ArrayList<String> foodtype;
    private long cartSize;
    GifImageView gifImageView;
    private Dialog progressDialog;
    private boolean sendActivity=true;

    public CakeCart() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tablayout_foodcart, container, false);
       // recyclerView = (RecyclerView) view.findViewById(R.id.horizontalview);
        recyclerView1 = (RecyclerView) view.findViewById(R.id.food);
        am=view.findViewById(R.id.total_price2);
        foodcount=0;
        food=new ArrayList <String> (30 );
        foodQ=new ArrayList <Integer> ( 30);
        Price=new ArrayList <Integer> (30 );
        foodtype=new ArrayList <String> (30 );
        del=new ArrayList<String>( 30);
          gifImageView = (GifImageView) view.findViewById(R.id.emptyCart);
        gifImageView.setImageResource(R.drawable.ufo);


        mAuth=FirebaseAuth.getInstance();
       try {
           databaseReference= FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
           Load_firebase();
       }catch (Exception e){
           Toast.makeText(getContext(),"Unable to load the cart. Please check your network connection",Toast.LENGTH_SHORT).show();
           getActivity().finish();
       }
        initial_total();

        view.findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.findViewById(R.id.checkout).setEnabled(false);
                final int[] success = new int[1];
                try {
                    showProgress_dialog();
                    for (int i = 0; i < foodQ.size(); i++)
                    {        databaseReference.child(food.get(i)).child("Quantity").setValue(Integer.toString(foodQ.get(i))).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            success[0]++;

                        }
                    });
                    }


                }catch (Exception ArrayIndexOutOfBoundsException){}

              //  Intent intent =new Intent(getActivity(),orderFood.class);
              //  intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
              //  startActivity(intent);
                check_food(true);



            }
        });





        return view;
    }
    void setSendActivity(){

        if (sendActivity){
             Intent intent =new Intent(getActivity(), orderFood.class);
             intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
            startActivity(intent);
        }else{
            progressDialog.dismiss();
            Toast.makeText(getContext(),"Your cart is updated... ",Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void reset(){
        initial_total();
    }
    void check_food(final boolean activity){
        sendActivity=true;
        for (int i=0;i<cartSize;i++){
            final int finalI = i;
            String Foodname = food.get(i);
            if (foodtype.get(i).equals("Cake")) {
                Foodname = Foodname.toLowerCase();
                Foodname = food.get(i).substring(0, Foodname.indexOf("cake") + 4);


            }
            if (food.get(i).contains("Shaped Candles")) {
                Foodname = food.get(i).substring(0, food.get(i).indexOf("Candles") + 7);
            }
            FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+foodtype.get(i)+"/"+Foodname).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        FirebaseDatabase.getInstance().getReference("User Informations/"
                                +mAuth.getCurrentUser().getUid()+"/FoodCart/Cake").child(food.get(finalI)).setValue(null);
                        reset();
                        sendActivity=false;
                    }

                        if (finalI + 1 == foodQ.size()) {
                            if (activity) {
                            setSendActivity();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    void initial_total(){

        //final int[] t = new int[1];
        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int t=0;
                foodQ=new ArrayList<>();
                food=new ArrayList<>();
                foodtype=new ArrayList<>();
                Price=new ArrayList<>();
                cartSize=dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Food foods=dataSnapshot1.getValue(Food.class);
                    try {
                      //  t  = Integer.parseInt(foods.getPrice())*Integer.parseInt(foods.getQuantity()) + t ;
                        t  =  foods.getPrice()*Integer.parseInt(foods.getQuantity()) + t ;

                        try {food.add(foods.getFood_name());
                            Price.add( foods.getPrice());
                            foodQ.add(Integer.parseInt(foods.getQuantity()));
                            foodtype.add(foods.getType());
                        }catch(Exception  IndexOutOfBoundsException){}

                        am.setText("₹"+Integer.toString(t));

                    }catch (Exception NumberFormatException){}

                }
                try {
                    check_food(false);
                }catch (Exception e){
                    Log.e("Found food","Exception  "+e.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        view.findViewById(R.id.checkout).setEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        initial_total();
    }

    void Load_firebase() {
      //  ProgressBar progressBar1=view.findViewById(R.id.CakeCartProgress);
       // ProgressBar progressBar2=view.findViewById(R.id.progressFood);
        LinearLayoutManager horizontal=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView1.setLayoutManager(horizontal);
        recyclerView1.setAdapter(adapter);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        //totalamount( );
        Firebase("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake",recyclerView1,R.layout.foodcart,"1");
       // Firebase("Food Items/Beverages",recyclerView,R.layout.horizontallist,progressBar2,"2");
        //SetTotal();
        CheckforCakeAvailability();
    }

    void CheckforCakeAvailability( ){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference data1= FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Cake");
        data1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{if (dataSnapshot.getChildrenCount()==0){
                    // findViewById(R.id.appbarSelection).setVisibility(View.GONE);
                  visiblity();
                }}catch (Exception NullPointerException){
                   visiblity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void Firebase(String address, RecyclerView recyclerView, int layout,  final String Type){

        Total_amount=0;
        final String[] sa = new String[1];
      //  progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference mDatabase1= FirebaseDatabase.getInstance().getReference().child(address);
        FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                layout,
                mainactiv.FoodViewHolder.class,
                mDatabase1

        ) {
             @Override
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                 viewHolder.setDescVisibility();
                  if (model.getType().equals("Cake") ){
                    viewHolder.setListnerforminus().setVisibility(View.INVISIBLE);
                    viewHolder.setListnerforplus().setVisibility(View.INVISIBLE);
                }
                 if (model.getFood_name().contains("Shaped Candles") ){
                     viewHolder.setListnerforminus().setVisibility(View.GONE);
                     viewHolder.setListnerforplus().setVisibility(View.INVISIBLE);
                 }
                //  am.setText(Integer.toString(Total_amount));

                if (Type.equals ("1")) {viewHolder.setImageID(model.getFood_Image());
                    try {
                        viewHolder.setQuantity(String.valueOf(foodQ.get(food.indexOf(model.getFood_name()))));
                    }catch (Exception ArrayIndexOutOfBoundsException){
                        viewHolder.setQuantity("1");

                    }
                    viewHolder.setListnercancel().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDatabase1.child(model.getFood_name()).setValue(null);
                            Toast.makeText(getActivity(), "Deleted from the cart", Toast.LENGTH_LONG).show();
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
                else  viewHolder.setImage(getActivity(), model.getFood_Image());
               // progressBar.setVisibility(View.INVISIBLE);


            }
        };

        recyclerView.setAdapter(FBRA);

     }
    void visiblity(){

      /*  Handler handler=new Handler();
        handler .postDelayed(new Runnable() {
            @Override
            public void run() {
                gifImageView.setVisibility(View.VISIBLE);

            }
        },1000);*/
        gifImageView.setVisibility(View.VISIBLE);
         view.findViewById(R.id.checkout).setVisibility(View.GONE);
        view.findViewById(R.id.total_price2).setVisibility(View.GONE);
    }
    private void delete(String food_name) {
        try{int loc=food.indexOf(food_name);
            food.remove(loc);
            foodQ.remove(loc);
            Price.remove(loc);
            cartSize--;
        if (cartSize==0)
            visiblity();
        }catch (Exception NullPointerException){}
        foodcount--;

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
    void showProgress_dialog(){
        final View dialogView = View.inflate(getContext(), R.layout.progress_layout, null);

        progressDialog = new Dialog(getContext());
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

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            progressDialog.dismiss();
        }catch (Exception ignored){}

    }
}