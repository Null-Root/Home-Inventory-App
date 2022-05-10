package com.tuna_salmon.androidapp.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

        public String CurrentCategory = null;
        //endregion

        private SharedPreferences LocalData = null;

        public synchronized Map<String, String> getAppData() {
            Map<String, String> ret_val = new HashMap<>();
            ret_val.put(Const.Device.ID_TYPE, LocalData.getString(Const.Device.ID_TYPE, null));
            ret_val.put(Const.Device.ID, CurrentID);
            ret_val.put(Const.ID_Type.USER_ID, LocalData.getString(Const.ID_Type.USER_ID, null));
            ret_val.put(Const.ID_Type.CUSTOM_ID, LocalData.getString(Const.ID_Type.CUSTOM_ID, null));
            ret_val.put(Const.Device.DATA_SOURCE, LocalData.getString(Const.Device.DATA_SOURCE, null));
            return ret_val;
        }

        public synchronized boolean isAnyIDUsed() {
            if(CurrentID != null) {
                if(!CurrentID.equals("")) {
                    return true;
                }
            }
            return false;
        }

        public void onMainDataChanged() {
            LocalData.edit()
                    .putString(CurrentIDType, CurrentID)
                    .putString(Const.Device.ID_TYPE, CurrentIDType)
                    .putString(Const.Device.NAME, CurrentName)
                    .apply();
        }

        public synchronized void setIDInstance(String ID, String IDType) {
            this.CurrentID = ID;
            this.CurrentIDType = IDType;

            // Call On Data Change
            onMainDataChanged();
        }
    }

}
