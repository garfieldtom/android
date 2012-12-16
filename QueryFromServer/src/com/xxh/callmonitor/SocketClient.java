package com.xxh.callmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {
	
	
	static Socket client;
	static int TIMEOUT=5000;
	
    public SocketClient(String site, int port){
        try{
            client = new Socket(site,port);
            //client = new Socket();  
            //InetSocketAddress isa = new InetSocketAddress(site,port);  
            //client.connect(isa,TIMEOUT); //TIMEOUT为超时时间  

            //System.out.println("Client is created! site:"+site+" port:"+port);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public String sendMsg(String msg){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println(msg);
            out.flush();
            return in.readLine();
        }catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }
    
    
    public void closeSocket(){
        try{
            client.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
 

}
