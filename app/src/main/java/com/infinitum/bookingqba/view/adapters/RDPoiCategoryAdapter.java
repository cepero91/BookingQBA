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
import com.infinitum.bookingqba.util.CategoryUtil;
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
        myViewHolder.ivCategory.setImageResource(CategoryUtil.getImageResourceByCategoryID(poiCategory.getId()));
        if (selectedCategory[i]) {
            myViewHolder.tvCategory.setVisibility(View.VISIBLE);
            myViewHolder.tvCategory.animate().alpha(1)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator()).start();
            myViewHolder.ivCategory.setImageTintList(ColorStateList.valueOf(Color.parseColor("#607D8B")));
        } else {
            myViewHolder.tvCategory.setAlpha(0);
            myViewHolder.tvCategory.setVisibility(View.GONE);
            myViewHolder.ivCategory.setImageTintList(ColorStateList.valueOf(Color.parseColor("#90A4AE")));
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
