package com.tuna_salmon.home_inventory_app.local_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditItemsTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditTable;
import com.tuna_salmon.home_inventory_app.local_database.transaction_objects.CategoryTransaction;
import com.tuna_salmon.home_inventory_app.local_database.transaction_objects.ItemTransaction;
import com.tuna_salmon.home_inventory_app.local_database.transaction_objects.MultiEditItemsTransaction;
import com.tuna_salmon.home_inventory_app.local_database.transaction_objects.MultiEditTransaction;

@Database(entities = {CategoryTable.class, ItemTable.class, MultiEditTable.class, MultiEditItemsTable.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase s_db;

    private static final String DATABASE_NAME = "local_db";

    public static void Initialize(Context ctx) {
        s_db = Room.databaseBuilder(
                ctx.getApplicationContext(),
                LocalDatabase.class,
                DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public synchronized static LocalDatabase getDatabase() {
        return s_db;
    }

    public abstract CategoryTransaction categoryTransaction();
    public abstract ItemTransaction itemTransaction();
    public abstract MultiEditTransaction multiEditTransaction();
    public abstract MultiEditItemsTransaction multiEditItemsTransaction();
}
