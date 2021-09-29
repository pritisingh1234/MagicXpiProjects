package ISO8583;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.company.converter.ISOConverter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

public class iso_FMB {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		byte[] keyBytes = Files.readAllBytes(new File("1.txt").toPath());
		byte[] b = new String(keyBytes).getBytes("ASCII");
		System.out.println(new String(b, "ASCII"));
		
		System.out.println(ISOUtil.hexString(keyBytes));
		
		System.exit(0);
	    String isohexmsg="31383034201a200110202020202020200c2020202030393232313330323437313233343536303830313139303532303130313030303030303030303036303030303238";
		iso_FMB sb=new iso_FMB();
		//byte[] res=sb.getISOResponse("1804",isohexmsg);
		//System.out.println(new String(res));
	}
	/*public byte[] getISOResponse(String mti, String isohexmsg){
		byte[] res=null;
		try {
			ISOConverter isoConv = new ISOConverter();
			byte[] b = ISOUtil.hex2byte(isohexmsg);
			String xmlMsg;
			xmlMsg = isoConv.rawMsgToXmlMsg(b, true);
			String response=testXmlStringtoISOBytes(xmlMsg,mti);
			res=response.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	*/

	public String testXmlStringtoISOBytes(String xmlISO, String mti) {
		ISOConverter isoConv = new ISOConverter();
		String xmlMsg=null;
		byte[] isoMsg=null;
		try {
			String xmlStr = xmlISO;
			isoMsg = isoConv.xmlToRawIsoMsg(xmlStr.getBytes(),mti);
		} catch (Exception e) {
			e.printStackTrace();
		}
		xmlMsg=new String((isoMsg));
		return xmlMsg;
	}
	public static String logISOMsg(ISOMsg msg)
	  {
	    String Response = "";
	    try
	    {
	      Response = Response + "MTI:" + msg.getMTI() + "&";
	      for (int i = 1; i <= msg.getMaxField(); i++) {
	        if (msg.hasField(i)) {
	          Response = Response + "Field-" + i + ":" + msg.getString(i) + "&";
	        }
	      }
	    }
	    catch (ISOException e)
	    {
	      e.printStackTrace();
	    }
	    return Response;
	  }
	
	
}
