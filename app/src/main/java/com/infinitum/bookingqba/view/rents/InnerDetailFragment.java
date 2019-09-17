package com.infinitum.bookingqba.view.rents;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentInnerDetailBinding;
import com.infinitum.bookingqba.view.adapters.RDGalleryAdapter;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentAmenitieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentInnerDetail;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.thekhaeng.pushdownanim.PushDown;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        innerDetailBinding.setByNight(View.GONE);
        innerDetailBinding.setByHours(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        if(rentInnerDetail.getRentMode().equalsIgnoreCase("por noche")){
            innerDetailBinding.setByNight(View.VISIBLE);
            innerDetailBinding.setByHours(View.GONE);
        }else if(rentInnerDetail.getRentMode().equalsIgnoreCase("por horas")){
            innerDetailBinding.setByNight(View.GONE);
            innerDetailBinding.setByHours(View.VISIBLE);
        }
        innerDetailBinding.setDetail(rentInnerDetail);
        setupAmenitiesAdapter(rentInnerDetail.getAmenitieItems());
        setupGalerieAdapter(rentInnerDetail.getGalerieItems());

//        innerDetailBinding.llContentAddress.setOnClickListener(this);
//        innerDetailBinding.llBtnMovile.setOnClickListener(this);
//        innerDetailBinding.llBtnSms.setOnClickListener(this);
//        innerDetailBinding.llBtnHome.setOnClickListener(this);
//        innerDetailBinding.llBtnEmail.setOnClickListener(this);
//        innerDetailBinding.llBookRequest.setOnClickListener(this);
        innerDetailBinding.llPrice.setOnClickListener(this);
        PushDownAnim.setPushDownAnimTo(innerDetailBinding.btnBook).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InnerDetailInteraction) {
            this.innerDetailInteraction = (InnerDetailInteraction) context;
        }
    }


    private void setupAmenitiesAdapter(ArrayList<RentAmenitieItem> argAmenities) {
        if (argAmenities != null && argAmenities.size() > 0) {
            innerDetailBinding.fbAmenities.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (RentAmenitieItem rentAmenitieItem : argAmenities) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.recycler_rent_detail_amenities_item, null);
                textView.setText(rentAmenitieItem.getmName());
                params.setMargins(5, 5, 5, 5);
                textView.setLayoutParams(params);
                innerDetailBinding.fbAmenities.addView(textView);
            }
        }
    }

    private void setupGalerieAdapter(ArrayList<RentGalerieItem> argGaleries) {
        if (argGaleries != null && argGaleries.size() > 0) {
            RDGalleryAdapter adapter = new RDGalleryAdapter(argGaleries, (InnerDetailInteraction) getActivity());
            innerDetailBinding.rvGalery.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            innerDetailBinding.rvGalery.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(innerDetailBinding.rvGalery, false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_price:
                innerDetailInteraction.onDrawChangeClick();
                break;
            case R.id.btn_book:
                innerDetailInteraction.onBookRequestClick();
                break;
//            case R.id.ll_content_address:
//                createGeoRent();
//                break;
//            case R.id.ll_btn_movile:
//                innerDetailInteraction.phoneCallClick(rentInnerDetail.getPersonalPhone());
//                break;
//            case R.id.ll_btn_sms:
//                innerDetailInteraction.phoneSMSClick(rentInnerDetail.getPersonalPhone());
//                break;
//            case R.id.ll_btn_home:
//                innerDetailInteraction.phoneHomeClick(rentInnerDetail.getHomePhone());
//                break;
//            case R.id.ll_btn_email:
//                innerDetailInteraction.phoneEmailClick(rentInnerDetail.getEmail());
//                break;
//            case R.id.ll_book_request:
//                innerDetailInteraction.onBookRequestClick();
//                break;
        }
    }

    private void createGeoRent() {
        GeoRent geoRent = new GeoRent(rentInnerDetail.getId(), rentInnerDetail.getName(), rentInnerDetail.firstImage());
        geoRent.setRentMode(rentInnerDetail.getRentMode());
        geoRent.setRating(rentInnerDetail.getRating());
        geoRent.setGeoPoint(new GeoPoint(rentInnerDetail.getLatitude(), rentInnerDetail.getLongitude()));
        geoRent.setPrice(rentInnerDetail.getPrice());
        ArrayList<GeoRent> geoRents = new ArrayList<>();
        geoRents.add(geoRent);
        innerDetailInteraction.onAddressClick(geoRents);
    }
}
