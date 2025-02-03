package com.tuna_salmon.home_inventory_app.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "multiEditItems_table", primaryKeys = {"multi_edit_name", "multi_edit_item_name"})
public class MultiEditItemsTable {

    @ColumnInfo(name = "multi_edit_name")
    @NonNull
    public String Name;

    @ColumnInfo(name = "multi_edit_item_name")
    @NonNull
    public String ItemName;

    @ColumnInfo(name = "multi_edit_item_count")
    @NonNull
    public double ItemCount;

    @ColumnInfo(name = "multi_edit_item_price")
    @NonNull
    public double ItemPrice;

    @ColumnInfo(name = "multi_edit_item_unit")
    @NonNull
    public String ItemUnit;
}
