package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerListWishItemBinding;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.listwish.ListWishItem;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

import java.util.List;

public class WishRentAdapter extends RecyclerView.Adapter<WishRentAdapter.MyViewHolder>{

    private List<BaseItem> rentWishedList;
    private LayoutInflater inflater;
    private FragmentNavInteraction mListener;

    public WishRentAdapter(List<BaseItem> rentWishedList, LayoutInflater inflater, FragmentNavInteraction mListener) {
        this.rentWishedList = rentWishedList;
        this.inflater = inflater;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerListWishItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_list_wish_item, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ListWishItem item = (ListWishItem) rentWishedList.get(i);
        if(item == null){
            myViewHolder.clear();
        }else{
            myViewHolder.bind(item);
            myViewHolder.itemView.setOnClickListener(v -> mListener.onItemClick(v,item));
        }
    }

    @Override
    public int getItemCount() {
        return rentWishedList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RecyclerListWishItemBinding binding;

        public MyViewHolder(@NonNull RecyclerListWishItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ListWishItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }

        public void clear() {
            binding.setItem(null);
        }
    }
}
