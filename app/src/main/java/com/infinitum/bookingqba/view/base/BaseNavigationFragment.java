package com.infinitum.bookingqba.view.base;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.interaction.PageFourInteraction;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;


public abstract class BaseNavigationFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    protected FragmentNavInteraction mListener;

    public BaseNavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof FragmentNavInteraction) {
            mListener = (FragmentNavInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
