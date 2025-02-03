package com.tuna_salmon.home_inventory_app.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserAppHandler {

    public static class Data {

        public static class Temp {
            public static class Auth {
                private static String Name = "";
                private static String Email = "";
                private static String Password = "";
                private static String Code = "";

                public static synchronized void SetAccount(String _Name, String _Email, String _Pass, String _Code) {
                    Name = _Name;
                    Email = _Email;
                    Password = _Pass;
                    Code = _Code;
                }

                public static synchronized String getName() {
                    return Name;
                }

                public static synchronized String getEmail() {
                    return Email;
                }

                public static synchronized String getPassword() {
                    return Password;
                }

                public static synchronized String getCode() {
                    return Code;
                }
            }
        }

        //region Singleton
        private static Data s_instance = null;
        private Data() {}

        public static synchronized Data AppData() {
            if(s_instance == null)
                s_instance = new Data();
            return s_instance;
        }
        //endregion

        private Context context = null;
        private String CurrentID = null;
        private String CurrentIDType = null;

        private String CurrentName = null;
        private String CurrentEmail = null;
        private String CurrentPassword = null;

        public String CurrentCategory = null;

        private SharedPreferences LocalData = null;

        public void Initialize(Context ctx) {
            this.context = ctx;

            LocalData = this.context.getSharedPreferences(Const.SharedPrefs.USER_DATA, Context.MODE_PRIVATE);

            // Check For Name
            if (!LocalData.contains(Const.Device.NAME))
                LocalData.edit().putString(Const.Device.NAME, "<DEF_USER>").apply();
            // Check For Email
            if (!LocalData.contains(Const.Device.EMAIL))
                LocalData.edit().putString(Const.Device.EMAIL, null).apply();
            // Check For Password
            if(!LocalData.contains(Const.Device.PASSWORD))
                LocalData.edit().putString(Const.Device.PASSWORD, null).apply();
            // Check For UserID
            if (!LocalData.contains(Const.ID_Type.USER_ID))
                LocalData.edit().putString(Const.ID_Type.USER_ID, null).apply();
            // Check For Custom ID
            if (!LocalData.contains(Const.ID_Type.CUSTOM_ID))
                LocalData.edit().putString(Const.ID_Type.CUSTOM_ID, null).apply();
            // Check For ID Last Chosen
            if (!LocalData.contains(Const.Device.ID_TYPE))
                LocalData.edit().putString(Const.Device.ID_TYPE, null).apply();
            // Check For Data Source
            if (!LocalData.contains(Const.Device.DATA_SOURCE))
                LocalData.edit().putString(Const.Device.DATA_SOURCE, Const.DataSource.ONLINE).apply();
            

            // Set Singleton Variables
            this.CurrentName = LocalData.getString(Const.Device.NAME, null);
            this.CurrentEmail = LocalData.getString(Const.Device.EMAIL, null);
            this.CurrentPassword = LocalData.getString(Const.Device.PASSWORD, null);
            this.CurrentIDType = LocalData.getString(Const.Device.ID_TYPE, null);
            this.CurrentID = LocalData.getString(this.CurrentIDType, null);
        }

        private synchronized void OnMainDataChanged() {
            LocalData.edit()
                    .putString(CurrentIDType, CurrentID)
                    .putString(Const.Device.ID_TYPE, CurrentIDType)
                    .putString(Const.Device.NAME, CurrentName)
                    .putString(Const.Device.EMAIL, CurrentEmail)
                    .putString(Const.Device.PASSWORD, CurrentPassword)
                    .apply();

            if(LocalData.getString(Const.Device.DATA_SOURCE, null).equals(Const.DataSource.ONLINE)) {
                DataService.getService().UpdateCookie();
            }
        }

        public synchronized void Set_ID_Instance(String ID, String IDType) {
            this.CurrentID = ID;
            this.CurrentIDType = IDType;

            // Call On Data Change
            OnMainDataChanged();
        }

        public synchronized void Set_User_Credentials(String Name, String Email, String Password) {
            this.CurrentName = Name;
            this.CurrentEmail = Email;
            this.CurrentPassword = Password;

            // Call On Data Change
            OnMainDataChanged();
        }

        public synchronized Map<String, String> Get_App_Data() {
            Map<String, String> ret_val = new HashMap<>();
            ret_val.put(Const.Device.NAME, LocalData.getString(Const.Device.NAME, null));
            ret_val.put(Const.Device.EMAIL, LocalData.getString(Const.Device.EMAIL, null));
            ret_val.put(Const.Device.PASSWORD, LocalData.getString(Const.Device.PASSWORD, null));
            ret_val.put(Const.Device.ID_TYPE, LocalData.getString(Const.Device.ID_TYPE, null));
            ret_val.put(Const.Device.ID, CurrentID);
            ret_val.put(Const.ID_Type.USER_ID, LocalData.getString(Const.ID_Type.USER_ID, null));
            ret_val.put(Const.ID_Type.CUSTOM_ID, LocalData.getString(Const.ID_Type.CUSTOM_ID, null));
            ret_val.put(Const.Device.DATA_SOURCE, LocalData.getString(Const.Device.DATA_SOURCE, null));
            return ret_val;
        }

        public synchronized <T> void Set_Local_Item(String Ref_Name, T Data) {
            // Get SharedPreferences Editor
            SharedPreferences.Editor editor = LocalData.edit();

            if (Data instanceof String)
                editor.putString(Ref_Name, (String) Data);
            else if (Data instanceof Integer)
                editor.putInt(Ref_Name, (Integer) Data);
            else if (Data instanceof Boolean)
                editor.putBoolean(Ref_Name, (Boolean) Data);
            else if (Data instanceof Float)
                editor.putFloat(Ref_Name, (Float) Data);
            else if (Data instanceof Long)
                editor.putLong(Ref_Name, (Long) Data);

            editor.apply();

            OnMainDataChanged();
        }

        public synchronized <T> T Get_Local_Item(String Ref_Name, Type DataType) {
            T Item = null;

            if(DataType == String.class)
                Item = (T) LocalData.getString(Ref_Name, null);
            else if(DataType == Integer.class)
                Item = (T) Integer.valueOf(LocalData.getInt(Ref_Name, 0));
            else if(DataType == Boolean.class)
                Item = (T) Boolean.valueOf(LocalData.getBoolean(Ref_Name, false));
            else if(DataType == Float.class)
                Item = (T) Float.valueOf(LocalData.getFloat(Ref_Name, 0f));
            else if(DataType == Long.class)
                Item = (T) Long.valueOf(LocalData.getLong(Ref_Name, 0l));

            return Item;
        }

        public synchronized void Set_Local_Array(String Ref_Name, Set<String> Data) {
            LocalData.edit().putStringSet(Ref_Name, Data).apply();
        }

        public synchronized Set<String> Get_Local_Array(String Ref_Name) {
            return LocalData.getStringSet(Ref_Name, null);
        }

        public synchronized boolean isAccountRegistered() {
            if(CurrentEmail != null && CurrentPassword != null && CurrentName != null) {
                return true;
            }
            return false;
        }

        public synchronized boolean isAnyIDUsed() {
            if(CurrentID != null) {
                if(!CurrentID.equals("")) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class UI {

        //region Singleton
        private static UI s_instance = null;
        private UI() {}

        public static synchronized UI AppUI() {
            if(s_instance == null)
                s_instance = new UI();
            return s_instance;
        }
        //endregion

        private Context context;

        private AlertDialog LoadingAnimation;

        public void Initialize(Context ctx, LayoutInflater layoutInflater) {
            this.context = ctx;
            // Loading Animation
            AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
            alert.setView(layoutInflater.inflate(R.layout.app_loading_animation, null));
            LoadingAnimation = alert.create();
            LoadingAnimation.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            LoadingAnimation.setCancelable(false);
        }

        public void StartLoadingAnimation() {
            LoadingAnimation.show();
        }

        public void EndLoadingAnimation() {
            LoadingAnimation.dismiss();
        }

        public void SimpleDialog(String Title, String Content) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this.context)
                    .setTitle(Title)
                    .setMessage(Content)
                    .setNegativeButton("Proceed", null);
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        public void SimpleToast(String Content, int ToastDuration) {
            Toast.makeText(this.context, Content, ToastDuration).show();
        }
    }
}
