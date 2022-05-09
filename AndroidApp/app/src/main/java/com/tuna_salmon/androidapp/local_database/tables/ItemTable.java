package com.tuna_salmon.androidapp.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "item_table",
        indices = {@Index(value = {"item_id", "item_name"}, unique = true)},
        primaryKeys = {"item_id", "item_category"},
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryTable.class,
                        parentColumns = "category_id",
                        childColumns = "item_category",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ItemTable {
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    public String ID;

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

    @ColumnInfo(name = "item_last_updated")
    @NonNull
    public Date LastUpdated;

    @ColumnInfo(name = "item_person_last_updated")
    @NonNull
    public String PersonLastUpdated;

    @PrimaryKey
    @ColumnInfo(name = "item_category")
    @NonNull
    public String Category_ID;
}
