package com.tuna_salmon.home_inventory_app.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;

import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditTable;

import java.util.List;

@Dao
public interface MultiEditTransaction {

    @Query("SELECT * FROM multiEdit_table WHERE multi_edit_name = :Name")
    List<MultiEditTable> check(String Name);

    @Query("INSERT INTO multiEdit_table VALUES (:Name, :Desc)")
    void create(String Name, String Desc);

    @Query("SELECT * FROM multiEdit_table")
    List<MultiEditTable> read();

    @Query("UPDATE multiEdit_table SET multi_edit_name = :NewName, multi_edit_desc = :Desc WHERE multi_edit_name = :RefName")
    void update(String RefName, String NewName, String Desc);

    @Query("DELETE FROM multiEdit_table WHERE multi_edit_name = :Name")
    void delete(String Name);

    @Query("DELETE FROM multiEdit_table")
    void clear_table();
}
