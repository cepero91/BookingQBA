package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerRentMostCommentedItemBinding;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostCommentItem;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

import java.util.List;

public class MostCommentRentAdapter extends RecyclerView.Adapter<MostCommentRentAdapter.MyViewHolder>{

    private List<BaseItem> rentMostCommentItems;
    private LayoutInflater inflater;
    private FragmentNavInteraction mListener;

    public MostCommentRentAdapter(List<BaseItem> rentMostCommentItems, LayoutInflater inflater, FragmentNavInteraction mListener) {
        this.rentMostCommentItems = rentMostCommentItems;
        this.inflater = inflater;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerRentMostCommentedItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_rent_most_commented_item, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        RentMostCommentItem item = (RentMostCommentItem) rentMostCommentItems.get(i);
        if(item == null){
            myViewHolder.clear();
        }else{
            myViewHolder.bind(item);
            myViewHolder.itemView.setOnClickListener(v -> mListener.onItemClick(v,item));
        }
    }

    @Override
    public int getItemCount() {
        return rentMostCommentItems.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RecyclerRentMostCommentedItemBinding binding;

        public MyViewHolder(@NonNull RecyclerRentMostCommentedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RentMostCommentItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }

        public void clear() {
            binding.setItem(null);
        }
    }
}
