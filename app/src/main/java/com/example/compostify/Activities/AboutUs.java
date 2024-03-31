package com.example.compostify.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.compostify.R;
import com.example.compostify.databinding.ActivityAboutUsBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

public class AboutUs extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    ShapeableImageView imgTeam1;
    ShapeableImageView imgTeam2;
    ShapeableImageView imgTeam3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ShapeableImageView instances after inflating the layout
        imgTeam1 = binding.imgTeam1;
        imgTeam2 = binding.imgTeam2;
        imgTeam3 = binding.imgTeam3;

        RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, 50) // 50% radius makes it a circle
                .build();

        // Apply shape to each ShapeableImageView
        // Crop image using Glide and apply circle transformation
        Glide.with(this)
                .load(R.drawable.riddhi) // Replace with your image resource
                .apply(requestOptions)
                .into(imgTeam1);
        Glide.with(this)
                .load(R.drawable.jay) // Replace with your image resource
                .apply(requestOptions)
                .into(imgTeam2);
        Glide.with(this)
                .load(R.drawable.dipali) // Replace with your image resource
                .apply(requestOptions)
                .into(imgTeam3);

    }
}
