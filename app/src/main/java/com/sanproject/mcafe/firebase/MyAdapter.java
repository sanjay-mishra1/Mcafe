package com.sanproject.mcafe.firebase;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sanproject.mcafe.R;
import com.sanproject.mcafe.album_allorders;
import com.sanproject.mcafe.mainactiv;

import static com.sanproject.mcafe.constant.constants.Canteen;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder>{
     private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;
    private static final int LAYOUT_THREE = 3;
    private static final int LAYOUT_FIVE = 5;
    private static final int LAYOUT_FOUR =4 ;

    public MyAdapter(Class<album_allorders> modelClass,
                       int modelLayout,
                       Class<mainactiv.FoodViewHolder> viewHolderClass,
                       Query ref ) {

         super(modelClass, modelLayout, viewHolderClass, ref  );
            Log.e("Firebase Messaging"," From adapter query is "+ref.orderByKey().limitToLast(1));
    }


    @Override
    public mainactiv.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
             return new mainactiv.FoodViewHolder(view);

        } else if (viewType == LAYOUT_TWO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_img, parent, false);
            return new mainactiv.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_THREE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new mainactiv.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_FIVE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_layout, parent, false);
            return new mainactiv.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_FOUR){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_img, parent, false);
            return new mainactiv.FoodViewHolder(view);

        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv.FoodViewHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position){

    album_allorders model = getItem(position);

try {
    switch (model.getFrom()) {
        case "USER_TEXT":
            return LAYOUT_ONE;
        case "USER_IMG":
            return LAYOUT_TWO;

        case "ADMIN_TEXT":
            return LAYOUT_THREE;
        case "Date":
            return LAYOUT_FIVE;
        case "ADMIN_IMG":
            return LAYOUT_FOUR;
    }
}catch (Exception ignored){}
        return position;

}/*
void animation(int Position, Context context,Intent intent,View view)
{
    ActivityOptionsCompat.makeSceneTransitionAnimation()
    ActivityOptionsCompat options =

            ActivityOptionsCompat.makeSceneTransitionAnimation(    ,
                    getItemViewType(Position),   // Starting view
                    "animation"    // The String
            );

    ActivityCompat.startActivity(context, intent, options.toBundle());
}*/



    @Override
    protected void populateViewHolder(final mainactiv.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        try {
            if (model.getFrom().contains("IMG")) {
                viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(viewHolder.itemView.getContext(), Message_Image.class);
                        intent.putExtra("Image", model.getFood_Image());
                        intent.putExtra("Message", model.getMessage());
                        //  animation(position, viewHolder.itemView.getContext(),intent,viewHolder.itemView);
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }catch (Exception ignored){
         }


           viewHolder.setMessage(model.getMessage());

           viewHolder.setTime(model.getTime());
           viewHolder.setImageMessage(model.getFood_Image(),viewHolder.itemView.getContext());
           viewHolder.setAdminImage();
           viewHolder.setcanteenName(Canteen);
           viewHolder.setDate2(model.getDate());
           try{
               viewHolder.setstatus(model.isSeen(),viewHolder.itemView.getContext());
           }catch (Exception ignored){}

    }



}
