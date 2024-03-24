package com.example.compostify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.compostify.R;
import com.example.compostify.WasteDetailsActivity;
import com.example.compostify.db.UserRecentActivity;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserRecentActivityAdapter extends RecyclerView.Adapter<UserRecentActivityAdapter.ViewHolder> {
    private List<UserRecentActivity> recentActivityList;
    private Context context;

    // Constructor to initialize the list and context
    public UserRecentActivityAdapter(Context context, List<UserRecentActivity> recentActivityList) {
        this.context = context;
        this.recentActivityList = recentActivityList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView txtTitle;
        public MaterialTextView txtTypeOfWaste;
        public MaterialTextView txtDate;
        public MaterialTextView txtTime;
        public MaterialTextView txtWeight;
        public MaterialTextView txtViewMore;
        public ImageView imgWasteImage;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTypeOfWaste = itemView.findViewById(R.id.txtTypeOfWaste);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtViewMore = (MaterialTextView) itemView.findViewById(R.id.txtViewMore);
            imgWasteImage = itemView.findViewById(R.id.imgWasteImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recent_activity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserRecentActivity activity = recentActivityList.get(position);

        holder.txtTitle.setText(activity.getTypeOfUser());
        holder.txtTypeOfWaste.setText(activity.getTypeOfWaste());
        holder.txtDate.setText(activity.getDate());
        holder.txtTime.setText(activity.getTime());
        holder.txtWeight.setText(activity.getWeight());

        RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
            // Load the first image into the ImageView
            Glide.with(context)
                    .load(activity.getImageUrl())
                    .apply(requestOptions)
                    .placeholder(R.drawable.placeholder) // Placeholder image while loading
                    .error(R.drawable.side_image) // Error image if loading fails
                    .into(holder.imgWasteImage);

        // Set click listener for "View More" TextView
        holder.txtViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event
                // For example, start a new activity
                Intent intent = new Intent(context, WasteDetailsActivity.class);
                // Pass user ID and post date/time to the next activity
//                intent.putExtra("userId", activity.getUserId());
//                intent.putExtra("postDateTime", activity.getPostDateTime());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentActivityList.size();
    }
}
