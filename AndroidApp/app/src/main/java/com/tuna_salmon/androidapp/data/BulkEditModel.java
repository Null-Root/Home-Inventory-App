package com.tuna_salmon.androidapp.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

public class BulkEditModel {
    public String bulk_edit_id;
    public String bulk_edit_name;
    public String bulk_edit_desc;
    public String bulk_edit_last_updated;
    public String bulk_edit_person_last_updated;
}