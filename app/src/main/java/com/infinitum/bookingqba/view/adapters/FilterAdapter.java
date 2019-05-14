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
import com.infinitum.bookingqba.view.adapters.items.filter.CheckableItem;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<CheckableItem> checkableItemList;
    private SparseBooleanArray selectionIndex;

    public FilterAdapter(List<CheckableItem> checkableItemList) {
        this.checkableItemList = checkableItemList;
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
        if(selectionIndex.get(i)){
            filterViewHolder.changeViewState(true);
        }else{
            filterViewHolder.changeViewState(false);
        }
        filterViewHolder.bind(checkableItemList.get(i));
        filterViewHolder.itemView.setOnClickListener(v -> changeCheckState(i));
    }


    @Override
    public int getItemCount() {
        return checkableItemList == null ? 0 : checkableItemList.size();
    }

    private void initSparseBoolean(int size){
        selectionIndex = new SparseBooleanArray(size);
        for(int i=0; i < size; i++){
            selectionIndex.append(i,false);
        }
    }

    public ArrayList<String> getSelectedItems(){
        ArrayList<String> selectedItems = new ArrayList<>();
        for(int i=0; i < selectionIndex.size(); i++){
            if(selectionIndex.get(i)){
                selectedItems.add(checkableItemList.get(i).getId());
            }
        }
        return selectedItems;
    }

    private void changeCheckState(int pos){
        if(selectionIndex.get(pos)){
            selectionIndex.append(pos,false);
        }else{
            selectionIndex.append(pos,true);
        }
        notifyDataSetChanged();
    }

    public void resetSelectedItem(){
        initSparseBoolean(checkableItemList.size());
        notifyDataSetChanged();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterShipTitle;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.filterShipTitle = itemView.findViewById(R.id.filter_ship_title);
        }

        private void bind(CheckableItem checkableItem) {
            filterShipTitle.setText(checkableItem.getName());
        }

        private void changeViewState(boolean isSelected) {
            if (isSelected) {
                filterShipTitle.setTextColor(Color.WHITE);
                filterShipTitle.setBackgroundResource(R.drawable.shape_filter_ship_selected);
            } else {
                filterShipTitle.setTextColor(Color.parseColor("#9E9E9E"));
                filterShipTitle.setBackgroundResource(R.drawable.shape_filter_ship_unselected);
            }
        }

    }
}