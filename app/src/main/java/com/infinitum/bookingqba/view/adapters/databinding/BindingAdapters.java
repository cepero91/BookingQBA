package com.infinitum.bookingqba.view.adapters.databinding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.BindingAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.util.GlideApp;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;
import java.util.Set;

public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleGoneAnim")
    public static void showHideAnim(View view, boolean show) {
        if (show) {
            view.setAlpha(1);
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
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

    @BindingAdapter("setRating")
    public static void setRating(BaseRatingBar view, float rating) {
        view.setRating(rating);
    }

    @BindingAdapter("setPriceText")
    public static void setPriceText(TextView view, double price) {
        String priceString = String.valueOf(price);
        view.setText(String.format("$ %s", priceString));
    }

    @BindingAdapter("setRentModeString")
    public static void setRentModeString(TextView view, String rentMode) {
        view.setText(rentMode);
    }

    @BindingAdapter("setMaxGaleriePictures")
    public static void setMaxGaleriePictures(TextView view, int size) {
        view.setText(String.format("%s Foto%s",size,size>1?"s":""));
        view.setGravity(Gravity.CENTER);
    }

    @BindingAdapter("setRentDetailImage")
    public static void setRentDetailImage(AppCompatImageView view, byte[] imageByte) {
        GlideApp.with(view).load(imageByte).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(view);
    }

    @BindingAdapter("setMaxRooms")
    public static void setMaxRooms(TextView view, int maxRoom) {
        String maxRoomString = String.valueOf(maxRoom);
        view.setText(String.format("%s Cuarto%s", maxRoomString,maxRoom>1?"s":""));
    }

    @BindingAdapter("setCapability")
    public static void setCapability(TextView view, int capability) {
        String capabilityString = String.valueOf(capability);
        view.setText(String.format("%s Huesp.", capabilityString));
    }

    @BindingAdapter("setMaxBath")
    public static void setMaxBath(TextView view, int maxBath) {
        String maxBathString = String.valueOf(maxBath);
        view.setText(String.format("%s Baño%s", maxBathString,maxBath>1?"s":""));
    }

    @BindingAdapter("setMaxBeds")
    public static void setMaxBeds(TextView view, int maxBeds) {
        String maxBedsString = String.valueOf(maxBeds);
        view.setText(String.format("%s Cama%s", maxBedsString,maxBeds>1?"s":""));
    }

    @BindingAdapter("setRentListImage")
    public static void setRentListImage(AppCompatImageView view, byte[] imageByte) {
        GlideApp.with(view).load(imageByte).placeholder(R.drawable.placeholder).into(view);
    }

    @BindingAdapter("setFlexBoxItem")
    public static void setFlexBoxItem(FlexboxLayout view, List<View> items) {
        if(items!=null && items.size()>0){
            for(View item: items){
                view.addView(item);
            }
        }
    }

    @BindingAdapter("setGalerieVisibility")
    public static void setGalerieVisibility(View view, int size) {
        view.setVisibility(size > 1 ? View.VISIBLE : View.GONE);
    }


}