package com.tuna_salmon.home_inventory_app.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_table", indices = {@Index(value = {"category_name"}, unique = true)})
public class CategoryTable {

    @PrimaryKey
    @ColumnInfo(name = "category_name")
    @NonNull
    public String Name;
}
