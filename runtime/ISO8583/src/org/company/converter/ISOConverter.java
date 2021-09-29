package org.company.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.company.common.Base64Coder;
import org.company.common.Util;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;
import org.jpos.iso.packager.XMLPackager;

import ISO8583.iso_FMB;

/**
 * @author 
 * @since 
 * 		<li>This is the major class for conversion of ISO to XML and vice versa</li>
 */
public class ISOConverter {

	public ISOConverter() {
	}

	/**
	 * Raw Msg To XML Msg
	 * @param rawMsg
	 * @param isLengthExist
	 * @return
	 * @throws Exception
	 */
	public byte[] rawMsgToXmlMsg(byte[] rawMsg, boolean isLengthExist) throws Exception {
		byte[] b1 = null;
		byte[] xmlMsg = null;
		try {
			if(isLengthExist){
				b1 = Arrays.copyOfRange(rawMsg, 4, rawMsg.length);
			}else{
				b1 = rawMsg;
			}
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(new ISO93APackager());
			isoMsg.unpack(b1);

			ISOMsg isoXMLMsg = convertIsoToXml(isoMsg);
			System.out.println("RRR"+new String(isoXMLMsg.pack()));
			//xmlMsg = Util.byteToStr(isoXMLMsg.pack());
			xmlMsg=isoXMLMsg.pack();
				
		} catch (Exception e1) {
			throw e1;
		}
		
		return xmlMsg;
	}
	
	/**
	 * Raw Msg To XML Msg
	 * @param rawMsg
	 * @param isLengthExist
	 * @return
	 * @throws Exception
	 */
	public String rawMsgToXmlMsgB(byte[] rawMsg, boolean isLengthExist) throws Exception {
		byte[] b1 = null;
		String xmlMsg = null;
		try {
			if(isLengthExist){
				b1 = Arrays.copyOfRange(rawMsg, 4, rawMsg.length);
			}else{
				b1 = rawMsg;
			}
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(new ISO93BPackager());
			isoMsg.unpack(b1);

			ISOMsg isoXMLMsg = convertIsoToXml(isoMsg);
			xmlMsg = Util.byteToStr(isoXMLMsg.pack());
				
		} catch (Exception e1) {
			throw e1;
		}
		return xmlMsg;
	}
	/**
	 * Raw Msg To XML Msg
	 * @param file
	 * @param isLengthExist
	 * @return
	 * @throws Exception
	 */
	public String rawMsgToXmlMsg(File file, boolean isLengthExist) throws Exception {
		byte[] b = new byte[(int) file.length()];
		byte[] b1 = null;
		String xmlMsg = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(b);
			if(isLengthExist){
				b1 = Arrays.copyOfRange(b, 4, b.length);
			}else{
				b1 = b;
			}
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(new ISO93APackager());
			isoMsg.unpack(b1);

			ISOMsg isoXMLMsg = convertIsoToXml(isoMsg);
			xmlMsg = Util.byteToStr(isoXMLMsg.pack());
				
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			throw e;
		} catch (IOException e1) {
			System.out.println("Error Reading The File.");
			e1.printStackTrace();
			throw e1;
		}
		return xmlMsg;
	}

	/**
	 * Raw Msg in Base64 To XML Msg
	 * @param rawMsg
	 * @param isLengthExist
	 * @return
	 * @throws Exception
	 */
	public String rawMsgInBase64ToXmlMsg(String base64Msg, boolean isLengthExist) throws Exception {
		byte[] b1 = null;
		String xmlMsg = null;
		byte[] rawMsg = null;
		try {
			rawMsg = Base64Coder.decode(base64Msg);
			if(isLengthExist){
				b1 = Arrays.copyOfRange(rawMsg, 4, rawMsg.length);
			}else{
				b1 = rawMsg;
			}
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(new ISO93APackager());
			isoMsg.unpack(b1);

			ISOMsg isoXMLMsg = convertIsoToXml(isoMsg);
			xmlMsg = Util.byteToStr(isoXMLMsg.pack());
				
		} catch (Exception e1) {
			throw e1;
		}
		return xmlMsg;
	}
	public String rawMsgInBase64ToXmlMsgB(String base64Msg, boolean isLengthExist) throws Exception {
		byte[] b1 = null;
		String xmlMsg = null;
		byte[] rawMsg = null;
		try {
			rawMsg = Base64Coder.decode(base64Msg);
			System.out.println("11111111111 : decode ISO : "+new String(rawMsg));
			if(isLengthExist){
				b1 = Arrays.copyOfRange(rawMsg, 4, rawMsg.length);
			}else{
				b1 = rawMsg;
			}
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(new ISO93BPackager());
			isoMsg.unpack(b1);

			ISOMsg isoXMLMsg = convertIsoToXml(isoMsg);
			xmlMsg = Util.byteToStr(isoXMLMsg.pack());
				
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return xmlMsg;
	}
	
	/**
	 * XML To Raw Iso Msg
	 * @param xmlfile
	 * @return
	 * @throws Exception
	 */
	public byte[] xmlToRawIsoMsg(File xmlfile) throws Exception {
		byte[] b = new byte[(int) xmlfile.length()];
		byte[] rawMsg = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(xmlfile);
			fileInputStream.read(b);
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(b);

			ISOMsg isoMsg = convertXmlToIso(xmlISOMsg);
			rawMsg = isoMsg.pack();
			System.out.println(rawMsg.length);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			throw e;
		} catch (IOException e1) {
			System.out.println("Error Reading The File.");
			e1.printStackTrace();
			throw e1;
		}
		return rawMsg;
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
	    System.out.println(Response);
	    return Response;
	  }
	/**
	 * XML To Raw Iso Msg
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	
	public byte[] xmlToRawIsoMsg(byte[] xmlData) throws Exception {
		byte[] rawMsg = null;
		try {
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(xmlData);

			ISOMsg isoMsg = convertXmlToIso(xmlISOMsg);
			rawMsg = isoMsg.pack();
			logISOMsg(isoMsg);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return rawMsg;
	}
	/**
	 * XML To Raw Iso Msg
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	
	public byte[] xmlToRawIsoMsg(byte[] xmlData,String mti) throws Exception {
		byte[] rawMsg = null;
		try {
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(xmlData);

			ISOMsg isoMsg = convertXmlToIso1(xmlISOMsg,mti);
			rawMsg = isoMsg.pack();
			logISOMsg(isoMsg);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return rawMsg;
	}
	/**
	 * XML To Raw Iso Msg
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public ISOMsg xmlToRawIsoMsgB(byte[] xmlData) throws Exception {
		ISOMsg isoMsg=null;
		byte[] rawMsg = null;
		try {
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(xmlData);
			isoMsg = convertXmlToIso(xmlISOMsg);
			rawMsg = isoMsg.pack();
			System.out.println(rawMsg.length);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return isoMsg;
	}
	/**
	 * XML String To Raw Iso Msg
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public byte[] xmlToRawIsoMsg(String xmlData) throws Exception {
		byte[] rawMsg = null;
		try {
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(xmlData.getBytes());

			ISOMsg isoMsg = convertXmlToIso(xmlISOMsg);
			rawMsg = isoMsg.pack();
			System.out.println(rawMsg.length);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return rawMsg;
	}

	/**
	 * XML String To Raw Iso Msg in Base 64
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public String xmlToRawIsoMsgInBase64(String xmlData) throws Exception {
		byte[] rawMsg = null;
		String rawMsgInBase64 = null;
		try {
			ISOMsg xmlISOMsg = new ISOMsg();
			xmlISOMsg.setPackager(new XMLPackager());
			xmlISOMsg.unpack(xmlData.getBytes());

			ISOMsg isoMsg = convertXmlToIso(xmlISOMsg);
			rawMsg = isoMsg.pack();
			rawMsgInBase64 = Base64Coder.encode(rawMsg);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return rawMsgInBase64;
	}

	public static ISOMsg convertIsoToXml(ISOMsg msg) throws ISOException {
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(new ISO93APackager());
		try {
			for (int i=0;i<=msg.getMaxField();i++) {
				if (msg.hasField(i)) {
					isoMsg.set(i,msg.getString(i).trim());
				}
				//isoMsg.setMTI("1814");
				isoMsg.set (39,"0800");
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
		}
		return isoMsg;
 
	}
	public ISOMsg convertXmlToIsoB(ISOMsg xmlIsoMsg) throws ISOException {
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(new ISO93BPackager());
		try {
			for (int i = 0; i <= xmlIsoMsg.getMaxField(); i++) {
				if (xmlIsoMsg.hasField(i)) {
					isoMsg.set(i, xmlIsoMsg.getString(i));
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
		}
		return isoMsg;
	}
	public ISOMsg convertXmlToIso(ISOMsg xmlIsoMsg) {
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(new ISO93APackager());
		try {
			for (int i = 0; i <= xmlIsoMsg.getMaxField(); i++) {
				if (xmlIsoMsg.hasField(i)) {
					isoMsg.set(i, xmlIsoMsg.getString(i));
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
		}
		return isoMsg;
	}
	public ISOMsg convertXmlToIso1(ISOMsg xmlIsoMsg, String mti) {
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(new ISO93APackager());
		try {
			for (int i = 0; i <= xmlIsoMsg.getMaxField(); i++) {
				if (xmlIsoMsg.hasField(i)) {
					isoMsg.set(i, xmlIsoMsg.getString(i));
				}
			}
			isoMsg.setMTI(mti);
			isoMsg.set(39, "800");
			iso_FMB.logISOMsg(isoMsg);
			
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
		}
		return isoMsg;
	}
}
