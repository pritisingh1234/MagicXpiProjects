package ISO8583;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO93CPackager;
import org.json.JSONException;
import org.json.JSONObject;

import com.magicsoftware.ibolt.commons.logging.LogLevel;
import com.magicsoftware.ibolt.commons.logging.Logger;
import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.UserProperty;
import com.magicsoftware.xpi.sdk.UserProperty.Direction;
import com.magicsoftware.xpi.sdk.UserProperty.XpiType;
import com.magicsoftware.xpi.sdk.step.IStep;
import com.magicsoftware.xpi.sdk.step.StepGeneralParams;

import sun.font.CreatedFontTracker;



public class StepRuntime implements IStep {
	
	JSONObject object=null;
	String resposne = null;
	boolean flag = false;
	UserProperty Filepath = null;
	UserProperty fileVar = null;
	UserProperty varOrNone = null;
	Runtime triggerISO=new Runtime();
	protected LogModules module = LogModules.STEP;
	public static final String RESULT_VAR 			= "StoreResultVar";
	public static final String RESULT_FILE 			= "StoreResultFile";
	public static final String SUCCESS 				= "OperationSuccess";
	private boolean successOp = true;
	@Override
	public void invoke(StepGeneralParams params) throws SDKException {
	String payload = "";
	String ip="localhost";
	int port = 6666;
	fileVar = params.getUserProperty("ComboResultValue");
	varOrNone = params.getUserProperty("ComboOperationValue");
	Filepath = params.getUserProperty("storeResultFile");
	UserProperty EntityObject = null;
	UserProperty Operation =null;
	HashMap<String, String> resource =null;
	FileOutputStream fos=null;
	try{
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Connector Step Called..");
		payload = new String(params.getPayloadOBject());
		EntityObject= params.getUserProperty("EntityObject");
		Operation = params.getUserProperty("Operation");
		resource = params.getResourceObject();
		byte[] response=null;
		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Original Payload."+payload);
		if (resource.containsKey("IP"))
			ip = resource.get("IP");
		if (resource.containsKey("Port"))
			port = Integer.parseInt(resource.get("Port"));
		Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Step Resource IP."+ip);
		Logger.logMessage(LogLevel.LEVEL_INFO, this.module,"Step Resource Port."+port);
			object=new JSONObject(payload);
			if(Operation.getValue().toString().equals("Transaction"))
				response=ISOTransactionReq(object,ip,port);
			if(Operation.getValue().toString().equals("TransactionResponse"))
				response=ISOTransactionResp(object,ip,port);
			if(Operation.getValue().toString().equals("ReverseTransaction"))
				response=ISORevTransactionReq(object,ip,port);
			if(Operation.getValue().toString().equals("ReverseTransactionResponse"))
				response=ISORevTransactionResp(object,ip,port);
			if(Operation.getValue().toString().equals("BalanceInquiry"))
				response=ISOBalanceInquiry(object,ip,port);
			if(Operation.getValue().toString().equals("BalanceInquiryResponse"))
				response=ISOBalanceInquiryResp(object,ip,port);
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Magic Response in Step wrapper ."+new String(response));
		
	
			HashMap<String, UserProperty> userProperties = new HashMap<>();
			if (params.getUserProperties().get(RESULT_FILE)!=null){
				UserProperty resultFile = params.getUserProperties().get(RESULT_FILE);
				Object resultFileObj = resultFile.getValue();
				if (resultFileObj != null && !resultFileObj.equals("")){ 
					String fileName = (String)resultFileObj;
					File file = new File(fileName);
					fos = new FileOutputStream(file);
					fos.write(new String(response).getBytes("UTF-16LE"));
					fos.flush();
					fos.close();
				}
			}
			if(!(new String(response).contains("MTI")))
			successOp=false;
			UserProperty success = new UserProperty(XpiType.Logical.name(), Direction.Out.name(), successOp);
			UserProperty resultBlob = new UserProperty(XpiType.Blob.name(), Direction.Out.name(), response);
			userProperties.put(SUCCESS, success );
			userProperties.put(RESULT_VAR, resultBlob );
			params.setUserProperties(userProperties);

	} catch (Exception e) {
		Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Unable to set result" + Runtime.StackTraceToString(e));
		
		throw new SDKException(100, "Error : " + Runtime.StackTraceToString(e));
	}finally {
		
	}
	}
	public byte[] ISOTransactionReq(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
		try{
			reqMsg=createISOMessage(object2);   
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Transaction_Request"+Runtime.logISOMsg(reqMsg));
				reqMsg.pack();	
				Socket s=new Socket(ip,port);  
				DataOutputStream dout=new DataOutputStream(s.getOutputStream()); 
				dout.writeUTF(new String(reqMsg.pack()));  
				dout.flush();  
				dout.close();  
				s.close();  
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Magic Transaction Request Sent over 6666 port..\n"+ip+" "+port+Runtime.logISOMsg(reqMsg));
			return (Runtime.logISOMsg(reqMsg)).getBytes();
			} catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Magic Transaction Request Sent over 6666 port error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public static ISOMsg createISOMessage(JSONObject object2) throws JSONException, ISOException {
					
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(11, object2.getString ("Field11"));
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, object2.getString ("Field37"));
		testMsg.set(41, object2.getString("Field41"));
		testMsg.set(42, object2.getString("Field42"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(53, object2.getString("Field53"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
		
		
	}
	public byte[] ISOTransactionResp(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
		try{
			reqMsg=createPaymentResp(object2); 
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Transaction_Response"+Runtime.logISOMsg(reqMsg));
			return new String(reqMsg.pack()).getBytes();
			} catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Transaction_Response Error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public static ISOMsg createPaymentResp(JSONObject object2)
			throws ISOException {
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(5, object2.getString ("Field5"));
		testMsg.set(6, object2.getString ("Field6"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(10, object2.getString ("Field10"));
		testMsg.set(11, object2.getString ("Field11"));
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(28, object2.getString("Field28"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, object2.getString ("Field37"));
		testMsg.set(39, object2.getString("Field39"));
		testMsg.set(43, object2.getString("Field43"));
		testMsg.set(48, object2.getString("Field48"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(50, object2.getString("Field50"));
		testMsg.set(51, object2.getString("Field51"));
		testMsg.set(53, object2.getString("Field53"));
		testMsg.set(100, object2.getString("Field100"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
}
	public byte[] ISORevTransactionReq(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
	
		try{
			reqMsg=createRevISOMessage(object2);   
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Reverse_Transaction_Request"+Runtime.logISOMsg(reqMsg));
				reqMsg.pack();
				Socket s=new Socket(ip,port);  
				DataOutputStream dout=new DataOutputStream(s.getOutputStream());
				dout.writeUTF(new String(reqMsg.pack()));  
				dout.flush();  
				dout.close();  
				s.close();  
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Magic Transaction Request Sent over 6666 port..\n"+ip+" "+port+Runtime.logISOMsg(reqMsg));
			return (Runtime.logISOMsg(reqMsg)).getBytes();
			} catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Magic Transaction Request Sent over 6666 port error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public ISOMsg createRevISOMessage(JSONObject object2) throws JSONException, ISOException {
		
		
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(11, object2.getString ("Field11"));
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(25, object2.getString("Field25"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(30, object2.getString ("Field4"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, object2.getString ("Field37"));
		testMsg.set(41, object2.getString("Field41"));
		testMsg.set(42, object2.getString("Field42"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(53, object2.getString("Field53"));
		testMsg.set(56, object2.getString("Field56"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
		
	
	}
	public byte[] ISORevTransactionResp(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
		
		try{
			reqMsg=createRevPaymentResp(object2);  
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Reverse_Transaction_Response"+Runtime.logISOMsg(reqMsg));
			return  new String(reqMsg.pack()).getBytes();
			} 
		catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Reverse_Transaction_Response Error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public static ISOMsg createRevPaymentResp(JSONObject object2)
			throws ISOException {
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(5, object2.getString ("Field5"));
		testMsg.set(6, object2.getString ("Field6"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(10, object2.getString ("Field10"));
		testMsg.set(11, object2.getString ("Field11"));
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(28, object2.getString("Field28"));
		testMsg.set(30, object2.getString("Field30"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, object2.getString ("Field37"));
		testMsg.set(39, object2.getString("Field39"));
		testMsg.set(43, object2.getString("Field43"));
		testMsg.set(48, object2.getString("Field48"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(50, object2.getString("Field50"));
		testMsg.set(51, object2.getString("Field51"));
		testMsg.set(53, object2.getString("Field53"));
		testMsg.set(56, object2.getString("Field56"));
		testMsg.set(100, object2.getString("Field100"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
}
	public byte[] ISOBalanceInquiry(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
		try{
			reqMsg=createBalanceInquiryReq(object2);   
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Balance_Inquiry_Request"+Runtime.logISOMsg(reqMsg));
				reqMsg.pack();
				Socket s=new Socket(ip,port);  
				DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
				dout.writeUTF(new String(reqMsg.pack()));  
				dout.flush();  
				dout.close();  
				s.close();  
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Magic Transaction Request Sent over 6666 port..\n"+ip+" "+port+Runtime.logISOMsg(reqMsg));
			return (Runtime.logISOMsg(reqMsg)).getBytes();
			} catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Magic Transaction Request Sent over 6666 port error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public ISOMsg createBalanceInquiryReq(JSONObject object2) throws JSONException, ISOException {
		
		
			
			String f11=new DecimalFormat("000000").format(new Random().nextInt(999999));
			String f37 = "000000".concat(f11);
			System.out.println("Filed-11: FMB"+ f11);
			System.out.println("Filed-37: FMB"+ f37);
			
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(11, f11);
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, f37);
		testMsg.set(41, object2.getString("Field41"));
		testMsg.set(42, object2.getString("Field42"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
		
	}
	public byte[] ISOBalanceInquiryResp(JSONObject object2, String ip, int port) {
		byte[] Response = null;
		ISOMsg reqMsg=null;
	
		try{
			reqMsg=createBalanceInqResp(object2); 
			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Balance_Inquiry_Response"+Runtime.logISOMsg(reqMsg));
			return  new String(reqMsg.pack()).getBytes();
			} 
		catch (Exception e) {
			Response=Runtime.StackTraceToString(e).getBytes();
			Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Balance_Inquiry_Response Error..\n"+ip+" "+port+Runtime.StackTraceToString(e));
			return Response;
		}
	}
	public static ISOMsg createBalanceInqResp(JSONObject object2)
			throws ISOException {
		String Field54=object2.getString("Field54");
		String Amount=Field54.substring(9,Field54.length());
		String F54 = Field54.substring(0,9)+String.format("%012d", Integer.parseInt(Amount));
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93CPackager());
		testMsg.setMTI(object2.getString("MTI"));
		testMsg.setHeader(object2.getString("Header").getBytes());
		testMsg.set(2, object2.getString ("Field2"));
		testMsg.set(3, object2.getString ("Field3"));
		testMsg.set(4, object2.getString ("Field4"));
		testMsg.set(5, object2.getString ("Field5"));
		testMsg.set(7, object2.getString ("Field7"));
		testMsg.set(11, object2.getString ("Field11"));
		testMsg.set(12, object2.getString("Field12"));
		testMsg.set(17, object2.getString("Field17"));
		testMsg.set(19, object2.getString("Field19"));
		testMsg.set(22, object2.getString("Field22"));
		testMsg.set(24, object2.getString("Field24"));
		testMsg.set(26, object2.getString("Field26"));
		testMsg.set(28, object2.getString("Field28"));
		testMsg.set(32, object2.getString("Field32"));
		testMsg.set(37, object2.getString("Field37"));
		testMsg.set(39, object2.getString("Field39"));
		testMsg.set(43, object2.getString("Field43"));
		testMsg.set(48, object2.getString("Field48"));
		testMsg.set(49, object2.getString("Field49"));
		testMsg.set(50, object2.getString("Field50"));
		testMsg.set(53, object2.getString("Field53"));
		testMsg.set(54, F54);
		testMsg.set(100, object2.getString("Field100"));
		testMsg.set(102, object2.getString("Field102"));
		testMsg.set(103, object2.getString("Field103"));
		return testMsg;
}
}
