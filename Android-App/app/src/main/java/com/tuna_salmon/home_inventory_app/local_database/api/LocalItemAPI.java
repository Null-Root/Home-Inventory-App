package com.tuna_salmon.home_inventory_app.local_database.api;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.local_database.LocalDatabase;
import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;

import java.util.ArrayList;
import java.util.List;

public class LocalItemAPI {

    private LocalDatabase localDatabase;

    private LocalItemAPI() {}
    private static LocalItemAPI s_instance = null;
    public static LocalItemAPI getAPI() {
        if(s_instance == null)
            s_instance = new LocalItemAPI();
        return s_instance;
    }

    public String ParseData(String JsonData) {

        localDatabase = LocalDatabase.getDatabase();

        DataModel.Recv.Item response = new DataModel().new Recv(). new Item();

        response.AuthPermission = 2;

        // Decode Json
        DataModel.Send.Item request = new Gson().fromJson(JsonData, DataModel.Send.Item.class);

        switch (request.Action)
        {
            case Const.ActionTypes.ADD:
            {
                CustomFunctions.Logln(request.Item_Name);
                if(UniqueItem(request)) {
                    localDatabase.itemTransaction().create(
                            request.Item_Category,
                            request.Item_Name,
                            request.Item_Count,
                            request.Item_Unit,
                            request.Item_CriticalCount,
                            request.Item_Price,
                            request.Item_LastEdited,
                            request.Item_PersonLastEdit);
                    response.Exists = false;
                }
                else
                    response.Exists = true;

                response.Item_List = GetAllItemData(request.Item_Category);

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.EDIT:
            {
                if(UniqueItem(request)) {
                    localDatabase.itemTransaction().update(
                            request.Ref_Item_Name,
                            request.Item_Count,
                            request.Item_Unit,
                            request.Item_CriticalCount,
                            request.Item_Price,
                            request.Item_LastEdited,
                            request.Item_PersonLastEdit,
                            request.Item_Name
                    );

                    localDatabase.multiEditItemsTransaction().updateItemNameOnly(request.Ref_Item_Name, request.Item_Name);

                    response.Exists = false;
                }
                else
                    response.Exists = true;

                response.Item_List = GetAllItemData(request.Item_Category);

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.DELETE:
            {
                localDatabase.itemTransaction().delete(request.Item_Name);
                localDatabase.multiEditItemsTransaction().deleteByItem(request.Item_Name);

                response.Item_List = GetAllItemData(request.Item_Category);

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.LOAD:
            {
                response.Item_List = GetAllItemData(request.Item_Category);

                response.Status = 0;
            }
            break;
        }

        return new Gson().toJson(response);
    }

    private ArrayList<DataModel.DatabaseModel.Item> GetAllItemData(String Category) {

        ArrayList<DataModel.DatabaseModel.Item> ret_arr = new ArrayList<>();

        List<ItemTable> itemList = localDatabase.itemTransaction().read(Category);

        for(int pos = 0; pos < itemList.size(); pos++) {

            DataModel.DatabaseModel.Item item = new DataModel().new DatabaseModel().new Item();

            item.Name = itemList.get(pos).Name;
            item.Count = itemList.get(pos).Count;
            item.Unit = itemList.get(pos).Unit;
            item.CriticalCount = itemList.get(pos).CriticalCount;
            item.Price = itemList.get(pos).Price;
            item.LastEdited = itemList.get(pos).LastEdited;
            item.PersonLastEdit = itemList.get(pos).PersonLastEdit;

            item.Color = "#D5F4F5";

            if(item.CriticalCount >= item.Count) item.Color = "#f5cb42";
            if(item.Count <= 0) item.Color = "#ff1c64";

            ret_arr.add(item);
        }

        return ret_arr;
    }

    private boolean UniqueItem(DataModel.Send.Item request) {
        if(localDatabase.itemTransaction().check(request.Item_Category, request.Item_Name).size() != 0) {
            if(request.Item_Name.equals(request.Ref_Item_Name)) return true;
            else return false;
        }
        return true;
    }
}
