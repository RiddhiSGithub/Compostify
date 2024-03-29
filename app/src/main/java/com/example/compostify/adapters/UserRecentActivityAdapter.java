package com.example.compostify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.compostify.Activities.EditPost;
import com.example.compostify.R;
import com.example.compostify.db.UserRecentActivity;
import com.google.android.material.textview.MaterialTextView;

import java.util.Collections;
import java.util.List;

public class UserRecentActivityAdapter extends RecyclerView.Adapter<UserRecentActivityAdapter.ViewHolder> {

    private static final String TAG = "UserActivityAdapter";
    private List<UserRecentActivity> recentActivityList;
    private final RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
    // Constructor to initialize the list and context
    public UserRecentActivityAdapter(Context context, List<UserRecentActivity> recentActivityList) {
//        this.context = context;
        this.recentActivityList = recentActivityList;
        // Sort the list by date and time in descending order
        Collections.sort(recentActivityList, (o1, o2) -> {
            // Compare dates first
            int dateComparison = o2.getDate().compareTo(o1.getDate());
            if (dateComparison == 0) {
                // If dates are the same, compare times
                return o2.getTime().compareTo(o1.getTime());
            }
            return dateComparison;
        });
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView txtTypeOfWaste;
        public MaterialTextView txtDate;
        public MaterialTextView txtTime;
        public MaterialTextView txtWeight;
        public MaterialTextView txtEditPost;
        public ImageView imgWasteImage;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTypeOfWaste = itemView.findViewById(R.id.txtTypeOfWaste);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtEditPost = itemView.findViewById(R.id.txtEditPost);
            imgWasteImage = itemView.findViewById(R.id.imgWasteImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recent_activity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserRecentActivity activity = recentActivityList.get(position);

        holder.txtTypeOfWaste.setText(activity.getTypeOfWaste());
        holder.txtDate.setText(activity.getDate());
        holder.txtTime.setText(activity.getTime());
        holder.txtWeight.setText(activity.getWeight());

        // Check if imageUrl is not null or empty
        if (activity.getImageUrl() != null && !activity.getImageUrl().isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
            Glide.with(holder.itemView.getContext())
                    .load(activity.getImageUrl().get(0))
                    .apply(requestOptions)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.side_image) // Set error image here
                    .into(holder.imgWasteImage);
        } else {
            // If imageUrl is empty or null, load the error image
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.side_image)
                    .into(holder.imgWasteImage);
        }



        holder.imgWasteImage.setOnClickListener(v -> {
            // Create an Intent to start ActivityEditPost
            Intent intent = new Intent(v.getContext(), EditPost.class);

            // pass user publish details to ActivityEditPost
            intent.putExtra("userPost", activity);

            // Start the ActivityEditPost
            v.getContext().startActivity(intent);
        });

        // Set click listener for "Edit Post" TextView
        holder.txtEditPost.setOnClickListener(v -> {
            // Create an Intent to start ActivityEditPost
            Intent intent = new Intent(v.getContext(), EditPost.class);

            // pass user publish details to ActivityEditPost
            intent.putExtra("userPost", activity);

            // Start the ActivityEditPost
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return recentActivityList.size();
    }
}
