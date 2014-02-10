package com.example.scubclient;

import static com.example.scubclient.ConstantUtil.SERVER_ADRESS;
import static com.example.scubclient.ConstantUtil.SERVER_PORT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class JzActivity extends ListActivity{

	private ListView mListView=null;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private View mainLayout;
	private LayoutInflater inflater;
	private SimpleAdapter adapter;
	private Connector connector=null;
	private ProgressDialog pd=null;
	private String[] menuName={"退出"};
	private int[] menuImage={R.drawable.exit};
	private AlertDialog menuDialog;
	private GridView menuGrid;
	private View menuView;
	private int start=0;
	private int end=20;
	private int mManage=0;  //管理员登录
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ExitApp.getInstance().addActivity(this);
		sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
		mManage=sharedPrefenrence.getInt("manage", 0);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //窗口去掉标题
		//设置窗口为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置窗口半透明
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		inflater=(LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainLayout=inflater.inflate(R.layout.jztitle, null);
		
		menuView=View.inflate(this, R.layout.gridview_menu, null);
		menuDialog=new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new MenuOnKeyListener());
		menuGrid=(GridView)menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menuName,menuImage));
		if(mManage==1){
			menuGrid.setOnItemClickListener(new MyOnItemClickListener());
		}else{
			menuGrid.setOnItemClickListener(new MyUserOnItemClickListener());
		}
		
		//获取服务器信息
		pd=ProgressDialog.show(JzActivity.this, "加载数据", "请稍后...");
		
		getInfo();
		
		mListView.setOnItemClickListener(new ListItemClickListener());
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		refresh();
//	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		mData.clear();
		getInfo();
		adapter.notifyDataSetChanged();
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
			map.put("itemImage",R.drawable.publish);
			map.put("itemText",  "发布信息");
			data.add(map);
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

	//获取信息
	public void getInfo(){
		mListView=getListView();
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					if(connector==null){
						connector=new Connector();
					}
					connector.ConnectServer(SERVER_ADRESS,SERVER_PORT);
					String msg="<#GET_JZT#>"+start+"|"+end;
					connector.out.writeUTF(msg);
					String reply=connector.in.readUTF();
					if(reply.startsWith("<#INFO_SUCCES#>")){
						String str=reply.substring(15);
						String []m=str.split("\\|");
						int count=Integer.parseInt(m[0]);
						for(int i=0,j=1;i<count;i++){
							Map<String,Object> map=new HashMap<String,Object>();
							String t=m[j+1].length()>10?m[j+1].substring(0, 10)+"....."+"\n"+m[j+2]:m[j+1]+"\n"+m[j+2];
							map.put("id",m[j]);
							map.put("title",t);
							map.put("hname", m[j+2]);
							map.put("time", m[j+3]);
							j+=4;
							mData.add(map);
						}
						connector.ExitConnect();
						handler.sendEmptyMessage(0);
					}else{
						handler.sendEmptyMessage(1);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
	Handler handler=new Handler(){

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 0:
				pd.dismiss();
				setadapter();
				setListAdapter(adapter);
				break;
			case 1:
				pd.dismiss();
				Toast.makeText(JzActivity.this, "数据加载失败", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private void setadapter(){
		adapter=new SimpleAdapter(this,mData,android.R.layout.simple_list_item_2,new String[]{"title","time"},new int[]{android.R.id.text1,android.R.id.text2});
	}
	
	
	public void setContentView(int layoutResId){
		View v=this.inflater.inflate(layoutResId, null);
		ViewGroup container=(ViewGroup)mainLayout.findViewById(R.id.container);
		container.addView(v);
		super.setContentView(mainLayout);
	}
	
	private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(JzActivity.this, ShowJzActivity.class);
			Bundle bundle=new Bundle();
			Map<String,Object> map=mData.get(arg2);
			String infoid=(String)map.get("id");
			
			sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);
			editor=sharedPrefenrence.edit();
			editor.putString("infoid", infoid);
			bundle.putInt("type", 1);
			editor.commit();
			
			startActivity(intent);
		}
	}
	
	private class MenuOnKeyListener implements OnKeyListener{

		@Override
		public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
			// TODO Auto-generated method stub
			if(arg1==KeyEvent.KEYCODE_MENU){  //监听按键
				arg0.dismiss();
			}
			return false;
		}
		
	}
	
	private class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=null;
			switch(arg2){
			case 0:
				intent=new Intent(JzActivity.this,JzPublishActivity.class);
				startActivity(intent);
				break;
			case 1:
				exitSys();
				break;
			case 2:
				
				break;
			}
		}
	}
	
	private class MyUserOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=null;
			switch(arg2){
			case 0:
				exitSys();
				break;
			case 1:
				break;
			}
		}
	}
	
	private void exitSys(){
		 AlertDialog alertDialog = new AlertDialog.Builder(JzActivity.this)  
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
}
