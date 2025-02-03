package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.other_inventory_data_access;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
public class OtherInventoryDataAccessFragment extends Fragment implements InventoryDataAccessListener, SwipeRefreshLayout.OnRefreshListener {

    private SharedInventoryDataAccessViewModel mViewModel;

    private SearchView OtherInventorySearchView;
    private SwipeRefreshLayout OtherInventorySwipeRefresh;
    private FloatingActionButton OtherInventoryAddAccessRequest;

    private LinearLayout AddAccessRequestLayout;
    private EditText AddAccessRequestID;
    private Button AddAccessRequestCancel;
    private Button AddAccessRequestSend;

    private SharedInventoryDataRecycler SharedInventoryDataAdapter;

    private static OtherInventoryDataAccessFragment s_instance = null;
    private OtherInventoryDataAccessFragment() {}

    public static OtherInventoryDataAccessFragment getInstance() {
        if(s_instance == null)
            s_instance = new OtherInventoryDataAccessFragment();
        return s_instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_ida_other_inventory_data_access, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SharedInventoryDataAccessViewModel.class);
        mViewModel.inventoryDataAccessListener = this;
        OtherInventorySwipeRefresh = view.findViewById(R.id.OtherInventorySwipeRefresh);
        RecyclerView otherInventoryRecyclerView = view.findViewById(R.id.OtherInventoryRecyclerView);
        OtherInventorySearchView = view.findViewById(R.id.OtherInventorySearchView);
        OtherInventoryAddAccessRequest = view.findViewById(R.id.otherInventoryAddAccessRequest);
        AddAccessRequestLayout = view.findViewById(R.id.otherInventoryAddAccessRequestLayout);
        AddAccessRequestID = view.findViewById(R.id.otherInventoryAddAccessRequestOtherID);
        AddAccessRequestSend = view.findViewById(R.id.otherInventoryAddAccessRequestSend);
        AddAccessRequestCancel = view.findViewById(R.id.otherInventoryAddAccessRequestCancel);

        SharedInventoryDataAdapter = new SharedInventoryDataRecycler(getContext(), new ArrayList<>(), mViewModel, false);
        otherInventoryRecyclerView.setAdapter(SharedInventoryDataAdapter);
        otherInventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OtherInventorySwipeRefresh.setOnRefreshListener(this::onRefresh);
        OtherInventorySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        OtherInventoryAddAccessRequest.setOnClickListener(v -> {
            // Show Add Access Request Layout
            AddAccessRequestLayout.setVisibility(View.VISIBLE);
        });

        AddAccessRequestCancel.setOnClickListener(v -> {
            // Hide Add Access Request Layout
            AddAccessRequestLayout.setVisibility(View.GONE);
        });

        AddAccessRequestSend.setOnClickListener(v -> {
            // Send Access Request
            mViewModel.OtherInventoryAddProfileData(AddAccessRequestID.getText().toString());
        });

        mViewModel.OtherInventoryLoadProfileData();
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
        mViewModel.OtherInventoryLoadProfileData();
        OtherInventorySwipeRefresh.setRefreshing(false);
    }
}