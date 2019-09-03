package com.infinitum.bookingqba.view.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentHomeBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.view.adapters.MostCommentRentAdapter;
import com.infinitum.bookingqba.view.adapters.MostRatingRentAdapter;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.viewmodel.HomeViewModel;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_COMMENTED;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_RATING;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_SPINNER_INDEX;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;


public class HomeFragment extends BaseNavigationFragment implements View.OnClickListener {

    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel homeViewModel;
    private Disposable disposable;

    private CommonSpinnerList spinnerList;
    private ArrayAdapter<String> adapter;

    @Inject
    SharedPreferences sharedPreferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentHomeBinding.setIsLoading(true);

        setHasOptionsMenu(true);

        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        loadProvinces();

        fragmentHomeBinding.spinnerProvinces.setTitle(getResources().getString(R.string.dialog_provinces_title));
        fragmentHomeBinding.spinnerProvinces.setPositiveButton(getResources().getString(R.string.positive_buttom));
        fragmentHomeBinding.tvMostCommentedViewAll.setOnClickListener(this);
        fragmentHomeBinding.tvMostRatingViewAll.setOnClickListener(this);

    }

    private void loadProvinces() {
        disposable = homeViewModel.getProvinces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProvinceLoad, Timber::e);
        compositeDisposable.add(disposable);
    }


    private void onProvinceLoad(Resource<CommonSpinnerList> listResource) {
        int provinceIndex = sharedPreferences.getInt(PROVINCE_SPINNER_INDEX, 0);
        this.spinnerList = listResource.data;
        if (adapter == null) {
            adapter = new ArrayAdapter<>(getActivity(), R.layout.center_text_layout, spinnerList.getArrayNames());
        }
        fragmentHomeBinding.spinnerProvinces.setAdapter(adapter);
        fragmentHomeBinding.spinnerProvinces.setSelection(provinceIndex);
        fragmentHomeBinding.spinnerProvinces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String uuid = spinnerList.getUuidOnPos(position);
                loadData(uuid);
                saveProvinceToPreference(position, uuid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void saveProvinceToPreference(int pos, String uuid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROVINCE_SPINNER_INDEX, pos);
        editor.putString(PROVINCE_UUID, uuid);
        editor.apply();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    public void loadData(String provinceUuid) {
        disposable = homeViewModel.getAllItems(provinceUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapResource -> setItemsAdapter(mapResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    public void setItemsAdapter(Map<String,List<BaseItem>> listMap) {
        fragmentHomeBinding.setIsLoading(false);

        if(listMap.containsKey("MostCommented") && listMap.get("MostCommented")!=null){
            MostCommentRentAdapter mostCommentRentAdapter = new MostCommentRentAdapter(listMap.get("MostCommented"), getLayoutInflater(), mListener);
            fragmentHomeBinding.rvMostCommented.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            fragmentHomeBinding.rvMostCommented.setAdapter(mostCommentRentAdapter);
        }

        if(listMap.containsKey("MostRating") && listMap.get("MostRating")!=null) {
            MostRatingRentAdapter mostRatingRentAdapter = new MostRatingRentAdapter(listMap.get("MostRating"), getLayoutInflater(), mListener);
            fragmentHomeBinding.rvMostRating.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentHomeBinding.rvMostRating.setAdapter(mostRatingRentAdapter);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_most_commented_view_all:
                goToRentListWithOrderType(ORDER_TYPE_MOST_COMMENTED);
                break;
            case R.id.tv_most_rating_view_all:
                goToRentListWithOrderType(ORDER_TYPE_MOST_RATING);
                break;
        }
    }

    private void goToRentListWithOrderType(char orderType) {
        mListener.onViewAllClick(orderType);
    }
}
