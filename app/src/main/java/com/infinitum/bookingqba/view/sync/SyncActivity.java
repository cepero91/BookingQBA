package com.infinitum.bookingqba.view.sync;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySyncBinding;
import com.infinitum.bookingqba.viewmodel.SyncViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity {

    private ActivitySyncBinding syncBinding;
    private SyncViewModel syncViewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncBinding = DataBindingUtil.setContentView(this,R.layout.activity_sync);

        syncViewModel = ViewModelProviders.of(this, viewModelFactory).get(SyncViewModel.class);
    }

}
