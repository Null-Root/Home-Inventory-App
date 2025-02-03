package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SharedInventoryDataRecycler extends RecyclerView.Adapter<SharedInventoryDataRecycler.SharedInventoryViewHolder> implements Filterable {

    private final Context ctx;
    private ArrayList<DataModel.DatabaseModel.ShareData.InventoryProfileData> OriginalData;
    private ArrayList<DataModel.DatabaseModel.ShareData.InventoryProfileData> ViewedData;
    private final SharedInventoryDataAccessViewModel mViewModel;
    private final boolean canEditPermissions;

    public SharedInventoryDataRecycler(Context ctx, ArrayList<DataModel.DatabaseModel.ShareData.InventoryProfileData> Data, SharedInventoryDataAccessViewModel mViewModel, boolean CanEditPermissions) {
        this.ctx = ctx;
        this.mViewModel = mViewModel;
        this.OriginalData = Data;
        this.ViewedData = new ArrayList<>(this.OriginalData);
        this.canEditPermissions = CanEditPermissions;
    }

    public void UpdateList(ArrayList<DataModel.DatabaseModel.ShareData.InventoryProfileData> newList) {
        this.OriginalData = newList;
        this.ViewedData = new ArrayList<>(this.OriginalData);

        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public SharedInventoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SharedInventoryViewHolder(LayoutInflater.from(ctx).inflate(R.layout.model_inventory_profile_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SharedInventoryViewHolder holder, int position) {
        holder.BasicProfileHolder.setText(ViewedData.get(position).Name);

        if(this.canEditPermissions) {
            holder.PermissionsReadWrite.setVisibility(View.VISIBLE);
            holder.PermissionsReadWrite.setSelection(ViewedData.get(position).Permission);

            holder.PermissionsReadWrite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mViewModel.MyInventoryUpdateProfileData(ViewedData.get(position).UniqueID, holder.PermissionsReadWrite.getSelectedItemPosition());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        else {
            holder.PermissionsReadOnly.setVisibility(View.VISIBLE);
            holder.PermissionsReadOnly.setText(Integer.toString(ViewedData.get(position).Permission));
        }

        holder.DeleteProfileButtons.setOnClickListener(v -> {
            mViewModel.MyInventoryDeleteProfileData(ViewedData.get(position).Name, ViewedData.get(position).UniqueID);
        });
    }

    @Override
    public int getItemCount() {
        return ViewedData.size();
    }

    public class SharedInventoryViewHolder extends RecyclerView.ViewHolder {

        private TextView BasicProfileHolder;
        private TextView PermissionsReadOnly;
        private Spinner PermissionsReadWrite;
        private Button DeleteProfileButtons;

        public SharedInventoryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            BasicProfileHolder = itemView.findViewById(R.id.inventoryProfileDataMainData);
            PermissionsReadOnly = itemView.findViewById(R.id.inventoryProfileDataReadOnly);
            PermissionsReadWrite = itemView.findViewById(R.id.inventoryProfileDataReadWrite);
            DeleteProfileButtons = itemView.findViewById(R.id.inventoryProfileDataRemoveUser);
        }
    }

    @Override
    public Filter getFilter() {
        return MyInventoryProfileFilter;
    }

    private final Filter MyInventoryProfileFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ViewedData = new ArrayList<>(OriginalData);

            List<DataModel.DatabaseModel.ShareData.InventoryProfileData> filteredList = new ArrayList<>();
            if(constraint != null || constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(DataModel.DatabaseModel.ShareData.InventoryProfileData profileData : ViewedData) {
                    if((profileData.Name.toLowerCase() + "#" + profileData.UniqueID.toLowerCase()).contains(filterPattern))
                        filteredList.add(profileData);
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
