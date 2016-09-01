package com.mycj.massager_191;

import com.mycj.massager_191.util.DisplayUtil;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private boolean isScaling = false;
	private int pattern;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}
	
	private void initViews() {
		RelativeLayout rlPattern_01 = (RelativeLayout) findViewById(R.id.rl_pattern_01);
		RelativeLayout rlPattern_02 = (RelativeLayout) findViewById(R.id.rl_pattern_02);
		RelativeLayout rlPattern_03 = (RelativeLayout) findViewById(R.id.rl_pattern_03);
		RelativeLayout rlPattern_04 = (RelativeLayout) findViewById(R.id.rl_pattern_04);
		RelativeLayout rlPattern_05 = (RelativeLayout) findViewById(R.id.rl_pattern_05);
		RelativeLayout rlPattern_06 = (RelativeLayout) findViewById(R.id.rl_pattern_06);
		RelativeLayout rlPattern_07 = (RelativeLayout) findViewById(R.id.rl_pattern_07);
		RelativeLayout rlPattern_08 = (RelativeLayout) findViewById(R.id.rl_pattern_08);
		RelativeLayout rlPattern_09 = (RelativeLayout) findViewById(R.id.rl_pattern_09);
		RelativeLayout rlPattern_10 = (RelativeLayout) findViewById(R.id.rl_pattern_10);
		RelativeLayout rlPattern_11 = (RelativeLayout) findViewById(R.id.rl_pattern_11);
		RelativeLayout rlPattern_12 = (RelativeLayout) findViewById(R.id.rl_pattern_12);
		OnClickListener patternListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isScaling) {
					startAnimation(v);
				}
			}
		};
		rlPattern_01.setOnClickListener(patternListener);
		rlPattern_02.setOnClickListener(patternListener);
		rlPattern_03.setOnClickListener(patternListener);
		rlPattern_04.setOnClickListener(patternListener);
		rlPattern_05.setOnClickListener(patternListener);
		rlPattern_06.setOnClickListener(patternListener);
		rlPattern_07.setOnClickListener(patternListener);
		rlPattern_08.setOnClickListener(patternListener);
		rlPattern_09.setOnClickListener(patternListener);
		rlPattern_10.setOnClickListener(patternListener);
		rlPattern_11.setOnClickListener(patternListener);
		rlPattern_12.setOnClickListener(patternListener);
	}
	
	
	private void startAnimation(View v){
		pattern = 0;
		switch (v.getId()) {
		case R.id.rl_pattern_01:
			pattern = 1;
			break;
		case R.id.rl_pattern_02:
			pattern = 2;
			break;
		case R.id.rl_pattern_03:
			pattern = 3;
			break;
		case R.id.rl_pattern_04:
			pattern = 4;
			break;
		case R.id.rl_pattern_05:
			pattern = 5;
			break;
		case R.id.rl_pattern_06:
			pattern = 6;
			break;
		case R.id.rl_pattern_07:
			pattern = 7;
			break;
		case R.id.rl_pattern_08:
			pattern = 8;
			break;
		case R.id.rl_pattern_09:
			pattern = 9;
			break;
		case R.id.rl_pattern_10:
			pattern = 10;
			break;
		case R.id.rl_pattern_11:
			pattern = 11;
			break;
		case R.id.rl_pattern_12:
			pattern = 12;
			break;
		}
		isScaling = true;
		ObjectAnimator oaX = ObjectAnimator.ofFloat(v, "scaleX", 1f,0.8f,1f);
		ObjectAnimator oaY = ObjectAnimator.ofFloat(v, "scaleY", 1f,0.8f,1f);
		oaX.setDuration(500);
		oaX.setInterpolator(new OvershootInterpolator());
		oaY.setDuration(500);
		oaY.setInterpolator(new OvershootInterpolator());
		AnimatorSet set = new AnimatorSet();
		set.playTogether(oaX,oaY);
		set.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				isScaling = false;
				Intent intent = new Intent(MainActivity.this,MassagerActivity.class);
				Bundle b = new Bundle();
				b.putInt("pattern", pattern);
				intent.putExtras(b);
				startActivity(intent);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				isScaling = false;
			}
		});
		set.start();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
//		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//		Log.e("", "displayMetrics:" + displayMetrics.densityDpi);
//		int px2dip1 = DisplayUtil.px2dip(this, 162 *2 * 1.0f / 3);
//		int px2dip2 = DisplayUtil.px2dip(this, 186 *2 * 1.0f / 3);
//		int px2dip3 = DisplayUtil.px2dip(this, 127 *2 * 1.0f / 3);
//		int px2dip4 = DisplayUtil.px2dip(this, 288 *2 * 1.0f / 3);
//		Log.e("", "px2dip:" + px2dip1);
//		Log.e("", "px2dip:" + px2dip2);
//		Log.e("", "px2dip:" + px2dip3);
//		Log.e("", "px2dip:" + px2dip4);
	}
}
