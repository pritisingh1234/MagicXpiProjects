package ISO8583;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import org.company.common.Base64Coder;
import org.company.converter.ISOConverter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOHeader;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.header.BaseHeader;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

/**
 * @author
 * @since
 * 		<li>This is the test class for testing all methods of ISOConverter</li>
 */
public class TestConverters {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testISOByteArraytoXmlString();
		//testXmlStringtoISOBytes();
		//testISOBase64toXmlString();
		//testXmlStringtoISOInBase64();
		try {
			ISOConverter isoConv = new ISOConverter();
			//String isohexmsg = "";
			//String isohexmsg = "3033343331323030f030810180a4800000000000060000363136353034393230323030363138393333303430303030333030303030303030303030333030303035343730303620202020202032303139303133313039303035393230313930313331323030303230333032303331303330303320202020202020202020343055535344204d52432020202020202020202020202020202020466972737420486f75736520204d5730353037304d574b4430303030303030303030303030303030303030303030303144303030303030303030303030303030304d574b4d574b3333032373231323030b0308101400080000000000004000028393330303030303030303030303030303030303030303534383836343136383736363230313930313330323030323438323031393031333032303030323233303343524d494e523338323320202020202020202030323253504945523031313030303833343230302020202020202030303343524d3133323139303030313031323039393132333130303030303030303030303030302e303030303030303030303030303030302e30302020202020202020202020202020202020202020202020202020202020202020352044422020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202023033202020202020202020303230202020202030303230353033303132363134333720203033202020202020202020333030202020202030333030304d574b3438303430303535303033434d4e3030334d4f4230323542494c4c502d41495254454c50522d3039393433333235303430303841495254454c5052";
			
			//String isohexmsg = "3039343931323130b0308001460180000000000000000028393330303030303030303030303030303030303030303534383836343136383736363230313930313330323030323438323031393031333030323233303343524d554e493030303030303130322b303030303030303037393939393830302b303030303030303037393939393830302b303030303030303030303030303030302b303030303030303030303030303030302b303030303030303037393939393830304d55522020202020202020202020202020494e5230303343524d37333859303532303139303132332020202020444331362020203154434944323031393031323920202020202020202020202020312e30304d42205452414e53464552202d20303630313030303331373333204c2020202020202020202020202020202020202020202032303139303132393136333131392020202020202020202020202020202020202020202020203739393939382e303032303139303132332020202020444331352020203154434944323031393031323920202020202020203230303030312e30304d42205452414e53464552202d2054455354313131313131313131312020202020202020202020202020202020202020202032303139303132393136313231302020202020202020202020202020202020202020202020203739393939392e303032303139303132332020202020444331312020203354434943323031393031323320202020202020313030303030302e30305452414e5346455220202020202020202020202020202020202020202020202020202020202020202020202020202020202032303139303132383137333633372020202020202020202020202020202020202020202020313030303030302e303032303137303430332020533438393631302020203154434944323031373034303220202020202020202020202020372e37354d422d43726564697420546f203036303130303034353836302020202020202020202020202020202020202020202020202032303137303430323230333731322020202020202020202020202020202020202020202020202020202020302e303032303136313133302020444331323335312031323354495043323031363131333020202020202020202020202020372e3735303131303030383334323030496e742e506430312d30362d3230313620546f2033302d31312d32303136202020202020202032303136313133303230343531312020202020202020202020202020202020202020202020202020202020372e3735";
			
			// request 1
			//String isohexmsg = "3032373231323030b0308101400080000000000004000028393330303030303030303030303030303030303030303534383836343136383736363230313930313330323030323438323031393031333032303030323233303343524d494e523338323320202020202020202030323253504945523031313030303833343230302020202020202030303343524d3133323139303030313031323039393132333130303030303030303030303030302e303030303030303030303030303030302e303020202020202020202020202020202020202020202020202020202020202020203520444220202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020";
			// request 2
			//String isohexmsg = "3039343931323130b0308001460180000000000000000028393330303030303030303030303030303030303030303534383836343136383736363230313930313330323030323438323031393031333030323233303343524d554e493030303030303130322b303030303030303037393939393830302b303030303030303037393939393830302b303030303030303030303030303030302b303030303030303030303030303030302b303030303030303037393939393830304d55522020202020202020202020202020494e5230303343524d37333859303532303139303132332020202020444331362020203154434944323031393031323920202020202020202020202020312e30304d42205452414e53464552202d20303630313030303331373333204c2020202020202020202020202020202020202020202032303139303132393136333131392020202020202020202020202020202020202020202020203739393939382e303032303139303132332020202020444331352020203154434944323031393031323920202020202020203230303030312e30304d42205452414e53464552202d2054455354313131313131313131312020202020202020202020202020202020202020202032303139303132393136313231302020202020202020202020202020202020202020202020203739393939392e303032303139303132332020202020444331312020203354434943323031393031323320202020202020313030303030302e30305452414e5346455220202020202020202020202020202020202020202020202020202020202020202020202020202020202032303139303132383137333633372020202020202020202020202020202020202020202020313030303030302e303032303137303430332020533438393631302020203154434944323031373034303220202020202020202020202020372e37354d422d43726564697420546f203036303130303034353836302020202020202020202020202020202020202020202020202032303137303430323230333731322020202020202020202020202020202020202020202020202020202020302e303032303136313133302020444331323335312031323354495043323031363131333020202020202020202020202020372e3735303131303030383334323030496e742e506430312d30362d3230313620546f2033302d31312d32303136202020202020202032303136313133303230343531312020202020202020202020202020202020202020202020202020202020372e3735";
			 //String isohexmsg = "3032363231323030f030810180a4800000000000040000303136353034393230303032303136343032343031303030303030303030303030303030313030303031323334353620202020202032303137303531393138333835343230313830313031323030303230333032303330303733303030352020202020202020343041544d2034204154204254204252414e434820202020202020424c4e202020202020202020204d5730353037304d574b4430303030303030303030303132303030303030303030303144303030303030303030303031323030304d574b4d574b32393033202020202020202020313330202020202035313330363931313131303033434d4e30303341544d";
			
			byte[] keyBytes = Files.readAllBytes(new File("4.txt").toPath());
			 
		        //String isohexmsg=ISOUtil.hexString(keyBytes);
		      // String isohexmsg="31383034201a200110202020202020200c2020202030393232313330323437313233343536303830313139303532303130313030303030303030303036303030303238";
			 //String isohexmsg="31383034201a200110202020202020200c2020202030393232313330323437313233343536303830313139303532303130313030303030303030303036303030303238";
			//String isohexmsg="180482200110000000000000000C00000000100511015477066608312009140A100000000006000028";
			
			byte[] res=getISOResponse("1814",keyBytes,2,true,"ISO8583-1993001000000");
			System.out.println("Response : "+new String(res));
			/*System.out.println(new String(ISOUtil.hex2byte("82200110000000000000000C00000000")));
			 String newmsg="1804"+new String(ISOUtil.hex2byte("82200110000000000000000C00000000"))+"1005110154770666083120091410100000000006000028";
			System.out.println("Len:"+newmsg.length());
			System.out.println("Hex String : " + newmsg);
			String xmlMsg;
			xmlMsg = isoConv.rawMsgToXmlMsg(newmsg.getBytes(), true);
			System.out.println(xmlMsg);
			System.out.println(new String(ISOUtil.hex2byte("1814")));
			System.out.println(new String(ISOUtil.hex2byte("10051101547706660831200914080010100000000006000028")));*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] getISOResponse(String mti, byte[] isoRequestMsg, int msgLenType, boolean isLenAvail, String FixedHeader){
		ISOMsg xmlMsg=null;
		byte[] finalByteResp =null;
		byte[] xmlMsg1=null;
		try {
			ISOConverter isoConv = new ISOConverter();

				isoRequestMsg=new String(isoRequestMsg).substring(new String(isoRequestMsg).indexOf("ISO8583")).replace(FixedHeader, "").getBytes();
				System.out.println("isoRequestMsg : "+new String(isoRequestMsg));
				//String hexIsoMsg=(((((ISOUtil.hexString(isoRequestMsg).replace("0A", "10")).replace("0B", "11")).replace("0C", "12")).replace("0D", "13")).replace("0E", "14")).replace("0F", "15");
				String hexIsoMsg=(ISOUtil.hexString(isoRequestMsg).replace("0A", "10"));
				//String hexIsoMsg="180482200110000000000000000C000000001005141732173524080219052010100000000006000028";
				System.out.println("hexIsoMsg : "+new String(hexIsoMsg));
				String bitMap=hexIsoMsg.substring(4,36);
				System.out.println("Request Bitmap :"+bitMap);
				byte[] bitMaptoBinary=ISOUtil.hex2byte(bitMap);
				int len=new String(mti+new String(bitMaptoBinary)+hexIsoMsg.substring(36,hexIsoMsg.length())).length();
				String newmsg="00"+len+mti+new String(bitMaptoBinary)+hexIsoMsg.substring(36,hexIsoMsg.length());
				System.out.println("newmsg : " + newmsg);
	
				xmlMsg1 = isoConv.rawMsgToXmlMsg(newmsg.getBytes(), isLenAvail);
				xmlMsg1=(new String(xmlMsg1).substring(0,50)+"0A"+new String(xmlMsg1).substring(52,xmlMsg1.length)).getBytes();
				System.out.println("xmlMsg1: " + new String(xmlMsg1));
				
				String resData=new String(ISOUtil.hex2byte(new String(xmlMsg1).substring(20)));
				System.out.println("resData: " + resData);
				byte[] data= (FixedHeader + new String(xmlMsg1).substring(0,20)).getBytes();
				System.out.println("Response bit map : "+new String(data).substring(25,41));
				System.out.println("Response bit map"+ISOUtil.hexString((new String(data).substring(25,41)).getBytes()));
		      //  int len=data.length;
				
					
				//String finalRes=new String(ISOUtil.hex2byte(""+data.length))+(new String(xmlMsg1).replace(mti, new String(ISOUtil.hex2byte(mti)))).replace(resData, new String(ISOUtil.hex2byte(resData)));
				String finalRes=(new String(data).replace(mti, new String(ISOUtil.hex2byte(mti)))+resData);

				//String finalRes = (new String(data).replace(mti, new String(ISOUtil.hex2byte(mti)))).replace(resData, new String(ISOUtil.hex2byte(resData)));
				//System.out.println("finalRes: " + finalRes);
				
			// CHanges for header length
				//byte[] data2= (FixedHeader + new String(finalRes)).getBytes();
				byte[] data2=new String(finalRes).getBytes();
				short messageLength = (short)data2.length;
				//System.out.println(messageLength-msgLenType);
	            messageLength=(short) (messageLength-msgLenType);
	            //System.out.println("Msg Len:"+messageLength);
	            ByteBuffer bb = ByteBuffer.allocate(msgLenType);
	            bb.putShort(messageLength);
	            
	            byte[] headerArr1 = bb.array();
	            byte fByte = headerArr1[0];
	            byte sByte = (byte) (headerArr1[1] + (byte)2);
	            byte[] headerArr2 = new byte[2];
	            headerArr2[0] = fByte;
	            headerArr2[1] = sByte;
	            BaseHeader bh = new BaseHeader(headerArr2);
	            xmlMsg=createISOMsg1();
	            xmlMsg.setHeader(bh);
				//System.out.println("Header L:"+new String(xmlMsg.getHeader()));
				finalByteResp=(new String(xmlMsg.getHeader())+finalRes).getBytes();
				//System.out.println("Final123132:"+finalRes);
		//cahnges end here
				
				
				
				//System.out.println(new String(ISOUtil.hex2byte("10051417321735240802190520080010100000000006000028")));
				//finalByteResp=finalRes.getBytes();
		} catch (Exception e) {
			finalByteResp=e.toString().getBytes();
		}
		return finalByteResp;
	}
	

	/*private static void testISOByteArraytoXmlString() {
		ISOConverter isoConv = new ISOConverter();
		try {
		
			byte[] b = createISOMsg();
			System.out.println("Original String : " + new String(b));
			System.out.println("Hex String : " + ISOUtil.hexString(b));
			String xmlMsg = isoConv.rawMsgToXmlMsg(b, false);
			System.out.println(xmlMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	public String Unpack(String message)
	  {
	    String Result = null;
	    Logger l = new Logger();
	    l.addListener(new SimpleLogListener());
	    ISOMsg m = new ISOMsg();
	    try
	    {
	    	ISO93BPackager p = new ISO93BPackager();
	      m.setPackager(p);
	      m.unpack(ISOUtil.hex2byte(message));
	      m.pack();
	      m.dump(System.out, "");
	      for (int i = 1; i <= m.getMaxField(); i++) {
	        if (m.hasField(i)) {
	          Result = Result + "Field [" + i + "] =" + m.getString(i) + "\n";
	        }
	      }
	    }
	    catch (ISOException e)
	    {
	      Result = e.toString();
	    }
	    return Result;
	  }

	/*private static void testXmlStringtoISOBytes() {
		ISOConverter isoConv = new ISOConverter();
		try {
			String xmlStr = createXmlMessage();
			
			System.out.println("XML Message: " + xmlStr);
			byte[] isoMsg = isoConv.xmlToRawIsoMsg(xmlStr.getBytes());
			System.out.println(ISOUtil.hexString(isoMsg));
			
			//Test Byte Array
			String xmlMsg = isoConv.rawMsgToXmlMsg(isoMsg, false);
			System.out.println(xmlMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	private static String testXmlStringtoISOBytes(String xmlISO) {
		ISOConverter isoConv = new ISOConverter();
		String xmlMsg=null;
		byte[] isoMsg=null;
		try {
			String xmlStr = xmlISO;
			isoMsg = isoConv.xmlToRawIsoMsg(xmlStr.getBytes());
			System.out.println(new String((isoMsg)));
			
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
	private static void testXmlStringtoISOInBase64() {
		ISOConverter isoConv = new ISOConverter();
		try {
			String xmlStr = createXmlMessage();
			
			System.out.println("XML Message: " + xmlStr);
			String isoMsg = isoConv.xmlToRawIsoMsgInBase64(xmlStr);
			System.out.println("Base64 IsoMsg : " + isoMsg);
			
			//Test Byte Array
			String xmlMsg = isoConv.rawMsgInBase64ToXmlMsg(isoMsg, false);
			System.out.println(xmlMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testISOBase64toXmlString() {
		ISOConverter isoConv = new ISOConverter();
		try {
		
			byte[] b = createISOMsg();
			System.out.println("Actual ISO : "+new String(b));
			String base64Msg = Base64Coder.encode(b);
			System.out.println("Base64 String : " + base64Msg);
			String xmlMsg = isoConv.rawMsgInBase64ToXmlMsg(base64Msg, false);
			System.out.println(xmlMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private static byte[] createISOMsg() throws Exception {
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
		return xmlISOMsg.pack();
	}
	
	private static String createXmlMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("<isomsg>");
		sb.append("<field id=\"0\" value=\"1100\"/>");
		sb.append("<field id=\"2\" value=\"4412341212341234\"/>");
		sb.append("<field id=\"3\" value=\"310000\"/>");
		sb.append("<field id=\"7\" value=\"2601091245\"/>");
		sb.append("<field id=\"11\" value=\"261122\"/>");
		sb.append("<field id=\"12\" value=\"002601091245\"/>");
		sb.append("<field id=\"13\" value=\"0119\"/>");
		sb.append("<field id=\"14\" value=\"1222\"/>");
		sb.append("<field id=\"18\" value=\"6014\"/>");
		sb.append("<field id=\"19\" value=\"356\"/>");
		sb.append("<field id=\"35\" value=\"8888881010000639=1912101111000000000\"/>");
		sb.append("<field id=\"37\" value=\"442123261122\"/>");
		sb.append("<field id=\"41\" value=\"12345678\"/>");
		sb.append("<field id=\"42\" value=\"     0123456789\"/>");
		sb.append("<field id=\"52\" value=\"24B37008E153EDE4\"/>");
		sb.append("<field id=\"64\" value=\"F997BD3E76376802\"/>");
		sb.append("</isomsg>");
		return sb.toString();
	}
}
