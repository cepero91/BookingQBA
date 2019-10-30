package com.infinitum.bookingqba.view.adapters.items.rentlist;

import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;

public class RentListViewHolder extends RecyclerView.ViewHolder {

    private RecyclerRentListItemBinding itemBinding;


    public RentListViewHolder(@NonNull RecyclerRentListItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public void bind(GeoRent item, @Nullable Location userLocation) {
        itemBinding.setItem(item);
        if(userLocation != null){
            itemBinding.setActivateDistance(true);
            itemBinding.setHumanDistance(item.getHumanDistanceBetween(userLocation));
        }else{
            itemBinding.setActivateDistance(false);
            itemBinding.setHumanDistance("");
        }
        itemBinding.executePendingBindings();
    }

    public void clear() {
        itemBinding.setItem(null);
        itemBinding.setActivateDistance(false);
        itemBinding.setHumanDistance("");
    }
}
