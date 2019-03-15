package com.infinitum.bookingqba.view.adapters.databinding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.BindingAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleGoneAnim")
    public static void showHideAnim(View view, boolean show) {
        if(show){
            view.setAlpha(1);
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }else{
            view.animate()
                    .alpha(0f).setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    })
                    .start();
        }
    }
}