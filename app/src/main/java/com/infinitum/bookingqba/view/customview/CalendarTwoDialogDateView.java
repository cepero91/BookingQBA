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
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.infinitum.bookingqba.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarTwoDialogDateView extends LinearLayout implements View.OnClickListener, OnSelectDateListener {

    private TextView startDate, endDate, validationText;
    private List<Calendar> disabledDays;
    private Calendar startSelectedDay;
    private Calendar minEndSelectedDay;
    private Calendar endSelectedDay;

    public CalendarTwoDialogDateView(Context context) {
        this(context, null, 0);
    }

    public CalendarTwoDialogDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarTwoDialogDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((Activity) getContext())
                .getLayoutInflater()
                .inflate(R.layout.two_dialog_calendar_date_layout, this, true);

        init();
    }

    private void init() {
        setupSubviews();
    }

    private void setupSubviews() {
        startDate = findViewById(R.id.tv_btn_start_date);
        endDate = findViewById(R.id.tv_btn_end_date);
        validationText = findViewById(R.id.tv_validation);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endDate.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_start_date:
                showStartDatePicker();
                break;
            case R.id.tv_btn_end_date:
                showEndDatePicker();
                break;
        }

    }

    private void showEndDatePicker() {
        Calendar cloneStart = (Calendar) startSelectedDay.clone();
        cloneStart.add(Calendar.DAY_OF_MONTH,1);
        DatePickerBuilder oneDayBuilder = new DatePickerBuilder(getContext(), this::updateEndDateView)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .headerColor(R.color.material_color_grey_200)
                .headerLabelColor(R.color.material_color_blue_grey_500)
                .selectionColor(R.color.colorPrimary)
                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(R.color.colorPrimary)
                .previousButtonSrc(R.drawable.ic_fa_angle_left_line)
                .forwardButtonSrc(R.drawable.ic_fa_angle_right_line)
                .minimumDate(cloneStart)
                .disabledDays(getDisabledDays());

        if(endSelectedDay!=null){
            oneDayBuilder.date(endSelectedDay);
        }

        com.applandeo.materialcalendarview.DatePicker oneDayPicker = oneDayBuilder.build();
        oneDayPicker.show();
    }

    private void showStartDatePicker() {
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, 0);
        DatePickerBuilder oneDayBuilder = new DatePickerBuilder(getContext(), this::updateStartDateView)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .headerColor(R.color.material_color_grey_200)
                .headerLabelColor(R.color.material_color_blue_grey_500)
                .selectionColor(R.color.colorPrimary)
                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(R.color.colorPrimary)
                .previousButtonSrc(R.drawable.ic_fa_angle_left_line)
                .forwardButtonSrc(R.drawable.ic_fa_angle_right_line)
                .minimumDate(min)
                .disabledDays(getDisabledDays());

        if(startSelectedDay!=null){
            oneDayBuilder.date(startSelectedDay);
        }

        DatePicker oneDayPicker = oneDayBuilder.build();
        oneDayPicker.show();
    }

    private void updateStartDateView(List<Calendar> calendar) {
        if (calendar != null) {
            startSelectedDay = calendar.get(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            startDate.setText(simpleDateFormat.format(startSelectedDay.getTime()));
            startDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_fa_calendar_check_line), null, null, null);
            startDate.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.colorPrimary));
            startDate.setTextColor(getResources().getColor(R.color.colorPrimary));
            endDate.setEnabled(true);
        }
    }

    private void updateEndDateView(List<Calendar> calendar) {
        if (calendar != null) {
            endSelectedDay = calendar.get(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            endDate.setText(simpleDateFormat.format(endSelectedDay.getTime()));
            endDate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_fa_calendar_check_line), null, null, null);
            endDate.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.colorPrimary));
            endDate.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public List<Calendar> getDisabledDays() {
        disabledDays = testDisableDays();
        return disabledDays != null ? disabledDays : new ArrayList<>();
    }

    @Override
    public void onSelect(List<Calendar> calendar) {

    }


    private List<Calendar> testDisableDays() {
        List<Calendar> calendarList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String[] datesExample = new String[]{"8-09-2019", "15-09-2019", "20-09-2019"};
        Date date = null;
        Calendar calendarItem;
        for (String dates : datesExample) {
            try {
                date = simpleDateFormat.parse(dates);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendarItem = dateToCalendar(date);
            calendarList.add(calendarItem);
        }
        return calendarList;
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
