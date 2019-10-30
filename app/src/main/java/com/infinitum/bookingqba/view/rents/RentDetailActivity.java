package com.infinitum.bookingqba.view.rents;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.galery.GaleryActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.DATE_RANGE;
import static com.infinitum.bookingqba.util.Constants.END_DATE;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.MAXCAPABILITY;
import static com.infinitum.bookingqba.util.Constants.NIGHT_COUNT;
import static com.infinitum.bookingqba.util.Constants.PRICE;
import static com.infinitum.bookingqba.util.Constants.RENT_ID;
import static com.infinitum.bookingqba.util.Constants.START_DATE;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;


public class RentDetailActivity extends DaggerAppCompatActivity implements View.OnClickListener,
        HasSupportFragmentInjector, InnerDetailInteraction, BookDaysDialog.BookDaysDialogInteraction {

    private ActivityRentDetailBinding rentDetailBinding;
    private InnerViewPagerAdapter innerViewPagerAdapter;
    private String deviceID = "";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NetworkHelper networkHelper;

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
    private int wishedValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);
        rentUuid = getIntent().getExtras().getString("uuid");
        rentDetailBinding.ivRent.setTransitionName(rentUuid);
        supportPostponeEnterTransition();
        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(rentDetailBinding.ivRent,"radius",0);
                objectAnimator.setDuration(250);
                objectAnimator.start();
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

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
//            Picasso.get().load(imageUrlPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(rentDetailBinding.ivRent);
            Picasso.get()
                    .load(imageUrlPath)
                    .resize(Constants.THUMB_WIDTH, Constants.THUMB_HEIGHT)
                    .into(rentDetailBinding.ivRent, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {
                            supportStartPostponedEnterTransition();
                        }
                    });
        }

//        if (rentUuid.length() > 1) {
//            addOrUpdateRentVisitCount(rentUuid);
//            rentIsWished(rentUuid, userId);
//            loadRentDetail();
//        }
        setupToolbar();
    }

    private void rentIsWished(String rentUuid, String userId) {
        disposable = viewModel.rentIsWished(rentUuid, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integerResource -> {
                    wishedValue = integerResource.data != null ? integerResource.data : 0;
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    //------------------------------------ LIVECYCLE -------------------------------------//

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
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
            String latLon = rentItem.getRentInnerDetail().getLatitude() + " " + rentItem.getRentInnerDetail().getLongitude();
            poiDetail = RentDetailPoiFragment.newInstance(rentItem.getRentInnerDetail().getPoiItemMap(),
                    rentItem.getRentInnerDetail().getReferenceZone(), latLon);
            fragments.add(poiDetail);
            titles.add("Donde ir");
        }
        if (rentItem.getCommentItems().size() > 0) {
//            commentDetail = RentCommentFragment.newInstance(rentItem.getCommentItems());
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
//        String userId = sharedPreferences.getBoolean(USER_IS_AUTH, false) ? sharedPreferences.getString(USER_ID, "") : "";
//        RatingDialog ratingDialog = RatingDialog.newInstance(wishedValue, userId, rentUuid);
//        ratingDialog.show(getSupportFragmentManager(), "dialogMenu");
    }

    @Override
    public void onGaleryClick(String id, ArrayList<RentGalerieItem> rentGalerieItems) {
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
    public void onCommentClick(String rentUuid, String rentOwnerId, ArrayList<RentCommentItem> argComment) {

    }

    @Override
    public void onOfferClick(ArrayList<RentOfferItem> argOffer) {

    }

    @Override
    public void updateWishedValue(int wished) {

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
    public void onBookRequestClick(int capability) {
        BookDaysDialog bookDaysDialog = BookDaysDialog.newInstance(rentUuid, capability);
        bookDaysDialog.show(getSupportFragmentManager(), "calendarDialog");
    }

    @Override
    public void onVoteClick(String userOwner) {

    }


    @Override
    public void onDrawChangeClick() {
        if (networkHelper.isNetworkAvailable()) {
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
                            if (i < mapResource.data.size() - 1) {
                                builder.append("\n");
                            }
                            i++;
                        }
                        AlertUtils.showCFInfo(this, "Cambio de Moneda",
                                builder.toString(), getResources().getColor(R.color.material_color_blue_grey_500));
                    }, Timber::e);
            compositeDisposable.add(disposable);
        } else {
            AlertUtils.showCFInfo(this, "Oops!!",
                    Constants.CONNEXION_ERROR_MSG, getResources().getColor(R.color.material_color_red_500));
        }
    }

    @Override
    public void onPoiPlacesClick(String rentUuid, String referenceZone, String latLon) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_location:
                RentInnerDetail innerDetail = rentItem.getRentInnerDetail();
                GeoRent geoRent = new GeoRent(innerDetail.getId(), innerDetail.getName(), innerDetail.firstImage(), (float) innerDetail.getPrice(), innerDetail.getRentMode());
                geoRent.setRating(innerDetail.getRating());
                geoRent.setRatingCount(innerDetail.getVotes());
                geoRent.setAddress(innerDetail.getAddress());
                geoRent.setGeoPoint(new GeoPoint(innerDetail.getLatitude(), innerDetail.getLongitude()));
                geoRent.setReferenceZone(innerDetail.getReferenceZone());
                geoRent.setPoiItemMap(innerDetail.getPoiItemMap());
                ArrayList<GeoRent> geoRents = new ArrayList<>();
                geoRents.add(geoRent);
                onAddressClick(geoRents);
                break;
        }
    }

    @Override
    public void onRangeSelected(String startDate, String endDate, int nightCount, int capability) {
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra(PRICE, rentItem.getRentInnerDetail().getPrice());
        intent.putExtra(RENT_ID, rentUuid);
        intent.putExtra(MAXCAPABILITY, rentItem.getRentInnerDetail().getCapability());
        intent.putExtra(NIGHT_COUNT, nightCount);
        intent.putExtra(DATE_RANGE, String.format("%s / %s", startDate, endDate));
        intent.putExtra(START_DATE, startDate);
        intent.putExtra(END_DATE, endDate);
        startActivity(intent);
    }
}



