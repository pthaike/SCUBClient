package com.example.scubclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connector {

	Socket socket=null;
	DataInputStream in=null;
	DataOutputStream out=null;
	public Connector() {
		// TODO Auto-generated constructor stub
		
	}
	
	public void ConnectServer(String url,int port){
		try{
			socket=new Socket(url,port);
			out=new DataOutputStream(socket.getOutputStream());
			in=new DataInputStream(socket.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	//¶Ï¿ªÁ¬½Ó
	public void ExitConnect(){
		try{
			out.writeUTF("<#EXITCON#>");
			out.close();
			in.close();
			socket.close();
			socket=null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
