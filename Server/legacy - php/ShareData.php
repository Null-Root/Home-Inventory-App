<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';

	$response = new ShareDataResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		if(strcmp($Data['Token'], $App_Token) == 0)
		{
			$response->ID_Exists = true;

			// IDs
			$MyInventoryID = GetInventoryID($Data['UniqueID'], $Connection);
			$OtherInventoryID = GetInventoryID($Data['ID'], $Connection);

			// My Share Tables
			$MyShareDataTableName = "ShareData_" . $MyInventoryID;
			$MySharedInventoryProfileTableName = "SharedInventoryProfile_" . $MyInventoryID;

			// Other Share Tables
			$OtherShareDataTableName = "ShareData_" . $OtherInventoryID;
			$OtherSharedInventoryProfileTableName = "SharedInventoryProfile_" . $OtherInventoryID;

			switch ($Data['Action'])
			{
				case 'My_Inventory_Edit':
				{
					// Check If ID Sent Exists on Database
					if(!isIDExists($Data['ID'], $Connection)) {
						$response->ID_Exists = false;
						echo json_encode($response);
						exit();
					}

					// Edit Other's Permission in this user share table
					SecureSQL(sprintf("UPDATE %s SET SharedPermission=? WHERE SharedPublicID=?", $MyShareDataTableName), "i", array($Data['Permission'], $Data['ID']), $Connection);

					$response->InventoryProfiles = Get_MyInventory_ProfileList($MyInventoryID, $Connection);

					$response->Status = 0;
				}
				break;
				case 'My_Inventory_Load':
				{
					$response->InventoryProfiles = Get_MyInventory_ProfileList($MyInventoryID, $Connection);

					$response->Status = 0;
				}
				break;
				case 'My_Inventory_Delete':
				{
					// Check If ID Sent Exists on Database
					if(!isIDExists($Data['ID'], $Connection)) {
						$response->ID_Exists = false;
						echo json_encode($response);
						exit();
					}

					// Delete other user contents from this user share table
					SecureSQL(sprintf('DELETE FROM %s WHERE SharedPublicID=?', $MyShareDataTableName), 's', array($Data['ID']), $Connection);

					// Delete this user public id from other user table
					SecureSQL(sprintf('DELETE FROM %s WHERE SharedProfileID=?', $OtherSharedInventoryProfileTableName), 's', array($Data['UniqueID']), $Connection);

					$response->InventoryProfiles = Get_MyInventory_ProfileList($MyInventoryID, $Connection);

					$response->Status = 0;
				}
				break;
				case 'Other_Inventory_Add':
				{
					// Check If ID Sent Exists on Database
					if(!isIDExists($Data['ID'], $Connection)) {
						$response->ID_Exists = false;
						echo json_encode($response);
						exit();
					}

					// Add this user contents to other user share table
					SecureSQL(sprintf('INSERT INTO %s VALUES (?, ?, ?)', $OtherShareDataTableName), 'ssi', array($Data['Name'], $Data['UniqueID'], 0), $Connection);

					// Add Other User Public ID to this Table
					SecureSQL(sprintf('INSERT INTO %s VALUES (?)', $MySharedInventoryProfileTableName), 's', array($Data['ID']), $Connection);

					$response->InventoryProfiles = Get_OtherInventory_ProfileList($MyInventoryID, $Connection);

					$response->Status = 0;
				}
				break;
				case 'Other_Inventory_Load':
				{
					$response->InventoryProfiles = Get_OtherInventory_ProfileList($MyInventoryID, $Connection);

					$response->Status = 0;
				}
				break;
				case 'Other_Inventory_Delete':
				{
					// Check If ID Sent Exists on Database
					if(!isIDExists($Data['ID'], $Connection)) {
						$response->ID_Exists = false;
						echo json_encode($response);
						exit();
					}

					// Deete this user contents from other user share table
					SecureSQL(sprintf('DELETE FROM %s WHERE SharedPublicID=?', $OtherShareDataTableName), 's', array($Data['UniqueID']), $Connection);

					// Delete Other User Public ID from this Table
					SecureSQL(sprintf('DELETE FROM %s WHERE SharedProfileID=?', $MySharedInventoryProfileTableName), 's', array($Data['ID']), $Connection);

					$response->InventoryProfiles = Get_OtherInventory_ProfileList($MyInventoryID, $Connection);

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