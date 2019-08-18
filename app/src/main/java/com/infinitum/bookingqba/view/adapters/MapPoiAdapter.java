package com.infinitum.bookingqba.view.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;

import java.util.List;

public class MapPoiAdapter extends RecyclerView.Adapter<MapPoiAdapter.MyViewHolder> {

    List<PoiItem> poiItems;
    private MapPoiClick mapPoiClick;
    private boolean[] selected;

    public MapPoiAdapter(List<PoiItem> poiItems, MapPoiClick mapPoiClick) {
        this.poiItems = poiItems;
        this.mapPoiClick = mapPoiClick;
        initBooleanArray(poiItems);
    }

    private void initBooleanArray(List<PoiItem> poiItems) {
        selected = new boolean[poiItems.size()];
        for (int i = 0; i < poiItems.size(); i++) {
            selected[i] = false;
        }
    }

    private void select(int pos, boolean checked) {
        selected[pos] = checked;
    }

    public void showAll() {
        for (int i = 0; i < poiItems.size(); i++) {
            selected[i] = true;
        }
        notifyDataSetChanged();
    }

    public void hideAll() {
        initBooleanArray(poiItems);
        notifyDataSetChanged();
    }

    public boolean isOnlyOneSelected() {
        int selectedCount = 0;
        for (Boolean item : selected) {
            if (item) {
                selectedCount++;
                if (selectedCount > 1)
                    break;
            }
        }
        return selectedCount == 1;
    }

    public PoiItem getOnlyOneSelected(){
        for (int i = 0; i < selected.length; i++) {
            if(selected[i]){
                return poiItems.get(i);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_map_poi_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        PoiItem poiItem = poiItems.get(i);
        myViewHolder.tvPoi.setText(poiItem.getName());
        myViewHolder.tvCategory.setText(poiItem.getCategory());
        if (selected[i]) {
            changeIconColor(myViewHolder, true);
        } else {
            changeIconColor(myViewHolder, false);
        }
        myViewHolder.llContentPoi.setOnClickListener(v -> {
            if (selected[i]) {
                select(i, false);
                mapPoiClick.onPoiSelected(poiItem, false);
                changeIconColor(myViewHolder, false);
            } else {
                select(i, true);
                mapPoiClick.onPoiSelected(poiItem, true);
                changeIconColor(myViewHolder, true);
            }

        });
    }

    private void changeIconColor(MyViewHolder myViewHolder, boolean b) {
        if (b) {
            myViewHolder.llContentPoi.setBackgroundResource(R.drawable.shape_filter_small_ship_selected);
            myViewHolder.tvCategory.setTextColor(Color.WHITE);
            myViewHolder.tvPoi.setTextColor(Color.WHITE);
        } else {
            myViewHolder.llContentPoi.setBackgroundResource(R.drawable.shape_filter_small_ship_unselected);
            myViewHolder.tvCategory.setTextColor(Color.parseColor("#9E9E9E"));
            myViewHolder.tvPoi.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    @Override
    public int getItemCount() {
        return poiItems.size();
    }

    public int posByUuid(String uuid) {
        for (int i = 0; i < poiItems.size(); i++) {
            if (poiItems.get(i).getId().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private TextView tvPoi;
        private LinearLayout llContentPoi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPoi = itemView.findViewById(R.id.tv_poi);
            llContentPoi = itemView.findViewById(R.id.ll_content_poi);
        }
    }

    public interface MapPoiClick {
        void onPoiSelected(PoiItem poiItem, boolean selected);
    }
}
