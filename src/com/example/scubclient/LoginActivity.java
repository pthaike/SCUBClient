package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends Activity{

	private EditText nameEdit=null;
	private EditText pswdEdit=null;
	private Button btnLogin=null;
	private ImageButton lgbackImageButton=null;
	private CheckBox loginRemember=null;
	private ProgressDialog pd=null;
	private Connector connector=null;
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
	private String username=null;
	private String userpswd=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ExitApp.getInstance().addActivity(this);
		btnLogin=(Button) findViewById(R.id.btnLogin);
		lgbackImageButton=(ImageButton)findViewById(R.id.lgbackbt);
		checkIfRemember();
		btnLogin.setOnClickListener(new loginOnclickListener());
		lgbackImageButton.setOnClickListener(new backOnclickListener());
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
	
	private class backOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
	
	private class loginOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			nameEdit=(EditText)findViewById(R.id.etname);
			pswdEdit=(EditText)findViewById(R.id.etpswd);
			username=nameEdit.getEditableText().toString().trim(); //获取用户名
			userpswd=pswdEdit.getEditableText().toString().trim(); //获取密码
			if(username.equals("")||userpswd.equals("")){ //判断是否为空
				Toast.makeText(LoginActivity.this, "请输入帐号或密码!", Toast.LENGTH_SHORT).show(); //提示用户输入
				return;
			}else{
				pd=ProgressDialog.show(LoginActivity.this, "请稍后", "正在连接服务器.....",true,true);
				Login();
			}
		}
		
	}
	
	//方法：将用户的id和密码存入Preferences
    public void rememberMe(String uid,String pwd){
    	sharedPrefenrence = getPreferences(MODE_PRIVATE);	//获得Preferences
    	SharedPreferences.Editor editor = sharedPrefenrence.edit();			//获得Editor
    	editor.putString("username", uid);							//将用户名存入Preferences
    	editor.putString("pswd", pwd);							//将密码存入Preferences
    	editor.commit();
    }
    
    //方法：从Preferences中读取用户名和密码
    public void checkIfRemember(){
    	sharedPrefenrence = getPreferences(MODE_PRIVATE);	//获得Preferences
    	String uid = sharedPrefenrence.getString("username", null);
    	String pwd = sharedPrefenrence.getString("pswd", null);
    	if(uid != null && pwd!= null){
    		nameEdit=(EditText)findViewById(R.id.etname);
			pswdEdit=(EditText)findViewById(R.id.etpswd);
    		loginRemember = (CheckBox)findViewById(R.id.loginRemember);
    		nameEdit.setText(uid);
    		pswdEdit.setText(pwd);
    		loginRemember.setChecked(true);
    	}
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
						connector=new Connector();  //连接服务器
					}
					connector.ConnectServer(SERVER_ADRESS, SERVER_PORT);
					String msg="<#LOGIN_INFO#>"+username+"|"+userpswd; //组织传输信息
					connector.out.writeUTF(msg);  //发送信息
					String reply=connector.in.readUTF();  //读取用户反馈信息
					pd.dismiss();  //取消进度条
					connector.ExitConnect();
					if(reply.startsWith("<#MANAGE_TRUE#>")){
						//登录成功
						Intent intent=new Intent();
						String mid=reply.substring(15);
						sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
						editor=sharedPrefenrence.edit();
						editor.putInt("manage", 1);
						editor.putString("mid", mid);
						editor.commit();
						loginRemember = (CheckBox)findViewById(R.id.loginRemember);		//获得CheckBox对象
						if(loginRemember.isChecked()){
							rememberMe(username,userpswd);
						}
						Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show(); //提示用户输入
						intent.setClass(LoginActivity.this,MainActivity.class);
						startActivity(intent);
							
					}else if(reply.equals("<#MANAGE_FALSE#>")){
						Toast.makeText(LoginActivity.this, "登录失败,用户名或密码出错", Toast.LENGTH_LONG).show();
						Looper.loop();
						Looper.myLooper().quit();
					}
					//}
				}catch (Exception e){
					e.printStackTrace();
				}
				super.run();
			}
			
		}.start();
	}

}
