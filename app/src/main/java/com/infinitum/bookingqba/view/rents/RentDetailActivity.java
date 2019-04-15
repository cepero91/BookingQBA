package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailItem;
import com.infinitum.bookingqba.view.adapters.rentdetail.InnerViewPagerAdapter;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RentDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

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

}
