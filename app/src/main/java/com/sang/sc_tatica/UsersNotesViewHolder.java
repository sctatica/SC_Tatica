package com.sang.sc_tatica;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersNotesViewHolder extends RecyclerView.ViewHolder {

    TextView mTitleTv, mDescriptionTv;
    View mView;

    public UsersNotesViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.OnItemClick(v, getAdapterPosition());
            }
        });

        // item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.OnItemLongClick(v, getAdapterPosition());

                return true;
            }
        });

        // initilize views with model layout.xml
        mTitleTv = itemView.findViewById(R.id.rTitle);
        mDescriptionTv = itemView.findViewById(R.id.rDescription);

    }

    // interface for click listener
    private UsersNotesViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);

    }

    public void setOnClickListener(UsersNotesViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
