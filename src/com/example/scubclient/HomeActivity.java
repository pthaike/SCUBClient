package com.example.scubclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity{

	private Button button=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		button=(Button)findViewById(R.id.Button01);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle=new Bundle();
				Intent intent=new Intent();
				bundle.putInt("manage", 0);
				bundle.putInt("type", 1);
				intent.putExtras(bundle);
				intent.setClass(HomeActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
