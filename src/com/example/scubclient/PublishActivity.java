package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class PublishActivity extends Activity{

	private EditText titleEdit=null;
	private EditText contextEdit=null;
	private ImageButton submitbtn=null;
	private ImageButton datebt=null;
	private ImageButton timebt=null;
	private TextView dateText=null;
	private TextView timeText=null;
	private Connector connector=null;
	private ImageButton exitbt=null;
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
	private int mtype;
	private String mdate=null;
	private String mtime=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.pubishinfo);
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		ExitApp.getInstance().addActivity(this);
		
		titleEdit=(EditText)findViewById(R.id.infotitle);
		contextEdit=(EditText)findViewById(R.id.infocontext);
		submitbtn=(ImageButton)findViewById(R.id.publishbtn);
		exitbt=(ImageButton)findViewById(R.id.exitbtn);
		datebt=(ImageButton)findViewById(R.id.qgdatebtn);
		timebt=(ImageButton)findViewById(R.id.qgtimebtn);
		dateText=(TextView)findViewById(R.id.qgdate);
		timeText=(TextView)findViewById(R.id.qgtime);
		
		sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);  ///
		mtype=sharedPrefenrence.getInt("type", 1);
		
		submitbtn.setOnClickListener(new MyOnclikListener());
		exitbt.setOnClickListener(new ExitClickListener());
		datebt.setOnClickListener(new DateClickListener());
		timebt.setOnClickListener(new TimeClickListener());
	}
	
	private class ExitClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}

	private class DateClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			java.util.Date now = new java.util.Date();
			//DatePickerDialog datePicker=new DatePickerDialog(PublishActivity.this,mtype, new MyonDateSetListner(), 2013, 10, 11);
			DatePickerDialog datePicker=new DatePickerDialog(PublishActivity.this,mtype, new MyonDateSetListner(), now.getYear(), now.getMonth(), now.getDay());
			datePicker.show();
		}
		
	}
	
	private class MyonDateSetListner implements OnDateSetListener{

		@Override
		public void onDateSet(DatePicker View, int year, int month, int day) {
			// TODO Auto-generated method stub
			mdate=year+"-"+month+"-"+day;
			dateText.setText(mdate);
		}
		
	}
	
	private class TimeClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			java.util.Date now = new java.util.Date();
			TimePickerDialog time=new TimePickerDialog(PublishActivity.this,new MyOnTimeSetListenr(),now.getHours(),now.getMinutes(),true);
			time.show();
		}
		
	}
	
	private class MyOnTimeSetListenr implements OnTimeSetListener{

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mtime=hourOfDay+":"+minute+"";
			timeText.setText(mtime);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(connector!=null){
			connector.ExitConnect();
		}
		super.onDestroy();
	}
	
	private class MyOnclikListener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						String title=titleEdit.getEditableText().toString().trim();
						String context=contextEdit.getEditableText().toString().trim();
						if(connector==null){
							connector=new Connector();
							System.out.println("track0---》");
						}
						if(title.equals("")||context.equals("")||mtime==null||mdate==null){
							myhandler.sendEmptyMessage(1);
							return;
						}
						System.out.println("track1---》");
						connector.ConnectServer(SERVER_ADRESS,SERVER_PORT);
						String msg="<#STORE_QG#>";
						msg=msg+1+"|"+title+"|"+context+"|"+mdate+" "+mtime;   
						connector.out.writeUTF(msg);
						String reply=connector.in.readUTF();
						myhandler.sendEmptyMessage(3);
						connector.ExitConnect();
						if(reply.startsWith("<#STORE_SUCCESE#>")){
							sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
							editor=sharedPrefenrence.edit();
							editor.putBoolean("public", true);
							editor.commit();
							myhandler.sendEmptyMessage(0);
							finish();
						}else{
							myhandler.sendEmptyMessage(3);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					super.run();
				}
				
			}.start();
		}
	}
	
	MyHandler myhandler=new MyHandler();
	private class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				Toast.makeText(PublishActivity.this, "信息发布成功", Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(PublishActivity.this, "请填完整数据", Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(PublishActivity.this, "信息发布失败", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(PublishActivity.this, "已提交", Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	}
}
