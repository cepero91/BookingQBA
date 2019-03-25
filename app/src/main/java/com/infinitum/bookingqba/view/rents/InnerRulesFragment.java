package com.infinitum.bookingqba.view.rents;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerRulesBinding;


public class InnerRulesFragment extends Fragment {

    private FragmentInnerRulesBinding innerRulesBinding;

    private static final String ARG_RULES = "rules";

    private String argRules;


    public InnerRulesFragment() {
        // Required empty public constructor
    }

    public static InnerRulesFragment newInstance(String rules) {
        InnerRulesFragment fragment = new InnerRulesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RULES, rules);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argRules = getArguments().getString(ARG_RULES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerRulesBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_inner_rules, container, false);
        return innerRulesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        innerRulesBinding.setRules(argRules);
        innerRulesBinding.executePendingBindings();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
