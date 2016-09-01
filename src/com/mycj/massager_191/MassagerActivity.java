package com.mycj.massager_191;

import java.util.ArrayList;
import java.util.List;

import com.laputa.blue.broadcast.LaputaBroadcast;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.laputa.blue.util.XLog;
import com.mycj.massager_191.base.BaseActivity;
import com.mycj.massager_191.base.BaseApp;
import com.mycj.massager_191.service.BlueService;
import com.mycj.massager_191.util.Constant;
import com.mycj.massager_191.util.TimeUtil;
import com.mycj.massager_191.util.ToastUtil;
import com.mycj.massager_191.view.ColorCircleView;
import com.mycj.massager_191.view.ColorCircleView.OnTimePointChangeListener;
import com.mycj.massager_191.view.DeviceAdapter;
import com.mycj.massager_191.view.DeviceDialog;
import com.mycj.massager_191.view.DeviceDialog.OnButtonClickListener;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.core.ProtocolBroadcast;
import com.mycj.protocol.core.ProtocolBroadcastReceiver;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MassagerActivity extends BaseActivity {

	private DeviceDialog chooseBlueADialog;
	private List<BluetoothDevice> devices;
	private DeviceAdapter adapter;
	private int pattern = 0;
	private ImageView ivLoading;
	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_massager);
		blueService = getBlueService();
		devices = new ArrayList<BluetoothDevice>();
		adapter = new DeviceAdapter(devices, this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (null != bundle) {
			pattern = bundle.getInt("pattern");
		}
		initViews();
		// 更新uI
//		updateUi(blueService != null ? blueService.getMassagerInfo() : null);
		updateUi(BaseApp.info);

		updateBleStatus(blueService != null && blueService.ifAllConnected());
		registerReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private void updateTitle(String title) {
		tvTitle.setText(title);
	}

	private void updateUi(MycjMassagerInfo info) {
		updateTitle("按摩模式 : " + pattern);
		boolean start = false;
		boolean load = false;
		int time = 0;
		int power = 0;

		if (null != info && info.getOpen() == 1 && info.getPattern() == pattern) {
			start = info.getOpen() == 1 ? true : false;
			load = info.getLoader() == 1 ? true : false;
			time = info.getLeftTime();
			power = info.getPower();

		} else {
			start = false;
			load = false;
			time = Constant.DEFAULT_TIME;
			power = Constant.DEFAULT_POWER;
		}

		updateStartOrStop(start);
		updateFuzai(load);
		updateTime(time * 60);
		updatePower(power);

	}

	private void updatePower(int power) {
		ccPower.setProgress(power - 1);
	}

	private void updateTime(int time) {
		tvTime.setText(TimeUtil.getStringTime(time));
	}

	private void updateBleStatus(boolean connect) {
		if (connect) {
			tvBleStatus.setText("连接成功");
			tvBleStatus.setTextColor(Color.BLUE);
			ivBleStatus.setImageResource(R.drawable.ic_ble_icon_1);
		} else {
			tvBleStatus.setText("按摩器掉线了...");
			tvBleStatus.setTextColor(Color.RED);
			ivBleStatus.setImageResource(R.drawable.ic_ble_icon_1_miss);
		}
	}

	private void updateFuzai(boolean load) {
		if (load) {
			ivFuzai.setImageResource(R.drawable.ic_electload_ok);
		} else {
			ivFuzai.setImageResource(R.drawable.ic_electload);
		}
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_title);

		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivHistory = (ImageView) findViewById(R.id.iv_history);
		ccPower = (ColorCircleView) findViewById(R.id.cc_massager_power);
		ivFuzai = (ImageView) findViewById(R.id.iv_massager_fuzai);
		ivStartOrStop = (ImageView) findViewById(R.id.iv_massager_start_or_stop);
		tvTime = (TextView) findViewById(R.id.tv_massage_time);
		llBleStatus = (LinearLayout) findViewById(R.id.ll_ble_status);
		ivBleStatus = (ImageView) findViewById(R.id.iv_ble_status);
		ivLoading = (ImageView) findViewById(R.id.iv_loading);
		tvBleStatus = (TextView) findViewById(R.id.tv_ble_status);
		// 设置监听
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.iv_back:
					finish();
					break;
				case R.id.iv_history:
					startActivity(new Intent(MassagerActivity.this,
							HistoryActivity.class));
					break;
				case R.id.iv_massager_start_or_stop:
//					doStartOrStop(blueService != null ? blueService
//							.getMassagerInfo() : null);
					doStartOrStop(BaseApp.info);
					break;
				case R.id.ll_ble_status:
					showBleDeviceDialog();
					break;

				default:
					break;
				}
			}

		};
		ivBack.setOnClickListener(listener);
		ivHistory.setOnClickListener(listener);
		ivStartOrStop.setOnClickListener(listener);
		llBleStatus.setOnClickListener(listener);
		ccPower.setOnTimePointChangeListener(new OnTimePointChangeListener() {
			@Override
			public void onChanging(int progress) {
				ivStartOrStop.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onChanged(int progress) {
				ivStartOrStop.setVisibility(View.VISIBLE);
				if (isLoading) {
					ToastUtil.showCustomToast(MassagerActivity.this, "正在加载...");
					return;
				}
				startLoading();
			}
		});

	}

	private void startAnimation(View v) {
		oa = ObjectAnimator.ofFloat(v, "rotation", 0, 360f);
		oa.setDuration(1500);
		oa.setRepeatCount(-1);
		oa.setInterpolator(new LinearInterpolator());
		oa.start();
	}

	private void stopAnimation() {
		if (oa != null) {
			oa.cancel();
		}
	}

	private void updateStartOrStop(boolean start) {
		if (start) {
			ivStartOrStop.setImageResource(R.drawable.ic_stop_pressed);
		} else {
			ivStartOrStop.setImageResource(R.drawable.ic_start);
		}
	}

	private void doStartOrStop(MycjMassagerInfo info) {
		if (isLoading) {
			ToastUtil.showCustomToast(this, "正在加载...");
			return;
		}
		if (null != info && info.getOpen() == 1 && info.getPattern() == pattern) {
			// start = info.getOpen()==1?true:false;
			// load = info.getLoader()==1?true:false;
			// time = info.getLeftTime();
			// power = info.getPower();
			if (blueService != null && blueService.ifAllConnected()) {
				blueService.stopMassager();
			}
		} else {
			if (blueService != null && blueService.ifAllConnected()) {
				int open = 1;
				int leftTime = Constant.DEFAULT_TIME;
				int settingTime = Constant.DEFAULT_TIME;
				int power = Constant.DEFAULT_POWER;
				int temperature = 0;
				int tempUnit = 0;
				int loader = 0;
				int hr = 0;
				info = new MycjMassagerInfo(open, pattern, power, leftTime,
						settingTime, temperature, tempUnit, loader, hr);
				blueService.startMassager(info);
			}
			startLoading();
			// updateUi(info);
		}
	}

	private void stopLoading() {
		stopAnimation();
		ivLoading.setVisibility(View.GONE);
		XLog.e("MassagerActivity", "开始/结束 5秒后~");
		isLoading = false;
		ccPower.setIsCanTouch(true); 
	}

	private void startLoading() {
		isLoading = true;
		startAnimation(ivLoading);
		ivLoading.setVisibility(View.VISIBLE);
		ccPower.setIsCanTouch(false);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				stopLoading();
				// int open = 1;
				// int leftTime = Constant.DEFAULT_TIME;
				// int settingTime = Constant.DEFAULT_TIME;
				// int power = Constant.DEFAULT_POWER;
				// int temperature = 0;
				// int tempUnit = 0;
				// int loader = 0;
				// int hr = 0;
				// MycjMassagerInfo info = new MycjMassagerInfo(open, pattern,
				// power, leftTime, settingTime, temperature, tempUnit, loader,
				// hr);
				// blueService.startMassager(info);
				// ProtocolManager.newInstance(MassagerActivity.this).sendBroadcastForCurrentMassagerInfo(info);;
			}
		}, Constant.DEFAULT_LOADING_TIME);
	}

	private void showBleDeviceDialog() {
		chooseBlueADialog = new DeviceDialog(MassagerActivity.this)
				.builder(adapter)
				.setOnLeftClickListener(getString(R.string.back),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								chooseBlueADialog.dismiss();
							}
						})
				.setOnRightClickListener(getString(R.string.refresh),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								devices.clear();
								adapter.notifyDataSetChanged();
								blueService.startScan();
							}
						})
				.setOnButtonClickListener(new OnButtonClickListener() {
					@Override
					public void onListViewSelected(View v, int position) {
						BluetoothDevice device = devices.get(position);
						String address = device.getAddress();
						/*
						 * if (checkBlueIsAdd(address)) {
						 * Toast.makeText(getApplicationContext(),
						 * getString(R.string.device_added),
						 * Toast.LENGTH_SHORT).show(); return; }
						 */

						// tvBleA.setText(address);

						XLog.e("设置A的 address :" + address);
						BondedDeviceUtil
								.save(1, address, MassagerActivity.this);
						blueService.connect(device);

					}
				});
		chooseBlueADialog.show();
	}

	private Handler mHandler = new Handler() {

	};
	private ProtocolBroadcastReceiver receiverData = new ProtocolBroadcastReceiver() {

		@Override
		protected void onChangeTime(final int time, int timeSetting) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		protected void onChangeTemperature(final int temp, int tempUnit) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		protected void onChangePower(final int power) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {

				}

			});
		}

		@Override
		protected void onChangePattern(final int pattern) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		protected void onChangeMassagerInfo(final MycjMassagerInfo info) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {

					updateUi(info);

				}
			});

		}

		@Override
		protected void onChangeLoader(int loader) {
			// TODO Auto-generated method stub
			super.onChangeLoader(loader);
		}

		@Override
		protected void onChangeHeartRate(int heartRate) {
			// TODO Auto-generated method stub
			super.onChangeHeartRate(heartRate);
		}

		@Override
		protected void onChangeTimeCallBack(final int stauts, final int time) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (stauts == 1) {
						showCustomToast("改变时间成功");

					} else {
						showCustomToast("改变时间失败");
					}

				}
			});

		}

		@Override
		protected void onChangeTempeatureCallBack(final int stauts,
				final int temp, int tempUnit) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (stauts == 1) {
						showCustomToast("改变温度成功");

					} else {
						showCustomToast("改变温度失败");
					}

				}
			});
		}

		@Override
		protected void onChangePowerCallBack(final int stauts, final int power) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (stauts == 1) {
						showCustomToast("改变力度成功");

					} else {
						showCustomToast("改变力度失败");
					}

				}
			});
		}

		@Override
		protected void onChangePatternCallBack(final int stauts,
				final int pattern) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (stauts == 1) {
						showCustomToast("改变模式成功");

					} else {
						showCustomToast("改变模式失败");
					}

				}
			});
		}

	};
	private BroadcastReceiver receiverBle = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals(LaputaBroadcast.ACTION_LAPUTA_DEVICE_FOUND)) {
				Log.e("", "__________- --___------__________-");
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						BluetoothDevice device = intent
								.getParcelableExtra(LaputaBroadcast.EXTRA_LAPUTA_DEVICE);
						if (device != null && !devices.contains(device)) {
							devices.add(device);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} else if (action.equals(LaputaBroadcast.ACTION_LAPUTA_STATE)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						int state = intent.getExtras().getInt(
								LaputaBroadcast.EXTRA_LAPUTA_STATE);
						if (state == SimpleLaputaBlue.STATE_SERVICE_DISCOVERED) {
							// startActivity(new
							// Intent(DeviceActivity.this,MainActivity.class));
							// overridePendingTransition(R.anim.activity_from_left_to_right_enter,
							// R.anim.activity_from_left_to_right_exit);
							// showCustomToast("连接成功!");

							updateBleStatus(true);
						} else {
							updateBleStatus(false);
						}
					}

				});
			}
		}
	};
	
	
	private void registerReceiver() {
		registerReceiver(receiverBle, LaputaBroadcast.getIntentFilter());
		registerReceiver(receiverData, ProtocolBroadcast.getIntentFilter());
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiverBle);
		unregisterReceiver(receiverData);
	}

	private boolean isCanSweep = false;
	private BlueService blueService;
	private TextView tvTitle;
	private ImageView ivBack;
	private ImageView ivHistory;
	private ColorCircleView ccPower;
	private ImageView ivFuzai;
	private ImageView ivStartOrStop;
	private TextView tvTime;
	private LinearLayout llBleStatus;
	private ImageView ivBleStatus;
	private TextView tvBleStatus;
	private ObjectAnimator oa;
}
