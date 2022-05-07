<?php
	require_once 'Config.php';
	require_once 'DataModel.php';
	require_once 'Functions.php';

	$response = new AppResponse();

	try
	{
		$Data = json_decode($_POST['data'], true);

		if(strcmp($Data['Token'], $App_Token) == 0)
		{
			$response->ChangeDatabaseData = false;

			switch ($Data['Action'])
			{
				case 'OfflineToOnline':
				{
					$CategoryQuery = "TRUNCATE Category_%s";
					$ItemQuery = "TRUNCATE Item_%s";
					$MultiEditQuery = "TRUNCATE MultiEdit_%s";
					$MultiEditItemsQuery = "TRUNCATE MultiEditItems_%s";

					$DB_ID = GetInventoryID($Data['UniqueID'], $Connection);

					SecureSQL(sprintf($CategoryQuery, $DB_ID), '', array(), $Connection);
					SecureSQL(sprintf($ItemQuery, $DB_ID), '', array(), $Connection);
					SecureSQL(sprintf($MultiEditQuery, $DB_ID), '', array(), $Connection);
					SecureSQL(sprintf($MultiEditItemsQuery, $DB_ID), '', array(), $Connection);

					$DatabaseData = $Data['New_DB_Data'];
					
					foreach($DatabaseData['categoryTable'] as $category)
					{
						SecureSQL(sprintf("INSERT INTO Category_%s VALUES (?)", $DB_ID), 's', array($category['Name']), $Connection);
					}

					foreach($DatabaseData['itemTable'] as $item)
					{
						SecureSQL(sprintf("INSERT INTO Item_%s VALUES (?, ?, ?, ?, ?, ?, ?, ?)", $DB_ID), 'ssisiiss', array($item['Category'], $item['Name'], $item['Count'], $item['Unit'], $item['CriticalCount'], $item['Price'], $item['LastEdited'], $item['PersonLastEdit']), $Connection);
					}

					foreach($DatabaseData['multiEditTable'] as $multiEdit)
					{
						SecureSQL(sprintf("INSERT INTO MultiEdit_%s VALUES (?, ?)", $DB_ID), 'ss', array($multiEdit['Name'], $multiEdit['Desc']), $Connection);
					}

					foreach($DatabaseData['multiEditItemsTable'] as $multiEditItems)
					{
						SecureSQL(sprintf("INSERT INTO Category_%s VALUES (?, ?, ?, ?, ?)", $DB_ID), 'ssdds', array($multiEditItems['Name'], $multiEditItems['ItemName'], $multiEditItems['ItemCount'], $multiEditItems['ItemPrice'], $multiEditItems['ItemUnit']), $Connection);
					}

					$response->Status = 0;
				}
				break;
				case 'OnlineToOffline':
				{
					$response->ChangeDatabaseData = true;

					$Query = "SELECT * FROM %s";
					$DB_ID = GetInventoryID($Data['UniqueID'], $Connection);

					$response->New_DB_Data = new DatabaseData();
					$response->New_DB_Data->categoryTable = array();
					$response->New_DB_Data->itemTable = array();
					$response->New_DB_Data->multiEditTable = array();
					$response->New_DB_Data->multiEditItemsTable = array();

					$CategoryDBTable = SecureSQL(sprintf($Query, "Category_".$DB_ID), "", array(), $Connection);
					$CategoryResultSet = mysqli_stmt_get_result($CategoryDBTable);
					if(mysqli_num_rows($CategoryResultSet) > 0)
					{
						while($Row = mysqli_fetch_assoc($CategoryResultSet))
						{
							$category_table = new CategoryTable();
							$category_table->Name = $Row['Category_Name'];

							array_push($response->New_DB_Data->categoryTable, $category_table);
						}
					}

					$ItemDBTable = SecureSQL(sprintf($Query, "Item_".$DB_ID), "", array(), $Connection);
					$ItemResultSet = mysqli_stmt_get_result($ItemDBTable);
					if(mysqli_num_rows($ItemResultSet) > 0)
					{
						while($Row = mysqli_fetch_assoc($ItemResultSet))
						{
							$item_table = new ItemTable();
    						$item_table->Category = $Row['Item_Category'];
    						$item_table->Name = $Row['Item_Name'];
    						$item_table->Count = $Row['Item_Count'];
    						$item_table->Unit = $Row['Item_Unit'];
    						$item_table->CriticalCount = $Row['Item_CriticalCount'];
    						$item_table->Price = $Row['Item_Price'];
    						$item_table->LastEdited = $Row['Item_LastEdited'];
    						$item_table->PersonLastEdit =$Row['Item_PersonLastEdit'];

							array_push($response->New_DB_Data->itemTable, $item_table);
						}
					}

					$MultiEditDBTable = SecureSQL(sprintf($Query, "MultiEdit_".$DB_ID), "", array(), $Connection);
					$MultiEditResultSet = mysqli_stmt_get_result($MultiEditDBTable);
					if(mysqli_num_rows($MultiEditResultSet) > 0)
					{
						while($Row = mysqli_fetch_assoc($MultiEditResultSet))
						{
							$multi_edit_table = new MultiEditTable();
							$multi_edit_table->Name = $Row['MultiEdit_Name'];
							$multi_edit_table->Desc = $Row['MultiEdit_Desc'];

							array_push($response->New_DB_Data->multiEditTable, $multi_edit_table);
						}
					}					

					$MultiEditItemsDBTable = SecureSQL(sprintf($Query, "MultiEditItems_".$DB_ID), "", array(), $Connection);
					$MultiEditItemsResultSet = mysqli_stmt_get_result($MultiEditItemsDBTable);
					if(mysqli_num_rows($MultiEditItemsResultSet) > 0)
					{
						while($Row = mysqli_fetch_assoc($MultiEditItemsResultSet))
						{
							$multi_edit_item_table = new MultiEditItemsTable();
    						$multi_edit_item_table->Name = $Row['ME_Name'];
    						$multi_edit_item_table->ItemName = $Row['ME_ItemName'];
    						$multi_edit_item_table->ItemCount = $Row['ME_ItemCount'];
    						$multi_edit_item_table->ItemPrice = $Row['ME_ItemPrice'];
    						$multi_edit_item_table->ItemUnit = $Row['ME_ItemUnit'];

    						array_push($response->New_DB_Data->multiEditItemsTable, $multi_edit_item_table);
						}
					}

					$response->Status = 0;
				}
				break;
				case 'CheckForUpdate':
				{
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