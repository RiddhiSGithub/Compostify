package com.example.compostify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class WasteListAdapter extends RecyclerView.Adapter<WasteListAdapter.wasteViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public WasteListAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public WasteListAdapter.wasteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_waste_list_modal, parent, false);
        return new wasteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WasteListAdapter.wasteViewHolder holder, int position) {
        User user = userArrayList.get(position);

            Glide.with(context).load(user.getDownloadUrl()).into(holder.imageView);

        holder.txtBusinessName.setText(user.getBusinessName());
        holder.txtAddress.setText(user.getCityName());
        holder.txtAvailableWaste.setText(user.getTotalWeight());

        holder.lytItemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WasteDetailsActivity.class);
                intent.putExtra("publishId", user.getUserId());
                intent.putExtra("fromSearch", true);
                v.getContext().startActivity(intent);
            }
        });


    }

    public void setData(List<User> data) {
        this.userArrayList = (ArrayList<User>) data;
    }

    @Override
    public int getItemCount() {
        if (userArrayList == null)
            return 0;
        else
            return userArrayList.size();
    }

    public static class wasteViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtBusinessName;
        TextView txtAddress;
        TextView txtAvailableWaste;

        LinearLayout lytItemCard;

        public wasteViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewLogo);
            txtBusinessName = itemView.findViewById(R.id.txtBusinessName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtAvailableWaste = itemView.findViewById(R.id.txtAvailableWaste);
            lytItemCard = itemView.findViewById(R.id.itemCard);
        }
    }
}
