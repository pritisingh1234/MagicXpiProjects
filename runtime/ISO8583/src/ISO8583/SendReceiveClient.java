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
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.SerializationUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;


public class SendReceiveClient {
	
	

	public static ISOMsg echoTest(String MTI,String Header,String f7,String f11, String f24,String f28, String f93, String f94)
				throws ISOException {
		f11=new DecimalFormat("000000").format(new Random().nextInt(999999));
		System.out.println("Filed-11:"+ f11);
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
	
	public static ISOMsg echoTestResp(String ISOReq)
			throws ISOException, IOException {
	ISOMsg testMsg = new ISOMsg();
	testMsg.setPackager(new ISO93BPackager());
	
	String str[]=new String(ISOReq).split("&");
	
	testMsg.setMTI("1814");
	testMsg.setHeader((Runtime.object.getString("Header")).getBytes());
	testMsg.set(7, 	str[1].substring(8) );
	testMsg.set(11, str[2].substring(9));
	testMsg.set(24, str[3].substring(9));
	testMsg.set(28, str[4].substring(9));
	testMsg.set(39, "0800");
	testMsg.set(93, str[5].substring(9));
	testMsg.set(94, str[6].substring(9));
	return testMsg;
}
	
	static String logISOMsg(ISOMsg msg) throws IOException {
		String Response = "";

		try {
			
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
