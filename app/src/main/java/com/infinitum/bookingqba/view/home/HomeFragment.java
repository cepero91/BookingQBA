package com.infinitum.bookingqba.view.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.infinitum.bookingqba.view.adapters.SpinnerAdapter;
import com.infinitum.bookingqba.view.adapters.items.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.items.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentNewItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.items.home.RZoneItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.HomeViewModel;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.PROVINCE_SPINNER_INDEX;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;
import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;


public class HomeFragment extends BaseNavigationFragment {

    private static final int MAX_SPAN_COUNT = 3;

    private RendererRecyclerViewAdapter recyclerViewAdapter;
    private GridLayoutManager mLayoutManager;

    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel homeViewModel;
    private Disposable disposable;

    private CommonSpinnerList spinnerList;
    private SpinnerAdapter adapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TelephonyManager telephonyManager;

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

        loadProvinces();

        setupRecyclerView();

        fragmentHomeBinding.spinnerProvinces.setTitle(getResources().getString(R.string.dialog_provinces_title));
        fragmentHomeBinding.spinnerProvinces.setPositiveButton(getResources().getString(R.string.positive_buttom));

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
            adapter = new SpinnerAdapter(getActivity(), R.layout.spinner_text_layout, spinnerList.getArrayNames());
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

    public void setupRecyclerView() {
        ViewCompat.setNestedScrollingEnabled(fragmentHomeBinding.recyclerView, false);
        fragmentHomeBinding.recyclerView.setLayoutManager(setupLayoutManager());
        fragmentHomeBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
    }


    public void loadData(String provinceUuid) {
        disposable = homeViewModel.getAllItems(provinceUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> setItemsAdapter(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    public void setItemsAdapter(List<ViewModel> rendererViewModelList) {
        fragmentHomeBinding.setIsLoading(false);
        recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getHeader());
        recyclerViewAdapter.registerRenderer(getCompositeBinder());
        recyclerViewAdapter.setItems(rendererViewModelList);
        OverScrollDecoratorHelper.setUpOverScroll(fragmentHomeBinding.recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        fragmentHomeBinding.recyclerView.setAdapter(recyclerViewAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_fall_down);
        fragmentHomeBinding.recyclerView.setLayoutAnimation(animationController);
    }

    @NonNull
    private CompositeViewBinder<?> getCompositeBinder() {
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

    private ViewBinder<?> getHeader() {
        return new ViewBinder<>(
                R.layout.recycler_header_popular,
                HeaderItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_header_title, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .setOnClickListener(R.id.tv_view_all, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getReferenceZone() {
        return new ViewBinder<>(
                R.layout.recycler_ref_zone_item,
                RZoneItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_ref_zone, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.iv_ref_zone, (ViewProvider<AppCompatImageView>) view -> {
                            view.setImageBitmap(BitmapFactory.decodeByteArray(model.getImageByte(), 0, model.getImageByte().length));
                        })
                        .setOnClickListener(R.id.ll_ref_zone_content, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getRentPopItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentPopItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format("$ %.2f", model.getPrice())))
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view -> {
                                    String path;
                                    if (!model.getImagePath().contains("http")) {
                                        path = "file:" + model.getImagePath();
                                    } else {
                                        path = model.getImagePath();
                                    }
                                    Picasso.get().load(path)
                                            .fit()
                                            .placeholder(R.drawable.placeholder)
                                            .into(view);
                                }
                        ).setOnClickListener(R.id.cl_rent_home_content, (v -> mListener.onItemClick(v, model)))
        );
    }

    private ViewBinder<?> getRentNewItem(int layout) {
        return new ViewBinder<>(
                layout,
                RentNewItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_title, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.sr_scale_rating, (ViewProvider<BaseRatingBar>) view -> view.setRating(model.getRating()))
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view ->
                        {
                            String path;
                            if (!model.getImagePath().contains("http")) {
                                path = "file:" + model.getImagePath();
                            } else {
                                path = model.getImagePath();
                            }
                            Picasso.get()
                                    .load(path)
                                    .resize(THUMB_WIDTH,THUMB_HEIGHT)
                                    .placeholder(R.drawable.placeholder)
                                    .into(view);
                        })
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


}
