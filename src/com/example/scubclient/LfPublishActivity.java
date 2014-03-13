package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LfPublishActivity extends Activity{

	private ImageButton submitbt=null;
	private ImageButton exitbt=null;
	private EditText stdnumEdit=null;
	private EditText stdpswdEdit=null;
	private EditText desEdit=null;
	private EditText contextEdit=null;
	private CheckBox lfcheckBox=null;
	private String mstdnum=null;
	private String mstdpswd=null;
	private String mdes=null;
	private String mcontent=null;
	private Connector connector=null;
	private CheckStudent checkStudent;
	private int lftype;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.lfpublish);
		ExitApp.getInstance().addActivity(this);
		
		submitbt=(ImageButton)findViewById(R.id.lfpublishbtn);
		exitbt=(ImageButton)findViewById(R.id.lfexitbtn);
		stdnumEdit=(EditText)findViewById(R.id.lfnum);
		stdpswdEdit=(EditText)findViewById(R.id.lfpswd);
		desEdit=(EditText)findViewById(R.id.lfname);
		contextEdit=(EditText)findViewById(R.id.lfcontext);
		
		submitbt.setOnClickListener(new SubmitOnClickListener());
		exitbt.setOnClickListener(new ExitOnClickListener());
	}

	private class SubmitOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			new MyThread().start();
		}
		
	}
	
	private class MyThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mstdnum=stdnumEdit.getText().toString().trim();
			mstdpswd=stdpswdEdit.getText().toString().trim();
			mdes=desEdit.getText().toString().trim();
			mcontent=contextEdit.getText().toString().trim();
			lfcheckBox=(CheckBox)findViewById(R.id.checklf);
			if(lfcheckBox.isChecked()){
				lftype=0;
			}else{
				lftype=1;
			}
			if(mstdnum.equals("")||mstdpswd.equals("")||mdes.equals("")||mcontent.equals("")){
				myHandler.sendEmptyMessage(1);
				return;
			}
			myHandler.sendEmptyMessage(3);
			checkStudent=new CheckStudent();
			if(!checkStudent.Check(mstdnum, mstdpswd)){
				myHandler.sendEmptyMessage(2);
				return;
			}else{
				try{
					if(connector==null){
						connector=new Connector();
					}
					connector.ConnectServer(SERVER_ADRESS, SERVER_PORT);
					boolean contrue=connector.ConnectServer(SERVER_ADRESS,SERVER_PORT);
					if(!contrue||!connector.socket.isConnected()||connector.socket.isClosed()){
						myHandler.sendEmptyMessage(4);
						return;
					}
					String msg="<#STORE_LF#>"+lftype+"|"+mstdnum+"|"+mdes+"|"+mcontent;
					connector.out.writeUTF(msg);
					String reply=connector.in.readUTF();
					if(reply.equals("<#STORE_SUCCESE#>")){
						myHandler.sendEmptyMessage(0);
						finish();
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			super.run();
		}
		
	}
	
	private class ExitOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
	
	MyHandler myHandler =new MyHandler();
	
	private class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				Toast.makeText(LfPublishActivity.this, "发布成功", Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(LfPublishActivity.this, "填完整信息", Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(LfPublishActivity.this, "学号或密码出错", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(LfPublishActivity.this, "已提交,请稍等", Toast.LENGTH_LONG).show();
				break;
			case 4:
				Toast.makeText(LfPublishActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
}
