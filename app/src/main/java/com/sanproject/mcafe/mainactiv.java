package com.sanproject.mcafe;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.recyclerview.widget.RecyclerView;

import static com.sanproject.mcafe.message.help_activity.CanImage;

public class mainactiv {


    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public RecyclerView returnMEssageRecycler() {
            return mView.findViewById(R.id.recycler_view);
        }


        public ImageView getImageId() {
            return (ImageView) mView.findViewById(R.id.thumbnail);
        }

        public ImageView getImageView() {
            ImageView imageView = mView.findViewById(R.id.message_body_img);
            return imageView;
        }

        public Button addbutton() {
            return (Button) mView.findViewById(R.id.add);
        }

        public void setName(String name) {

            TextView food_name = (TextView) mView.findViewById(R.id.title);
            food_name.setText(name);

        }

        public void setName1(View ctx, String name) {

            TextView food_name = (TextView) mView.findViewById(R.id.title);
            food_name.setText(name);

        }

        public void setDesc(String desc) {
            TextView food_desc = (TextView) mView.findViewById(R.id.Desc);
            food_desc.setText(desc);
        }

        public void setDescVisibility() {
            TextView food_desc = (TextView) mView.findViewById(R.id.Desc);
            food_desc.setVisibility(View.GONE);
        }

        public void setPrice(String price) {
            TextView food_price = (TextView) mView.findViewById(R.id.CakePrice);

            food_price.setText(String.format("₹ %s", price));

        }

        public void setPrice1(View ctx, String price) {
            TextView food_price = (TextView) mView.findViewById(R.id.CakePrice);

            food_price.setText(price);

        }

        public void ruppes() {
            ImageView i = (ImageView) mView.findViewById(R.id.rupess);
            i.setVisibility(View.GONE);
        }

        public void setImageID(String image) {
            ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);

            try {
                Glide.with(mView).load(image).apply(RequestOptions.circleCropTransform()).into(food_image);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
public void setCanteenData(String image,String name){
    ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);

    try {
        Glide.with(mView).load(image).apply(RequestOptions.circleCropTransform()).into(food_image);
    } catch (Exception e) {
        e.printStackTrace();
    }
    TextView food_name = (TextView) mView.findViewById(R.id.title);
    food_name.setText(name);
    mView.findViewById(R.id.buttons).setVisibility(View.INVISIBLE);
    mView.findViewById(R.id.cancel).setVisibility(View.INVISIBLE);
}
        public void setImage(Context ctx, String image) {
            final ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);
           //  Picasso.get().load(image).into(food_image);
            Glide.with(ctx).load(image).into(food_image);

        }

        public void setImage4(Context ctx, String image) {
            final ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);
           Glide.with(ctx).load(image).into(food_image);

        }

        public void setImage1(final Context ctx, final String image) {
            final ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);
            ProgressBar progressBar = mView.findViewById(R.id.progressbar);
             RequestBuilder<Drawable> thumbnail = Glide.with(ctx).load(progressBar);
            Glide.with(ctx).load(image).thumbnail(thumbnail).into(food_image);



        }

        public void setImage3(final Context ctx, final String image) {
            final ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);
             Glide.with(ctx).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(image).into(food_image);



        }

        public void setImage2(final Context ctx, final String image) {
            final ImageView food_image = (ImageView) mView.findViewById(R.id.thumbnail);
            ProgressBar progressBar = mView.findViewById(R.id.progressbar);

            RequestBuilder<Drawable> thumbnail = Glide.with(ctx).load(progressBar);
            Glide.with(ctx).load(image).thumbnail(thumbnail).into(food_image);



        }

        public String setDiscount(String discount, String price) {
            TextView t = mView.findViewById(R.id.cakeoriginalPrice);
            TextView t2 = mView.findViewById(R.id.price);
            TextView textView= mView.findViewById(R.id.CakePrice);
            try {

                if (discount.isEmpty() || discount.equals(price)) {


                    textView.setText(String.format("₹%s", price));
                    t.setText("");
                    t2.setText("");
                  //  t.setVisibility(View.GONE);
                    return price;
                } else {
                    t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    t.setText(String.format("₹%s", price));
                   // t2.setVisibility(View.VISIBLE);
                     textView.setText("");
                   // t.setVisibility(View.VISIBLE);
                    t2.setText(String.format("₹%s", discount));
                    return discount;
                }
            } catch (Exception NullPointerException) {
               // mView.findViewById(R.id.CakePrice).setVisibility(View.VISIBLE);
                t.setText("");
                t2.setText("");
                textView.setText(String.format("₹%s", price));
                //t.setVisibility(View.GONE);
                return price;
            }
        }

        public EditText returnAgeEdittext() {
            return mView.findViewById(R.id.edittextAge);
        }

        public Button OK() {
            return mView.findViewById(R.id.okbutton);
        }

        public TextView returnAgeTexttext() {
            return mView.findViewById(R.id.TextViewAge);
        }

        public void setDiscount1(String discount, String price) {
            TextView t = mView.findViewById(R.id.cakeoriginalPrice);
            TextView t2 = mView.findViewById(R.id.CakePrice);
            TextView t3 = mView.findViewById(R.id.cakediscount);
             try {

                if (discount.isEmpty())
                    t.setVisibility(View.GONE);
                else {
                    t.setText("₹" + price);
                    t2.setVisibility(View.VISIBLE);
                    t.setVisibility(View.VISIBLE);
                    t2.setText("₹" + discount);
                    t3.setVisibility(View.VISIBLE);
                    t3.setText(String.format("%s%% OFF", calculateDiscount(discount, price)));

                }
            } catch (Exception NullPointerException) {
                t.setVisibility(View.GONE);
            }
        }

        private String calculateDiscount(String discountedPrice, String Price) {
            Log.e("Discount calculated", "Discounted price " +
                    discountedPrice + " Price " + Price + " Discount " + String.format("%.0f", (((Double.parseDouble(Price) - Double.parseDouble(discountedPrice)) / Double.parseDouble(Price)) * 100)));
            // return String.valueOf( (((Integer.parseInt(Price)-Integer.parseInt(discountedPrice))/Integer.parseInt(Price))*100));
            return String.format("%.0f", (((Double.parseDouble(Price) - Double.parseDouble(discountedPrice)) / Double.parseDouble(Price)) * 100));
        }

        public ImageView setListnercancel() {
            return (ImageView) mView.findViewById(R.id.cancel);
        }

        public ImageView setListnerforplus() {
            return (ImageView) mView.findViewById(R.id.imageButton_add1);
        }

        public ImageView setListnerforminus() {
            return (ImageView) mView.findViewById(R.id.imageButton_remove1);
        }

        public void setQuantity(String Q) {
            TextView food_q = (TextView) mView.findViewById(R.id.edit_quantity1);
            food_q.setVisibility(View.VISIBLE);
            food_q.setText(Q);
        }

        public String getQuantity() {
            TextView food_q = (TextView) mView.findViewById(R.id.edit_quantity1);
            food_q.setVisibility(View.VISIBLE);
            return food_q.getText().toString().trim();
        }
        public TextView getQuantity_ids() {

            return (TextView) mView.findViewById(R.id.edit_quantity1);
        }

        public void setcanteenName(String Q) {
          try {
              TextView food_q = (TextView) mView.findViewById(R.id.text_message_name);

              food_q.setText(Q);
          }catch (Exception ignored){}
        }


        public void makeVisibletheADDbuttons() {
            mView.findViewById(R.id.imageButton_add1).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.imageButton_remove1).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.edit_quantity1).setVisibility(View.VISIBLE);
        }

        public void setMessage(String Message) {
            try {

                TextView textView = mView.findViewById(R.id.text_message_body);
                if (!Message.equals("")) {
                    textView.setText(Message);
                    textView.setVisibility(View.VISIBLE);
                }
            } catch (Exception NullPointerException) {
            }

        }
        public void setMessage_notification(String Message) {
            try {
                Message =Message.substring(0,Message.indexOf("Time"));
                TextView textView = mView.findViewById(R.id.text_message_body);
                if (!Message.equals("")) {
                    //textView.setText(Message.substring(0,Message.indexOf("Time")));
                     textView.setText(Message);
                    textView.setVisibility(View.VISIBLE);
                   // set_Time_from_millis(textView, Long.parseLong(Message.substring(Message.indexOf("Time")+4),Message.length()-1));
                }
            } catch (Exception e) {
                Log.e("Time_notification","Time is"+e.toString());

            }

        }
        void set_Time_from_millis(TextView textView,long durationInMillis){
            Log.e("Time_notification","Time is"+durationInMillis);
           // long millis = durationInMillis % 1000;
          //  long second = (durationInMillis / 1000) % 60;
            long minute = (durationInMillis / (1000 * 60)) % 60;
            long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
            textView.append(" "+String.valueOf(hour)+":"+String.valueOf(minute));
        }

        public void setTime(String time) {
            try {
                TextView textView = mView.findViewById(R.id.text_message_time);
                // time=time.substring(time.indexOf("||")+3,time.length());
                textView.setText(time);
            } catch (Exception NullPointerException) {
            }

        }
public void set_time_from_firebase(long time){
    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    TextView textView = mView.findViewById(R.id.text_message_time);
     textView.setText( sfd.format(new Date(time)));
}
        public TextView setDate1(String time) {
            TextView textView = mView.findViewById(R.id.date);

            try {
                time = time.substring(0, time.indexOf("||") - 1);
                textView.setText(String.format("  %s  ", time));
            } catch (Exception NullPointerException) {
            }
            return textView;
        }

        public void setDate2(String time) {
            try {
                TextView textView = mView.findViewById(R.id.date);
                //   time= time.substring(0,time.indexOf("||")-1 );
                textView.setText(time);
            } catch (Exception NullPointerException) {
            }

        }

        public void setImageMessage(String image,Context context) {
            try {
                ImageView food_image = (ImageView) mView.findViewById(R.id.message_body_img);
                //Picasso.with(ctx).load(image).into(food_image);
               // Picasso.get().load(image).into(food_image);
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(image).into(food_image);

            } catch (Exception NullPointerException) {
            }

        }

        public void setAdminImage() {
            try {
                ImageView img = mView.findViewById(R.id.image_message_profile);
                Glide.with(mView).load(CanImage).apply(RequestOptions.circleCropTransform()).into(img);

            } catch (Exception NullPointerException) {
            }
        }


        public void setMessageBox(String message, String Time, String status, String from, String Message_time) {
            try {
                if (from.toUpperCase().equals("ADMIN")) {
                    mView.findViewById(R.id.layout_message_sent).setVisibility(View.GONE);
                    TextView messageBox = mView.findViewById(R.id.text_message_body);
                    messageBox.setText(message);
                    //     TimeText.setText(Time);
                    settime((TextView) mView.findViewById(R.id.date), (TextView) mView.findViewById(R.id.text_message_time),
                            Time, Message_time, (RelativeLayout) mView.findViewById(R.id.relateDate));
                } else {
                    mView.findViewById(R.id.layout_message_received).setVisibility(View.GONE);
                    TextView messageBox = mView.findViewById(R.id.text_message_body);
                     messageBox.setText(message);
                  }

            } catch (Exception NullPointerException) {
            }
        }

        public RelativeLayout returnDateRelateHelp() {
            return (RelativeLayout) mView.findViewById(R.id.relateDate);
        }

        public void setDate(String date_string) {
            TextView textView = mView.findViewById(R.id.date);
            textView.setText(date_string.substring(0, date_string.indexOf("||") - 1));

        }

        void settime(TextView date_text, TextView time_text, String date_string, String todayDate, RelativeLayout relate) {

            time_text.setText(date_string.substring(date_string.indexOf("||") + 3, date_string.length()));
            date_text.setText(date_string.substring(0, date_string.indexOf("||") - 1));

        }

        public void setrating(String sum_of_ratings, String total_noOfTime_rated) {
            try {
                if (!total_noOfTime_rated .equals("0") && !total_noOfTime_rated.isEmpty()) {
                    mView.findViewById(R.id.star_linear).setVisibility(View.VISIBLE);
                    TextView textView = mView.findViewById(R.id.rating_star);
                    float rating = Float.parseFloat(sum_of_ratings) / Float.parseFloat(total_noOfTime_rated);
                    RatingBar ratingBar = mView.findViewById(R.id.rating);
                    ratingBar.setRating(rating);
                    textView.setText(String.format("%.1f", rating));

                } else {
                    mView.findViewById(R.id.star_linear).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                mView.findViewById(R.id.star_linear).setVisibility(View.GONE);

            }
        }

        int getDiscount(float price, float originalPrice) {
            return (int) (((originalPrice - price) / originalPrice) * 100);
        }

        public void setDiscount2(String gm500_d, String gm500) {
           try {
                if (gm500_d != null) {
                    Log.e("Debug cake discount","Gm500_d not null d"+gm500_d+" gm500 "+gm500);
                    TextView discount = mView.findViewById(R.id.cakediscount);
                    discount.setVisibility(View.VISIBLE);
                    TextView dis_price = mView.findViewById(R.id.cakeoriginalPrice);
                    dis_price.setVisibility(View.VISIBLE);
                     discount.setText(String.format("%d%%", getDiscount( Integer.parseInt(gm500),Integer.parseInt(gm500_d))));
                    dis_price.setText(String.format("₹%s", gm500_d));
                    dis_price.setPaintFlags(dis_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                } else {
                    Log.e("Debug cake discount","Gm500_d  is null d"+gm500_d+" gm500 "+gm500);

                    mView.findViewById(R.id.cakediscount).setVisibility(View.INVISIBLE);
                    mView.findViewById(R.id.cakeoriginalPrice).setVisibility(View.INVISIBLE);
                }
           }  catch (Exception e) {
               Log.e("Debug cake discount","Gm500_d   null in catch d"+gm500_d+" gm500 "+gm500);

              mView.findViewById(R.id.cakediscount).setVisibility(View.INVISIBLE);
              mView.findViewById(R.id.cakeoriginalPrice).setVisibility(View.INVISIBLE);
         }
        }





        public void setstatus(boolean status,Context context) {
            if (!status){
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation())
                        .load(R.drawable.sent).into((ImageView) itemView.findViewById(R.id.status));
            }
        }
    }
}



