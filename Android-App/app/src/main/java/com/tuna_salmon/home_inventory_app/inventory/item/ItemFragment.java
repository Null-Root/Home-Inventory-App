package com.tuna_salmon.home_inventory_app.inventory.item;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.FragmentWithFab;
import com.tuna_salmon.home_inventory_app.inventory.category.CategoryRecycler;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ItemFragment extends FragmentWithFab implements ItemListener, SwipeRefreshLayout.OnRefreshListener {

    private ItemViewModel mViewModel;
    private NavController navController;

    private SwipeRefreshLayout ItemSwipeRefresh;
    private ItemRecycler ItemAdapter;

    public View Item_Overlay;
    public LinearLayout Item_DetailedContainer;
    public TextView Item_DetailedName;
    public TextView Item_DetailedCount;
    public TextView Item_DetailedPrice;
    public TextView Item_DetailedUnit;
    public TextView Item_DetailedLastEdited;
    public TextView Item_DetailedPersonLastEdited;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        //region Fab Setup View
        FabOverlay = view.findViewById(R.id.ItemOverlay);
        Main_Fab = view.findViewById(R.id.itemMainFab);
        //endregion

        //region Item Detailed Info Setup View
        Item_Overlay = view.findViewById(R.id.ItemOverlay);
        Item_DetailedContainer = view.findViewById(R.id.itemShowItemDetailed);
        Item_DetailedName = view.findViewById(R.id.itemShowItemDetailedName);
        Item_DetailedCount = view.findViewById(R.id.itemShowItemDetailedCount);
        Item_DetailedPrice = view.findViewById(R.id.itemShowItemDetailedPrice);
        Item_DetailedUnit = view.findViewById(R.id.itemShowItemDetailedUnit);
        Item_DetailedLastEdited = view.findViewById(R.id.itemShowItemDetailedLastEdited);
        Item_DetailedPersonLastEdited = view.findViewById(R.id.itemShowItemDetailedPersonLastEdited);
        //endregion

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        mViewModel.itemListener = this;

        //region Item Setup View
        SearchView searchView = view.findViewById(R.id.itemSearchView);
        RecyclerView itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        ItemAdapter = new ItemRecycler(getContext(), new ArrayList<>(), navController, mViewModel);
        ItemSwipeRefresh = view.findViewById(R.id.itemSwipeRefreshLayout);
        //endregion

        itemRecyclerView.setAdapter(ItemAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemSwipeRefresh.setOnRefreshListener(this);

        mViewModel.LoadItem();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ItemAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ItemAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Main_Fab.setOnClickListener(v -> {
            // Create Alert Dialog of Category Modify
            AlertDialog ItemDialog = new AlertDialog.Builder(getContext())
                    .setView(getLayoutInflater().inflate(R.layout.sub_fragment_modify_item, null))
                    .setCancelable(false)
                    .create();
            ItemDialog.show();

            //region Get Views
            TextView Title = ItemDialog.findViewById(R.id.itemModifyLabel);
            EditText Item_Name = ItemDialog.findViewById(R.id.modifyItemNameHolder);
            EditText Item_Count = ItemDialog.findViewById(R.id.modifyItemCountHolder);
            EditText Item_CriticalCount = ItemDialog.findViewById(R.id.modifyItemCriticalCountHolder);
            EditText Item_Price = ItemDialog.findViewById(R.id.modifyItemPriceHolder);
            Spinner Item_Unit = ItemDialog.findViewById(R.id.modifyItemUnitHolder);
            Button CancelButton = ItemDialog.findViewById(R.id.modifyItemCancelButton);
            Button SaveButton = ItemDialog.findViewById(R.id.modifyItemSaveButton);
            //endregion

            // Set Values
            Title.setText("Add Item");
            SaveButton.setVisibility(View.GONE);

            // Checks
            TextWatcher CheckInput = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.isItemNameValid(Item_Name, Item_Count, Item_CriticalCount, Item_Price)) {
                        SaveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        SaveButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            // Set Live Change Logic
            Item_Name.addTextChangedListener(CheckInput);
            Item_Count.addTextChangedListener(CheckInput);
            Item_CriticalCount.addTextChangedListener(CheckInput);
            Item_Price.addTextChangedListener(CheckInput);

            // Set Button Listeners
            CancelButton.setOnClickListener(v1 -> {
                ItemDialog.dismiss();
            });

            SaveButton.setOnClickListener(v1 -> {
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                mViewModel.AddItem(
                        Item_Name.getText().toString(),
                        Float.parseFloat(Item_Count.getText().toString()),
                        Float.parseFloat(Item_CriticalCount.getText().toString()),
                        Float.parseFloat(Item_Price.getText().toString()),
                        Item_Unit.getSelectedItem().toString());
                ItemDialog.dismiss();
            });
        });

        Item_DetailedContainer.setOnClickListener(v -> {
            ViewItemDetailed(false);
        });

        Item_Overlay.setOnClickListener(v -> {
            ViewItemDetailed(false);
        });
    }

    private void ViewItemDetailed(boolean Show) {
        if(Show) {
            Item_DetailedContainer.setVisibility(View.VISIBLE);
            Item_Overlay.setVisibility(View.VISIBLE);
        }
        else {
            Item_DetailedContainer.setVisibility(View.GONE);
            Item_Overlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnDataItemCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();
            if(mViewModel.CheckItemCallback(s)) {
                DataModel.Recv.Item response = new Gson().fromJson(s, DataModel.Recv.Item.class);
                switch(response.Status) {
                    case 0: // 0 -> WORKING
                        DataProcess(response);
                        break;
                    case 1: // 1 -> CODE/SERVER ERROR
                        UserAppHandler.UI.AppUI().SimpleDialog("Error", response.Message);
                        break;
                }
            }
            else {
                UserAppHandler.UI.AppUI().SimpleDialog("Error", "Server Error");
            }
        });
    }

    private void DataProcess(DataModel.Recv.Item response) {
        switch (response.AuthPermission) {
            case 0:
                // Show That Not Allowed
                Main_Fab.setVisibility(View.GONE);
                break;
            case 1:
                // Disable Add/Edit
                Main_Fab.setVisibility(View.GONE);

                if(!response.Exists)
                    ItemAdapter.UpdateList(response.Item_List);
                else
                    UserAppHandler.UI.AppUI().SimpleDialog("Duplicate Error", "Item Name was a duplicate");
                break;
            case 2:
                if(!response.Exists)
                    ItemAdapter.UpdateList(response.Item_List);
                else
                    UserAppHandler.UI.AppUI().SimpleDialog("Duplicate Error", "Item Name was a duplicate");
                break;
        }
    }

    @Override
    public void OnShowItemDetailed(ArrayList<DataModel.DatabaseModel.Item> ViewedDataInstance, int position) {
        Item_DetailedName.setText(ViewedDataInstance.get(position).Name);
        Item_DetailedCount.setText(Double.toString(ViewedDataInstance.get(position).Count));
        Item_DetailedPrice.setText(Double.toString(ViewedDataInstance.get(position).Price));
        Item_DetailedUnit.setText(ViewedDataInstance.get(position).Unit);
        Item_DetailedLastEdited.setText(ViewedDataInstance.get(position).LastEdited);
        Item_DetailedPersonLastEdited.setText(ViewedDataInstance.get(position).PersonLastEdit);

        ViewItemDetailed(true);
    }

    @Override
    public void onRefresh() {
        mViewModel.LoadItem();
        ItemSwipeRefresh.setRefreshing(false);
    }
}