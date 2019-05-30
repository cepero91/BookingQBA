package com.infinitum.bookingqba.view.listwish;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentListWishBinding;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID_DEFAULT;


public class ListWishFragment extends BaseNavigationFragment {

    private FragmentListWishBinding wishBinding;
    private Disposable disposable;

    private RentViewModel rentViewModel;

    @Inject
    SharedPreferences sharedPreferences;

    public ListWishFragment() {
        // Required empty public constructor
    }

    public static ListWishFragment newInstance() {
        return new ListWishFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        wishBinding.setIsLoading(true);
        wishBinding.setIsEmpty(false);

        setupRecyclerView();

        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wishBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_wish, container, false);
        return wishBinding.getRoot();
    }

    public void setupRecyclerView() {
        wishBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wishBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
    }

    public void loadData() {
        String province = sharedPreferences.getString(PROVINCE_UUID,PROVINCE_UUID_DEFAULT);
        disposable = rentViewModel.getRentListWish(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> setItemsAdapter(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }


    public void setItemsAdapter(List<ListWishItem> rendererViewModelList) {
        if(rendererViewModelList.size()>0) {
            RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
            recyclerViewAdapter.registerRenderer(getListWishBinder(R.layout.recycler_list_wish_item));
            recyclerViewAdapter.setItems(rendererViewModelList);
            wishBinding.recyclerView.setAdapter(recyclerViewAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(wishBinding.recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            wishBinding.setIsLoading(false);
            wishBinding.setIsEmpty(false);
        }else{
            wishBinding.setIsLoading(false);
            wishBinding.setIsEmpty(true);
        }
    }



    private ViewBinder<?> getListWishBinder(int layout) {
        return new ViewBinder<>(
                layout,
                ListWishItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_rent_name, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.tv_address, (ViewProvider<TextView>) view -> view.setText(model.getAddress()))
                        .find(R.id.tv_rent_mode, (ViewProvider<TextView>) view -> view.setText(" /"+model.getRentMode()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format("$ %s", String.valueOf(model.getPrice()))))
                        .find(R.id.siv_rent_image, (ViewProvider<RoundedImageView>) view ->
                                Glide.with(getView().getContext())
                                        .load(model.getImagePath())
                                        .crossFade()
                                        .placeholder(R.drawable.placeholder)
                                        .into(view))
                        .setOnClickListener(R.id.cv_rent_content, (v -> mListener.onItemClick(v, model)))
        );
    }

}
