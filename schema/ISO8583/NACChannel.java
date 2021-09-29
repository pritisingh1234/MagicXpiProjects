package ISO8583;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.*;
import org.jpos.iso.packager.ISO93BPackager;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Talks with TCP based NACs
 * Sends [LEN][TPDU][ISOMSG]
 * (len=2 bytes network byte order)
 *
 * @author Alejandro P. Revilla
 * @version $Revision$ $Date$
 * @see ISOMsg
 * @see ISOException
 * @see ISOChannel
 */
public class NACChannel extends BaseChannel {
    /**
     * Public constructor 
     */
	static ISO93BPackager pack = new ISO93BPackager();
	static String IP="10.50.8.141";
	static int port=58550;
    boolean tpduSwap = true;
    int lenlen = 0;
    private static NACChannel obj;  
    private NACChannel(){}  
      
    public static NACChannel getNACChannelInstance(){  
      if (obj == null){  
         synchronized(NACChannel.class){  
           if (obj == null){  
               obj = new NACChannel(IP,port,pack);//instance will be created at request time  
           }  
       }              
       }  
     return obj;
    }
    /*public NACChannel () {
        super();
    }*/
    /**
     * Construct client ISOChannel
     * @param host  server TCP Address
     * @param port  server port number
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @see ISOPackager
     */
    public NACChannel (String host, int port, ISOPackager p) {
        super(host, port, p);
        //this.header = TPDU;
    }
    /**
     * Construct server ISOChannel
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @exception IOException on error
     * @see ISOPackager
     */
    public NACChannel (ISOPackager p, byte[] TPDU) throws IOException {
        super(p);
        this.header = TPDU;
    }
    /**
     * constructs server ISOChannel associated with a Server Socket
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @param serverSocket where to accept a connection
     * @exception IOException on error
     * @see ISOPackager
     */
    public NACChannel (ISOPackager p, byte[] TPDU, ServerSocket serverSocket) 
        throws IOException
    {
        super(p, serverSocket);
        this.header = TPDU;
    }
    @Override
	protected void sendMessageLength(int len) throws IOException {
        len += lenlen;
        serverOut.write (len >> 8);
        serverOut.write (len);
    }
    @Override
	protected int getMessageLength() throws IOException, ISOException {
        byte[] b = new byte[2];
        serverIn.readFully(b,0,2);
        return ((b[0] &0xFF) << 8 | b[1] &0xFF) - lenlen;
    }
    @Override
	protected void sendMessageHeader(ISOMsg m, int len) throws IOException { 
        byte[] h = m.getHeader();
        if (h != null) {
            if (tpduSwap && h.length == 5) {
                // swap src/dest address
                byte[] tmp = new byte[2];
                System.arraycopy (h,   1, tmp, 0, 2);
                System.arraycopy (h,   3,   h, 1, 2);
                System.arraycopy (tmp, 0,   h, 3, 2);
            }
        }
        else 
            h = header;
        if (h != null) 
            serverOut.write(h);
    }
    /**
     * New QSP compatible signature (see QSP's ConfigChannel)
     * @param header String as seen by QSP
     */
    @Override
	public void setHeader (String header) {
        super.setHeader (ISOUtil.str2bcd(header, false));
    }
    @Override
	public void setConfiguration (Configuration cfg) 
        throws ConfigurationException
    {
        super.setConfiguration (cfg);
        tpduSwap = cfg.getBoolean ("tpdu-swap", true);
        lenlen = cfg.getBoolean ("include-header-length", false) ? 2 : 0;
    }
}