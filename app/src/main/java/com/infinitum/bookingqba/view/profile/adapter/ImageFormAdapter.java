package com.infinitum.bookingqba.view.profile.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.profile.uploaditem.GaleryFormObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;

public class ImageFormAdapter extends RecyclerView.Adapter<ImageFormAdapter.ViewHolder> {

    private ArrayList<GaleryFormObject> imagesPath;
    private OnImageDeleteClick onImageDeleteClick;

    public ImageFormAdapter(ArrayList<GaleryFormObject> imagesPath, OnImageDeleteClick onImageDeleteClick) {
        this.imagesPath = imagesPath;
        this.onImageDeleteClick = onImageDeleteClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_add_image_form, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GaleryFormObject galeryFormObject = imagesPath.get(i);
        String path = galeryFormObject.getVersion() == 1?galeryFormObject.getUrl():"file:"+galeryFormObject.getUrl();
        Picasso.get()
                .load(path)
                .resize(THUMB_WIDTH, THUMB_HEIGHT)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.porterShapeImageView);
        viewHolder.ll_delete_item.setOnClickListener(v -> {
                    if (galeryFormObject.getVersion() == 1) {
                        onImageDeleteClick.onImageDelete(imagesPath.get(i).getUuid(), i);
                    } else {
                        removeItem(i);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    public void removeItem(int pos) {
        imagesPath.remove(pos);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView porterShapeImageView;
        private LinearLayout ll_delete_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            porterShapeImageView = itemView.findViewById(R.id.iv_galery);
            ll_delete_item = itemView.findViewById(R.id.ll_btn_delete);
        }
    }

    public interface OnImageDeleteClick {
        void onImageDelete(String uuid, int pos);
    }
}
