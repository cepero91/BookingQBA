package com.infinitum.bookingqba.view.profile.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.profile.uploaditem.OfferFormObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class OfferFormAdapter extends RecyclerView.Adapter<OfferFormAdapter.ViewHolder> {

    private ArrayList<OfferFormObject> offerFormObjects;
    private OnOfferInteraction onOfferInteraction;

    public OfferFormAdapter(ArrayList<OfferFormObject> offerFormObjects, OnOfferInteraction onOfferInteraction) {
        this.offerFormObjects = offerFormObjects;
        this.onOfferInteraction = onOfferInteraction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_form_offer_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        OfferFormObject offerFormObject = offerFormObjects.get(i);
        viewHolder.name.setText(offerFormObject.getName());
        viewHolder.desc.setText(offerFormObject.getDescription());
        viewHolder.price.setText(offerFormObject.getPrice());
        viewHolder.ivEdit.setOnClickListener(v -> onOfferInteraction.onOfferEdit(offerFormObject,i));
        viewHolder.ivTrash.setOnClickListener(v -> {
                removeItem(i);
        });
    }

    @Override
    public int getItemCount() {
        return offerFormObjects.size();
    }

    public void removeItem(int pos) {
        offerFormObjects.remove(pos);
        notifyDataSetChanged();
    }

    public void updateItem(OfferFormObject object, int pos){
        OfferFormObject finalObject = offerFormObjects.get(pos);
        finalObject.setUuid(object.getUuid());
        finalObject.setName(object.getName());
        finalObject.setDescription(object.getDescription());
        finalObject.setPrice(object.getPrice());
        notifyDataSetChanged();
    }

    public void addOffer(OfferFormObject offerFormObject){
        this.offerFormObjects.add(offerFormObject);
        notifyDataSetChanged();
    }

    public ArrayList<OfferFormObject> getOfferFormObjects() {
        return offerFormObjects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivEdit;
        private AppCompatImageView ivTrash;
        private TextView name,desc,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivTrash = itemView.findViewById(R.id.iv_trash);
            name = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_description);
            price = itemView.findViewById(R.id.tv_price);
        }
    }

    public interface OnOfferInteraction {
        void onOfferEdit(OfferFormObject offerFormObject, int pos);
    }
}
