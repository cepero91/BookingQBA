package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentMostCommentedItemBinding;
import com.infinitum.bookingqba.databinding.RecyclerRentMostRatingItemBinding;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostCommentItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostRatingItem;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

import java.util.List;

public class MostRatingRentAdapter extends RecyclerView.Adapter<MostRatingRentAdapter.MyViewHolder>{

    private List<BaseItem> rentMostRatingItems;
    private LayoutInflater inflater;
    private FragmentNavInteraction mListener;

    public MostRatingRentAdapter(List<BaseItem> rentMostRatingItems, LayoutInflater inflater, FragmentNavInteraction mListener) {
        this.rentMostRatingItems = rentMostRatingItems;
        this.inflater = inflater;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerRentMostRatingItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_rent_most_rating_item, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        RentMostRatingItem item = (RentMostRatingItem) rentMostRatingItems.get(i);
        if(item == null){
            myViewHolder.clear();
        }else{
            myViewHolder.bind(item);
            myViewHolder.itemView.setOnClickListener(v -> mListener.onItemClick(v,item));
        }
    }

    @Override
    public int getItemCount() {
        return rentMostRatingItems.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RecyclerRentMostRatingItemBinding binding;

        public MyViewHolder(@NonNull RecyclerRentMostRatingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RentMostRatingItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }

        public void clear() {
            binding.setItem(null);
        }
    }
}
