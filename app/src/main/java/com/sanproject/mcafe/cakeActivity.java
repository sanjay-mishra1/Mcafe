package com.sanproject.mcafe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gifLoader.GifImageView;

import static com.sanproject.mcafe.constant.constants.Canteen;

/**
 * Created by sanjay on 02/07/2018.
 */

public class cakeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    GifImageView gifImageView;
    private Dialog progressDialog;
    View views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupfor_cake);
        recyclerView=findViewById(R.id.cakeRecycler);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(cakeActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new food_beverages.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        gifImageView=findViewById(R.id.pageLoading);
        gifImageView.setImageResource(R.drawable.loadingbackground);

        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Food Items/"+Canteen+"/"+"Cake");

        FirebaseRecyclerAdapter<Food,mainactiv.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
                Food.class,
                R.layout.cake_layout,
                mainactiv.FoodViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.setName(model.getFood_name());
                findViewById(R.id.loading).setVisibility(View.GONE);
                viewHolder.setrating(String.valueOf(model.getSum_of_Ratings()),String.valueOf(model.getTotal_NoOfTime_Rated()));
                viewHolder.setDiscount2(model.getGm500_d(),model.getGm500());
                viewHolder.setPrice(model.getGm500());
                viewHolder.setImage4(getApplicationContext(), model.getFood_Image());
                viewHolder.getImageId().setEnabled(true);
                viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        views=view;
                        view.setEnabled(false);
                        showProgress_dialog();
                        Intent intent=new Intent(cakeActivity.this,cakeDetailView.class);
                        intent.putExtra("CakeName",model.getFood_name());
                        intent.putExtra("CakePrice", model.getGm500());
                        intent.putExtra("d_gm500",model.getGm500_d());
                        intent.putExtra("CakeImage",model.getFood_Image());
                        intent.putExtra("gm1000",model.getGm1000());
                        intent.putExtra("d_gm1000",model.getGm1000_d() );
                        intent.putExtra("gm200",model.getGm200());
                        intent.putExtra("d_gm200",model.getGm200_d());
                        intent.putExtra("Ratingsum",String.valueOf(model.getSum_of_Ratings()));
                        intent.putExtra("Ratingno",String.valueOf(model.getTotal_NoOfTime_Rated()));
                        intent.putExtra("Shape", model.getShape());
                        intent.putExtra("Flavour",model.getFlavour());
                        Log.e("Debug discount in cake ","200gm "+model.getGm200_d()+" 500gm  "+model.getGm500_d()+" 1000gm "+model.getGm1000_d());
                        Log.e("Cake activity","Shape "+model.getShape()+" Flavour"+model.getFlavour());

                        startActivity(intent);
                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        views=view;
                        view.setEnabled(false);
                        showProgress_dialog();
                        Intent intent=new Intent(cakeActivity.this,cakeDetailView.class);
                        intent.putExtra("CakeName",model.getFood_name());
                        intent.putExtra("CakePrice", model.getGm500());
                        intent.putExtra("d_gm500",model.getGm500_d());
                        intent.putExtra("CakeImage",model.getFood_Image());
                        intent.putExtra("gm1000",model.getGm1000());
                        intent.putExtra("d_gm1000",model.getGm1000_d() );
                        intent.putExtra("gm200",model.getGm200());
                        intent.putExtra("d_gm200",model.getGm200_d());
                        intent.putExtra("Ratingsum",String.valueOf(model.getSum_of_Ratings()));
                        intent.putExtra("Ratingno",String.valueOf(model.getTotal_NoOfTime_Rated()));
                        intent.putExtra("Shape", model.getShape());
                        intent.putExtra("Flavour",model.getFlavour());
                        Log.e("Debug discount in cake ","200gm "+model.getGm200_d()+" 500gm  "+model.getGm500_d()+" 1000gm "+model.getGm1000_d());
                        Log.e("Cake activity","Shape "+model.getShape()+" Flavour"+model.getFlavour());

                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    void showProgress_dialog(){
        final View dialogView = View.inflate(this, R.layout.progress_layout, null);

        progressDialog = new Dialog(this);
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


        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            try {
                views.setEnabled(true);
            }catch (Exception ignored){}
            progressDialog.dismiss();
        }catch (Exception ignored){}

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            progressDialog.dismiss();
        }catch (Exception ignored){}

    }
}
