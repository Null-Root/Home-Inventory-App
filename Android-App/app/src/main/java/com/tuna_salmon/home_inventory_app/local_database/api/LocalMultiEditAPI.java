package com.tuna_salmon.home_inventory_app.local_database.api;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.local_database.LocalDatabase;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditItemsTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditTable;

import java.util.ArrayList;
import java.util.List;

public class LocalMultiEditAPI {

    private LocalDatabase localDatabase;

    private LocalMultiEditAPI() {}
    private static LocalMultiEditAPI s_instance = null;
    public static LocalMultiEditAPI getAPI() {
        if(s_instance == null)
            s_instance = new LocalMultiEditAPI();
        return s_instance;
    }

    public String ParseData(String JsonData) {

        localDatabase = LocalDatabase.getDatabase();

        DataModel.Recv.MultiEdit response = new DataModel().new Recv(). new MultiEdit();

        response.AuthPermission = 2;

        // Decode Json
        DataModel.Send.MultiEdit request = new Gson().fromJson(JsonData, DataModel.Send.MultiEdit.class);

        switch (request.Action)
        {
            case Const.ActionTypes.ADD:
            {
                if(UniqueMultiEdit(request)) {
                    localDatabase.multiEditTransaction().create(request.MultiEdit_Name, request.MultiEdit_Desc);

                    for (DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer item : request.MultiEdit_ItemContainers){
                        localDatabase.multiEditItemsTransaction().create(
                                request.MultiEdit_Name,
                                item.ME_ItemName,
                                item.ME_ItemCount,
                                item.ME_ItemPrice,
                                item.ME_ItemUnit
                        );

                        response.Exists = false;
                    }
                }
                else
                    response.Exists = true;

                response.MultiEditList = GetAllMultiEditData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.EDIT:
            {
                if(UniqueMultiEdit(request)) {
                    localDatabase.multiEditTransaction().update(request.Ref_MultiEdit_Name, request.MultiEdit_Name, request.MultiEdit_Desc);

                    localDatabase.multiEditItemsTransaction().delete(request.MultiEdit_Name);

                    for (DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer item : request.MultiEdit_ItemContainers) {
                        localDatabase.multiEditItemsTransaction().create(
                                request.MultiEdit_Name,
                                item.ME_ItemName,
                                item.ME_ItemCount,
                                item.ME_ItemPrice,
                                item.ME_ItemUnit
                        );
                    }

                    response.Exists = false;
                }
                else
                    response.Exists = true;

                response.MultiEditList = GetAllMultiEditData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.DELETE:
            {
                localDatabase.multiEditTransaction().delete(request.MultiEdit_Name);
                localDatabase.multiEditItemsTransaction().delete(request.MultiEdit_Name);

                response.MultiEditList = GetAllMultiEditData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.LOAD:
            {
                response.MultiEditList = GetAllMultiEditData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.USE:
            {
                boolean AllowItemCountChange = true;

                response.MultiEditErrorList = new ArrayList<>();

                if(request.CheckInputs) {
                    for(DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer item : request.MultiEdit_ItemContainers) {
                        List<ItemTable> itemInfoStatus = localDatabase.itemTransaction().readByItem(item.ME_ItemName);

                        double ItemCount = itemInfoStatus.get(0).Count;
                        double ItemMod = item.ME_ItemCount;
                        double ItemCriticalCount = itemInfoStatus.get(0).CriticalCount;

                        if(ItemMod < 0) {

                            if(ItemCount + ItemMod < ItemCriticalCount) {
                                AllowItemCountChange = false;

                                DataModel.DatabaseModel.MultiEdit.MultiEditErrorList multi_edit_error = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditErrorList();
                                multi_edit_error.ItemName = item.ME_ItemName;
                                multi_edit_error.ItemColor = "#aaab9d";
                                multi_edit_error.ErrorMessage = String.format(
                                        "Item count reached critical count. [%s - %s = %s] \n Critical Count: %s}",
                                        ItemCount,
                                        ItemMod,
                                        ItemCount + ItemMod,
                                        ItemCriticalCount
                                );

                                response.MultiEditErrorList.add(multi_edit_error);
                            }

                            if(ItemCount + ItemMod < 0.000) {
                                AllowItemCountChange = false;

                                DataModel.DatabaseModel.MultiEdit.MultiEditErrorList multi_edit_error = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditErrorList();
                                multi_edit_error.ItemName = item.ME_ItemName;
                                multi_edit_error.ItemColor = "#f54242";
                                multi_edit_error.ErrorMessage = String.format(
                                        "Item Count will go below zero [%s - %s = %s]",
                                        ItemCount,
                                        ItemMod,
                                        ItemCount + ItemMod
                                );

                                response.MultiEditErrorList.add(multi_edit_error);
                            }
                        }
                        else {

                            if(ItemCount + ItemMod > 99999.000) {
                                AllowItemCountChange = false;

                                DataModel.DatabaseModel.MultiEdit.MultiEditErrorList multi_edit_error = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditErrorList();
                                multi_edit_error.ItemName = item.ME_ItemName;
                                multi_edit_error.ItemColor = "#f54242";
                                multi_edit_error.ErrorMessage = String.format(
                                        "Item Count will go past the limit [%s + %s = %s]",
                                        ItemCount,
                                        ItemMod,
                                        ItemCount + ItemMod
                                );

                                response.MultiEditErrorList.add(multi_edit_error);
                            }
                        }
                    }
                }

                if(AllowItemCountChange)
                {
                    for(DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer item : request.MultiEdit_ItemContainers) {
                        List<ItemTable> itemInfoStatus = localDatabase.itemTransaction().readByItem(item.ME_ItemName);

                        double NewItemCount = itemInfoStatus.get(0).Count + item.ME_ItemCount;

                        if (NewItemCount < 0.000)
                            NewItemCount = 0;
                        else if (NewItemCount > 99999.000)
                            NewItemCount = 99999;

                        localDatabase.itemTransaction().updateItemCount(item.ME_ItemName, NewItemCount);
                    }

                    response.MultiEditList = GetAllMultiEditData();

                    response.Status = 0;
                }
            }
            break;
        }

        return new Gson().toJson(response);
    }

    private ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> GetAllMultiEditData() {
        ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> ret_arr = new ArrayList<>();

        List<MultiEditTable> multiEditList = localDatabase.multiEditTransaction().read();

        for(MultiEditTable multiEdit : multiEditList) {
            DataModel.DatabaseModel.MultiEdit.MultiEditContainer multiEditContainer = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditContainer();
            multiEditContainer.Name = multiEdit.Name;
            multiEditContainer.Color = "#8697c2";
            multiEditContainer.Desc = multiEdit.Desc;

            multiEditContainer.MultiEditItemsList = new ArrayList<>();

            List<MultiEditItemsTable> multiEditItemList = localDatabase.multiEditItemsTransaction().read(multiEdit.Name);

            for(MultiEditItemsTable multiEditItem : multiEditItemList) {
                DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer multiEditItemContainer = new DataModel().new DatabaseModel().new MultiEdit().new MultiEditItemContainer();
                multiEditItemContainer.ME_ItemName = multiEditItem.ItemName;
                multiEditItemContainer.ME_ItemCount = multiEditItem.ItemCount;
                multiEditItemContainer.ME_ItemPrice = multiEditItem.ItemPrice;
                multiEditItemContainer.ME_ItemUnit = multiEditItem.ItemUnit;

                multiEditContainer.MultiEditItemsList.add(multiEditItemContainer);
            }

            ret_arr.add(multiEditContainer);
        }

        return ret_arr;
    }

    private boolean UniqueMultiEdit(DataModel.Send.MultiEdit request) {
        if(localDatabase.multiEditTransaction().check(request.MultiEdit_Name).size() != 0) {
            if(request.MultiEdit_Name.equals(request.Ref_MultiEdit_Name)) return true;
            else return false;
        }
        return true;
    }
}
