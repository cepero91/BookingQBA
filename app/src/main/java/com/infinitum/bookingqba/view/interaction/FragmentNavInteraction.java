package com.infinitum.bookingqba.view.interaction;

import android.view.View;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public interface FragmentNavInteraction {

    void onItemClick(View view, BaseItem baseItem);

    void onViewAllClick(char orderType);

}
