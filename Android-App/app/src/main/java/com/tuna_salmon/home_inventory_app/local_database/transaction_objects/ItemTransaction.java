package com.tuna_salmon.home_inventory_app.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;

import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;

import java.util.List;

@Dao
public interface ItemTransaction {

    @Query("SELECT * FROM item_table WHERE item_category = :Category AND item_name = :Name")
    List<ItemTable> check(String Category, String Name);

    @Query("INSERT INTO item_table VALUES (:Category, :Name, :Count, :Unit, :CriticalCount, :Price, :LastEdited, :PersonLastEdited)")
    void create(String Category,
                String Name,
                double Count,
                String Unit,
                double CriticalCount,
                double Price,
                String LastEdited,
                String PersonLastEdited);

    @Query("SELECT * FROM item_table")
    List<ItemTable> readAll();

    @Query("SELECT * FROM item_table WHERE item_category = :Category")
    List<ItemTable> read(String Category);

    @Query("SELECT * FROM item_table WHERE item_name = :ItemName")
    List<ItemTable> readByItem(String ItemName);

    @Query("UPDATE item_table SET item_name = :NewName, item_count = :Count, item_unit = :Unit, item_critical_count = :CriticalCount, item_price = :Price, item_last_edited = :LastEdited, item_person_last_edited = :PersonLastEdited WHERE item_name = :RefName")
    void update(String RefName,
                double Count,
                String Unit,
                double CriticalCount,
                double Price,
                String LastEdited,
                String PersonLastEdited,
                String NewName);

    @Query("UPDATE item_table SET item_category = :NewCategoryName WHERE item_category = :RefCategoryName")
    void updateItemCategory(String RefCategoryName, String NewCategoryName);

    @Query("UPDATE item_table SET item_count = :NewItemCount WHERE item_name = :ItemName")
    void updateItemCount(String ItemName, double NewItemCount);

    @Query("DELETE FROM item_table WHERE item_name = :Name")
    void delete(String Name);

    @Query("DELETE FROM item_table WHERE item_category = :Category")
    void deleteByCategory(String Category);

    @Query("DELETE FROM item_table")
    void clear_table();
}
