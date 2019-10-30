package com.infinitum.bookingqba.view.rents;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hsalf.smilerating.BaseRating;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.DialogCalendarBinding;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.customview.StateView;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class BookDaysDialog extends DialogFragment implements View.OnClickListener, MonthScrollListener {

    private DialogCalendarBinding dialogCalendarBinding;
    private BookDaysDialogInteraction interaction;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel rentViewModel;

    @Nullable
    private Calendar from;
    @Nullable
    private Calendar until;

    private List<String> unavailableDays;
    private String token;
    private String userid;
    private String rentUuid;
    private int capability;

    private static final String RENT_UUID = "rentUuid";
    private static final String RENT_CAPABILITY = "capability";

    public BookDaysDialog() {
        // Required empty public constructor
    }

    public static BookDaysDialog newInstance(String argRentUuid, int maxCapability) {
        BookDaysDialog bookDaysDialog = new BookDaysDialog();
        Bundle args = new Bundle();
        args.putString(RENT_UUID, argRentUuid);
        args.putInt(RENT_CAPABILITY, maxCapability);
        bookDaysDialog.setArguments(args);
        return bookDaysDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("Dialog =====> onCreate");
        if (getArguments() != null) {
            rentUuid = getArguments().getString(RENT_UUID);
            capability = getArguments().getInt(RENT_CAPABILITY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.e("Dialog =====> onCreateDialog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.e("Dialog =====> onCreateView");
        dialogCalendarBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_calendar, container, false);
        return dialogCalendarBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        token = sharedPreferences.getString(USER_TOKEN, "");
        userid = sharedPreferences.getString(USER_ID, "");

        Timber.e("Dialog =====> onActivityCreated");

        dialogCalendarBinding.setLoading(true);

        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupCalendarView();
        fetchDisabledDay();
    }

    private void fetchDisabledDay() {
        disposable = rentViewModel.disabledDays(token, rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disabledDaysResource -> {
                    if (disabledDaysResource.data != null) {
                        unavailableDays = disabledDaysResource.data.getDates();
                        dialogCalendarBinding.scrollCalendar.refresh();
                        dialogCalendarBinding.setLoading(false);
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    AlertUtils.showErrorToast(getActivity(), "Oops!! algo anda mal");
                });
        compositeDisposable.add(disposable);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
        Timber.e("Dialog =====> onStart");
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof BookDaysDialogInteraction) {
            interaction = (BookDaysDialogInteraction) context;
        }
    }

    @Override
    public void onDetach() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_done:
                if (interaction != null) {
                    if (from != null && until != null) {
                        SimpleDateFormat bookDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String start = bookDateFormat.format(from.getTime());
                        String end = bookDateFormat.format(until.getTime());
                        long nightCountInMillis = until.getTimeInMillis() - from.getTimeInMillis();
                        int nightCount = (int) TimeUnit.DAYS.convert(nightCountInMillis, TimeUnit.MILLISECONDS);
                        dismiss();
                        interaction.onRangeSelected(start, end, nightCount, capability);
                    } else {
                        AlertUtils.showErrorToast(getActivity(), "Seleccione un rango de fechas");
                    }
                }
                break;
            case R.id.iv_btn_back:
                dismiss();
                break;
        }
    }


    //------------------------------ LIFE CYCLE ---------------------

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDestroyView();
    }

    //------------------------------- Calendar Config ----------------------

    private void setupCalendarView() {
        dialogCalendarBinding.ivBtnDone.setOnClickListener(this);
        dialogCalendarBinding.ivBtnBack.setOnClickListener(this);
        dialogCalendarBinding.scrollCalendar.setDateWatcher(this::doGetStateForDate);
        dialogCalendarBinding.scrollCalendar.setOnDateClickListener(this::doOnCalendarDayClicked);
        dialogCalendarBinding.scrollCalendar.setMonthScrollListener(this);
    }

    @Override
    public boolean shouldAddNextMonth(int lastDisplayedYear, int lastDisplayedMonth) {
        return doShouldAddNextMonth(lastDisplayedYear, lastDisplayedMonth);
    }

    @Override
    public boolean shouldAddPreviousMonth(int firstDisplayedYear, int firstDisplayedMonth) {
        return false;
    }

    @State
    private int doGetStateForDate(int year, int month, int day) {
        if (isInThePast(year, month, day)) {
            return CalendarDay.DISABLED;
        }
        if (isUnavailable(year, month, day)) {
            return CalendarDay.UNAVAILABLE;
        }
        if (isInRange(from, until, year, month, day)) {
            if (until == null) {
                return CalendarDay.ONLY_SELECTED;
            }
            if (isSelected(from, year, month, day)) {
                return CalendarDay.FIRST_SELECTED;
            } else if (isSelected(until, year, month, day)) {
                return CalendarDay.LAST_SELECTED;
            } else {
                return CalendarDay.SELECTED;
            }
        }
        if (isToday(year, month, day)) {
            return CalendarDay.TODAY;
        }
        return CalendarDay.DEFAULT;
    }

    private boolean isSelected(Calendar selected, int year, int month, int day) {
        if (selected == null) {
            return false;
        }
        //noinspection UnnecessaryLocalVariable
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selected.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, selected.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, selected.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long millis2 = calendar.getTimeInMillis();

        return millis == millis2;
    }

    private boolean isInRange(Calendar from, Calendar until, int year, int month, int day) {
        if (from == null || until == null) {
            return from != null && isSelected(from, year, month, day);
        }
        //noinspection UnnecessaryLocalVariable
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, from.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, from.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, from.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis1 = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, until.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, until.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, until.get(Calendar.DAY_OF_MONTH));
        long millis2 = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long millis3 = calendar.getTimeInMillis();
        return millis1 <= millis3 && millis2 >= millis3;
    }

    private void doOnCalendarDayClicked(int year, int month, int day) {
        if (isInThePast(year, month, day))
            return;
        if (isUnavailable(year, month, day))
            return;

        Calendar clickedOn = Calendar.getInstance();
        clickedOn.set(Calendar.YEAR, year);
        clickedOn.set(Calendar.MONTH, month);
        clickedOn.set(Calendar.DAY_OF_MONTH, day);
        clickedOn.set(Calendar.HOUR_OF_DAY, 0);
        clickedOn.set(Calendar.MINUTE, 0);
        clickedOn.set(Calendar.SECOND, 0);
        clickedOn.set(Calendar.MILLISECOND, 0);

        if (shouldClearAllSelected(clickedOn)) {
            updateUiRangeBar(null, null);
            from = null;
            until = null;
        } else if (shouldSetFrom(clickedOn)) {
            from = clickedOn;
            updateUiRangeBar(from, null);
            until = null;
        } else if (shouldSetUntil()) {
            if (isUnavailableRange(clickedOn))
                return;
            until = clickedOn;
            updateUiRangeBar(from, until);
        }
    }

    private boolean isUnavailableRange(Calendar untilClicked) {
        boolean isUnDay = false;
        if (from != null) {
            Calendar startValue = Calendar.getInstance();
            startValue.setTime(from.getTime());
            Calendar endValue = Calendar.getInstance();
            endValue.setTime(untilClicked.getTime());
            while (startValue.before(endValue)) {
                startValue.add(Calendar.DATE, 1);
                if (isDisableDay(startValue)) {
                    isUnDay = true;
                    break;
                }
            }
        }
        return isUnDay;
    }

    private boolean shouldSetUntil() {
        return until == null;
    }

    private boolean shouldClearAllSelected(Calendar calendar) {
        return from != null && from.equals(calendar);
    }

    private boolean shouldSetFrom(Calendar calendar) {
        return from == null || until != null || isBefore(from, calendar);
    }

    private boolean isBefore(Calendar c1, Calendar c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        //noinspection UnnecessaryLocalVariable
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, c2.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, c2.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH));
        long millis2 = calendar.getTimeInMillis();

        return millis < millis2;
    }

    private boolean doShouldAddNextMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long target = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        return calendar.getTimeInMillis() < target;
    }

    private boolean isInThePast(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long then = calendar.getTimeInMillis();
        return now > then;
    }

    private boolean isToday(int year, int month, int day) {
        //noinspection UnnecessaryLocalVariable
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Today in milliseconds
        long today = calendar.getTime().getTime();

        // Given day in milliseconds
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long calendarMillis = calendar.getTime().getTime();

        return today == calendarMillis;
    }

    private void updateUiRangeBar(Calendar start, Calendar end) {
        if (start != null) {
            dialogCalendarBinding.from.setDate(start.getTime());
        } else {
            dialogCalendarBinding.from.setDate(null);
        }
        dialogCalendarBinding.to.setCta(start == null ? "" : getString(R.string.select_ending_date));
        if (end != null) {
            dialogCalendarBinding.to.setDate(end.getTime());
        } else {
            dialogCalendarBinding.to.setDate(null);
        }
    }

    private boolean isUnavailable(int year, int month, int day) {
        return isDisableDay(year, month, day);
    }

    private boolean isDisableDay(Calendar dayCalendar) {
        return isDisableDay(dayCalendar.get(Calendar.YEAR), dayCalendar.get(Calendar.MONTH), dayCalendar.get(Calendar.DATE));
    }

    private boolean isDisableDay(int year, int month, int day) {
        if (unavailableDays != null && unavailableDays.size() > 0) {
            String textDate = String.valueOf(year) + "-" + (month + 1) + "-" + day;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date extraDate = null;
            try {
                extraDate = simpleDateFormat.parse(textDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (String dateStr : unavailableDays) {
                Date tempDate = null;
                try {
                    tempDate = simpleDateFormat.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (tempDate != null && extraDate != null && tempDate.getTime() == extraDate.getTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface BookDaysDialogInteraction {
        void onRangeSelected(String startDate, String endDate, int nightCount, int capability);
    }
}
