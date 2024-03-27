package com.example.compostify;

import android.os.Bundle;
import android.view.MenuItem;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.compostify.databinding.ActivityHomePageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {

    ActivityHomePageBinding homePageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageBinding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(homePageBinding.getRoot());

        // Set up initial fragment
        replaceFragment(new HomeFragment(), getString(R.string.app_name));


        // Set up bottom navigation item selected listener
        homePageBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment(), getString(R.string.app_name));
                } else if (item.getItemId() == R.id.Search) {
                    replaceFragment(new SearchFragment(),getString(R.string.search_title));
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