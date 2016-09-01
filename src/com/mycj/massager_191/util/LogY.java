package com.mycj.massager_191.util;

import android.util.Log;

public class LogY {
	
	public static  void e(boolean isDebug,String tag ,String msg){
		if (isDebug) {
			Log.e(tag, msg);
		}
	}
	
	public static void i(boolean isDebug,String tag ,String msg){
		if (isDebug) {
			Log.i(tag, msg);
		}
	}
	
}
