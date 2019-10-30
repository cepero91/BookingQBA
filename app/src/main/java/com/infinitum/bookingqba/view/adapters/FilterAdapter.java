package com.infinitum.bookingqba.view.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableFilterItem;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<CheckableFilterItem> checkableItemList;
    private SparseBooleanArray selectionIndex;
    private OnShipClick onShipClick;

    public FilterAdapter(List<CheckableFilterItem> checkableItemList, OnShipClick onShipClick) {
        this.checkableItemList = checkableItemList;
        this.onShipClick = onShipClick;
        initSparseBoolean(checkableItemList.size());
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_filter_ships, viewGroup, false);
        return new FilterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder filterViewHolder, int i) {
        CheckableFilterItem checkableFilterItem = checkableItemList.get(i);
        if (selectionIndex.get(i)) {
            filterViewHolder.changeViewState(true);
        } else {
            filterViewHolder.changeViewState(false);
        }
        filterViewHolder.bind(checkableFilterItem);
        filterViewHolder.itemView.setOnClickListener(v -> {
            changeCheckState(i);
            onShipClick.onShipClick();
        });
    }


    @Override
    public int getItemCount() {
        return checkableItemList == null ? 0 : checkableItemList.size();
    }

    private void initSparseBoolean(int size) {
        if (size > 0) {
            selectionIndex = new SparseBooleanArray(size);
            for (int i = 0; i < size; i++) {
                selectionIndex.append(i, checkableItemList.get(i).isChecked());
            }
        }
    }

    public ArrayList<String> getSelectedItems() {
        ArrayList<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < selectionIndex.size(); i++) {
            if (selectionIndex.get(i)) {
                selectedItems.add(checkableItemList.get(i).getId());
            }
        }
        return selectedItems;
    }

    public boolean isAtLessOneSelected(){
        for (int i = 0; i < selectionIndex.size(); i++) {
            if (selectionIndex.get(i)) {
                return true;
            }
        }
        return false;
    }

    private void changeCheckState(int pos) {
        if (selectionIndex.get(pos)) {
            selectionIndex.append(pos, false);
        } else {
            selectionIndex.append(pos, true);
        }
        notifyDataSetChanged();
    }

    public void resetSelectedItem() {
        initSparseBoolean(checkableItemList.size());
        notifyDataSetChanged();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterShipTitle;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.filterShipTitle = itemView.findViewById(R.id.filter_ship_title);
        }

        private void bind(CheckableFilterItem checkableItem) {
            filterShipTitle.setText(checkableItem.getName());
        }

        private void changeViewState(boolean isSelected) {
            if (isSelected) {
                filterShipTitle.setTextColor(Color.WHITE);
                filterShipTitle.setBackgroundResource(R.drawable.shape_filter_ship_selected);
            } else {
                filterShipTitle.setTextColor(Color.parseColor("#78909C"));
                filterShipTitle.setBackgroundResource(R.drawable.shape_filter_ship_unselected);
            }
        }
    }

    public interface OnShipClick {
        void onShipClick();
    }
}
