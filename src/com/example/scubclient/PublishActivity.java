package com.example.scubclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;

public class PublishActivity extends Activity{

	private EditText titleEdit=null;
	private EditText contextEdit=null;
	private Button submitbtn=null;
	private Connector connector=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.pubishinfo);
		super.onCreate(savedInstanceState);
		titleEdit=(EditText)findViewById(R.id.infotitle);
		contextEdit=(EditText)findViewById(R.id.infocontext);
		submitbtn=(Button)findViewById(R.id.publishbtn);
		submitbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try{
							String title=titleEdit.getEditableText().toString().trim();
							String context=contextEdit.getEditableText().toString().trim();
							System.out.println("跟踪---->>>>"+title);
							if(connector==null){
								connector=new Connector(SERVER_ADRESS,SERVER_PORT);
							}
							String msg="<#STORE_QG#>";
							msg=msg+1+"|"+title+"|"+context+"|"+"";   /////////////1wei管理员id
							connector.out.writeUTF(msg);
							String reply=connector.in.readUTF();
							System.out.println("跟踪---》"+reply);
							if(reply.startsWith("<#STORE_SUCCESE#>")){
								Toast.makeText(PublishActivity.this, "信息发布成功", Toast.LENGTH_LONG).show();
								finish();
							}else{
								Toast.makeText(PublishActivity.this, "信息发布失败", Toast.LENGTH_LONG).show();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						super.run();
					}
					
				}.start();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(connector!=null){
			connector.ExitConnect();
		}
		super.onDestroy();
	}
}
