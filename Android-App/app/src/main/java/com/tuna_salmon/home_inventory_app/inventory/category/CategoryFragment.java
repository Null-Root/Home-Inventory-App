package com.tuna_salmon.home_inventory_app.inventory.category;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.FragmentWithFab;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends FragmentWithFab implements CategoryListener, SwipeRefreshLayout.OnRefreshListener {

    private CategoryViewModel mViewModel;
    private NavController navController;

    private SwipeRefreshLayout CategorySwipeRefresh;
    private CategoryRecycler CategoryAdapter;

    private AlertDialog CategoryDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_categoryFragment_to_mainFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        //region Fab Setup View
        FabOverlay = view.findViewById(R.id.CategoryOverlay);
        Main_Fab = view.findViewById(R.id.categoryMainFab);
        //endregion

        // Setup Alert Dialog
        CategoryDialog = new AlertDialog.Builder(getContext())
                .setView(getLayoutInflater().inflate(R.layout.sub_fragment_modify_category, null))
                .setCancelable(false)
                .create();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        mViewModel.categoryListener = this;

        //region Category Setup View
        SearchView searchView = view.findViewById(R.id.categorySearchView);
        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        CategoryAdapter = new CategoryRecycler(getContext(), new ArrayList<>(), navController, mViewModel);
        CategorySwipeRefresh = view.findViewById(R.id.categorySwipeRefreshLayout);
        //endregion

        categoryRecyclerView.setAdapter(CategoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategorySwipeRefresh.setOnRefreshListener(this);

        // Primary Load
        mViewModel.LoadCategory();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CategoryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CategoryAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Main_Fab.setOnClickListener(v -> {

            CategoryDialog.show();

            // Get Views
            TextView Title = CategoryDialog.findViewById(R.id.categoryModifyLabel);
            EditText Category_Name = CategoryDialog.findViewById(R.id.categoryAddCategoryName);
            Button CancelButton = CategoryDialog.findViewById(R.id.categoryAddCategoryCancelButton);
            Button SaveButton = CategoryDialog.findViewById(R.id.categoryAddCategoryAddButton);

            // Set Values
            Title.setText("Add Category");
            SaveButton.setVisibility(View.GONE);

            // Set Live Change Logic
            Category_Name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.isCategoryNameValid(s.toString()))
                        SaveButton.setVisibility(View.VISIBLE);
                    else
                        SaveButton.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Set Button Listeners
            CancelButton.setOnClickListener(v1 -> {
                CategoryDialog.dismiss();
            });

            SaveButton.setOnClickListener(v1 -> {
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                mViewModel.AddCategory(Category_Name.getText().toString());
                CategoryDialog.dismiss();
            });
        });
    }

    @Override
    public void OnDataCategoryCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();
            if(mViewModel.CheckCategoryResultCallback(s)) {
                DataModel.Recv.Category response = new Gson().fromJson(s, DataModel.Recv.Category.class);
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

    private void DataProcess(DataModel.Recv.Category response) {
        switch (response.AuthPermission) {
            case 0:
                // Show That Not Allowed
                Main_Fab.setVisibility(View.GONE);
                break;
            case 1:
                // Disable Add/Edit
                Main_Fab.setVisibility(View.GONE);

                if(!response.Exists)
                    CategoryAdapter.UpdateList(response.Category_List);
                else
                    UserAppHandler.UI.AppUI().SimpleDialog("Duplicate Error", "Category Name was a duplicate");
                break;
            case 2:
                if(!response.Exists)
                    CategoryAdapter.UpdateList(response.Category_List);
                else
                    UserAppHandler.UI.AppUI().SimpleDialog("Duplicate Error", "Category Name was a duplicate");
                break;
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.LoadCategory();
        CategorySwipeRefresh.setRefreshing(false);
    }
}