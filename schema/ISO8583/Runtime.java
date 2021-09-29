package ISO8583;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimerTask;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO93BPackager;

import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.UserProperty;
import com.magicsoftware.xpi.sdk.trigger.TriggerGeneralParams;
import com.magicsoftware.xpi.sdk.trigger.external.FlowLauncher;
import com.magicsoftware.xpi.sdk.trigger.external.IExternalTrigger;
import com.magicsoftware.xpi.sdk.trigger.external.Response;
import java.util.Timer;

public class Runtime implements IExternalTrigger{
private final static Logger log = Logger.getLogger("MyTriggerLoggerName");
	private FlowLauncher launcher;
	private int counter=0;
	private Timer timer;
	static ISO93BPackager pack = new ISO93BPackager();
	//static String IP="10.23.223.101";
	//static int port=5012;
	//static String IP="10.50.8.141";
	//static int port=58550;
	//static NACChannel asciiChnl = new NACChannel(IP,port, pack);
	static NACChannel asciiChnl =null;
	static int cnt=1;
	static String filePath="E:\\AuditLog\\Ramesh\\";
	public Runtime(){}
	@Override
	public void disable() {
		// Called when the flow is disabled. 
	}

	@Override
	public void enable() {
		// Called when the flow is enabled. 
	}
	@Override
	public void load(TriggerGeneralParams generalParams, FlowLauncher fl) throws SDKException {
		launcher=fl;
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    call();
			  }
			}, 1000, 1000);
	}
	@Override
	public void unload() {
		//will be called when the engine shuts down.
	}
	
	private void call(){
		HashMap<String, Object> args = new HashMap<String, Object>();
		ISOMsg respMsg =null;
		try {
			Custom_Logs.WriteLogs("Inside Call(): ");
			asciiChnl=NACChannel.getNACChannelInstance();
			if (!asciiChnl.isConnected()){
				Custom_Logs.WriteLogs("New Connection: ");
				
				asciiChnl.connect();
			Custom_Logs.WriteLogs("Connection established ");
			String serial=new DecimalFormat("000000").format(new Random().nextInt(999999));
			ISOMsg reqMsg = SendReceiveClient.echoTest("1804","ISO8583-1993021000000", "1029025643", serial, "0801", "201029", "1000000000", "000028");
			Custom_Logs.WriteLogs("Echo ISO Request: "+logISOMsg(reqMsg));
			asciiChnl.send(reqMsg);
			Custom_Logs.WriteLogs("eCHO sENT sUCCESSFULLY: ");
			}

			if (asciiChnl.isConnected()) {
				Custom_Logs.WriteLogs("while (asciiChnl.isConnected()).."+asciiChnl.isConnected());
				SimpleDateFormat df = new SimpleDateFormat("YYYYMMddHHmmss");
				String timestamp = df.format(new Date());
				respMsg = asciiChnl.receive();
				Custom_Logs.WriteLogs("Switch incomming request/response MTI..\n"+respMsg.getMTI());
				Custom_Logs.WriteLogs("Switch incomming request/response..\n"+logISOMsg(respMsg));
				Custom_Logs.WriteLogs("Switch incomming request/response hex dump..\n"+ISOUtil.hexdump(respMsg.pack()));
				if(respMsg.getMTI().equals("1814"))
				{
					FileWriter writer1 = new FileWriter(filePath+"Switch_Response"+respMsg.getMTI()+timestamp+".txt"); 
					writer1.write(logISOMsg(respMsg)); 
					writer1.flush();
					writer1.close();
				}
				else
				{
					 FileWriter writer = new FileWriter(filePath+"NAT\\NAT_Request"+respMsg.getMTI()+timestamp+".txt"); 
				      writer.write(logISOMsg(respMsg)); 
				      writer.flush();
				      writer.close();
				     /* HashMap<String, String> map=logISOMsgMap(respMsg);
				      ISOMsg reqMsg1 = SendReceiveClient.echoTestResp("1814","ISO8583-1993021000000", map.get("Field-7"), map.get("Field-11"), map.get("Field-24"), map.get("Field-28"),"0800", map.get("Field-93"), map.get("Field-94"));
				      
				      pack = new ISO93BPackager();
				      asciiChnl.send(reqMsg1);
				      FileWriter writer1 = new FileWriter(filePath+"Magic\\Magic_Response_"+respMsg.getMTI()+timestamp+".txt"); 
				      writer1.write(logISOMsg(reqMsg1)); 
				      writer1.flush();
				      writer1.close();*/
					//Response=logISOMsg(reqMsg);
				}
			}
			Custom_Logs.WriteLogs("SwitchData..");
			args.put("SwitchData", logISOMsg(respMsg).getBytes());
            //args.put("ChannelObject",asciiChnl.toString().getBytes());
           // Custom_Logs.WriteLogs("ChannelObject..");
            launcher.invoke(args);
		} catch (Exception e) {
			// You can use the log4j infrastructure already loaded by Magic xpi and write to your own log
			Custom_Logs.WriteLogs("Exception...\n"+StackTraceToString(e));
			log.error("Invoke flow has failed",e);
		}
	}
	public byte[] echoTestingResponse(String MTI,String Header,String f7,String f11, String f24,String f28,String f39, String f93, String f94,byte[] byteOobj) {
		String Response = "";
		try {
			Custom_Logs.WriteLogs("Inside echoTestingResponse() Called..");
			asciiChnl=NACChannel.getNACChannelInstance();
			if (!asciiChnl.isConnected()){
				Custom_Logs.WriteLogs("New Connection: ");
				
				asciiChnl.connect();
			Custom_Logs.WriteLogs("Connection established ");
			}
			ISOMsg reqMsg = SendReceiveClient.echoTestResp(MTI,Header, f7, f11, f24, f28,f39, f93, f94);
			pack = new ISO93BPackager();
			Custom_Logs.WriteLogs("Response "+logISOMsg(reqMsg));
			
			asciiChnl.send(reqMsg);
			Response=logISOMsg(reqMsg);
			System.out.print(Response);
			
		} catch (Exception e) {
			
			Response=StackTraceToString(e);
			Custom_Logs.WriteLogs("Exception occured..\n"+Response);
		}
		return Response.getBytes();
	}
public static NACChannel isChannelConnected(String hostIp, int port, ISO93BPackager pack) {
		try {
				Custom_Logs.WriteLogs("isChannelConnected Called..");
				if (!asciiChnl.isConnected())
				{
					asciiChnl.connect();
					Custom_Logs.WriteLogs("Connected Again..");
					return asciiChnl;
				}else{
					Custom_Logs.WriteLogs("Already Connected..");
					return asciiChnl;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Custom_Logs.WriteLogs("Exception occured : "+StackTraceToString(e));
				return null;
			}
			
			
		}
static HashMap<String,String> logISOMsgMap(ISOMsg msg) throws IOException {
	String Response = "";
	HashMap<String,String> map=new HashMap<>();
	try {
		
		System.out.println("Header:" + new String(msg.getHeader())+ "&");
		//System.out.println("MTI:" + new String(msg.geb)+ "&");
		//Response = String.valueOf(Response) + "MTI:" + msg.getMTI() + "&";
		//map.put("MTI","1814");
		//map.put("Header","ISO8583-1993021000000");
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
			
			//System.out.println("Header:" + new String(msg.getHeader())+ "&");
			//System.out.println("MTI:" + new String(msg.geb)+ "&");
			Response = String.valueOf(Response) + "MTI:" + msg.getMTI() + "&";
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
