package com.tuna_salmon.home_inventory_app.multi_edit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.inventory.InventoryRepository;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MultiEditViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private InventoryRepository mInventoryRepository;
    public MultiEditListener multiEditListener;

    @Inject
    public MultiEditViewModel(InventoryRepository inventoryRepository) {
        this.mInventoryRepository = inventoryRepository;
    }

    public boolean MultiEditMainInputCheck(EditText NameHolder, EditText DescHolder) {
        boolean IsValidName = false, IsValidDesc = false;

        String NameStr = NameHolder.getText().toString();
        if(CustomFunctions.isVarSet(NameStr)) {
            if(CustomFunctions.isValidStr(NameStr, 1, 12)) {
                IsValidName = true;
            }
        }

        String DescStr = DescHolder.getText().toString();
        if(CustomFunctions.isVarSet(DescStr)) {
            if(CustomFunctions.isValidStr(DescStr, 1, 12)) {
                IsValidDesc = true;
            }
        }


        if(!IsValidName) {
            NameHolder.setError("Invalid Name");
        }

        if(!IsValidDesc) {
            DescHolder.setError("Invalid Name");
        }

        if(IsValidName && IsValidDesc) {
            return true;
        }
        return false;
    }

    public boolean MultiEditSubInputCheck(Spinner CategoryHolder, Spinner ItemHolder, Spinner OperationHolder, EditText ItemCountChange) {
        boolean IsValidCategory = false, IsValidItem = false, IsValidOperation = false, IsValidItemCountChange = false;

        if(CategoryHolder.getCount() != 0) {
            IsValidCategory = true;
        }

        if(ItemHolder.getCount() != 0) {
            IsValidItem = true;
        }

        if(OperationHolder.getCount() != 0) {
            IsValidOperation = true;
        }

        String ItemCountChangeStr = ItemCountChange.getText().toString();
        if(CustomFunctions.isVarSet(ItemCountChangeStr)) {
            if(CustomFunctions.isValidNumber(ItemCountChangeStr)) {
                IsValidItemCountChange = true;
            }
        }

        if(IsValidCategory && IsValidItem && IsValidOperation && IsValidItemCountChange) {
            return true;
        }
        return false;
    }

    public void AddMultiEdit(String Name, String Desc, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEditItemContainer) {
        DataModel.Send.MultiEdit multiEdit = new DataModel().new Send().new MultiEdit();
        multiEdit.Token = Const.App.TOKEN;
        multiEdit.Action = Const.ActionTypes.ADD;
        multiEdit.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        multiEdit.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        multiEdit.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        multiEdit.MultiEdit_Name = Name;
        multiEdit.MultiEdit_Desc = Desc;
        multiEdit.MultiEdit_ItemContainers = MultiEditItemContainer;

        multiEditListener.OnDataMultiEditCallback(mInventoryRepository.MakeMultiEditRequest(new Gson().toJson(multiEdit)));
    }

    public void LoadMultiEdit() {
        DataModel.Send.MultiEdit multiEdit = new DataModel().new Send().new MultiEdit();
        multiEdit.Token = Const.App.TOKEN;
        multiEdit.Action = Const.ActionTypes.LOAD;
        multiEdit.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        multiEdit.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        multiEdit.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        multiEditListener.OnDataMultiEditCallback(mInventoryRepository.MakeMultiEditRequest(new Gson().toJson(multiEdit)));
    }

    public void EditMultiEdit(String Ref_Name, String Name, String Desc, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEditItemContainer) {
        DataModel.Send.MultiEdit multiEdit = new DataModel().new Send().new MultiEdit();
        multiEdit.Token = Const.App.TOKEN;
        multiEdit.Action = Const.ActionTypes.EDIT;
        multiEdit.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        multiEdit.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        multiEdit.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        multiEdit.Ref_MultiEdit_Name = Ref_Name;
        multiEdit.MultiEdit_Name = Name;
        multiEdit.MultiEdit_Desc = Desc;
        multiEdit.MultiEdit_ItemContainers = MultiEditItemContainer;

        multiEditListener.OnDataMultiEditCallback(mInventoryRepository.MakeMultiEditRequest(new Gson().toJson(multiEdit)));
    }

    public void DeleteMultiEdit(String Name) {
        DataModel.Send.MultiEdit multiEdit = new DataModel().new Send().new MultiEdit();
        multiEdit.Token = Const.App.TOKEN;
        multiEdit.Action = Const.ActionTypes.DELETE;
        multiEdit.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        multiEdit.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        multiEdit.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        multiEdit.MultiEdit_Name = Name;
        multiEditListener.OnDataMultiEditCallback(mInventoryRepository.MakeMultiEditRequest(new Gson().toJson(multiEdit)));
    }

    public void UseMultiEdit(String Name, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEditItemContainer, boolean SkipCheck) {
        DataModel.Send.MultiEdit multiEdit = new DataModel().new Send().new MultiEdit();
        multiEdit.Token = Const.App.TOKEN;
        multiEdit.Action = Const.ActionTypes.USE;
        multiEdit.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        multiEdit.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        multiEdit.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        multiEdit.MultiEdit_Name = Name;
        multiEdit.CheckInputs = !SkipCheck;
        multiEdit.MultiEdit_ItemContainers = MultiEditItemContainer;

        multiEditListener.OnUseMultiEditCallback(mInventoryRepository.MakeMultiEditRequest(new Gson().toJson(multiEdit)), Name, MultiEditItemContainer);
    }

    public void ShowMultiEdit(ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> ViewedDataInstance, int position) {
        multiEditListener.OnShowMultiEditCallback(ViewedDataInstance, position);
    }

    public boolean LoadMultiEditItems(Context ctx,
                                      LinearLayout MainLayout,
                                      ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> DataList,
                                      boolean LoadDeleteButton) {
        try
        {
            MainLayout.removeAllViews();

            if (MainLayout.getChildCount() == 0)
            {
                // Get Templates
                if (DataList.size() != 0)
                {
                    for (DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer data : DataList)
                    {
                        MainLayout.addView(MultiEditItem(ctx, MainLayout, DataList, data, LoadDeleteButton));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    public CardView MultiEditItem(Context ctx,
                                  LinearLayout MainLayoutForRecursiveLoadCall,
                                  ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> ArrayListForRecursiveLoadCall,
                                  DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer MultiEditItemContainer,
                                  boolean LoadDeleteButton) {
        //region Main Card View
        CardView MainCardViewContainer = new CardView(ctx);
        MainCardViewContainer.setContentPadding(0, CustomFunctions.Sp_To_Px(ctx, 5), 0, CustomFunctions.Sp_To_Px(ctx, 5));
        MainCardViewContainer.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#d5f4f5")));
        MainCardViewContainer.setRadius(CustomFunctions.Sp_To_Px(ctx, 4));
        MainCardViewContainer.setCardElevation(CustomFunctions.Sp_To_Px(ctx, 2));
        MainCardViewContainer.setMaxCardElevation(CustomFunctions.Sp_To_Px(ctx, 10));
        MainCardViewContainer.setPreventCornerOverlap(false);
        LinearLayout.LayoutParams MainCardViewLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MainCardViewLayout.gravity = Gravity.CENTER_HORIZONTAL;
        MainCardViewLayout.setMargins(CustomFunctions.Sp_To_Px(ctx, 4),
                CustomFunctions.Sp_To_Px(ctx, 4),
                CustomFunctions.Sp_To_Px(ctx, 4),
                CustomFunctions.Sp_To_Px(ctx, 4));
        MainCardViewContainer.setLayoutParams(MainCardViewLayout);
        //endregion

        //region Item Name Holder
        TextView ItemNameHolder = new TextView(ctx);
        ItemNameHolder.setText(MultiEditItemContainer.ME_ItemName);
        ItemNameHolder.setTextSize(CustomFunctions.Sp_To_Px(ctx, 8));
        FrameLayout.LayoutParams ItemNameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ItemNameLayout.gravity = Gravity.CENTER_VERTICAL;
        ItemNameLayout.setMargins(CustomFunctions.Sp_To_Px(ctx, 5), 0,
                CustomFunctions.Sp_To_Px(ctx, 5), 0);
        ItemNameHolder.setLayoutParams(ItemNameLayout);
        //endregion

        //region Item Change Count Holder
        TextView ItemChangeCountHolder = new TextView(ctx);
        ItemChangeCountHolder.setText(String.format("%s %s", MultiEditItemContainer.ME_ItemCount, MultiEditItemContainer.ME_ItemUnit));
        ItemChangeCountHolder.setTextSize(CustomFunctions.Sp_To_Px(ctx, 9));
        FrameLayout.LayoutParams ItemChangeCountLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ItemChangeCountLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        ItemChangeCountLayout.rightMargin = CustomFunctions.Sp_To_Px(ctx, 40);
        ItemChangeCountHolder.setLayoutParams(ItemChangeCountLayout);
        //endregion

        //region Remove Item Button
        ImageButton RemoveItemButton = new ImageButton(ctx);
        if (LoadDeleteButton)
        {
            RemoveItemButton.setContentDescription("Remove Item From Collection");
            RemoveItemButton.setBackgroundResource(android.R.drawable.ic_delete);
            FrameLayout.LayoutParams RemoveItemLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RemoveItemLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            RemoveItemLayout.rightMargin = CustomFunctions.Sp_To_Px(ctx, 7.5f);
            RemoveItemButton.setLayoutParams(RemoveItemLayout);
        }
        //endregion

        //region Functions
        if(LoadDeleteButton) {
            RemoveItemButton.setOnClickListener(v -> {
                // Remove "this" item
                ArrayListForRecursiveLoadCall.remove(MultiEditItemContainer);
                // Load Again the List
                LoadMultiEditItems(ctx, MainLayoutForRecursiveLoadCall, ArrayListForRecursiveLoadCall, LoadDeleteButton);
            });
        }
        //endregion

        //region Add All Views
        MainCardViewContainer.addView(ItemNameHolder);
        MainCardViewContainer.addView(ItemChangeCountHolder);
        if(LoadDeleteButton)
        {
            MainCardViewContainer.addView(RemoveItemButton);
        }
        //endregion

        return MainCardViewContainer;
    }

    public boolean LoadMultiEditErrors(Context ctx,
                                       LinearLayout MainLayout,
                                       ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditErrorList> ErrorList) {
        try
        {
            MainLayout.removeAllViews();

            if (MainLayout.getChildCount() == 0)
            {
                // Get Templates
                if (ErrorList.size() != 0)
                {
                    for(DataModel.DatabaseModel.MultiEdit.MultiEditErrorList ErrorInfo : ErrorList)
                    {
                        MainLayout.addView(MultiEditErrorLayout(ctx, ErrorInfo));
                    }
                }
            }
        }
        catch(Exception ex)
        {
            return false;
        }
        return true;
    }

    private CardView MultiEditErrorLayout(Context ctx, DataModel.DatabaseModel.MultiEdit.MultiEditErrorList MultiEditErrorData)
    {
        //region Main Card View
        CardView MainCardViewContainer = new CardView(ctx);
        MainCardViewContainer.setContentPadding(CustomFunctions.Sp_To_Px(ctx, 7.5f),
                CustomFunctions.Sp_To_Px(ctx, 7.5f),
                CustomFunctions.Sp_To_Px(ctx, 7.5f),
                CustomFunctions.Sp_To_Px(ctx, 7.5f));
        MainCardViewContainer.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#d5f4f5")));
        MainCardViewContainer.setRadius(CustomFunctions.Sp_To_Px(ctx, 2.5f));
        MainCardViewContainer.setCardElevation(CustomFunctions.Sp_To_Px(ctx, 4));
        MainCardViewContainer.setPreventCornerOverlap(false);
        LinearLayout.LayoutParams MainCardViewLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MainCardViewLayout.gravity = Gravity.CENTER_HORIZONTAL;
        MainCardViewLayout.setMargins(CustomFunctions.Sp_To_Px(ctx, 8),
                CustomFunctions.Sp_To_Px(ctx, 8),
                CustomFunctions.Sp_To_Px(ctx, 8),
                CustomFunctions.Sp_To_Px(ctx, 8));
        MainCardViewContainer.setLayoutParams(MainCardViewLayout);
        //endregion

        //region Item Name
        TextView ItemNameHolder = new TextView(ctx);
        ItemNameHolder.setText(MultiEditErrorData.ItemName);
        ItemNameHolder.setTextColor(ColorStateList.valueOf(Color.BLACK));
        FrameLayout.LayoutParams ItemNameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ItemNameLayout.gravity = Gravity.CENTER_HORIZONTAL;
        ItemNameHolder.setLayoutParams(ItemNameLayout);
        //endregion

        //region Item Error
        TextView ItemErrorHolder = new TextView(ctx);
        ItemErrorHolder.setText(MultiEditErrorData.ErrorMessage);
        ItemErrorHolder.setTextColor(ColorStateList.valueOf(Color.parseColor(MultiEditErrorData.ItemColor)));
        FrameLayout.LayoutParams ItemErrorLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ItemErrorLayout.topMargin = CustomFunctions.Sp_To_Px(ctx, 30);
        ItemErrorHolder.setLayoutParams(ItemErrorLayout);
        //endregion

        MainCardViewContainer.addView(ItemNameHolder);
        MainCardViewContainer.addView(ItemErrorHolder);

        return MainCardViewContainer;
    }

    public LiveData<String> GetMultiEditData(String ME_Data_Type, String Additional_Params) {
        switch(ME_Data_Type) {
            case Const.DataType.CATEGORY:
                DataModel.Send.Category categoryData = new DataModel().new Send().new Category();
                categoryData.Token = Const.App.TOKEN;
                categoryData.Action = Const.ActionTypes.LOAD;
                categoryData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
                categoryData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
                categoryData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
                return mInventoryRepository.MakeCategoryRequest(new Gson().toJson(categoryData));
            case Const.DataType.ITEM:
                DataModel.Send.Item itemData = new DataModel().new Send().new Item();
                itemData.Token = Const.App.TOKEN;
                itemData.Action = Const.ActionTypes.LOAD;
                itemData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
                itemData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
                itemData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
                itemData.Item_Category = Additional_Params;
                return mInventoryRepository.MakeItemRequest(new Gson().toJson(itemData));
        }
        return null;
    }

    public int GetIndexFromItemList(ArrayList<DataModel.DatabaseModel.Item> arrayList, String Key) {
        int tmp_val = 0;
        for(DataModel.DatabaseModel.Item item : arrayList) {
            if(item.Name.equals(Key)) {
                return tmp_val;
            }
            tmp_val++;
        }
        return -1;
    }
}