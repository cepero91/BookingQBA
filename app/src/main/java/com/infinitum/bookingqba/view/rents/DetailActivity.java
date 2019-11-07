package com.infinitum.bookingqba.view.rents;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityDetailBinding;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.galery.GaleryActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.DATE_RANGE;
import static com.infinitum.bookingqba.util.Constants.END_DATE;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.MAXCAPABILITY;
import static com.infinitum.bookingqba.util.Constants.NIGHT_COUNT;
import static com.infinitum.bookingqba.util.Constants.PRICE;
import static com.infinitum.bookingqba.util.Constants.RENT_ID;
import static com.infinitum.bookingqba.util.Constants.START_DATE;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;

public class DetailActivity extends DaggerAppCompatActivity implements HasSupportFragmentInjector,
        InnerDetailInteraction, View.OnClickListener, Animator.AnimatorListener,
        BookDaysDialog.BookDaysDialogInteraction {

    private final static int REQUEST_PHONE_CALL = 1044;
    private final static int REQUEST_PHONE_SMS = 1045;

    private ActivityDetailBinding detailBinding;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;

    private BaseItem esentialRent;
    private int wished = 0;
    private RentDetailFragment rentDetailFragment;
    private int phoneActionCode = 0;
    private String phoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        esentialRent = getIntent().getParcelableExtra("item");
        detailBinding.setItem(esentialRent);

        showPiccasoImage(esentialRent);

        setupLottieView();

        setupToolbar();

        rentDetailFragment = RentDetailFragment.newInstance(esentialRent.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content_detail_fragment, rentDetailFragment).commit();
    }

    private void setupLottieView() {
        detailBinding.lvHeart.setOnClickListener(this);
        detailBinding.lvHeart.addAnimatorListener(this);
        if (sharedPreferences.getBoolean(USER_IS_AUTH, false)) {
            detailBinding.circle.setVisibility(View.VISIBLE);
            detailBinding.lvHeart.setVisibility(View.VISIBLE);
        } else {
            detailBinding.circle.setVisibility(View.GONE);
            detailBinding.lvHeart.setVisibility(View.GONE);
        }
    }

    private void showPiccasoImage(BaseItem esentialRent) {
        String imageUrlPath = "file:" + esentialRent.getImagePath();
        Picasso.get()
                .load(imageUrlPath)
                .placeholder(R.drawable.placeholder)
                .into(detailBinding.ivRent);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    private void setupToolbar() {
        setSupportActionBar(detailBinding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onGaleryClick(String id, ArrayList<RentGalerieItem> rentGalerieItems) {
        Intent intent = new Intent(this, GaleryActivity.class);
        intent.putExtra("id", id);
        intent.putParcelableArrayListExtra("imageList", rentGalerieItems);
        startActivity(intent);
    }

    @Override
    public void onAddressClick(ArrayList<GeoRent> geoRentList) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putParcelableArrayListExtra("geoRents", geoRentList);
        setResult(FROM_DETAIL_TO_MAP, intent);
        this.finish();
    }

    @Override
    public void onCommentClick(String rentUuid, String rentOwnerId, ArrayList<RentCommentItem> argComment) {
        RentCommentFragment rentCommentFragment = RentCommentFragment.newInstance(rentUuid, rentOwnerId, argComment);
        rentCommentFragment.show(getSupportFragmentManager(), "rentPoi");
    }

    @Override
    public void onOfferClick(ArrayList<RentOfferItem> argOffer) {
        RentOfferFragment rentOfferFragment = RentOfferFragment.newInstance(argOffer);
        rentOfferFragment.show(getSupportFragmentManager(), "rentOffer");
    }

    @Override
    public void updateWishedValue(int wished) {
        this.wished = wished;
        updateLottieView(wished);
    }

    private void updateLottieView(int wished) {
        if (wished == 0) {
            detailBinding.lvHeart.setProgress(0f);
        } else if (wished == 1) {
            detailBinding.lvHeart.setProgress(1f);
        }
    }

    @Override
    public void phoneCallClick(String phone) {
        phoneActionCode = 1;
        phoneStr = phone;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void phoneSMSClick(String phone) {
        phoneActionCode = 2;
        phoneStr = phone;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PHONE_SMS);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void phoneHomeClick(String phone) {

    }

    @Override
    public void phoneEmailClick(String email) {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + email);
        mailIntent.setData(data);
        startActivity(Intent.createChooser(mailIntent, "Send mail..."));
    }

    @Override
    public void onBookRequestClick(int maxCapability) {
        BookDaysDialog bookDaysDialog = BookDaysDialog.newInstance(esentialRent.getId(), maxCapability);
        bookDaysDialog.show(getSupportFragmentManager(), "calendarDialog");
    }

    @Override
    public void onVoteClick(String userOwner) {
        RatingDialog ratingDialog = RatingDialog.newInstance(esentialRent.getId(), userOwner);
        ratingDialog.show(getSupportFragmentManager(), "ratingDialog");
    }

    @Override
    public void onDrawChangeClick() {

    }

    @Override
    public void onPoiPlacesClick(String rentUuid, String referenceZone, String latLon) {
        RentPoiFragment rentPoiFragment = RentPoiFragment.newInstance(rentUuid, referenceZone, latLon);
        rentPoiFragment.show(getSupportFragmentManager(), "rentPoi");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_heart:
                if (!detailBinding.lvHeart.isAnimating()) {
                    if (wished == 0) {
                        wished = 1;
                        detailBinding.lvHeart.playAnimation();
                    } else if (wished == 1) {
                        wished = 0;
                        detailBinding.lvHeart.setProgress(0);
                        saveWishedValue();
                    }
                }
                break;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        saveWishedValue();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private void saveWishedValue() {
        if (rentDetailFragment != null && rentDetailFragment.isAdded() && rentDetailFragment.isVisible()) {
            rentDetailFragment.updateFavoriteRent(wished);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (permissions[0]) {
            case Manifest.permission.CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (phoneActionCode == 1) {
                        phoneActionCode = 0;
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneStr));
                        startActivity(intent);
                    }
                } else if (phoneActionCode == 1) {
                    phoneActionCode = 0;
                    AlertUtils.showErrorToast(this, "Conceda los permisos para llamar.");
                }
                break;
            case Manifest.permission.SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (phoneActionCode == 2) {
                        phoneActionCode = 0;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneStr));
                        startActivity(intent);
                    }
                } else if (phoneActionCode == 2) {
                    phoneActionCode = 0;
                    AlertUtils.showErrorToast(this, "Conceda los permisos para enviar mensaje.");
                }
                break;
        }
    }

    @Override
    public void onRangeSelected(String startDate, String endDate, int nightCount, int capability) {
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra(PRICE, esentialRent.getPrice());
        intent.putExtra(RENT_ID, esentialRent.getId());
        intent.putExtra(MAXCAPABILITY, capability);
        intent.putExtra(NIGHT_COUNT, nightCount);
        intent.putExtra(DATE_RANGE, String.format("%s / %s", startDate, endDate));
        intent.putExtra(START_DATE, startDate);
        intent.putExtra(END_DATE, endDate);
        startActivity(intent);
    }
}