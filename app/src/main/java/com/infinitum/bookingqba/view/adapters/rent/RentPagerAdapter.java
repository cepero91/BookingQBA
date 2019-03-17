package com.infinitum.bookingqba.view.adapters.rent;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.vivchar.rendererrecyclerviewadapter.DiffCallback;
import com.infinitum.bookingqba.R;

public class RentPagerAdapter extends PagedListAdapter<RentListItem, RentListViewHolder> {

    private LayoutInflater inflater;

    public RentPagerAdapter(LayoutInflater inflater) {
        super(RENT_DIFF);
        this.inflater = inflater;
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
        return (new RentListViewHolder(inflater.inflate(R.layout.recycler_rent_list_item, viewGroup, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RentListViewHolder rentListViewHolder, int i) {
        RentListItem item = getItem(i);
        if(item == null){
            rentListViewHolder.clear();
        }else{
            rentListViewHolder.bind(item);
        }
    }
}
