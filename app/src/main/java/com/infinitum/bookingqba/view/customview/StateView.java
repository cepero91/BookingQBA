package com.infinitum.bookingqba.view.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fujiyuu75.sequent.Sequent;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.infinitum.bookingqba.R;

public class StateView extends LinearLayout {

    private RoundedImageView stateImageView;
    private TextView stateTextView;
    private LinearLayout llRoot;

    public StateView(Context context) {
        this(context, null, 0);
    }

    public StateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((Activity)getContext())
                .getLayoutInflater()
                .inflate(R.layout.stateview_layout, this, true);

        init();
    }

    private void init() {
        setupSubviews();
    }

    private void setupSubviews() {
        llRoot = findViewById(R.id.ll_root);
        stateImageView = findViewById(R.id.iv_state);
        stateTextView = findViewById(R.id.tv_state);
        llRoot.setVisibility(GONE);
    }

    public void setStatus(Status status){
        switch (status){
            case LOADING:
                llRoot.setVisibility(GONE);
                break;
            case SUCCESS:
                llRoot.setVisibility(GONE);
                break;
            case EMPTY:
                llRoot.setVisibility(VISIBLE);
                stateImageView.setImageResource(R.drawable.fogg_no_comments);
                stateTextView.setText("Nada que mostrar");
                break;
            case NO_CONNECTION:
                llRoot.setVisibility(VISIBLE);
                stateImageView.setImageResource(R.drawable.fogg_no_connection_2);
                stateTextView.setText("Sin conexion");
                break;
        }
    }

    public enum Status{
        SUCCESS, LOADING , EMPTY, NO_CONNECTION
    }


}
