package com.tuna_salmon.androidapp.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.tuna_salmon.androidapp.data.CategoryModel;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryTransaction {
    @Transaction
    @Query(
            "INSERT INTO category_table " +
            "VALUES (:ID, :Name, :LastUpdated, :PersonLastUpdated)"
    )
    void create(String ID, String Name, String LastUpdated, String PersonLastUpdated);

    @Transaction
    @Query("SELECT * FROM category_table")
    List<CategoryModel> read();

    @Transaction
    @Query(
            "SELECT * FROM category_table " +
            "WHERE category_name = :RefID"
    )
    List<CategoryModel> read_specific(String RefID);

    @Transaction
    @Query(
            "UPDATE category_table " +
            "SET " +
                    "category_name = :Name, " +
                    "category_last_updated = :LastUpdated, " +
                    "category_person_last_updated = :PersonLastUpdated " +
            "WHERE category_id = :RefID"
    )
    void update(
            String RefID,
            String Name,
            String LastUpdated,
            String PersonLastUpdated
    );

    @Transaction
    @Query(
            "DELETE FROM category_table " +
            "WHERE category_id = :RefID"
    )
    void delete(String RefID);

    @Transaction
    @Query("DELETE FROM category_table")
    void clear_table();
}