package com.infinitum.bookingqba.view.profile;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentProfileBinding;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.view.adapters.SpinnerAdapter;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.viewmodel.RentAnaliticsViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private RentAnaliticsViewModel viewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    private CommonSpinnerList commonSpinnerList;
    private String[] rentSelect;
    private int lastPosSelected;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return profileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        profileBinding.setShowSelect(false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentAnaliticsViewModel.class);

        loadUserData();

        loadRentAnalitics();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        compositeDisposable.clear();
    }

    private void loadRentAnalitics() {
        Set<String> stringSet = sharedPreferences.getStringSet(USER_RENTS, null);
        rentSelect = stringSet.toArray(new String[stringSet.size()]);
        if (rentSelect.length > 0) {
            profileBinding.tvRentName.setVisibility(View.GONE);
            disposable = viewModel.getRentSpinnerList(Arrays.asList(rentSelect))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(commonSpinnerList -> {
                        if (commonSpinnerList.getArrayNames().length > 1) {
                            updateSpinnerView(commonSpinnerList);
                        } else if (commonSpinnerList.getArrayNames().length == 1) {
                            updateSingleView(commonSpinnerList);
                        }
                    }, Timber::e);
            compositeDisposable.add(disposable);
        }
    }

    private void loadUserData() {
        String avatar = sharedPreferences.getString(USER_AVATAR, "");
        String url = BASE_URL_API + "/" + avatar;
        Glide.with(getView().getContext())
                .load(url)
                .crossFade()
                .placeholder(R.drawable.user_placeholder)
                .into(profileBinding.userAvatar);
        profileBinding.tvUsername.setText(sharedPreferences.getString(USER_NAME, getString(R.string.empty_text)));
    }

    private void updateSingleView(CommonSpinnerList commonSpinnerList) {
        this.commonSpinnerList = commonSpinnerList;
        profileBinding.setShowSelect(false);
        profileBinding.tvRentName.setVisibility(View.VISIBLE);
        profileBinding.tvRentName.setText(commonSpinnerList.getArrayNames()[0]);
        profileBinding.tvMsg.setText("");
        fetchRentAnalitic(commonSpinnerList.getUuidOnPos(0));
    }

    private void updateSpinnerView(CommonSpinnerList commonSpinnerList) {
        this.commonSpinnerList = commonSpinnerList;
        String[] arrNames = commonSpinnerList.getArrayNames();
        profileBinding.setItems(arrNames);
        profileBinding.setShowSelect(true);
        profileBinding.setIsLoading(false);
        profileBinding.spinnerRents.post(() -> {
            int height = profileBinding.spinnerRents.getHeight();
            profileBinding.spinnerRents.setDropDownVerticalOffset(height);
        });
        profileBinding.spinnerRents.setAdapter(new SpinnerAdapter(getActivity(), R.layout.spinner_profile_text_layout, arrNames));
        profileBinding.spinnerRents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lastPosSelected = position;
                String uuid = commonSpinnerList.getUuidOnPos(position);
                profileBinding.setIsLoading(true);
                profileBinding.tvMsg.setText("");
                fetchRentAnalitic(uuid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchRentAnalitic(String uuid) {
        disposable = viewModel.rentAnalitics(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(analiticsGroup -> {
                    if (analiticsGroup != null) {
                        profileBinding.setIsLoading(false);
                        profileBinding.srScaleRating.setRating(analiticsGroup.getRatingStarAnalitics().getRatingAverage());
                        profileBinding.pbDetailPercent.setEndProgress(analiticsGroup.getProfilePercentAnalitics().getPercent());
                        profileBinding.pbDetailPercent.startProgressAnimation();

                        ValueAnimator visitAnimator = getAnimatorTextNumber(profileBinding.tvVisitCount, analiticsGroup.getVisitAnalitics().getTotalVisit());
                        ValueAnimator commentAnimator = getAnimatorTextNumber(profileBinding.tvTotalComments, analiticsGroup.getGeneralCommentAnalitics().getTotalComment());
                        ValueAnimator wishAnimator = getAnimatorTextNumber(profileBinding.tvTotalListWish, analiticsGroup.getWishAnalitics().getTotalWish());
                        ValueAnimator terribleAnimator = getAnimatorTextNumber(profileBinding.tvTerribleCount,
                                analiticsGroup.getCommentsEmotionAnalitics().getTerribleCount());
                        ValueAnimator badAnimator = getAnimatorTextNumber(profileBinding.tvBadCount,
                                analiticsGroup.getCommentsEmotionAnalitics().getBadCount());
                        ValueAnimator okAnimator = getAnimatorTextNumber(profileBinding.tvOkCount,
                                analiticsGroup.getCommentsEmotionAnalitics().getOkCount());
                        ValueAnimator goodAnimator = getAnimatorTextNumber(profileBinding.tvGoodCount,
                                analiticsGroup.getCommentsEmotionAnalitics().getGoodCount());
                        ValueAnimator excelentAnimator = getAnimatorTextNumber(profileBinding.tvExcelentCount,
                                analiticsGroup.getCommentsEmotionAnalitics().getExcelentCount());
                        startNumberAnimation(visitAnimator, commentAnimator, wishAnimator,
                                terribleAnimator, badAnimator, okAnimator, goodAnimator,
                                excelentAnimator);

                        int totalVotes = analiticsGroup.getRatingStarAnalitics().getTotalVotes();
                        float percent5 = ((float) analiticsGroup.getRatingStarAnalitics().getFiveStar() / (float) totalVotes) * 100;
                        float percent4 = ((float) analiticsGroup.getRatingStarAnalitics().getFourStar() / (float) totalVotes) * 100;
                        float percent3 = ((float) analiticsGroup.getRatingStarAnalitics().getThreeStar() / (float) totalVotes) * 100;
                        float percent2 = ((float) analiticsGroup.getRatingStarAnalitics().getTwoStar() / (float) totalVotes) * 100;
                        float percent1 = ((float) analiticsGroup.getRatingStarAnalitics().getOneStar() / (float) totalVotes) * 100;

                        profileBinding.pb5Star.setEndProgress(percent5);
                        profileBinding.pb4Star.setEndProgress(percent4);
                        profileBinding.pb3Star.setEndProgress(percent3);
                        profileBinding.pb2Star.setEndProgress(percent2);
                        profileBinding.pb1Star.setEndProgress(percent1);

                        profileBinding.pb5Star.startProgressAnimation();
                        profileBinding.pb4Star.startProgressAnimation();
                        profileBinding.pb3Star.startProgressAnimation();
                        profileBinding.pb2Star.startProgressAnimation();
                        profileBinding.pb1Star.startProgressAnimation();


                        List<String> missing = analiticsGroup.getProfilePercentAnalitics().getMissingList();
                        if (missing.size() > 0) {
                            profileBinding.llContentProfile.setVisibility(View.VISIBLE);
                            StringBuilder commaSeparate = new StringBuilder();
                            for (int i = 0; i < missing.size(); i++) {
                                if (i < missing.size() - 1) {
                                    commaSeparate.append(missing.get(i)).append(" \n");
                                } else {
                                    commaSeparate.append(missing.get(i));
                                }
                            }
                            profileBinding.tvMissingList.setText(commaSeparate.toString());
                        } else {
                            profileBinding.llContentProfile.setVisibility(View.GONE);
                        }

                        profileBinding.tvPlaceRating.setText(String.valueOf(analiticsGroup.getRentPositionAnalitics().getPlaceRating()));
                        profileBinding.tvPlaceViews.setText(String.valueOf(analiticsGroup.getRentPositionAnalitics().getPlaceViews()));
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    if (throwable instanceof ConnectException) {
                        profileBinding.tvMsg.setText("Sin conexion");
                    } else {
                        profileBinding.tvMsg.setText("Ooops!! un error a ocurrido");
                    }
                    resetAllViews();
                });
        compositeDisposable.add(disposable);
    }

    private void resetAllViews() {
        //------- Profile percent
        profileBinding.pbDetailPercent.setProgress(0f);
        //------- Emotion
        profileBinding.tvTerribleCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvBadCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvOkCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvGoodCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvExcelentCount.setText(getString(R.string.empty_short_text));
        //------- Star
        profileBinding.srScaleRating.setRating(0f);
        profileBinding.pb5Star.setProgress(0f);
        profileBinding.pb4Star.setProgress(0f);
        profileBinding.pb3Star.setProgress(0f);
        profileBinding.pb2Star.setProgress(0f);
        profileBinding.pb1Star.setProgress(0f);
        //------- Number
        profileBinding.tvVisitCount.setText(getString(R.string.empty_short_text));
        profileBinding.tvTotalComments.setText(getString(R.string.empty_short_text));
        profileBinding.tvTotalListWish.setText(getString(R.string.empty_short_text));
        //-------- Missing List
        profileBinding.tvMissingList.setText(R.string.empty_text);
        //-------- Position
        profileBinding.tvPlaceRating.setText(R.string.empty_short_text);
        profileBinding.tvPlaceViews.setText(R.string.empty_short_text);
    }

    private void startNumberAnimation(ValueAnimator... valueAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator);
        animatorSet.start();
    }

    @NonNull
    private ValueAnimator getAnimatorTextNumber(TextView textView, int number) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, number);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> textView.setText(animation.getAnimatedValue().toString()));
        return valueAnimator;
    }

    //------------------------------- MENU --------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (rentSelect.length > 1) {
                    fetchRentAnalitic(commonSpinnerList.getUuidOnPos(lastPosSelected));
                } else if (rentSelect.length == 0) {
                    fetchRentAnalitic(commonSpinnerList.getUuidOnPos(0));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
