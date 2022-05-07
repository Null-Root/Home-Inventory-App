<?php

	// SQL Functions

	function SecureSQL($TranslatedQuery, $DataTypes, $InputArray, $SQLConnection)
	{
		$SQL_Statement = mysqli_stmt_init($SQLConnection);
		mysqli_stmt_prepare($SQL_Statement, $TranslatedQuery);
		// Bind Parameters
		mysqli_stmt_bind_param($SQL_Statement, $DataTypes, ...$InputArray);
		// Execute Query
		mysqli_stmt_execute($SQL_Statement);
		return $SQL_Statement;
	}

	function BasicSQL($SQLQuery, $SQLConnection)
	{
		return mysqli_query($SQLConnection, $SQLQuery);
	}





	// Common Functions

	function IntToBool($Integer)
	{
		if($Integer <= 0)
		{
			return false;
		}
		else
		{
			return true;
		}
		return null;
	}

	function generateRandomString($length = 10) // Returns String
	{
	    $characters = '0123456789abcdefghijklmnopqrstuvwxyz_';
	    $charactersLength = strlen($characters);
	    $randomString = '';
	    for ($i = 0; $i < $length; $i++)
	    {
	        $randomString .= $characters[rand(0, $charactersLength - 1)];
	    }
	    return $randomString;
	}





	// Data Security

	function IsInventoryOwner($Email, $Password, $PublicID, $Connection)
	{
		$PreparedStatement = SecureSQL('SELECT * FROM UserData WHERE UserEmail=?', 's', array($Email), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				if(password_verify($Password, $Row['UserPass']))
				{
					if(strcmp($Row['UserPublicUniqueID'], $PublicID) == 0)
					{
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	function InventoryVisibility($PublicID, $Connection)
	{
		$PreparedStatement = SecureSQL('SELECT UserVisibility FROM UserData WHERE UserPublicUniqueID=?', 's', array($PublicID), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				return $Row['UserVisibility'];
			}
		}
		return -1;
	}

	function GetInventoryID($PublicID, $Connection)
	{
		$PreparedStatement = SecureSQL('SELECT UserPrivateUniqueID FROM UserData WHERE UserPublicUniqueID=?', 's', array($PublicID), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				return $Row['UserPrivateUniqueID'];
			}
		}
		return '';
	}

	function AccountPermissions($Email, $PublicID, $Connection)
	{
		$TableName = "ShareData_" . GetInventoryID($PublicID, $Connection);
		$Query = sprintf('SELECT SharedPermission FROM %s WHERE SharedPublicID=?', $TableName);

		$PreparedStatement = SecureSQL($Query, 's', array(Get_User_Data('UserPublicUniqueID', $Email, $Connection)), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				return $Row['SharedPermission'];
			}
		}
		return -1;
	}





	// Account Functions

	function isAccountValid($User_Email, $User_Pass, $Connection) // Returns Array
	{
		$res = array();

		$res['Result'] = false;
		$res['Message'] = "Account does not exist";

		$PreparedStatement = SecureSQL('SELECT UserPass FROM UserData WHERE UserEmail=?', 's', array($User_Email), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			$res['Message'] = "Wrong Password";
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				if(password_verify($User_Pass, $Row['UserPass']))
				{
					$res['Result'] = true;
					$res['Message'] = "Account Exist";
				}
			}
		}
		return $res;
	}

	function generateNewID($Connection, $TypeOfId, $StringLength = 10)
	{
		$QueryString = "SELECT " . $TypeOfId . " FROM UserData WHERE " . $TypeOfId . "=?";
		$GeneratedCode = generateRandomString($StringLength);
		$PreparedStatement = SecureSQL($QueryString, 's', array($GeneratedCode), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			// Call Function if code already exists
			return generateNewID($Connection, $TypeOfId);
		}
		return $GeneratedCode;
	}

	function Get_User_Data($DataToGet, $Email, $Connection)
	{
		$PreparedStatement = SecureSQL(sprintf("SELECT %s FROM UserData WHERE UserEmail=?", $DataToGet), "s", array($Email), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				return $Row[$DataToGet];
			}
		}
		return '';
	}





	// Share Data

	function isIDExists($UniqueID, $Connection)
	{
		$PreparedStatement = SecureSQL("SELECT * FROM UserData WHERE UserPublicUniqueID=?", "s", array($UniqueID), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			return true;
		}
		return false;
	}

	function Get_MyInventory_ProfileList($UniqueID, $Connection)
	{
		$ret_arr = array();
		$PreparedStatement = SecureSQL(sprintf("SELECT * FROM ShareData_%s", $UniqueID), '', array(), $Connection);
		$ResultSet = mysqli_stmt_get_result($PreparedStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				$inventory_profile = new InventoryProfileList();
				$inventory_profile->Name = $Row['SharedName'];
				$inventory_profile->ID = $Row['SharedPublicID'];
				$inventory_profile->Permission = $Row['SharedPermission'];

				array_push($ret_arr, $inventory_profile);
			}
		}
		mysqli_stmt_close($PreparedStatement);
		return $ret_arr;
	}

	function Get_OtherInventory_ProfileList($UniqueID, $Connection)
	{
		$ret_arr = array();

		// Get All Unique ID's from Shared Inventory Profile Table
		$MainStmt = SecureSQL(sprintf("SELECT * FROM SharedInventoryProfile_%s", $UniqueID), '', array(), $Connection);
		$MainRS = mysqli_stmt_get_result($MainStmt);
		if(mysqli_num_rows($MainRS) > 0)
		{
			while($Main_Row = mysqli_fetch_assoc($MainRS))
			{
				// Get Private ID's of Public ID's
				$SubPrivateID = GetInventoryID($Main_Row['SharedProfileID'], $Connection);

				$inventory_profile = new InventoryProfileList();

				$inventory_profile->ID = $Main_Row['SharedProfileID'];

				// Get Name
				$SubNameStmt = SecureSQL('SELECT * FROM UserData WHERE UserPrivateUniqueID=?', 's', array($SubPrivateID), $Connection);
				$SubNameRS = mysqli_stmt_get_result($SubNameStmt);
				if(mysqli_num_rows($SubNameRS) > 0)
				{
					$Name_Row = mysqli_fetch_assoc($SubNameRS);
					$inventory_profile->Name = $Name_Row['UserName'];
				}

				// Get Permissions
				$SubPermissionStmt = SecureSQL(sprintf('SELECT * FROM ShareData_%s', $SubPrivateID), '', array(), $Connection);
				$SubPermissionRS = mysqli_stmt_get_result($SubPermissionStmt);
				if(mysqli_num_rows($SubPermissionRS) > 0)
				{
					$Permission_Row = mysqli_fetch_assoc($SubPermissionRS);
					$inventory_profile->Permission = $Permission_Row['SharedPermission'];
				}

				array_push($ret_arr, $inventory_profile);
			}
		}
		mysqli_stmt_close($MainStmt);
		return $ret_arr;
	}





	// Category Functions

	function Get_Categories($Connection, $UniqueIDAccessor)
	{
		$CategoryTableName = 'Category_' . $UniqueIDAccessor;
		$ItemTableName = 'Item_' . $UniqueIDAccessor;

		$ret_list = array();

		$MainStatement = SecureSQL(sprintf('SELECT * FROM %s', $CategoryTableName), '', array(), $Connection);
		$ResultSet = mysqli_stmt_get_result($MainStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				$category = new CategoryDatabaseResponse();

				$category->Name = $Row['Category_Name'];
				$category->Count = 0;
				$category->Color = "#FF03DAC5";

				$SubStatement = SecureSQL(sprintf('SELECT * FROM %s WHERE Item_Category=?', $ItemTableName), 's', array($Row['Category_Name']), $Connection);
				$SubResultSet = mysqli_stmt_get_result($SubStatement);
				if(mysqli_num_rows($SubResultSet) > 0)
				{
					$category->Count = mysqli_num_rows($SubResultSet);
					while($SubRow = mysqli_fetch_assoc($SubResultSet))
					{
						// Check
						if($SubRow['Item_Count'] <= 0)
						{
							$category->Color = "#ff1c64";
						}
					}
				}

				mysqli_stmt_close($SubStatement);
				array_push($ret_list, $category);
			}
		}
		mysqli_stmt_close($MainStatement);
		return $ret_list;
	}

	function UniqueCategory($Data, $CategoryTableName, $Connection)
	{
		$PreparedStatement = SecureSQL(sprintf("SELECT * FROM %s WHERE Category_Name=?", $CategoryTableName), "s", array($Data['Category_Name']), $Connection);
		mysqli_stmt_store_result($PreparedStatement);
		if(mysqli_stmt_num_rows($PreparedStatement) > 0)
		{
			if(strcmp($Data['Ref_Category_Name'], $Data['Category_Name']) == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}





	// Item Functions

	function Get_Items($Connection, $UniqueIDAccessor, $Category_Name)
	{
		$ItemTableName = 'Item_' . $UniqueIDAccessor;

		$ret_list = array();

		$MainStatement = SecureSQL(sprintf('SELECT * FROM %s WHERE Item_Category=?', $ItemTableName), 's', array($Category_Name), $Connection);
		$ResultSet = mysqli_stmt_get_result($MainStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				$item = new ItemDatabaseResponse();
        		$item->Name = $Row['Item_Name'];
        		$item->Count = $Row['Item_Count'];
        		$item->Unit = $Row['Item_Unit'];
        		$item->CriticalCount = $Row['Item_CriticalCount'];
        		$item->Price = $Row['Item_Price'];
        		$item->LastEdited = $Row['Item_LastEdited'];
        		$item->PersonLastEdit = $Row['Item_PersonLastEdit'];

        		$item->Color = "#D5F4F5";

        		if($item->CriticalCount >= $item->Count)
				{
					$item->Color = "#f5cb42";
				}

				if($item->Count <= 0)
				{
					$item->Color = "#ff1c64";
				}

				mysqli_stmt_close($SubStatement);
				array_push($ret_list, $item);
			}
		}
		mysqli_stmt_close($MainStatement);
		return $ret_list;
	}

	function UniqueItem($Data, $ItemTableName, $Connection)
	{
		$PreparedStatement = SecureSQL(sprintf("SELECT * FROM %s WHERE Item_Name=?", $ItemTableName), "s", array($Data['Item_Name']), $Connection);
		mysqli_stmt_store_result($PreparedStatement);
		if(mysqli_stmt_num_rows($PreparedStatement) > 0)
		{
			if(strcmp($Data['Ref_Item_Name'], $Data['Item_Name']) == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}





	// Multi Edit Functions

	function Get_MultiEdit($Connection, $UniqueIDAccessor)
	{
		$MultiEditTableName = 'MultiEdit_' . $UniqueIDAccessor;
		$MultiEditItemsTableName = 'MultiEditItems_' . $UniqueIDAccessor;

		$ret_list = array();

		$MainStatement = SecureSQL(sprintf('SELECT * FROM %s', $MultiEditTableName), '', array(), $Connection);
		$ResultSet = mysqli_stmt_get_result($MainStatement);
		if(mysqli_num_rows($ResultSet) > 0)
		{
			while($Row = mysqli_fetch_assoc($ResultSet))
			{
				$multi_edit = new MultiEditContainer();
				$multi_edit->Name = $Row['MultiEdit_Name'];
				$multi_edit->Color = "#8697c2";
				$multi_edit->Desc = $Row['MultiEdit_Desc'];

				$multi_edit->MultiEditItemsList = array();	

				// MultiEdit Items List
				$SubStatement = SecureSQL(sprintf("SELECT * FROM %s WHERE ME_Name=?", $MultiEditItemsTableName), 's', array($Row['MultiEdit_Name']), $Connection);
				$SubResultSet = mysqli_stmt_get_result($SubStatement);
				if(mysqli_num_rows($SubResultSet) > 0)
				{
					while($SubRow = mysqli_fetch_assoc($SubResultSet))
					{
						$multi_edit_items = new MultiEditItemContainer();
						$multi_edit_items->ME_ItemName = $SubRow['ME_ItemName'];
						$multi_edit_items->ME_ItemCount = $SubRow['ME_ItemCount'];
						$multi_edit_items->ME_ItemPrice = $SubRow['ME_ItemPrice'];
						$multi_edit_items->ME_ItemUnit = $SubRow['ME_ItemUnit'];
						array_push($multi_edit->MultiEditItemsList, $multi_edit_items);	
					}
				}
				array_push($ret_list, $multi_edit);
			}
			mysqli_stmt_close($MainStatement);
		}
		return $ret_list;
	}

	function UniqueMultiEdit($Data, $MultiTableName, $Connection)
	{
		$PreparedStatement = SecureSQL(sprintf("SELECT * FROM %s WHERE MultiEdit_Name=?", $MultiTableName), "s", array($Data['MultiEdit_Name']), $Connection);
		mysqli_stmt_store_result($PreparedStatement);
		if(mysqli_stmt_num_rows($PreparedStatement) > 0)
		{
			if(strcmp($Data['Ref_MultiEdit_Name'], $Data['MultiEdit_Name']) == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
?>