package com.tuna_salmon.home_inventory_app.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "multiEdit_table", indices = {@Index(value = {"multi_edit_name"}, unique = true)})
public class MultiEditTable {

    @PrimaryKey
    @ColumnInfo(name = "multi_edit_name")
    @NonNull
    public String Name;

    @ColumnInfo(name = "multi_edit_desc")
    @NonNull
    public String Desc;
}
