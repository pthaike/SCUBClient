package com.example.scubclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends Activity{

	private Button mbutton=null;
	private Button ubutton=null;
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		ExitApp.getInstance().addActivity(this);
		mbutton=(Button)findViewById(R.id.mButton);
		ubutton=(Button)findViewById(R.id.uButton);
		
		mbutton.setOnClickListener(new manageListener());
		ubutton.setOnClickListener(new userlistener());
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	 * 用户按钮监听器
	 * @author lenovo
	 *
	 */
	private class userlistener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//保存用户信息，0表示普通用户，1表示管理员
			sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
			editor=sharedPrefenrence.edit();
			editor.putInt("manage", 0);
			editor.commit();
			
			Intent intent=new Intent();
			intent.setClass(HomeActivity.this, MainActivity.class);
			startActivity(intent);
		}
		
	}

	/**
	 * 管理员按钮监听器
	 * @author lenovo
	 *
	 */
	private class manageListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(HomeActivity.this, LoginActivity.class);
			startActivity(intent);
		}
		
	}
}
