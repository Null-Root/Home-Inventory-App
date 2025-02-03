package com.tuna_salmon.home_inventory_app.local_database.api;

import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.local_database.LocalDatabase;
import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditItemsTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditTable;

import java.util.ArrayList;

public class LocalAPI {

    private LocalDatabase localDatabase;

    private LocalAPI() {
        localDatabase = LocalDatabase.getDatabase();
    }
    private static LocalAPI s_instance = null;
    public static LocalAPI getAPI() {
        if(s_instance == null)
            s_instance = new LocalAPI();
        return s_instance;
    }

    public DataModel.DatabaseData getDatabaseDataFromLocal() {
        DataModel.DatabaseData databaseData = new DataModel().new DatabaseData();
        databaseData.categoryTable = new ArrayList<>(localDatabase.categoryTransaction().read());
        databaseData.itemTable = new ArrayList<>(localDatabase.itemTransaction().readAll());
        databaseData.multiEditTable = new ArrayList<>(localDatabase.multiEditTransaction().read());
        databaseData.multiEditItemsTable = new ArrayList<>(localDatabase.multiEditItemsTransaction().readAll());
        return databaseData;
    }

    public void setDatabaseDataFromWeb(DataModel.DatabaseData databaseData) {
        // Clear Local Database
        localDatabase.categoryTransaction().clear_table();
        localDatabase.itemTransaction().clear_table();
        localDatabase.multiEditTransaction().clear_table();
        localDatabase.multiEditItemsTransaction().clear_table();

        // Add Database Data
        for (CategoryTable categoryTable : databaseData.categoryTable) {
            localDatabase.categoryTransaction().create(categoryTable.Name);
        }
        for (ItemTable itemTable : databaseData.itemTable) {
            localDatabase.itemTransaction().create(
                    itemTable.Category,
                    itemTable.Name,
                    itemTable.Count,
                    itemTable.Unit,
                    itemTable.CriticalCount,
                    itemTable.Price,
                    itemTable.LastEdited,
                    itemTable.PersonLastEdit);
        }
        for (MultiEditTable multiEditTable : databaseData.multiEditTable) {
            localDatabase.multiEditTransaction().create(multiEditTable.Name, multiEditTable.Desc);
        }
        for(MultiEditItemsTable multiEditItemsTable : databaseData.multiEditItemsTable) {
            localDatabase.multiEditItemsTransaction().create(
                    multiEditItemsTable.Name,
                    multiEditItemsTable.ItemName,
                    multiEditItemsTable.ItemCount,
                    multiEditItemsTable.ItemPrice,
                    multiEditItemsTable.ItemUnit
            );
        }
    }
}
