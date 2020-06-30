package com.sanproject.mcafe;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.sanproject.mcafe.constant.constants.Canteen;


public class account_settings extends AppCompatActivity {
public TextView user,email,phone,pass,collegeid,logout,delete;
Button edit;
String names,mobile,eid,cid;
public View mview=null;
FirebaseAuth auth;
    Uri uriProfileImage;
      String profileImageUrl;
    SharedPreferences shared;
    public static final String MYPREFERENCES="MyPrefs";
    private static final int CHOOSE_IMAGE = 101;

DatabaseReference databaseReference;
    private ImageView imageView;

    public account_settings() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      v = inflater.inflate(R.layout.newsetting_layout,container,false);
            setContentView(R.layout.newsetting_layout);
        shared=getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        imageView=findViewById(R.id.UserImage);


        Glide.with(this).load(R.drawable.compressed_login).into((ImageView) findViewById(R.id.SettingsImage));
        user= findViewById(R.id.edituser1);
        email= findViewById(R.id.editemail1);
        phone= findViewById(R.id.editphone);
        pass= findViewById(R.id.editpassword);
        collegeid= findViewById(R.id.collegeId);
        logout= findViewById(R.id.LogoutId);
        delete= findViewById(R.id.DeleteId);
        edit= findViewById(R.id.edit);
        auth=FirebaseAuth.getInstance();
        email.setText(auth.getCurrentUser().getEmail());

        databaseReference=  FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User Informations").
                child((auth.getCurrentUser().getUid())).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> map=(Map<String,Object>) dataSnapshot.getValue();
                        try {
                            user.setText((String) map.get("Name"));
                            phone.setText((String) map.get("Mobile_number"));
                            collegeid.setText((String) map.get("College_id"));
                            //   collegeid.setText((String) map.get("College_id"));
                            String img=(String) map.get("User_Img");
                            if (img!=null){
                                imageView.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(img).apply(RequestOptions.circleCropTransform()).into(imageView);

                            }else{
                                TextView textView= findViewById(R.id.img_text);
                                textView.setText(Name_for_Image(user.getText().toString().trim()));
                            }

                        }catch (Exception NullPointerException){}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(account_settings.this,change_account_info.class);
                intent.putExtra("User",user.getText().toString().trim());
                intent.putExtra("Mobile",phone.getText().toString().trim());
                intent.putExtra("Email",email.getText().toString().trim());
                intent.putExtra("CollegeId",collegeid.getText().toString().trim());
                startActivity(intent);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                SharedPreferences.Editor editor=shared.edit();
                editor.clear();
                editor.apply();
                  finish();
                  Canteen=null;
                Intent intent=new Intent(account_settings.this,logIN.class);
                startActivity(intent);
            }
        });

        collegeid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(account_settings.this,college_change_avtivity.class);
                intent.putExtra("CollegeId",collegeid.getText().toString().trim());
                startActivity(intent);
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar snackbar=Snackbar.make(findViewById(R.id.settingsCoordinate),"checking email address",Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                String emailaddress= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                if (!email.getText().toString().trim().isEmpty() && emailaddress!=null) {
                    snackbar.setText("Sending password reset mail to "+emailaddress);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                snackbar.setText("Password reset mail is send to your email id");
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        snackbar.dismiss();
                                    }
                                },3000);
                                Toast.makeText(account_settings.this, "Email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                snackbar.setText("Email doesn't exist");
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        snackbar.dismiss();
                                    }
                                },3000);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackbar.setText("Error occurred Error:"+e.getMessage());
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    snackbar.dismiss();
                                }
                            },3000);
                        }
                    });
                }else {
                    snackbar.setText("Failed to send email");
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    },3000);
                }
            }
        });

        changeUserImageOnclick();
    }

    public void changeUserImageOnclick( ) {
         findViewById(R.id.edit_profile_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showImageChooser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    start_camera_dialog();

                }


            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void start_camera_dialog( ){

        Log.e("Camera","Inside start_camera_dialog");
        final View dialogView = View.inflate( this, R.layout.settings_img_more_options, null);
        final Dialog dialog = new Dialog(Objects.requireNonNull(this),R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
      //  lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
         lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);


        dialog.setContentView(dialogView);

         dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    return true;
                }

                return false;
            }
        });
        dialogView.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showImageChooser();
            }
        });
        dialogView.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
               findViewById(R.id.UserImage).setVisibility(View.INVISIBLE);

                TextView textView= findViewById(R.id.img_text);
                textView.setText(Name_for_Image(user.getText().toString().trim()));
                FirebaseDatabase.getInstance().getReference("User Informations/"+
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/User_Img").setValue(null);
             }
        });
      //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));


        dialog.show();
    }


    private CharSequence Name_for_Image(String s1) {
        int j = 0, flag = 0, loc2 = 0;
        String Name = "";
        try {

            char[] ch = s1.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] != ' ' && flag == 0) {
                    flag = 1;
                    j = i;
                }
            }
            String name23;
            Name = s1.substring(j, s1.length());
            loc2 = Name.indexOf(" ");
            if (j == -1)
                j = 1;
            name23 = Name.substring(j, loc2);
            Name = Name.replace(name23, "");
            int j2 = 0;
            char[] ch1 = Name.toCharArray();
            System.out.println(" Name=-" + Name);
            int k = 0;
            while (k < Name.length()) {
                if (ch1[k] == ' ') {

                    j2 = k;
                }
                k++;
            }
            String second;

            if (j2 == Name.length() - 1)
                second = (Character.toString(ch[j + 1]));
            else second = (Character.toString(ch1[j2 + 1]));

            Name = (Character.toString(ch[j])) + second;
            Name = Name.toUpperCase();

            System.out.println("After loop ch1= -" + "-" + " Name=" + Name);


            // System.out.println("After loop ch1= -"+ch1[j2+1]+"-"+" Name="+Name );
        } catch (Exception NullPointerException) {
        }
        return check_credentials(Name);
    }
    String check_credentials(String name){
        if (name.length()>2){
            char[] ch = name.toCharArray();
            name=Character.toString(ch[0])+Character.toString(ch[1]);
        }
        return name.toUpperCase();
    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Food Item Image"), CHOOSE_IMAGE);
    }
    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("food/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
             profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                              taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     profileImageUrl =uri.toString();
                                     saveuserinformation(  profileImageUrl);
                                     Toast.makeText(getApplicationContext(),"Profile image changed successfully",Toast.LENGTH_SHORT).show();
                                 }
                             });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         }
                    });
        }
    }

     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriProfileImage);
                Glide.with(getApplicationContext()).asBitmap().apply(RequestOptions.circleCropTransform()).load(bitmap).into(imageView);
                //imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

             } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveuserinformation(String url) {
       FirebaseDatabase.getInstance().getReference("User Informations/"+FirebaseAuth.getInstance().getCurrentUser().getUid()
       ).child("User_Img").setValue(url);
    }

    public void onbackclicked(View view) {
        finish();
    }
}


