package com.example.scubclient;

import java.lang.reflect.Field;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends TabActivity {

	private Button mButton = null;
	private int mManage=0;
	private int mtype=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); //窗口去掉标题
		final TabHost tabHost = getTabHost();
		final TabWidget tabWidget=tabHost.getTabWidget();
		Field mBottomLeftStrip;
		Field mBottomRightStrip;
		Intent intent=getIntent();
		Bundle b=intent.getExtras();
		mManage=b.getInt("manage");
		mtype=b.getInt("type");
		Bundle bundle=new Bundle();
		bundle.putInt("manage", mManage);
		Intent homeintent=new Intent(this,HomeActivity.class);
		Intent jwcintent=new Intent(this,JwcActivity.class);
		jwcintent.putExtras(bundle);
		Intent jzintent=new Intent(this,JzActivity.class);
		jzintent.putExtras(bundle);
		Intent qgintent=new Intent(this,QgActivity.class);
		qgintent.putExtras(bundle);
		Intent lfintent=new Intent(this,JwcActivity.class);
		lfintent.putExtras(bundle);
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("首页",this.getResources().getDrawable(R.drawable.a)).setContent(homeintent));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("教务处",this.getResources().getDrawable(R.drawable.a)).setContent(jwcintent));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("讲座",this.getResources().getDrawable(R.drawable.a)).setContent(jzintent));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("青广",this.getResources().getDrawable(R.drawable.a)).setContent(qgintent));
		tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("失物招领",this.getResources().getDrawable(R.drawable.a)).setContent(lfintent));
		for(int i=0;i<tabWidget.getChildCount();i++){
			tabWidget.getChildAt(i).getLayoutParams().height=48;
			final TextView tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
			if(Float.valueOf(Build.VERSION.RELEASE)<=2.1){
				try{
					mBottomLeftStrip=tabWidget.getClass().getDeclaredField("mBottomLeftStrip");
					mBottomRightStrip=tabWidget.getClass().getDeclaredField("mBottomRightStrip");
					if(!mBottomLeftStrip.isAccessible()){
						mBottomLeftStrip.setAccessible(true);
					}
					if(!mBottomRightStrip.isAccessible()){
						mBottomRightStrip.setAccessible(true);
					}
					mBottomLeftStrip.set(tabWidget, getResources().getDrawable(R.drawable.a));  //修改a////////////
					mBottomRightStrip.set(tabWidget, getResources().getDrawable(R.drawable.b));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			View vw=tabWidget.getChildAt(i);
			if(tabHost.getCurrentTab()==i){
				vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.f));///////////
			}else{
				vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.e));///////////
			}
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				for(int i=0;i<tabWidget.getChildCount();i++){
					View vw=tabWidget.getChildAt(i);
					if(tabHost.getCurrentTab()==i){
						vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.f));//////////
					}else{
						vw.setBackgroundDrawable(getResources().getDrawable(R.drawable.e));//////////
					}
				}
			}
			
		});
		tabHost.setBackgroundColor(Color.rgb(196,223,250)); //196,223,250  33,143,228
		if(mtype==5){
			Intent infointent=new Intent(MainActivity.this,InfoContextActivity.class);
			Bundle bundle1=new Bundle();
			bundle1.putBoolean("jwc", false);
			bundle1.putInt("manage", 1);
			bundle1.putInt("type", 5);
			infointent.putExtras(bundle1);
			startActivity(infointent);
		}
		tabHost.setCurrentTab(1);
		setContentView(tabHost);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
