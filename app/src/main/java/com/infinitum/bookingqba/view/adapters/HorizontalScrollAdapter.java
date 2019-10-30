package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.adapters.items.horizontal.HorizontalItem;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {

    private List<HorizontalItem> horizontalItems;

    public HorizontalScrollAdapter(List<HorizontalItem> horizontalItems) {
        this.horizontalItems = horizontalItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.horizontal_text_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HorizontalItem item = horizontalItems.get(i);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        return horizontalItems.size();
    }

    public HorizontalItem getItem(int position){
        return horizontalItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRentName;
        private RoundedImageView ivRent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvRentName = itemView.findViewById(R.id.tv_rent_name);
            this.ivRent = itemView.findViewById(R.id.iv_rent);
        }

        public void bind(HorizontalItem item) {
            this.tvRentName.setText(item.getRentName());
            Picasso.get()
                    .load(item.getPortrait())
                    .resize(420, 280)
                    .placeholder(R.drawable.placeholder)
                    .into(this.ivRent);
        }

        public void showText(){
            this.tvRentName.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }

        public void hideText(){
            this.tvRentName.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
    }
}
