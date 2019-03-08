package com.infinitum.bookingqba.view.tutorial;


import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentPageOneBinding;
import com.infinitum.bookingqba.util.GlideApp;


import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageOneFragment extends Fragment {

    private FragmentPageOneBinding pageOneBinding;

    public PageOneFragment() {
        // Required empty public constructor
    }


    public static PageOneFragment newInstance() {
        PageOneFragment fragment = new PageOneFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pageOneBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_page_one,container,false);
        return pageOneBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
