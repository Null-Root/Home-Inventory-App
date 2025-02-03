package com.tuna_salmon.home_inventory_app.multi_edit;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.extra_stuffs.FragmentWithFab;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MultiEditFragment extends FragmentWithFab implements MultiEditListener, SwipeRefreshLayout.OnRefreshListener {

    private NavController navController;
    private MultiEditViewModel mViewModel;

    private SwipeRefreshLayout MultiEditSwipeRefresh;
    private MultiEditRecycler MultiEditAdapter;

    private View MultiEdit_Overlay;
    private LinearLayout ShowContainer;
    private TextView NameHolder;
    private TextView DescHolder;
    private LinearLayout DisplayObjects;
    private Button BackButton;
    private Button ProceedButton;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_multiEditFragment_to_mainFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi_edit, container, false);

        //region Fab Setup View
        Main_Fab = view.findViewById(R.id.multiEditMainFab);
        FabOverlay = view.findViewById(R.id.MultiEditOverlay);
        //endregion

        //region MultiEdit Use Setup View
        MultiEdit_Overlay = view.findViewById(R.id.MultiEditOverlay);
        ShowContainer = view.findViewById(R.id.multiEditShowContainer);
        NameHolder = view.findViewById(R.id.multiEditShowNameHolder);
        DescHolder = view.findViewById(R.id.multiEditShowDescHolder);
        DisplayObjects = view.findViewById(R.id.multiEditShowItemsHolder);
        BackButton = view.findViewById(R.id.multiEditShowBackBtnHolder);
        ProceedButton = view.findViewById(R.id.multiEditShowUseBtnHolder);
        //endregion

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MultiEditViewModel.class);
        mViewModel.multiEditListener = this;
        navController = Navigation.findNavController(view);

        //region Category Setup View
        SearchView searchView = view.findViewById(R.id.multiEditSearchView);
        RecyclerView categoryRecyclerView = view.findViewById(R.id.multiEditRecyclerView);
        MultiEditAdapter = new MultiEditRecycler(getContext(), new ArrayList<>(), navController, mViewModel, getViewLifecycleOwner());
        MultiEditSwipeRefresh = view.findViewById(R.id.multiEditSwipeRefreshLayout);
        //endregion

        categoryRecyclerView.setAdapter(MultiEditAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MultiEditSwipeRefresh.setOnRefreshListener(this);

        // Primary Load
        mViewModel.LoadMultiEdit();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MultiEditAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MultiEditAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Main_Fab.setOnClickListener(v -> {
            AlertDialog MultiEditDialog = new AlertDialog.Builder(getContext(), R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
                    .setView(getLayoutInflater().inflate(R.layout.sub_fragment_modify_multi_edit, null))
                    .setCancelable(false)
                    .create();
            MultiEditDialog.show();

            //region Get Main Views
            LinearLayout DisplayItems = MultiEditDialog.findViewById(R.id.multiEditDisplayItems);
            TextView LabelHolder = MultiEditDialog.findViewById(R.id.multiEditModifyLabel);
            EditText NameHolder = MultiEditDialog.findViewById(R.id.multiEditNameHolder);
            EditText DescHolder = MultiEditDialog.findViewById(R.id.multiEditDescHolder);
            Button AddItemsHolder = MultiEditDialog.findViewById(R.id.multiEditAddItemsButton);
            Button CancelButton = MultiEditDialog.findViewById(R.id.multiEditCancelButton);
            Button SaveButton = MultiEditDialog.findViewById(R.id.multiEditSaveButton);
            //endregion

            //region Get Add Item Popup Views
            View AddItemOverlay = MultiEditDialog.findViewById(R.id.ME_AddItemViewBG);
            LinearLayout AddItemPopup = MultiEditDialog.findViewById(R.id.multiEditAddItemPopup);
            Spinner CategorySpinner = MultiEditDialog.findViewById(R.id.multiEditSetCategoryName);
            Spinner ItemSpinner = MultiEditDialog.findViewById(R.id.multiEditSetItemName);
            Spinner ItemOperationSpinner = MultiEditDialog.findViewById(R.id.multiEditItemOperationType);
            EditText ItemChangeHolder = MultiEditDialog.findViewById(R.id.multiEditItemCountChange);
            Button AddItemCancelButton = MultiEditDialog.findViewById(R.id.multiEditItemCancelButton);
            Button AddItemSaveButton = MultiEditDialog.findViewById(R.id.multiEditItemSaveButton);
            //endregion

            //region Lists and Index
            AtomicReference<Integer> ME_Item_Index = new AtomicReference<>();
            MutableLiveData<ArrayList<DataModel.DatabaseModel.Category>> categoryArrayList = new MutableLiveData<>();
            MutableLiveData<ArrayList<DataModel.DatabaseModel.Item>> itemArrayList = new MutableLiveData<>();
            ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEditContainer = new ArrayList<>();
            //endregion

            //region Set Values
            LabelHolder.setText("Add Multi-Edit");
            SaveButton.setVisibility(View.GONE);
            AddItemSaveButton.setVisibility(View.GONE);
            //endregion

            //region Functions For Main
            AddItemsHolder.setOnClickListener(v1 -> {
                // Open Add Item View
                AddItemOverlay.setVisibility(View.VISIBLE);
                AddItemPopup.setVisibility(View.VISIBLE);

                mViewModel.GetMultiEditData(Const.DataType.CATEGORY, "_").observe(getViewLifecycleOwner(), s -> {
                    // Parse Data
                    if(CustomFunctions.isVarSet(s)) {
                        DataModel.Recv.Category categoryData = new Gson().fromJson(s, DataModel.Recv.Category.class);

                        // Set Category Lists
                        categoryArrayList.setValue(categoryData.Category_List);
                    }
                });
            });

            categoryArrayList.observe(getViewLifecycleOwner(), categories -> {
                // Set Values For Category Spinner
                ArrayList<String> dataStringFromCategories = new ArrayList<>();
                for (DataModel.DatabaseModel.Category category : categories)
                    dataStringFromCategories.add(category.Name);
                CategorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, dataStringFromCategories));
            });
            //endregion

            //region Check For Main
            TextWatcher MainTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.MultiEditMainInputCheck(NameHolder, DescHolder)) {
                        SaveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        SaveButton.setVisibility(View.GONE);
                    }
                }
            };

            NameHolder.addTextChangedListener(MainTextWatcher);
            DescHolder.addTextChangedListener(MainTextWatcher);
            //endregion

            //region Function For Sub
            CategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Reset Item Spinner
                    ItemSpinner.setAdapter(null);

                    // Get Items
                    mViewModel.GetMultiEditData(Const.DataType.ITEM, CategorySpinner.getSelectedItem().toString()).observe(getViewLifecycleOwner(), s -> {
                        // Parse Data
                        if(CustomFunctions.isVarSet(s)) {
                            DataModel.Recv.Item itemData = new Gson().fromJson(s, DataModel.Recv.Item.class);

                            // Set Item Lists
                            itemArrayList.setValue(itemData.Item_List);
                        }
                    });

                    if(mViewModel.MultiEditSubInputCheck(CategorySpinner, ItemSpinner, ItemOperationSpinner, ItemChangeHolder)) {
                        AddItemSaveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        AddItemSaveButton.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            itemArrayList.observe(getViewLifecycleOwner(), items -> {
                // Set Values For Category Spinner
                ArrayList<String> dataStringFromItems = new ArrayList<>();
                for (DataModel.DatabaseModel.Item item : items)
                    dataStringFromItems.add(item.Name);
                ItemSpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, dataStringFromItems));
            });

            ItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(mViewModel.MultiEditSubInputCheck(CategorySpinner, ItemSpinner, ItemOperationSpinner, ItemChangeHolder)) {
                        AddItemSaveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        AddItemSaveButton.setVisibility(View.GONE);
                    }

                    ME_Item_Index.set(mViewModel.GetIndexFromItemList(itemArrayList.getValue(), ItemSpinner.getSelectedItem().toString()));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //endregion

            //region Check For Sub
            ItemChangeHolder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.MultiEditSubInputCheck(CategorySpinner, ItemSpinner, ItemOperationSpinner, ItemChangeHolder)) {
                        AddItemSaveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        AddItemSaveButton.setVisibility(View.GONE);
                    }
                }
            });
            //endregion

            //region Button Functions
            AddItemCancelButton.setOnClickListener(v1 -> {
                AddItemOverlay.setVisibility(View.GONE);
                AddItemPopup.setVisibility(View.GONE);
            });

            AddItemSaveButton.setOnClickListener(v1 -> {
                // Get Index as Reference
                DataModel.DatabaseModel.Item item = itemArrayList.getValue().get(ME_Item_Index.get());
                DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer multiEditItemContainer = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditItemContainer();
                multiEditItemContainer.ME_ItemName = item.Name;
                multiEditItemContainer.ME_ItemCount = Float.parseFloat(ItemOperationSpinner.getSelectedItem().toString() + ItemChangeHolder.getText().toString());
                multiEditItemContainer.ME_ItemPrice = item.Price;
                multiEditItemContainer.ME_ItemUnit = item.Unit;

                // Add To Multi-Edit Item Lists
                MultiEditContainer.add(multiEditItemContainer);

                // Remove Popup
                AddItemOverlay.setVisibility(View.GONE);
                AddItemPopup.setVisibility(View.GONE);

                // Load All Inside Item List
                mViewModel.LoadMultiEditItems(getContext(), DisplayItems, MultiEditContainer, true);
            });

            CancelButton.setOnClickListener(v1 -> {
                MultiEditDialog.dismiss();
            });

            SaveButton.setOnClickListener(v1 -> {
                mViewModel.AddMultiEdit(NameHolder.getText().toString(), DescHolder.getText().toString(), MultiEditContainer);
                MultiEditDialog.dismiss();
                // UserAppHandler.UI.AppUI().StartLoadingAnimation();
            });
            //endregion
        });

        BackButton.setOnClickListener(v -> {
            ViewMultiEditDetailed(false);
        });
    }

    private void ViewMultiEditDetailed(boolean Show) {
        if(Show) {
            MultiEdit_Overlay.setVisibility(View.VISIBLE);
            ShowContainer.setVisibility(View.VISIBLE);
        }
        else {
            MultiEdit_Overlay.setVisibility(View.GONE);
            ShowContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnDataMultiEditCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();
            if(CustomFunctions.isVarSet(s)) {
                DataModel.Recv.MultiEdit response = new Gson().fromJson(s, DataModel.Recv.MultiEdit.class);
                switch (response.Status) {
                    case 0: // 0   -> WORKING
                        MultiEditAdapter.UpdateList(response.MultiEditList);
                        break;
                    case 1: // 1   -> CODE/SERVER ERROR
                        UserAppHandler.UI.AppUI().SimpleDialog("Error", "Server Error");
                        break;
                }
            }
        });
    }

    @Override
    public void OnUseMultiEditCallback(LiveData<String> liveData, String Name, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> multiEditItemContainers) {
        liveData.observe(this, s -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();
            if(CustomFunctions.isVarSet(s)) {
                DataModel.Recv.MultiEdit response = new Gson().fromJson(s, DataModel.Recv.MultiEdit.class);
                switch (response.Status) {
                    case 0: // 0   -> WORKING
                        if(response.MultiEditErrorList.size() > 0) {
                            // DO NOT CLOSE YET
                            mViewModel.LoadMultiEditErrors(getContext(), DisplayObjects, response.MultiEditErrorList);
                            ProceedButton.setOnClickListener(v -> {
                                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                                mViewModel.UseMultiEdit(Name, multiEditItemContainers, true);
                            });
                        }
                        else {
                            // Use Function is Successful, Can Close Now
                            ShowContainer.setVisibility(View.GONE);
                        }
                        MultiEditAdapter.UpdateList(response.MultiEditList);
                        break;
                    case 1: // 1   -> CODE/SERVER ERROR
                        UserAppHandler.UI.AppUI().SimpleDialog("Error", "Server Error");
                        break;
                }
            }
        });
    }

    @Override
    public void OnShowMultiEditCallback(ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> ViewedDataInstance, int position) {
        NameHolder.setText(ViewedDataInstance.get(position).Name);
        DescHolder.setText(ViewedDataInstance.get(position).Desc);
        mViewModel.LoadMultiEditItems(getContext(), DisplayObjects, ViewedDataInstance.get(position).MultiEditItemsList, false);
        ProceedButton.setOnClickListener(v -> {
            AlertDialog builder = new AlertDialog.Builder(getContext())
                    .setTitle("Use MultiEdit")
                    .setMessage(String.format("Use %s (Will affect item counts)?", ViewedDataInstance.get(position).Name))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        UserAppHandler.UI.AppUI().StartLoadingAnimation();
                        mViewModel.UseMultiEdit(ViewedDataInstance.get(position).Name, ViewedDataInstance.get(position).MultiEditItemsList, false);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });
        ViewMultiEditDetailed(true);
    }

    @Override
    public void onRefresh() {
        mViewModel.LoadMultiEdit();
        MultiEditSwipeRefresh.setRefreshing(false);
    }
}