package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.InnerViewPagerAdapter;
import com.infinitum.bookingqba.view.galery.GaleryActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;


import org.oscim.core.GeoPoint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
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
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;

//NestedScrollView.OnScrollChangeListener,

public class RentDetailActivity extends AppCompatActivity implements
        View.OnClickListener, DialogComment.CommentInteraction, DialogRating.RatingInteraction, InnerDetailInteraction {

    public static final int HAS_COMMENT_ONLY = 0;
    public static final int HAS_OFFER_ONLY = 1;
    public static final int HAS_COMMENT_OFFER = 2;
    private ActivityRentDetailBinding rentDetailBinding;
    private int imageViewHeight;
    private InnerViewPagerAdapter innerViewPagerAdapter;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel viewModel;
    private RentItem rentItem;
    private String rentUuid = "";
    private int isWished = 0;
    private int mirrorWished = 0;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private FragmentManager fragmentManager;
    private List<WeakReference<Fragment>> weakReferenceList;
    Fragment innerDetail;
    Fragment commentDetail;
    Fragment offerDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        rentDetailBinding.setIsLoading(true);
        rentDetailBinding.fabComment.setOnClickListener(this);
        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null) {
            rentUuid = getIntent().getExtras().getString("uuid");
            String imageUrlPath = getIntent().getExtras().getString("url");
            isWished = getIntent().getExtras().getInt("wished");
            mirrorWished = isWished;
            GlideApp.with(this).load(imageUrlPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(rentDetailBinding.ivRent);
        }

        if (rentUuid.length() > 1) {
            loadRentDetail();
        }

        //Configurations
//        rentDetailBinding.nested.setOnScrollChangeListener(this);
        imageViewHeight = getResources().getDimensionPixelSize(R.dimen.rent_detail_img_dimen);
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
                        addOrUpdateRentVisitCount(rentItem.getRentInnerDetail().getId());
                    }
                })
                .subscribe();
        compositeDisposable.add(disposable);
    }


    private void onSuccess(Resource<RentItem> rentDetailResource) {
        rentItem = rentDetailResource.data;
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

    private void setupViewPager(RentItem rentItem) {
        if (rentItem.getCommentItems().size() > 0 && rentItem.getOfferItems().size() == 0) {
//            rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_COMMENT_ONLY));
        } else if (rentItem.getOfferItems().size() > 0 && rentItem.getCommentItems().size() == 0) {
//            rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_OFFER_ONLY));
        } else if (rentItem.getOfferItems().size() > 0 && rentItem.getCommentItems().size() > 0) {
//            rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_COMMENT_OFFER));
        } else {
//            rentDetailBinding.clComposite.setVisibility(View.GONE);
            Fragment innerDetail = InnerDetailFragment.newInstance(rentItem.getRentInnerDetail());
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_single, innerDetail).commit();
        }
    }

//    private void changeNavTabVisibility(int type) {
//        switch (type) {
//            case HAS_COMMENT_ONLY:
//                rentDetailBinding.flSingle.setVisibility(View.GONE);
//                rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
//                rentDetailBinding.lItemComment.setVisibility(View.VISIBLE);
//                break;
//            case HAS_OFFER_ONLY:
//                rentDetailBinding.flSingle.setVisibility(View.GONE);
//                rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
//                rentDetailBinding.lItemOffer.setVisibility(View.VISIBLE);
//                break;
//            case HAS_COMMENT_OFFER:
//                rentDetailBinding.flSingle.setVisibility(View.GONE);
//                rentDetailBinding.clComposite.setVisibility(View.VISIBLE);
//                rentDetailBinding.lItemComment.setVisibility(View.VISIBLE);
//                rentDetailBinding.lItemOffer.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

    @NonNull
    private Pair<List<Fragment>,List<String>> getFragmentList(RentItem rentItem, int type) {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        innerDetail = InnerDetailFragment.newInstance(rentItem.getRentInnerDetail());
        commentDetail = RentCommentFragment.newInstance(rentItem.getCommentItems());
        offerDetail = RentOfferFragment.newInstance(rentItem.getOfferItems());
        switch (type) {
            case HAS_COMMENT_ONLY:
                fragments.add(innerDetail);
                fragments.add(commentDetail);
                titles.add("Detalles");
                titles.add("Comentarios");
                break;
            case HAS_OFFER_ONLY:
                fragments.add(innerDetail);
                fragments.add(offerDetail);
                titles.add("Detalles");
                titles.add("Ofertas");
                break;
            case HAS_COMMENT_OFFER:
                fragments.add(innerDetail);
                fragments.add(commentDetail);
                fragments.add(offerDetail);
                titles.add("Detalles");
                titles.add("Comentarios");
                titles.add("Ofertas");
                break;
        }
        return new Pair<>(fragments,titles);
    }

    private void setupViewPagerWithNav(Pair<List<Fragment>,List<String>> pair) {
        innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager(),pair.first,pair.second);
        rentDetailBinding.viewpager.setAdapter(innerViewPagerAdapter);
        rentDetailBinding.tlTab.setViewPager(rentDetailBinding.viewpager);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_comment:
                showDialogComment();
                break;
        }
    }

    private void showDialogComment() {
        String username = sharedPreferences.getString(USER_NAME, "");
        String userid = sharedPreferences.getString(USER_ID, "");
        String rentId = rentUuid;
        if (username.equals("") && userid.equals("")) {
            AlertUtils.showErrorAlert(this, "Debe estar autenticado para comentar");
        } else {
            DialogComment lf = DialogComment.newInstance(username, userid, rentId);
            lf.show(getSupportFragmentManager(), "CommentDialog");
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
            case R.id.action_vote:
                disposable = viewModel.getLastRentVote(rentUuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aFloat -> {
                            DialogRating lf = DialogRating.newInstance(aFloat);
                            lf.show(getSupportFragmentManager(), "RatingDialog");
                        }, Timber::e);
                compositeDisposable.add(disposable);
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
            updateEntity(rentUuid, isWished);
        } else if (isWished == 0) {
            isWished = 1;
            updateEntity(rentUuid, isWished);
        }
    }

    private void updateEntity(String rentUuid, int wished) {
        disposable = viewModel.updateRent(rentUuid, wished)
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

//    @Override
//    public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
//        changeToolbarBackground(i1);
//    }

    private void changeToolbarBackground(float i1) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, i1 / imageViewHeight);
        rentDetailBinding.toolbar.setBackgroundColor(ColorUtil.getColorWithAlpha(alpha, baseColor));
    }

    @Override
    public void sendComment(Comment comment) {
        disposable = viewModel.addComment(comment).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(this, "Comentario guardado"), Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    public void sendRating(float rating, String comment) {
        String rentId = rentUuid;
        disposable = viewModel.addRating(rating, comment, rentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(this, "RatingEmbeded guardado"), Timber::e);
        compositeDisposable.add(disposable);
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

}



