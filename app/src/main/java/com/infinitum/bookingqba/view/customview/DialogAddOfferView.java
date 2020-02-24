package com.infinitum.bookingqba.view.customview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.view.profile.AddPoiAdapter;
import com.infinitum.bookingqba.view.profile.uploaditem.OfferFormObject;

import org.mapsforge.poi.storage.PointOfInterest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DialogAddOfferView extends LinearLayout implements View.OnClickListener {

    private AddOfferClick addOfferClick;
    private EditText nameEditText, priceEditText, descEditText;
    private OfferFormObject offerFormObject;
    private OfferFormObject offerFormObjectCopy;
    private boolean isEditFlag = false;
    private int pos = -1;

    public DialogAddOfferView(Context context) {
        this(context, null, 0);
    }

    public DialogAddOfferView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogAddOfferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof AddOfferClick) {
            addOfferClick = (AddOfferClick) context;
        } else {
            throw new IllegalStateException(context + " must implement" + AddOfferClick.class.getSimpleName());
        }
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.dialog_add_offer, this);
        setupSubviews();
    }

    private void setupSubviews() {
        offerFormObject = new OfferFormObject();
        TextView btnSave = findViewById(R.id.tv_btn_save);
        btnSave.setOnClickListener(this);
        nameEditText = findViewById(R.id.et_offer_name);
        priceEditText = findViewById(R.id.et_price);
        descEditText = findViewById(R.id.et_description);
    }

    public void setOfferFormObject(OfferFormObject offerFormObject, int pos) {
        this.isEditFlag = true;
        this.offerFormObject = offerFormObject;
        nameEditText.setText(offerFormObject.getName());
        descEditText.setText(offerFormObject.getDescription());
        priceEditText.setText(offerFormObject.getPrice());
        this.pos = pos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_save:
                prepareForSave();
                break;
        }

    }

    private void prepareForSave() {
        offerFormObjectCopy = new OfferFormObject();
        offerFormObjectCopy.setName(nameEditText.getText().toString());
        offerFormObjectCopy.setDescription(descEditText.getText().toString());
        offerFormObjectCopy.setPrice(priceEditText.getText().toString());
        if (!isEditFlag) {
            offerFormObject.setUuid(UUID.randomUUID().toString());
            offerFormObject.setName(nameEditText.getText().toString());
            offerFormObject.setDescription(descEditText.getText().toString());
            offerFormObject.setPrice(priceEditText.getText().toString());
            offerFormObject.setVersion(0);
            addOfferClick.onButtonSaveClick(offerFormObject, false, -1);
        } else {
            if (!offerFormObject.equals(offerFormObjectCopy)) {
                offerFormObject.setName(nameEditText.getText().toString());
                offerFormObject.setDescription(descEditText.getText().toString());
                offerFormObject.setPrice(priceEditText.getText().toString());
                offerFormObject.setVersion(0);
                addOfferClick.onButtonSaveClick(offerFormObject, true, pos);
            } else {
                AlertUtils.showErrorTopToast(getContext(),"No hay nada que guardar");
            }
        }

    }

    public interface AddOfferClick {

        void onButtonSaveClick(OfferFormObject offerFormObject, boolean isEdit, int pos);

    }
}
