<?php
	/**
	 * Database Config
	 */

    /*
	$Servername = "sql304.epizy.com";
    $Username = "epiz_27114609";
    $Password = "XcFas0eqjpq";
    $Database = "epiz_27114609_household_inventory_app_db";
    */

    $Servername = "localhost";
    $Username = "root";
    $Password = "tTyAnp8wX73CscU3";
    $Database = "test";

    // Connection var
    $Connection = mysqli_connect($Servername, $Username, $Password, $Database);

    // Const Token
    $App_Token = "QDEJwuB3pFN3jjRJKZBJ";
?>