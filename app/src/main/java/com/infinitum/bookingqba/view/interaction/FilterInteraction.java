package com.infinitum.bookingqba.view.interaction;

import android.arch.paging.PagedList;
import android.location.Location;
import android.support.annotation.Nullable;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;

import java.util.List;
import java.util.Map;

public interface FilterInteraction {

    void closeFilter();

    void onFilterElement(Resource<List<GeoRent>> resourceResult, @Nullable Location lastLocationKnow);

    void onFilterClean();

}
