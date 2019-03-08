package com.infinitum.bookingqba.view.tutorial;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;


public abstract class BasePageFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    protected PageFourInterface mListener;

    protected CompositeDisposable compositeDisposable;


    public BasePageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof PageFourInterface) {
            mListener = (PageFourInterface) context;
            compositeDisposable = new CompositeDisposable();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        compositeDisposable = null;
    }



}
