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
				pd=ProgressDialog.show(LoginActivity.this, "���Ժ�", "�������ӷ�����.....",true,true);
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
	
	//�û���¼
	private void Login(){
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				try{
					if(connector==null){
						connector=new Connector(SERVER_ADRESS,SERVER_PORT);  //���ӷ�����
					}
					nameEdit=(EditText)findViewById(R.id.etname);
					pswdEdit=(EditText)findViewById(R.id.etpswd);
					String username=nameEdit.getEditableText().toString().trim(); //��ȡ�û���
					String userpswd=pswdEdit.getEditableText().toString().trim(); //��ȡ����
					if(username.equals("")||userpswd.equals("")){ //�ж��Ƿ�Ϊ��
						Toast.makeText(LoginActivity.this, "�������ʺŻ�����!", Toast.LENGTH_SHORT).show(); //��ʾ�û�����
						return;
					}else{
						String msg="<#LOGIN_INFO#>"+username+"|"+userpswd; //��֯������Ϣ
						connector.out.writeUTF(msg);  //������Ϣ
						String reply=connector.in.readUTF();  //��ȡ�û�������Ϣ
						pd.dismiss();  //ȡ��������
						if(reply.equals("<#MANAGE_TRUE#>")){
							//��¼�ɹ�
							Intent intent=new Intent();
							Bundle bundle=new Bundle();
							bundle.putInt("manage", 1);
							bundle.putInt("type", mtype);
							intent.putExtras(bundle);
							intent.setClass(LoginActivity.this,MainActivity.class);
							startActivity(intent);
							
						}else if(reply.equals("<#MANAGE_FALSE#>")){
							Toast.makeText(LoginActivity.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
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
