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

public class LocalOrGlobalAdapter extends ArrayAdapter<LocalOrGlobal> {

    public LocalOrGlobalAdapter(Context context, ArrayList<LocalOrGlobal> localOrGlobalArrayList) {
        super(context, 0, localOrGlobalArrayList);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_local_or_global, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView txtName = convertView.findViewById(R.id.txtName);

        LocalOrGlobal localOrGlobal = getItem(position);

        if (localOrGlobal != null) {
            imageView.setImageResource(localOrGlobal.getImage());
            txtName.setText(localOrGlobal.getName());
        }

        return convertView;
    }
}
