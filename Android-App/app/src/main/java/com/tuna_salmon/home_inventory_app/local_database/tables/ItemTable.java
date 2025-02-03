package com.tuna_salmon.home_inventory_app.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_table", indices = {@Index(value = {"item_name"}, unique = true)})
public class ItemTable {

    @ColumnInfo(name = "item_category")
    @NonNull
    public String Category;

    @PrimaryKey
    @ColumnInfo(name = "item_name")
    @NonNull
    public String Name;

    @ColumnInfo(name = "item_count")
    @NonNull
    public double Count;

    @ColumnInfo(name = "item_unit")
    @NonNull
    public String Unit;

    @ColumnInfo(name = "item_critical_count")
    @NonNull
    public double CriticalCount;

    @ColumnInfo(name = "item_price")
    @NonNull
    public double Price;

    @ColumnInfo(name = "item_last_edited")
    @NonNull
    public String LastEdited;

    @ColumnInfo(name = "item_person_last_edited")
    @NonNull
    public String PersonLastEdit;
}
