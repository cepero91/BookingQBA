package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import java.util.List;

public class MarkerDetailPoiAdapter extends RecyclerView.Adapter<MarkerDetailPoiAdapter.MyViewHolder> {

    private List<RentPoiItem> poiItems;
    private PoiClick poiClick;

    public MarkerDetailPoiAdapter(List<RentPoiItem> poiItems, PoiClick poiClick) {
        this.poiItems = poiItems;
        this.poiClick = poiClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_rent_detail_poi_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        RentPoiItem poiItem = poiItems.get(i);
        myViewHolder.tvPoi.setText(poiItem.getName());
        myViewHolder.itemView.setOnClickListener(v->{
            if(poiClick!=null)
                poiClick.onPoiClick(poiItem);
        });
    }


    @Override
    public int getItemCount() {
        return poiItems.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPoi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPoi = itemView.findViewById(R.id.tv_poi);
        }
    }

    public interface PoiClick {
        void onPoiClick(RentPoiItem poiItem);
    }

}
