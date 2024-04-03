package com.example.compostify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.compostify.Activities.AboutUs;
import com.example.compostify.databinding.ActivityHomePageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {

    ActivityHomePageBinding homePageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageBinding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(homePageBinding.getRoot());

        homePageBinding.actionBar.aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AboutUs.class);
                startActivity(intent);
            }
        });
        // Set up initial fragment
        replaceFragment(new HomeFragment(), getString(R.string.app_name));

        // Set up bottom navigation item selected listener
        homePageBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment(), getString(R.string.app_name));
                } else if (item.getItemId() == R.id.List) {
                    replaceFragment(new PostsListFragment(),getString(R.string.posts_list));
                } else if (item.getItemId() == R.id.publish) {
                    replaceFragment(new PublishFragment(), getString(R.string.publish_title));
                } else if (item.getItemId() == R.id.profile) {
                    replaceFragment(new ProfileFragment(), getString(R.string.profile_title));
                }

                return true;
            }
        });
    }



    private void replaceFragment(Fragment fragment, String title) {
        // Pass the fragment title as an argument
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

        // Update toolbar title
        homePageBinding.actionBar.materialToolbar.setTitle(title);
    }
}