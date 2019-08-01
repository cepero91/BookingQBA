package com.infinitum.bookingqba.view.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.profile.AddPoiAdapter;


import org.mapsforge.poi.storage.PointOfInterest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DialogLocationConfirmView extends LinearLayout implements View.OnClickListener {

    private DialogLocationConfirmListener dialogLocationConfirmListener;

    private String argLatitude;
    private String argLongitude;
    private String referenceZone;
    private Collection<PointOfInterest> points;

    private RecyclerView recyclerView;

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
        TextView btnConfirm = findViewById(R.id.tv_btn_confirm);
        TextView btnSave = findViewById(R.id.tv_btn_save);
        btnConfirm.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        findViewById(R.id.ll_content_progress).setVisibility(GONE);
        findViewById(R.id.ll_content_params).setVisibility(GONE);
        recyclerView = findViewById(R.id.rv_poi);
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
        ((TextView) findViewById(R.id.tv_reference_zone)).setText(referenceZone);
    }

    public void setPoints(Collection<PointOfInterest> points) {
        this.points = points;
        setupPoisAdapter(points);
    }

    private void setupPoisAdapter(Collection<PointOfInterest> argPois) {
        if (argPois != null && argPois.size() > 0) {
            List<PointOfInterest> pointOfInterests = new ArrayList<>(argPois);
            AddPoiAdapter addPoiAdapter = new AddPoiAdapter(pointOfInterests);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(addPoiAdapter);
        }
    }

    public void showContentParamsView() {
        findViewById(R.id.ll_content_progress).setVisibility(GONE);
        findViewById(R.id.ll_content_params).setVisibility(VISIBLE);
    }

    public void setLocationString(String argLatitude, String argLongitude) {
        this.argLatitude = argLatitude;
        this.argLongitude = argLongitude;
    }

    public void isLoading(boolean isLoading){
        if(isLoading){
            findViewById(R.id.ll_content_confirmation).setVisibility(GONE);
            findViewById(R.id.ll_content_progress).setVisibility(VISIBLE);
        }else{
            findViewById(R.id.ll_content_progress).setVisibility(GONE);
            findViewById(R.id.ll_content_params).setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_confirm:
                dialogLocationConfirmListener.onButtonConfirmClick();
                break;
            case R.id.tv_btn_save:
                dialogLocationConfirmListener.onButtonSaveClick();
                break;
        }

    }

    public interface DialogLocationConfirmListener {

        void onButtonSaveClick();

        void onButtonConfirmClick();

    }
}
