package com.tuna_salmon.home_inventory_app.local_database.api;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.local_database.LocalDatabase;
import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;

import java.util.ArrayList;
import java.util.List;

public class LocalCategoryAPI {

    private LocalDatabase localDatabase;

    private LocalCategoryAPI() {}
    private static LocalCategoryAPI s_instance = null;
    public static LocalCategoryAPI getAPI() {
        if(s_instance == null)
            s_instance = new LocalCategoryAPI();
        return s_instance;
    }

    public String ParseData(String JsonData) {

        localDatabase = LocalDatabase.getDatabase();

        DataModel.Recv.Category response = new DataModel().new Recv(). new Category();

        response.AuthPermission = 2;

        // Decode Json
        DataModel.Send.Category request = new Gson().fromJson(JsonData, DataModel.Send.Category.class);

        switch (request.Action)
        {
            case Const.ActionTypes.ADD:
            {
                if(UniqueCategory(request)) {
                    localDatabase.categoryTransaction().create(request.Category_Name);
                    response.Exists = false;
                }
                else
                    response.Exists = true;

                response.Category_List = GetAllCategoryData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.EDIT:
            {
                if(UniqueCategory(request)) {
                    localDatabase.categoryTransaction().update(request.Ref_Category_Name, request.Category_Name);
                    localDatabase.itemTransaction().updateItemCategory(request.Ref_Category_Name, request.Category_Name);
                    response.Exists = false;
                }
                else
                    response.Exists = true;

                response.Category_List = GetAllCategoryData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.DELETE:
            {
                localDatabase.categoryTransaction().delete(request.Category_Name);
                localDatabase.itemTransaction().deleteByCategory(request.Category_Name);

                response.Category_List = GetAllCategoryData();

                response.Status = 0;
            }
            break;
            case Const.ActionTypes.LOAD:
            {
                response.Category_List = GetAllCategoryData();

                response.Status = 0;
            }
            break;
        }

        return new Gson().toJson(response);
    }

    private ArrayList<DataModel.DatabaseModel.Category> GetAllCategoryData() {

        ArrayList<DataModel.DatabaseModel.Category> ret_arr = new ArrayList<>();

        List<CategoryTable> categoryList = localDatabase.categoryTransaction().read();

        for(int pos = 0; pos < categoryList.size(); pos++) {

            DataModel.DatabaseModel.Category category = new DataModel().new DatabaseModel().new Category();

            category.Name = categoryList.get(pos).Name; // Name
            category.Color = "#FF03DAC5"; // Default Color

            List<ItemTable> categoryItemsList = localDatabase.itemTransaction().read(category.Name);

            category.Count = categoryItemsList.size(); // Count

            if(categoryItemsList.size() > 0) {
                for(int sub_pos = 0; sub_pos < categoryItemsList.size(); sub_pos++) {
                    if(categoryItemsList.get(sub_pos).Count <= 0) {
                        category.Color = "#ff1c64"; // Conditional Color
                    }
                }
            }

            ret_arr.add(category);
        }

        return ret_arr;
    }

    private boolean UniqueCategory(DataModel.Send.Category request) {
        if(localDatabase.categoryTransaction().check(request.Category_Name).size() != 0) {
            if(request.Category_Name.equals(request.Ref_Category_Name)) return true;
            else return false;
        }
        return true;
    }
}
