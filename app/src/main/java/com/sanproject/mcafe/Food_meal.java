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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

 import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

 import androidx.core.app.ActivityOptionsCompat;
 import androidx.fragment.app.Fragment;
 import androidx.recyclerview.widget.DefaultItemAnimator;
 import androidx.recyclerview.widget.GridLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;


public class Food_meal extends Fragment {
      ImageView next;
    public  int count=0;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
     private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
     RelativeLayout relativeLayout;

    public Food_meal(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.main,container,false);
        count=0;
        progressBar=view.findViewById(R.id.progressFood);
        progressBar.setVisibility(View.INVISIBLE);


        next=view.findViewById(R.id.next_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
         RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        mDatabase= FirebaseDatabase.getInstance().getReference().child("Food Items/Meal");

firbase();


        return view;

    }


void firbase() {
    super.onStart();
    progressBar.setVisibility(View.VISIBLE);

     FirebaseRecyclerAdapter <Food,mainactiv.FoodViewHolder> FBRA=new FirebaseRecyclerAdapter<Food, mainactiv.FoodViewHolder>(
            Food.class,
            R.layout.album_card,
            mainactiv.FoodViewHolder.class,
            // Food_meal.FoodViewHolder.class,
            mDatabase


    )
    {

        protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final Food model, int position) {
            progressBar.setVisibility(View.GONE);

            viewHolder.setName(model.getFood_name());
            viewHolder.setPrice(String.valueOf(model.getPrice()));
            viewHolder.setDesc(model.getDescription());
            viewHolder.setImage(getActivity(), model.getFood_Image());
            final String price= viewHolder.setDiscount(String.valueOf(model.getDiscount()),String.valueOf(model.getPrice()));
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Intent intent =new Intent(food_snaks.this,ScrollingActivity.class);
                    Intent intent = new Intent(getContext(), food_info.class);
                    intent.putExtra("Image", model.getFood_Image());
                    intent.putExtra("Name", model.getFood_name());
                    intent.putExtra("Price",String.valueOf(model.getPrice()));
                    intent.putExtra("Discount",String.valueOf(model.getDiscount()));
                    intent.putExtra("Favorite",model.getFavorite());
                    intent.putExtra("Description", model.getDescription());
                    intent.putExtra("Type", model.getType());
                    intent.putExtra("Total_orders",model.getTotal_orders());
                    intent.putExtra("No_of_Ratings",model.getTotal_NoOfTime_Rated());
                    intent.putExtra("Ratings",model.getSum_of_Ratings());
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation
                                    (getActivity(), viewHolder.getImageId(), "My_Animation");
                    startActivity(intent, optionsCompat.toBundle());

                }
            });
            viewHolder.getImageId().setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View view) {

                         viewHolder.mView.setBackgroundColor(Color.RED);
                        foodcart(model.getFood_name(), price,model.getType(),model.getFood_Image());

                     }
                });

        }

    };
    recyclerView.setAdapter(FBRA);

}

    void foodcart(final String Food, final String Price, final String Type, final String Image) {
    mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User Informations/" + mAuth.getCurrentUser().getUid() + "/FoodCart/Food");
        mDatabase.child(Food).child("Food_name").setValue(Food);
        mDatabase.child(Food).child("price").setValue(Price);

        mDatabase.child(Food).child("Type").setValue(Type);

        mDatabase.child(Food).child("Food_Image").setValue(Image);

        mDatabase.child(Food).child("Quantity").setValue("1");

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


/*        private void showPopupMenu(View view,String FoodName) {
            // inflate menu
            PopupMenu popup = new PopupMenu(mView.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_album, popup.getMenu());
            popup.setOnMenuItemClickListener(new FoodViewHolder.MyMenuItemClickListener());
            popup.show();
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            public MyMenuItemClickListener() {
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add_favourite:
                        Toast.makeText(mView.getContext(), "Add to favourite", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_play_next:
                        Toast.makeText(mView.getContext(), "Play next", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                }
                return false;
            }
        }


    }*/

}
