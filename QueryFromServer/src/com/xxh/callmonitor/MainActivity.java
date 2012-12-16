package com.xxh.callmonitor;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String SHAREDPREFERENCES="MobileQuery";
	private String Host="192.168.245.133";
	private String TAG="CallMonitor";
	private int Port=2222;
	private Socket socket = null;
	private boolean socketState=false;
	private TextView txtPhoneState=null;
	//创建悬浮窗口
	private WindowManager wm=null;
	private WindowManager.LayoutParams wmParams=null;
	private MyFloatView myFV;//=null;
	private TextView tv=null;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //隐藏显示
        txtPhoneState = (TextView)findViewById(R.id.pState);
        
        mPhoneCallListener phoneListener=new mPhoneCallListener();
        /*设定TelephonyManager去抓取系统TELEPHONY_SERVICE*/ 
        TelephonyManager telMgr = (TelephonyManager)getSystemService (TELEPHONY_SERVICE);
        telMgr.listen(phoneListener, mPhoneCallListener.LISTEN_CALL_STATE); 
       
        
        SharedPreferences settings=getSharedPreferences(SHAREDPREFERENCES,Activity.MODE_PRIVATE);
		Host=settings.getString("Host", "127.0.0.1");
        Port=settings.getInt("Port",2222);
        
        Log.i(TAG,"Host:"+Host);
        Log.i(TAG,"Port:"+Port);
        
        //显示状态栏图标
        showNotification();
        //sendMsg("Test Connect to Server ...");
        //sendMsg("Hello2");
        //sendMsg("Hello3");
       
        //4.0以上的通信都必须放到线程里去做 不能在UI线程～sendMsg在4.0下会报告错误,说是加入下面内容即可：
        //如果版本是2.3以下，则不能调用下面代码
        /*
         
        */
        /*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
        .build());
        */
        //初始化Socket
       // socketState= InitSocket();
        
        /*
        try
        {
        	sendMsg("Connect to Server ...");
        	Toast.makeText(this,"连接服务器成功!", Toast.LENGTH_LONG).show();
        }catch(Exception e)
        {
        	Toast.makeText(this,"连接服务器发生错误!", Toast.LENGTH_LONG).show();
        }
	*/
        //moveTaskToBack(true);
        
    	//创建悬浮窗口
        Log.i(TAG,"创建悬浮窗口");
        //createView();        
    }
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"创建线程");
		//myThread myTread=new myThread();
		Log.i(TAG,"启动线程");
		//myTread.run();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//CloseSocket();
		super.onDestroy();
		
		//在程序退出(Activity销毁）时销毁悬浮窗口
    	//wm.removeView(myFV);
	}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public void onServiceButtonClick(View v)
	{
		Log.i(TAG,String.format("点击按钮:%d",v.getId()));
		Intent intent=null;
		switch(v.getId())
   	    {
        case R.id.btnSystemSet :
        	intent=new Intent(this,SystemSetActivity.class);
	        startActivity(intent);
            break;
        case R.id.btnAbout:
        	intent=new Intent(this,AboutActivity.class);
	        startActivity(intent);
            break;
        case R.id.btnStartQuery:
        	intent=new Intent(this,QueryActivity.class);
	        startActivity(intent);
            break;
        case R.id.btnClose:
			//TODO 系统设置
        	ConfirmExit(); 
	        break;
        case R.id.btnHide:
        	//lyService.setVisibility(View.GONE);
        	finish();
            break;
        case R.id.btnTest:
        	sendMsg("this is a test info !");
            break;
        }
 
    }
  
    
    private void createView(){
    	Log.i(TAG,"开始创建悬浮窗口");
    	Context context=null;
    	context=getApplicationContext();
    	Log.i(TAG,"实例化MyFloatView");
    	myFV=new MyFloatView(context);
    	Log.i(TAG,"设置资源");
    	myFV.setImageResource(R.drawable.ic_launcher);
    	//获取WindowManager
    	Log.i(TAG,"创建wm");
    	wm=(WindowManager)getApplicationContext().getSystemService("window");
        //设置LayoutParams(全局变量）相关参数
    	Log.i(TAG,"设置params");
    	wmParams =((MyApplication)getApplication()).getMywmParams();//new WindowManager.LayoutParams();
        wmParams.type=2002;
        //wmParams.format=1;
        wmParams.flags|=8;
        
        
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x=0;
        wmParams.y=0;
        
        //设置悬浮窗口长宽数据
        wmParams.width=40;
        wmParams.height=40;
       
        Log.i(TAG,"创建TextView");
        //tv = new TextView(this);    
        Log.i(TAG,"设置TextView");
        //tv.setText("这是悬浮窗口，来电号码：" );
        //显示myFloatView图像
        wm.addView(myFV, wmParams);
        
        //SystemClock.sleep(5000);
        //wm.removeView(myFV);
    	
    }
    
    
    //TODO:Host作为参数传人
    private boolean InitSocket()
    {
    	//InetAddress serverAddr =null;
    	//初始化设置,这样可以修改设置后不用退出程序了
    	SharedPreferences settings=getSharedPreferences(SHAREDPREFERENCES,Activity.MODE_PRIVATE);
		Host=settings.getString("Host", "127.0.0.1");
        Port=settings.getInt("Port",2222);
        
		Log.d("TCP", "C: Connecting...");   
		try
    	{
			Log.i(TAG, "Connect to Host:"+Host);
			//serverAddr = InetAddress.getByName(Host);//TCPServer.SERVERIP
			//socket = new Socket(serverAddr, Port);
			socket = new Socket(Host, Port);
			Log.i(TAG, "Socket连接成功");
			return true;
    	}catch(Exception e)
    	{
    		Log.i(TAG, "Socket连接失败",e);
    		return false;
    	}
		
    }
    
    private boolean CloseSocket()
    {
    		try
    	    {
    	    	socket.close();
    	    	return true;
    	    } catch(Exception e) 
    	    {   
    	        Log.e("TCP", "S: Error", e);
    	        return false;
    	    }
    }
    
    
    private void RemoveNotification()    
    {   
        this.finish();   
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);   
           nm.cancel(R.string.app_name);   
    }  
    
    protected void ConfirmExit() {  
        AlertDialog.Builder builder = new Builder(this);  
        builder.setMessage("确定要退出吗?");  
        builder.setTitle("提示");  
        builder.setPositiveButton("确认",  
        new android.content.DialogInterface.OnClickListener() {  
            //@Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                //AccoutList.this.finish();  
                //System.exit(1);  
                finish();
                
                //去掉状态栏图标
                RemoveNotification();
                //android.os.Process.killProcess(android.os.Process.myPid());  
            }  
        });  
        builder.setNegativeButton("取消",  
        new android.content.DialogInterface.OnClickListener() {  
            //@Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
    }  
    
    
   /*
    		去掉通知栏通知的方法 
            NotificationManager notificationManager = (NotificationManager) this 
                    .getSystemService(NOTIFICATION_SERVICE); 
            notificationManager.cancel(0);  
			通过NotificationManager  的cancel(int)方法，来清除某个通知。其中参数就是Notification的唯一标识ID。
			当然也可以通过  cancelAll() 来清除状态栏所有的通知。
    */
    
    private void showNotification() { 
        // 创建一个NotificationManager的引用 
        NotificationManager notificationManager = (NotificationManager) 
            getSystemService(android.content.Context.NOTIFICATION_SERVICE); 
        
        // 定义Notification的各种属性 
        
        Notification notification =new Notification(R.drawable.app_icon, 
                "CallMonitor", System.currentTimeMillis()); 
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中 
        //notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用 
        notification.flags |= Notification.FLAG_SHOW_LIGHTS; 
        notification.defaults = Notification.DEFAULT_LIGHTS; 
        notification.ledARGB = Color.BLUE; 
        notification.ledOnMS =5000; 
                
        // 设置通知的事件消息 
        CharSequence contentTitle ="电话通知"; // 通知栏标题 
        CharSequence contentText ="CallMonitor Running..."; // 通知栏内容 
        Intent notificationIntent =new Intent(this, MainActivity.class); // 点击该通知后要跳转的Activity 
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, 
                notificationIntent, 0); 
        notification.setLatestEventInfo(this, contentTitle, contentText, 
                contentItent); 

        // 把Notification传递给NotificationManager 
        notificationManager.notify(R.string.app_name, notification); 
    } 

    public boolean ShowNotification(String sMsg)
    {
  	  
        //第一步
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //第二步
        Notification notification = new Notification(R.drawable.app_icon, "您有新来电："+sMsg, System.currentTimeMillis());
        //第三步
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
        //第四步
        notification.setLatestEventInfo(this,"来电号码",sMsg,contentIntent);
        //第五步
        notificationManager.notify(R.drawable.app_icon,notification);
        
        return true;

    }

   /*
    // 从服务端程序接收数据 ,放在循环或线程中
InputStream ips = socket.getInputStream(); 
InputStreamReader ipsr = new InputStreamReader(ips); 
BufferedReader br = new BufferedReader(ipsr); 
String s = ""; 
while((s = br.readLine()) != null) 
System.out.println(s); 
    */ 
    
   //发送数据 
    public String sendMsg(String msg)
    {
    	try
    	{
    		//TODO:测试,只在这里调用
    		InitSocket();
    		Log.d("TCP", "C: Sending: '" + msg + "'");   
    	    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);   
    	    out.println(msg);   
    	    //TODO:for test
    	    //等待5秒,接收服务器数据
    	    SystemClock.sleep(5000);
    	    myThread myTread=new myThread();
    		Log.i(TAG,"启动线程");
    		myTread.run();
    		
    	} catch (Exception e) 
    	{
			// TODO Auto-generated catch block
    		Log.e("TCP", "S: Error", e);  
		}finally
		{
			CloseSocket();
		}
    	

    	return "";
    }
    
    
    
    /*使用PhoneCallListener来启动事件*/
    public class mPhoneCallListener extends PhoneStateListener 
    { 
      @Override public void onCallStateChanged(int state, String incomingNumber) 
      {
        // TODO Auto-generated method stub 
        switch(state) 
        { 
          /*取得电话待机状态*/ 
        
          case TelephonyManager.CALL_STATE_IDLE: txtPhoneState.setText(R.string.hello_world);
          break; 
          //*取得电话通话状态
          case TelephonyManager.CALL_STATE_OFFHOOK: txtPhoneState.setText(R.string.hello_world);
          break;
          //*取得来电状态
          case TelephonyManager.CALL_STATE_RINGING: 
        	  txtPhoneState.setText (incomingNumber );
        	  //在状态栏显示通知
        	  //ShowNotification(incomingNumber);
        	  //发送Socket消息
        	  //SendSocketMsg(incomingNumber);
        	  sendMsg("CallEvent|"+incomingNumber);
          break; 
          default: break;
          } 
        super.onCallStateChanged(state, incomingNumber); 
        }
      }
    
    
    //创建读取线程
    public class myThread extends Thread
    {
    	//构造函数
    	public myThread()
    	{
    		
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			//一直循环
			boolean bTread=true;
			while(bTread)
			{
				Log.i(TAG,"进入线程循环");
				try
				{
					sleep(100);
					//读取输入流
					//String s = ""; 
					Log.i(TAG,"开始读取数据....");
					InputStream ips = socket.getInputStream(); 
					Log.i(TAG,"获取输入流...");
					byte[] buffer=new byte[ips.available()];
					Log.i(TAG,"读取输入流...");
					ips.read(buffer);
					String s=new String(buffer);
					Log.i(TAG,"判断输入流...");
					//TODO:测试
					//createView();
					s="hello,this is a test";
					if(s!="")
					{	
							Log.i(TAG,"收到消息"+s);
							//showTopWindow(s);
							//createView();

							Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
							//showAlert(s);
							/*
							Message message=new Message();
							message.what=1;
							StringBuilder sb=(StringBuilder)message.obj;
							sb.append(s);
							Log.i(TAG,"发送消息");
							Log.i(TAG,sb.toString());
							mHandler.sendMessage(message);
							*/
					}else
					{
						Log.i(TAG,"没有收到消息");
					}
				}catch(Exception e)
				{
					
				}
				
				//TODO:for test
				bTread=false;
			}	
		}
		
		@Override
		public synchronized void start() {
			// TODO Auto-generated method stub
			super.start();
		}
    	
    }

    //显示悬浮窗口
    private void showTopWindow(String msg)
    {
    	WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);     
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();     
        params.type =WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;// WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;     
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;    
             
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;     
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;     
        params.format = PixelFormat.RGBA_8888;   
        TextView tv = new TextView(this);    
        tv.setText("这是悬浮窗口，来电号码：" + msg);   
        wm.addView(tv, params);   
        
        SystemClock.sleep(5000);
        wm.removeView(tv);  

    }
    
    private void showAlert(String msg)
    {
    	         new AlertDialog.Builder(MainActivity.this)
    	         /**设置标题**/
    	         .setTitle("重要")
    	         /**设置icon**/
    	         .setIcon(android.R.drawable.alert_dark_frame)
    	         /**设置内容**/
    	         .setMessage(msg)
    	         .setNegativeButton("取消", new DialogInterface.OnClickListener(){
    	 
    	             public void onClick(DialogInterface dialog, int which) {
    	                 // TODO Auto-generated method stub
    	                 
    	             }}).setPositiveButton("确定", new DialogInterface.OnClickListener(){
    	 
    	                 public void onClick(DialogInterface dialog, int which) {
    	                     /**关闭窗口**/
    	                     finish();
    	                     
    	                 }}).show();
    	         
    }
    
	private Handler mHandler =new Handler()
	{
		public  void handleMessage(Message msg)
		{
			switch (msg.what) 
			{
			 case 1:
				 //updateTitle();
				 //显示消息内容
				 StringBuilder sb=(StringBuilder)msg.obj;
				 String s=sb.toString();
				 Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
				 break;
			}
		};	
	};


    
}
