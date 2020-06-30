package com.sanproject.mcafe;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Food_types extends Fragment {
    public String Selected_foodName[];
    public int Selected_foodPrice[];
    public String Name;
    public String Image[];
    private TextView total_food;
    ImageView next;
    public int count=0;
    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<album> albumList;
    RelativeLayout relativeLayout;
    String type;
    ImageView dec;
    public Food_types(){

    }
    Intent intent ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.main,container,false);

        try {

            type = intent.getStringExtra("Type");
            Toast.makeText(getActivity(),"Type=="+type,Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
        }


        Selected_foodName=new String[10];
        Selected_foodPrice=new int[10];
        Image=new String[10];

        next=view.findViewById(R.id.next_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        total_food= view.findViewById(R.id.total_foodItem);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/"+type);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Final_Food_selelction.class);
                intent.putExtra("Name",Selected_foodName);
                intent.putExtra("Price",Selected_foodPrice);
                intent.putExtra("Image",Image);
                intent.putExtra("Total",count);
                startActivity(intent);
            }
        });

        super.onStart();


        try {
            Glide.with(getActivity()).load(R.drawable.cover).into((ImageView) view.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }

    public void getFoodType(String Type) {
       type=Type;
       if(type.isEmpty()){
           type="Snaks";
       }

    }

    @Override
    public void onStart() {
        super.onStart();

        //      mAuth.addAuthStateListener(mAuthlistener);
        FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.album_card,
                mainactiv.FoodViewHolder.class,
                mDatabase


        )
        {     //  @Override
            protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {


                viewHolder.setName(model.getFood_name());
                viewHolder.setPrice(String.valueOf(model.getPrice()));
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity(),model.getFood_Image());
             //   viewHolder.setType(model.getType());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        next.setVisibility(View.VISIBLE);
                        String name=model.getFood_name();
                        if(Arrays.asList(Selected_foodName).contains(name)){
                            viewHolder.mView.setBackgroundColor(Color.WHITE);
                            for(int i=0;i<count;i++)
                            {
                                if(Selected_foodName[i]==name){
                                    for(int j=i;j<count;j++){
                                        Selected_foodName[j]=Selected_foodName[j+1];
                                        Selected_foodPrice[j]=Selected_foodPrice[j+1];
                                        Image[j] = Image[j+1];
                                    }
                                }
                            }
                            count--;
                            Toast.makeText(getActivity(),"Already in the list "+count,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            viewHolder.mView.setBackgroundColor(Color.RED);
                            Selected_foodName[count] = model.getFood_name();

                     //       Selected_foodPrice[count] = Integer.parseInt( model.getPrice());
                         //   Selected_foodPrice[count] =   model.getPrice();
                            Image[count] = model.getFood_Image();
                            count++;
                            String c = Integer.toString(count);
                            total_food.setText(c + " Items");
                            Toast.makeText(getActivity(), "Done  " + Selected_foodName[count - 1], Toast.LENGTH_SHORT).show();
                        }     }
                });

            }
        };
        recyclerView.setAdapter(FBRA);


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
            }
            else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
