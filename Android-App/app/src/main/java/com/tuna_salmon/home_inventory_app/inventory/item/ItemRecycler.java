package com.tuna_salmon.home_inventory_app.inventory.item;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.FragmentWithFab;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemRecycler extends RecyclerView.Adapter<ItemRecycler.ItemViewHolder> implements Filterable {

    private final Context ctx;
    private final ItemViewModel mViewModel;
    private final NavController navController;

    private ArrayList<DataModel.DatabaseModel.Item> OriginalData;
    private ArrayList<DataModel.DatabaseModel.Item> ViewedData;

    private boolean ShowItemDetailed = false;

    public ItemRecycler(Context ctx, ArrayList<DataModel.DatabaseModel.Item> Data, NavController navController, ItemViewModel mViewModel) {
        this.ctx = ctx;
        this.navController = navController;
        this.mViewModel = mViewModel;

        this.OriginalData = Data;
        this.ViewedData = new ArrayList<>(this.OriginalData);
    }

    public void UpdateList(ArrayList<DataModel.DatabaseModel.Item> newList) {
        this.OriginalData = newList;
        this.ViewedData = new ArrayList<>(this.OriginalData);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemRecycler.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemRecycler.ItemViewHolder(LayoutInflater.from(ctx).inflate(R.layout.model_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecycler.ItemViewHolder holder, int position) {
        holder.Item_SubContainer.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(ViewedData.get(position).Color)));
        holder.Item_Name_Holder.setText(ViewedData.get(position).Name);
        holder.Item_Count_Holder.setText(Double.toString(ViewedData.get(position).Count));

        holder.Item_SubContainer.setOnClickListener(v -> {
            ShowItemDetailed = true;
            mViewModel.UseItem(ViewedData, position); // Call From ViewModel
        });

        holder.Item_Settings_Holder.setOnClickListener(v -> {
            // Create Alert Dialog of Category Modify
            AlertDialog ItemDialog = new AlertDialog.Builder(ctx)
                    .setView(LayoutInflater.from(ctx).inflate(R.layout.sub_fragment_modify_item, null))
                    .setCancelable(false)
                    .create();
            ItemDialog.show();

            // Get Views
            TextView Title = ItemDialog.findViewById(R.id.itemModifyLabel);
            EditText Item_Name = ItemDialog.findViewById(R.id.modifyItemNameHolder);
            EditText Item_Count = ItemDialog.findViewById(R.id.modifyItemCountHolder);
            EditText Item_CriticalCount = ItemDialog.findViewById(R.id.modifyItemCriticalCountHolder);
            EditText Item_Price = ItemDialog.findViewById(R.id.modifyItemPriceHolder);
            Spinner Item_Unit = ItemDialog.findViewById(R.id.modifyItemUnitHolder);
            Button CancelButton = ItemDialog.findViewById(R.id.modifyItemCancelButton);
            Button UpdateButton = ItemDialog.findViewById(R.id.modifyItemSaveButton);

            // Set Values
            Title.setText("Update Item");
            UpdateButton.setVisibility(View.GONE);

            Item_Name.setText(ViewedData.get(position).Name);
            Item_Count.setText(Double.toString(ViewedData.get(position).Count));
            Item_CriticalCount.setText(Double.toString(ViewedData.get(position).CriticalCount));
            Item_Price.setText(Double.toString(ViewedData.get(position).Price));
            Item_Unit.setSelection(ArrayAdapter.createFromResource(ctx, R.array.def_item_units, R.layout.support_simple_spinner_dropdown_item).getPosition(ViewedData.get(position).Unit));

            // Checks
            TextWatcher CheckInput = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.isItemNameValid(Item_Name, Item_Count, Item_CriticalCount, Item_Price)) {
                        UpdateButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        UpdateButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            Item_Unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    UpdateButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Set Live Change Logic
            Item_Name.addTextChangedListener(CheckInput);
            Item_Count.addTextChangedListener(CheckInput);
            Item_CriticalCount.addTextChangedListener(CheckInput);
            Item_Price.addTextChangedListener(CheckInput);

            // Set Button Listeners
            CancelButton.setOnClickListener(v1 -> {
                ItemDialog.dismiss();
            });

            UpdateButton.setOnClickListener(v1 -> {
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                mViewModel.EditItem(
                        ViewedData.get(position).Name,
                        Item_Name.getText().toString(),
                        Float.parseFloat(Item_Count.getText().toString()),
                        Float.parseFloat(Item_CriticalCount.getText().toString()),
                        Float.parseFloat(Item_Price.getText().toString()),
                        Item_Unit.getSelectedItem().toString());
                ItemDialog.dismiss();
            });
        });

        holder.Item_Delete_Holder.setOnClickListener(v -> {
            // Prompt App Delete
            AlertDialog builder = new AlertDialog.Builder(ctx)
                    .setTitle("Delete Item")
                    .setMessage(String.format("Permanently Delete %s?", ViewedData.get(position).Name))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.DeleteItem(ViewedData.get(position).Name);
                    })
                    .setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView Item_SubContainer;
        public TextView Item_Name_Holder;
        public TextView Item_Count_Holder;
        public ImageView Item_Settings_Holder;
        public ImageView Item_Delete_Holder;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Item_SubContainer = itemView.findViewById(R.id.itemSubContainer);
            Item_Name_Holder = itemView.findViewById(R.id.itemNameHolder);
            Item_Count_Holder = itemView.findViewById(R.id.itemCountHolder);
            Item_Settings_Holder = itemView.findViewById(R.id.itemSettingsHolder);
            Item_Delete_Holder = itemView.findViewById(R.id.itemDeleteHolder);
        }
    }

    @Override
    public Filter getFilter() {
        return ItemFilter;
    }

    private Filter ItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ViewedData = new ArrayList<>(OriginalData);

            List<DataModel.DatabaseModel.Item> filteredList = new ArrayList<>();
            if(constraint != null || constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(DataModel.DatabaseModel.Item item : ViewedData) {
                    if(item.Name.toLowerCase().contains(filterPattern))
                        filteredList.add(item);
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
