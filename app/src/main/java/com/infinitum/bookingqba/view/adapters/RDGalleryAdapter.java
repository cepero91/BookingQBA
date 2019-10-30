package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.infinitum.bookingqba.view.interaction.InnerDetailInteraction;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class RDGalleryAdapter extends RecyclerView.Adapter<RDGalleryAdapter.MyViewHolder> {

    List<RentGalerieItem> galerieItems;
    private GalleryInteration interaction;

    public RDGalleryAdapter(List<RentGalerieItem> galerieItems, GalleryInteration interaction) {
        this.galerieItems = galerieItems;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_rent_detail_galeries_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        RentGalerieItem galery = galerieItems.get(i);
        String path = "file:" + galery.getImage();
        Picasso.get()
                .load(path)
                .resize(THUMB_WIDTH, THUMB_HEIGHT)
                .placeholder(R.drawable.placeholder)
                .into(myViewHolder.ivRounde);
        myViewHolder.itemView.setOnClickListener(v->{
            interaction.onGalleryClick(galery.getId());
        });
    }


    @Override
    public int getItemCount() {
        return galerieItems.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView ivRounde;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRounde = itemView.findViewById(R.id.iv_galerie);
        }
    }

    public interface GalleryInteration{
        void onGalleryClick(String id);
    }

}
