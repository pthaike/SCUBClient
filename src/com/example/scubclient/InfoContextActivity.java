package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InfoContextActivity extends Activity{

	private Connector connector=null;
	private TextView titleText=null;
	private TextView timeText=null;
	private TextView contextText=null;
	private ImageView imageView=null;
	private String[] info=null;
	private Map<String,Object> mmap=new HashMap<String,Object>();
	private String []menu_name_array={"退出"};
	private int[] menu_image_array={R.drawable.delete2,R.drawable.publish,R.drawable.exit};
	private AlertDialog menuDialog;
	private ProgressDialog pd=null;
	private GridView menuGrid;
	private View menuView;
	private int mManage=1;  //管理员登录
	private boolean jwc=false;
	private String minfoid=null;
	private int mtype;
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE); //窗口去掉标题
		setContentView(R.layout.context);
		super.onCreate(savedInstanceState);
		ExitApp.getInstance().addActivity(this);
		titleText=(TextView)findViewById(R.id.title);
		timeText=(TextView)findViewById(R.id.time);
		contextText=(TextView)findViewById(R.id.content);
		imageView=(ImageView)findViewById(R.id.headview);
		
		sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);  ///
		mManage=sharedPrefenrence.getInt("manage", 0);
		jwc=sharedPrefenrence.getBoolean("jwc", false);
		mtype=sharedPrefenrence.getInt("type", 0);
		
		if(jwc){
			imageView.setBackgroundResource(R.drawable.jwceader);
			String link=sharedPrefenrence.getString("link", "");
			System.out.println("link--->"+link);
			getJwcinfo(link);
		}else{
			//imageView.setBackgroundResource(R.drawable.head);
			minfoid=sharedPrefenrence.getString("id","0");
			mtype=sharedPrefenrence.getInt("type", 1);
			getSingleInfo(minfoid);
		}
		menuView=View.inflate(this, R.layout.gridview_menu, null);
		menuDialog=new AlertDialog.Builder(this).create();
		if(!jwc){
			menuDialog.setView(menuView);
		}
		menuDialog.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1==KeyEvent.KEYCODE_MENU){  //监听按键
					arg0.dismiss();
				}
				return false;
			}
			
		});
		menuGrid=(GridView)menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array,menu_image_array));
		if(mManage==1){
			menuGrid.setOnItemClickListener(new MyOnItemClickListener());
		}else{
			menuGrid.setOnItemClickListener(new MyUserOnItemClickListener());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(connector!=null){
			//connector.ExitConnect();
		}
		super.onDestroy();
	}
	
	//获取菜单
		private SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray){
			ArrayList<HashMap<String,Object>> data=new ArrayList<HashMap<String,Object>>();
			if(mManage==1){
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("itemImage",R.drawable.delete2);
				map.put("itemText",  "删除");
				data.add(map);
				HashMap<String,Object> map1=new HashMap<String,Object>();
				map1.put("itemImage",R.drawable.publish);
				map1.put("itemText",  "发布信息");
				data.add(map1);
			}
			for(int i=0;i<menuNameArray.length;i++){
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("itemImage", imageResourceArray[i]);
				map.put("itemText", menuNameArray[i]);
				data.add(map);
			}
			SimpleAdapter simpleAdapter=new SimpleAdapter(this,data,R.layout.item_menu,new String[]{"itemImage","itemText"},new int[]{R.id.item_image,R.id.item_text});
			return simpleAdapter;
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			menu.add("Menu");  //必须创建一项
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onMenuOpened(int featureId, Menu menu) {
			// TODO Auto-generated method stub
			if(menuDialog==null){
				menuDialog=new AlertDialog.Builder(this).setView(menuView).show();
			}else{
				menuDialog.show();
			}
			return super.onMenuOpened(featureId, menu);
		}

		//菜单显示前可以调整菜单
		@Override
		public boolean onPrepareOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			return super.onPrepareOptionsMenu(menu);
		}
	
	private void getSingleInfo(final String id){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				try{
					if(connector==null){
						connector=new Connector();
					}
					connector.ConnectServer(SERVER_ADRESS,SERVER_PORT);
					String msg="<#GET_QG#>"+id;
					connector.out.writeUTF(msg);
					String reply=connector.in.readUTF();
					if(reply.startsWith("<#INFO_SUCCES#>")){
						info=reply.substring(15).split("\\|");
						connector.ExitConnect();
						handler.sendEmptyMessage(2);
					}else if(reply.startsWith("<#INFO_NONE#>")){
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				super.run();
			}
			
		}.start();
	}
	
	public void getJwcinfo(final String link){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				GetJwcInfo jwcinfo=new GetJwcInfo();
				String html=jwcinfo.getContextHtml(link);
				if(html==null){
					handler.sendEmptyMessage(1);
				}
				System.out.println(html);
				mmap=jwcinfo.filterContextHtml(html);
				System.out.println(mmap);
				handler.sendEmptyMessage(3);
				super.run();
			}
		}.start();
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				pd=ProgressDialog.show(InfoContextActivity.this, "获取信息", "请稍后...");
				break;
			case 1:
				pd.dismiss();
				connecttishi();
				break;
			case 2:
				pd.dismiss();
				titleText.setText(info[2]);
				timeText.setText(info[4]);
				contextText.setText(info[3]);
				break;
			case 3:
				pd.dismiss();
				String title=(String)mmap.get("title");
				String time=(String)mmap.get("time");
				String context=(String)mmap.get("context");
				titleText.setText(title);
				timeText.setText(time);
				contextText.setText(context);
				break;
			case 4:
				pd.dismiss();
				Toast.makeText(InfoContextActivity.this, "没有改信息", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	private class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=null;
			switch(arg2){
			case 0:
				//删除信息
				if(jwc){
					Toast.makeText(InfoContextActivity.this, "教务处信息不能删除",Toast.LENGTH_LONG).show();
				}else{
					delInfo();
				}
				break;
			case 1:
				//发布信息
				intent=new Intent(InfoContextActivity.this,PublishActivity.class);
				startActivity(intent);
				break;
			case 2:
				//退出
				exitSys();
				break;
			case 3:
				
				break;
			}
		}
	}
	
	private class MyUserOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			switch(arg2){
			case 0:
				exitSys();
				break;
			case 1:
				exitSys();
				break;
			}
		}
	}
	
	private void exitSys(){
		 AlertDialog alertDialog = new AlertDialog.Builder(InfoContextActivity.this)  
        .setTitle("提示！")   
        .setMessage("您确定要退出系统吗?")  
        .setPositiveButton("确定",   
        new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
            	ExitApp.getInstance().exit();   
                dialog.cancel();  //提示对话框关闭  
            }  
        })  
        .setNegativeButton("取消",  
        new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub  
                dialog.cancel();    //关闭对话框  
            }  
        }).create();  
        alertDialog.show();  
	}
	
	//
	private void delInfo(){
		AlertDialog alertDialog = new AlertDialog.Builder(InfoContextActivity.this)  
        .setTitle("提示！")   
        .setMessage("确定删除?")  
        .setPositiveButton("确定",   
        new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
            	System.out.println("track-->>>onclick");
                delInfoHelp(); 
                dialog.cancel();  //提示对话框关闭  
            }  
        })  
        .setNegativeButton("取消",  
        new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub  
                dialog.cancel();    //关闭对话框  
            }  
        }).create();  
        alertDialog.show(); 
	}
	
	//删除信息
	private void delInfoHelp(){
		new Thread(){
			public void run(){
				try{
					Connector con=new Connector();
					con.ConnectServer(SERVER_ADRESS,SERVER_PORT);
					String msg=null;
					msg="<#DEL_QG#>"+minfoid;
					System.out.println(msg);
					con.out.writeUTF(msg);
					String reply=con.in.readUTF();
					System.out.println(reply);
					con.ExitConnect();
					if(reply.equals("<#DELINFO_S#>")){
						myhandler.sendEmptyMessage(1);
						System.exit(0);
					}else{
						myhandler.sendEmptyMessage(2);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}.start();
	}
	
	Handler myhandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				Toast.makeText(InfoContextActivity.this, "删除成功",Toast.LENGTH_LONG).show();
				finish();
				break;
			case 2:
				Toast.makeText(InfoContextActivity.this, "删除失败",Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	};
	
	protected void connecttishi(){
		 AlertDialog alertDialog = new AlertDialog.Builder(InfoContextActivity.this)  
	        .setTitle("提示！")   
	        .setMessage("请检测网络")  
	        .setPositiveButton("确定",   
	        new DialogInterface.OnClickListener() {  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.cancel();  //提示对话框关闭  
	            }  
	        }).create();  
	        alertDialog.show();
	}
}
