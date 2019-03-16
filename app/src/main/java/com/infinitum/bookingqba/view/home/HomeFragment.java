package com.infinitum.bookingqba.view.home;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewHolder;
import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewState;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.ViewState;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewStateProvider;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentHomeBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.home.RentNewItem;
import com.infinitum.bookingqba.view.adapters.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.home.RZoneItem;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.HomeViewModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.willy.ratingbar.BaseRatingBar;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;


public class HomeFragment extends BaseNavigationFragment {

    private static final int MAX_SPAN_COUNT = 3;

    private RendererRecyclerViewAdapter recyclerViewAdapter;
    private GridLayoutManager mLayoutManager;
    private DataGenerator dataGenerator = new DataGenerator();

    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel homeViewModel;
    private Disposable disposable;

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

        setHasOptionsMenu(true);

        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        fragmentHomeBinding.setIsLoading(true);
        fragmentHomeBinding.slShimmerHome.startShimmer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                testingRecycleView();
            }
        },1000);



    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_filter_panel);
        menuItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void initRecycleView() {
        recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getHeader());
        recyclerViewAdapter.registerRenderer(getCompositeRent());
        recyclerViewAdapter.setItems(dataGenerator.getRZComposite());
        fragmentHomeBinding.recyclerView.setAdapter(recyclerViewAdapter);
        fragmentHomeBinding.recyclerView.setLayoutManager(setupLayoutManager());
        fragmentHomeBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(5, 5));
    }

    @NonNull
    private CompositeViewBinder<?> getCompositeRent() {
        return (CompositeViewBinder) new CompositeViewBinder<>(
                R.layout.recycler_composite,
                R.id.recycler_view,
                RecyclerViewItem.class,
                Collections.singletonList(new BetweenSpacesItemDecoration(5, 0)),
                new CompositeViewStateProvider<RecyclerViewItem, CompositeViewHolder>() {
                    @Override
                    public ViewState createViewState(@NonNull final CompositeViewHolder holder) {
                        return new CompositeViewState(holder);
                    }

                    @Override
                    public int createViewStateID(@NonNull final RecyclerViewItem model) {
                        return model.getID();
                    }
                }
        ).registerRenderer(getReferenceZone())
                .registerRenderer(getRentPopItem(R.layout.recycler_rent_pop_item))
                .registerRenderer(getRentNewItem(R.layout.recycler_rent_new_item));
    }


    public RecyclerView.LayoutManager setupLayoutManager() {
        mLayoutManager = new GridLayoutManager(getActivity(), MAX_SPAN_COUNT);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                final Type type = recyclerViewAdapter.getType(position);
                if (type.equals(RentPopItem.class) || type.equals(RZoneItem.class)) {
                    return 1;
                }
                return 3;
            }
        });
        return mLayoutManager;
    }


    private ViewBinder<?> getHeader() {
        return new ViewBinder<>(
                R.layout.recycler_header_popular,
                HeaderItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_header_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .setOnClickListener(R.id.tv_view_all, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getReferenceZone() {
        return new ViewBinder<>(
                R.layout.recycler_ref_zone_item,
                RZoneItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_ref_zone, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.iv_ref_zone, (ViewProvider<AppCompatImageView>) view -> GlideApp.with(getView()).load(model.getmImage()).into(view))
                        .setOnClickListener(R.id.ll_ref_zone_content, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getRentPopItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentPopItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view ->
                                GlideApp.with(getView()).load(model.getmImage()).placeholder(R.drawable.placeholder).into(view))
                        .setOnClickListener(R.id.cl_rent_home_content, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getRentNewItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentNewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view ->
                                GlideApp.with(getView()).load(model.getmImage()).placeholder(R.drawable.placeholder).into(view))
                        .setOnClickListener(R.id.cl_rent_home_content, (v -> mListener.onItemClick(v, model)))
        );
    }


    @Override
    public void onDestroyView() {
        fragmentHomeBinding.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //TESTING


    public void testingRecycleView() {
        fragmentHomeBinding.recyclerView.setLayoutManager(setupLayoutManager());
        fragmentHomeBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
        loadData();
    }

    public void loadData() {
        disposable = homeViewModel.getAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<Resource<List<ViewModel>>>() {
                    @Override
                    public void onNext(Resource<List<ViewModel>> listResource) {
                        setItemsAdapter(listResource.data);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public void setItemsAdapter(List<ViewModel> rZoneItems) {
        recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getHeader());
        recyclerViewAdapter.registerRenderer(testComposite());
        recyclerViewAdapter.setItems(rZoneItems);
        fragmentHomeBinding.recyclerView.setAdapter(recyclerViewAdapter);

        fragmentHomeBinding.slShimmerHome.stopShimmer();
        fragmentHomeBinding.setIsLoading(false);

    }

    @NonNull
    private CompositeViewBinder<?> testComposite() {
        return (CompositeViewBinder) new CompositeViewBinder<>(
                R.layout.recycler_composite,
                R.id.recycler_view,
                RecyclerViewItem.class,
                Collections.singletonList(new BetweenSpacesItemDecoration(2, 0)),
                new CompositeViewStateProvider<RecyclerViewItem, CompositeViewHolder>() {
                    @Override
                    public ViewState createViewState(@NonNull final CompositeViewHolder holder) {
                        return new CompositeViewState(holder);
                    }

                    @Override
                    public int createViewStateID(@NonNull final RecyclerViewItem model) {
                        return model.getID();
                    }
                }
        ).registerRenderer(getReferenceZone())
                .registerRenderer(getRentPopItem(R.layout.recycler_rent_pop_item))
                .registerRenderer(getRentNewItem(R.layout.recycler_rent_new_item));

    }


}
