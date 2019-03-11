package com.infinitum.bookingqba.view.rents;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    private Button mButton;

    private FilterInteraction interaction;


    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
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
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButton = getView().findViewById(R.id.testButton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interaction.pulse();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FilterInteraction){
            interaction = (FilterInteraction) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interaction = null;
    }
}
