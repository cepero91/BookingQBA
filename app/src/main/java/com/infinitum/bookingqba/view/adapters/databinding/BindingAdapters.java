package com.infinitum.bookingqba.view.adapters.databinding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.BindingAdapter;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.flexbox.FlexboxLayout;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.view.adapters.items.reservation.BookInfoItem;
import com.moos.library.HorizontalProgressView;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;
import java.util.Set;

public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void visibleGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleByLength")
    public static void visibleByLength(View view, String text) {
        if (text != null) {
            view.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("discreteVisibility")
    public static void discreteVisibility(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
        view.setText(String.format("$ %.2f", price));
    }

    @BindingAdapter("setRentModeString")
    public static void setRentModeString(TextView view, String rentMode) {
        view.setText(rentMode);
    }

    @BindingAdapter("setMaxGaleriePictures")
    public static void setMaxGaleriePictures(TextView view, int size) {
        view.setText(String.format("%s Foto%s", size, size > 1 ? "s" : ""));
        view.setGravity(Gravity.CENTER);
    }


    @BindingAdapter("setMaxRooms")
    public static void setMaxRooms(TextView view, int maxRoom) {
        String maxRoomString = String.valueOf(maxRoom);
        view.setText(String.format("%s Cuarto%s", maxRoomString, maxRoom > 1 ? "s" : ""));
    }

    @BindingAdapter("setCapability")
    public static void setCapability(TextView view, int capability) {
        String capabilityString = String.valueOf(capability);
        view.setText(String.format("%s Huesp.", capabilityString));
    }

    @BindingAdapter("setMaxBath")
    public static void setMaxBath(TextView view, int maxBath) {
        String maxBathString = String.valueOf(maxBath);
        view.setText(String.format("%s Baño%s", maxBathString, maxBath > 1 ? "s" : ""));
    }

    @BindingAdapter("setMaxBeds")
    public static void setMaxBeds(TextView view, int maxBeds) {
        String maxBedsString = String.valueOf(maxBeds);
        view.setText(String.format("%s Cama%s", maxBedsString, maxBeds > 1 ? "s" : ""));
    }

    @BindingAdapter("setRentListImage")
    public static void setRentListImage(RoundedImageView view, String imagePath) {
        String path = "file:" + imagePath;
        Picasso.get()
                .load(path)
                .resize(420, 280)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setMyRentItemImage")
    public static void setMyRentItemImage(RoundedImageView view, String imagePath) {
        Picasso.get()
                .load(imagePath)
                .resize(420, 280)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setRemoteCircleImage")
    public static void setRemoteCircleImage(RoundedImageView view, String imagePath) {
        Picasso.get()
                .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setFlexBoxItem")
    public static void setFlexBoxItem(FlexboxLayout view, List<View> items) {
        if (items != null && items.size() > 0) {
            for (View item : items) {
                view.addView(item);
            }
        }
    }

    @BindingAdapter("setGalerieVisibility")
    public static void setGalerieVisibility(View view, int size) {
        view.setVisibility(size > 1 ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("setMbSize")
    public static void setMbSize(TextView view, long size) {
        float fileSizeKb = ((float) size / 1024);
        float fileSizeMb = ((float) fileSizeKb / 1024);
        view.setText(String.format("%.2f Mb", fileSizeMb));
    }

    @BindingAdapter("setPercent")
    public static void setPercent(TextView view, float percent) {
        view.setText(String.format("%.1f %s", percent, "%"));
    }

    @BindingAdapter("setProgress")
    public static void setProgress(HorizontalProgressView view, float percent) {
        view.setProgress(percent);
    }

    @BindingAdapter("setOverlayGone")
    public static void setOverlayGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter("setRoundedImage")
    public static void setRoundedImage(RoundedImageView view, String imagePath) {
        String path;
        if (!imagePath.contains("http")) {
            path = "file:" + imagePath;
        } else {
            path = imagePath;
        }
        Picasso.get()
                .load(path)
                .resize(480, 320)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter("setWished")
    public static void setWished(AppCompatImageView view, int wished) {
        if (wished == 1) {
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.ic_bookmark_orange);
        } else if (wished == 0) {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("setVotes")
    public static void setVotes(TextView view, int votes) {
        view.setText(String.format("(%s voto%s)", votes, votes > 1 ? "s" : ""));
    }

    @BindingAdapter("negativeMargin")
    public static void negativeMargin(View view, boolean needNegativeMargin) {
        int overlapTop = view.getResources().getDimensionPixelSize(R.dimen.overlap_top);
        if (needNegativeMargin) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior behavior = (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
            behavior.setOverlayTop(overlapTop);
        } else {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior behavior = (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
            behavior.setOverlayTop(0);
        }
    }

    @BindingAdapter("setIconResource")
    public static void setIconResource(AppCompatImageView view, int resource) {
        view.setImageResource(resource);
    }

    @BindingAdapter("setLockedIcon")
    public static void setLockedIcon(AppCompatImageView view, boolean active) {
        if (active) {
            view.setImageResource(R.drawable.ic_unlocked_2_bold_small_set);
        } else {
            view.setImageResource(R.drawable.ic_locked_5_bold_small_set);
        }
    }

    @BindingAdapter("setBookState")
    public static void setBookState(TextView view, ReservationType bookState) {
        switch (bookState) {
            case PENDING:
                view.setText("Pendiente");
                view.setBackgroundResource(R.drawable.shape_bg_pending);
                break;
            case CHECKED:
                view.setText("Vista");
                view.setBackgroundResource(R.drawable.shape_bg_checked);
                break;
            case ACCEPTED:
                view.setText("Aceptada");
                view.setBackgroundResource(R.drawable.shape_bg_accepted);
                break;
            case DENIED:
                view.setText("Cancelada");
                view.setBackgroundResource(R.drawable.shape_bg_denied);
                break;
            case OCUPATE:
                view.setText("Ocupados");
                view.setBackgroundResource(R.drawable.shape_bg_ocupate);
                break;
            default:
                view.setText("Pendiente");
                view.setBackgroundResource(R.drawable.shape_bg_pending);
                break;
        }
    }

    @BindingAdapter("visibilityByNight")
    public static void visibilityByNight(View view, String rentMode) {
        if (rentMode != null && rentMode.equalsIgnoreCase("por noche")) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("visibilityByHour")
    public static void visibilityByHour(View view, String rentMode) {
        if (rentMode != null && rentMode.equalsIgnoreCase("por horas")) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}