package com.infinitum.bookingqba.view.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.EmptyResultSetException;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.utils.CalendarProperties;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentCalendarBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.BlockDay;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.viewmodel.RentViewModel;

import org.reactivestreams.Publisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pl.rafman.scrollcalendar.adapter.ScrollCalendarAdapter;
import pl.rafman.scrollcalendar.adapter.example.DefaultRangeScrollCalendarAdapter;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;
import timber.log.Timber;

import static com.applandeo.materialcalendarview.CalendarView.RANGE_PICKER;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class CalendarFragment extends BaseNavigationFragment implements View.OnClickListener {

    private FragmentCalendarBinding calendarBinding;
    private RentViewModel rentViewModel;
    private Disposable disposable;
    private String rentUuidSelected = "";

    @Nullable
    private Calendar from;
    @Nullable
    private Calendar until;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private String rentNameSelected;
    private String[] rentOptionsNames;
    private String[] rentOptionsUuid;
    private String token;
    private String userid;
    private List<String> unavailableDays;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        return calendarBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calendarBinding.setLoading(true);

        setHasOptionsMenu(true);

        token = sharedPreferences.getString(USER_TOKEN, "");
        userid = sharedPreferences.getString(USER_ID, "");

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        setupCalendarView();

        fetchUserRents();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (rentOptionsNames != null && rentOptionsNames.length > 1) {
            menu.findItem(R.id.action_change_rent).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_block:
                Toast.makeText(getActivity(), "Hacer algo pa bloquear", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_change_rent:
                Toast.makeText(getActivity(), "Hacer algo pa cambiar de renta", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchUserRents() {
        disposable = rentViewModel.allRentByUserId(token, userid)
                .map(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        rentUuidSelected = listResource.data.get(0).getId();
                        prepareRentsOptions(listResource.data);
                        return listResource.data;
                    }
                    return new ArrayList<RentEsential>();
                })
                .flatMap((Function<List<RentEsential>, Publisher<Resource<DisabledDays>>>) objects -> {
                    if (objects != null && objects.size() > 0) {
                        return rentViewModel.disabledDays(token, objects.get(0).getId())
                                .subscribeOn(Schedulers.io()).toFlowable().onErrorReturn(Resource::error);
                    }
                    return Flowable.just(Resource.error("Datos nulos o vacios"));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null) {
                        calendarBinding.setLoading(false);
                        unavailableDays = getTestDateDisabled();
                        calendarBinding.scrollCalendar.getAdapter().notifyDataSetChanged();
                        if (rentOptionsNames != null && rentOptionsNames.length > 1) {
                            getActivity().invalidateOptionsMenu();
                        }
                    } else {
                        calendarBinding.setLoading(false);
                    }
                }, throwable -> {
                    calendarBinding.setLoading(false);
                });
        compositeDisposable.add(disposable);
    }

    private void getDisabledDayOfARent(String rentUuid) {
        disposable = rentViewModel.disabledDays(token, rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disabledDaysResource -> {
//                    if (disabledDaysResource.data != null && disabledDaysResource.status == Resource.Status.SUCCESS)
//                        calendarBinding.calendarView.setDisabledDays(DateUtils.transformStringDatesToCalendars(disabledDaysResource.data.getDates()));
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void prepareRentsOptions(List<RentEsential> data) {
        rentOptionsNames = new String[data.size()];
        rentOptionsUuid = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            rentOptionsUuid[i] = data.get(i).getId();
            rentOptionsNames[i] = data.get(i).getName();
        }
    }


    private ArrayList<String> getTestDateDisabled() {
        ArrayList<String> dates = new ArrayList<>();
        dates.add("2019-10-10");
        dates.add("2019-10-11");
        dates.add("2019-10-12");
        dates.add("2019-10-13");
        return dates;
    }

    private void setupCalendarView() {
        calendarBinding.scrollCalendar.setDateWatcher(this::doGetStateForDate);

        calendarBinding.scrollCalendar.setOnDateClickListener(this::doOnCalendarDayClicked);
        calendarBinding.scrollCalendar.setMonthScrollListener(new MonthScrollListener() {
            @Override
            public boolean shouldAddNextMonth(int lastDisplayedYear, int lastDisplayedMonth) {
                return doShouldAddNextMonth(lastDisplayedYear, lastDisplayedMonth);
            }

            @Override
            public boolean shouldAddPreviousMonth(int firstDisplayedYear, int firstDisplayedMonth) {
                return false;
            }
        });
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
            if(isUnavailableRange(clickedOn))
                return;
            until = clickedOn;
            updateUiRangeBar(from, until);
        }
    }

    private boolean isUnavailableRange(Calendar untilClicked){
        boolean isUnDay = false;
        if(from!=null){
            Calendar startValue = Calendar.getInstance();
            startValue.setTime(from.getTime());
            Calendar endValue = Calendar.getInstance();
            endValue.setTime(untilClicked.getTime());
            while (startValue.before(endValue)){
                startValue.add(Calendar.DATE,1);
                if(isDisableDay(startValue)){
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
            calendarBinding.from.setDate(start.getTime());
        } else {
            calendarBinding.from.setDate(null);
        }
        calendarBinding.to.setCta(start == null ? "" : getString(R.string.select_ending_date));
        if (end != null) {
            calendarBinding.to.setDate(end.getTime());
        } else {
            calendarBinding.to.setDate(null);
        }
    }

    private boolean isUnavailable(int year, int month, int day) {
        return isDisableDay(year, month, day);
    }

    private boolean isDisableDay(Calendar dayCalendar) {
        return isDisableDay(dayCalendar.get(Calendar.YEAR),dayCalendar.get(Calendar.MONTH),dayCalendar.get(Calendar.DATE));
    }

    private boolean isDisableDay(int year, int month, int day) {
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
        return false;
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_send:
//                if (com.applandeo.materialcalendarview.utils.DateUtils.isFullDatesRange(calendarBinding.calendarView.getSelectedDates())) {
//                    Calendar startDate = calendarBinding.calendarView.getSelectedDates().get(0);
//                    Calendar endDate = calendarBinding.calendarView.getSelectedDates().get(calendarBinding.calendarView.getSelectedDates().size() - 1);
//                    List<Calendar> startEndDates = new ArrayList<>();
//                    startEndDates.add(startDate);
//                    startEndDates.add(endDate);
//                    List<String> textDates = DateUtils.transformCalendarsToStringDates(startEndDates);
//                    BlockDay blockDay = new BlockDay(rentUuidSelected, "Futura descripcion", textDates.get(0), textDates.get(1));
//                    disposable = rentViewModel.blockDays(sharedPreferences.getString(USER_TOKEN, ""), blockDay)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(resultResource -> {
//                                if (resultResource.data != null && resultResource.data.getCode() == 200) {
//                                    AlertUtils.showSuccessToast(getActivity(), "Días bloqueados con éxito");
//                                    calendarBinding.calendarView.setDisabledDays(calendarBinding.calendarView.getSelectedDates());
//                                } else {
//                                    AlertUtils.showErrorToast(getActivity(), "Un error ha ocurrido");
//                                }
//                            }, Timber::e);
//                    compositeDisposable.add(disposable);
//                } else {
//                    AlertUtils.showErrorToast(getActivity(), "Seleccione un rango válido");
//                }
//                break;
//            case R.id.tv_rent:
//                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
//                builder.setTitle("Cambio de Renta");
//                builder.setItems(rentOptionsNames, (dialog, which) -> {
//                    dialog.dismiss();
//                    calendarBinding.tvRent.setText(rentOptionsNames[which]);
//                    getDisabledDayOfARent(rentOptionsUuid[which]);
//                    CalendarProperties calendarProperties = new CalendarProperties(getActivity());
//                    calendarProperties.se
//                    CalendarView calendarView = new CalendarView(getActivity());
//                });
//                builder.show();
//                break;
//        }
    }


}
