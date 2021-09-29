package ISO8583;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Custom_Logs {
	static String logFilePath="E:\\AuditLog\\Ramesh\\";
	
	static StringBuffer sbf=null;
	public static void WriteLogs(String log){
		try{
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH") ;
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") ;
			String fileName=dateFormat.format(date)+".txt";
			File file =new File(logFilePath+fileName);
			if(!file.exists())
				file.createNewFile();
		sbf=new StringBuffer();
		BufferedWriter bwr = new BufferedWriter(new FileWriter(file,true));
		bwr.write("\n");
		bwr.write(dateFormat1.format(date));
		bwr.write(" : "+log);
		bwr.write("\n");
		bwr.flush();
		bwr.close();
		}catch(Exception err){
			
		}
	}

	
}
