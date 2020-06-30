package com.sanproject.mcafe;

import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class final_AlbumsAdapter extends RecyclerView.Adapter<final_AlbumsAdapter.MyViewHolder> {
    private Context mContext;
    private Button final_price, final_price2;
    private int Total = 0;
    private int price_key = 0;
    private List<album> albumList;
    private TextView title, count, quantity1;

    public final_AlbumsAdapter() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // public TextView ;
        public ImageView thumbnail, overflow;
        public TextView quantity;

        public MyViewHolder(View view) {
            super(view);
            //final_price2=view.findViewById(R.id.total_price1);
            quantity1 = view.findViewById(R.id.edit_quantity1);
            quantity = view.findViewById(R.id.edit_quantity1);
            title = (TextView) view.findViewById(R.id.foodName);
            count = (TextView) view.findViewById(R.id.foodprice);
            thumbnail = (ImageView) view.findViewById(R.id.foodImage);
            overflow = (ImageView) view.findViewById(R.id.dots);
        }
    }

    public final_AlbumsAdapter(Context mContext, List<album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final album album = albumList.get(position);
        //title.setText(album.getName());
        title.setText(album.getName());
        count.setText(Integer.toString(album.getNumOfSongs()));
        //  Total = (album.getNumOfSongs()) + Total;
        // final_price.setText(Total);
        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

    }

    /**
     * Showing popup menu when tapping on 3 dots
     */


    /**
     * Click listener for popup menu items
     */


    @Override
    public int getItemCount() {
        return albumList.size();
    }

}



