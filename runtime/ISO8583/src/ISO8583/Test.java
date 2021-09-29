package ISO8583;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

import org.company.common.Util;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO93BPackager;

import com.magicsoftware.xpi.sdk.SDKException;
import com.magicsoftware.xpi.sdk.trigger.TriggerGeneralParams;
import com.magicsoftware.xpi.sdk.trigger.external.FlowLauncher;

public class Test {
	
public static void main(String[] args) throws Exception {
	
	TriggerGeneralParams  triggerGeneralParams=new TriggerGeneralParams(); 
	FlowLauncher fl=new FlowLauncher();
	/*HashMap<String,String> map=new HashMap<String,String>();
	map.put("IP","10.23.223.101");
	map.put("Port","5012");
	triggerGeneralParams.setServiceObject(map);
	run.load(triggerGeneralParams, fl);*/
	//byte[] keyBytes = Files.readAllBytes(new File("inr.log").toPath());
	//System.out.println("Init Array "+Arrays.toString(keyBytes));
    //byteToISO(keyBytes,false);
	createISOMsg1().pack();
}
private static ISOMsg createISOMsg1() throws Exception {
	ISOMsg xmlISOMsg = new ISOMsg();
	xmlISOMsg.setPackager(new ISO93BPackager());
	xmlISOMsg.setHeader("ISO8583-1993021000000".getBytes());
	xmlISOMsg.setMTI("1200");
	xmlISOMsg.set(2, "0002120001102000008");
	xmlISOMsg.set(3, "401000");
	xmlISOMsg.set(4, "000000759000");
	xmlISOMsg.set(5, "000000759000");
	xmlISOMsg.set(6, "000000759000");
	xmlISOMsg.set(7, "0120145557");
	xmlISOMsg.set(10, "00000001");
	xmlISOMsg.set(11, "00294");
	xmlISOMsg.set(12, "210120165557");
	xmlISOMsg.set(17, "0120");
	xmlISOMsg.set(19, "0894");
	xmlISOMsg.set(22, "000010Z00000");
	xmlISOMsg.set(24, "0200");
	xmlISOMsg.set(26, "5271");
	xmlISOMsg.set(28, "210120");
	xmlISOMsg.set(32, "000204");
	xmlISOMsg.set(37, "102016002946");
	xmlISOMsg.set(43, "                                        ");
	xmlISOMsg.set(48, "A02106313233343536");
	xmlISOMsg.set(49, "0967");
	xmlISOMsg.set(50, "0999");
	xmlISOMsg.set(51, "0999");
	xmlISOMsg.set(102,"0002041802687028001");
	xmlISOMsg.set(103,"0002120001102000008");
	return xmlISOMsg;
}

public static ISOMsg byteToISO(byte[] rawMsg, boolean isLengthExist) throws Exception {
	byte[] b1 = null;
	String xmlMsg = null;
	ISOMsg isoMsg=null;
	Runtime run=new Runtime();
	try {
		if(isLengthExist){
			b1 = Arrays.copyOfRange(rawMsg, 4, rawMsg.length);
		}else{
			b1 = rawMsg;
		}
		isoMsg = new ISOMsg();
		
		isoMsg.setPackager(new ISO93BPackager());
		isoMsg.setHeader("ISO8583-1993021000000".getBytes());
		isoMsg.unpack(b1);
		System.out.println(run.logISOMsg(isoMsg));
	
	} catch (Exception e1) {
		throw e1;
	}
	return isoMsg;
}
}
