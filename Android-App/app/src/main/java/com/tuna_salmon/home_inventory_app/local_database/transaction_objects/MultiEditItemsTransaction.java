package com.tuna_salmon.home_inventory_app.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;

import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditItemsTable;

import java.util.List;

@Dao
public interface MultiEditItemsTransaction {

    @Query("SELECT * FROM multiEditItems_table WHERE multi_edit_name = :Name AND multi_edit_item_name = :ItemName")
    List<MultiEditItemsTable> check(String Name, String ItemName);

    @Query("INSERT INTO multiEditItems_table VALUES (:Name, :ItemName, :ItemCount, :ItemPrice, :ItemUnit)")
    void create(String Name, String ItemName, double ItemCount, double ItemPrice, String ItemUnit);

    @Query("SELECT * FROM multiEditItems_table")
    List<MultiEditItemsTable> readAll();

    @Query("SELECT * FROM multiEditItems_table WHERE multi_edit_name = :Name")
    List<MultiEditItemsTable> read(String Name);

    @Query("UPDATE multiEditItems_table SET multi_edit_name = :NewName, multi_edit_item_name = :ItemName, multi_edit_item_count = :ItemCount, multi_edit_item_price = :ItemPrice, multi_edit_item_unit = :ItemUnit WHERE multi_edit_name = :RefName")
    void update(String RefName, String NewName, String ItemName, double ItemCount, double ItemPrice, String ItemUnit);

    @Query("UPDATE multiEditItems_table SET multi_edit_name = :NewName WHERE multi_edit_name = :RefName")
    void updateItemNameOnly(String RefName, String NewName);

    @Query("DELETE FROM multiEditItems_table WHERE multi_edit_name = :Name")
    void delete(String Name);

    @Query("DELETE FROM multiEditItems_table WHERE multi_edit_item_name = :ItemName")
    void deleteByItem(String ItemName);

    @Query("DELETE FROM multiEditItems_table")
    void clear_table();
}
