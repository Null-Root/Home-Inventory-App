package com.tuna_salmon.androidapp.local_database.transaction_objects;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.tuna_salmon.androidapp.data.BulkEditModel;

import java.util.Date;
import java.util.List;

@Dao
public interface BulkEditTransaction {
    @Transaction
    @Query("" +
            "INSERT INTO bulkEdit_table " +
            "VALUES (:Name, :Desc, :LastUpdated, :PersonLastUpdated)")
    void create(String ID, String Name, String Desc, Date LastUpdated, String PersonLastUpdated);

    @Transaction
    @Query("SELECT * FROM bulkEdit_table")
    List<BulkEditModel> read();

    @Transaction
    @Query("" +
            "SELECT * FROM bulkEdit_table " +
            "WHERE bulk_edit_id = :RefID"
    )
    List<BulkEditModel> read_specific(String RefID);

    @Transaction
    @Query("" +
            "UPDATE bulkEdit_table " +
            "SET " +
                "bulk_edit_name = :Name, " +
                "bulk_edit_desc = :Desc, " +
                "bulk_edit_last_updated = :LastUpdated, " +
                "bulk_edit_person_last_updated = :PersonLastUpdated " +
            "WHERE bulk_edit_id = :RefID"
    )
    void update(String RefID, String Name, String Desc, Date LastUpdated, String PersonLastUpdated);

    @Transaction
    @Query("" +
            "DELETE FROM bulkEdit_table " +
            "WHERE bulk_edit_id = :RefID"
    )
    void delete(String RefID);

    @Transaction
    @Query("DELETE FROM bulkEdit_table")
    void clear_table();
}
