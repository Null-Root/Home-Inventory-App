<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';
	require_once '_EmailSendData.php';

	$response = new AccountResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		if(strcmp($Data['Token'], $App_Token) == 0)
		{
			switch ($Data['Action'])
			{
				case 'Check ID':
				{
					// Check If ID is Valid
					$PreparedStatement = SecureSQL('SELECT * FROM UserData WHERE UserPublicUniqueID=?', 's', array($Data['UniqueID']), $Connection);
					$ResultSet = mysqli_stmt_get_result($PreparedStatement);
					if(mysqli_num_rows($ResultSet) > 0)
					{
						// ID Exists
						$response->AuthResponse = 1; // 1 -> AUTH PROMPT ACCEPTED
						$response->Message = "ID Exists :)";
					}
					else
					{
						$response->AuthResponse = 0; // 0 -> AUTH PROMPT REVOKED
						$response->Message = "ID Does Not Exist :(";
					}
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Sign Up':
				{
					// Check If Account Does Not Exist
					$PreparedStatement = SecureSQL('SELECT * FROM UserData WHERE UserEmail=?', 's', array($Data['Email']), $Connection);
					$ResultSet = mysqli_stmt_get_result($PreparedStatement);
					if(mysqli_num_rows($ResultSet) > 0)
					{
						$response->AuthResponse = 0; // 0 -> AUTH PROMPT REVOKED
						$response->Message = "Account Already Exists";
					}
					else
					{
						$response->AuthResponse = 1; // 1 -> GO TO CONFIRM (SIGN UP)

						// Try to send confirmation code
						EmailSendCode($Data['Name'], $Data['Email'], $Data['Code']);
					}
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Log In':
				{
					// Check If Account Exists
					$PreparedStatement = SecureSQL('SELECT * FROM UserData WHERE UserEmail=?', 's', array($Data['Email']), $Connection);
					$ResultSet = mysqli_stmt_get_result($PreparedStatement);
					if(mysqli_num_rows($ResultSet) > 0)
					{
						$response->AuthResponse = 0; // 0 -> AUTH PROMPT REVOKED
						$response->Message = "Wrong Password";
						
						// Return Public Unique ID
						while($Row = mysqli_fetch_assoc($ResultSet))
						{
							if(password_verify($Data['Password'], $Row['UserPass']))
							{
								$response->AuthResponse = 2; // 2 -> GO BACK TO DASHBOARD (LOG IN/CONFIRM)
								$response->Message = "Auth Success";
								$response->Name = $Row['UserName'];
								$response->UniqueID = $Row['UserPublicUniqueID'];
							}
						}
					}
					else
					{
						$response->AuthResponse = 0; // 0 -> AUTH PROMPT REVOKED
						$response->Message = "Account Does Not Exists";
					}
					$response->Status = 0; // 0 -> SUCCESS

				}
				break;
				case 'Add Account':
				{
					// Generate Private Unique ID
					$NewPrivateUserID = generateNewID($Connection, "UserPrivateUniqueID", 10);
					$NewPublicUserID = generateNewID($Connection, "UserPublicUniqueID", 10);

					// Add Credentials to Database
					SecureSQL('INSERT INTO UserData VALUES (?, ?, ?, ?, ?, ?, ?)', 'sssssii', array($Data['Name'], $Data['Email'], password_hash($Data['Password'], PASSWORD_DEFAULT), $NewPrivateUserID, $NewPublicUserID, false, 0), $Connection);

					// Generate Required Tables For Account //

					// Table

					// // CONNECTED TO OWNER // //
					$ShareDataTableContent = <<<SHAREDATA
					CREATE TABLE %s
					(
						SharedName VARCHAR(50) NOT NULL,
						SharedPublicID VARCHAR(100) UNIQUE NOT NULL,
						SharedPermission INT NOT NULL
					);
SHAREDATA;
					
					// // OWNER CONNECTS TO // //
					$SharedInventoryProfileContent = <<<SHAREINVENTORYPROFILE
					CREATE TABLE %s
					(
						SharedProfileID VARCHAR(100) UNIQUE NOT NULL
					);
SHAREINVENTORYPROFILE;

					$CategoryTableContent = <<<CATEGORY
					CREATE TABLE %s
					(
						Category_Name VARCHAR(50) UNIQUE NOT NULL
					);
CATEGORY;

					$ItemTableContent = <<<ITEM
					CREATE TABLE %s
					(
						Item_Category VARCHAR(50) NOT NULL,
						Item_Name VARCHAR(50) UNIQUE NOT NULL,
						Item_Count DECIMAL(10, 3) NOT NULL,
						Item_Unit VARCHAR(50) NOT NULL,
						Item_CriticalCount DECIMAL(10, 3) NOT NULL,
						Item_Price DECIMAL(10, 3) NOT NULL,
						Item_LastEdited VARCHAR(50) NOT NULL,
						Item_PersonLastEdit VARCHAR(50) NOT NULL
					);
ITEM;

					$MultiEditTableContent = <<<MULTI_EDIT
					CREATE TABLE %s
					(
					    MultiEdit_Name VARCHAR(50) UNIQUE NOT NULL,
						MultiEdit_Desc VARCHAR(300) NOT NULL
					);
MULTI_EDIT;

					$MultiEditItemsTableContent = <<<MULTI_EDIT_ITEM
					CREATE TABLE %s
					(
					    ME_Name VARCHAR(50) NOT NULL,
					    ME_ItemName VARCHAR(50) NOT NULL,
					    ME_ItemCount DECIMAL(10, 3) NOT NULL,
					    ME_ItemPrice DECIMAL(10, 3) NOT NULL,
					    ME_ItemUnit VARCHAR(50) NOT NULL
					);
MULTI_EDIT_ITEM;
	
					// Table Name
					$ShareDataTableName = "ShareData_" . $NewPrivateUserID;
					$SharedInventoryProfileTableName = "SharedInventoryProfile_" . $NewPrivateUserID;
					$CategoryTableName = "Category_" . $NewPrivateUserID;
					$ItemTableName = "Item_" . $NewPrivateUserID;
					$MultiEditTableName = "MultiEdit_" . $NewPrivateUserID;
					$MultiEditItemsTableName = "MultiEditItems_" . $NewPrivateUserID;

					// SQL
					BasicSQL(sprintf($SharedInventoryProfileContent, $SharedInventoryProfileTableName), $Connection);
					BasicSQL(sprintf($ShareDataTableContent, $ShareDataTableName), $Connection);
					BasicSQL(sprintf($CategoryTableContent, $CategoryTableName), $Connection);
					BasicSQL(sprintf($ItemTableContent, $ItemTableName), $Connection);
					BasicSQL(sprintf($MultiEditTableContent, $MultiEditTableName), $Connection);
					BasicSQL(sprintf($MultiEditItemsTableContent, $MultiEditItemsTableName), $Connection);

					// Return Public Unique ID
					$response->UniqueID = $NewPublicUserID;
					
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Delete Account':
				{
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						// Remove Credentials From Database
						SecureSQL('DELETE FROM UserData WHERE UserEmail=?', 's', array($Data['Email']), $Connection);

						// Get Private ID
						$Data_ID = GetInventoryID($Data['UniqueID'], $Connection);

						// Remove Generated Tables
						$ShareDataTableName = 'ShareData_' . $Data_ID;
						$SharedInventoryProfileTableName = "SharedInventoryProfile_" . $Data_ID;
						$CategoryTableName = 'Category_' . $Data_ID;
						$ItemTableName = 'Item_' . $Data_ID;
						$MultiEditTableName = 'MultiEdit_' . $Data_ID;
						$MultiEditItemsTableName = 'MultiEditItems_' . $Data_ID;

						// Loop IDs From Shared Profile Inventory: remove this id from other user share table [Remove All Ref from Accounts this user connected to]
						$PreparedStatement = SecureSQL(sprintf('SELECT * FROM %s', $SharedInventoryProfileTableName), '', array(), $Connection);
						$ResultSet = mysqli_stmt_get_result($PreparedStatement);
						if(mysqli_num_rows($ResultSet) > 0)
						{
							while($Row = mysqli_fetch_assoc($ResultSet))
							{
								$InventoryID = GetInventoryID($Row['SharedProfileID'], $Connection);
								$OtherShareDataTableName = 'ShareData_' . $InventoryID;

								SecureSQL(sprintf('DELETE FROM %s WHERE SharedPublicID=?', $OtherShareDataTableName), 's', array($Data['UniqueID']), $Connection);
							}
						}

						// Loop IDs From Share Table: remove this id from other user shared profile inventory [Remove All Ref from Accounts connected to this user]
						$PreparedStatement = SecureSQL(sprintf('SELECT * FROM %s', $ShareDataTableName), '', array(), $Connection);
						$ResultSet = mysqli_stmt_get_result($PreparedStatement);
						if(mysqli_num_rows($ResultSet) > 0)
						{
							while($Row = mysqli_fetch_assoc($ResultSet))
							{
								$InventoryID = GetInventoryID($Row['SharedPublicID'], $Connection);
								$OtherSharedInventoryProfileTableName = 'SharedInventoryProfile_' . $InventoryID;

								SecureSQL(sprintf('DELETE FROM %s WHERE SharedProfileID=?', $OtherSharedInventoryProfileTableName), 's', array($Data['UniqueID']), $Connection);
							}
						}

						// Remove User Tables
						BasicSQL(sprintf('DROP TABLE %s', $ShareDataTableName), $Connection);
						BasicSQL(sprintf('DROP TABLE %s', $SharedInventoryProfileTableName), $Connection);
						BasicSQL(sprintf('DROP TABLE %s', $CategoryTableName), $Connection);
						BasicSQL(sprintf('DROP TABLE %s', $ItemTableName), $Connection);
						BasicSQL(sprintf('DROP TABLE %s', $MultiEditTableName), $Connection);
						BasicSQL(sprintf('DROP TABLE %s', $MultiEditItemsTableName), $Connection);

						$response->AuthResponse = 2;
					}
					$response->Status = 0;
				}
				break;
				case 'Change ID':
				{
					// Change Public ID
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						// Generate New ID
						$NewID = generateNewID($Connection, "UserPublicUniqueID", 10);

						// Change Public ID
						SecureSQL('UPDATE UserData SET UserPublicUniqueID=? WHERE UserEmail=?', 'ss', array($NewID, $Data['Email']), $Connection);

						// Change ProfileID on each connected user
						// Loop IDs From Shared Profile Inventory: update this id from other user share table [Remove All Ref from Accounts this user connected to]
						$PreparedStatement = SecureSQL(sprintf('SELECT * FROM %s', $SharedInventoryProfileTableName), '', array(), $Connection);
						$ResultSet = mysqli_stmt_get_result($PreparedStatement);
						if(mysqli_num_rows($ResultSet) > 0)
						{
							while($Row = mysqli_fetch_assoc($ResultSet))
							{
								$InventoryID = GetInventoryID($Row['SharedProfileID'], $Connection);
								$OtherShareDataTableName = 'ShareData_' . $InventoryID;

								SecureSQL(sprintf('UPDATE %s SET SharedPublicID=? WHERE SharedPublicID=?', $OtherShareDataTableName), 'ss', array($NewID, $Data['UniqueID']), $Connection);
							}
						}

						// Loop IDs From Share Table: update this id from other user shared profile inventory [Remove All Ref from Accounts connected to this user]
						$PreparedStatement = SecureSQL(sprintf('SELECT * FROM %s', $ShareDataTableName), '', array(), $Connection);
						$ResultSet = mysqli_stmt_get_result($PreparedStatement);
						if(mysqli_num_rows($ResultSet) > 0)
						{
							while($Row = mysqli_fetch_assoc($ResultSet))
							{
								$InventoryID = GetInventoryID($Row['SharedPublicID'], $Connection);
								$OtherSharedInventoryProfileTableName = 'SharedInventoryProfile_' . $InventoryID;

								SecureSQL(sprintf('UPDATE %s SET SharedProfileID=? WHERE SharedProfileID=?', $OtherSharedInventoryProfileTableName), 'ss', array($NewID, $Data['UniqueID']), $Connection);
							}
						}

						$response->UniqueID = $NewID;
						$response->AuthResponse = 2;
					}
					$response->Status = 0;
				}
				break;
				case 'Get Lock':
				{
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						$response->LockState = IntToBool(Get_User_Data("UserLock", $Data['Email'], $Connection));
						$response->AuthResponse = 2;
					}
					$response->Status = 0;
				}
				break;
				case 'Set Lock':
				{
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						SecureSQL('UPDATE UserData SET UserLock=? WHERE UserEmail=?', 'is', array($Data['LockState'], $Data['Email']), $Connection);
						$response->LockState = IntToBool(Get_User_Data("UserLock", $Data['Email'], $Connection));
					}
					$response->Status = 0;
				}
				break;
				case 'Get Visibility':
				{
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						$response->Visibility = Get_User_Data("UserVisibility", $Data['Email'], $Connection);
						$response->AuthResponse = 2;
					}
					$response->Status = 0;
				}
				break;
				case 'Set Visibility':
				{
					if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
					{
						SecureSQL('UPDATE UserData SET UserVisibility=? WHERE UserEmail=?', 'is', array($Data['Visibility'], $Data['Email']), $Connection);
						$response->Visibility = Get_User_Data("UserVisibility", $Data['Email'], $Connection);
					}
					$response->Status = 0;
				}
				break;
			}
		}
		else
		{
			echo 'Unauthorized Access';
			exit();
		}
	}
	catch(Exception $e)
	{
		$response->Status = 1;
		$response->Message = $e->getMessage();
	}

	echo json_encode($response);
?>