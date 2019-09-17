package com.infinitum.bookingqba.view.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarRangeDateView extends LinearLayout implements View.OnClickListener {

    private TextView startEndDate, validationText;
    private List<Calendar> disabledDays;
    private List<Calendar> validRangeDate;
    private CalendarRangeDateInteraction rangeDateInteraction;

    public CalendarRangeDateView(Context context) {
        this(context, null, 0);
    }

    public CalendarRangeDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarRangeDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((Activity) getContext())
                .getLayoutInflater()
                .inflate(R.layout.range_calendar_date_layout, this, true);

        init();
    }

    private void init() {
        setupSubviews();
    }

    public void setRangeDateInteraction(CalendarRangeDateInteraction rangeDateInteraction) {
        this.rangeDateInteraction = rangeDateInteraction;
    }

    private void setupSubviews() {
        startEndDate = findViewById(R.id.tv_btn_start_end_date);
        validationText = findViewById(R.id.tv_validation);
        startEndDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_start_end_date:
                showStartEndDatePicker();
                break;
        }

    }

    private void showStartEndDatePicker() {
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, -1);
        DatePickerBuilder oneDayBuilder = new DatePickerBuilder(getContext(), this::updateStartDateView)
                .pickerType(CalendarView.RANGE_PICKER)
                .daysLabelsColor(R.color.material_color_blue_grey_500)
                .headerColor(R.color.material_color_grey_200)
                .headerLabelColor(R.color.material_color_blue_grey_500)
                .selectionColor(R.color.colorAccent)
                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(R.color.colorPrimary)
                .previousButtonSrc(R.drawable.ic_fa_angle_left_line)
                .forwardButtonSrc(R.drawable.ic_fa_angle_right_line)
                .minimumDate(min)
                .disabledDays(disabledDays != null ? disabledDays : new ArrayList<>());

        if (validRangeDate != null) {
            oneDayBuilder.selectedDays(validRangeDate);
        }

        DatePicker oneDayPicker = oneDayBuilder.build();
        oneDayPicker.show();
    }

    private void updateStartDateView(List<Calendar> calendar) {
        if (calendar != null) {
            Calendar startSelectedDay = calendar.get(0);
            Calendar endSelectedDay = calendar.get(calendar.size() - 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            if (com.applandeo.materialcalendarview.utils.DateUtils.isFullDatesRange(calendar)) {
                rangeDateInteraction.nightSelectedCount(calendar.size() - 1);
                startEndDateCallback(startSelectedDay, endSelectedDay);
                validRangeDate = calendar;
                startEndDate.setText(simpleDateFormat.format(startSelectedDay.getTime()) + " / " + simpleDateFormat.format(endSelectedDay.getTime()));
                startEndDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_fa_calendar_check_line), null, null, null);
                startEndDate.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.colorPrimary));
                startEndDate.setTextColor(getResources().getColor(R.color.colorPrimary));
                validationText.setVisibility(GONE);
            } else {
                rangeDateInteraction.nightSelectedCount(0);
                validRangeDate = null;
                startEndDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_fa_calendar_times_line), null, null, null);
                startEndDate.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.material_color_red_500));
                startEndDate.setTextColor(getResources().getColor(R.color.material_color_red_500));
                validationText.setText(getResources().getString(R.string.range_error));
                validationText.setVisibility(VISIBLE);
            }
        }
    }

    private void startEndDateCallback(Calendar startSelectedDay, Calendar endSelectedDay) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        rangeDateInteraction.validRangeSelected(simpleDateFormat.format(startSelectedDay.getTime()), simpleDateFormat.format(endSelectedDay.getTime()));
    }

    public void setStrDisabledDays(List<String> disabledStrDates) {
        disabledDays = DateUtils.transformStringDatesToCalendars(disabledStrDates);
    }

    public void setErrorMsg(String msg) {
        validationText.setVisibility(VISIBLE);
        validationText.setText(msg);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        startEndDate.setOnClickListener(enabled?this:null);
    }

    public interface CalendarRangeDateInteraction {
        void nightSelectedCount(int dayCount);

        void validRangeSelected(String startDate, String endDate);
    }
}
