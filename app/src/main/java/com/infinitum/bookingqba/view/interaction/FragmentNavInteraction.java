package com.infinitum.bookingqba.view.interaction;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

import java.util.List;

public interface FragmentNavInteraction {

    void onItemClick(View view, BaseItem baseItem);

}
