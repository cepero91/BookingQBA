package com.infinitum.bookingqba.view.customview;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.view.profile.AddPoiAdapter;
import com.thekhaeng.pushdownanim.PushDownAnim;


import java.util.List;

public class DialogLocationConfirmView extends ConstraintLayout implements View.OnClickListener {

    private DialogLocationConfirmListener dialogLocationConfirmListener;

    private TextView btnConfirm;

    public DialogLocationConfirmView(Context context) {
        this(context, null, 0);
    }

    public DialogLocationConfirmView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogLocationConfirmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof DialogLocationConfirmListener) {
            dialogLocationConfirmListener = (DialogLocationConfirmListener) context;
        } else {
            throw new IllegalStateException(context + " must implement" + DialogLocationConfirmListener.class.getSimpleName());
        }
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.dialog_location_result, this);
        setupSubviews();
    }

    private void setupSubviews() {
        btnConfirm = findViewById(R.id.tv_btn_confirm);
        PushDownAnim.setPushDownAnimTo(btnConfirm).setOnClickListener(this);
        findViewById(R.id.ll_content_progress).setVisibility(GONE);
    }

    public void isLoading(boolean isLoading){
        if(isLoading){
            findViewById(R.id.cl_content_confirmation).setVisibility(GONE);
            findViewById(R.id.ll_content_progress).setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_confirm:
                dialogLocationConfirmListener.onButtonConfirmClick();
                break;
        }

    }

    public interface DialogLocationConfirmListener {
        void onButtonConfirmClick();
    }
}
