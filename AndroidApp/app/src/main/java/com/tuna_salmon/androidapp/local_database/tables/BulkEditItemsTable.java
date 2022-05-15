package com.tuna_salmon.androidapp.local_database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "bulkEditItems_table",
        primaryKeys = {"bulk_edit_id", "bulk_edit_item_id"},
        indices = {@Index(value = {"bulk_edit_id", "bulk_edit_item_id"}, unique = true)},
        foreignKeys = {
                @ForeignKey(
                        entity = BulkEditTable.class,
                        parentColumns = "bulk_edit_id",
                        childColumns = "bulk_edit_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = ItemTable.class,
                        parentColumns = "item_id",
                        childColumns = "bulk_edit_item_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class BulkEditItemsTable {
    @ColumnInfo(name = "bulk_edit_id")
    @NonNull
    public String BulkEdit_ID;

    @ColumnInfo(name = "bulk_edit_item_id")
    @NonNull
    public String Item_ID;

    @ColumnInfo(name = "bulk_edit_item_mod_count")
    @NonNull
    public double ItemModCount;
}
