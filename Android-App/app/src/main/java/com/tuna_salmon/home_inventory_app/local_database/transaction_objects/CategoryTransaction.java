package com.tuna_salmon.home_inventory_app.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;

import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;

import java.util.List;

@Dao
public interface CategoryTransaction {

    @Query("SELECT * FROM category_table WHERE category_name = :Name")
    List<CategoryTable> check(String Name);

    @Query("INSERT INTO category_table VALUES (:Name)")
    void create(String Name);

    @Query("SELECT * FROM category_table")
    List<CategoryTable> read();

    @Query("UPDATE category_table SET category_name = :NewName WHERE category_name = :RefName")
    void update(String RefName, String NewName);

    @Query("DELETE FROM category_table WHERE category_name = :Name")
    void delete(String Name);

    @Query("DELETE FROM category_table")
    void clear_table();
}
