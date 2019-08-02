package com.infinitum.bookingqba.view.tutorial;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentPageTwoBinding;
import com.squareup.picasso.Picasso;


public class PageTwoFragment extends Fragment {

    private FragmentPageTwoBinding pageTwoBinding;

    public PageTwoFragment() {
        // Required empty public constructor
    }


    public static PageTwoFragment newInstance() {
        PageTwoFragment fragment = new PageTwoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pageTwoBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_page_two, container, false);
        return pageTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.get().load(R.drawable.compare_use_case).into(pageTwoBinding.ivUseCase);
    }


}
