package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerMyRentListBinding;
import com.infinitum.bookingqba.databinding.RecyclerRentMostCommentedItemBinding;
import com.infinitum.bookingqba.view.adapters.items.addrent.MyRentItem;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostCommentItem;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;

import java.util.List;

public class MyRentAdapter extends RecyclerView.Adapter<MyRentAdapter.MyViewHolder>{

    private List<MyRentItem> myRentItemList;
    private LayoutInflater inflater;
    private MyRentListInteraction interaction;

    public MyRentAdapter(List<MyRentItem> myRentItemList, LayoutInflater inflater, MyRentListInteraction interaction) {
        this.myRentItemList = myRentItemList;
        this.inflater = inflater;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerMyRentListBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_my_rent_list, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        MyRentItem item = myRentItemList.get(i);
        if(item == null){
            myViewHolder.clear();
        }else{
            myViewHolder.bind(item);
            myViewHolder.editRent.setOnClickListener(v -> interaction.onEditClick(item.getUuid()));
        }
    }

    @Override
    public int getItemCount() {
        return myRentItemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RecyclerMyRentListBinding binding;
        private TextView editRent;

        public MyViewHolder(@NonNull RecyclerMyRentListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            editRent = binding.tvEdit;
        }

        public void bind(MyRentItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }

        public void clear() {
            binding.setItem(null);
        }
    }

    public interface MyRentListInteraction{
        void onEditClick(String uuid);
    }
}
