package com.example.compostify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.compostify.R;
import com.example.compostify.db.Order;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class MyOrderItemListAdapter extends RecyclerView.Adapter<MyOrderItemListAdapter.MyOrderItemViewHolder> {

    private List<Order> orderItemList;

    public MyOrderItemListAdapter(List<Order> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public MyOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_list, parent, false);
        return new MyOrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderItemViewHolder holder, int position) {
        Order currentItem = orderItemList.get(position);

        holder.txtSellerBusinessName.setText(currentItem.getSellerBusinessName());
        holder.txtSellerName.setText(currentItem.getSellerName());
        holder.txtSellerTypeOfUser.setText(currentItem.getSellerTypeOfUser());
        holder.txtSellerAddress.setText(currentItem.getSellerAddress());
        holder.txtOrderDate.setText(currentItem.getOrderDate());
        holder.txtTypeOfWaste.setText(currentItem.getTypeOfWaste());
        holder.txtWeight.setText(currentItem.getWeight());
        holder.txtNaturalWeight.setText(currentItem.getNaturalWeight());
        holder.txtMixWeight.setText(currentItem.getMixWeight());
        holder.txtTotalAmount.setText(currentItem.getTotalAmount());
        holder.txtPurchaserBusinessName.setText(currentItem.getPurchaserBusinessName());
        holder.txtPurchaserName.setText(currentItem.getPurchaserName());
        holder.txtPurchaserTypeOfUser.setText(currentItem.getPurchaserTypeOfUser());
        holder.txtPurchaserCardNumber.setText(currentItem.getPurchaserCardNumber());

        // Masking the card number
        String cardNumber = currentItem.getPurchaserCardNumber();
        if (cardNumber != null && cardNumber.length() >= 4) {
            String maskedNumber = maskCardNumber(cardNumber);
            holder.txtPurchaserCardNumber.setText(maskedNumber);
        } else {
            holder.txtPurchaserCardNumber.setText(cardNumber);
        }

        if (currentItem.getTypeOfWaste() == null) {
            // Set a default value if type of waste is null
            holder.txtTypeOfWaste.setText("Default Waste Type");
        } else if (currentItem.getTypeOfWaste().equals("both")) {
            holder.txtTypeOfWaste.setText(currentItem.getTypeOfWaste());
            holder.txtNaturalWeight.setVisibility(View.VISIBLE);
            holder.txtMixWeight.setVisibility(View.VISIBLE);
            holder.txtNaturalWeight.setText(currentItem.getNaturalWeight());
            holder.txtMixWeight.setText(currentItem.getMixWeight());
        } else {
            holder.txtTypeOfWaste.setText(currentItem.getTypeOfWaste());
            holder.txtNaturalWeight.setVisibility(View.GONE);
            holder.txtMixWeight.setVisibility(View.GONE);
        }

        RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
        Glide.with(holder.itemView)
                .load(currentItem.getSellerBusinessLogoUrl())
                .placeholder(R.drawable.placeholder) // Placeholder drawable while loading
                .error(R.drawable.logo_app) // Drawable to display if loading fails
                .apply(requestOptions)
                .into(holder.imageView);
    }

    private String maskCardNumber(String cardNumber) {
        // Mask all digits except the last four
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < cardNumber.length() - 4; i++) {
            maskedNumber.append("*");
        }
        maskedNumber.append(cardNumber.substring(cardNumber.length() - 4));
        return maskedNumber.toString();
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class MyOrderItemViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView txtSellerBusinessName;
        MaterialTextView txtSellerName;
        MaterialTextView txtSellerTypeOfUser;
        MaterialTextView txtSellerAddress;
        MaterialTextView txtOrderDate;
        MaterialTextView txtTypeOfWaste;
        MaterialTextView txtWeight;
        MaterialTextView txtNaturalWeight;
        MaterialTextView txtMixWeight;
        MaterialTextView txtTotalAmount;
        MaterialTextView txtPurchaserBusinessName;
        MaterialTextView txtPurchaserName;
        MaterialTextView txtPurchaserTypeOfUser;
        MaterialTextView txtPurchaserCardNumber;
        ImageView imageView;

        public MyOrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSellerBusinessName = itemView.findViewById(R.id.txtSellerBusinessName);
            txtSellerName = itemView.findViewById(R.id.txtSellerName);
            txtSellerTypeOfUser = itemView.findViewById(R.id.txtTypeOfSellerUser);
            txtSellerAddress = itemView.findViewById(R.id.txtSellerBusinessAddress);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtTypeOfWaste = itemView.findViewById(R.id.txtTypeOfWaste);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtNaturalWeight = itemView.findViewById(R.id.txtNaturalWeight);
            txtMixWeight = itemView.findViewById(R.id.txtMixWeight);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtPurchaserBusinessName = itemView.findViewById(R.id.txtPurchaserBusinessName);
            txtPurchaserName = itemView.findViewById(R.id.txtPurchaserName);
            txtPurchaserTypeOfUser = itemView.findViewById(R.id.txtTypeOfPurchaserUser);
            txtPurchaserCardNumber = itemView.findViewById(R.id.txtPurchaserCardNumber);
            imageView = itemView.findViewById(R.id.imgSellerBusinessLogo);
        }
    }

}
