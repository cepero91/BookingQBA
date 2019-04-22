package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
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

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);
        rentDetailBinding.llContentAddress.setOnClickListener(this);

        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null)
            rentUuid = getIntent().getExtras().getString("uuid");

        imageViewHeight = getResources().getDimensionPixelSize(R.dimen.rent_detail_img_dimen);

        rentDetailBinding.nested.setScrollViewCallbacks(this);

        rentDetailBinding.setIsLoading(true);

        setupToolbar();

        setupScalingLayout();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        loadRentDetail();

        if (rentUuid.length() > 1)
            addOrUpdateRentVisitCount();

    }

    private void addOrUpdateRentVisitCount() {
        disposable = viewModel.addOrUpdateRentVisitCount(UUID.randomUUID().toString(), rentUuid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.e("Operacion exitosa");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void loadRentDetail() {
        disposable = viewModel.getRentDetailById(rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, Timber::e);
        compositeDisposable.add(disposable);
    }


    private void setupScalingLayout() {
        rentDetailBinding.scalingLayout.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                ViewCompat.animate(rentDetailBinding.fabIcon).alpha(1).setDuration(150).start();
                ViewCompat.animate(rentDetailBinding.filterLayout).alpha(0).setDuration(150).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        rentDetailBinding.fabIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        rentDetailBinding.filterLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
            }

            @Override
            public void onExpanded() {
                ViewCompat.animate(rentDetailBinding.fabIcon).alpha(0).setDuration(200).start();
                ViewCompat.animate(rentDetailBinding.filterLayout).alpha(1).setDuration(200).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        rentDetailBinding.filterLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        rentDetailBinding.fabIcon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
            }

            @Override
            public void onProgress(float progress) {
                if (progress > 0) {
                    rentDetailBinding.fabIcon.setVisibility(View.INVISIBLE);
                }

                if (progress < 1) {
                    rentDetailBinding.filterLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        rentDetailBinding.scalingLayout.setOnClickListener(view -> {
            if (rentDetailBinding.scalingLayout.getState() == State.COLLAPSED) {
                rentDetailBinding.scalingLayout.expand();
            }
        });


        rentDetailBinding.ivClose.setOnClickListener(view -> {
            if (rentDetailBinding.scalingLayout.getState() == State.EXPANDED) {
                rentDetailBinding.scalingLayout.collapse();
            }
        });
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
        if (scrollState != null) {
            if (scrollState.name().equals("UP")) {
                hideFabOrBar();
            } else if (scrollState.name().equals("DOWN")) {
                showFabOrBar();
            }
        }
    }

    private void hideFabOrBar() {
        if (rentDetailBinding.scalingLayout.getState() == State.COLLAPSED) {
            ViewCompat.animate(rentDetailBinding.scalingLayout)
                    .scaleX(0f).scaleY(0f)
                    .alpha(0f).setDuration(100)
                    .start();
        } else if (rentDetailBinding.scalingLayout.getState() == State.EXPANDED) {
            ViewCompat.animate(rentDetailBinding.scalingLayout)
                    .translationY(150)
                    .setDuration(100)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
    }

    private void showFabOrBar() {
        if (rentDetailBinding.scalingLayout.getState() == State.COLLAPSED) {
            ViewCompat.animate(rentDetailBinding.scalingLayout)
                    .scaleX(1f).scaleY(1f)
                    .alpha(1f).setDuration(200)
                    .start();
        } else if (rentDetailBinding.scalingLayout.getState() == State.EXPANDED) {
            ViewCompat.animate(rentDetailBinding.scalingLayout)
                    .translationY(0)
                    .setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
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
            GeoRent geoRent = new GeoRent(entity.getId());
            geoRent.setName(entity.getName());
            geoRent.setRentMode(rentDetailBinding.getRentDetailItem().getRentModeName());
            geoRent.setRating(entity.getRating());
            geoRent.setGeoPoint(new GeoPoint(entity.getLatitude(), entity.getLongitude()));
            geoRent.setImagePath(rentDetailBinding.getRentDetailItem().getFirstImage());
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
        if (rentDetailBinding.getRentDetailItem().getRentEntity().isWished() == 1) {
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
        if (rentDetailBinding.getRentDetailItem().getRentEntity().isWished() == 1) {
            rentDetailBinding.getRentDetailItem().getRentEntity().setWished(0);
            updateEntity(rentDetailBinding.getRentDetailItem().getRentEntity());
        } else if(rentDetailBinding.getRentDetailItem().getRentEntity().isWished() == 0) {
            rentDetailBinding.getRentDetailItem().getRentEntity().setWished(1);
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
