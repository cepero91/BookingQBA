package com.infinitum.bookingqba.view.adapters.items.rentlist;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;

public class RentListViewHolder extends RecyclerView.ViewHolder {

    private RecyclerRentListItemBinding itemBinding;


    public RentListViewHolder(@NonNull RecyclerRentListItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public void bind(GeoRent item) {
        itemBinding.setItem(item);
        itemBinding.executePendingBindings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemBinding.cvContentRentItem.setTransitionName(item.getId());
        }
    }

    public void clear() {
        itemBinding.setItem(null);
    }
}
