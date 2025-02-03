package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.my_inventory_data_access.MyInventoryDataAccessFragment;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.other_inventory_data_access.OtherInventoryDataAccessFragment;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InventoryDataAccessFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabAdapter tabAdapter;

    private String[] data = {"My Inventory", "Other Inventory"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_inventory_data_access, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.inventoryDataAccessTabLayout);
        viewPager2 = view.findViewById(R.id.inventoryDataAccessViewPager2);
        tabAdapter = new TabAdapter(this);

        viewPager2.setAdapter(tabAdapter);
        new TabLayoutMediator(
                tabLayout,
                viewPager2,
                (tab, position) -> {
                    tab.setText(data[position]);
                }
        ).attach();
    }

    class TabAdapter extends FragmentStateAdapter {

        public TabAdapter(@NonNull @NotNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0:
                    return MyInventoryDataAccessFragment.getInstance();
                case 1:
                    return OtherInventoryDataAccessFragment.getInstance();
            }
            return new Fragment();
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }
}