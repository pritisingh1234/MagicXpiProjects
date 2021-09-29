package ISO8583;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SerializationUtils;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;
import org.jpos.iso.packager.ISO93CPackager;
import org.json.JSONObject;

import com.magicsoftware.ibolt.*;
import com.magicsoftware.ibolt.commons.logging.LogLevel;
import com.magicsoftware.ibolt.commons.logging.Logger;
import com.magicsoftware.ibolt.commons.util.IBBase64.InputStream;
import com.magicsoftware.xpi.sdk.GeneralParams;
import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.UserProperty;
import com.magicsoftware.xpi.sdk.trigger.TriggerGeneralParams;
import com.magicsoftware.xpi.sdk.trigger.external.Argument;
import com.magicsoftware.xpi.sdk.trigger.external.FlowLauncher;
import com.magicsoftware.xpi.sdk.trigger.external.IExternalTrigger;
import com.magicsoftware.xpi.sdk.trigger.external.Response;
import com.magicsoftware.xpi.server.entities.TriggerEntity;
import com.magicsoftware.xpi.server.messages.ExternalResponse;
import com.sun.nio.sctp.AssociationChangeNotification;
import com.magicsoftware.xpi.sdk.trigger.*;

import java.util.Timer;

public class Runtime implements IExternalTrigger{
	protected LogModules module = LogModules.STEP;
	private  FlowLauncher launcher;
	private Timer timer;
	private Timer timer2;
	private Timer timer3;
	ServerSocket ss=null;
	static  ISO93BPackager pack = new ISO93BPackager();
	static  String IP=null;
	static  int port=0;
	static int localport=6666;
	static NACChannel asciiChnl;
	static int cnt=1;
	static JSONObject object=null;
	
	public Runtime(){}
	@Override
	public void disable() {
		// Called when the flow is disabled. 
	}

	@Override
	public void enable() {
		// Called when the flow is enabled. 
	}
	
	public void configJson()
	{
		try{
		 String data=null;
	        String relativePath=System.getProperty("user.dir")+"\\Echo.json";
	        Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Echo Json Path:"+relativePath); 
	        File myObj = new File(relativePath);
	        Scanner myReader = new Scanner(myObj);
	        
	        while (myReader.hasNextLine()) {
	          data = myReader.nextLine();
	          System.out.println(data);
	        }
	        myReader.close();
		object=new JSONObject(data);
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Echo Json Data:"+object); 
		}
		catch(Exception e)
		{
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Echo Json error:"+StackTraceToString(e)); 
			
		}
	}

	@Override
	public void load(TriggerGeneralParams generalParams, FlowLauncher fl) throws SDKException {
		launcher=fl;
		HashMap<String, String> resource =null;
		
		timer = new Timer();
		timer2 = new Timer();
		
		 
		
		try {
			configJson();
				 resource=generalParams.getServiceObject();
				 if (resource.containsKey("Switch IP"))
						IP = resource.get("Switch IP");
					if (resource.containsKey("Port"))
						port = Integer.parseInt(resource.get("Port"));
						if (resource.containsKey("Local Port"))
							localport = Integer.parseInt(resource.get("Local Port"));
			asciiChnl =new NACChannel(IP,port, pack);
			
			Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Switch IP"+IP);
			Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Switch Port"+port);
			Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Local Port"+localport);
						
			timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    call();
			  }
			}, 1000, 1000);
		Thread.sleep(2000);
		timer2.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    echoTesting();
			  }
			}, 1000, 29000);
		MgListener t3 = new MgListener(localport);
		  
		  Thread myThread3 = new Thread(t3,"t3");
		  myThread3.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void unload() {
		//will be called when the engine shuts down.
	}
	
	void call(){
		
		HashMap<String, Object> args = new HashMap<String, Object>();
		ISOMsg respMsg =null;
		try {
			
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"[call] - Object created with hash"+asciiChnl.hashCode());
			if (!asciiChnl.isConnected()){
				
				asciiChnl.connect();
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"[call] - Get socket "+asciiChnl.getSocket());
			
			String serial=new DecimalFormat("000000").format(new Random().nextInt(999999));
	        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
	        Date date = new Date();
	        
	        Calendar c = Calendar.getInstance();
	        c.setTime(date);
	        c.add(Calendar.HOUR, -2);
	        Date currentDatePlusOne = c.getTime();
	        String f7=dateFormat.format(currentDatePlusOne);
	        ISOMsg reqMsg = SendReceiveClient.echoTest(object.getString ("MTI"),object.getString ("Header"), f7, serial, object.getString ("Field24"), object.getString ("Field28"), object.getString ("Field93"), object.getString ("Field94"));
			
	        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Echo ISO Request: "+logISOMsg(reqMsg));
			asciiChnl.send(reqMsg);
			
			}
			
			if (asciiChnl.isConnected()) {
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"while (asciiChnl.isConnected()).."+asciiChnl.isConnected());
				respMsg = asciiChnl.receive();
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"receive function called...\n");
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Switch incomming request/response..\n"+logISOMsg(respMsg));
				if(respMsg.getMTI().equals("1814") && logISOMsg(respMsg).contains("Field-24:"+object.getString("SignOn_Field24")))
				{
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"SignOn_Response:"+logISOMsg(respMsg)); 
					
				}
				else if(respMsg.getMTI().equals("1814") && logISOMsg(respMsg).contains("Field-24:"+object.getString("Echo_Field24")))
				{
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Echo_Response:"+logISOMsg(respMsg)); 
				}
				else if(respMsg.getMTI().equals("1804"))
				{
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"NAT_Request:"+logISOMsg(respMsg));
					 
				      byte[] resdata=echoTestingResponse(logISOMsg(respMsg));
				      ISOMsg isoMsg = new ISOMsg();
						isoMsg.setPackager(new ISO93BPackager());
						isoMsg.unpack(resdata);
						isoMsg.setHeader((object.getString("Header")).getBytes());
						asciiChnl.send(isoMsg);
						Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Magic has set response back to Switch :\n"+logISOMsg(isoMsg));
						Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Magic has set response back to Switch :\n"+ new String(resdata));
				}
				else
				{
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Incoming Request for 1200 SwitchData:\n"+ logISOMsg(respMsg));
					
						args.put("SwitchData", logISOMsg(respMsg).getBytes());
						Response result=null;
						if(launcher!=null)
						{
							result = launcher.invoke(args);
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Launcher is not t null.. " + result.toString());
							
						}
						else
						{
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Launcher is null..");
						}
						Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Trigger Response Started..");
						if(result!=null)
						{
							
							UserProperty userP=result.getData("MgResponse");
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Printing Get Data " + userP.toString());
							
							if(userP!=null){
								Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Inside userP condition value " + userP.getValue() );
							
							byte[] resdata=(byte[]) userP.getValue();
							
							String s=new String(resdata);
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Trigger Response Data.."+s);
							ISOMsg isoMsg = new ISOMsg();
							isoMsg.setPackager(new ISO93BPackager());
							isoMsg.unpack(resdata);
							
							isoMsg.setHeader((object.getString("Header")).getBytes());
							
							asciiChnl.send(isoMsg);
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Magic has set response back to Switch :\n"+logISOMsg(isoMsg));
							Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Magic has set response back to Switch :\n"+ new String(resdata));
							
							}
						}
				}
			}		
							
						}
		 catch (Exception e) {
			// You can use the log4j infrastructure already loaded by Magic xpi and write to your own log
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Exception...\n"+StackTraceToString(e));
			
		}
	}
	public byte[] echoTestingResponse(String MTI,String Header,String f7,String f11, String f24,String f28,String f39, String f93, String f94,byte[] byteOobj) {
		String Response = "";
		try {
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Inside echoTestingResponse() Called..");
			
			if (!asciiChnl.isConnected()){
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"New Connection: ");
				
				asciiChnl.connect();
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Connection established ");
			}
			ISOMsg reqMsg = SendReceiveClient.echoTestResp(MTI,Header, f7, f11, f24, f28,f39, f93, f94);
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Response "+logISOMsg(reqMsg));
			
			asciiChnl.send(reqMsg);
			Response=logISOMsg(reqMsg);
			System.out.print(Response);
			
		} catch (Exception e) {
			
			Response=StackTraceToString(e);
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Exception...\n"+StackTraceToString(e));
		}
		return Response.getBytes();
	
	}
	public byte[] echoTestingResponse(String ISOReq) {
		byte[] Response = null;
		try {
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Inside echoTestingResponse() Called..");
			ISOMsg reqMsg = SendReceiveClient.echoTestResp(ISOReq);
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Magic Response Msg."+logISOMsg(reqMsg));
			return reqMsg.pack();
			
		} catch (Exception e) {
			
			Response=StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Exception...\n"+StackTraceToString(e));
			return Response;
		}
	}
public static NACChannel isChannelConnected() {
		try {
			
				
				if (!asciiChnl.isConnected())
				{
					asciiChnl.connect();
					
					return asciiChnl;
				}else{
					
					return asciiChnl;
				}
			} catch (Exception e) {
				e.printStackTrace();
				
				return null;
			}
			
			
		}
public  void echoTesting() {
	try {
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Echo Request Started..");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
       
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, -2);
        Date currentDatePlusOne = c.getTime();
        String f7=dateFormat.format(currentDatePlusOne);
		ISOMsg reqMsg = SendReceiveClient.echoTest(object.getString ("MTI"),object.getString ("Header"), f7, object.getString ("Field11"), object.getString ("Field24"), object.getString ("Field28"), object.getString ("Field93"), object.getString ("Field94"));
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Echo_Request"+logISOMsg(reqMsg));
		
		if(asciiChnl.getSocket()!=null)
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"[echoTesting] - Get socket "+asciiChnl.getSocket());
		
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"[echoTesting] - Object created with hash"+asciiChnl.hashCode());
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Created ISO Request :\n"+new String(reqMsg.pack()));
		
		if(!asciiChnl.isConnected()){
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Connection is not availble");
		}else{
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Connection is availble");
		}
		asciiChnl.send(reqMsg);
	} catch (Exception e) {
		Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Exception...\n"+StackTraceToString(e));
		e.printStackTrace();
	}
	
}



static HashMap<String,String> logISOMsgMap(ISOMsg msg) throws IOException {
	String Response = "";
	HashMap<String,String> map=new HashMap<>();
	try {
		
		System.out.println("Header:" + new String(msg.getHeader())+ "&");
		for (int i = 1; i <= msg.getMaxField(); i++) {
			if (msg.hasField(i)) {
				Response = String.valueOf(Response) + "Field-" + i + ":" + msg.getString(i) + "&";
				map.put("Field-" + i, msg.getString(i));

			}
		}
	} catch (Exception e) {
		Response=e.toString();
		
	}

	System.out.println(Response);
	return map;
}
	static String logISOMsg(ISOMsg msg) throws IOException {
		String Response = "";

		try {
			
			
			if (new String(msg.getHeader()) != null)
			{
				Response = String.valueOf(Response) + new String(msg.getHeader()) + "MTI:" + msg.getMTI() + "&";
			}
			else
			{
				Response = String.valueOf(Response)  + "MTI:" + msg.getMTI() + "&";
			}
			
			for (int i = 1; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					Response = String.valueOf(Response) + "Field-" + i + ":" + msg.getString(i) + "&";
				}
			}
		} catch (ISOException e) {
			Response=e.toString();
		}

		System.out.println(Response);
		return Response;
	}
	public static String StackTraceToString(Exception err) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		err.printStackTrace(pw);
		return sw.toString();
	}
	
	}


