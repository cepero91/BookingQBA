package com.infinitum.bookingqba.view.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentMyRentsBinding;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MyRentsFragment extends Fragment implements View.OnClickListener {

    private FragmentMyRentsBinding binding;
    private AddRentClick addRentClick;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel rentViewModel;

    public static final String USERID_ARGS = "userid";
    private String useridArgs;

    public MyRentsFragment() {
    }

    public static MyRentsFragment newInstance(String userid) {
        MyRentsFragment myRentsFragment = new MyRentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERID_ARGS, userid);
        myRentsFragment.setArguments(bundle);
        return new MyRentsFragment();
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
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        binding.fabAddRent.setOnClickListener(this);

        binding.setIsLoading(false);
        binding.setIsEmpty(true);
        binding.progressPvLinear.stop();

        rentsByUserId(useridArgs);
    }

    private void rentsByUserId(String useridArgs) {
        String token = getString(R.string.device);
        String userid = "1";
        disposable = rentViewModel.fetchRentByUserId(token, userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if(listResource.data!=null){
                        Toast.makeText(getActivity(), ""+listResource.data.size(), Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e);
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

    public interface AddRentClick {
        void onAddRentClick();
    }
}
