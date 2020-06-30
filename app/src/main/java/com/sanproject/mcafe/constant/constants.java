package com.sanproject.mcafe.constant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.annotation.RequiresApi;


public class constants {
    public static   String Canteen = "";
    public static   String Name = "";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   public static void animation(boolean show, final View cardView, View searchButton) {
       // final RelativeLayout cardView = findViewById(R.id.searchBar);

        int height = cardView.getHeight() ;
        int width = cardView.getWidth() ;
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (searchButton.getX());
        int cy = (int) (searchButton.getY());

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cy-cx,endRadius );
            revealAnimator.setDuration(1800);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            // showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cx, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(1800);
            anim.start();
        }

    }
}
