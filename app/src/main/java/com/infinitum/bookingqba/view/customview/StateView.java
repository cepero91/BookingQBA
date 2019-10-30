package com.infinitum.bookingqba.view.customview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.Constants;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class StateView extends LinearLayout implements View.OnClickListener {

    private AppCompatImageView stateImageView;
    private TextView tv_state_title, tv_state_description, tv_btn_refresh;
    private LinearLayout llRoot;

    private OnStateViewInteraction onStateViewInteraction;

    public StateView(Context context) {
        super(context, null);
        init(context);
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.stateview_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        llRoot = findViewById(R.id.ll_root);
        stateImageView = findViewById(R.id.iv_state);
        tv_state_title = findViewById(R.id.tv_state_title);
        tv_state_description = findViewById(R.id.tv_state_description);
        tv_btn_refresh = findViewById(R.id.tv_btn_refresh);
        PushDownAnim.setPushDownAnimTo(tv_btn_refresh).setOnClickListener(this);
        llRoot.setVisibility(GONE);
    }

    public void setHasRefreshButton(boolean showButton) {
        tv_btn_refresh.setVisibility(showButton ? VISIBLE : GONE);
    }

    public void setOnStateViewInteraction(OnStateViewInteraction onStateViewInteraction) {
        this.onStateViewInteraction = onStateViewInteraction;
    }

    public void setStatus(Status status) {
        switch (status) {
            case LOADING:
                llRoot.setVisibility(GONE);
                break;
            case SUCCESS:
                llRoot.setVisibility(GONE);
                break;
            case EMPTY:
                llRoot.setVisibility(VISIBLE);
                stateImageView.setImageResource(R.drawable.flame_empty);
                tv_state_title.setText("Oops!!");
                tv_state_description.setText(Constants.EMPTY_ERROR_MSG);
                break;
            case NO_CONNECTION:
                llRoot.setVisibility(VISIBLE);
                stateImageView.setImageResource(R.drawable.flame_no_conexion);
                tv_state_title.setText("Oops!!");
                tv_state_description.setText(Constants.CONNEXION_ERROR_MSG);
                break;
            case NO_COMMENT:
                llRoot.setVisibility(VISIBLE);
                stateImageView.setImageResource(R.drawable.flame_no_comment);
                tv_state_title.setText("Oops!!");
                tv_state_description.setText(Constants.NO_COMMENT_MSG);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_refresh:
                if (onStateViewInteraction != null) {
                    onStateViewInteraction.onRefreshClick();
                }
                break;
        }
    }

    public interface OnStateViewInteraction {
        void onRefreshClick();
    }

    public enum Status {
        SUCCESS, LOADING, EMPTY, NO_CONNECTION, NO_COMMENT
    }


}
