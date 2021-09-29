package ISO8583;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.SerializationUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;

public class SendReceiveClient {
	
	SimpleDateFormat df = null;
	static int cnt=1;
	static String filePath="E:\\AuditLog\\Ramesh\\";
	static ISO93BPackager pack = new ISO93BPackager();
	static NACChannel asciiChnl = null;
	//NACChannel asciiChnl = new NACChannel("10.50.8.141",5012, pack);
	String IP="";
	public static void main(String[] args) {
		SendReceiveClient iso_object = new SendReceiveClient();
		 //iso_object.IsoRequest("10.50.8.141", 18080, "4728200031510083",
		// "000300035080000602", "767676", "2207", "0613171922",
		// "190613171922");jjj
		//String Resp = iso_object.Bank2Wallet("10.50.8.141", 58550, "ISO8583-1993021000000", "9876543210", "4000",
			//	"5271"s, "000001000000", "11Lokesh ABCD015Pune Lokhanwala", "000006", "1000000000");
		String f11=new DecimalFormat("000000").format(new Random().nextInt(999999));
		byte[] Resp=iso_object.echoTesting("10.50.8.141",58550,"1804","ISO8583-1993021000000", "1014135643", f11, "0801", "201014", "1000000000", "000028");
		//logISOMsg(is);
		//byte[] Resp=iso_object.echoTestingResponse("10.50.8.141",58550,"1814","ISO8583-1993021000000", "1014135643", "068388", "0831", "201014","0800", "000028", "1000000000");
		try{
		System.out.println("Resp:" + new String(Resp));}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*static{
		pack=new ISO93BPackager();
		asciiChnl = new NACChannel("10.23.223.101",5012, pack);
	}*/
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
	public byte[] echoTesting(String HostIp, int HostPort,String MTI,String Header,String f7,String f11, String f24,String f28, String f93, String f94) {
		String Response = "";
		df = new SimpleDateFormat("YYYYMMddHHmmss");
		String timestamp1 = df.format(new Date());
		//f11=new DecimalFormat("000000").format(new Random().nextInt(999999));

		try {
			Custom_Logs.WriteLogs("Echo Request Started..");
			ISOMsg reqMsg = echoTest(MTI,Header, f7, f11, f24, f28, f93, f94);
			pack = new ISO93BPackager();
			Custom_Logs.WriteLogs("Created ISO Request :\n"+new String(reqMsg.pack()));
			
			//asciiChnl = new NACChannel(HostIp, Math.round(HostPort), pack);
			//asciiChnl.connect();
			//asciiChnl.send(reqMsg);
			
			
			FileWriter writerMgc = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\Magic\\MagicRequest_"+reqMsg.getMTI()+timestamp1+".txt"); 
			writerMgc.write(logISOMsg(reqMsg)); 
			writerMgc.flush();
			writerMgc.close();
			Custom_Logs.WriteLogs("Magic Echo Request..\n"+logISOMsg(reqMsg));
			/*FileWriter writerMgc2 = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\Magic\\RequestHexDump_"+f11+"_"+reqMsg.getMTI()+timestamp1+cnt+".txt"); 
			writerMgc2.write(ISOUtil.hexdump(reqMsg.pack())); 
			writerMgc2.flush();
			writerMgc2.close();
			Custom_Logs.WriteLogs("Magic Echo Request..\n"+logISOMsg(reqMsg));
			Custom_Logs.WriteLogs("Hexa Dump of ISO Request :\n"+new String(ISOUtil.hexdump(reqMsg.pack())));*/
			
			asciiChnl=isChannelConnected(HostIp, HostPort, pack);
			asciiChnl.send(reqMsg);
			while (asciiChnl.isConnected()) {
				Custom_Logs.WriteLogs("while (asciiChnl.isConnected()).."+asciiChnl.isConnected());
				SimpleDateFormat df = new SimpleDateFormat("YYYYMMddHHmmss");
				String timestamp = df.format(new Date());
				ISOMsg respMsg = asciiChnl.receive();
				Custom_Logs.WriteLogs("Switch incomming request/response MTI..\n"+respMsg.getMTI());
				Custom_Logs.WriteLogs("Switch incomming request/response..\n"+logISOMsg(respMsg));
				Custom_Logs.WriteLogs("Switch incomming request/response hex dump..\n"+ISOUtil.hexdump(respMsg.pack()));
				
				Response = logISOMsg(respMsg);
				if(respMsg.getMTI().equals("1814"))
				{
					FileWriter writer1 = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\Response\\Switch_Response"+respMsg.getMTI()+timestamp+cnt+".txt"); 
					writer1.write(Response); 
					writer1.flush();
					writer1.close();
				}
				else
				{
					FileWriter writer = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\Request\\Switch_Request"+respMsg.getMTI()+timestamp+cnt+".txt"); 
				      writer.write(logISOMsg(respMsg)); 
				      writer.flush();
				      writer.close();
				}
				cnt++;
			}
			System.out.print(Response);
		} catch (Exception e) {
			Response=e.toString();
			e.printStackTrace();
		}
		return Response.getBytes();
	}
	
	
	
	
	private NACChannel isChannelConnected(String hostIp, int port, ISO93BPackager pack) {
	try {
			//asciiChnl = new NACChannel(hostIp, port, pack);
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

	public static ISOMsg echoTest(String MTI,String Header,String f7,String f11, String f24,String f28, String f93, String f94)
				throws ISOException {
		//f11=new DecimalFormat("000000").format(new Random().nextInt(999999));
		System.out.println("Filed-11: FMB"+ f11);
		//f11 = "995762";

		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93BPackager());
		testMsg.setMTI(MTI);
		testMsg.setHeader(Header.getBytes());
		testMsg.set(7, f7 );
		testMsg.set(11, f11);
		testMsg.set(24, f24);
		testMsg.set(28, f28);
		testMsg.set(93, f93);
		testMsg.set(94, f94);
		
		System.out.println("Sign on Req Hex Dump "+ISOUtil.hexdump(testMsg.pack()));
		return testMsg;
	}
	
	
	public static ISOMsg echoTestResp(String MTI,String Header,String f7,String f11, String f24,String f28,String f39, String f93, String f94)
			throws ISOException {
	ISOMsg testMsg = new ISOMsg();
	testMsg.setPackager(new ISO93BPackager());
	testMsg.setMTI(MTI);
	testMsg.setHeader(Header.getBytes());
	testMsg.set(7, f7 );
	testMsg.set(11, f11);
	testMsg.set(24, f24);
	testMsg.set(28, f28);
	testMsg.set(39, f39);
	testMsg.set(93, f93);
	testMsg.set(94, f94);
	

	return testMsg;
}
	
	public byte[] createPayment(String HostIp, int HostPort,String MTI,String Header,String f2,String f3, String f4,String f7,String f11, String f12, String f17, String f19, String f22, String f24, String f26, String f32, String f37, String f39, String f41, String f42, String f49, String f102, String f103) {
		String Response = "";
		try {
			Custom_Logs.WriteLogs("createPayment() called..\n");
			ISOMsg reqMsg = createPaymentResp( MTI, Header, f2, f3,  f4, f7, f11,  f12,  f17,  f19,  f22,  f24,  f26,  f32,  f37, f39,  f41,  f42,  f49,  f102,  f103);
			pack = new ISO93BPackager();
			Custom_Logs.WriteLogs("createPayment Request..\n"+logISOMsg(reqMsg));
			asciiChnl=isChannelConnected(HostIp, HostPort, pack);
			asciiChnl.send(reqMsg);
			while (asciiChnl.isConnected()) {
				Custom_Logs.WriteLogs("asciiChnl.isConnected()..\n"+asciiChnl.isConnected());
				df = new SimpleDateFormat("YYYYMMddHHmmss");
				String timestamp = df.format(new Date());
				ISOMsg respMsg = asciiChnl.receive();
				Custom_Logs.WriteLogs("createPayment switch in comming data..\n"+logISOMsg(respMsg));
				Custom_Logs.WriteLogs("createPayment switch in comming data..\n"+respMsg.getMTI());
				System.out.println("Lokesh respMsg " + respMsg.getMTI());
				cnt++;
				System.out.println("Connection Status : " + asciiChnl.isConnected() );
				Response = logISOMsg(respMsg);
				if(respMsg.getMTI().equals("1814"))
				{
					FileWriter writer1 = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\SignOn\\Response\\MyFileISO"+respMsg.getMTI()+timestamp+cnt+".txt"); 
					writer1.write(logISOMsg(respMsg)); 
					writer1.flush();
					writer1.close();
				}
				else
				{
					FileWriter writer = new FileWriter("E:\\Workspace\\IsoXmlApi\\Logs\\SignOn\\Request\\MyFileISO"+respMsg.getMTI()+timestamp+cnt+".txt"); 
				      writer.write(logISOMsg(respMsg)); 
				      writer.flush();
				      writer.close();
				}
			}
			System.out.print(Response);
		} catch (Exception e) {
			Response=StackTraceToString(e);
		}
		return Response.getBytes();
	}
	
	public static ISOMsg createPaymentResp(String MTI,String Header,String f2,String f3, String f4,String f7,String f11, String f12, String f17, String f19, String f22, String f24, String f26, String f32, String f37,String f39 , String f41, String f42, String f49, String f102, String f103)
			throws ISOException {
	ISOMsg testMsg = new ISOMsg();
	testMsg.setPackager(new ISO93BPackager());
	testMsg.setMTI(MTI);
	testMsg.set(2, f2 );
	testMsg.set(3, f3);
	testMsg.set(4, f4);
	testMsg.set(7, f7);
	testMsg.set(11, f11);
	testMsg.set(12, f12);
	testMsg.set(17, f17);
	testMsg.set(19, f19 );
	testMsg.set(22, f22);
	testMsg.set(24, f24);
	testMsg.set(26, f26);
	testMsg.set(32, f32);
	testMsg.set(37, f37);
	testMsg.set(39, f39);
	testMsg.set(41, f41);
	testMsg.set(42, f42 );
	testMsg.set(49, f49);
	testMsg.set(102, f102);
	testMsg.set(103, f103);
	return testMsg;
}

	public static ISOMsg createPaymentReq(String FixedHeader, String MobileNumber, String ProcessingCode,
			String BusinessType, String Amount, String Name_Address, String SrcInstCode, String DstInstCode)
			throws ISOException {
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93BPackager());
		testMsg.setMTI("1804");
		testMsg.setHeader(FixedHeader.getBytes());
		testMsg.set(2, SrcInstCode + MobileNumber);
		testMsg.set(3, ProcessingCode);
		testMsg.set(4, Amount);
		testMsg.set(5, DstInstCode);
		testMsg.set(26, BusinessType);
		testMsg.set(32, SrcInstCode);
		testMsg.set(33, DstInstCode);
		//testMsg.set(48, Name_Address);
		testMsg.set(93, DstInstCode);
		testMsg.set(94, SrcInstCode);
		testMsg.set(100, SrcInstCode);
		testMsg.set(103, SrcInstCode + MobileNumber);

		return testMsg;
	}

	public static ISOMsg createPaymentReq1(String FixedHeader, String MobileNumber, String ProcessingCode,
			String BusinessType, String Amount, String Name_Address, String SrcInstCode, String DstInstCode)
			throws ISOException, IOException {
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93BPackager());
		//testMsg.setMTI(new String(ISOUtil.hex2byte("1100")));
		testMsg.setMTI("1100");
		testMsg.setHeader(FixedHeader.getBytes());
		testMsg.set(2, SrcInstCode + MobileNumber);
		testMsg.set(3, ProcessingCode);
		testMsg.set(4, Amount);
		testMsg.set(5, DstInstCode);
		testMsg.set(26, BusinessType);
		testMsg.set(32, SrcInstCode);
		testMsg.set(33, DstInstCode);
		//testMsg.set(48, Name_Address);
		testMsg.set(93, DstInstCode);
		testMsg.set(94, SrcInstCode);
		testMsg.set(100, SrcInstCode);
		testMsg.set(103, SrcInstCode + MobileNumber);
		/*testMsg.setPackager(new ISO93BPackager());
		testMsg.setMTI(new String(ISOUtil.hex2byte("1100")));
		//testMsg.setMTI("1100");
		testMsg.setHeader(FixedHeader.getBytes());
		testMsg.set(2, new String(ISOUtil.hex2byte(SrcInstCode + MobileNumber)));
		testMsg.set(3, new String(ISOUtil.hex2byte(ProcessingCode)));
		testMsg.set(4, new String(ISOUtil.hex2byte(Amount)));
		testMsg.set(5, new String(ISOUtil.hex2byte(DstInstCode)));
		testMsg.set(26, new String(ISOUtil.hex2byte(BusinessType)));
		testMsg.set(32, new String(ISOUtil.hex2byte(SrcInstCode)));
		testMsg.set(33, new String(ISOUtil.hex2byte(DstInstCode)));
		testMsg.set(48, Name_Address);
		testMsg.set(93, new String(ISOUtil.hex2byte(DstInstCode)));
		testMsg.set(94, new String(ISOUtil.hex2byte(SrcInstCode)));
		testMsg.set(100, new String(ISOUtil.hex2byte(SrcInstCode)));
		testMsg.set(103, new String(ISOUtil.hex2byte(SrcInstCode + MobileNumber)));*/
		logISOMsg(testMsg);
		return testMsg;
	}

	/*public String Bank2Wallet(String HostIp, int HostPort, String FixedHeader, String MobileNumber,
			String ProcessingCode, String BusinessType, String Amount, String Name_Address, String SrcInstCode,
			String DstInstCode) {
		String Response = "";
		/////
		ISOMsg xmlMsg = null;
		byte[] finalByteResp = null;
		byte[] xmlMsg1 = null;

		try {
			ISOMsg reqMsg = createPaymentReq1(FixedHeader, MobileNumber, ProcessingCode, BusinessType, Amount,
					Name_Address, SrcInstCode, DstInstCode);
			ISO93BPackager pack = new ISO93BPackager();
			//logISOMsg(reqMsg);
			// ASCIIChannel asciiChnl = new ASCIIChannel(HostIp,
			// Math.round(HostPort), pack);
			
			
            
            NACChannel asciiChnl = new NACChannel(HostIp, Math.round(HostPort), pack);
			asciiChnl.connect();
			
			//logISOMsg(reqMsg);
			asciiChnl.send(reqMsg);
			while (asciiChnl.isConnected()) {
				ISOMsg respMsg = asciiChnl.receive();
				System.out.println("Lokesh respMsg " + respMsg.getMTI());
				//logISOMsg(respMsg);
				//asciiChnl.disconnect();
				System.out.println("Connection Status : " + asciiChnl.isConnected() );
				Response = logISOMsg(respMsg);
			}
			System.out.print(Response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response;
	}*/
	public String IsoRequest(String HostIp, int HostPort, String PAN, String AccountId, String RadomeKey,
			String Expiry_Date, String Trnasmission_DnT, String LocalTrnasmission_DnT) {
		String Response = "";
		try {
			ISOMsg reqMsg = createIsoMessage(PAN, AccountId, RadomeKey, Expiry_Date, Trnasmission_DnT,
					LocalTrnasmission_DnT);
			ISO93BPackager pack = new ISO93BPackager();
			//ASCIIChannel asciiChnl = new ASCIIChannel(HostIp, Math.round(HostPort), pack);
			NACChannel asciiChnl = new NACChannel(HostIp, Math.round(HostPort), pack);
			asciiChnl.connect();

			asciiChnl.send(reqMsg);
			while (asciiChnl.isConnected()) {
				ISOMsg respMsg = asciiChnl.receive();

				asciiChnl.disconnect();
				Response = logISOMsg(respMsg);
			}
			System.out.print(Response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response;
	}

	public static ISOMsg createIsoMessage(String PAN, String AccountId, String RandomKey, String Expiry_Date,
			String Trnasmission_DnT, String LocalTrnasmission_DnT) throws ISOException {
		ISOMsg testMsg = new ISOMsg();
		testMsg.setPackager(new ISO93BPackager());
		testMsg.setMTI("0360");
		testMsg.set(2, PAN);
		testMsg.set(3, "900000");
		testMsg.set(7, Trnasmission_DnT);

		testMsg.set(11, RandomKey);
		testMsg.set(12, LocalTrnasmission_DnT);
		testMsg.set(14, Expiry_Date);

		testMsg.set(41, "103004  ");
		testMsg.set(42, "103002         ");

		testMsg.set(102, AccountId);

		return testMsg;
	}
	private static ISOMsg createISOMsg1() throws Exception {
		//Raw message
		//String msg = "3131303036323343363030303238433031303031313634343132333431323132333431323334333130303030323630313039313234353236313132323030323630313039313234353031313931323232363031343335363336383838383838313031303030303633393D3139313231303131313130303030303030303034343231323332363131323231323334353637382020202020303132333435363738393234423337303038453135334544453446393937424433453736333736383032";
		ISOMsg xmlISOMsg = new ISOMsg();
		xmlISOMsg.setPackager(new ISO93APackager());
		xmlISOMsg.setMTI("1100");
		xmlISOMsg.set(2, "4412341212341234");
		xmlISOMsg.set(3, "310000");
		xmlISOMsg.set(7, "2601091245");
		xmlISOMsg.set(11, "261122");
		xmlISOMsg.set(12, "2601091245");
		xmlISOMsg.set(13, "0119");
		xmlISOMsg.set(14, "1222");
		xmlISOMsg.set(18, "6014");
		xmlISOMsg.set(19, "356");
		xmlISOMsg.set(35, "8888881010000639=1912101111000000000");
		xmlISOMsg.set(37, "442123261122");
		xmlISOMsg.set(41, "12345678");
		xmlISOMsg.set(42, "     0123456789");
		xmlISOMsg.set(52, ISOUtil.hex2byte("24B37008E153EDE4"));
		xmlISOMsg.set(64, ISOUtil.hex2byte("F997BD3E76376802"));
		return xmlISOMsg;
	}
	static String logISOMsg(ISOMsg msg) throws IOException {
		String Response = "";

		try {
			/*System.out.println("ISO ASCII Representation : " + new String(msg.pack()));
			//
			   
		           FileWriter fw=new FileWriter("ISORes.txt");    
		           fw.write(new String(msg.pack()));    
		           fw.close();    */
		             
		         
			//
			System.out.println("Header:" + new String(msg.getHeader())+ "&");
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
	public String StackTraceToString(Exception err) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		err.printStackTrace(pw);
		return sw.toString();
	}
}
