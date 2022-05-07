<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';

	$response = new ItemResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		// Item

		if(strcmp($Data['Token'], $App_Token) == 0)
		{
			// Insert verification if request can be satisfied
			$InventoryID = '';
			$response->AuthPermission = 0;

			// Check If Has Account
			if(isset($Data['Email']) && isset($Data['Password']))
			{
				if(isAccountValid($Data['Email'], $Data['Password'], $Connection)['Result'])
				{
					// Check If Owner
					if(IsInventoryOwner($Data['Email'], $Data['Password'], $Data['UniqueID'], $Connection))
					{
						$response->AuthPermission = 2;
						$InventoryID = GetInventoryID($Data['UniqueID'], $Connection);
					}
					else
					{
						$response->AuthPermission = AccountPermissions($Data['Email'], $Data['UniqueID'], $Connection);
						// Check Account Permissions
						if($response->AuthPermission > 0)
						{
							$InventoryID = GetInventoryID($Data['UniqueID'], $Connection);
						}
						else
						{
							echo json_encode($response);
							exit();
						}
					}
				}
				else
				{
					echo json_encode($response);
					exit();
				}
			}
			else
			{
				// Check Inventory Visibility
				$response->AuthPermission = InventoryVisibility($Data['UniqueID'], $Connection);
				if($response->AuthPermission > 0)
				{
					$InventoryID = GetInventoryID($Data['UniqueID'], $Connection);
				}
				else
				{
					echo json_encode($response);
					exit();
				}
			}


			$ItemTableName = 'Item_' . $InventoryID;
			$MultiEditItemsTableName = 'MultiEditItems_' . $InventoryID;

			// MAIN
			switch ($Data['Action'])
			{
				case 'Add':
				{
					// Check If Item Already Exists
					if(UniqueItem($Data, $ItemTableName, $Connection))
					{
						// Add New Category
						SecureSQL(sprintf('INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?)', $ItemTableName), 'ssdsddss', array($Data['Item_Category'], $Data['Item_Name'], $Data['Item_Count'], $Data['Item_Unit'], $Data['Item_CriticalCount'], $Data['Item_Price'], $Data['Item_LastEdited'], $Data['Item_PersonLastEdit']), $Connection);
						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Send back New List
					$response->Item_List = Get_Items($Connection, $InventoryID, $Data['Item_Category']);

					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Edit':
				{
					// Check If Item Already Exists
					if(UniqueItem($Data, $ItemTableName, $Connection))
					{

						// Renaming Existing Item
						SecureSQL(sprintf('UPDATE %s SET Item_Name=?, Item_Count=?, Item_Unit=?, Item_CriticalCount=?, Item_Price=?, Item_LastEdited=?, Item_PersonLastEdit=? WHERE Item_Name=?', $ItemTableName), 'sdsddsss', array($Data['Item_Name'], $Data['Item_Count'], $Data['Item_Unit'], $Data['Item_CriticalCount'], $Data['Item_Price'], $Data['Item_LastEdited'], $Data['Item_PersonLastEdit'], $Data['Ref_Item_Name']), $Connection);

						// Additional Processing for MultiEdits
						SecureSQL(sprintf('UPDATE %s SET ME_ItemName=? WHERE ME_ItemName=?', $MultiEditItemsTableName), 'ss', array($Data['Item_Name'], $Data['Ref_Item_Name']), $Connection);
						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Send back New List
					$response->Item_List = Get_Items($Connection, $InventoryID, $Data['Item_Category']);

					$response->Status = 0; // 0 -> SUCCESS

				}
				break;
				case 'Delete':
				{
					// Delete Existing Category
					SecureSQL(sprintf('DELETE FROM %s WHERE Item_Name=?', $ItemTableName), 's', array($Data['Item_Name']), $Connection);

					// Delete Category Under Multi Edit Items
					SecureSQL(sprintf('DELETE FROM %s WHERE ME_ItemName=?', $MultiEditItemsTableName), 's', array($Data['Item_Name']), $Connection);

					// Send back New List
					$response->Item_List = Get_Items($Connection, $InventoryID, $Data['Item_Category']);
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Load':
				{
					// Send back List
					$response->Item_List = Get_Items($Connection, $InventoryID, $Data['Item_Category']);
					$response->Status = 0; // 0 -> SUCCESS
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
		$response->Message = $e->getMessage();
		$response->Status = 1;
	}

	echo json_encode($response);
?>