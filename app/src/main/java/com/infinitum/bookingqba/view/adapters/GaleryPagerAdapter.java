package com.infinitum.bookingqba.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentGalerieItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GaleryPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<RentGalerieItem> imagePathList;

    public GaleryPagerAdapter(Context context, LayoutInflater inflater, List<RentGalerieItem> imagePathList) {
        this.context = context;
        this.inflater = inflater;
        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        return imagePathList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    public int itemPosById(String id) {
        int pos = -1;
        for (int i = 0; i < imagePathList.size(); i++) {
            if (imagePathList.get(i).getId().equals(id)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_galery_item, container, false);
        RoundedImageView roundedImageView = view.findViewById(R.id.iv_rent);
        String path = imagePathList.get(position).getImage();
        if(!path.contains("http")){
            path = "file:"+path;
        }
        Picasso.get()
                .load(path)
                .placeholder(R.drawable.placeholder)
                .into(roundedImageView);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
