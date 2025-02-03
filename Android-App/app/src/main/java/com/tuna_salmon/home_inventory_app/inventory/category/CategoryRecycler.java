package com.tuna_salmon.home_inventory_app.inventory.category;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecycler extends RecyclerView.Adapter<CategoryRecycler.CategoryViewHolder> implements Filterable {

    private final Context ctx;
    private final CategoryViewModel mViewModel;
    private final NavController navController;

    private ArrayList<DataModel.DatabaseModel.Category> OriginalData;
    private ArrayList<DataModel.DatabaseModel.Category> ViewedData;

    public CategoryRecycler(Context ctx, ArrayList<DataModel.DatabaseModel.Category> Data, NavController navController, CategoryViewModel mViewModel) {
        this.ctx = ctx;
        this.navController = navController;
        this.mViewModel = mViewModel;

        this.OriginalData = Data;
        this.ViewedData = new ArrayList<>(this.OriginalData);
    }

    public void UpdateList(ArrayList<DataModel.DatabaseModel.Category> newList) {
        this.OriginalData = newList;
        this.ViewedData = new ArrayList<>(this.OriginalData);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(ctx).inflate(R.layout.model_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.Category_SubContainer.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(ViewedData.get(position).Color)));
        holder.Category_Name_Holder.setText(ViewedData.get(position).Name);
        holder.Category_ItemCount_Holder.setText(Integer.toString(ViewedData.get(position).Count));

        holder.Category_SubContainer.setOnClickListener(v -> {
            UserAppHandler.UI.AppUI().SimpleToast("Going to: " + ViewedData.get(position).Name, Toast.LENGTH_SHORT);
            UserAppHandler.Data.AppData().CurrentCategory = ViewedData.get(position).Name;
            navController.navigate(R.id.action_categoryFragment_to_itemFragment);
        });

        holder.Category_Settings_Holder.setOnClickListener(v -> {
            // Create Alert Dialog of Category Modify
            AlertDialog CategoryDialog = new AlertDialog.Builder(ctx)
                    .setView(LayoutInflater.from(ctx).inflate(R.layout.sub_fragment_modify_category, null))
                    .setCancelable(false)
                    .create();
            CategoryDialog.show();

            // Get Views
            TextView Title = CategoryDialog.findViewById(R.id.categoryModifyLabel);
            EditText Category_Name = CategoryDialog.findViewById(R.id.categoryAddCategoryName);
            Button CancelButton = CategoryDialog.findViewById(R.id.categoryAddCategoryCancelButton);
            Button UpdateButton = CategoryDialog.findViewById(R.id.categoryAddCategoryAddButton);

            // Set Values
            Title.setText("Modify Category");
            UpdateButton.setVisibility(View.GONE);

            Category_Name.setText(ViewedData.get(position).Name);

            // Set Live Change Logic
            Category_Name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(mViewModel.isCategoryNameValid(s.toString())) {
                        UpdateButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        UpdateButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Set Button Listeners
            CancelButton.setOnClickListener(v1 -> {
                CategoryDialog.dismiss();
            });

            UpdateButton.setOnClickListener(v1 -> {
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                mViewModel.EditCategory(ViewedData.get(position).Name, Category_Name.getText().toString());
                CategoryDialog.dismiss();
            });
        });

        holder.Category_Delete_Holder.setOnClickListener(v -> {
            // Prompt App Delete
            AlertDialog builder = new AlertDialog.Builder(ctx)
                    .setTitle("Delete Item")
                    .setMessage(String.format("Permanently Delete %s?", ViewedData.get(position).Name))
                    .setPositiveButton("Yes", (dialog, which) -> {
                mViewModel.DeleteCategory(ViewedData.get(position).Name);
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

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public CardView Category_SubContainer;
        public TextView Category_Name_Holder;
        public TextView Category_ItemCount_Holder;
        public ImageView Category_Settings_Holder;
        public ImageView Category_Delete_Holder;

        public CategoryViewHolder(@NonNull View categoryView) {
            super(categoryView);
            Category_SubContainer = categoryView.findViewById(R.id.categorySubContainer);
            Category_Name_Holder = categoryView.findViewById(R.id.categoryNameHolder);
            Category_ItemCount_Holder = categoryView.findViewById(R.id.categoryItemCountHolder);
            Category_Settings_Holder = categoryView.findViewById(R.id.categorySettingsHolder);
            Category_Delete_Holder = categoryView.findViewById(R.id.categoryDeleteHolder);
        }
    }

    @Override
    public Filter getFilter() {
        return CategoryFilter;
    }

    private Filter CategoryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ViewedData = new ArrayList<>(OriginalData);

            List<DataModel.DatabaseModel.Category> filteredList = new ArrayList<>();
            if(constraint != null || constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(DataModel.DatabaseModel.Category category : ViewedData) {
                    if(category.Name.toLowerCase().contains(filterPattern))
                        filteredList.add(category);
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
