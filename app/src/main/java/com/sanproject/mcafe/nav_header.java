package com.sanproject.mcafe;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sanjay on 14/04/2018.
 */

public class nav_header  {
    TextView nav_name,nav_imgName,nav_phone;
    DatabaseReference database;
    FirebaseAuth mAuth;
    View view;
    public nav_header(View view) {
      //  super(view);
    setNavigationInfo();
    }
nav_header(){}
        public void setNavigationInfo() {
          //  View view = new View(R.layout.nav_headermain);
            mAuth= FirebaseAuth.getInstance();
            database= FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid());
            nav_imgName=view.findViewById(R.id.name_image);
            nav_name=view.findViewById(R.id.username_nav);
            nav_phone=view.findViewById(R.id.phone_nav);
          //  navigation_dataDisplay nav_info=new navigation_dataDisplay();
            try {
                nav_phone.setText("Phone working");
                nav_name.setText("Name working");
                nav_imgName.setText("Image working");
            }catch (Exception NullPointerException){}
        }

        private CharSequence Name_for_Image(String s1) {
            int j=0,flag=0,loc2=0;
            char[] ch=s1.toCharArray();
            for(int i=0;i<ch.length;i++){
                if(ch[i]!=' ' && flag==0){
                    flag=1;
                    j=i;
                }
            }
            String name23;
            String Name=s1.substring(j, s1.length());
            loc2=Name.indexOf(" ");
            name23=Name.substring(j-1,loc2);
            Name=Name.replace(name23,"");
            int j2=0;
            char[] ch1=Name.toCharArray();
            System.out.println(" Name=-"+Name );
            int k=0;
            while(k<Name.length())
            {
                if(ch1[k]==' '){

                    j2=k;
                }
                k++;
            }

            Name=(Character.toString(ch[j])+Character.toString(ch1[j2+1]));
            Name=Name.toUpperCase();
            // System.out.println("After loop ch1= -"+ch1[j2+1]+"-"+" Name="+Name );

            return Name;
        }

    }

