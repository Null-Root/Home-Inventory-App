<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';

	$response = new CategoryResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		// Category
		
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


			$CategoryTableName = 'Category_' . $InventoryID;
			$ItemTableName = 'Item_' . $InventoryID;

			// MAIN
			switch ($Data['Action'])
			{
				case 'Add':
				{
					// Check If Category Already Exists
					if(UniqueCategory($Data, $CategoryTableName, $Connection))
					{
						// Add New Category
						SecureSQL(sprintf('INSERT INTO %s VALUES (?)', $CategoryTableName), 's', array($Data['Category_Name']), $Connection);
						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Send back New List
					$response->Category_List = Get_Categories($Connection, $InventoryID);
					
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Edit':
				{
					// Check If Category Already Exists
					if(UniqueCategory($Data, $CategoryTableName, $Connection))
					{
						// Renaming Existing Category	
						SecureSQL(sprintf('UPDATE %s SET Category_Name=? WHERE Category_Name=?', $CategoryTableName), 'ss', array($Data['Category_Name'], $Data['Ref_Category_Name']), $Connection);
						SecureSQL(sprintf('UPDATE %s SET Item_Category=? WHERE Item_Category=?', $ItemTableName), 'ss', array($Data['Category_Name'], $Data['Ref_Category_Name']), $Connection);
						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Send back New List
					$response->Category_List = Get_Categories($Connection, $InventoryID);

					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Delete':
				{
					// Delete Existing Category
					SecureSQL(sprintf('DELETE FROM %s WHERE Category_Name=?', $CategoryTableName), 's', array($Data['Category_Name']), $Connection);
					SecureSQL(sprintf('DELETE FROM %s WHERE Item_Category=?', $ItemTableName), 's', array($Data['Category_Name']), $Connection);
					// Send back New List
					$response->Category_List = Get_Categories($Connection, $InventoryID);
					$response->Status = 0; // 0 -> SUCCESS
				}
				break;
				case 'Load':
				{
					// Send back List
					$response->Category_List = Get_Categories($Connection, $InventoryID);
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
		$response->Status = 1;
	}

	echo json_encode($response);
?>