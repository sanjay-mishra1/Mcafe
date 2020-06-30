package com.sanproject.mcafe;

import android.view.View;
import android.widget.TextView;


public  class account_info_setters {
    View mView=null;

    public account_info_setters() {
        super();
        // mView = itemView;
    }


    public void setName(String name) {

        //TextView user = (TextView) mView.findViewById(R.id.edituser1);
        //user.setText(name);

    }

    public void setPhone(String phone) {

        TextView phones = (TextView) mView.findViewById(R.id.editphone);
        phones.setText(phone);
    }

    public void setEmail(String email) {
       // TextView emails = (TextView) mView.findViewById(R.id.editemail1);
        //emails.setText(email);

    }

    public void setCollegid(String id) {
        TextView collegeid = (TextView) mView.findViewById(R.id.collegeId);
        collegeid.setText(id);

    }
}

