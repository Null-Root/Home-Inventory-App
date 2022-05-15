package com.tuna_salmon.androidapp.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.tuna_salmon.androidapp.data.BulkEditItemsModel;
import com.tuna_salmon.androidapp.local_database.tables.BulkEditItemsTable;

import java.util.List;

@Dao
public interface BulkEditItemsTransaction {
    @Transaction
    @Query("" +
            "INSERT INTO bulkEditItems_table " +
            "VALUES(:ID, :Item_ID, :Item_Mod_Count)"
    )
    void create(String ID, String Item_ID, double Item_Mod_Count);

    @Transaction
    @Query("" +
            "SELECT " +
                "bulkEditItems_table.bulk_edit_id, " +
                "item_table.item_name, " +
                "bulkEditItems_table.bulk_edit_item_mod_count, " +
                "item_table.item_unit, " +
                "item_table.item_price " +
            "FROM item_table, bulkEditItems_table " +
            "WHERE " +
                "bulkEditItems_table.bulk_edit_id = :BulkEdit_ID "
    )
    List<BulkEditItemsModel> read(String BulkEdit_ID);

    @Transaction
    @Query("" +
            "UPDATE bulkEditItems_table " +
            "SET " +
                "bulk_edit_item_mod_count = :Item_Mod_Count " +
            "WHERE " +
                "bulk_edit_id = :Ref_BulkEdit_ID " +
            "AND " +
                "bulk_edit_item_id = :Ref_Item_ID"
    )
    void update(String Ref_BulkEdit_ID, String Ref_Item_ID, double Item_Mod_Count);

    @Transaction
    @Query("" +
            "DELETE FROM bulkEditItems_table " +
            "WHERE bulk_edit_item_id = :Ref_Item_ID")
    void delete(String Ref_Item_ID);
}
