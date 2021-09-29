package ISO8583;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO93APackager;
import org.jpos.iso.packager.ISO93BPackager;

import com.magicsoftware.ibolt.commons.logging.LogLevel;
import com.magicsoftware.ibolt.commons.logging.Logger;

public class MgListener implements Runnable{
	Runtime run=new Runtime();
	public static ServerSocket ss=null;
	public Socket s=null;
	protected LogModules module = LogModules.STEP;
	int localport1=6666;
	
	MgListener(int localPort)
	{
		this.localport1=localPort;
	}
	@Override
	public void run() {
	
		try {
			ss=new ServerSocket(localport1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
		try{
				
				s=ss.accept(); 
				DataInputStream dis=new DataInputStream(s.getInputStream());  
				String TranferRequest=dis.readUTF();  
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Transfer Request under MGListener: "+TranferRequest); 
				if (!Runtime.asciiChnl.isConnected()) {
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"New Connection: ");

					Runtime.asciiChnl.connect();
					Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Connection established ");

				}
				ISOMsg isoMsg = new ISOMsg();
				isoMsg.setPackager(new ISO93BPackager());
				isoMsg.unpack(TranferRequest.getBytes());
				/*
				 * Setting the header as it gets lost after unpack function and needs to set again so that it is sent correctly
				 */
				isoMsg.setHeader((Runtime.object.getString("Header")).getBytes());
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Sending ISO server");
				Runtime.asciiChnl.send(isoMsg);
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Sent ISO server");
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Trasaction ISO Request Std Alone: " + Runtime.logISOMsg(isoMsg));
				Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Trasaction ISO Request Std Alone Sent Successfully: " + Runtime.logISOMsg(isoMsg));
			
	
	}catch(Exception e){
		System.out.println(e);
		
		Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Trasaction ISO Request error: " + Runtime.StackTraceToString(e));
	
	} 
	}
}
}
