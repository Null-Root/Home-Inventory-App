package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.my_inventory_data_access;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.InventoryDataAccessListener;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.SharedInventoryDataAccessViewModel;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.SharedInventoryDataRecycler;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyInventoryDataAccessFragment extends Fragment implements InventoryDataAccessListener, SwipeRefreshLayout.OnRefreshListener {

    private SharedInventoryDataAccessViewModel mViewModel;

    private SearchView MyInventorySearchView;
    private SwipeRefreshLayout MyInventorySwipeRefresh;

    private SharedInventoryDataRecycler SharedInventoryDataAdapter;

    private static MyInventoryDataAccessFragment s_instance = null;
    private MyInventoryDataAccessFragment() {}

    public static MyInventoryDataAccessFragment getInstance() {
        if(s_instance == null)
            s_instance = new MyInventoryDataAccessFragment();
        return s_instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_ida_my_inventory_data_access, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SharedInventoryDataAccessViewModel.class);
        mViewModel.inventoryDataAccessListener = this;
        MyInventorySwipeRefresh = view.findViewById(R.id.MyInventorySwipeRefresh);
        RecyclerView myInventoryRecyclerView = view.findViewById(R.id.MyInventoryRecyclerView);
        MyInventorySearchView = view.findViewById(R.id.MyInventorySearchView);

        SharedInventoryDataAdapter = new SharedInventoryDataRecycler(getContext(), new ArrayList<>(), mViewModel, true);
        myInventoryRecyclerView.setAdapter(SharedInventoryDataAdapter);
        myInventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyInventorySwipeRefresh.setOnRefreshListener(this);
        MyInventorySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedInventoryDataAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SharedInventoryDataAdapter.getFilter().filter(newText);
                return false;
            }
        });
        mViewModel.MyInventoryLoadProfileData();
    }

    @Override
    public void OnDataCallback(LiveData<String> liveData) {
        liveData.observe(getViewLifecycleOwner(), s -> {
            if(CustomFunctions.isVarSet(s)) {
                DataModel.Recv.ShareData response = new Gson().fromJson(s, DataModel.Recv.ShareData.class);
                if(response.ID_Exists)
                    SharedInventoryDataAdapter.UpdateList(response.InventoryProfiles);
            }
        });
    }

    @Override
    public void onRefresh() {
        mViewModel.MyInventoryLoadProfileData();
        MyInventorySwipeRefresh.setRefreshing(false);
    }
}