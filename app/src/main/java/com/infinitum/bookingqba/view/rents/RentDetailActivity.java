package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailItem;
import com.infinitum.bookingqba.view.adapters.rentdetail.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RentDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks,
        View.OnClickListener {

    private ActivityRentDetailBinding rentDetailBinding;
    private int imageViewHeight;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel viewModel;
    private String rentUuid = "";
    private String imageUrlPath = "";
    private int isWished = 0;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        rentDetailBinding.setIsLoading(true);
        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null) {
            rentUuid = getIntent().getExtras().getString("uuid");
            imageUrlPath = getIntent().getExtras().getString("url");
            isWished = getIntent().getExtras().getInt("wished");
            GlideApp.with(this).load(imageUrlPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(rentDetailBinding.ivPortraitDetail);
        }

        if (rentUuid.length() > 1) {
            loadRentDetail();
        }

        //Configurations
        rentDetailBinding.llContentAddress.setOnClickListener(this);
        imageViewHeight = getResources().getDimensionPixelSize(R.dimen.rent_detail_img_dimen);
        rentDetailBinding.nested.setScrollViewCallbacks(this);
        setupToolbar();
    }

    private void addOrUpdateRentVisitCount(String uuid) {
        disposable = viewModel.addOrUpdateRentVisitCount(UUID.randomUUID().toString(), uuid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->Timber.e("Exito contador de visitas"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void loadRentDetail() {
        disposable = viewModel.getRentDetailById(rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::onSuccess)
                .doAfterNext(rentDetailItemResource -> {
                    if(rentDetailItemResource.data!=null) {
                        addOrUpdateRentVisitCount(rentDetailItemResource.data.getRentEntity().getId());
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }


    private void onSuccess(Resource<RentDetailItem> rentDetailResource) {
        rentDetailBinding.setRentDetailItem(rentDetailResource.data);
        setupViewPager(rentDetailResource.data);
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
        onScrollChanged(rentDetailBinding.nested.getCurrentScrollY(), false, false);
    }

    private void setupViewPager(RentDetailItem rentDetailItem) {
        InnerViewPagerAdapter innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager());
        Fragment innerDetail = InnerDetailFragment.newInstance(rentDetailItem.getRentEntity().getDescription(), rentDetailItem.getPoiItems(), rentDetailItem.getAmenitieItems(), rentDetailItem.getGalerieItems());
        Fragment innerRules = InnerRulesFragment.newInstance(rentDetailItem.getRentEntity().getRules());
        innerViewPagerAdapter.addFragment(innerDetail, "Detalles");
        innerViewPagerAdapter.addFragment(innerRules, "Reglas");
        rentDetailBinding.vpPages.setAdapter(innerViewPagerAdapter);
        rentDetailBinding.tlTab.setViewPager(rentDetailBinding.vpPages);
        rentDetailBinding.executePendingBindings();
        rentDetailBinding.vpPages.postDelayed(() -> rentDetailBinding.setIsLoading(false), 100);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / imageViewHeight);
        rentDetailBinding.toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    public void showMapBack(ArrayList<GeoRent> geoRentList) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putParcelableArrayListExtra("geoRents", geoRentList);
        setResult(6, intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_content_address) {
            RentEntity entity = rentDetailBinding.getRentDetailItem().getRentEntity();
            GeoRent geoRent = new GeoRent(entity.getId(),entity.getName(),rentDetailBinding.getRentDetailItem().getFirstImage(),entity.getIsWished());
            geoRent.setRentMode(rentDetailBinding.getRentDetailItem().getRentModeName());
            geoRent.setRating(entity.getRating());
            geoRent.setGeoPoint(new GeoPoint(entity.getLatitude(), entity.getLongitude()));
            geoRent.setPrice(entity.getPrice());
            ArrayList<GeoRent> rents = new ArrayList<>();
            rents.add(geoRent);
            showMapBack(rents);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rent_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        checkIsWished(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkIsWished(Menu menu) {
        if (isWished == 1) {
            menu.findItem(R.id.action_list_wish).setIcon(R.drawable.ic_bookmark_orange);
        } else {
            menu.findItem(R.id.action_list_wish).setIcon(R.drawable.ic_bookmark_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_wish:
                setIsWished();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setIsWished() {
        if (rentDetailBinding.getRentDetailItem().getRentEntity().getIsWished() == 1) {
            rentDetailBinding.getRentDetailItem().getRentEntity().setIsWished(0);
            updateEntity(rentDetailBinding.getRentDetailItem().getRentEntity());
        } else if(rentDetailBinding.getRentDetailItem().getRentEntity().getIsWished() == 0) {
            rentDetailBinding.getRentDetailItem().getRentEntity().setIsWished(1);
            updateEntity(rentDetailBinding.getRentDetailItem().getRentEntity());
        }
    }

    private void updateEntity(RentEntity entity){
        disposable = viewModel.updateRent(entity).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->showshow(),Timber::e);
        compositeDisposable.add(disposable);
    }

    private void showshow(){
        Timber.e("Si GUARDO CON EXITO");
        invalidateOptionsMenu();
    }

}
