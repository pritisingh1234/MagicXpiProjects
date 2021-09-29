package ISO8583;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO93APackager;

public class SignOnISOThread implements Runnable{
	Runtime run=new Runtime();
	ServerSocket ss=null;
	@Override
	public void run() {
		
		while(true)
			try{  
				run.call();
				}catch(Exception e){System.out.println(e);} 
	
	}
}
