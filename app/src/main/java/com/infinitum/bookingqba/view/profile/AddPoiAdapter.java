package com.infinitum.bookingqba.view.profile;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.util.CategoryUtil;
import com.infinitum.bookingqba.util.Constants;

import org.mapsforge.poi.storage.PoiCategory;
import org.mapsforge.poi.storage.PointOfInterest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AddPoiAdapter extends RecyclerView.Adapter<AddPoiAdapter.MyViewHolder> {
    private List<Poi> pointOfInterests;

    public AddPoiAdapter(List<Poi> pointOfInterests) {
        this.pointOfInterests = pointOfInterests;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_add_rent_poi_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Poi pointOfInterest = pointOfInterests.get(i);
        myViewHolder.tvPoiName.setText(pointOfInterest.getName());
        String distanceStr = pointOfInterest.getDistance() < 1 ?
                String.format("(a %.1f m)", pointOfInterest.getDistance() * 1000) :
                String.format("(a %.1f km)", pointOfInterest.getDistance());
        myViewHolder.tvDistance.setText(distanceStr);
    }

    @Override
    public int getItemCount() {
        return pointOfInterests.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPoiName;
        private TextView tvDistance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPoiName = itemView.findViewById(R.id.tv_poi);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }
    }
}
