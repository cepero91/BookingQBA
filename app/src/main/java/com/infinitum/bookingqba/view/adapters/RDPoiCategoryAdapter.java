package com.infinitum.bookingqba.view.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;

public class RDPoiCategoryAdapter extends RecyclerView.Adapter<RDPoiCategoryAdapter.MyViewHolder> {

    PoiCategory[] poiCategories;
    private int lastSelectedCategory;
    private boolean[] selectedCategory;
    private PoiCategorySelection poiCategorySelection;

    public RDPoiCategoryAdapter(PoiCategory[] poiCategories, PoiCategorySelection poiCategorySelection) {
        this.poiCategories = poiCategories;
        this.poiCategorySelection = poiCategorySelection;
        lastSelectedCategory = 0;
        initSelectedCategory(poiCategories);
    }

    private void initSelectedCategory(PoiCategory[] poiCategories) {
        if (poiCategories.length > 0) {
            selectedCategory = new boolean[poiCategories.length];
            selectedCategory[0] = true;
            for (int i = 1; i < poiCategories.length; i++) {
                selectedCategory[i] = false;
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_poi_category_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        PoiCategory poiCategory = poiCategories[i];
        myViewHolder.tvCategory.setText(poiCategory.getName());
        myViewHolder.ivCategory.setImageResource(getImageResourceByCategory(poiCategory.getId()));
        if (selectedCategory[i]) {
            myViewHolder.tvCategory.setVisibility(View.VISIBLE);
            myViewHolder.tvCategory.animate().alpha(1)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator()).start();
            myViewHolder.tvCategory.setTextColor(Color.WHITE);
            myViewHolder.ivCategory.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            myViewHolder.clRoot.setBackgroundResource(R.drawable.shape_poi_ship_selected);
            myViewHolder.clRoot.setElevation(3f);
        } else {
            myViewHolder.tvCategory.setAlpha(0);
            myViewHolder.tvCategory.setVisibility(View.GONE);
            myViewHolder.tvCategory.setTextColor(Color.parseColor("#607D8B"));
            myViewHolder.ivCategory.setImageTintList(ColorStateList.valueOf(Color.parseColor("#607D8B")));
            myViewHolder.clRoot.setBackgroundResource(R.drawable.shape_poi_ship_unselected);
            myViewHolder.clRoot.setElevation(0f);
        }
        myViewHolder.itemView.setOnClickListener(v -> {
            changeTextCategoryVisibility(i);
            poiCategorySelection.onPoiCategorySelected(poiCategory, i);
        });
    }

    private void changeTextCategoryVisibility(int currentSelected) {
        if (lastSelectedCategory != currentSelected) {
            selectedCategory[lastSelectedCategory] = false;
            selectedCategory[currentSelected] = true;
            lastSelectedCategory = currentSelected;
            notifyDataSetChanged();
        }
    }

    private int getImageResourceByCategory(int category) {
        switch (category) {
            case 0:
                return R.drawable.ic_fa_cutlery_line;
            case 2:
                return R.drawable.ic_fa_flash_line;
            case 6:
                return R.drawable.ic_fa_beer_line;
            case 7:
                return R.drawable.ic_fa_coffee_line;
            case 9:
                return R.drawable.ic_fa_spoon_line;
            case 21:
                return R.drawable.ic_fa_automobile_line;
            case 26:
                return R.drawable.ic_fa_cab_line;
            case 29:
                return R.drawable.ic_fa_usd_line;
            case 30:
                return R.drawable.ic_fa_money_line;
            case 34:
                return R.drawable.ic_fa_hospital_line;
            case 41:
                return R.drawable.ic_fa_picture_o_line;
            case 42:
                return R.drawable.ic_fa_video_camera_line;
            case 45:
                return R.drawable.ic_fa_music_line;
            case 47:
                return R.drawable.ic_fa_building_line;
            case 154:
                return R.drawable.ic_fa_institution_line;
            case 157:
                return R.drawable.ic_fa_street_view_line;
            case 158:
                return R.drawable.ic_fa_camera_line;
            case 159:
                return R.drawable.ic_fa_camera_retro_line;
            case 164:
                return R.drawable.ic_fa_history_line;
            case 171:
                return R.drawable.ic_fa_anchor_line;
            case 173:
                return R.drawable.ic_fa_tree_line;
            case 212:
                return R.drawable.ic_fa_leaf_line;
            case 369:
                return R.drawable.ic_fa_diamond_line;
            case 381:
                return R.drawable.ic_fa_fire_line;
            default:
                return R.drawable.ic_fa_diamond_line;
        }
    }

    @Override
    public int getItemCount() {
        return poiCategories != null ? poiCategories.length : 0;
    }

    public interface PoiCategorySelection {
        void onPoiCategorySelected(PoiCategory poiCategory, int i);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout clRoot;
        private AppCompatImageView ivCategory;
        private TextView tvCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clRoot = itemView.findViewById(R.id.ll_content_poi);
            ivCategory = itemView.findViewById(R.id.iv_category);
            tvCategory = itemView.findViewById(R.id.tv_name);
        }
    }

}
