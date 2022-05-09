package com.tuna_salmon.androidapp.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

public class BulkEditModel {
    public String ID;
    public String Name;
    public String Desc;
    public Date LastUpdated;
    public String PersonLastUpdated;
}