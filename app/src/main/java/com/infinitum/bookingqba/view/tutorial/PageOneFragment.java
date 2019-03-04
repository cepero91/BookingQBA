package com.infinitum.bookingqba.view.tutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageOneFragment extends Fragment {


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_one, container, false);
    }

}
