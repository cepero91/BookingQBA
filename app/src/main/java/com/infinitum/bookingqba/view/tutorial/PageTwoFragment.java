package com.infinitum.bookingqba.view.tutorial;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.GlideApp;


public class PageTwoFragment extends Fragment {

    private AppCompatImageView imageView;

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
        View view = inflater.inflate(R.layout.fragment_page_two, container, false);
        imageView = view.findViewById(R.id.iv_use_case);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GlideApp.with(view.getContext()).load(R.drawable.compare_use_case).into(imageView);
    }
}
