package com.infinitum.bookingqba.view.rents;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerDetailBinding;
import com.infinitum.bookingqba.util.GlideApp;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentNumber;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerDetailFragment extends Fragment {

    FragmentInnerDetailBinding innerDetailBinding;

    private static final String ARG_DESC = "argDesc";
    private static final String ARG_RULES = "argRules";
    private static final String ARG_NUMBER = "argNumber";
    private static final String ARG_POIS = "argPois";
    private static final String ARG_AMENITIES = "argAmenities";
    private static final String ARG_GALERIES = "argGaleries";

    private String argDesc;
    private String argRules;
    private RentNumber argNumber;
    private ArrayList<RentPoiItem> argPois;
    private ArrayList<RentAmenitieItem> argAmenities;
    private ArrayList<RentGalerieItem> argGaleries;


    public InnerDetailFragment() {
        // Required empty public constructor
    }

    public static InnerDetailFragment newInstance(String argDesc, String argRules, RentNumber rentNumber, ArrayList<RentPoiItem> argPois, ArrayList<RentAmenitieItem> argAmenities, ArrayList<RentGalerieItem> argGaleries) {
        InnerDetailFragment fragment = new InnerDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESC, argDesc);
        args.putString(ARG_RULES, argRules);
        args.putParcelable(ARG_NUMBER, rentNumber);
        args.putParcelableArrayList(ARG_POIS, argPois);
        args.putParcelableArrayList(ARG_AMENITIES, argAmenities);
        args.putParcelableArrayList(ARG_GALERIES, argGaleries);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argDesc = getArguments().getString(ARG_DESC);
            argRules = getArguments().getString(ARG_RULES);
            argNumber = getArguments().getParcelable(ARG_NUMBER);
            argPois = getArguments().getParcelableArrayList(ARG_POIS);
            argAmenities = getArguments().getParcelableArrayList(ARG_AMENITIES);
            argGaleries = getArguments().getParcelableArrayList(ARG_GALERIES);
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

        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        innerDetailBinding.setDescription(argDesc);
        innerDetailBinding.setRules(argRules);
        innerDetailBinding.setNumber(argNumber);
        setupAmenitiesAdapter(argAmenities);
        setupPoisAdapter(argPois);
        setupGalerieAdapter(argGaleries);
    }

    private void setupPoisAdapter(ArrayList<RentPoiItem> argPois) {
        if(argPois!=null && argPois.size()>0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getPoiVinder());
            adapter.setItems(argPois);
            innerDetailBinding.rvPois.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            innerDetailBinding.setPois(adapter);
        }
    }

    private void setupAmenitiesAdapter(ArrayList<RentAmenitieItem> argAmenities) {
        if(argAmenities!=null && argAmenities.size()>0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getAmenitiesVinder());
            adapter.setItems(argAmenities);
            innerDetailBinding.rvAmenities.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            innerDetailBinding.setAmenities(adapter);
        }
    }

    private void setupGalerieAdapter(ArrayList<RentGalerieItem> argGaleries) {
        if(argGaleries!=null && argGaleries.size()>0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getGalerieVinder());
            adapter.setItems(argGaleries);
            innerDetailBinding.rvGaleries.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            innerDetailBinding.setGaleries(adapter);
        }
    }

    private ViewBinder<?> getPoiVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_pois_item,
                RentPoiItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.iv_icon, (ViewProvider<AppCompatImageView>) view -> GlideApp.with(getView()).load(model.getIconByte()).into(view))
        );
    }

    private ViewBinder<?> getAmenitiesVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_amenities_item,
                RentAmenitieItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
        );
    }

    private ViewBinder<?> getGalerieVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_galeries_item,
                RentGalerieItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.iv_galerie, (ViewProvider<RoundedImageView>) view -> GlideApp.with(getView()).load(model.getImage()).placeholder(R.drawable.placeholder).into(view))
        );
    }
}
