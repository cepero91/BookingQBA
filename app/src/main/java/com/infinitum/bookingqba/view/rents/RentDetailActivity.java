package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityRentDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieName;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.view.adapters.rendered.rentdetail.RentDetailAmenitieItem;
import com.infinitum.bookingqba.view.adapters.rentdetail.InnerViewPagerAdapter;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RentDetailActivity extends AppCompatActivity {

    private ActivityRentDetailBinding rentDetailBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel viewModel;
    private String rentUuid = "";

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        rentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_detail);

        compositeDisposable = new CompositeDisposable();

        if (getIntent().getExtras() != null)
            rentUuid = getIntent().getExtras().getString("uuid");

        setupToolbar();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        disposable = viewModel.getRentDetailById(rentUuid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess,this::onError);
        compositeDisposable.add(disposable);

    }

    private void onError(Throwable throwable) {
        Timber.e(throwable);
    }

    private void onSuccess(Resource<RentDetail> rentDetailResource) {
        rentDetailBinding.setRentDetail(rentDetailResource.data);
        setupAmenitiesAdapter(rentDetailResource.data);
        setupViewPager(rentDetailResource.data);
    }

    private void setupToolbar() {
        setSupportActionBar(rentDetailBinding.toolbar);
        getSupportActionBar().setTitle("");
    }

    private void setupViewPager(RentDetail rentDetail) {
        InnerViewPagerAdapter innerViewPagerAdapter = new InnerViewPagerAdapter(getSupportFragmentManager());
        Fragment innerDetail = InnerDetailFragment.newInstance(rentDetail.getDescription(),new ArrayList<>());
        Fragment innerRules = InnerRulesFragment.newInstance(rentDetail.getRules());
        innerViewPagerAdapter.addFragment(innerDetail,"Detalles");
        innerViewPagerAdapter.addFragment(innerRules,"Reglas");
        rentDetailBinding.vpPages.setAdapter(innerViewPagerAdapter);
        rentDetailBinding.tlTab.setupWithViewPager(rentDetailBinding.vpPages);
        rentDetailBinding.executePendingBindings();
    }

    private void setupAmenitiesAdapter(RentDetail rentDetail){
        List<RentAmenitieName>rentAmenitieNames = rentDetail.getAmenitieNames();
        if(rentAmenitieNames!=null && rentAmenitieNames.size()>0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getAmenitieVinder());
            List<RentDetailAmenitieItem> detailAmenitieItems = new ArrayList<>();
            for (RentAmenitieName item : rentAmenitieNames) {
                detailAmenitieItems.add(new RentDetailAmenitieItem((String) item.getAmenityName().toArray()[0]));
            }
            adapter.setItems(detailAmenitieItems);
            rentDetailBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rentDetailBinding.setAdapter(adapter);
        }
    }

    private ViewBinder<?> getAmenitieVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_amenities_item,
                RentDetailAmenitieItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
