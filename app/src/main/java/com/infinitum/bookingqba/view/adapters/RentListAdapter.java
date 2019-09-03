package com.infinitum.bookingqba.view.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentListItemBinding;
import com.infinitum.bookingqba.util.FilteringUtil;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListViewHolder;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

import java.util.ArrayList;
import java.util.List;

public class RentListAdapter extends RecyclerView.Adapter<RentListViewHolder> implements Filterable {

    private LayoutInflater inflater;
    private FragmentNavInteraction listener;
    private List<GeoRent> filteredList;
    private List<GeoRent> itemList;


    public RentListAdapter(LayoutInflater inflater, FragmentNavInteraction listener, List<GeoRent> itemList) {
        this.inflater = inflater;
        this.listener = listener;
        this.itemList = itemList;
        this.filteredList = itemList;
    }

    @NonNull
    @Override
    public RentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerRentListItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.recycler_rent_list_item, viewGroup, false);
        return (new RentListViewHolder(itemBinding));
    }

    @Override
    public void onBindViewHolder(@NonNull RentListViewHolder rentListViewHolder, int i) {
        if (filteredList.size() > 0) {
            GeoRent item = filteredList.get(i);
            if (item == null) {
                rentListViewHolder.clear();
            } else {
                rentListViewHolder.bind(item);
                rentListViewHolder.itemView.setOnClickListener(v -> listener.onItemClick(v, item));
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredList != null ? filteredList.size() : 0;
    }

    public List<GeoRent> getFilteredList() {
        return filteredList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = itemList;
                } else {
                    filteredList = FilteringUtil.searchGeoRentFilter(itemList, charString);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<GeoRent>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
