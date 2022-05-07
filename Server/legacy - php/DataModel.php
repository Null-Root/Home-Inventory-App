<?php
	abstract class GlobalResponse
	{
		public $Message; // String
		public $Status; // Int
	    /*
	    0 -> SUCCESS
	    1 -> SERVER/CODE ERROR
	    */
	}

	class AccountResponse extends GlobalResponse
	{
		public $Name;
		public $UniqueID; // String
		public $LockState; // Bool
		public $Visibility; // Bool
	    public $AuthResponse; // Int
	    /*
	    0 -> AUTH PROMPT REVOKED
	    1 -> GO TO CONFIRM (SIGN UP)
	    2 -> GO BACK TO DASHBOARD (LOG IN/CONFIRM)
	    */
	}

	class ShareDataResponse extends GlobalResponse
	{
		public $ID_Exists;
		public $InventoryProfiles;
	}

	class InventoryProfileList
	{
		public $Name;
		public $ID;
		public $Permission;
	}


	// CategoryResponse
	class CategoryResponse extends GlobalResponse
	{
		public $AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
        public $Exists; // Boolean
        public $Category_List; // List of Categories
	}

	class CategoryDatabaseResponse
	{
		public $Name; // String
        public $Color; // String
        public $Count; // Int
	}


	// ItemResponse
	class ItemResponse extends GlobalResponse
	{
		public $AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
        public $Exists; // Boolean
        public $Item_List; // List of Items
	}

	class ItemDatabaseResponse
	{
        public $Name;
        public $Color;
        public $Count;
        public $Unit;
        public $CriticalCount;
        public $Price;
        public $LastEdited;
        public $PersonLastEdit;
	}


	// MultiEditResponse
	class MultiEditResponse extends GlobalResponse
	{
		public $AuthPermission; // 0 -> No Permissions, 1 -> Read Only, 2 -> Read, Write Allowed
        public $Exists;
        public $MultiEditList; // For Items under MultiEditContainer
        public $MultiEditErrorList; // For Errors on Using Multi-Edit
	}

	class MultiEditContainer
	{
		public $Name;
		public $Color;
		public $Desc;
		public $MultiEditItemsList; // For Items under MultiEditItemContainer
	}

	class MultiEditItemContainer
	{
		public $ME_ItemName;
        public $ME_ItemCount;
        public $ME_ItemPrice;
        public $ME_ItemUnit;
	}

	/**
	 * 
	 */
	class MultiEditErrorList
	{
		public $ItemName;
		public $ItemColor;
		public $ErrorMessage;
	}

	// For Overwriting Offline and Online Databases

	class AppResponse extends GlobalResponse
	{
		public $New_DB_Data;
		public $ChangeDatabaseData;
	}

	class DatabaseData
	{
        public $categoryTable;
        public $itemTable;
        public $multiEditTable;
        public $multiEditItemsTable;
	}

	class CategoryTable
	{
		public $Name;
	}

	class ItemTable
	{
    	public $Category;
    	public $Name;
    	public $Count;
    	public $Unit;
    	public $CriticalCount;
    	public $Price;
    	public $LastEdited;
    	public $PersonLastEdit;
	}

	class MultiEditTable
	{
    	public $Name;
    	public $Desc;
	}

	class MultiEditItemsTable
	{
    	public $Name;
    	public $ItemName;
    	public $ItemCount;
    	public $ItemPrice;
    	public $ItemUnit;
	}
?>