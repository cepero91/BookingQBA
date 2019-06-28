package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class FragmentStepAdapter extends AbstractFragmentStepAdapter {

    private Context context;

    public FragmentStepAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
        this.context = context;
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return FirstStepFragment.newInstance();
            case 1:
                return SecondStepFragment.newInstance();
            case 2:
                return ThreeStepFragment.newInstance();
            case 3:
                return FourStepFragment.newInstance();
            default:
                return FirstStepFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        StepViewModel.Builder builder = new StepViewModel.Builder(this.context);
        switch (position) {
            case 0:
                builder.setEndButtonLabel("Siguiente");
                break;
            case 1:
                builder.setEndButtonLabel("Siguiente");
                builder.setBackButtonLabel("Atras");
                break;
            case 2:
                builder.setEndButtonLabel("Siguiente");
                builder.setBackButtonLabel("Atras");
                break;
            case 3:
                builder.setEndButtonLabel("Terminar");
                builder.setBackButtonLabel("Atras");
        }
        return builder.create();
    }
}
