package com.infinitum.bookingqba.view.adapters.rent;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.util.GlideApp;
import com.willy.ratingbar.BaseRatingBar;

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
