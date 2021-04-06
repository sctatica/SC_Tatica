package com.sang.sc_tatica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PriorityAdapter extends ArrayAdapter<Priority> {
    public PriorityAdapter(Context context, ArrayList<Priority> priorityList) {
        super(context, 0, priorityList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_priority, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageColor);
        TextView txtDes = convertView.findViewById(R.id.txtDes);

        Priority current_priority = getItem(position);

        if (current_priority != null) {
            imageView.setImageResource(current_priority.getColorImage());
            txtDes.setText(current_priority.getDescription());
        }

        return convertView;
    }
}
