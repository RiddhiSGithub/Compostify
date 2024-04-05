package com.example.compostify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compostify.R;
import com.example.compostify.db.Order;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private List<Order> myOrderlist;
    private FirebaseFirestore db;

    public MyOrderAdapter(Context context, List<Order> orders){
        this.myOrderlist = orders;

        db =FirebaseFirestore.getInstance();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_list, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Order order = myOrderlist.get(position);
//        final String[] businessName = new String[1];
//        final String[] image = new String[1];
//        db.collection("users")
//                .document(order.getSellerId())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        businessName[0] = task.getResult().get("businessName").toString();
//                        image[0] = task.getResult().get("downloadUrl").toString();
//                    }
//                });
//        RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
//        Glide.with(holder.itemView.getContext())
//                .load(image[0])
//                .apply(requestOptions)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.side_image) // Set error image here
//                .into(holder.imgBusinessImage);
//
//        holder.BusinessName.setText(businessName[0]);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgBusinessImage;
        public MaterialTextView BusinessName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBusinessImage = itemView.findViewById(R.id.imgSellerBusinessLogo);
            BusinessName = itemView.findViewById(R.id.txtBusinessName);
        }
    }

}
