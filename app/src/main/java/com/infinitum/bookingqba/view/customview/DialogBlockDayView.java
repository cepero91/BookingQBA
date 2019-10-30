package com.infinitum.bookingqba.view.customview;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.view.profile.AddPoiAdapter;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class DialogBlockDayView extends ConstraintLayout implements View.OnClickListener {

    private TextView tv_from_value, tv_from_day, tv_until_value, tv_until_day, tv_btn_ocupate;
    private AppCompatEditText et_note;
    private Date from;
    private Date until;

    private DialogBlockDayInteraction dialogBlockDayInteraction;

    public DialogBlockDayView(Context context) {
        this(context, null, 0);
    }

    public DialogBlockDayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogBlockDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.dialog_block_dates, this);
        setupSubviews();
    }

    private void setupSubviews() {
        tv_btn_ocupate = findViewById(R.id.tv_btn_ocupate);
        PushDownAnim.setPushDownAnimTo(tv_btn_ocupate).setOnClickListener(this);
        tv_from_value = findViewById(R.id.tv_from_value);
        tv_from_day = findViewById(R.id.tv_day_from_value);
        tv_until_value = findViewById(R.id.tv_until_value);
        tv_until_day = findViewById(R.id.tv_day_until_value);
        et_note = findViewById(R.id.et_note);
    }

    public void setDialogBlockDayInteraction(DialogBlockDayInteraction dialogBlockDayInteraction) {
        this.dialogBlockDayInteraction = dialogBlockDayInteraction;
    }

    public void setFrom(Date from) {
        this.from = from;
        SimpleDateFormat largeDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault());
        tv_from_value.setText(largeDate.format(from));
        tv_from_day.setText(dayOfWeek.format(from));
    }

    public void setUntil(Date until) {
        this.until = until;
        SimpleDateFormat largeDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault());
        tv_until_value.setText(largeDate.format(until));
        tv_until_day.setText(dayOfWeek.format(until));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_ocupate:
                if(validInput()) {
                    dialogBlockDayInteraction.onButtonSendClick(et_note.getText().toString());
                }
                break;
        }

    }

    private boolean validInput() {
        boolean isValid = true;
        if(et_note.getText().toString().isEmpty()){
            isValid = false;
            et_note.setError("Campo requerido");
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
            et_note.startAnimation(animation);
        }
        return isValid;
    }

    public interface DialogBlockDayInteraction {
        void onButtonSendClick(String note);
    }
}
