package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerBookReservationItemBinding;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;

import java.util.List;

public class RentBookAdapter extends RecyclerView.Adapter<RentBookAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<ReservationItem> reservationItemList;
    private RentBookInteraction rentBookInteraction;

    public RentBookAdapter(LayoutInflater inflater, List<ReservationItem> reservationItemList, RentBookInteraction rentBookInteraction) {
        this.inflater = inflater;
        this.reservationItemList = reservationItemList;
        this.rentBookInteraction = rentBookInteraction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerBookReservationItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.recycler_book_reservation_item, viewGroup, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ReservationItem item = reservationItemList.get(i);
        if (item == null) {
            myViewHolder.unbind();
        } else {
            myViewHolder.itemBinding.setItem(item);
            myViewHolder.itemView.setOnClickListener(v -> rentBookInteraction.onBookReservationClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return reservationItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerBookReservationItemBinding itemBinding;

        public MyViewHolder(@NonNull RecyclerBookReservationItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(ReservationItem item) {
            itemBinding.setItem(item);
            itemBinding.executePendingBindings();
        }

        public void unbind() {
            itemBinding.setItem(null);
        }
    }

    public interface RentBookInteraction {
        void onBookReservationClick(ReservationItem item);
    }
}
