package com.mycj.massager_191.base;





import com.laputa.blue.util.XLog;
import com.mycj.massager_191.service.BlueService;
import com.mycj.massager_191.util.LogY;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.util.LogUtil;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;




public class BaseApp extends Application{
	public static int STATUS_CLOSE = 0x00;
	public static int STATUS_OPEN = 0x01;
	public static int STATUS_OPENING = 0x11;
	public static int STATUS_CLOSEING = 0x111;
	public static int status = STATUS_CLOSE;
	public static MycjMassagerInfo info;
	private BlueService xBlueService;
	public BlueService getXBlueService(){
		i( "Baseapp xBlueService:" + xBlueService);
		return this.xBlueService;
	}
	private ServiceConnection xBlueConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			xBlueService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof BlueService.BlueBinder) {
				BlueService.BlueBinder binder = (BlueService.BlueBinder) service;
				xBlueService = binder.getXBlueService();
				i("Baseapp xBlueService:" + xBlueService);
			}
		}
	};
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Intent xplIntent = new Intent(this,BlueService.class);
		bindService(xplIntent, xBlueConnection, Context.BIND_AUTO_CREATE);
		XLog.DEV_MODE = true;
		LogUtil.isDebug = true;
	}
	
	private boolean isDebug =  true;
	private void e(String msg){
		LogY.e(isDebug, "BlueService", msg);
	}
	private void i(String msg){
		LogY.i(isDebug, "BlueService", msg);
	}
	
}
