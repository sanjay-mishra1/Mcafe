package com.sanproject.mcafe.notification;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.sanproject.mcafe.AlbumsAdapter;
import com.sanproject.mcafe.R;

import androidx.annotation.Nullable;

public class notification_dialog extends Activity {
    AlbumsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_big);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

     }


}
