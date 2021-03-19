package com.sang.userlogin_registration.FullTips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sang.userlogin_registration.R;

import java.util.ArrayList;

public class FullTipsRecyclerViewAdapter extends RecyclerView.Adapter<FullTipsRecyclerViewAdapter.ViewHolder> {

    public static final String Tag = "RecyclerView";
    private Context context;
    private ArrayList<GetTips> TipsList;

    public FullTipsRecyclerViewAdapter(Context context, ArrayList<GetTips> tipsList) {
        this.context = context;
        this.TipsList = tipsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_pomodoro_break_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // for texts
        holder.type.setText(TipsList.get(position).getType());
        holder.title.setText(TipsList.get(position).getTitle());
        holder.description.setText(TipsList.get(position).getDescription());
        // for the image
        Glide.with(context).load(TipsList.get(position).getImageUrl()).into(holder.image);


    }


    @Override
    public int getItemCount() {
        return TipsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView type;
        TextView title;
        ImageView image;
        TextView description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.tips_type);
            title = itemView.findViewById(R.id.tips_title);
            image = itemView.findViewById(R.id.tips_image);
            description = itemView.findViewById(R.id.tips_description);

        }
    }

}
