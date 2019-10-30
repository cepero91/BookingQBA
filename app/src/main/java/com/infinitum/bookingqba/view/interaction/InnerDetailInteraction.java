package com.infinitum.bookingqba.view.interaction;

import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentCommentItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentOfferItem;

import java.util.ArrayList;
import java.util.List;

public interface InnerDetailInteraction {

    void onGaleryClick(String id, ArrayList<RentGalerieItem> rentGalerieItems);

    void onAddressClick(ArrayList<GeoRent> geoRentList);

    void onCommentClick(String rentUuid, String rentOwnerId,ArrayList<RentCommentItem> argComment);

    void onOfferClick(ArrayList<RentOfferItem> argOffer);

    void updateWishedValue(int wished);

    void phoneCallClick(String phone);

    void phoneSMSClick(String phone);

    void phoneHomeClick(String phone);

    void phoneEmailClick(String email);

    void onBookRequestClick(int maxCapability);

    void onVoteClick(String userOwner);

    void onDrawChangeClick();

    void onPoiPlacesClick(String rentUuid, String referenceZone, String latLon);

}
