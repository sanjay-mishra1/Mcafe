package com.sanproject.mcafe;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class final_food_list extends AppCompatActivity {
   public String[] New;
    public String[] Quantity;

    int[] Price;
    String Time;
    int Total=0;
    private RecyclerView recyclerView;
    private final_AlbumsAdapter adapter;
    private List<album> albumList;
    int arrayPrice[];
    public int Total_Price1=0;
    private TextView quantity;
    public Button price;
    public EditText Times;
    private Button Am,Pm;
    private String time_type="N";
    private ImageView load, remove, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_food_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        setSupportActionBar(toolbar);
              recyclerView = (RecyclerView) findViewById(R.id.foodlist);
        New=new String[10];
        Price =new int[10];
        Quantity=new String[10];
        albumList = new ArrayList<>();
         adapter = new final_AlbumsAdapter(this, albumList);
        arrayPrice=new int[40];

Am=findViewById(R.id.am);
Pm=findViewById(R.id.pm1);
Times=findViewById(R.id.times);
        /*RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
      price=findViewById(R.id.total_price1);
        prepareAlbums();

        try {
          //  Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
Am.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        time_type="AM";
        Am.setBackgroundColor(Color.BLUE);
        Pm.setBackgroundColor(Color.parseColor("#03f5d5"));
    }
});
        Pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type="PM";
                Pm.setBackgroundColor(Color.BLUE);
                Am.setBackgroundColor(Color.parseColor("#03f5d5"));
            }
        });

price.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String T=Times.getText().toString().trim();
        //String T_type=ti.getText().toString().trim();

        if(T.isEmpty()){
            Toast.makeText(final_food_list.this,"Please Enter Time",Toast.LENGTH_LONG).show();
            return;
        }
        if(time_type=="N"){
          //  Toast.makeText(final_food_list.this,"Please Enter AM or PM",Toast.LENGTH_LONG).show();
            Times.setError("Select AM or PM ");
           Times.requestFocus();
            return;

        }
        Toast.makeText(final_food_list.this,"Time "+time_type+" T="+T,Toast.LENGTH_LONG).show();
        time_type=T+time_type;
        Intent intent =new Intent(final_food_list.this,orderFood.class);
        intent.putExtra("Name",New);
        intent.putExtra("Time",time_type);
        intent.putExtra("Price",Price);
        intent.putExtra("Total",Total);
        intent.putExtra("Quantity",Quantity);
        startActivity(intent);
    }
});
    }

    private void prepareAlbums() {

       try {
           Intent intent = getIntent();
           New = intent.getStringArrayExtra("Name");
           Price = intent.getIntArrayExtra("Price");
           String[] Image = intent.getStringArrayExtra("Image");
           Total = intent.getIntExtra("Total", 0);
           String[] covers = new String[]{
           };

           album a = new album();
           for (int i = 0; i < Total; i++) {
               Toast.makeText(this, "Value " + i + New[i], Toast.LENGTH_SHORT).show();
               //  wait(20);
               Quantity[i]="1";
               a = new album(New[i], Price[i], Image[i]);
               Total_Price1=Total_Price1+Price[i];
               albumList.add(a);
           }
       }catch (Exception e){
           e.printStackTrace();
       }

       Toast.makeText(final_food_list.this,"price= "+Total_Price1,Toast.LENGTH_SHORT).show();
       // final_AlbumsAdapter.Total_price t_p= null;
     //  try{
     //   Total_Price1=t_p.total();
     //      get_price get=null;
     //      get.SEtingValueforPrice(Total_Price1);
     //  }catch (Exception NullPointerException){}
       // price.setText(Integer.toString( Total_Price1));

        // adapter.notifyDataSetChanged();
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
public class get_price{
        int price2;
        int ElementPrice=0;
        String names[];
        int price_array[];
        get_price(){

        }
    void SEtingValueforPrice(int price12) {
            Toast.makeText(getApplicationContext(),"Total is in final foodlist = "+ price12,Toast.LENGTH_LONG).show();
        price.setText(Integer.toString( price12));
    }
        int Total_price()
        {
            price2=Total_Price1;
            return price2;
        }
           }
 }
