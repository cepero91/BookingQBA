package com.infinitum.bookingqba.view.rents;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerDetailBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.squareup.picasso.Picasso;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerDetailFragment extends Fragment implements View.OnClickListener {

    FragmentInnerDetailBinding innerDetailBinding;

    public static final String RENT_INNER_DETAIL = "rentInnerDetail";
    private RentInnerDetail rentInnerDetail;

    private InnerDetailInteraction innerDetailInteraction;


    public InnerDetailFragment() {
        // Required empty public constructor
    }

    public static InnerDetailFragment newInstance(RentInnerDetail rentInnerDetail) {
        InnerDetailFragment fragment = new InnerDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(RENT_INNER_DETAIL, rentInnerDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rentInnerDetail = getArguments().getParcelable(RENT_INNER_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        innerDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_detail, container, false);
        return innerDetailBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(false);

        innerDetailBinding.llContentAddress.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        innerDetailBinding.setDetail(rentInnerDetail);
        setupAmenitiesAdapter(rentInnerDetail.getAmenitieItems());
        setupPoisAdapter(rentInnerDetail.getRentPoiItems());
        setupGalerieAdapter(rentInnerDetail.getGalerieItems());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InnerDetailInteraction) {
            this.innerDetailInteraction = (InnerDetailInteraction) context;
        }
    }

    private void setupPoisAdapter(ArrayList<RentPoiItem> argPois) {
        if (argPois != null && argPois.size() > 0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getPoiVinder());
            adapter.setItems(argPois);
            innerDetailBinding.rvPois.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            innerDetailBinding.setPois(adapter);
            ViewCompat.setNestedScrollingEnabled(innerDetailBinding.rvPois, false);
        }
    }

    private void setupAmenitiesAdapter(ArrayList<RentAmenitieItem> argAmenities) {
        if (argAmenities != null && argAmenities.size() > 0) {
            innerDetailBinding.fbAmenities.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for(RentAmenitieItem rentAmenitieItem: argAmenities){
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.recycler_rent_detail_amenities_item,null);
                textView.setText(rentAmenitieItem.getmName());
                params.setMargins(5,5,5,5);
                textView.setLayoutParams(params);
                innerDetailBinding.fbAmenities.addView(textView);
            }
        }
    }

    private void setupGalerieAdapter(ArrayList<RentGalerieItem> argGaleries) {
        if (argGaleries != null && argGaleries.size() > 0) {
            RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
            adapter.registerRenderer(getGalerieVinder());
            adapter.setItems(argGaleries);
            innerDetailBinding.rvGaleries.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            innerDetailBinding.setGaleries(adapter);
            ViewCompat.setNestedScrollingEnabled(innerDetailBinding.rvGaleries, false);
        }
    }

    private ViewBinder<?> getPoiVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_pois_item,
                RentPoiItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.tv_name, (ViewProvider<TextView>) view -> view.setText(model.getmName()))
                        .find(R.id.iv_icon, (ViewProvider<AppCompatImageView>) view -> {
                            view.setImageBitmap(BitmapFactory.decodeByteArray(model.getIconByte(),0,model.getIconByte().length));
                        }));
    }


    private ViewBinder<?> getGalerieVinder() {
        return new ViewBinder<>(
                R.layout.recycler_rent_detail_galeries_item,
                RentGalerieItem.class,
                (model, finder, payloads) -> finder
                        .find(R.id.iv_galerie, (ViewProvider<RoundedImageView>) view -> {
                            String path;
                            if (!model.getImage().contains("http")) {
                                path = "file:" + model.getImage();
                            } else {
                                path = model.getImage();
                            }
                            Picasso.get()
                                    .load(path)
                                    .resize(THUMB_WIDTH,THUMB_HEIGHT)
                                    .placeholder(R.drawable.placeholder)
                                    .into(view);
                        }).setOnClickListener(v -> {
                            innerDetailInteraction.onGaleryClick(model.getId());
                        })
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_content_address:
                createGeoRent();
        }
    }

    private void createGeoRent() {
        GeoRent geoRent = new GeoRent(rentInnerDetail.getId(), rentInnerDetail.getName(), rentInnerDetail.firstImage(), rentInnerDetail.getWished());
        geoRent.setRentMode(rentInnerDetail.getRentMode());
        geoRent.setRating(rentInnerDetail.getRating());
        geoRent.setGeoPoint(new GeoPoint(rentInnerDetail.getLatitude(), rentInnerDetail.getLongitude()));
        geoRent.setPrice(rentInnerDetail.getPrice());
        ArrayList<GeoRent> geoRents = new ArrayList<>();
        geoRents.add(geoRent);
        innerDetailInteraction.onAddressClick(geoRents);
    }
}
