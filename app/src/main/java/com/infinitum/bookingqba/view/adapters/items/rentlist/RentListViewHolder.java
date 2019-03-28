package com.infinitum.bookingqba.view.adapters.items.rentlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;

public class RentListViewHolder extends RecyclerView.ViewHolder {

    private RecyclerRentListItemBinding itemBinding;


    public RentListViewHolder(@NonNull RecyclerRentListItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public void bind(RentListItem item) {
        itemBinding.setItem(item);
        itemBinding.executePendingBindings();
    }

    public void clear() {
        itemBinding.setItem(null);
    }
}
