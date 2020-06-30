package com.sanproject.mcafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class pendingorders extends Fragment {
    public String Selected_foodName[];
    public int Selected_foodPrice[];
    public String Name;
    public String M_name,M_price,M_quantity,extra_t;
    public String Image[];
   // private TextView total_food;
    ImageView next;
    String b="0" ;
    public int count = 0;
    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
   // private FirebaseAuth.AuthStateListener mAuthlistener;
    private RecyclerView recyclerView;
    private adapter_allorders adapter;
   // private List<album_allorders> albumList;
    RelativeLayout relativeLayout;
    LinearLayout mainLinear;
    ImageView dec;
    Button cancel;
    ProgressBar progressBar;
    public pendingorders() {

    }
public View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.recycler_allorders, container, false);
        Selected_foodName = new String[10];
        progressBar=view.findViewById(R.id.progressFood);
        progressBar.setVisibility(View.VISIBLE);

        Selected_foodPrice = new int[10];
       // Image = new String[10];
        next = view.findViewById(R.id.next_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
     //   total_food = view.findViewById(R.id.total_foodItem);
        mainLinear=view.findViewById(R.id.main_relative_orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        cancel=view.findViewById(R.id.cancel_order);
        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/"+mAuth.getCurrentUser().getUid()+"/Orders/Pending");
        view.setFocusableInTouchMode(true);
        view.requestFocus();
     /*   view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==keyEvent.ACTION_DOWN){
                    if(i==KeyEvent.KEYCODE_BACK){
                         getActivity().finish();
                        Intent intent=new Intent(getActivity(),MainActivity.class);
                       startActivity(intent);

                        return true;
                    }
                }
                return false;}
        });*/

        super.onStart();

        return view;

    }

    boolean checkOrders(DatabaseReference address){
        final boolean[] status = {true};
        address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0){
                    view.findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
                    status[0] =false;
                    activate_ok_listener();


                }
                progressBar.setVisibility(View.GONE);
            }

            private void activate_ok_listener() {
               Button b= view.findViewById(R.id.ok_button);
               b.setText(R.string.menu);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                        Intent intent=new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return status[0];
    }

    @Override
    public void onStart() {
         super.onStart();
         if (checkOrders(mDatabase)) {
             FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                     album_allorders.class,
                     R.layout.new_orders_layout,
                     mainactiv_allorders.FoodViewHolder.class,
                     mDatabase) {
                 @Override
                 protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {

                     viewHolder.setStatus(model.getStatus());


                     viewHolder.setOrderNo(model.getOrderNo());
                     viewHolder.setDelivery(model.getDelivery());


                     viewHolder.setPayment(model.getPayment());
                     try {
                         viewHolder.setPayment2(String.valueOf((Integer.parseInt(model.getTotalAmount()) - Integer.parseInt(model.getPayment()))));

                         viewHolder.setMore(String.valueOf((Integer.parseInt(model.getTotalFood()) - 4)));
                     } catch (Exception NumberFormatException) {
                     }
                     // viewHolder.setMore(String.valueOf(model.getTotalFood()));
                   /*  int img=0;
                     if (model.getImage0()!=null)
                         img++;
                     if (model.getImage1()!=null)
                         img++;
                     if (model.getImage2()!=null)
                         img++;
                     if (model.getImage3()!=null)
                         img++;
                     viewHolder.setImage_Visibility(img);   */
                     viewHolder.setImage0(model.getImage0());
                     viewHolder.setImage1(model.getImage1());
                     viewHolder.setImage2(model.getImage2());
                     viewHolder.setImage3(model.getImage3());


                     viewHolder.Relative().setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                              Intent intent = new Intent(getActivity(), editOrder.class);
                             intent.putExtra("OrderNo", model.getOrderNo());
                             intent.putExtra("From", "Orders");
                             startActivity(intent);
                         }
                     });


                 }


             };
             recyclerView.setAdapter(FBRA);
         }
    }

    public void total(String t){
extra_t=t;
    }
    public String set_t(){
      return extra_t;
    }
    void Change(String name){
        int loc=name.indexOf("Name");
        int loc_price=name.indexOf("Price");
        M_name=name.substring(loc+5, loc_price);
        int loc_quan=name.indexOf("Quantity");
        M_price=name.substring(loc_price+6, loc_quan-1);
        M_quantity=name.substring(loc_quan+9, name.length());
    }


}