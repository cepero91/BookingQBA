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

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentPageThreeBinding;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageThreeFragment extends Fragment {

    private FragmentPageThreeBinding pageThreeBinding;


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
        pageThreeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_page_three, container, false);
        return pageThreeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.get().load(R.drawable.destination_use_case).into(pageThreeBinding.ivUseCase);
    }
}
