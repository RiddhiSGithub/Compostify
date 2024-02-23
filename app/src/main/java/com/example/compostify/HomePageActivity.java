package com.example.compostify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

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
        replaceFragment(new HomeFragment());

        // Manually change the color for the initially selected item (Home)
        changeColorIcon(homePageBinding.bottomNavigationView.getMenu().findItem(R.id.home));

        // Set up bottom navigation item selected listener
        homePageBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.Search) {
                    replaceFragment(new SearchFragment());
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.list) {
                    replaceFragment(new ListFragment());
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.publish) {
                    replaceFragment(new PublishFragment());
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.profile) {
                    replaceFragment(new ProfileFragment());
                    changeColorIcon(item);
                }



                return true;
            }
        });
    }

    private void changeColorIcon(MenuItem item) {
        // Apply ColorStateList to change the tint color only for the selected item
        int selectedColor = getResources().getColor(R.color.primary);
        int unselectedColor = getResources().getColor(R.color.secondary);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{-android.R.attr.state_checked};

        int[] colors = new int[]{selectedColor, unselectedColor};

        ColorStateList colorStateList = new ColorStateList(states, colors);
        homePageBinding.bottomNavigationView.setItemIconTintList(colorStateList);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
