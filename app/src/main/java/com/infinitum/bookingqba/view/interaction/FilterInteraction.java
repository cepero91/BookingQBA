package com.infinitum.bookingqba.view.interaction;

import android.arch.paging.PagedList;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;

import java.util.List;
import java.util.Map;

public interface FilterInteraction {

    void onFilterElement(PagedList<RentListItem> resourceResult);

    void onFilterClean();

}
