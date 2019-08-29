package com.infinitum.bookingqba.view.rents;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.aminography.primecalendar.PrimeCalendar;
import com.aminography.primecalendar.common.CalendarFactory;
import com.aminography.primecalendar.common.CalendarType;
import com.aminography.primedatepicker.OnDayPickedListener;
import com.aminography.primedatepicker.PickType;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityReservationBinding reservationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservationBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);

        reservationBinding.tvSinceValue.setOnClickListener(this);
        reservationBinding.tvToValue.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_value:
                showEndDatePicker();
                break;
            case R.id.tv_since_value:
                showStartDatePicker();
                break;
        }
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReservationActivity.this,
                (datePicker, year1, month1, day) -> {
                    reservationBinding.tvSinceValue.setText(day + "-" + month + "-" + year);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReservationActivity.this,
                (datePicker, year1, month1, day) -> {
                    reservationBinding.tvToValue.setText(day + "-" + month + "-" + year);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void prepareReservation(){

    }
}
