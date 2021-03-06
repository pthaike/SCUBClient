package com.example.scubclient;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends TabActivity {

	private Button mButton = null;
	private int mManage=0;
	private int mtype=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApp.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //窗口去掉标题
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		final TabHost tabHost = getTabHost();
		final TabWidget tabWidget=tabHost.getTabWidget();
		Field mBottomLeftStrip;
		Field mBottomRightStrip;
		Intent homeintent=new Intent(this,Introduction.class);
		Intent jwcintent=new Intent(this,JwcActivity.class);
		Intent jzintent=new Intent(this,JzActivity.class);
		Intent qgintent=new Intent(this,QgActivity.class);
		Intent lfintent=new Intent(this,LfActivity.class);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("").setContent(homeintent));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("").setContent(qgintent));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("").setContent(jwcintent));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("").setContent(jzintent));
		tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("").setContent(lfintent));
		for(int i=0;i<tabWidget.getChildCount();i++){
			tabWidget.getChildAt(i).getLayoutParams().height=80;
			final TextView tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
			final String VERSION = Build.VERSION.RELEASE.substring(0, 3);
            Log.d("debug", "version is "+VERSION);
			//if(Float.valueOf(Build.VERSION.RELEASE)<=2.1){
				if(Float.valueOf(VERSION)<=2.1){
				try{
					mBottomLeftStrip=tabWidget.getClass().getDeclaredField("mBottomLeftStrip");
					mBottomRightStrip=tabWidget.getClass().getDeclaredField("mBottomRightStrip");
					if(!mBottomLeftStrip.isAccessible()){
						mBottomLeftStrip.setAccessible(true);
					}
					if(!mBottomRightStrip.isAccessible()){
						mBottomRightStrip.setAccessible(true);
					}
					mBottomLeftStrip.set(tabWidget, getResources().getDrawable(R.drawable.home));  //修改a////////////
					mBottomRightStrip.set(tabWidget, getResources().getDrawable(R.drawable.qingguang));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				
			}
			View vw=tabWidget.getChildAt(i);
			if(tabHost.getCurrentTab()==i){
				vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.home));///////////
			}else{
				switch(i){
				case 0:
					vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.home));
					break;
				case 1:
					vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.qingguang));
					break;
				case 2:
					vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.jiao));
					break;
				case 3:
					vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.jiangzuo));
					break;
				case 4:
					vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.lf));
					break;
				}
				//vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.lf));///////////
			}
		}
		/*tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				for(int i=0;i<tabWidget.getChildCount();i++){
					View vw=tabWidget.getChildAt(i);
					if(tabHost.getCurrentTab()==i){
						vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.select));//////////
					}else{
						vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.select));//////////
					}
				}
			}
			
		});*/
		//tabHost.setBackgroundResource(R.drawable.jzback);
		tabHost.setBackgroundColor(Color.argb(0,0, 255, 255)); //196,223,250  33,143,228
		tabHost.setCurrentTab(0);
		setContentView(tabHost);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
