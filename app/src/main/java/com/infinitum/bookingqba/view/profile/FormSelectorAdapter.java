package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.infinitum.bookingqba.R;

import java.util.List;

public class FormSelectorAdapter extends ArrayAdapter<FormSelectorItem>{

    private List<FormSelectorItem> objects;

    public FormSelectorAdapter(@NonNull Context context, @NonNull List<FormSelectorItem> objects) {
        super(context, 0, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.form_single_selection_item, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        FormSelectorItem option = getItem(position);

        vh.name.setText(option.getName());

        return convertView;
    }

    public String getUuid(int position){
        return getItem(position).getUuid();
    }

    private static final class ViewHolder {
        TextView name;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.tv_name);
        }
    }
}
