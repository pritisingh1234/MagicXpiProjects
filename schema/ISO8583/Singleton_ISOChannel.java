package ISO8583;

import org.jpos.iso.packager.ISO93BPackager;

class Singleton_ISOChannel {
		static String IP="10.50.8.141";
		static int port=58550;
		static ISO93BPackager pack = new ISO93BPackager();
		private static NACChannel asciiChnl = new NACChannel(IP,port, pack);

	   private Singleton_ISOChannel() {      
	   }

	   public static NACChannel getInstance() {

	      // create object if it's not already created
	      if(asciiChnl == null) {
	    	  try {
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
					Custom_Logs.WriteLogs("Exception occured : "+Runtime.StackTraceToString(e));
					return null;
				}
	      }

	       // returns the singleton object
	       return asciiChnl;
	   }

	   public void getConnection() {
	       System.out.println("You are now connected to the database.");
	   }
	}

	class Main {
		/*public static void main(String[] args) {
	      Database db1;

	      // refers to the only object of Database
	      db1= Database.getInstance();
	      
	      db1.getConnection();
	   }*/
	}