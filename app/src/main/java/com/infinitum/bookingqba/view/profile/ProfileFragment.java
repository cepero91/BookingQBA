package com.infinitum.bookingqba.view.profile;


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
import com.infinitum.bookingqba.view.adapters.comments.CommentListAdapter;
import com.infinitum.bookingqba.view.adapters.items.comment.CommentItem;
import com.infinitum.bookingqba.view.adapters.items.profile.RentProfileItem;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentAnaliticsViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;

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
        profileBinding.setIsLoading(true);
        disposable = viewModel.getRentAnalitics(new ArrayList<>()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rentAnalitics -> {
                    updateViews(rentAnalitics);
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateViews(RentAnalitics rentAnalitics) {
        profileBinding.setIsLoading(false);
        profileBinding.tvRentName.setText(rentAnalitics.getRentName());
        profileBinding.pbDetailPercent.setPercentage((int) rentAnalitics.getRentDetailPercent());
        profileBinding.pbDetailPercent.setStepCountText(String.format("%s %s", rentAnalitics.getRentDetailPercent(), "%"));
        profileBinding.tvVisitCount.setText(String.valueOf(rentAnalitics.getTotalVisitCount()));
        profileBinding.tvTotalComments.setText(String.valueOf(rentAnalitics.getTotalComments()));
        profileBinding.tvTotalListWish.setText(String.valueOf(rentAnalitics.getTotalListWish()));

        int totalVotation = 0;
        for (int i = 0; i < rentAnalitics.getRatingByStar().length; i++) {
            totalVotation += rentAnalitics.getRatingByStar()[i];
        }

        float p5 = ((float)rentAnalitics.getRatingByStar()[0]/(float) totalVotation)*100;
        float p4 = ((float)rentAnalitics.getRatingByStar()[1]/(float) totalVotation)*100;
        float p3 = ((float)rentAnalitics.getRatingByStar()[2]/(float) totalVotation)*100;
        float p2 = ((float)rentAnalitics.getRatingByStar()[3]/(float) totalVotation)*100;
        float p1 = ((float)rentAnalitics.getRatingByStar()[4]/(float) totalVotation)*100;

        profileBinding.pb5Star.setEndProgress(p5);
        profileBinding.pb4Star.setEndProgress(p4);
        profileBinding.pb3Star.setEndProgress(p3);
        profileBinding.pb2Star.setEndProgress(p2);
        profileBinding.pb1Star.setEndProgress(p1);

        profileBinding.pb5Star.startProgressAnimation();
        profileBinding.pb4Star.startProgressAnimation();
        profileBinding.pb3Star.startProgressAnimation();
        profileBinding.pb2Star.startProgressAnimation();
        profileBinding.pb1Star.startProgressAnimation();

    }


}
