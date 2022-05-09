package com.tuna_salmon.androidapp.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "bulkEdit_table",
        primaryKeys = {"bulk_edit_id"},
        indices = {@Index(value = {"bulk_edit_id"}, unique = true)}
)
public class BulkEditTable {
    @PrimaryKey
    @ColumnInfo(name = "bulk_edit_id")
    public String ID;

    @ColumnInfo(name = "bulk_edit_name")
    @NonNull
    public String Name;

    @ColumnInfo(name = "bulk_edit_desc")
    @NonNull
    public String Desc;

    @ColumnInfo(name = "bulk_edit_last_updated")
    @NonNull
    public Date LastUpdated;

    @ColumnInfo(name = "bulk_edit_person_last_updated")
    @NonNull
    public String PersonLastUpdated;
}
