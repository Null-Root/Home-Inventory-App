package com.tuna_salmon.home_inventory_app.data;

public class Const {
    public static class App {
        public final static String TOKEN = "QDEJwuB3pFN3jjRJKZBJ";
        public final static String VERSION = "v4.0.0";
    }

    public static class Account {
        public final static String SIGN_UP = "Sign Up";
        public final static String LOG_IN = "Log In";

        public final static String ADD_ACCOUNT = "Add Account";
        public final static String DELETE_ACCOUNT = "Delete Account";
        public final static String CHANGE_ID = "Change ID";
        public final static String GET_LOCK_STATE = "Get Lock";
        public final static String SET_LOCK_STATE = "Set Lock";
        public final static String GET_VISIBILITY_STATE = "Get Visibility";
        public final static String SET_VISIBILITY_STATE = "Set Visibility";
    }

    public static class ActionTypes {
        public final static String ADD = "Add";
        public final static String EDIT = "Edit";
        public final static String DELETE = "Delete";
        public final static String LOAD = "Load";

        public final static String USE = "Use";

        public static class SharedData {
            public final static String MY_INVENTORY_EDIT = "My_Inventory_Edit";
            public final static String MY_INVENTORY_LOAD = "My_Inventory_Load";
            public final static String MY_INVENTORY_DELETE = "My_Inventory_Delete";

            public final static String OTHER_INVENTORY_ADD = "Other_Inventory_Add";
            public final static String OTHER_INVENTORY_LOAD = "Other_Inventory_Load";
            public final static String OTHER_INVENTORY_DELETE = "Other_Inventory_Delete";
        }

        public static class AppFunction {
            public final static String OFFLINE_TO_ONLINE = "OfflineToOnline";
            public final static String ONLINE_TO_OFFLINE = "OnlineToOffline";
            public final static String CHECK_FOR_UPDATES = "Check For Updates";
        }
    }

    public static class ID {
        public final static String CHECK_ID = "Check ID";
    }

    public static class API {
        private final static String BASE_URL = "http://ts-mysql-db-site.epizy.com/native_android_app/home_inventory_app/";
        public final static String ACCOUNT = BASE_URL + "Account.php";
        public final static String SHARE_DATA = BASE_URL + "ShareData.php";
        public final static String CATEGORY = BASE_URL + "Category.php";
        public final static String ITEM = BASE_URL + "Item.php";
        public final static String MULTI_EDIT = BASE_URL + "MultiEdit.php";
        public final static String APP = BASE_URL + "App.php";
    }

    public static class ID_Type {
        public final static String USER_ID = "UserID";
        public final static String CUSTOM_ID = "CustomID";
    }

    public static class Device {
        public final static String NAME = "Name";
        public final static String ID = "ID";
        public final static String ID_TYPE = "ID_Type";
        public final static String EMAIL = "Email";
        public final static String PASSWORD = "Password";
        public final static String DATA_SOURCE = "Data_Source";
    }

    public static class DataType {
        public final static String ACCOUNT = "Account";
        public final static String CATEGORY = "Category";
        public final static String ITEM = "Item";
        public final static String MULTI_EDIT = "Multi Edit";
    }

    public static class DataSource {
        public final static String ONLINE = "Online";
        public final static String OFFLINE = "Offline";
    }

    public static class SharedPrefs {
        public final static String USER_DATA = "UserData";
    }
}