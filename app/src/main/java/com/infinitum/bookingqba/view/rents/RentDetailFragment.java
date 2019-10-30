package com.infinitum.bookingqba.view.rents;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graphhopper.util.StopWatch;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerDetailBinding;
import com.infinitum.bookingqba.databinding.FragmentRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.RDGalleryAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;

public class RentDetailFragment extends Fragment implements View.OnClickListener, RDGalleryAdapter.GalleryInteration {

    private FragmentRentDetailBinding fragmentRentDetailBinding;
    private RentInnerDetail rentInnerDetail;
    private InnerDetailInteraction innerDetailInteraction;
    private static final String RENT_UUID = "rentUuid";
    private String rentUuid;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;

    private RentViewModel rentViewModel;
    private ArrayList<RentCommentItem> commentList;
    private ArrayList<RentOfferItem> offerList;
    private int wishedValue;
    private String userId;


    public RentDetailFragment() {
        // Required empty public constructor
    }

    public static RentDetailFragment newInstance(String rentUuid) {
        RentDetailFragment fragment = new RentDetailFragment();
        Bundle args = new Bundle();
        args.putString(RENT_UUID, rentUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rentUuid = getArguments().getString(RENT_UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent_detail, container, false);
        return fragmentRentDetailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        fragmentRentDetailBinding.setLoading(true);
        fragmentRentDetailBinding.flPoiPlaces.setOnClickListener(this);
        fragmentRentDetailBinding.flOffer.setOnClickListener(this);
        fragmentRentDetailBinding.flComment.setOnClickListener(this);
        fragmentRentDetailBinding.llCallNow.setOnClickListener(this);
        fragmentRentDetailBinding.llSendSms.setOnClickListener(this);
        fragmentRentDetailBinding.tvBtnBook.setOnClickListener(this);
        fragmentRentDetailBinding.tvBtnVote.setOnClickListener(this);
        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        rentIsWished(rentUuid);
        addOrUpdateRentVisitCount(rentUuid);
        loadRentDetail(rentUuid);
    }

    private void addOrUpdateRentVisitCount(String uuid) {
        disposable = rentViewModel.addOrUpdateRentVisitCount(UUID.randomUUID().toString(), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(() -> Timber.e("Exito contador de visitas"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void rentIsWished(String rentUuid) {
        if (sharedPreferences.getBoolean(USER_IS_AUTH, false)) {
            userId = sharedPreferences.getString(USER_ID, "");
            disposable = rentViewModel.rentIsWished(rentUuid, userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integerResource -> {
                        wishedValue = integerResource.data != null ? integerResource.data : 0;
                        innerDetailInteraction.updateWishedValue(wishedValue);
                    }, Timber::e);
            compositeDisposable.add(disposable);
        }
    }

    private void loadRentDetail(String rentUuid) {
        disposable = rentViewModel.getRentDetailById(rentUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rentItemResource -> {
                    if (rentItemResource.data != null) {
                        updateUI(rentItemResource);
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateUI(Resource<RentItem> rentItemResource) {
        rentInnerDetail = rentItemResource.data.getRentInnerDetail();
        fragmentRentDetailBinding.setDetail(rentInnerDetail);
        fragmentRentDetailBinding.setLoading(false);
        if (rentInnerDetail.getGalerieItems().size() <= 1) {
            fragmentRentDetailBinding.llGalery.setVisibility(View.GONE);
        } else {
            setupGalerieAdapter(rentInnerDetail.getGalerieItems());
        }
        commentList = rentItemResource.data.getCommentItems();
        fragmentRentDetailBinding.tvCommentTitle.setText(String.format("Comentarios(%s)", rentItemResource.data.getCommentItems().size()));
        if (rentItemResource.data.getOfferItems().size() > 0) {
            fragmentRentDetailBinding.flOffer.setVisibility(View.VISIBLE);
            offerList = rentItemResource.data.getOfferItems();
        }
        setupAmenitiesAdapter(rentInnerDetail.getAmenitieItems());
        if (rentInnerDetail.getRentMode().equalsIgnoreCase("por horas")) {
            fragmentRentDetailBinding.llCallNow.setVisibility(View.VISIBLE);
            fragmentRentDetailBinding.llSendSms.setVisibility(View.VISIBLE);
            fragmentRentDetailBinding.tvPhoneNumber.setText(rentInnerDetail.getPersonalPhone());
            fragmentRentDetailBinding.tvPhoneSms.setText(rentInnerDetail.getPersonalPhone());
        }
        boolean userIsAuth = sharedPreferences.getBoolean(USER_IS_AUTH, false);
        fragmentRentDetailBinding.tvBtnVote.setVisibility(View.VISIBLE);
        if(userIsAuth && rentInnerDetail.getRentMode().equalsIgnoreCase("por noche")) {
            fragmentRentDetailBinding.tvBtnBook.setVisibility(View.VISIBLE);
            fragmentRentDetailBinding.ivSendEmailToOwner.setVisibility(View.VISIBLE);
        }
        fragmentRentDetailBinding.executePendingBindings();
    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof InnerDetailInteraction) {
            this.innerDetailInteraction = (InnerDetailInteraction) context;
        }
    }

    @Override
    public void onDetach() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        compositeDisposable.clear();
        super.onDestroyView();
    }

    private void setupAmenitiesAdapter(ArrayList<RentAmenitieItem> argAmenities) {
        if (argAmenities != null && argAmenities.size() > 0) {
            fragmentRentDetailBinding.fbAmenities.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (RentAmenitieItem rentAmenitieItem : argAmenities) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.recycler_rent_detail_amenities_item, null);
                textView.setText(rentAmenitieItem.getmName());
                params.setMargins(3, 3, 3, 3);
                textView.setLayoutParams(params);
                fragmentRentDetailBinding.fbAmenities.addView(textView);
            }
        }
    }

    private void setupGalerieAdapter(ArrayList<RentGalerieItem> argGaleries) {
        if (argGaleries != null && argGaleries.size() > 0) {
            RDGalleryAdapter adapter = new RDGalleryAdapter(argGaleries, this);
            fragmentRentDetailBinding.rvGalery.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            fragmentRentDetailBinding.rvGalery.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(fragmentRentDetailBinding.rvGalery, false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_price:
                innerDetailInteraction.onDrawChangeClick();
                break;
            case R.id.ll_call_now:
                innerDetailInteraction.phoneCallClick(rentInnerDetail.getPersonalPhone());
                break;
            case R.id.ll_send_sms:
                innerDetailInteraction.phoneSMSClick(rentInnerDetail.getPersonalPhone());
                break;
            case R.id.fl_email_to_owner:
                innerDetailInteraction.phoneEmailClick(rentInnerDetail.getEmail());
                break;
            case R.id.fl_poi_places:
                GeoRent geoRent = new GeoRent(rentInnerDetail.getId(), rentInnerDetail.getName(), rentInnerDetail.firstImage(), (float) rentInnerDetail.getPrice(), rentInnerDetail.getRentMode());
                geoRent.setRating(rentInnerDetail.getRating());
                geoRent.setRatingCount(rentInnerDetail.getVotes());
                geoRent.setAddress(rentInnerDetail.getAddress());
                geoRent.setGeoPoint(new GeoPoint(rentInnerDetail.getLatitude(), rentInnerDetail.getLongitude()));
                geoRent.setReferenceZone(rentInnerDetail.getReferenceZone());
                geoRent.setPoiItemMap(rentInnerDetail.getPoiItemMap());
                ArrayList<GeoRent> geoRents = new ArrayList<>();
                geoRents.add(geoRent);
                innerDetailInteraction.onAddressClick(geoRents);
                break;
            case R.id.fl_comment:
                innerDetailInteraction.onCommentClick(rentInnerDetail.getId(), rentInnerDetail.getOwnerId(), commentList);
                break;
            case R.id.fl_offer:
                innerDetailInteraction.onOfferClick(offerList);
                break;
            case R.id.tv_btn_book:
                innerDetailInteraction.onBookRequestClick(rentInnerDetail.getCapability());
                break;
            case R.id.tv_btn_vote:
                innerDetailInteraction.onVoteClick(rentInnerDetail.getOwnerId());
                break;
        }
    }

    public void updateFavoriteRent(int value) {
        updateFavoriteRent(rentUuid, userId, value);
    }

    private void updateFavoriteRent(String rentUuid, String userId, int wished) {
        disposable = rentViewModel.addOrUpdateRentIsWished(rentUuid, userId, wished)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(() -> Timber.e("WISHED ===> saved"), Timber::e);
        compositeDisposable.add(disposable);
    }

    private void createGeoRent() {
//        GeoRent geoRent = new GeoRent(rentInnerDetail.getId(), rentInnerDetail.getName(), rentInnerDetail.firstImage());
//        geoRent.setRentMode(rentInnerDetail.getRentMode());
//        geoRent.setRating(rentInnerDetail.getRating());
//        geoRent.setGeoPoint(new GeoPoint(rentInnerDetail.getLatitude(), rentInnerDetail.getLongitude()));
//        geoRent.setPrice(rentInnerDetail.getPrice());
//        ArrayList<GeoRent> geoRents = new ArrayList<>();
//        geoRents.add(geoRent);
//        innerDetailInteraction.onAddressClick(geoRents);
    }

    @Override
    public void onGalleryClick(String id) {
        innerDetailInteraction.onGaleryClick(id, rentInnerDetail.getGalerieItems());
    }
}
