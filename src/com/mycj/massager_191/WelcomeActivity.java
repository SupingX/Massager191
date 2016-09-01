package com.mycj.massager_191;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.mycj.massager_191.util.DisplayUtil;

public class WelcomeActivity extends Activity {
	private TextView tvMassage;
	private TextView tvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		tvMassage = (TextView) findViewById(R.id.tv_massage);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText(getVersion());
		startAnimator();
		
	}
	
	  public String getVersion() {
	      try {
	         PackageManager manager = this.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
	         return version;
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	      return "";
	 }
	  
	private void startAnimator() {
		Point screenMetrics = DisplayUtil.getScreenMetrics(this);
		ObjectAnimator animator0 = ObjectAnimator.ofFloat(tvMassage, "translationY", screenMetrics.y,screenMetrics.y/2);
		animator0.setDuration(1000);
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvMassage, "alpha", 0f,1.0f);
		animator1.setDuration(2000);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(tvMassage, "scaleX", 1.0f,1.5f);
		animator2.setDuration(2000);
		animator2.setInterpolator(new OvershootInterpolator());
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(tvMassage, "scaleY", 1.0f,1.5f);
		animator3.setDuration(2000);
		animator3.setInterpolator(new OvershootInterpolator());
		AnimatorSet set1 = new AnimatorSet();
		
//		set1.play(animator0)
//		.before(animator2).with(animator3);
		set1.play(animator2).with(animator3);
		
		AnimatorSet set2 = new AnimatorSet();
		
		set2.play(animator0).with(animator1).before(set1);
		set2.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				finish();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
//				startActivity(new Intent(SpalishActivity.this,MainActivity.class));
//				finish();
			}
		});
		set2.start();
		
	}
	
}
