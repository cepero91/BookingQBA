package com.infinitum.bookingqba.view.rents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.PopupMenu;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentDetailNumber;
import com.infinitum.bookingqba.view.adapters.rentdetail.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.ogaclejapan.smarttablayout.SmartTabLayout;


import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;

public class RentDetailActivity extends AppCompatActivity implements NestedScrollView.OnScrollChangeListener,
        View.OnClickListener, ViewPager.OnPageChangeListener {

    private ActivityRentDetailBinding rentDetailBinding;
    private int imageViewHeight;
    private InnerViewPagerAdapter innerViewPagerAdapter;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel viewModel;
    private String rentUuid = "";
    private int isWished = 0;
    private int mirrorWished = 0;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private boolean isCommentActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        rentDetailBinding.setIsLoading(true);
        rentDetailBinding.fabComment.hide();
        rentDetailBinding.fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogComment lf = DialogComment.newInstance();
                lf.show(getSupportFragmentManager(), "CommentDialog");
            }
        });
        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null) {
            rentUuid = getIntent().getExtras().getString("uuid");
            String imageUrlPath = getIntent().getExtras().getString("url");
            isWished = getIntent().getExtras().getInt("wished");
            mirrorWished = isWished;
            GlideApp.with(this).load(imageUrlPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(rentDetailBinding.ivPortraitDetail);
        }

        if (rentUuid.length() > 1) {
            loadRentDetail();
        }

        //Configurations
        rentDetailBinding.llContentAddress.setOnClickListener(this);
        imageViewHeight = getResources().getDimensionPixelSize(R.dimen.rent_detail_img_dimen);
        rentDetailBinding.nested.setOnScrollChangeListener(this);
        setupToolbar();
    }

    private void addOrUpdateRentVisitCount(String uuid) {
        disposable = viewModel.addOrUpdateRentVisitCount(UUID.randomUUID().toString(), uuid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Timber.e("Exito contador de visitas"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void loadRentDetail() {
        disposable = viewModel.getRentDetailById(rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::onSuccess)
                .doAfterNext(rentDetailItemResource -> {
                    if (rentDetailItemResource.data != null) {
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
    }

    private void setupViewPager(RentDetailItem rentDetailItem) {
        RentEntity entity = rentDetailItem.getRentEntity();
        innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager());
        RentDetailNumber rentDetailNumber = new RentDetailNumber(entity.getMaxBeds(), entity.getMaxBath(), entity.getCapability(), entity.getMaxRooms());
        Fragment innerDetail = InnerDetailFragment.newInstance(entity.getDescription(), entity.getRules(), rentDetailNumber, rentDetailItem.getPoiItems(), rentDetailItem.getAmenitieItems(), rentDetailItem.getGalerieItems());
        innerViewPagerAdapter.addFragment(innerDetail, "Detalles");
        if (rentDetailItem.getCommentItems().size() > 0) {
            Fragment innerComment = RentCommentFragment.newInstance(rentDetailItem.getCommentItems());
            innerViewPagerAdapter.addFragment(innerComment, "Comentarios");
        }
        rentDetailBinding.vpPages.setAdapter(innerViewPagerAdapter);
        rentDetailBinding.tlTab.setViewPager(rentDetailBinding.vpPages);
        rentDetailBinding.tlTab.setOnPageChangeListener(this);
        rentDetailBinding.vpPages.postDelayed(() -> rentDetailBinding.setIsLoading(false), 500);
        rentDetailBinding.executePendingBindings();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    public void showMapBack(ArrayList<GeoRent> geoRentList) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putParcelableArrayListExtra("geoRents", geoRentList);
        setResult(FROM_DETAIL_TO_MAP, intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_content_address) {
            RentEntity entity = rentDetailBinding.getRentDetailItem().getRentEntity();
            GeoRent geoRent = new GeoRent(entity.getId(), entity.getName(), rentDetailBinding.getRentDetailItem().getFirstImage(), entity.getIsWished());
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
        } else if (isWished == 0) {
            menu.findItem(R.id.action_list_wish).setIcon(R.drawable.ic_bookmark_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_wish:
                setIsWished();
                return true;
            case R.id.action_comment:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setIsWished() {
        if (isWished == 1) {
            isWished = 0;
            rentDetailBinding.getRentDetailItem().getRentEntity().setIsWished(0);
            updateEntity(rentDetailBinding.getRentDetailItem().getRentEntity());
        } else if (isWished == 0) {
            isWished = 1;
            rentDetailBinding.getRentDetailItem().getRentEntity().setIsWished(1);
            updateEntity(rentDetailBinding.getRentDetailItem().getRentEntity());
        }
    }

    private void updateEntity(RentEntity entity) {
        disposable = viewModel.updateRent(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::invalidateOptionsMenu, Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    public void onBackPressed() {
        if (isWished == mirrorWished) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("refresh", true);
            setResult(FROM_DETAIL_REFRESH, intent);
            this.finish();
        }
    }

    @Override
    public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
        changeToolbarBackground(i1);
        if(isCommentActive) {
            if (i3 > i1) {
                rentDetailBinding.fabComment.hide();
            } else {
                rentDetailBinding.fabComment.show();
            }
        }
    }

    private void changeToolbarBackground(float i1) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, i1 / imageViewHeight);
        rentDetailBinding.toolbar.setBackgroundColor(ColorUtil.getColorWithAlpha(alpha, baseColor));
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (innerViewPagerAdapter.getPageTitle(i).equals("Comentarios")) {
            isCommentActive = true;
        } else {
            isCommentActive = false;
            rentDetailBinding.fabComment.hide();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}



