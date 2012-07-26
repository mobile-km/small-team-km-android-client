package com.teamkn.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class JudgeNetWork {
	public static boolean checkNet_(Context context) {  
        ConnectivityManager mConnectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();  
        if (info == null) {  
            return false;  
        }  
        if (!info.isAvailable() || !mConnectivity.getBackgroundDataSetting()) {  
  
            return false;  
  
        } else {  
            return true;  
        }  
    }  
}
