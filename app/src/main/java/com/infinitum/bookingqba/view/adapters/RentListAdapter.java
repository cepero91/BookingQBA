package com.infinitum.bookingqba.view.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListViewHolder;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

public class RentListAdapter extends PagedListAdapter<GeoRent, RentListViewHolder> {

    private LayoutInflater inflater;
    private FragmentNavInteraction listener;

    public RentListAdapter(LayoutInflater inflater, FragmentNavInteraction listener) {
        super(RENT_DIFF);
        this.inflater = inflater;
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<GeoRent> RENT_DIFF = new DiffUtil.ItemCallback<GeoRent>() {
        @Override
        public boolean areItemsTheSame(@NonNull GeoRent oldItem, @NonNull GeoRent newItem) {
            return (oldItem.equals(newItem));
        }

        @Override
        public boolean areContentsTheSame(@NonNull GeoRent oldItem, @NonNull GeoRent newItem) {
            return areItemsTheSame(oldItem, newItem);
        }
    };

    @NonNull
    @Override
    public RentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerRentListItemBinding itemBinding = DataBindingUtil.inflate(inflater,R.layout.recycler_rent_list_item, viewGroup, false);
        return (new RentListViewHolder(itemBinding));
    }

    @Override
    public void onBindViewHolder(@NonNull RentListViewHolder rentListViewHolder, int i) {
        GeoRent item = getItem(i);
        if(item == null){
            rentListViewHolder.clear();
        }else{
            rentListViewHolder.bind(item);
            rentListViewHolder.itemView.setOnClickListener(v -> listener.onItemClick(v,item));
        }
    }

}
