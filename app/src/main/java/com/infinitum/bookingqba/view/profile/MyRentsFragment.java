package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMyRentsBinding;
import com.infinitum.bookingqba.view.adapters.items.addrent.MyRentItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.viewmodel.RentFormViewModel;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class MyRentsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentMyRentsBinding binding;
    private AddRentClick addRentClick;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;


    private RentFormViewModel rentViewModel;

    public static final String USERID_ARGS = "userid";
    public static final String USER_TOKEN = "token";
    private String useridArgs;
    private String tokenArgs;

    public MyRentsFragment() {
    }

    public static MyRentsFragment newInstance(String userid, String token) {
        MyRentsFragment myRentsFragment = new MyRentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERID_ARGS, userid);
        bundle.putString(USER_TOKEN, token);
        myRentsFragment.setArguments(bundle);
        return myRentsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_rents, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.useridArgs = getArguments().getString(USERID_ARGS);
            this.tokenArgs = getArguments().getString(USER_TOKEN);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentFormViewModel.class);

        binding.fabAddRent.setOnClickListener(this);
        binding.srlRefresh.setOnRefreshListener(this);

        rentsByUserId();
    }

    private void rentsByUserId() {
        binding.srlRefresh.setRefreshing(true);
        binding.setIsEmpty(false);
        binding.setIsLoading(true);
        disposable = rentViewModel.fetchRentByUserId(tokenArgs, useridArgs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        binding.setIsEmpty(false);
                        setItemsAdapter(listResource.data);
                    } else {
                        binding.setIsEmpty(true);
                    }
                    binding.setIsLoading(false);
                    binding.srlRefresh.setRefreshing(false);
                }, throwable -> {
                    binding.setIsEmpty(true);
                    binding.setIsLoading(false);
                    binding.srlRefresh.setRefreshing(false);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AddRentClick) {
            this.addRentClick = (AddRentClick) context;
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void onDetach() {
        this.addRentClick = null;
        compositeDisposable.clear();
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_rent:
                addRentClick.onAddRentClick();
                break;
        }
    }

    @Override
    public void onRefresh() {
        rentsByUserId();
    }

    public interface AddRentClick {
        void onAddRentClick();

        void onEditRent(String uuid);
    }

    public void setItemsAdapter(List<MyRentItem> rendererViewModelList) {
        RendererRecyclerViewAdapter recyclerViewAdapter = new RendererRecyclerViewAdapter();
        recyclerViewAdapter.registerRenderer(getMyRentBinder(R.layout.recycler_my_rent_list));
        recyclerViewAdapter.setItems(rendererViewModelList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    private ViewBinder<?> getMyRentBinder(int layout) {
        return new ViewBinder<>(
                layout,
                MyRentItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_rent_name, (ViewProvider<TextView>) view -> view.setText(model.getName()))
                        .find(R.id.tv_rent_mode, (ViewProvider<TextView>) view -> view.setText(" /" + model.getRentMode()))
                        .find(R.id.tv_state, (ViewProvider<TextView>) view -> view.setText(model.isActive() ? "Activada" : "Desactivada"))
                        .find(R.id.tv_price, (ViewProvider<TextView>) view -> view.setText(String.format(String.format("$ %.2f", model.getPrice()))))
                        .find(R.id.siv_rent_image, (ViewProvider<RoundedImageView>) view -> {
                            String path = model.getPortrait();
                            Picasso.get()
                                    .load(path)
                                    .resize(THUMB_WIDTH, THUMB_HEIGHT)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(view);
                        })
                        .setOnClickListener(R.id.btn_edit, (v -> addRentClick.onEditRent(model.getUuid())))
        );
    }
}
