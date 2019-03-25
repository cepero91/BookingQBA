package com.infinitum.bookingqba.view.rents;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerDetailBinding;
import com.infinitum.bookingqba.model.local.pojo.RentAmenitieName;
import com.infinitum.bookingqba.view.adapters.rendered.rentdetail.RentDetailAmenitieItem;
import com.infinitum.bookingqba.view.adapters.rendered.rentdetail.RentDetailPoiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerDetailFragment extends Fragment {

    FragmentInnerDetailBinding innerDetailBinding;

    private static final String ARG_DESC = "argDesc";
    private static final String ARG_POIS = "argPois";

    private String argDesc;
    private ArrayList<String> argPois;


    public InnerDetailFragment() {
        // Required empty public constructor
    }

    public static InnerDetailFragment newInstance(String argDesc, ArrayList<String> argPois) {
        InnerDetailFragment fragment = new InnerDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESC, argDesc);
        args.putStringArrayList(ARG_POIS, argPois);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argDesc = getArguments().getString(ARG_DESC);
            argPois = getArguments().getStringArrayList(ARG_POIS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerDetailBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_inner_detail, container, false);
        return innerDetailBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        innerDetailBinding.setDescription(argDesc);
        setupAdapter(argPois);
        innerDetailBinding.executePendingBindings();
    }

    private void setupAdapter(ArrayList<String> argPois) {
        if(argPois!=null && argPois.size()>0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getPoiVinder());
            List<RentDetailPoiItem> rentDetailPoiItems = new ArrayList<>();
            for (String item : argPois) {
                rentDetailPoiItems.add(new RentDetailPoiItem(item));
            }
            adapter.setItems(rentDetailPoiItems);
            innerDetailBinding.rvPois.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            innerDetailBinding.setAdapter(adapter);
        }
    }

    private ViewBinder<?> getPoiVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_pois,
                RentDetailPoiItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
        );
    }
}
