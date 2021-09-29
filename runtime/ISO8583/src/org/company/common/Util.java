package org.company.common;

public class Util {
	
	
	public static String byteToStr(byte[] byteFeild){
		StringBuilder strData = new StringBuilder();
		for(int i=0;i<byteFeild.length;i++){
			strData.append((char)byteFeild[i]);
			System.out.println("i: "+(char)byteFeild[i] );
		}
		
		return strData.toString();
	}

}
