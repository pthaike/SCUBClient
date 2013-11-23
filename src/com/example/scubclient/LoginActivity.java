package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{

	private EditText nameEdit=null;
	private EditText pswdEdit=null;
	private Button btnLogin=null;
	private ProgressDialog pd=null;
	private Connector connector=null;
	private int mtype;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		mtype=bundle.getInt("type");
		btnLogin=(Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pd=ProgressDialog.show(LoginActivity.this, "请稍后", "正在连接服务器.....",true,true);
				Login();
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

	public LoginActivity() {
		// TODO Auto-generated constructor stub
	}
	
	//用户登录
	private void Login(){
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				try{
					if(connector==null){
						connector=new Connector(SERVER_ADRESS,SERVER_PORT);  //连接服务器
					}
					nameEdit=(EditText)findViewById(R.id.etname);
					pswdEdit=(EditText)findViewById(R.id.etpswd);
					String username=nameEdit.getEditableText().toString().trim(); //获取用户名
					String userpswd=pswdEdit.getEditableText().toString().trim(); //获取密码
					if(username.equals("")||userpswd.equals("")){ //判断是否为空
						Toast.makeText(LoginActivity.this, "请输入帐号或密码!", Toast.LENGTH_SHORT).show(); //提示用户输入
						return;
					}else{
						String msg="<#LOGIN_INFO#>"+username+"|"+userpswd; //组织传输信息
						connector.out.writeUTF(msg);  //发送信息
						String reply=connector.in.readUTF();  //读取用户反馈信息
						pd.dismiss();  //取消进度条
						if(reply.equals("<#MANAGE_TRUE#>")){
							//登录成功
							Intent intent=new Intent();
							Bundle bundle=new Bundle();
							bundle.putInt("manage", 1);
							bundle.putInt("type", mtype);
							intent.putExtras(bundle);
							intent.setClass(LoginActivity.this,MainActivity.class);
							startActivity(intent);
							
						}else if(reply.equals("<#MANAGE_FALSE#>")){
							Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
							Looper.loop();
							Looper.myLooper().quit();
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				super.run();
			}
			
		}.start();
	}

}
