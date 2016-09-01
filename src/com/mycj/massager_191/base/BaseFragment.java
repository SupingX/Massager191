package com.mycj.massager_191.base;


import com.mycj.massager_191.service.BlueService;
import com.mycj.massager_191.util.ToastUtil;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class BaseFragment extends Fragment {
	public void show(String msg){
	/*     View toastRoot = LayoutInflater.from(getActivity()).inflate(
	                R.layout.common_toast, null);
	        ((TextView) toastRoot.findViewById(R.id.toast_text)).setText(msg);
	        Toast toast = new Toast(getActivity());
	        toast.setGravity(Gravity.CENTER, 0, 0);
	        toast.setDuration(Toast.LENGTH_SHORT);
	        toast.setView(toastRoot);
	        toast.show();*/
		
		ToastUtil.showCustomToast(getActivity()	, msg);
//		ToastUtil.showToast(getActivity(), msg);
//		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	public BlueService getBlueService(){
		BaseApp app = (BaseApp) getActivity().getApplication();
		return app.getXBlueService();
	}
	

}
