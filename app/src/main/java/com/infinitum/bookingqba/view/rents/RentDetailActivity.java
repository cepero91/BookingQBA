package com.infinitum.bookingqba.view.rents;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.customview.SuccessDialogContentView;
import com.infinitum.bookingqba.view.galery.GaleryActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.profile.UserAuthActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;


import org.oscim.core.GeoPoint;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.util.Constants.ALTERNATIVE_SYNC;
import static com.infinitum.bookingqba.util.Constants.DATE_RANGE;
import static com.infinitum.bookingqba.util.Constants.END_DATE;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.IS_PROFILE_ACTIVE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_TAG;
import static com.infinitum.bookingqba.util.Constants.MAXCAPABILITY;
import static com.infinitum.bookingqba.util.Constants.NAV_HEADER_REQUIRED_UPDATE;
import static com.infinitum.bookingqba.util.Constants.NIGHT_COUNT;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_ID;
import static com.infinitum.bookingqba.util.Constants.PRICE;
import static com.infinitum.bookingqba.util.Constants.RENT_ID;
import static com.infinitum.bookingqba.util.Constants.START_DATE;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;


public class RentDetailActivity extends DaggerAppCompatActivity implements View.OnClickListener ,HasSupportFragmentInjector, InnerDetailInteraction {

    private ActivityRentDetailBinding rentDetailBinding;
    private InnerViewPagerAdapter innerViewPagerAdapter;
    private String deviceID = "";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel viewModel;
    private RentItem rentItem;
    private String rentUuid = "";
    private String rentName = "";

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    Fragment innerDetail;
    Fragment commentDetail;
    Fragment offerDetail;
    Fragment poiDetail;
    private final static int REQUEST_PHONE_CALL = 1044;
    private final static int REQUEST_PHONE_SMS = 1045;
    String userId;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);

        deviceID = sharedPreferences.getString(IMEI, "");
        userId = sharedPreferences.getString(USER_ID, "");
        token = sharedPreferences.getString(USER_TOKEN, "");
        rentDetailBinding.setIsLoading(true);
        rentDetailBinding.setHasTab(false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null) {
            rentUuid = getIntent().getExtras().getString("uuid");
            rentName = getIntent().getExtras().getString("name");
            rentDetailBinding.tvRentName.setText(rentName);
            String imageUrlPath = "file:" + getIntent().getExtras().getString("url");
            Picasso.get().load(imageUrlPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(rentDetailBinding.ivRent);
        }

        if (rentUuid.length() > 1) {
            addOrUpdateRentVisitCount(rentUuid);
            loadRentDetail();
        }
        setupToolbar();
    }

    //------------------------------------ LIVECYCLE -------------------------------------//

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    //------------------------------------------------------------------------------------

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    private void addOrUpdateRentVisitCount(String uuid) {
        disposable = viewModel.addOrUpdateRentVisitCount(UUID.randomUUID().toString(), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Timber.e("Exito contador de visitas"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void loadRentDetail() {
        disposable = viewModel.getRentDetailById(rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, Timber::e);
        compositeDisposable.add(disposable);
    }


    private void onSuccess(Resource<RentItem> rentDetailResource) {
        rentDetailBinding.lvLocation.setOnClickListener(this);
        rentItem = rentDetailResource.data;
        setupViewPager(rentDetailResource.data);
        rentDetailBinding.tvVotes.setText(rentDetailResource.data.getRentInnerDetail().humanVotes());
        rentDetailBinding.srScaleRating.setRating(rentDetailResource.data.getRentInnerDetail().getRating());
    }

    private void setupToolbar() {
        setSupportActionBar(rentDetailBinding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager(RentItem rentItem) {
        setupViewPagerWithNav(getFragmentList(rentItem));
    }

    @NonNull
    private Pair<List<Fragment>, List<String>> getFragmentList(RentItem rentItem) {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        innerDetail = InnerDetailFragment.newInstance(rentItem.getRentInnerDetail());
        fragments.add(innerDetail);
        titles.add("Detalles");
        if (rentItem.getRentInnerDetail().getPoiItemMap().size() > 0) {
            poiDetail = RentDetailPoiFragment.newInstance(rentItem.getRentInnerDetail().getPoiItemMap(),
                    rentItem.getRentInnerDetail().getReferenceZone());
            fragments.add(poiDetail);
            titles.add("Donde ir");
        }
        if (rentItem.getCommentItems().size() > 0) {
            commentDetail = RentCommentFragment.newInstance(rentItem.getCommentItems());
            fragments.add(commentDetail);
            titles.add("Comentarios");
        }
        if (rentItem.getOfferItems().size() > 0) {
            offerDetail = RentOfferFragment.newInstance(rentItem.getOfferItems());
            fragments.add(offerDetail);
            titles.add("Ofertas");
        }
        rentDetailBinding.setHasTab(fragments.size() > 1);
        return new Pair<>(fragments, titles);
    }

    private void setupViewPagerWithNav(Pair<List<Fragment>, List<String>> pair) {
        rentDetailBinding.setIsLoading(false);
        innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager(), pair.first, pair.second);
        rentDetailBinding.viewpager.setAdapter(innerViewPagerAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(rentDetailBinding.viewpager);
        rentDetailBinding.tlTab.setViewPager(rentDetailBinding.viewpager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rent_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                showFragmentMenuDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragmentMenuDialog() {
        int wished = rentItem.getRentInnerDetail().getWished();
        String userId = sharedPreferences.getBoolean(USER_IS_AUTH, false) ? sharedPreferences.getString(USER_ID, "") : "";
        DialogDetailMenu dialogDetailMenu = DialogDetailMenu.newInstance(wished, userId, rentUuid);
        dialogDetailMenu.show(getSupportFragmentManager(), "dialogMenu");
    }

    @Override
    public void onGaleryClick(String id) {
        Intent intent = new Intent(this, GaleryActivity.class);
        intent.putExtra("id", id);
        intent.putParcelableArrayListExtra("imageList", rentItem.getRentInnerDetail().getGalerieItems());
        startActivity(intent);
    }

    @Override
    public void onAddressClick(ArrayList<GeoRent> geoRents) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putParcelableArrayListExtra("geoRents", geoRents);
        setResult(FROM_DETAIL_TO_MAP, intent);
        this.finish();
    }

    @Override
    public void phoneCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        if (ContextCompat.checkSelfPermission(RentDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RentDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void phoneSMSClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
        if (ContextCompat.checkSelfPermission(RentDetailActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RentDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PHONE_SMS);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void phoneHomeClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        if (ContextCompat.checkSelfPermission(RentDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RentDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PHONE_CALL);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void phoneEmailClick(String email) {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + email);
        mailIntent.setData(data);
        startActivity(Intent.createChooser(mailIntent, "Send mail..."));
    }

    @Override
    public void onBookRequestClick() {
        disposable = viewModel.disabledDays(token,rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disabledDaysResource -> {
                    if(disabledDaysResource.data!= null){
                        showDatesRangeDialog(DateUtils.transformStringDatesToCalendars(disabledDaysResource.data.getDates()));
                    }
                },Timber::e);
        compositeDisposable.add(disposable);
    }

    private void showDatesRangeDialog(List<Calendar> calendarList) {
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, -1);
        DatePickerBuilder oneDayBuilder = new DatePickerBuilder(this, this::checkValidRange)
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
                .disabledDays(calendarList != null ? calendarList : new ArrayList<>());
        oneDayBuilder.build().show();
    }

    private void checkValidRange(List<Calendar> calendarList) {
        if(com.applandeo.materialcalendarview.utils.DateUtils.isFullDatesRange(calendarList)){
            Intent intent = new Intent(this, ReservationActivity.class);
            intent.putExtra(PRICE, rentItem.getRentInnerDetail().getPrice());
            intent.putExtra(RENT_ID, rentUuid);
            intent.putExtra(MAXCAPABILITY, rentItem.getRentInnerDetail().getCapability());
            intent.putExtra(NIGHT_COUNT,calendarList.size()-1);
            Calendar startSelectedDay = calendarList.get(0);
            Calendar endSelectedDay = calendarList.get(calendarList.size() - 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy",Locale.getDefault());
            String start = simpleDateFormat.format(startSelectedDay.getTime());
            String end = simpleDateFormat.format(endSelectedDay.getTime());
            intent.putExtra(DATE_RANGE,String.format("%s / %s",start,end));
            SimpleDateFormat bookDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            intent.putExtra(START_DATE,bookDateFormat.format(startSelectedDay.getTime()));
            intent.putExtra(END_DATE,bookDateFormat.format(endSelectedDay.getTime()));
            startActivity(intent);
        }else{
            AlertUtils.showErrorToast(this,"Seleccione un rango válido");
        }
    }


    @Override
    public void onDrawChangeClick() {
        disposable = viewModel.drawChangeByFinalPrice(sharedPreferences.getString(USER_TOKEN, "")
                , rentItem.getRentInnerDetail().getPrice())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapResource -> {
                    StringBuilder builder = new StringBuilder();
                    int i = 0;
                    for (Map.Entry<String, Double> entry : mapResource.data.entrySet()) {
                        String item = String.format("%s ~ %.2f", entry.getKey(), entry.getValue());
                        builder.append(item);
                        if (i < mapResource.data.size()-1) {
                            builder.append("\n");
                        }
                        i++;
                    }
                    CFAlertDialog.Builder dialogBuilder = new CFAlertDialog.Builder(this);
                    dialogBuilder.setTitle("Cambio de Moneda");
                    dialogBuilder.setMessage(builder.toString());
                    dialogBuilder.setTextGravity(CENTER);
                    dialogBuilder.setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                    dialogBuilder.show();
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lv_location:
                RentInnerDetail innerDetail = rentItem.getRentInnerDetail();
                GeoRent geoRent = new GeoRent(innerDetail.getId(),innerDetail.getName(),innerDetail.firstImage());
                geoRent.setRating(innerDetail.getRating());
                geoRent.setRatingCount(innerDetail.getVotes());
                geoRent.setAddress(innerDetail.getAddress());
                geoRent.setPrice(innerDetail.getPrice());
                geoRent.setGeoPoint(new GeoPoint(innerDetail.getLatitude(),innerDetail.getLongitude()));
                geoRent.setReferenceZone(innerDetail.getReferenceZone());
                geoRent.setRentMode(innerDetail.getRentMode());
                geoRent.setWished(innerDetail.getWished());
                geoRent.setPoiItemMap(innerDetail.getPoiItemMap());
                ArrayList<GeoRent> geoRents = new ArrayList<>();
                geoRents.add(geoRent);
                onAddressClick(geoRents);
                break;
        }
    }
}



