package com.tuna_salmon.home_inventory_app.multi_edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.inventory.item.ItemRecycler;
import com.tuna_salmon.home_inventory_app.inventory.item.ItemViewModel;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MultiEditRecycler extends RecyclerView.Adapter<MultiEditRecycler.MultiEditViewHolder> implements Filterable {

    private final Context ctx;
    private final MultiEditViewModel mViewModel;
    private final NavController navController;

    public LifecycleOwner getViewLifecycleOwner;

    private ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> OriginalData;
    private ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> ViewedData;

    public MultiEditRecycler(Context ctx, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> Data, NavController navController, MultiEditViewModel mViewModel, LifecycleOwner getViewLifecycleOwner) {
        this.ctx = ctx;
        this.navController = navController;
        this.mViewModel = mViewModel;

        this.OriginalData = Data;
        this.ViewedData = new ArrayList<>(this.OriginalData);

        this.getViewLifecycleOwner = getViewLifecycleOwner;
    }

    public void UpdateList(ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> newList) {
        this.OriginalData = newList;
        this.ViewedData = new ArrayList<>(this.OriginalData);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MultiEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MultiEditRecycler.MultiEditViewHolder(LayoutInflater.from(ctx).inflate(R.layout.model_multi_edit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MultiEditViewHolder holder, int position) {
        holder.MultiEditContainer.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(ViewedData.get(position).Color)));
        holder.MultiEditName.setText(ViewedData.get(position).Name);

        holder.MultiEditContainer.setOnClickListener(v -> {
            mViewModel.ShowMultiEdit(ViewedData, position);
        });

        holder.MultiEditSettings.setOnClickListener(v -> {
            AlertDialog MultiEditDialog = new AlertDialog.Builder(ctx, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
                    .setView(LayoutInflater.from(ctx).inflate(R.layout.sub_fragment_modify_multi_edit, null))
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
            Button UpdateButton = MultiEditDialog.findViewById(R.id.multiEditSaveButton);
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
            ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEditContainer;
            //endregion

            //region Set Values
            LabelHolder.setText("Edit Multi-Edit");
            UpdateButton.setVisibility(View.GONE);
            AddItemSaveButton.setVisibility(View.GONE);

            NameHolder.setText(ViewedData.get(position).Name);
            DescHolder.setText(ViewedData.get(position).Desc);
            MultiEditContainer = ViewedData.get(position).MultiEditItemsList;
            mViewModel.LoadMultiEditItems(ctx, DisplayItems, MultiEditContainer, true);
            //endregion

            //region Functions For Main
            AddItemsHolder.setOnClickListener(v1 -> {
                // Open Add Item View
                AddItemOverlay.setVisibility(View.VISIBLE);
                AddItemPopup.setVisibility(View.VISIBLE);

                mViewModel.GetMultiEditData(Const.DataType.CATEGORY, "_").observe(this.getViewLifecycleOwner, s -> {
                    // Parse Data
                    if(CustomFunctions.isVarSet(s)) {
                        DataModel.Recv.Category categoryData = new Gson().fromJson(s, DataModel.Recv.Category.class);

                        // Set Category Lists
                        categoryArrayList.setValue(categoryData.Category_List);
                    }
                });
            });

            categoryArrayList.observe(getViewLifecycleOwner, categories -> {
                // Set Values For Category Spinner
                ArrayList<String> dataStringFromCategories = new ArrayList<>();
                for (DataModel.DatabaseModel.Category category : categories)
                    dataStringFromCategories.add(category.Name);
                CategorySpinner.setAdapter(new ArrayAdapter<>(ctx, R.layout.support_simple_spinner_dropdown_item, dataStringFromCategories));
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
                        UpdateButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        UpdateButton.setVisibility(View.GONE);
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
                    mViewModel.GetMultiEditData(Const.DataType.ITEM, CategorySpinner.getSelectedItem().toString()).observe(getViewLifecycleOwner, s -> {
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

            itemArrayList.observe(getViewLifecycleOwner, items -> {
                // Set Values For Category Spinner
                ArrayList<String> dataStringFromItems = new ArrayList<>();
                for (DataModel.DatabaseModel.Item item : items)
                    dataStringFromItems.add(item.Name);
                ItemSpinner.setAdapter(new ArrayAdapter<>(ctx, R.layout.support_simple_spinner_dropdown_item, dataStringFromItems));
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
                mViewModel.LoadMultiEditItems(ctx, DisplayItems, MultiEditContainer, true);
            });

            CancelButton.setOnClickListener(v1 -> {
                MultiEditDialog.dismiss();
            });

            UpdateButton.setOnClickListener(v1 -> {
                mViewModel.EditMultiEdit(ViewedData.get(position).Name, NameHolder.getText().toString(), DescHolder.getText().toString(), MultiEditContainer);
                MultiEditDialog.dismiss();
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
            });
            //endregion
        });

        holder.MultiEditDelete.setOnClickListener(v -> {
            // Prompt App Delete
            AlertDialog builder = new AlertDialog.Builder(ctx)
                    .setTitle("Delete MultiEdit")
                    .setMessage(String.format("Permanently Delete %s?", ViewedData.get(position).Name))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.DeleteMultiEdit(ViewedData.get(position).Name);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return ViewedData.size();
    }

    public class MultiEditViewHolder extends RecyclerView.ViewHolder {

        public CardView MultiEditContainer;
        public TextView MultiEditName;
        public ImageView MultiEditSettings;
        public ImageView MultiEditDelete;

        public MultiEditViewHolder(@NonNull View multiEditView) {
            super(multiEditView);
            MultiEditContainer = multiEditView.findViewById(R.id.multiEditContainer);
            MultiEditName = multiEditView.findViewById(R.id.multiEditNameHolder);
            MultiEditSettings = multiEditView.findViewById(R.id.multiEditSettingsHolder);
            MultiEditDelete = multiEditView.findViewById(R.id.multiEditDeleteHolder);
        }
    }

    @Override
    public Filter getFilter() {
        return MultiEditFilter;
    }

    private Filter MultiEditFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ViewedData = new ArrayList<>(OriginalData);

            List<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> filteredList = new ArrayList<>();
            if(constraint != null || constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(DataModel.DatabaseModel.MultiEdit.MultiEditContainer multiEdit : ViewedData) {
                    if(multiEdit.Name.toLowerCase().contains(filterPattern))
                        filteredList.add(multiEdit);
                }
            }
            else
                filteredList.addAll(ViewedData);
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ViewedData.clear();
            ViewedData.addAll((List) results.values);

            notifyDataSetChanged();
        }
    };
}
