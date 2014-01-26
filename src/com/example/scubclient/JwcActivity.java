package com.example.scubclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class JwcActivity extends ListActivity{

	private ListView mListView=null;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private GetJwcInfo jwcinfo=null;
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
	private int mManage=0;  //管理员登录
	private SharedPreferences sharedPrefenrence=null;
	private Editor editor=null;
	//管理员登陆
	//private final int ITEM_MANAGE=0;
	//退出
	private final int ITEM_EXIT=1;
	//关于
	private final int ITEM_ABOUT=2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ExitApp.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //窗口去掉标题
		//设置窗口为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置窗口半透明
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		inflater=(LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainLayout=inflater.inflate(R.layout.title, null);
		
		sharedPrefenrence=getSharedPreferences("config",Context.MODE_PRIVATE);  ///
		mManage=sharedPrefenrence.getInt("manage", 0);
		
		menuView=View.inflate(this, R.layout.gridview_menu, null);
		menuDialog=new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new MyOnkeyListener());
		menuGrid=(GridView)menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menuName,menuImage));
		menuGrid.setOnItemClickListener(new MyOnItemClickListener());
		//获取服务器信息
		pd=ProgressDialog.show(JwcActivity.this, "加载数据", "请稍后...");
		getInfo();
		mListView.setOnItemClickListener(new MyListOnItemListener());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(connector!=null){
			connector.ExitConnect();
		}
		super.onDestroy();
	}

	//获取菜单
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray){
		ArrayList<HashMap<String,Object>> data=new ArrayList<HashMap<String,Object>>();
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
					jwcinfo=new GetJwcInfo();
					String html=jwcinfo.getTitlehtml();
					System.out.println("html----->"+html);
					if(html==null){
						handler.sendEmptyMessage(0);
						return;
					}
					mData=jwcinfo.filterTitleHtml(html);
					setadapter();
					
					handler.sendEmptyMessage(1);
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
				connecttishi();
				break;
			case 1:
				pd.dismiss();
				setListAdapter(adapter);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void setadapter(){
		adapter=new SimpleAdapter(this,mData,android.R.layout.simple_list_item_2,new String[]{"title","time"},new int[]{android.R.id.text1,android.R.id.text2});
	}
	
	protected void connecttishi(){
		 AlertDialog alertDialog = new AlertDialog.Builder(JwcActivity.this)  
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
	
	public void setContentView(int layoutResId){
		View v=this.inflater.inflate(layoutResId, null);
		ViewGroup container=(ViewGroup)mainLayout.findViewById(R.id.container);
		container.addView(v);
		super.setContentView(mainLayout);
	}
	
	private class MyOnkeyListener implements OnKeyListener{

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode==KeyEvent.KEYCODE_MENU){  //监听按键
				dialog.dismiss();
			}
			return false;
		}
		
	}
	
	private class MyListOnItemListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(JwcActivity.this, InfoContextActivity.class);
			Bundle bundle=new Bundle();
			Map<String,Object> map=mData.get(arg2);
			String infoid=(String)map.get("link");
			
			getSharedPreferences("config",Context.MODE_PRIVATE);
			editor=sharedPrefenrence.edit();
			editor.putString("link", infoid);
			editor.putBoolean("jwc", true);
			editor.putInt("manage", 1);
			editor.commit();
			
			startActivity(intent);
			
		}
		
	}
	
	private class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent=null;
			switch(arg2){
			case ITEM_EXIT:
				exitSys();
				break;
			case ITEM_ABOUT:
				break;
			}
		}
		
	}
	
	private void exitSys(){
		 AlertDialog alertDialog = new AlertDialog.Builder(JwcActivity.this)  
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
