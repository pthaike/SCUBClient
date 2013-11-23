package com.example.scubclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connector {

	Socket socket=null;
	DataInputStream in=null;
	DataOutputStream out=null;
	public Connector(String url,int port) {
		// TODO Auto-generated constructor stub
		try{
			socket=new Socket(url,port);
			out=new DataOutputStream(socket.getOutputStream());
			in=new DataInputStream(socket.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	//�Ͽ�����
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
