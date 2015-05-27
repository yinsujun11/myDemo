package com.mydemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;
public class Tools {
	private static Toast TOAST = null;
	public static int getVersionCode(Context context){
		try {
			PackageInfo info=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if(info!=null){
				return info.versionCode;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public static String getVersionName(Context context){
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if(packageInfo!=null){
				return packageInfo.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
    public static void showTextToast(Context context,String msg) {
		if(msg==null){
			return;
		}
		if (TOAST == null) {
			TOAST = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		} else {
			TOAST.setText(msg);
		}                       
		TOAST.show();
	}
}
