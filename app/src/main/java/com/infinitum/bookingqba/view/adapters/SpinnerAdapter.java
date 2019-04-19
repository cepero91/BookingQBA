package com.infinitum.bookingqba.view.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.infinitum.bookingqba.R;

public class SpinnerAdapter extends ArrayAdapter{


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
        tv.setTypeface(ResourcesCompat.getFont(getContext(),R.font.poppinsregular));
        tv.setTextColor(getContext().getResources().getColor(R.color.material_color_blue_grey_500));
        tv.setPadding(10,12,10,12);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tv;
    }
}
