package com.mycj.massager_191.service;

import java.util.HashSet;
import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.blue.core.Configration;
import com.laputa.blue.core.OnBlueChangedListener;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.mycj.massager_191.base.BaseApp;
import com.mycj.massager_191.util.LogY;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.core.MassagerProtocolNotifyManager;
import com.mycj.protocol.core.MassagerProtocolWriteManager;
import com.mycj.protocol.notify.OnProtocolNotifyListenerBasedapter;

public class BlueService extends Service {

	private AbstractSimpleLaputaBlue simpleLaputaBlue;
	
	public AbstractSimpleLaputaBlue getAbstractSimpleLaputaBlue() {
		return simpleLaputaBlue;
	}

	private MassagerProtocolWriteManager write = MassagerProtocolWriteManager.newInstance();


	public void startMassager(MycjMassagerInfo info) {
		if (info == null) {
			return;
		}
		write(write.writeForStartMassager(info));
	}

	public void stopMassager() {
		write(write.writeForStopMassager());
	}

	private Handler mHandler = new Handler() {
	};

	@Override
	public void onCreate() {
		super.onCreate();
		acquireWakeLock();
		simpleLaputaBlue = new SimpleLaputaBlue(this, new Configration(),
				new OnBlueChangedListener() {
					@Override
					public void reconnect(HashSet<String> devices) {
						e("-->size : " + devices.size());
						final String addressA = BondedDeviceUtil.get(1,
								getApplicationContext());
						if (BluetoothAdapter.checkBluetoothAddress(addressA)) {
							try {
								// 当前app存贮的蓝牙
								BluetoothDevice remoteDevice = simpleLaputaBlue
										.getAdapter().getRemoteDevice(addressA);
								// 所有的绑定蓝牙列表
								Set<BluetoothDevice> bondedDevices = simpleLaputaBlue
										.getAdapter().getBondedDevices();
								//
								if (bondedDevices.contains(remoteDevice)) {
									i("--> 已绑定 ：" + addressA);
									if (!ifAllConnected()) {
										connect(remoteDevice);
										return;
									}
								} else {
									i("--> 未绑定 ：" + addressA);
									// 当搜索列表中包含保存的addressA,并且未连接，就连接。
									if (devices.contains(addressA)) {
										if (!ifAllConnected()) {
											connect(remoteDevice);
										}
									} else {
										i("--> 搜索列表无：" + addressA);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								i("--> 重新连接失败！");
							}

						} else {
							i("--> 蓝牙地址不匹配，没有addessA" + addressA);
						}
					}

					@Override
					public void onStateChanged(String address, int state) {

					}

					@Override
					public void onServiceDiscovered(String address) {

					}

					@Override
					public void onCharacteristicChanged(String address,
							byte[] value) {
						parseData(value);

					}

					@Override
					public boolean isAllConnected() {
						return ifAllConnected();
					}
				});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new BlueBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class BlueBinder extends Binder {
		public BlueService getXBlueService() {
			return BlueService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
		closeAll();

	}

	public void startScan() {
		simpleLaputaBlue.scanDevice(true);
	}

	public void write(byte[] data) {

		simpleLaputaBlue.write(BondedDeviceUtil.get(1, this), data);
	}

	public void write(byte[][] data) {
		new WriteAsyncTask().execute(data);
	}

	public void stopScan() {
		simpleLaputaBlue.scanDevice(false);
	}

	public void connect(BluetoothDevice device) {
		simpleLaputaBlue.connect(device.getAddress());
	}

	public boolean ifAllConnected() {
		return simpleLaputaBlue.isConnected(BondedDeviceUtil.get(1, this));
	}

	public void startAutoConnect() {
		simpleLaputaBlue.startAutoConnectTask();
	}

	public void stopAutoConnect() {
		simpleLaputaBlue.stopAutoConnectTask();
	}

	public void closeAll() {
		simpleLaputaBlue.closeAll();
	}

	private class WriteAsyncTask extends AsyncTask<byte[][], Void, Void> {

		@Override
		protected Void doInBackground(byte[][]... params) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			byte[][] bs = params[0];
			if (bs != null && bs.length > 0) {
				for (int j = 0; j < bs.length; j++) {
					write(bs[j]);
				}
			}
			return null;
		}
	}

	/**
	 * <p>
	 * 解析数据
	 * </p>
	 * 
	 * @param data
	 * @param gatt
	 */
	private void parseData(byte[] data) {
		if (manager == null) {
			manager = new MassagerProtocolNotifyManager(this,
				new OnProtocolNotifyListenerBasedapter() {
				@Override
				public void onParseTime(String desc, int leftTime,
						int settingTime) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setLeftTime(leftTime);
						BaseApp.info.setSettingTime(settingTime);
					}
				}

				@Override
				public void onParseTemperature(String desc, int temp,
						int tempUnit) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setTemperature(temp);
						BaseApp.info.setTempUnit(tempUnit);
					}
				}

				@Override
				public void onParsePower(String desc, int power) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setPower(power);
					}
				}

				@Override
				public void onParsePattern(String desc, int pattern) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setPattern(pattern);
					}
				}

				@Override
				public void onParseMassagerInfo(String desc,
						MycjMassagerInfo info) {
					i(desc);
					BaseApp.info = info;
				}

				@Override
				public void onParseLoader(String desc, int loader) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setLoader(loader);
					}
			
				}

				@Override
				public void onParseChangeTimeCallBack(String desc,
						int success, int settingTime) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setSettingTime(settingTime);
						BaseApp.info.setLeftTime(settingTime);
					}
				}

				@Override
				public void onParseChangeTemperatureCallBack(
						String desc,int success, int temp, int tempUnit) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setTemperature(temp);
						BaseApp.info.setTempUnit(tempUnit);
					}
				}

				@Override
				public void onParseChangePowerCallBack(String desc,
						int success, int power) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setPower(power);
					}
				}

				@Override
				public void onParseChangeHeartRate(String desc, int hr) {
					i(desc);
					if (BaseApp.info!=null) {
						BaseApp.info.setHr(hr);
					}

				}
				});
		}
		manager.parse(data);

	}

	WakeLock wakeLock = null;
	private MassagerProtocolNotifyManager manager;

	// 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	private void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "PostLocationService");
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	
	private boolean isDebug =  true;
	private void e(String msg){
		LogY.e(isDebug, "BlueService", msg);
	}
	private void i(String msg){
		LogY.i(isDebug, "BlueService", msg);
	}
	
}
