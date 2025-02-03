package com.tuna_salmon.home_inventory_app.services;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.local_database.api.LocalCategoryAPI;
import com.tuna_salmon.home_inventory_app.local_database.api.LocalItemAPI;
import com.tuna_salmon.home_inventory_app.local_database.api.LocalMultiEditAPI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataService {

    private RequestQueue mainRequestQueue;
    private String Cookie;

    private static DataService s_instance = null;
    private DataService() {}

    public static synchronized DataService getService() {
        if(s_instance == null)
            s_instance = new DataService();
        return s_instance;
    }

    public void Initialize(Context ctx) {
        mainRequestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        Cookie = "";

        if (UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.DATA_SOURCE).equals(Const.DataSource.ONLINE))
            UpdateCookie();
    }

    public LiveData<String> DataRequest(String URL, String Data, boolean requireWebData) {
        System.out.println(Data);
        if(requireWebData)
            return WebDataRequest(URL, Data);
        else {
            switch(Objects.requireNonNull(UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.DATA_SOURCE))) {
                case Const.DataSource.ONLINE:
                    return WebDataRequest(URL, Data);
                case Const.DataSource.OFFLINE:
                    return LocalDataRequest(URL, Data);
            }
        }
        return new MutableLiveData<>();
    }

    private LiveData<String> LocalDataRequest(String URL, String Data) {
        CustomFunctions.Logln(Data);
        MutableLiveData<String> data = new MutableLiveData<>();
        switch (URL) {
            case Const.API.CATEGORY:
                data.setValue(LocalCategoryAPI.getAPI().ParseData(Data));
                break;
            case Const.API.ITEM:
                data.setValue(LocalItemAPI.getAPI().ParseData(Data));
                break;
            case Const.API.MULTI_EDIT:
                data.setValue(LocalMultiEditAPI.getAPI().ParseData(Data));
                break;
        }
        return data;
    }

    @NotNull
    private LiveData<String> WebDataRequest(String URL, String Data) {
        UpdateCookie();
        MutableLiveData<String> data = new MutableLiveData<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    CustomFunctions.Logln(response);
                    if(response.contains("<html>") && response.length() > 200) {
                        UpdateCookie();
                        data.setValue(null);
                    }
                    else {
                        data.setValue(response);
                    }
                },
                error -> {
                    CustomFunctions.Logln(error.getMessage());
                    data.setValue(null);
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("data", Data);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Cookie", "__test=" + Cookie + "; expires=Friday, January 1, 2038 at 5:25:55 AM; path=/");
                return params;
            }
        };
        mainRequestQueue.add(stringRequest);
        return data;
    }

    //region Cookie Generation Handler
    public void UpdateCookie()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.API.ACCOUNT,
                response -> {
                    if(response.length() > 200)
                        getEncryptedCookie(response);
                },
                error -> {
                    UpdateCookie();
                });
        mainRequestQueue.add(stringRequest);
    }

    private void getEncryptedCookie(String Failed_Response)
    {
        ArrayList<String> sample = CustomSplit(Failed_Response, "toNumbers(\"", "\")");

        byte[] key = HexToBytes(sample.get(0));
        byte[] iv = HexToBytes(sample.get(1));
        byte[] bytesIn = HexToBytes(sample.get(2));

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] result = cipher.doFinal(bytesIn);
            this.Cookie = BytesToHex(result);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String BytesToHex(byte[] bytes)
    {
        char[] HexChars = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HexChars[v >>> 4];
            hexChars[j * 2 + 1] = HexChars[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] HexToBytes(String Hex)
    {
        Hex = Hex.replace("-", "");
        byte[] res = new byte[Hex.length() / 2];
        for(int i = 0; i < Hex.length(); i += 2)
        {
            res[i/2] = Integer.valueOf(Hex.substring(i, i+2), 16).byteValue();
        }
        return res;
    }

    private static ArrayList<String> CustomSplit(String SrcStr, String SubStart, String SubEnd)
    {
        ArrayList<String> Collection = new ArrayList<>();
        int StartIndex = 0, SubStartIndex = 0, SubEndIndex = 0;
        while(StartIndex < SrcStr.length())
        {
            SubStartIndex = SrcStr.indexOf(SubStart, StartIndex) + SubStart.length();
            SubEndIndex = SrcStr.indexOf(SubEnd, SubStartIndex) + SubEnd.length()-1;

            if(SubStartIndex >= SubEndIndex || SubEndIndex < StartIndex)
                break;

            Collection.add(SrcStr.substring(SubStartIndex, SubEndIndex-1));

            StartIndex = SubEndIndex;
        }
        return Collection;
    }
    //endregion
}