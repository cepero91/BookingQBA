package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.geo.DistanceUtil;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import org.mapsforge.core.model.LatLong;

import java.util.List;

public class RDPoiAdapter extends RecyclerView.Adapter<RDPoiAdapter.MyViewHolder> {

    private List<RentPoiItem> poiItems;
    private LatLong rentLocation;

    public RDPoiAdapter(List<RentPoiItem> poiItems, LatLong rentLocation) {
        this.poiItems = poiItems;
        this.rentLocation = rentLocation;
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
        myViewHolder.tvDistance.setText(DistanceUtil.distanceBetween(rentLocation.latitude,rentLocation.longitude,
                poiItem.getLatitude(),poiItem.getLongitude()));
    }


    @Override
    public int getItemCount() {
        return poiItems.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPoi;
        private TextView tvDistance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPoi = itemView.findViewById(R.id.tv_poi);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }
    }

}
