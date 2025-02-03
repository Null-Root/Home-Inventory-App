package com.tuna_salmon.home_inventory_app.data;

import com.tuna_salmon.home_inventory_app.local_database.tables.CategoryTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.ItemTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditItemsTable;
import com.tuna_salmon.home_inventory_app.local_database.tables.MultiEditTable;

import java.util.ArrayList;

public class DataModel {
    public class Send {
        public abstract class GLOBAL {
            public String Token;
            public String UniqueID; // PRIVATE -> USER ID; PUBLIC -> OTHERS ID, CUSTOM ID
            public String Action;

            public String Email;
            public String Password;
        }

        public class App extends GLOBAL {
            public DatabaseData New_DB_Data;
        }

        public class Account extends GLOBAL {
            public String Name;
            public String Code;
            public boolean LockState;
            public int Visibility;
        }

        public class Category extends GLOBAL {
            public String Ref_Category_Name;
            public String Category_Name;
        }

        public class Item extends GLOBAL {
            public String Ref_Item_Name;
            public String Item_Category;
            public String Item_Name;
            public double Item_Count;
            public String Item_Unit;
            public double Item_CriticalCount;
            public double Item_Price;
            public String Item_LastEdited;
            public String Item_PersonLastEdit;
        }

        public class MultiEdit extends GLOBAL {
            public String Ref_MultiEdit_Name;
            public boolean CheckInputs;
            public String MultiEdit_Name;
            public String MultiEdit_Desc;
            public ArrayList<DatabaseModel.MultiEdit.MultiEditItemContainer> MultiEdit_ItemContainers;
        }

        public class ShareData extends GLOBAL {
            public String Name;
            public String ID;
            public int Permission;
        }
    }

    public class Recv {
        public abstract class GLOBAL {
            public String Message;
            public int Status;
            /*
            0   -> WORKING
            1   -> CODE/SERVER ERROR
            */
        }

        public class App extends GLOBAL {
            public DatabaseData New_DB_Data;
            public boolean ChangeDatabaseData;
        }

        public class Account extends GLOBAL {
            public String Name;
            public String UniqueID;
            public boolean LockState;
            public int Visibility;
            public int AuthResponse;
            /*
            0 -> AUTH PROMPT REVOKED
            1 -> GO TO CONFIRM (SIGN UP)
            2 -> GO BACK TO DASHBOARD (LOG IN/CONFIRM)
            */
            public int ID_Status;
            /*
            0 -> ACCEPTED
            1 -> REJECTED
            */
        }

        public class Category extends GLOBAL {
            public int AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
            public boolean Exists;
            public ArrayList<DatabaseModel.Category> Category_List;
        }

        public class Item extends GLOBAL {
            public int AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
            public boolean Exists;
            public ArrayList<DatabaseModel.Item> Item_List;
        }

        public class MultiEdit extends GLOBAL {
            public int AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
            public boolean Exists;
            public ArrayList<DatabaseModel.MultiEdit.MultiEditContainer> MultiEditList;
            public ArrayList<DatabaseModel.MultiEdit.MultiEditErrorList> MultiEditErrorList; // For Using Multi-Edit
        }

        public class ShareData extends GLOBAL {
            public boolean ID_Exists;
            public ArrayList<DatabaseModel.ShareData.InventoryProfileData> InventoryProfiles;
        }
    }

    public class DatabaseModel {
        public class Category {
            public String Name;
            public String Color;
            public int Count;
        }
        public class Item {
            public String Name;
            public String Color;
            public double Count;
            public String Unit;
            public double CriticalCount;
            public double Price;
            public String LastEdited;
            public String PersonLastEdit;
        }
        public class MultiEdit {
            public class MultiEditContainer {
                public String Name;
                public String Color;
                public String Desc;
                public ArrayList<MultiEditItemContainer> MultiEditItemsList;
            }
            public class MultiEditItemContainer {
                public String ME_ItemName;
                public double ME_ItemCount;
                public double ME_ItemPrice;
                public String ME_ItemUnit;
            }
            public class MultiEditErrorList {
                public String ItemName;
                public String ItemColor;
                public String ErrorMessage;
            }
        }
        public class ShareData {
            public class InventoryProfileData {
                public String Name;
                public String UniqueID;
                public int Permission;
            }
        }
    }

    public class DatabaseData {
        public ArrayList<CategoryTable> categoryTable;
        public ArrayList<ItemTable> itemTable;
        public ArrayList<MultiEditTable> multiEditTable;
        public ArrayList<MultiEditItemsTable> multiEditItemsTable;
    }
}
