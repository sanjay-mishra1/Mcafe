package com.sanproject.mcafe;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class mainactiv_allorders  {

   // private RecyclerView mFoodList;
 //   public DatabaseReference mDatabase;
  //  FirebaseAuth mAuth;
   // private FirebaseAuth.AuthStateListener mAuthlistener;
   // private CheckBox checkBox;
   // private Button add_food;
   // Button  menu_button;
   // private int count;
   // String food0,food1,food2,food3,food4,food5,food6;
   // String Total_foods;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFoodList=(RecyclerView) findViewById(R.id.foodlist);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items");
      }*/
/*public void activity(){
        Intent intent=new Intent(this,editOrder.class);
        startActivity(intent);
}*/

  /*  @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthlistener);
        FirebaseRecyclerAdapter<album_allorders, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<album_allorders, FoodViewHolder>(
                album_allorders.class,
                R.layout.activity_all_orders,
                FoodViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, album_allorders model, int position) {
                viewHolder.setFood0(model.getFood0());
                viewHolder.setFood1(model.getFood1());
                viewHolder.setFood2(model.getFood2());
                viewHolder.setFood3(model.getFood3());
                viewHolder.setFood4(model.getFood4());
                viewHolder.setFood5(model.getFood5());
                viewHolder.setFood6(model.getFood6());
                viewHolder.setDelivery(model.getDelivery());
                viewHolder.setOrderNo(model.getOrderNo());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setTime(model.getTime());
                viewHolder.setTotal_Amount(model.getTotalAmount());
                viewHolder.setPayment(model.getPayment());
                viewHolder.setTotal_Food(model.getTotalFood());
                final String food_key = getRef(position).getKey().toString();
                Total_foods=model.getTotalFood();
            }
        };
        //mFoodList.setAdapter(FBRA);
    }
*/
    /*    @Override
    public void onClick(View view) {
        // if(view==add_food){
        //if(count==1)
        {
            //addfood();
            //   Picasso.get().load("drawable/tick.jpg").into(add_food);
            //      Toast.makeText(this,"Adding Food",Toast.LENGTH_SHORT).show();

            //add_food.setImageResource(R.drawable.tick);
            // count++;
        }
         /*   if(count==2){
                //Picasso.get().load("drawable/add_icon.jpg").into(add_food);
                //add_food.setImageResource();
                add_food.setImageResource(R.drawable.add_icon);

               // removefood();
                count=1;
            }*/
//            count++;
        //          Toast.makeText(this,"Adding Food",Toast.LENGTH_SHORT).show();
        //    }
     /*   if(view==menu_button){
            Toast.makeText(this,"Adding Menu ",Toast.LENGTH_SHORT).show();

        }   }*/


    //////////////here////////////////
  /*  private void addfood() {
        //food_record record;
        add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainactiv_allorders.this,"Adding Food",Toast.LENGTH_SHORT).show();

            }
        });
        //  record.name=get
    }
*/


    public static class FoodViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        private String Food0,Food1,Food2,Food3,Food4,Food5,Food6;
        private String Payment;
        private String Status;
        private String Time;
        private String Total_Amount="Total Amount";
        private String Total_Food="Total Food";
        private String Delivery;
        private String OrderNo;
        private CardView mainLinear;
        album_allorders model;
       // String F_food[]={model.getFood0(),model.getFood1(),model.getFood2(),model.getFood3(),model.getFood4(),model.getFood5(),model.getFood6()};
        public void setmView(View mView) {
            this.mView = mView;
        }

        public void visibility(){
            mainLinear=mView.findViewById(R.id.card_view);
            mainLinear.setVisibility(View.GONE);
        }
        public void visibility_Visible(){
            mainLinear=mView.findViewById(R.id.card_view);
            mainLinear.setVisibility(View.VISIBLE);
        }
        public void setFood0(String food0) {
            Food0 = food0;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final11);
            food_name.setText( Food0  );
        }

        public void setFood1(String food1) {
            Food1 = food1;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final12);
            food_name.setText( Food1  );
        }

        public void setFood2(String food2) {
            Food2 = food2;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final13);
            food_name.setText( Food2  );
        }

        public void setFood3(String food3) {
            Food3 = food3;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final14);
            food_name.setText( Food3  );
        }

        public void setFood4(String food4) {
            Food4 = food4;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final15);
            food_name.setText( Food4  );
        }

        public void setFood5(String food5) {
            Food5 = food5;
            TextView food_name=(TextView) mView.findViewById(R.id.name_final16);
            food_name.setText( Food5  );
        }

        public void setFood6(String food6) {
            Food6 = food6;

            TextView food_name=(TextView) mView.findViewById(R.id.name_final17);
            food_name.setText( Food6  );
        }

        public void setPayment(String payment) {
            Payment = payment;

            TextView food_name=(TextView) mView.findViewById(R.id.PaymentStatus1);
            food_name.setText(String.format("₹%s", Payment));
        }
        public void setPayment2(String payment) {
            Payment = payment;

            TextView food_name=(TextView) mView.findViewById(R.id.pay2);
            if (payment.contains("-")){
                food_name.setTextSize(14);
                food_name.setText(String.format("Get ₹%s from canteen   ",
                        String.valueOf(Integer.parseInt(Payment) * (-1))));
            }else
            food_name.setText(String.format("Remaining ₹%s  ", Payment));
        }
        public void setMore(String payment) {
           if (Integer.parseInt(payment)>=1) {
               Payment = payment;

               TextView food_name = (TextView) mView.findViewById(R.id.more);
               food_name.setVisibility(View.VISIBLE);
               food_name.setText(String.format("+ %s more", Payment));
           }
        }
        public void setImage0(String img) {
       //     if (img!=null) {
                ImageView food_img = (ImageView) mView.findViewById(R.id.img1);
          //  food_img.setVisibility(View.VISIBLE);
            Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
    //    }
        }
        public void setImage1(String img) {
         //   if (img!=null) {

                ImageView food_img = (ImageView) mView.findViewById(R.id.img2);
          //  food_img.setVisibility(View.VISIBLE);
            Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
       // }
        }
        public void setImage2(String img) {
        //    if (img!=null) {

                ImageView food_img=(ImageView) mView.findViewById(R.id.img3);
         //   food_img.setVisibility(View.VISIBLE);
            Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);

        }//}
        public void setImage3(String img) {
         //   if (img!=null) {
                ImageView food_img = (ImageView) mView.findViewById(R.id.img4);
            //    food_img.setVisibility(View.VISIBLE);
                Glide.with(mView).load(img).apply(RequestOptions.circleCropTransform()).into(food_img);
          //  }
        }

        public void setStatus(String status) {
            Status = status;
            int drawable = R.drawable.statusbackground;;
try {
    if (status.contains("Accepted")) {
        drawable = R.drawable.acceptedstatus_background;
    } else if (status.contains("Completed")) {
        drawable = R.drawable.completedstatus_background;
    } else if (status.contains("Cancel")) {
        drawable = R.drawable.cancelledstatus_background;
    } else if (status.contains("Delivered")) {
        drawable = R.drawable.deliveredstatus_background;
    }

    else {
        drawable = R.drawable.statusbackground;
    }
}catch (Exception NullPointerException){}
            TextView food_name=(TextView) mView.findViewById(R.id.Orderstatus1);
            food_name.setBackgroundResource(drawable);

            food_name.setText( Status  );
        }

        public RelativeLayout Relative( ) {

           RelativeLayout r=(RelativeLayout) mView.findViewById(R.id.main);
             return r;
        }
        public void setTime(String time) {
            Time = time;

            TextView food_name=(TextView) mView.findViewById(R.id.deliverytime);
            food_name.setText( Time  );
        }

        public void setTotal_Amount(String total_Amount) {
            Total_Amount = total_Amount;
            TextView food_name=(TextView) mView.findViewById(R.id.total_final1);
            food_name.setText( Total_Amount  );
        }

        public void setTotal_Food(String total_Food) {
            Total_Food = total_Food;

           // TextView food_name=(TextView) mView.findViewById(R.id.tota);
            //food_name.setText(Delivery);
        }

        public void setDelivery(String delivery) {
            Delivery = delivery;
            TextView food_name=(TextView) mView.findViewById(R.id.deliverytime);
            food_name.setText(Delivery);}

        public void setDeliveryedit(String delivery) {
            Delivery = delivery;
            EditText food_name=(EditText) mView.findViewById(R.id.deliverytime);
            food_name.setText(Delivery);}
        public void setOrderNo(String orderNo) {
            OrderNo = orderNo;
            TextView food_name=(TextView) mView.findViewById(R.id.orderno1);
            food_name.setText(OrderNo);}
        public void setFood_name(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.title);
            food_name.setText(OrderNo);}
            public ImageView inc(){
            return (ImageView) mView.findViewById(R.id.increase);
            }

       /*     public ImageView Remove_food() {
            return   mView.findViewById(R.id.remove_food);
         }*/

        public ImageView dec(){
            return (ImageView) mView.findViewById(R.id.decrease);
        }
        public void setQuantity(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.edit_quantity1);
            food_name.setText(OrderNo);}
        public void setprice(String name) {
            OrderNo = name;
            TextView food_name=(TextView) mView.findViewById(R.id.CakePrice);
            food_name.setText(OrderNo);}
      public void visibilityfor_Buttons(){
            ImageView i=(ImageView) mView.findViewById(R.id.increase);
           i.setVisibility(View.GONE);
            i=(ImageView) mView.findViewById(R.id.decrease);
            i.setVisibility(View.GONE);
            mView.findViewById(R.id.multiply).setVisibility(View.VISIBLE);
        }
        public void setRelativeLayout2(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final22);
            relativeLayout.setVisibility(View.VISIBLE);
        } public void setRelativeLayout3(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final23);
            relativeLayout.setVisibility(View.VISIBLE);
        } public void setRelativeLayout4(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final24);
            relativeLayout.setVisibility(View.VISIBLE);
        } public void setRelativeLayout5(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final25);
            relativeLayout.setVisibility(View.VISIBLE);
        } public void setRelativeLayout6(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final26);
            relativeLayout.setVisibility(View.VISIBLE);
        } public void setRelativeLayout7(){
            RelativeLayout relativeLayout=mView.findViewById(R.id.relative_final27);
            relativeLayout.setVisibility(View.VISIBLE);
        }
        public void setEdit(){
Button edit=mView.findViewById(R.id.edit);
edit.setVisibility(View.VISIBLE);
        }
        public void setCacel(){
            Button edit=mView.findViewById(R.id.cancel_order);
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

   /*     public void setImage_Visibility(int image_Visibility) {
            int id[]={R.id.img1,R.id.img2,R.id.img3,R.id.img4};
         for (int i=0;i<image_Visibility;i++){
             mView.findViewById(id[i]).setVisibility(View.VISIBLE);
         }
        }*/

        /*
        public void setName(String name) {

            TextView food_name=(TextView) mView.findViewById(R.id.title);
            food_name.setText(name);

        }
        public void setDesc(String desc) {
            TextView food_desc=(TextView) mView.findViewById(R.id.Desc);
            food_desc.setText(desc);
        }
        public void setPrice(String price) {
            TextView food_price=(TextView) mView.findViewById(R.id.count);
            food_price.setText(price);

        }
        public void setImage(Context ctx, String image) {
            ImageView food_image=(ImageView) mView.findViewById(R.id.thumbnail);
            //Picasso.with(ctx).load(image).into(food_image);
            Picasso.get().load(image).into(food_image);
        }
        public void setType(String type) {
            //  TextView food_type=(TextView) mView.findViewById(R.id.foodtype);
            //food_type.setText(type);

        }
*/
    }

}

