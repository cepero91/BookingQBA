package com.infinitum.bookingqba.view.profile;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentProfileBinding;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.SpinnerAdapter;
import com.infinitum.bookingqba.view.adapters.comments.CommentListAdapter;
import com.infinitum.bookingqba.view.adapters.items.comment.CommentItem;
import com.infinitum.bookingqba.view.adapters.items.profile.RentProfileItem;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentAnaliticsViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentAnaliticsViewModel viewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

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

        profileBinding.setIsLoading(true);
        profileBinding.setShowSelect(false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentAnaliticsViewModel.class);

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
        disposable = viewModel.getRentAnalitics(new ArrayList<>()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rentAnalitics -> {
                    updateViews(rentAnalitics);
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateViews(List<RentAnalitics> rentAnaliticsList) {
        if(rentAnaliticsList.size() > 1){
            profileBinding.tvRentName.setVisibility(View.GONE);
            String[] arrEntries = new String[rentAnaliticsList.size()];
            for (int i = 0; i < rentAnaliticsList.size(); i++) {
                arrEntries[i] = rentAnaliticsList.get(i).getRentName();
            }
            profileBinding.setItems(arrEntries);
            profileBinding.setShowSelect(true);
            profileBinding.spinnerRents.post(() -> {
                int height = profileBinding.spinnerRents.getHeight();
                profileBinding.spinnerRents.setDropDownVerticalOffset(height);
            });
            profileBinding.spinnerRents.setAdapter(new SpinnerAdapter(getActivity(),R.layout.spinner_profile_text_layout,arrEntries));
            profileBinding.spinnerRents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateParamsView(rentAnaliticsList.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            profileBinding.tvRentName.setVisibility(View.VISIBLE);
            updateParamsView(rentAnaliticsList.get(0));
        }


    }

    private void updateParamsView(RentAnalitics rentAnalitics) {
        profileBinding.setIsLoading(false);
        profileBinding.srScaleRating.setRating(rentAnalitics.getRating());
        profileBinding.tvRentName.setText(rentAnalitics.getRentName());
        profileBinding.pbDetailPercent.setEndProgress(rentAnalitics.getRentDetailPercent());

        ValueAnimator tvVisitCountAnimator = ValueAnimator.ofInt(0, rentAnalitics.getTotalVisitCount());
        tvVisitCountAnimator.setDuration(3000);
        tvVisitCountAnimator.addUpdateListener(animation -> profileBinding.tvVisitCount
                .setText(animation.getAnimatedValue().toString()));

        ValueAnimator tvCommentAnimator = ValueAnimator.ofInt(0, rentAnalitics.getTotalComments());
        tvCommentAnimator.setDuration(3000);
        tvCommentAnimator.addUpdateListener(animation -> profileBinding.tvTotalComments
                .setText(animation.getAnimatedValue().toString()));

        ValueAnimator tvListWishAnimator = ValueAnimator.ofInt(0, rentAnalitics.getTotalListWish());
        tvListWishAnimator.setDuration(3000);
        tvListWishAnimator.addUpdateListener(animation -> profileBinding.tvTotalListWish
                .setText(animation.getAnimatedValue().toString()));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(tvVisitCountAnimator,tvCommentAnimator,tvListWishAnimator);
        animatorSet.start();

        int totalVotation = 0;
        for (int i = 0; i < rentAnalitics.getRatingByStar().length; i++) {
            totalVotation += rentAnalitics.getRatingByStar()[i];
        }

        float p5 = ((float) rentAnalitics.getRatingByStar()[0] / (float) totalVotation) * 100;
        float p4 = ((float) rentAnalitics.getRatingByStar()[1] / (float) totalVotation) * 100;
        float p3 = ((float) rentAnalitics.getRatingByStar()[2] / (float) totalVotation) * 100;
        float p2 = ((float) rentAnalitics.getRatingByStar()[3] / (float) totalVotation) * 100;
        float p1 = ((float) rentAnalitics.getRatingByStar()[4] / (float) totalVotation) * 100;

        profileBinding.pb5Star.setEndProgress(p5);
        profileBinding.pb4Star.setEndProgress(p4);
        profileBinding.pb3Star.setEndProgress(p3);
        profileBinding.pb2Star.setEndProgress(p2);
        profileBinding.pb1Star.setEndProgress(p1);

        profileBinding.pbDetailPercent.startProgressAnimation();
        profileBinding.pb5Star.startProgressAnimation();
        profileBinding.pb4Star.startProgressAnimation();
        profileBinding.pb3Star.startProgressAnimation();
        profileBinding.pb2Star.startProgressAnimation();
        profileBinding.pb1Star.startProgressAnimation();
    }


}
