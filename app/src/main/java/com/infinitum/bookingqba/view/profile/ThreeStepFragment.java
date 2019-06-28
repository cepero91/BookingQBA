package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentSecondStepBinding;
import com.infinitum.bookingqba.databinding.FragmentThreeStepBinding;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import dagger.android.support.AndroidSupportInjection;

public class ThreeStepFragment extends Fragment implements Step{

    private FragmentThreeStepBinding binding;
    private OnStepFormEnd onStepFormEnd;

    public ThreeStepFragment(){}

    public static ThreeStepFragment newInstance(){
        return new ThreeStepFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_three_step,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if(context instanceof OnStepFormEnd){
            onStepFormEnd = (OnStepFormEnd) context;
        }
    }

    @Override
    public void onDetach() {
        onStepFormEnd = null;
        super.onDetach();
    }

    //--------------------------------- STEP ---------------------------
    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
