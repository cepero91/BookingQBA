package com.infinitum.bookingqba.view.info;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInfoBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.base.BaseNavigationFragment;
import com.infinitum.bookingqba.view.interaction.InfoInteraction;
import com.infinitum.bookingqba.viewmodel.InformationViewModel;
import com.infinitum.bookingqba.viewmodel.RentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class InfoFragment extends BaseNavigationFragment implements View.OnClickListener {

    private FragmentInfoBinding infoBinding;
    private Disposable disposable;
    private InfoInteraction infoInteraction;
    private boolean canSync = false;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NetworkHelper networkHelper;

    private InformationViewModel informationViewModel;


    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        infoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false);
        return infoBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        informationViewModel = ViewModelProviders.of(this, viewModelFactory).get(InformationViewModel.class);

        infoBinding.btnSync.setOnClickListener(this);
        infoBinding.btnFeedback.setOnClickListener(this);
        infoBinding.btnPolitics.setOnClickListener(this);

        loadData();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InfoInteraction) {
            this.infoInteraction = (InfoInteraction) context;
        }
    }


    private void loadData() {
        disposable = Flowable.combineLatest(informationViewModel.lastLocalDatabaseUpdate(),
                informationViewModel.lastRemoteDatabaseUpdate(),
                this::checkCanSync)
                .map(this::convertToRelativeDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUi, Timber::e);
        compositeDisposable.add(disposable);
    }

    @NonNull
    private List<String> convertToRelativeDate(List<Resource<DatabaseUpdateEntity>> resources) {
        List<String> dateString = new ArrayList<>();
        for (Resource<DatabaseUpdateEntity> resource : resources) {
            if (resource.data != null) {
                TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(new Locale("es", "ES")).build();
                String dateRelative = TimeAgo.using(resource.data.getLastDateUpdateEntity().getTime(), messages);
                dateString.add(dateRelative);
            }else{
                dateString.add("No disponible");
            }
        }
        return dateString;
    }

    @NonNull
    private List<Resource<DatabaseUpdateEntity>> checkCanSync(Resource<DatabaseUpdateEntity> local, Resource<DatabaseUpdateEntity> remote) {
        if (local.data != null && remote.data != null) {
            if (local.data.getLastDateUpdateEntity().before(remote.data.getLastDateUpdateEntity())) {
                canSync = true;
            }
        }
        List<Resource<DatabaseUpdateEntity>> listCombine = new ArrayList<>();
        listCombine.add(local);
        listCombine.add(remote);
        return listCombine;
    }

    private void updateUi(List<String> strings) {
        infoBinding.setLocalUpdate(strings.get(0));
        infoBinding.setRemoteAvailable(strings.get(1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sync:
                if (networkHelper.isNetworkAvailable()) {
                    if(canSync) {
                        infoInteraction.startSync();
                    }else{
                        AlertUtils.showInfoAlert(getActivity(),"Los datos ya estan actualizados");
                    }
                }else{
                    AlertUtils.showErrorAlert(getActivity(),"Sin conexion");
                }
                break;
            case R.id.btn_politics:
                infoInteraction.showPoliticsDialog();
                break;
            case R.id.btn_feedback:
                infoInteraction.showFeedbackDialog();
                break;
        }
    }
}
