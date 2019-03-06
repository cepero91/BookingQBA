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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageThreeFragment extends Fragment {

    private AppCompatImageView imageView;


    public PageThreeFragment() {
        // Required empty public constructor
    }

    public static PageThreeFragment newInstance() {
        PageThreeFragment fragment = new PageThreeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_two, container, false);
        imageView = view.findViewById(R.id.iv_use_case);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GlideApp.with(view.getContext()).load(R.drawable.destination_use_case).into(imageView);
    }
}
