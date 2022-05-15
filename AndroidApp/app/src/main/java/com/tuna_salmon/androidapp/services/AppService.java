package com.tuna_salmon.androidapp.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.tuna_salmon.androidapp.Const;
import com.tuna_salmon.androidapp.R;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AppService {

    private Context ctx;
    private DataService _dataService;

    private Data data_instance;
    private UI ui_instance;

    public AppService(Context ctx, LayoutInflater layoutInflater, DataService _dataService) {
        this._dataService = _dataService;
        this.ctx = ctx;

        data_instance = new Data();
        ui_instance = new UI(layoutInflater);
    }

    public Data getAppDataService() {
        return data_instance;
    }

    public UI getAppUIService() {
        return ui_instance;
    }

    public class UI {
        private UI(LayoutInflater layoutInflater) {
            // Loading Animation
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setView(layoutInflater.inflate(R.layout.app_loading_animation, null));
            LoadingAnimation = alert.create();
            LoadingAnimation.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            LoadingAnimation.setCancelable(false);
        }

        private AlertDialog LoadingAnimation;

        public void startLoadingAnimation() {
            LoadingAnimation.show();
        }

        public void endLoadingAnimation() {
            LoadingAnimation.dismiss();
        }

        public void simpleDialog(String Title, String Content) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx)
                    .setTitle(Title)
                    .setMessage(Content)
                    .setNegativeButton("Proceed", null);
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        public void simpleShortToast(String Content) {
            Toast.makeText(ctx, Content, Toast.LENGTH_SHORT).show();
        }

        public void simpleLongToast(String Content) {
            Toast.makeText(ctx, Content, Toast.LENGTH_LONG).show();
        }
    }

    public class Data {
        //region Loaded App Data
        private String CurrentID = null;
        private String CurrentIDType = null;
        private String CurrentName = null;
        private Bitmap CurrentPic = null;

        public String CurrentCategory = null;
        //endregion

        private SharedPreferences LocalData = null;

        public Data() {
            LocalData = ctx.getSharedPreferences(Const.SharedPrefs.USER_DATA, Context.MODE_PRIVATE);

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
            this.CurrentIDType = LocalData.getString(Const.Device.ID_TYPE, null);
            this.CurrentID = LocalData.getString(this.CurrentIDType, null);
        }

        public Map<String, String> getAppData() {
            Map<String, String> ret_val = new HashMap<>();
            ret_val.put(Const.Device.ID_TYPE, LocalData.getString(Const.Device.ID_TYPE, null));
            ret_val.put(Const.Device.ID, CurrentID);
            ret_val.put(Const.ID_Type.USER_ID, LocalData.getString(Const.ID_Type.USER_ID, null));
            ret_val.put(Const.ID_Type.CUSTOM_ID, LocalData.getString(Const.ID_Type.CUSTOM_ID, null));
            ret_val.put(Const.Device.DATA_SOURCE, LocalData.getString(Const.Device.DATA_SOURCE, null));
            return ret_val;
        }

        public <T> void setAppData(String Ref_Name, T Data) {
            // Get SharedPreferences Editor
            SharedPreferences.Editor editor = LocalData.edit();

            if (Data instanceof String) {
                editor.putString(Ref_Name, (String) Data);
            }
            else if (Data instanceof Integer) {
                editor.putInt(Ref_Name, (Integer) Data);
            }
            else if (Data instanceof Boolean) {
                editor.putBoolean(Ref_Name, (Boolean) Data);
            }
            else if (Data instanceof Float) {
                editor.putFloat(Ref_Name, (Float) Data);
            }
            else if (Data instanceof Long) {
                editor.putLong(Ref_Name, (Long) Data);
            }

            editor.apply();

            onMainDataChanged();
        }

        public boolean isAnyIDUsed() {
            if(CurrentID != null) {
                if(!CurrentID.equals("")) {
                    return true;
                }
            }
            return false;
        }

        public void onMainDataChanged() {
            // Load to Storage
            LocalData.edit()
                    .putString(CurrentIDType, CurrentID)
                    .putString(Const.Device.ID_TYPE, CurrentIDType)
                    .putString(Const.Device.NAME, CurrentName)
                    .apply();

            // Load to Variables
        }

        public void setIDInstance(String ID, String IDType) {
            this.CurrentID = ID;
            this.CurrentIDType = IDType;

            // Call On Data Change
            onMainDataChanged();
        }

        public void setAccountInfo(String ID, String Name, Bitmap Pic) {
            CurrentName = Name;
            CurrentID = ID;
            CurrentPic = Pic;

            // Call On Data Change
            onMainDataChanged();
        }

        public boolean isAccountRegistered() {
            return false;
        }
    }
}
