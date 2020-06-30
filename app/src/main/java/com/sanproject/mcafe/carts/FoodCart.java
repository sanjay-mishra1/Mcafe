package com.sanproject.mcafe.carts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.sanproject.mcafe.food_info;
import com.sanproject.mcafe.mainactiv;
import com.sanproject.mcafe.orderFood;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

public class FoodCart extends Fragment {
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
    // ArrayList<Integer> img ;
    GifImageView gifImageView;
    public int foodcount=0;
    private ArrayList<String> foodtype;
    private long cartSize=0;
    private Dialog progressDialog;
    //  public int[] foodQ;
    boolean sendActivity=true;
    public FoodCart() {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_final__food_selelction, container, false);
         recyclerView1 = view.findViewById(R.id.food);
        am=view.findViewById(R.id.total_price2);
        foodcount=0;
        food= new ArrayList<>(30);
        foodQ= new ArrayList<>(30);
        Price= new ArrayList<>(30);
        foodtype= new ArrayList<>(30);
        del= new ArrayList<>(30);
          gifImageView = view.findViewById(R.id.emptyCart);
        gifImageView.setImageResource(R.drawable.ufo);
        mAuth=FirebaseAuth.getInstance();

        CheckforCakeAvailability();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
        //initial_total();
         view.findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Total_amount=0;
                showProgress_dialog();
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


                }catch (Exception ignored){}

               // Intent intent =new Intent(getActivity(),orderFood.class);
                //intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
                check_food(true);
               // startActivity(intent);

            }
        });




        return view;
    }
void setSendActivity(){

        if (sendActivity){
             Intent intent =new Intent(getActivity(), orderFood.class);
             intent.putExtra("Address","User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
            //check_food(true);
             startActivity(intent);

        }else{
            Toast.makeText(getContext(),"Your cart is updated... ",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
}
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initial_total();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void reset(){

             initial_total();


    }
void check_food(final boolean activity){
    sendActivity=true;
    for (int i=0;i<foodQ.size();i++){
            final int finalI = i;
            Log.e("Found food","Inside for loop "+food.get(i));

            FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+foodtype.get(i)+"/"+food.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        FirebaseDatabase.getInstance().getReference("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food")
                                .child(food.get(finalI)).setValue(null);
                        Log.e("Found food","Food name changed");
                        reset();
                        sendActivity=false;

                    }else Log.e("Found food","Food name not changed "+food.get(finalI)+" data "+dataSnapshot);

                        if (finalI + 1 == foodQ.size()) {
                            if (activity) {
                            setSendActivity();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Found food","Nothing found "+databaseError.toString());
                }
            });

        }
}
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void initial_total(){

         DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("User Informations/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid()+"/FoodCart/Food");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int t=0;
                foodQ=new ArrayList<>();
                food=new ArrayList<>();
                foodtype=new ArrayList<>();
                Price=new ArrayList<>();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Food foods=dataSnapshot1.getValue(Food.class);
                    try {
                          t  =  Objects.requireNonNull(foods).getPrice()*Integer.parseInt(foods.getQuantity()) + t ;

                        try {food.add(foods.getFood_name());
                             Price.add(  foods.getPrice());
                            foodQ.add(Integer.parseInt(foods.getQuantity()));
                            foodtype.add(foods.getType());
                        }catch(Exception ignored){}

                        am.setText(String.format("₹%s", Integer.toString(t)));
                    }catch (Exception ignored){}

                }
                check_food(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

     @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     void CheckforCakeAvailability( ){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference data1= FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/FoodCart/Food");
        data1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartSize=dataSnapshot.getChildrenCount();
                try{if (dataSnapshot.getChildrenCount()==0){
                     visiblity();
                }}catch (Exception NullPointerException){
                    visiblity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        load_Firebase();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        initial_total();
        view.findViewById(R.id.checkout).setEnabled(true);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void load_Firebase() {
       // super.onStart();
        ProgressBar progressBar1=view.findViewById(R.id.progressFood);
         LinearLayoutManager horizontal=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView1.setLayoutManager(horizontal);
        recyclerView1.setAdapter(adapter);
         recyclerView1.setItemAnimator(new DefaultItemAnimator());
       try {
            Firebase("User Informations/"+ Objects.requireNonNull(mAuth.getCurrentUser()).
                    getUid()+"/FoodCart/Food",recyclerView1,R.layout.foodcart,progressBar1,"1");
       }catch (Exception e){
           Toast.makeText(getContext(),"Unable to load the cart.Please check your network connection",Toast.LENGTH_SHORT).show();
           Objects.requireNonNull(getActivity()).finish();
       }
     }



    public void Firebase(String address, RecyclerView recyclerView, int layout, final ProgressBar progressBar, final String Type){

        Total_amount=0;
        final String[] sa = new String[1];
        progressBar.setVisibility(View.VISIBLE);
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
                viewHolder.setImageID(model.getFood_Image());

                try {
                    viewHolder.setQuantity(String.valueOf(foodQ.get(food.indexOf(model.getFood_name()))));
                } catch (Exception ArrayIndexOutOfBoundsException) {
                    viewHolder.setQuantity("1");

                }

                viewHolder.setListnercancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase1.child(model.getFood_name()).setValue(null);
                        Toast.makeText(getActivity(), "cancel", Toast.LENGTH_LONG).show();
                        delete(model.getFood_name());
                        SetTotal();
                    }
                });
                viewHolder.setListnerforplus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sa[0] = String.valueOf(storeQunatity(model.getFood_name(), 'a'));
                        viewHolder.setQuantity(sa[0]);
                        SetTotal();
                    }
                });
                viewHolder.setListnerforminus().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sa[0] = String.valueOf(storeQunatity(model.getFood_name(), 's'));
                        viewHolder.setQuantity(sa[0]);
                        SetTotal();
                    }
                });
            viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), food_info.class);
                    intent.putExtra("Image", model.getFood_Image());
                    intent.putExtra("Name", model.getFood_name());
                    intent.putExtra("Price",String.valueOf(model.getPrice()));
                    intent.putExtra("Discount",String.valueOf(model.getPrice()));
                    intent.putExtra("Hide",false);
                     intent.putExtra("Description", model.getDescription());
                    intent.putExtra("Type", model.getType());
                  //  intent.putExtra("Total_orders",String.valueOf(model.getTotal_orders()));
                  //  intent.putExtra("No_of_Ratings",String.valueOf(model.getTotal_NoOfTime_Rated()));
                   // intent.putExtra("Ratings",String.valueOf(model.getSum_of_Ratings()));
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation
                                    (getActivity(), viewHolder.getImageId(), "My_Animation");
                    startActivity(intent, optionsCompat.toBundle());

                }
            });
                //SetTotal();
                //foodcount++;

                 progressBar.setVisibility(View.INVISIBLE);


        }
        };

        recyclerView.setAdapter(FBRA);

        // storeQunatity("nothing");
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
    view. findViewById(R.id.progressFood).setVisibility(View.GONE);
    view.findViewById(R.id.checkout).setVisibility(View.GONE);
    view.findViewById(R.id.total_price2).setVisibility(View.GONE);
}
    private void delete(String food_name) {
        try{int loc=food.indexOf(food_name);
            food.remove(loc);
            foodQ.remove(loc);
            Price.remove(loc);
            cartSize--;
        if (cartSize<=0){
            visiblity();
        }
        }catch (Exception NullPointerException){}
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

     public void expand_To_Bottom(final View v, int duration, int targetHeight){

        int prevHeight =v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt( prevHeight,targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public   void expand(final View v, final int duration, final int targetHeight) {

       int prevHeight = v.getWidth();
     //  int prevHeight =500;

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    expand_To_Bottom(view,400,300);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
                valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getWidth();
        Display display;
        display =getActivity(). getWindowManager().getDefaultDisplay();
        //(int) v.getContext().getResources().getDisplayMetrics().density
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, 0);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //endAnimation(12, findViewById(R.id.progressbarRelative)  );
                       // levitate(findViewById(R.id.progressbarRelative), 12, true, false);
                    }
                });
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();


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
                return i == KeyEvent.KEYCODE_BACK;

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