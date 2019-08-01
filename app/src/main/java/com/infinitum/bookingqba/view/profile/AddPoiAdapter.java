package com.infinitum.bookingqba.view.profile;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;

import org.mapsforge.poi.storage.PoiCategory;
import org.mapsforge.poi.storage.PointOfInterest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AddPoiAdapter extends RecyclerView.Adapter<AddPoiAdapter.MyViewHolder> {
    private List<PointOfInterest> pointOfInterests;

    public AddPoiAdapter(List<PointOfInterest> pointOfInterests) {
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
        PointOfInterest pointOfInterest = pointOfInterests.get(i);
        StringBuilder categoryBuilder = new StringBuilder();
        Set<PoiCategory> poiCategorySet = pointOfInterest.getCategories();
        if(poiCategorySet.size() > 1){
            PoiCategory[] poiCategories = poiCategorySet.toArray(new PoiCategory[poiCategorySet.size()]);
            for (PoiCategory poiCategory : poiCategories) {
                if (!poiCategory.getTitle().equals("Address")) {
                    categoryBuilder.append(poiCategory.getTitle());
                    break;
                }
            }
        }else{
            categoryBuilder.append(pointOfInterest.getCategory().getTitle());
        }
        myViewHolder.tvCategory.setText(categoryBuilder.toString());
        myViewHolder.tvPoi.setText(pointOfInterest.getName("es"));
        if(i%2==0){
            myViewHolder.llContentPoi.setBackgroundColor(Color.parseColor("#ECEFF1"));
        }else{
            myViewHolder.llContentPoi.setBackgroundColor(Color.parseColor("#CFD8DC"));
        }
    }

    @Override
    public int getItemCount() {
        return pointOfInterests.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llContentPoi;
        private TextView tvCategory;
        private TextView tvPoi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llContentPoi = itemView.findViewById(R.id.ll_content_poi);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPoi = itemView.findViewById(R.id.tv_poi);
        }
    }
}
