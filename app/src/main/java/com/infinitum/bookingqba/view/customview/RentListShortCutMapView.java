package com.infinitum.bookingqba.view.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.profile.uploaditem.OfferFormObject;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class RentListShortCutMapView extends LinearLayout implements View.OnClickListener {

    private ShortCutMapInteraction shortCutMapInteraction;
    private FrameLayout frameLayoutBar;
    private LinearLayout llContentButtons;
    private TextView buttonFive, buttonTen, buttonAll;
    private AppCompatImageView shortCutIcon;
    private boolean isContentOpen = false;

    public RentListShortCutMapView(Context context) {
        this(context, null, 0);
    }

    public RentListShortCutMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RentListShortCutMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((Activity)getContext())
                .getLayoutInflater()
                .inflate(R.layout.rent_list_short_cut_map_layout, this, true);

        init();
    }

    private void init() {
        setupSubviews();
    }

    private void setupSubviews() {
        frameLayoutBar = findViewById(R.id.fl_content_conf_map);
        llContentButtons = findViewById(R.id.ll_content_short_cut_buttons);
        llContentButtons.setVisibility(GONE);
        buttonFive = findViewById(R.id.button_five);
        buttonTen = findViewById(R.id.button_ten);
        buttonAll = findViewById(R.id.button_all);
        shortCutIcon = findViewById(R.id.iv_short_cut_icon);
        frameLayoutBar.setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(buttonFive).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(buttonTen).setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(buttonAll).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_content_conf_map:
                showOrHideShortCutContent();
                break;
            case R.id.button_five:
                shortCutMapInteraction.onButtonClick(v) ;
                break;
            case R.id.button_ten:
                shortCutMapInteraction.onButtonClick(v);
                break;
            case R.id.button_all:
                shortCutMapInteraction.onButtonClick(v);
                break;
        }

    }

    private void showOrHideShortCutContent() {
        if(isContentOpen){
            isContentOpen = false;
            llContentButtons.setVisibility(GONE);
            shortCutIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.material_color_blue_grey_500)));
        }else{
            isContentOpen = true;
            llContentButtons.setVisibility(VISIBLE);
            shortCutIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.material_color_teal_A700)));
        }
    }

    public void setShortCutMapInteraction(ShortCutMapInteraction shortCutMapInteraction) {
        this.shortCutMapInteraction = shortCutMapInteraction;
    }

    public interface ShortCutMapInteraction {
        void onButtonClick(View view);
    }
}
