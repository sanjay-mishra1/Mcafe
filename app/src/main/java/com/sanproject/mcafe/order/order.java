package com.sanproject.mcafe.order;

import android.os.Bundle;
import android.view.View;

import com.sanproject.mcafe.R;
import com.sanproject.mcafe.orderFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class order extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FragmentManager FM = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = FM.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new orderFragment()).commit();

    }
}
