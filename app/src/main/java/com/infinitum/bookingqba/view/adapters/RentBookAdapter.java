package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerBookReservationItemBinding;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;

import java.util.List;

import timber.log.Timber;

public class RentBookAdapter extends RecyclerView.Adapter<RentBookAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<ReservationItem> reservationItemList;
    private RentBookInteraction rentBookInteraction;
    private ReservationType reservationType;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public RentBookAdapter(LayoutInflater inflater, List<ReservationItem> reservationItemList, RentBookInteraction rentBookInteraction, ReservationType reservationType) {
        this.inflater = inflater;
        this.reservationItemList = reservationItemList;
        this.rentBookInteraction = rentBookInteraction;
        this.reservationType = reservationType;
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
            binderHelper.bind(myViewHolder.itemBinding.swipeLayout,item.getId());
            myViewHolder.bind(item);
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
            itemBinding.deleteLayout.setOnClickListener(v -> {
                rentBookInteraction.onBookReservationDelete(item);
                reservationItemList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });
            itemBinding.clFront.setOnClickListener(v -> {
                Timber.e("Entro");
                rentBookInteraction.onBookReservationClick(item,reservationType);
            });
            itemBinding.executePendingBindings();
        }

        public void unbind() {
            itemBinding.setItem(null);
        }
    }

    public interface RentBookInteraction {
        void onBookReservationClick(ReservationItem item, ReservationType reservationType);
        void onBookReservationDelete(ReservationItem item);
    }
}
