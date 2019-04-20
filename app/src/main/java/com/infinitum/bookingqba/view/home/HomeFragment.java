package com.infinitum.bookingqba.view.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentHomeBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.baseitem.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.items.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentNewItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.items.home.RZoneItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.ProvinceSpinnerList;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;
import com.infinitum.bookingqba.viewmodel.HomeViewModel;
import com.willy.ratingbar.BaseRatingBar;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.PERMISSION;
import static com.infinitum.bookingqba.util.Constants.PERMISSION_GRANTED;
import static com.infinitum.bookingqba.util.Constants.PERMISSION_NOT_GRANTED;


public class HomeFragment extends BaseNavigationFragment {

    private static final int MAX_SPAN_COUNT = 3;

    private RendererRecyclerViewAdapter recyclerViewAdapter;
    private GridLayoutManager mLayoutManager;

    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel homeViewModel;
    private Disposable disposable;

    private ProvinceSpinnerList spinnerList;

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

        fragmentHomeBinding.setIsLoading(true);

        checkPermissions();

        loadProvinces();

        setupRecyclerView();

        fragmentHomeBinding.spinner.setTitle(getResources().getString(R.string.dialog_provinces_title));
        fragmentHomeBinding.spinner.setPositiveButton(getResources().getString(R.string.positive_buttom));


    }

    private void checkPermissions() {
        int permGranted = sharedPreferences.getInt(PERMISSION, 0);
        if (permGranted == 0 || permGranted == PERMISSION_NOT_GRANTED) {
            final PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    savePermissionToPreference(PERMISSION_GRANTED);
                    String deviceUniqueID = getDeviceUniversalID();
                    saveImeiToPreference(deviceUniqueID);
                    Timber.e("Device ID %s", deviceUniqueID);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    savePermissionToPreference(PERMISSION_NOT_GRANTED);
                }
            };

            TedPermission.with(getActivity())
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle(R.string.permission_dialog_title)
                    .setRationaleMessage(R.string.permission_dialog_message)
                    .setDeniedTitle(R.string.permission_denied_title)
                    .setDeniedMessage(R.string.permission_denied_message)
                    .setGotoSettingButtonText(R.string.permission_go_setting)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
                    .check();
        }

    }

    private void savePermissionToPreference(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PERMISSION, value);
        editor.apply();
    }


    private void saveImeiToPreference(String deviceUniqueID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMEI, deviceUniqueID);
        editor.apply();
    }

    @SuppressLint("MissingPermission")
    private String getDeviceUniversalID() {
        String deviceUniqueID = null;
        if (telephonyManager != null) {
            deviceUniqueID = telephonyManager.getDeviceId();
        }
        if (deviceUniqueID == null) {
            deviceUniqueID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueID;
    }

    private void loadProvinces() {
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        disposable = homeViewModel.getProvinces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProvinceLoad, Timber::e);
        compositeDisposable.add(disposable);
    }


    private void onProvinceLoad(Resource<ProvinceSpinnerList> listResource) {
        this.spinnerList = listResource.data;
        ArrayAdapter adapter = new ArrayAdapter<>(getView().getContext(), R.layout.spinner_text_layout, spinnerList.getArrayNames());
        fragmentHomeBinding.setEntries(adapter);
        fragmentHomeBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData(spinnerList.getUuidOnPos(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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

    public void setupRecyclerView() {
        ViewCompat.setNestedScrollingEnabled(fragmentHomeBinding.recyclerView, false);
        fragmentHomeBinding.recyclerView.setLayoutManager(setupLayoutManager());
        fragmentHomeBinding.recyclerView.addItemDecoration(new BetweenSpacesItemDecoration(2, 0));
    }


    public void loadData(String province) {
        disposable = homeViewModel.getAllItems(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> setItemsAdapter(listResource.data), Timber::e);
        compositeDisposable.add(disposable);
    }

    public void setItemsAdapter(List<ViewModel> rendererViewModelList) {
        recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getHeader());
        recyclerViewAdapter.registerRenderer(getCompositeBinder());
        recyclerViewAdapter.setItems(rendererViewModelList);
        fragmentHomeBinding.recyclerView.setAdapter(recyclerViewAdapter);
        fragmentHomeBinding.setIsLoading(false);
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
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format("$ %s", String.valueOf(model.getPrice()))))
                        .find(R.id.iv_rent, (ViewProvider<RoundedImageView>) view ->
                                GlideApp.with(getView()).load(model.getImagePath()).placeholder(R.drawable.placeholder).into(view))
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
                                GlideApp.with(getView()).load(model.getImagePath()).placeholder(R.drawable.placeholder).into(view))
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
