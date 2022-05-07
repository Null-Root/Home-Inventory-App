<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';

	$response = new MultiEditResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		// Multi Edit

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


			$MultiEditTableName = 'MultiEdit_' . $InventoryID;
			$MultiEditItemsTableName = 'MultiEditItems_' . $InventoryID;
			$ItemTableName = 'Item_' . $InventoryID;

			switch ($Data['Action'])
			{
				case 'Add':
				{
					if(UniqueMultiEdit($Data, $MultiEditTableName, $Connection))
					{
						SecureSQL(sprintf("INSERT INTO %s VALUES(?, ?)", $MultiEditTableName), 'ss', array($Data['MultiEdit_Name'], $Data['MultiEdit_Desc']), $Connection);

						// Insert Items Recursively
						foreach($Data['MultiEdit_ItemContainers'] as $item)
						{
							SecureSQL(sprintf("INSERT INTO %s VALUES(?, ?, ?, ?, ?)", $MultiEditItemsTableName), 'ssdds', array($Data['MultiEdit_Name'], $item['ME_ItemName'], $item['ME_ItemCount'], $item['ME_ItemPrice'], $item['ME_ItemUnit']), $Connection);
						}

						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Return MultiEdit List
					$response->MultiEditList = Get_MultiEdit($Connection, $InventoryID);

					// Get Data
					$response->Status = 0;
				}
				break;
				case 'Edit':
				{
					if(UniqueMultiEdit($Data, $MultiEditTableName, $Connection))
					{
						SecureSQL(sprintf("UPDATE %s SET MultiEdit_Name=?, MultiEdit_Desc=? WHERE MultiEdit_Name=?", $MultiEditTableName), 'sss', array($Data['MultiEdit_Name'], $Data['MultiEdit_Desc'], $Data['Ref_MultiEdit_Name']), $Connection);

						// Remove and Insert Template Items Data based on MultiEdit_Name //

						// // Remove All
						SecureSQL(sprintf("DELETE FROM %s WHERE ME_Name=?", $MultiEditItemsTableName), 's', array($Data['Ref_MultiEdit_Name']), $Connection);

						// // Insert Back
						foreach($Data['MultiEdit_ItemContainers'] as $item)
						{
							SecureSQL(sprintf("INSERT INTO %s VALUES(?, ?, ?, ?, ?)", $MultiEditItemsTableName), 'sssss', array($Data['MultiEdit_Name'], $item['ME_ItemName'], $item['ME_ItemCount'], $item['ME_ItemPrice'], $item['ME_ItemUnit']), $Connection);
						}

						$response->Exists = false;
					}
					else
					{
						$response->Exists = true;
					}

					// Return MultiEdit List
					$response->MultiEditList = Get_MultiEdit($Connection, $InventoryID);

					$response->Status = 0;
				}
				break;
				case 'Delete':
				{
					SecureSQL(sprintf("DELETE FROM %s WHERE MultiEdit_Name=?", $MultiEditTableName), 's', array($Data['MultiEdit_Name']), $Connection);
					// Remove All Items
					SecureSQL(sprintf("DELETE FROM %s WHERE ME_Name=?", $MultiEditItemsTableName), 's', array($Data['MultiEdit_Name']), $Connection);

					// Return MultiEdit List
					$response->MultiEditList = Get_MultiEdit($Connection, $InventoryID);

					$response->Status = 0;
				}
				break;
				case 'Load':
				{
					// Return MultiEdit List
					$response->MultiEditList = Get_MultiEdit($Connection, $InventoryID);

					$response->Status = 0;					
				}
				break;
				case 'Use':
				{
					// Local Vars
					$AllowItemCountChange = true;
					$response->MultiEditList = Get_MultiEdit($Connection, $InventoryID);
					$response->MultiEditErrorList = array();
					// Check Each Items
					if($Data['CheckInputs'])
					{
						foreach ($Data['MultiEdit_ItemContainers'] as $item)
						{
							// Get Current Number of Item
							$SubStatement = SecureSQL(sprintf("SELECT * FROM %s WHERE Item_Name=?", $ItemTableName), 's', array($item['ME_ItemName']), $Connection);
							$SubResultSet = mysqli_stmt_get_result($SubStatement);
							if(mysqli_num_rows($SubResultSet) > 0)
							{
								while($SubRow = mysqli_fetch_assoc($SubResultSet))
								{
									$ItemCount = (float)$SubRow['Item_Count'];
									$ItemMod = (float)$item['ME_ItemCount'];
									$ItemCriticalCount = (float)$SubRow['Item_CriticalCount'];

									if($ItemMod < 0)
									{
										// Warning
										if($ItemCount + $ItemMod < $ItemCriticalCount)
										{
											$AllowItemCountChange = false;
											
											// Add Warning to List
											$multi_edit_error = new MultiEditErrorList();
											$multi_edit_error->ItemName = $item['ME_ItemName'];
											$multi_edit_error->ItemColor = '#aaab9d';
											$multi_edit_error->ErrorMessage = sprintf("Item count reached critical count. [%s - %s = %s] \n Critical Count: %s}", (string)$ItemCount, (string)(abs($ItemMod)), (string)($ItemCount + $ItemMod), (string)$ItemCriticalCount);

											array_push($response->MultiEditErrorList, $multi_edit_error);
										}

										// Error
										if($ItemCount + $ItemMod < 0.000) // Check If Going Negative
										{
											$AllowItemCountChange = false;

											// Add Error to List
											$multi_edit_error = new MultiEditErrorList();
											$multi_edit_error->ItemName = $item['ME_ItemName'];
											$multi_edit_error->ItemColor = "#f54242";
											$multi_edit_error->ErrorMessage = sprintf("Item Count will go below zero [%s - %s = %s]", (string)$ItemCount, (string)(abs($ItemMod)), (string)($ItemCount + $ItemMod));

											array_push($response->MultiEditErrorList, $multi_edit_error);
										}
									}
									else
									{
										// Error
										if($ItemCount + $ItemMod > 99999.000) // Check If Count will go above the limit (99,999)
										{
											$AllowItemCountChange = false;
											// Add Error to List
											$multi_edit_error = new MultiEditErrorList();
											$multi_edit_error->ItemName = $item['ME_ItemName'];
											$multi_edit_error->IteColor = "#f54242";
											$multi_edit_error->ErrorMessage = sprintf("Item Count will go past the limit [%s + %s = %s]", (string)$ItemCount, (string)(abs($ItemMod)), (string)($ItemCount + $ItemMod));
											
											array_push($response->MultiEditErrorList, $multi_edit_error);
										}
									}
								}	
							}
						}
					}
					if($AllowItemCountChange)
					{
						// Use Each Items
						foreach($Data['MultiEdit_ItemContainers'] as $item)
						{
							// Get Current Number of Item
							$SubStatement = SecureSQL(sprintf("SELECT Item_Count FROM %s WHERE Item_Name=?", $ItemTableName), 's', array($item['ME_ItemName']), $Connection);
							$SubResultSet = mysqli_stmt_get_result($SubStatement);
							if(mysqli_num_rows($SubResultSet) > 0)
							{
								while($SubRow = mysqli_fetch_assoc($SubResultSet))
								{
									$NewItemCount = (float)$SubRow['Item_Count'] + (float)$item['ME_ItemCount'];

									// Check For Count that go past the limit (0, 10000)
									if($NewItemCount < 0.000)
									{
										$NewItemCount = 0;
									}
									elseif ($NewItemCount > 99999.000)
									{
										$NewItemCount = 99999;
									}

									// Update Item
									$MainStatement = SecureSQL(sprintf("UPDATE %s SET Item_Count=? WHERE Item_Name=?", $ItemTableName), 'ds', array($NewItemCount, $item['ME_ItemName']), $Connection);
								}
							}
						}
						$response->Status = 0;
					}
				}
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