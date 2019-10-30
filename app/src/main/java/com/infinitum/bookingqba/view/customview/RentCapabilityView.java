package com.infinitum.bookingqba.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RentCapabilityView extends ConstraintLayout {

    private TextView tvRooms;
    private TextView tvBeds;
    private TextView tvBaths;
    private TextView tvHost;
    private String humanRooms, humanBeds, humanBaths, humanHost;

    public RentCapabilityView(Context context) {
        super(context);
        init(context, null);
    }

    public RentCapabilityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RentCapabilityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.rent_capability_layout, this);
    }

    public void setHumanRooms(String humanRooms) {
        this.humanRooms = humanRooms;
        tvRooms.setText(humanRooms);
    }

    public void setHumanBeds(String humanBeds) {
        this.humanBeds = humanBeds;
        tvBeds.setText(humanBeds);
    }

    public void setHumanBaths(String humanBaths) {
        this.humanBaths = humanBaths;
        tvBaths.setText(humanBaths);
    }

    public void setHumanHost(String humanHost) {
        this.humanHost = humanHost;
        tvHost.setText(humanHost);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvRooms = findViewById(R.id.tv_rooms);
        tvBeds = findViewById(R.id.tv_beds);
        tvBaths = findViewById(R.id.tv_baths);
        tvHost = findViewById(R.id.tv_host);
    }


}
