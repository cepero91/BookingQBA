package com.infinitum.bookingqba.view.adapters.rent;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vivchar.rendererrecyclerviewadapter.DiffCallback;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

public class RentPagerAdapter extends PagedListAdapter<RentListItem, RentListViewHolder> {

    private LayoutInflater inflater;
    private FragmentNavInteraction listener;

    public RentPagerAdapter(LayoutInflater inflater, FragmentNavInteraction listener) {
        super(RENT_DIFF);
        this.inflater = inflater;
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<RentListItem> RENT_DIFF = new DiffUtil.ItemCallback<RentListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull RentListItem oldItem, @NonNull RentListItem newItem) {
            return (oldItem.equals(newItem));
        }

        @Override
        public boolean areContentsTheSame(@NonNull RentListItem oldItem, @NonNull RentListItem newItem) {
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
        RentListItem item = getItem(i);
        if(item == null){
            rentListViewHolder.clear();
        }else{
            rentListViewHolder.bind(item);
            rentListViewHolder.itemView.setOnClickListener(v -> listener.onItemClick(v,item));
        }
    }
}
