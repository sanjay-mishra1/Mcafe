package com.sanproject.mcafe.message;


 import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.sanproject.mcafe.constant.constants.Canteen;
import static com.sanproject.mcafe.constant.constants.Name;

public class StoreMessage   {
     private EditText editText;
    private String IMG;
    private String caption;
    private RelativeLayout progressbar;
    StoreMessage(String Type, String uid, EditText editText, RelativeLayout progressBar, String message){
        this.editText=editText;
        this.progressbar=progressBar;
        this.IMG=message;
        TotalHelp(Type,uid);
    }
    public StoreMessage(String Type, String uid, EditText editText, RelativeLayout progressBar, String message,String caption){
        this.editText=editText;
        this.progressbar=progressBar;
        this.IMG=message;
        this.caption=caption;
         TotalHelp(Type,uid);
    }

    private void TotalHelp(final String type, String uid) {
        progressbar.setVisibility(View.VISIBLE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd",Locale.US);
        final String key = date.format(c.getTime());
        date = new SimpleDateFormat("MMMM d, yyyy || h:mm a",Locale.US);
        final String messageTime = date.format(c.getTime());
          getDateStatus(setDate1(messageTime),FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(uid + "/Keys" ),type,key,setTime(messageTime),uid);

     }
    private void getDateStatus(final String date, DatabaseReference address, final String type, final String key, final String Time, final String uid)   {
        final boolean[] status = {false};

        final Query query=address.orderByValue().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {


                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {               // album_allorders data1=dataSnapshot1 .getValue(album_allorders.class);

                            if (date.contains((String)dataSnapshot1.child("Date").getValue())) {
                                saveMessage(type, date, key, Time, true,uid);

                            } else {

                                saveMessage(type, date, key, Time, false,uid);

                            }
                        }

                    }else{
                        saveMessage(type,date,key,Time,false,uid);

                    }
                }catch (Exception e){

                    status[0]=false;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
      private void saveMessage(String type, String date, String key, String Time, boolean Status, String uid) {
        Log.e("Firebase Messaging","Inside save message  ");
      String  Total= String.valueOf(System.currentTimeMillis());
        DatabaseReference   database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(uid + "/Messages/"+ "/").
                child(String.valueOf(Total));
        Log.e("Firebase Messaging","Save message credentials are "+"T <"+type+"> D <"+date+"> ke <"+key+"> Time<"+Time+">"
                +Status+" <uid"+uid);
         String message = editText.getText().toString().trim();
        if (message.trim().isEmpty()) {
            // Edit_message.setError("Please enter Something");
           // editText.requestFocus();
            if (type.contains("TEXT")) {
                Log.e("help", "  Error message is empty");
                Log.e("Firebase Messaging", "Inside save message  message is empty");
                progressbar.setVisibility(View.GONE);
                return;
            }else{
                message="";
            }
        }
        editText.setText("");
        if (!Status)
        {

            storenewKey(key,date, String.valueOf(Total),uid);
            database.child("From").setValue("Date");
            database.child("Date").setValue(date.toUpperCase());
            database = FirebaseDatabase.getInstance().getReference().child("Messages").
                    child("Help").child(Canteen).child(uid + "/Messages/"+ "/").
                    child(String.valueOf(Total+1));
            database.child("Date").setValue(date);

        }else{
            database.child("Date").setValue("");
        }
        Log.e("help", " Message Saved ");
        database.child("Message").setValue(message);
        database.child("Time").setValue(Time);

         if (type.contains("IMG"))
         { message="IMG "+caption;
             database.child("Food_Image").setValue(IMG);
             if (caption.trim().isEmpty())
                 database.child("Message").setValue("");
             else database.child("Message").setValue(caption);



         }
         save_Extra_info(message,Time,uid);

          database.child("Seen").setValue(false);
        database.child("From").setValue(type);

     progressbar.setVisibility(View.GONE);

    }
    private void save_Extra_info(String Message,String Time,String uid){
        final DatabaseReference   database = FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(uid);
        //SharedPreferences shared= getSharedPreferences("User_Credentials", Context.MODE_PRIVATE);
        database.child("UId").setValue(uid);
        database.child("User_Name").setValue(Name);
     //   String img=shared.getString("UserImg","");
         database.child("User_Img").setValue("");

        database.child("User_last_Message").setValue(Message);
        database.child("Time").setValue(Time);
        database.child("Server_Time").setValue(ServerValue.TIMESTAMP);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long counter ;
                try
                {

                    counter=Long.parseLong( (String) dataSnapshot.child("Last_message_counter").getValue());
                    Log.e("Last_message_counter","Inside try "+ String.valueOf(counter));
                    database.child("Last_message_counter").setValue(String.valueOf(counter+1));
                }catch (Exception e){
                    Log.e("Last_message_counter","  "+ String.valueOf(1));
                    database.child("Last_message_counter").setValue("1");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public String setTime(String time){

        time=time.substring(time.indexOf("||")+3,time.length());
        return (time);


    }
    private void storenewKey(String key, String date, String Total, String uid){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Messages").
                child("Help").child(Canteen).child(uid + "/Keys/"+key);
        databaseReference.child("Date").setValue(date);
        databaseReference.child("key").setValue(key);
        databaseReference.child("Total").setValue(Total);
    }
     private String setDate1(String time){

        try {
            time= time.substring(0,time.indexOf("||")-1 );
            return (String.format("  %s  ", time));
        }catch (Exception ignored){}
        return "";
    }

}
