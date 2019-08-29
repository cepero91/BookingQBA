package com.infinitum.bookingqba.view.interaction;

import android.view.View;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;

import java.util.List;

public interface FragmentNavInteraction {

    void onItemClick(View view, BaseItem baseItem);

    void onViewAllClick(char orderType);

    void shortCutToMap(List<GeoRent> geoRentList);
}
