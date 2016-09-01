package com.mycj.massager_191.base;




import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.dialog.AbstractLaputaDialog;
import com.laputa.dialog.LaputaAlertDialog;
import com.mycj.massager_191.R;
import com.mycj.massager_191.service.BlueService;
import com.mycj.massager_191.util.ToastUtil;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;




/**
 * Created by Administrator on 2015/11/20.
 */
public class BaseActivity extends FragmentActivity {
	
	  /** 显示自定义Toast提示(来自String) **/
    protected void showCustomToast(String text) {
     /*   View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
                R.layout.common_toast, null);
        ((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
        Toast toast = new Toast(BaseActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();*/
    	ToastUtil.showCustomToast(this	, text);
//		ToastUtil.showToast(this, text);
    }
    
    public BlueService getBlueService(){
    	BaseApp application = (BaseApp) getApplication();
    	return application.getXBlueService();
    }
    
	public AbstractLaputaDialog showAlertDialog(String msg){
		AbstractLaputaDialog dialog = new LaputaAlertDialog(this, R.layout.view_laputa_alert_dialog)
    	.builder()
    	.setCancelable(true)
//    	.setCanceledOnTouchOutside(true)
    	.setMsg(msg)
    	;
		dialog.show();
		return dialog;
	}
	
    public boolean isBleEnable(){
    	boolean checkFeature = AbstractSimpleLaputaBlue.checkFeature(this);
    	boolean enable = AbstractSimpleLaputaBlue.isEnable(this);
    	return checkFeature && enable;
    }
}
