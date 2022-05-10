package com.tuna_salmon.androidapp.ext;

import android.content.Context;
import android.util.TypedValue;

public class ExtFunctions {
    public static int Sp_To_Px(Context ctx, float Value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Value, ctx.getResources().getDisplayMetrics());
    }

    public static boolean isVarSet(String Input) {
        if(Input != null) {
            if(!Input.equals("")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidNumber(String Input) {
        if (Input.contains(".")) {
            String[] inp_arr = Input.split("[.]");
            if (inp_arr.length == 2) {
                return inp_arr[0].length() <= 5 && inp_arr[1].length() <= 3;
            }
        }
        else
        {
            return Input.length() <= 5;
        }
        return false;
    }

    public static boolean isValidStr(String Input, int MinLength, int MaxLength) {
        if(Input.length() >= MinLength && Input.length() <= MaxLength) {
            return true;
        }
        return false;
    }

    public static void Logln(String logs)
    {
        System.out.println(logs);
    }

    public static void Log(String Delim, Object... logs) {
        for(Object log : logs) {
            System.out.print(log + Delim);
        }
    }
}
