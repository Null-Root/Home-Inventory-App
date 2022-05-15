package com.tuna_salmon.androidapp.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;


import com.tuna_salmon.androidapp.data.ItemModel;

import java.util.Date;
import java.util.List;

@Dao
public interface ItemTransaction {
    @Transaction
    @Query(
            "INSERT INTO item_table" +
            " VALUES (" +
                    ":ID," +
                    ":Name," +
                    ":Count," +
                    ":Unit," +
                    ":CriticalCount," +
                    ":Price," +
                    ":LastUpdated," +
                    ":PersonLastUpdated," +
                    ":Category_ID" +
            ")"
    )
    void create(
            String ID,
            String Name,
            double Count,
            String Unit,
            double CriticalCount,
            double Price,
            String LastUpdated,
            String PersonLastUpdated,
            String Category_ID
    );

    @Transaction
    @Query(
            "SELECT * FROM item_table " +
            "WHERE item_category = :Ref_Category_ID"
    )
    List<ItemModel> read(String Ref_Category_ID);

    @Transaction
    @Query(
            "SELECT * FROM item_table " +
            "WHERE item_category = :Ref_Category_ID AND item_id = :Ref_Item_ID")
    List<ItemModel> read_specific(String Ref_Category_ID, String Ref_Item_ID);

    @Transaction
    @Query("SELECT item_id FROM item_table " +
            "WHERE item_name = :Ref_Item_Name")
    String read_item_id(String Ref_Item_Name);

    @Transaction
    @Query("" +
            "UPDATE item_table " +
            "SET " +
                "item_name = :Name, " +
                "item_count = :Count, " +
                "item_unit = :Unit, " +
                "item_critical_count = :CriticalCount, " +
                "item_price = :Price, " +
                "item_last_updated = :LastUpdated, " +
                "item_person_last_updated = :PersonLastUpdated " +
            "WHERE item_id = :Ref_Item_ID AND item_category = :Ref_Category_ID"
    )
    void update(
            String Ref_Category_ID,
            String Ref_Item_ID,
            String Name,
            double Count,
            String Unit,
            double CriticalCount,
            double Price,
            String LastUpdated,
            String PersonLastUpdated
    );

    @Transaction
    @Query("" +
            "UPDATE item_table " +
            "SET " +
                "item_count = :Count, " +
                "item_last_updated = :LastUpdated, " +
                "item_person_last_updated = :PersonLastUpdated " +
            "WHERE item_id = :Ref_Item_ID AND item_category = :Ref_Category_ID"
    )
    void update_item_count(
            String Ref_Category_ID,
            String Ref_Item_ID,
            double Count,
            String LastUpdated,
            String PersonLastUpdated
    );

    @Transaction
    @Query(
            "DELETE FROM item_table " +
            "WHERE item_id = :RefID"
    )
    void delete(String RefID);
}
