package com.infinitum.bookingqba.view.adapters.rent;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.GlideApp;
import com.willy.ratingbar.BaseRatingBar;

public class RentListViewHolder extends RecyclerView.ViewHolder {

    private TextView tvName;
    private TextView tvAddress;
    private AppCompatImageView ivImage;
    private BaseRatingBar baseRatingBar;
    private TextView tvPrice;
    private View itemView;


    public RentListViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        tvName = itemView.findViewById(R.id.tv_name);
        tvAddress = itemView.findViewById(R.id.tv_address);
        ivImage = itemView.findViewById(R.id.iv_rent);
        baseRatingBar = itemView.findViewById(R.id.sr_scale_rating);
        tvPrice = itemView.findViewById(R.id.tv_price);
    }

    public void bind(RentListItem item) {
        tvName.setText(item.getmName());
        tvAddress.setText(item.getmAddress());
        tvPrice.setText(String.valueOf(item.getmPrice()));
        GlideApp.with(itemView).load(item.getImageByte()).placeholder(R.drawable.placeholder).into(ivImage);
        baseRatingBar.setRating(item.getRating());
    }

    public void clear() {
        tvName.setText(null);
        tvAddress.setText(null);
        tvPrice.setText(null);
        tvPrice.setText(null);
    }
}
