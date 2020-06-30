package com.sanproject.mcafe;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import static android.app.Activity.RESULT_OK;


public class help_fragment extends Fragment {
    private String checkDate="";
    private static final int TAKE_PICTURE =102 ;
    TextView Edit_message;
     DatabaseReference database;
    FirebaseAuth auth;
    public int count = 0;
    private long Total = 0;
    private static final int CHOOSE_IMAGE = 101;
    Uri uriProfileImage;
    ProgressBar progressBar;
    boolean actionButtonStatus=false;
    String profileImageUrl;
    private PopupWindow popupWindow;
    private Uri imageUri;
    private ImageButton actionButton;

    public help_fragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    private MyAdapter adapter;
    private AlbumsAdapter adapter1;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.new_help_layout, container, false);
         recyclerView = view.findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager horizontal = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontal);
//        recyclerView.scrollToPosition(adapter1.getItemCount()-1);
          auth = FirebaseAuth.getInstance();
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().
         //       getReference("Messages/Help/" + auth.getCurrentUser().getUid());
    //adapter= new MyAdapter(album_allorders.class, R.layout.item_message_sent,mainactiv.FoodViewHolder.class,databaseReference);

       recyclerView.setAdapter(adapter1);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        firebase();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
        Edit_message = view.findViewById(R.id.edittext_chatbox);
        actionButton= view.findViewById(R.id.button_send_message);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 setActionButton("TEXT");

            }
        });
        Edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkMessageBox(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

       /* view.findViewById(R.id.button_addExtra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View s) {
                OpenPopup(view);
            }
        });*/
        super.onStart();

        return view;

    }
    void checkMessageBox(String message){
            String emo_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

        if (message.matches(".*\\w.*")||!message.matches(emo_regex)   ){
             Glide.with(view).load(R.drawable.ic_send_black_24dp).into(actionButton);
            actionButtonStatus=true;
            //actionButton.T
        }else{
            Glide.with(view).load(R.drawable.add_color).into(actionButton);
            actionButtonStatus=false;

         //   Picasso.get().load(R.drawable.ic_send_black_24dp).into(actionButton);
           // actionButton.setTag("send");
        }
    }
    void setActionButton(String type){
     if (actionButtonStatus){
        TotalHelp(type);
     }else
      {
          showDiag();



      }
    }

    void TotalHelp(final String type) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd");
        String key=date.format(c.getTime());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().
                child("Messages").child("Help").child(auth.getCurrentUser().getUid()+"/Messages").child(key);//.setValue((message)+"  Email- "+email);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Total = dataSnapshot.getChildrenCount();
                    Log.e("help", "  Total Found " + Total);

                    Total += 1;
                    saveMessage(type);
                } catch (Exception NullPointerException) {
                    Total = 1;
                    saveMessage(type);
                    Log.e("help", "Exception  Total Found " + Total);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void firebase(){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference("Messages/Help/" + auth.getCurrentUser().getUid()+"/Keys");
        FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder> fbra=new FirebaseRecyclerAdapter<album_allorders, mainactiv.FoodViewHolder>(
                album_allorders.class,
                R.layout.help_main_recycler,
                mainactiv.FoodViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(mainactiv.FoodViewHolder viewHolder, album_allorders model, int position) {

                Log.e("help","Inside main firebase key from model "+model.getMessageKey() );
                DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Messages").
                        child("Help").child(auth.getCurrentUser().getUid()+"/Messages/"+model.getMessageKey());
               // viewHolder.setDate1(model.getTime());
                viewHolder.returnDateRelateHelp().bringChildToFront(viewHolder.setDate1(model.getTime()));

               // adapter= new MyAdapter(album_allorders.class, R.layout.item_message_sent,mainactiv.FoodViewHolder.class,databaseReference1);
                RecyclerView recyclerView;
                recyclerView = viewHolder.returnMEssageRecycler();
                LinearLayoutManager horizontal = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(horizontal);
//                recyclerView.setAdapter(adapter);
             }
        };

        recyclerView.setAdapter(fbra);
    }
     void saveMessage(String type) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd");
        String key=date.format(c.getTime());
        database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(auth.getCurrentUser().getUid()+"/Messages/"+key+"/").
                child(String.valueOf(Total));
         if (!type.contains("IMG")) {
            String message = Edit_message.getText().toString().trim();
            if (message.isEmpty()) {
                // Edit_message.setError("Please enter Something");
                Edit_message.requestFocus();
                Log.e("help", "  Error message is empty");

                return;
            }
             DatabaseReference   database1 = FirebaseDatabase.getInstance().getReference().child("Messages").
                     child("Help").child(auth.getCurrentUser().getUid()+"/Keys/"+key);
             database1.child("MessageKey").setValue(key);

            Edit_message.setText("");
              c = Calendar.getInstance();
              date = new SimpleDateFormat("MMMM d, yyyy || h:mm a");

             String message_time = date.format(c.getTime()).toUpperCase();
             database1.child("Time").setValue(message_time);

             Log.e("help", " Message Saved ");
             database.child("Message").setValue(message);
            database.child("Time").setValue(message_time);
            database.child("Status").setValue("UNREAD");
            database.child("From").setValue("USER_TEXT");
         }else{
             DatabaseReference   database1 = FirebaseDatabase.getInstance().getReference().child("Messages").
                     child("Help").child(auth.getCurrentUser().getUid()+"/Keys/"+key);
             database1.child("MessageKey").setValue(key);

             c = Calendar.getInstance();
              date = new SimpleDateFormat("MMMM d, yyyy || h:mm a");
            String message_time = date.format(c.getTime()).toUpperCase();
             database1.child("Time").setValue(message_time);

             Log.e("help", " Message Saved ");
            database.child("Food_Image").setValue(profileImageUrl);
            database.child("Time").setValue(message_time);
            database.child("Status").setValue("UNREAD");
            database.child("From").setValue("USER_IMG");
          }

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.mainRelative);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (actionButton.getX() + (actionButton.getWidth()/2));
        int cy = (int) (actionButton.getY())+ actionButton.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }


    }
     void OpenPopup(View view){
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final View custom = layoutInflater.inflate(R.layout.pop_add_extra_help, null);
        CoordinatorLayout relativeLayout = view.findViewById(R.id.helpConstraint);
        popupWindow = new PopupWindow(custom, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        custom.findViewById(R.id.relateGallery). setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        custom.findViewById(R.id.relateCamera). setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        custom.findViewById(R.id.relateOrder). setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrders();
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing()){
                 //   revealShow(custom, false, popupWindow);
                }
            }
        });
    }
    private void showDiag() {

        final View dialogView = View.inflate(getContext(),R.layout.pop_add_extra_help,null);

        final Dialog dialog = new Dialog(getContext() );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        CoordinatorLayout relativeLayout = view.findViewById(R.id.helpConstraint);
        //popupWindow = new PopupWindow(custom, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(150,170);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        dialogView.setLayoutParams(params);
        //ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });



         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
     private void openOrders() {
     //   popupWindow.dismiss();

    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "filename.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        //imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, CHOOSE_IMAGE);
       // popupWindow.dismiss();
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select  Image"), CHOOSE_IMAGE);
       // popupWindow.dismiss();

    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("Chat_Images/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl =uri.toString();
                                    TotalHelp("IMG");
                                    assert popupWindow != null;
                                    popupWindow.dismiss();

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            uploadImageToFirebaseStorage();
         }
    }
}