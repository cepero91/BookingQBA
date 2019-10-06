package com.infinitum.bookingqba.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.RecyclerBookReservationItemBinding;
import com.infinitum.bookingqba.databinding.RecyclerUserBookReservationInfoItemBinding;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.BookRequestInfo;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;

import java.util.List;

public class UserBookRequestInfoAdapter extends RecyclerView.Adapter<UserBookRequestInfoAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<BookRequestInfo> requestInfos;
    private UserBookRequestInteraction userBookRequestInteraction;

    public UserBookRequestInfoAdapter(LayoutInflater inflater, List<BookRequestInfo> requestInfos, UserBookRequestInteraction userBookRequestInteraction) {
        this.inflater = inflater;
        this.requestInfos = requestInfos;
        this.userBookRequestInteraction = userBookRequestInteraction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerUserBookReservationInfoItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.recycler_user_book_reservation_info_item, viewGroup, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        BookRequestInfo item = requestInfos.get(i);
        if (item == null) {
            myViewHolder.unbind();
        } else {
            myViewHolder.itemBinding.setItem(item);
            myViewHolder.itemView.setOnClickListener(v -> userBookRequestInteraction.onBookReservationClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return requestInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerUserBookReservationInfoItemBinding itemBinding;

        public MyViewHolder(@NonNull RecyclerUserBookReservationInfoItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(BookRequestInfo item) {
            itemBinding.setItem(item);
            itemBinding.executePendingBindings();
        }

        public void unbind() {
            itemBinding.setItem(null);
        }
    }

    public interface UserBookRequestInteraction {
        void onBookReservationClick(BookRequestInfo item);
    }
}
