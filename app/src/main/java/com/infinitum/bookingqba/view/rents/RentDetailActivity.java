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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.remote.pojo.Comment;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.ColorUtil;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
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
import com.squareup.picasso.Picasso;


import org.oscim.core.GeoPoint;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import static com.infinitum.bookingqba.util.Constants.ALTERNATIVE_SYNC;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.IS_PROFILE_ACTIVE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_TAG;
import static com.infinitum.bookingqba.util.Constants.NAV_HEADER_REQUIRED_UPDATE;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_ID;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

//NestedScrollView.OnScrollChangeListener,

public class RentDetailActivity extends DaggerAppCompatActivity implements HasSupportFragmentInjector,
        DialogComment.CommentInteraction, DialogRating.RatingInteraction,
        InnerDetailInteraction {

    public static final int HAS_COMMENT_ONLY = 0;
    public static final int HAS_OFFER_ONLY = 1;
    public static final int HAS_COMMENT_OFFER = 2;
    public static final int HAS_DETAIL_ONLY = 3;
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
    private int isWished = 0;
    private int mirrorWished = 0;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    Fragment innerDetail;
    Fragment commentDetail;
    Fragment offerDetail;
    private boolean loginIsClicked = false;
    private boolean showMenuGroup = false;
    private final static int REQUEST_PHONE_CALL = 1044;
    private final static int REQUEST_PHONE_SMS = 1045;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate");
        AndroidInjection.inject(this);

        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);

        deviceID = sharedPreferences.getString(IMEI, "");

        rentDetailBinding.setIsLoading(true);
        rentDetailBinding.setHasTab(false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null) {
            rentUuid = getIntent().getExtras().getString("uuid");
            String imageUrlPath = getIntent().getExtras().getString("url");
            if (!imageUrlPath.contains("http")) {
                imageUrlPath = "file:" + imageUrlPath;
            }
            isWished = getIntent().getExtras().getInt("wished");
            mirrorWished = isWished;
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
        Timber.e("Entro OnSuccess");
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
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_COMMENT_ONLY));
        } else if (rentItem.getOfferItems().size() > 0 && rentItem.getCommentItems().size() == 0) {
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_OFFER_ONLY));
        } else if (rentItem.getOfferItems().size() > 0 && rentItem.getCommentItems().size() > 0) {
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_COMMENT_OFFER));
        } else {
            setupViewPagerWithNav(getFragmentList(rentItem, HAS_DETAIL_ONLY));
        }
    }

    @NonNull
    private Pair<List<Fragment>, List<String>> getFragmentList(RentItem rentItem, int type) {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        innerDetail = InnerDetailFragment.newInstance(rentItem.getRentInnerDetail());
        commentDetail = RentCommentFragment.newInstance(rentItem.getCommentItems());
        offerDetail = RentOfferFragment.newInstance(rentItem.getOfferItems());
        switch (type) {
            case HAS_DETAIL_ONLY:
                rentDetailBinding.setHasTab(false);
                fragments.add(innerDetail);
                titles.add("Detalles");
                break;
            case HAS_COMMENT_ONLY:
                rentDetailBinding.setHasTab(true);
                fragments.add(innerDetail);
                fragments.add(commentDetail);
                titles.add("Detalles");
                titles.add("Comentarios");
                break;
            case HAS_OFFER_ONLY:
                rentDetailBinding.setHasTab(true);
                fragments.add(innerDetail);
                fragments.add(offerDetail);
                titles.add("Detalles");
                titles.add("Ofertas");
                break;
            case HAS_COMMENT_OFFER:
                rentDetailBinding.setHasTab(true);
                fragments.add(innerDetail);
                fragments.add(commentDetail);
                fragments.add(offerDetail);
                titles.add("Detalles");
                titles.add("Comentarios");
                titles.add("Ofertas");
                break;
        }
        return new Pair<>(fragments, titles);
    }

    private void setupViewPagerWithNav(Pair<List<Fragment>, List<String>> pair) {
        rentDetailBinding.setIsLoading(false);
        innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager(), pair.first, pair.second);
        rentDetailBinding.viewpager.setAdapter(innerViewPagerAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(rentDetailBinding.viewpager);
        rentDetailBinding.tlTab.setViewPager(rentDetailBinding.viewpager);
    }

    private void showDialogComment() {
        boolean isAuth = sharedPreferences.getBoolean(USER_IS_AUTH, false);
        String username = sharedPreferences.getString(USER_NAME, "");
        String userid = sharedPreferences.getString(USER_ID, "");
        String rentId = rentUuid;
        if (!isAuth) {
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
    public void onBackPressed() {
        if (isWished == mirrorWished && !showMenuGroup) {
            super.onBackPressed();
        } else if (isWished != mirrorWished && showMenuGroup) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("refresh", true);
            intent.putExtra("group", true);
            setResult(FROM_DETAIL_REFRESH_SHOW_GROUP, intent);
            this.finish();
        } else if (isWished != mirrorWished) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("refresh", true);
            setResult(FROM_DETAIL_REFRESH, intent);
            this.finish();
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("refresh", true);
            intent.putExtra("group", true);
            setResult(FROM_DETAIL_SHOW_GROUP, intent);
            this.finish();
        }
    }

    @Override
    public void sendComment(Comment comment) {
        disposable = viewModel.addComment(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(this, "Comentario guardado"), Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    public void sendRating(float rating, String comment) {
        String rentId = rentUuid;
        disposable = viewModel.addRating(rating, comment, rentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> AlertUtils.showSuccessToast(this, "Votacion exitosa"), Timber::e);
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
        String userId = sharedPreferences.getString(USER_ID, "");
        String token = sharedPreferences.getString(USER_TOKEN, "");
        String rentId = rentUuid;
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(USER_TOKEN, token);
        intent.putExtra("price", rentItem.getRentInnerDetail().getPrice());
        intent.putExtra("rentId", rentId);
        intent.putExtra("maxcapability", rentItem.getRentInnerDetail().getCapability());
        startActivity(intent);
    }

    //------------------------------------- Login -------------------------------------//


    //menu
//    case R.id.action_list_wish:
//    setIsWished();
//                return true;
//            case R.id.action_vote:
//    disposable = viewModel.getLastRentVote(rentUuid)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(pair -> {
//        DialogRating lf = DialogRating.newInstance(pair.first, pair.second);
//        lf.show(getSupportFragmentManager(), "RatingDialog");
//    }, Timber::e);
//                compositeDisposable.add(disposable);
//                return true;
//            case android.R.id.home:
//    onBackPressed();
//                return true;
//            case R.id.action_comment:
//    showDialogComment();
//                return true;
}



