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

public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {

    private ArrayList<HorizontalItem> horizontalItems;

    public HorizontalScrollAdapter(ArrayList<HorizontalItem> horizontalItems) {
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

    public String getItem(int position){
        return horizontalItems.get(position).getRentName();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private TextView tvRentName;
        private BaseRatingBar baseRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.ll_content_rent);
            this.tvRentName = itemView.findViewById(R.id.tv_rent_name);
            this.baseRatingBar = itemView.findViewById(R.id.sr_scale_rating);
        }

        public void bind(HorizontalItem item) {
            this.baseRatingBar.setRating(Float.parseFloat(item.getRating()));
            this.tvRentName.setText(item.getRentName());
        }

        public void showText(){
            this.linearLayout.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }

        public void hideText(){
            this.linearLayout.animate()
                    .alpha(0.6f)
                    .setDuration(500)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
    }
}
