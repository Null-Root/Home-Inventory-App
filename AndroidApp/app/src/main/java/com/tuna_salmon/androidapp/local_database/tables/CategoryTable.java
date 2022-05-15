package com.tuna_salmon.androidapp.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "category_table",
        indices = {@Index(value = {"category_id", "category_name"}, unique = true)}
)
public class CategoryTable {
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    @NonNull
    public String ID;

    @ColumnInfo(name = "category_name")
    @NonNull
    public String Name;

    @ColumnInfo(name = "category_last_updated")
    @NonNull
    public String LastUpdated;

    @ColumnInfo(name = "category_person_last_updated")
    @NonNull
    public String PersonLastUpdated;
}
