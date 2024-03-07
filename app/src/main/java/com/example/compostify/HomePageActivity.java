package com.example.compostify;

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

        // Manually change the color for the initially selected item (Home)
        changeColorIcon(homePageBinding.bottomNavigationView.getMenu().findItem(R.id.home));

        // Set up bottom navigation item selected listener
        homePageBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment(), getString(R.string.app_name));
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.Search) {
                    replaceFragment(new SearchFragment(),getString(R.string.search_title));
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.publish) {
                    replaceFragment(new PublishFragment(), getString(R.string.publish_title));
                    changeColorIcon(item);
                } else if (item.getItemId() == R.id.profile) {
                    replaceFragment(new ProfileFragment(), getString(R.string.profile_title));
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