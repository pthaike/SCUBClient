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
			username=nameEdit.getEditableText().toString().trim(); //��ȡ�û���
			userpswd=pswdEdit.getEditableText().toString().trim(); //��ȡ����
			if(username.equals("")||userpswd.equals("")){ //�ж��Ƿ�Ϊ��
				Toast.makeText(LoginActivity.this, "�������ʺŻ�����!", Toast.LENGTH_SHORT).show(); //��ʾ�û�����
				return;
			}else{
				pd=ProgressDialog.show(LoginActivity.this, "���Ժ�", "�������ӷ�����.....",true,true);
				Login();
			}
		}
		
	}
	
	//���������û���id���������Preferences
    public void rememberMe(String uid,String pwd){
    	sharedPrefenrence = getPreferences(MODE_PRIVATE);	//���Preferences
    	SharedPreferences.Editor editor = sharedPrefenrence.edit();			//���Editor
    	editor.putString("username", uid);							//���û�������Preferences
    	editor.putString("pswd", pwd);							//���������Preferences
    	editor.commit();
    }
    
    //��������Preferences�ж�ȡ�û���������
    public void checkIfRemember(){
    	sharedPrefenrence = getPreferences(MODE_PRIVATE);	//���Preferences
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
	
	//�û���¼
	private void Login(){
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				try{
					if(connector==null){
						connector=new Connector();  //���ӷ�����
					}
					connector.ConnectServer(SERVER_ADRESS, SERVER_PORT);
					String msg="<#LOGIN_INFO#>"+username+"|"+userpswd; //��֯������Ϣ
					connector.out.writeUTF(msg);  //������Ϣ
					String reply=connector.in.readUTF();  //��ȡ�û�������Ϣ
					pd.dismiss();  //ȡ��������
					connector.ExitConnect();
					if(reply.startsWith("<#MANAGE_TRUE#>")){
						//��¼�ɹ�
						Intent intent=new Intent();
						String mid=reply.substring(15);
						sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
						editor=sharedPrefenrence.edit();
						editor.putInt("manage", 1);
						editor.putString("mid", mid);
						editor.commit();
						loginRemember = (CheckBox)findViewById(R.id.loginRemember);		//���CheckBox����
						if(loginRemember.isChecked()){
							rememberMe(username,userpswd);
						}
						Toast.makeText(LoginActivity.this, "��½�ɹ�", Toast.LENGTH_SHORT).show(); //��ʾ�û�����
						intent.setClass(LoginActivity.this,MainActivity.class);
						startActivity(intent);
							
					}else if(reply.equals("<#MANAGE_FALSE#>")){
						Toast.makeText(LoginActivity.this, "��¼ʧ��,�û������������", Toast.LENGTH_LONG).show();
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
