package com.infinitum.bookingqba.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.infinitum.bookingqba.R;

import org.oscim.tiling.source.mapfile.PointOfInterest;

import java.util.List;

public class DialogLocationConfirmView extends LinearLayout implements View.OnClickListener {

    private DialogLocationConfirmListener dialogLocationConfirmListener;

    private String address;
    private String referenceZone;
    private List<PointOfInterest> points;

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
        findViewById(R.id.ll_content_progress).setVisibility(GONE);
        findViewById(R.id.ll_content_params).setVisibility(GONE);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public void setPoints(List<PointOfInterest> points) {
        this.points = points;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_btn_confirm:
                findViewById(R.id.ll_content_confirmation).setVisibility(GONE);
                findViewById(R.id.ll_content_progress).setVisibility(VISIBLE);
                dialogLocationConfirmListener.onButtonConfirmClick();
                break;
            case R.id.tv_btn_save:
                dialogLocationConfirmListener.onButtonSaveClick();
        }

    }

    public interface DialogLocationConfirmListener {

        void onButtonSaveClick();

        void onButtonConfirmClick();

    }
}
