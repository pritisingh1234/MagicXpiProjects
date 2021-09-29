/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2010 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.iso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.magicsoftware.ibolt.commons.logging.LogLevel;
import com.magicsoftware.ibolt.commons.logging.Logger;


import ISO8583.LogModules;
import ISO8583.Runtime;

/**
 * @author joconnor
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public class ISOStringFieldPackager extends ISOFieldPackager
{
	protected LogModules module = LogModules.STEP;
    private Interpreter interpreter;
    private Padder padder;
    private Prefixer prefixer;

    /**
     * Constructs a default ISOStringFieldPackager. There is no padding,
     * no length prefix and a literal interpretation. The set methods must be called to
     * make this ISOBaseFieldPackager useful.
     */
    public ISOStringFieldPackager()
    {
        super();
        this.padder = NullPadder.INSTANCE;
        this.interpreter = LiteralInterpreter.INSTANCE;
        this.prefixer = NullPrefixer.INSTANCE;
    }

    /**
     * Constructs an ISOStringFieldPackager with a specific Padder, Interpreter and Prefixer.
     * The length and description should be set with setLength() and setDescription methods.
     * @param padder The type of padding used.
     * @param interpreter The interpreter used to encode the field.
     * @param prefixer The type of length prefixer used to encode this field.
     */
    public ISOStringFieldPackager(Padder padder, Interpreter interpreter, Prefixer prefixer)
    {
        super();
        this.padder = padder;
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    /**
     * Creates an ISOStringFieldPackager.
     * @param maxLength The maximum length of the field in characters or bytes depending on the datatype.
     * @param description The description of the field. For human readable output.
     * @param interpreter The interpreter used to encode the field.
     * @param padder The type of padding used.
     * @param prefixer The type of length prefixer used to encode this field.
     */
    public ISOStringFieldPackager(int maxLength, String description, Padder padder,
                                  Interpreter interpreter, Prefixer prefixer)
    {
        super(maxLength, description);
        this.padder = padder;
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    /**
     * Sets the Padder.
     * @param padder The padder to use during packing and unpacking.
     */
    public void setPadder(Padder padder)
    {
        this.padder = padder;
    }

    /**
     * Sets the Interpreter.
     * @param interpreter The interpreter to use in packing and unpacking.
     */
    public void setInterpreter(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    /**
     * Sets the length prefixer.
     * @param prefixer The length prefixer to use during packing and unpacking.
     */
    public void setPrefixer(Prefixer prefixer)
    {
        this.prefixer = prefixer;
    }

    /**
     * Returns the prefixer's packed length and the interpreter's packed length.
	 * @see org.jpos.iso.ISOFieldPackager#getMaxPackedLength()
	 */
    @Override
	public int getMaxPackedLength()
    {
        return prefixer.getPackedLength() + interpreter.getPackedLength(getLength());
    }

    /** Create a nice readable message for errors */
    private String makeExceptionMessage(ISOComponent c, String operation) {
        Object fieldKey = "unknown";
        if (c != null)
        {
            try
            {
                fieldKey = c.getKey();
            } catch (Exception ignore)
            {
            }
        }
        return this.getClass().getName() + ": Problem " + operation + " field " + fieldKey;
    }
    
    /*
     * Adding the custom code for hex to ascii and ascii to hex
     * 
     */
    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

        private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);	
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
    
    /*
     * Custom code ends here
     */

    /**
	 * Convert the component into a byte[].
	 */
    @Override
	public byte[] pack(ISOComponent c) throws ISOException
    {
        try
        {
        	Integer fnum = (Integer)c.getKey();
        	if(fnum==48){
        	System.out.println("Field Number:"+fnum);
        	}
        	System.out.println("Field Number:"+fnum);
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Field Number:"+fnum);
        	
        	System.out.println("Field Number captured in pack class" + c.getKey());
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Field Number captured in pack class" + c.getKey());
           
        	String data = (String)c.getValue();
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"[data] "+data);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Field Number captured in pack class" + c.getKey());
            if (data.length() > getLength())
            {
                throw new ISOException("Field length " + data.length() + " too long. Max: " + getLength());
            }
            String paddedData = padder.pad(data, getLength());
            
            byte[] rawData = new byte[prefixer.getPackedLength()
                    + interpreter.getPackedLength(paddedData.length())];
            
            System.out.println("rawData [AAA] - "+new String(rawData));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [AAA] - "+new String(rawData));
            
            prefixer.encodeLength(paddedData.length(), rawData);
            System.out.println("rawData [BBB] - "+new String(rawData));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [BBB] - "+new String(rawData));
            System.out.println("MITI Trcker :"+prefixer.getPackedLength());
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"MITI Trcker :"+prefixer.getPackedLength());
            String hlen=new String(rawData).substring(0,prefixer.getPackedLength());
            if(prefixer.getPackedLength()>0)
            {
            
            System.out.println();
            System.out.println(hlen);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,hlen);
            System.out.println(Integer.parseInt(hlen));
            System.out.println(Integer.toHexString(Integer.parseInt(hlen)));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,Integer.toHexString(Integer.parseInt(hlen)));
            System.out.println(new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength()));
            String strLen=new String(Integer.toHexString(Integer.parseInt(hlen)));
            System.out.println("Actual Hlen:"+new String(Integer.toHexString(Integer.parseInt(hlen))));
            int hlen1= strLen.length();
            
            System.out.println("String len:"+strLen);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"String len:"+strLen);
            System.out.println("Int len:"+hlen1);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Int len:"+hlen1);
            System.out.println("Prefix Len:"+prefixer.getPackedLength());
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Prefix Len:"+prefixer.getPackedLength());
            String finalLen=null;
            String fteLen =null;
            fteLen = hlen;
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Hlen value "+ fteLen);
            
            if (fnum == 48) 
            {
            	int newfteLen = Integer.parseInt(hlen)%2;
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Value if newfte "+newfteLen);
                 if (newfteLen == 0)
                 {
                	 newfteLen = Integer.parseInt(hlen)/2;
                	 
                 }
                 else
                 {
                	 newfteLen = (Integer.parseInt(hlen) + 1)/2;
                	 
                 }
                 
                 Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Value after division "+newfteLen);
                 fteLen = Integer.toString(newfteLen);
                 hlen1 = new String(Integer.toHexString(Integer.parseInt(fteLen))).length();
                 System.out.println("RJ-hlen1"+hlen1);
                 //fteLen = "0"+Integer.toString(newfteLen);//Added By Ramesh on 30-06-2021
            }
            System.out.println("RJ-fteLen"+fteLen);
            System.out.println("RJ1-fteLen"+Integer.toHexString(Integer.parseInt(fteLen)));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Hlen value 2 "+ fteLen);
            
            if(hlen1<prefixer.getPackedLength())
            {
            	int diff=prefixer.getPackedLength()-hlen1;
            	System.out.println("Differnce:"+diff);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Differnce:"+diff);
            	finalLen= Integer.toHexString(Integer.parseInt(fteLen));
            	System.out.println("RJ-finalLen "+finalLen);
            	for(int j=0;j<diff;j++)
            	{
            		finalLen="0"+finalLen;
            	}
            	System.out.println("FinalLen:"+finalLen);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"FinalLen:"+finalLen);
            	System.out.println("Length in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(finalLen))));
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(finalLen))));
            	System.out.println("Data in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength()))));
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Data in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength()))));
             
            	
            	 rawData=(finalLen +new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength())).getBytes();
            	 System.out.println("RawMsg:"+new String(rawData));
            	 Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"RawMsg:"+new String(rawData));
            }
            else{
            	
            	System.out.println("Length in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(Integer.toHexString(Integer.parseInt(hlen))))) );
            	System.out.println("Data in Hex to Ascii" + new String (ISOUtil.hex2byte(new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength()))));
            	rawData=(Integer.toHexString(Integer.parseInt(fteLen))+new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength())).getBytes();
            }
            		
            System.out.println(new String(rawData));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,new String(rawData));
            }
            interpreter.interpret(paddedData, rawData, prefixer.getPackedLength());
            System.out.println("rawData [CCC] - "+new String(rawData));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [CCC] - "+new String(rawData));
            
            
            //////for character fields the data doesn't need to be converted to ASCII from hex, so passing it it as it is
            
            if (fnum == 103 || fnum == 22 || fnum == 37 || fnum == 41 || fnum == 42 ||  fnum == 43 || fnum == 54 || fnum == 102)
            {
            	/*
            	 * Adding prefix is giving error when the prefix length is zero, so need to add that in condition
            	 */
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"prefixer length " + prefixer.getPackedLength());
            	if (prefixer.getPackedLength() > 0)
            	{
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [DDD] - "+new String(rawData));
            		
            		
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"prefixer length is greater than zero");
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"ASCII value for Length character " + (ISOUtil.hex2byte(new String(rawData).substring(0,prefixer.getPackedLength()))));
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"ASCII value for Length character " + (new String(rawData).substring(0,prefixer.getPackedLength())).getBytes());
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"ASCII value for Length Lokesh " + new String(rawData).substring(0,prefixer.getPackedLength()));
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"ASCII value for Length Lokesh 2 " + new String(ISOUtil.hex2byte(new String(rawData).substring(0,prefixer.getPackedLength()))));
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"ASCII value for Length Lokesh 3 " + new String(ISOUtil.hexString(new String(rawData).substring(0,prefixer.getPackedLength()).getBytes())));
            		
            		
            		rawData = 	( new String(ISOUtil.hex2byte(new String(rawData).substring(0,prefixer.getPackedLength()))) + new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength()) ).getBytes();
            		
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [EEE] - "+new String(rawData));
            	}
            	else
            	{
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [FFF] - "+new String(rawData));
            
            		
            		rawData = 	(  new String(rawData) ).getBytes();
            		
            		Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [GGG] - "+new String(rawData));
            	}
        	}
        
            else
            {
            	/*
            	 * Adding customization logic to handle old values in field 2 or other fields/ Fixed lengths are 
            	 * already taken care of so jsut need to take care in case of non fixed lengths with prefix lengths
            	 */
            	if (prefixer.getPackedLength() > 0)
            	{
            		int evenlen = Integer.parseInt(hlen)%2;
            		if (evenlen != 0)
            		{
            			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Adding additional zero since length of data is odd "+new String(rawData));
            			rawData = (new String(rawData).substring(0,prefixer.getPackedLength()) + "0" + new String(rawData).substring(prefixer.getPackedLength(),Integer.parseInt(hlen)+prefixer.getPackedLength())).getBytes() ;
            			Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Adding additional zero since length of data is odd "+new String(rawData));
            		}
            	}
            	
            	/*
            	 * Customization ends here
            	 */
            	rawData=ISOUtil.hex2byte(new String(rawData));
            	//String hextoascii = hexToAscii(new String(rawData));
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [HHH] - "+new String(rawData));
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData Hex[HH3] - "+ISOUtil.hexString(rawData));
            	//rawData = hextoascii.getBytes();
            	//rawData = new String (ISOUtil.hex2byte(new String(rawData))).getBytes() ; --Original
            	
            }
            
            
            
            System.out.println("rawData [DDD] - "+new String(rawData));
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"rawData [III] - "+new String(rawData));
            
            
            return rawData;
        } catch(Exception e)
        {	
        	e.printStackTrace();
        	Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Packing Error:"+Runtime.StackTraceToString(e));
            throw new ISOException(makeExceptionMessage(c, "packing"), e);
        }
    }

    /**
     * Unpacks the byte array into the component.
     * @param c The component to unpack into.
     * @param b The byte array to unpack.
     * @param offset The index in the byte array to start unpacking from.
     * @return The number of bytes consumed unpacking the component.
     */
    @Override
	public int unpack(ISOComponent c, byte[] b, int offset) throws ISOException
    {
        try
        {
        	Integer fnum = (Integer)c.getKey();
        	System.out.println("Field number " + fnum);
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv incoming byte array actual incomming Array" + Arrays.toString(b));
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Field number " + fnum);
        	System.out.println("Recv Inside ISOStringFieldPackager Unpack function");
        	System.out.println("Recv Offset value" + offset);
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv Offset value" + offset);
        	System.out.println("Recv incoming byte array " + new String(b));
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv incoming byte array " + new String(b));
        	
        	
            //int len = prefixer.decodeLength(b, offset);
        	int len = prefixer.newDecodeLength(b, offset);
        	int origLen = 0;
            System.out.println("Recv Length of remaining data " + len);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv Length of remaining data " + len);
            if (len == -1)
            {
                // The prefixer doesn't know how long the field is, so use
    			// maxLength instead
                len = getLength();
            }
            
            int newlen = len%2;
            if (newlen == 0)
            {
            	newlen = len/2;
            	System.out.println("Length is even " + newlen);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length is even " + newlen);
            	
            }
            else
            {
            	newlen = (len +1)/2;
            	System.out.println("Length is odd " + newlen);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length is odd " + newlen);
            }
            origLen = len;
            len = newlen;
            System.out.println("Recv Length of remaining data2 " + len);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv Length of remaining data2 " + len);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Recv Length of remaining Origlen " + origLen);
           
           
            
            int lenLen = prefixer.getPackedLength();
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"prefixer length " + lenLen);
            
            if (lenLen > 0)
            {
            	
            
            int newlenlen = lenLen%2;
            
            if (newlenlen == 0)
            {
            	newlenlen = lenLen/2;
            	System.out.println("Length is even " + newlenlen);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length is even " + newlenlen);
            }
            else
            {
            	newlenlen = (lenLen +1)/2;
            	System.out.println("Length is odd " + newlenlen);
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length is odd " + newlenlen);
            }
            lenLen = newlenlen;
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"prefixer length again " + lenLen);
            System.out.println("Recv Length of Prefix after division by 2 " + lenLen);
            }
            
            System.out.println("Prefixed length defined for field " +lenLen );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Prefixed length defined for field " +lenLen );
            
            /*Code for handling fields which will not be converted to hex while sending
             * Just add all the CHAR type fields in the OR condition and it will not be converted
             */
            
            if (fnum == 103 || fnum == 22 || fnum == 37 || fnum == 41 || fnum == 42 ||   fnum == 43 || fnum == 54 || fnum == 102)
            {
            	len = len * 2;
            	int difference = len - origLen;
            	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Difference in len for specified fields " + difference);
            	
            	System.out.println("Recv The field is of CHar type with length " + len);
            	// String unpacked = interpreter.uninterpret(b, offset + lenLen, len);
            	String unpacked = interpreter.uninterpret(b, offset + lenLen, origLen);
            	// unpacked = unpacked.substring(difference, origLen);
                 
            	 System.out.println("Unpacked data " + unpacked);
            	 Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Unpacked data for string fields " + unpacked);
            	 
            	 /*
            	  * Changing the length to correct teh offset returned value
            	  */
            	 len = origLen;
            	 Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Return length modified to correct offset " + len);
            	 
                 
                 c.setValue(unpacked);
                            	
        	}
            else
            {
            	
            int difference = len + len - origLen;
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Difference between original length and calculate one " +difference );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," OFFSET AGAIN " +offset );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," LELEN AGAIN " +lenLen );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," LEN AGAIN " +len );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," orig Len AGAIN " + origLen );
            
            /*
             * Customization done to handle field 48
             */
            if (fnum == 48)
            {
            	len = origLen;
            }
            
            String unpacked = interpreter.custom_uninterpret(b, offset + lenLen, len);
            
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"unpacked1 data" + new String(unpacked));
            //Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Unpacked data before hex" + ISOUtil.hexString(unpacked1));
            
                      
            //String unpacked = ISOUtil.hexString(unpacked1);
            
            
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Unpacked data for nromal string with logical lenth " + unpacked);
            /* Previous code
              String unpacked = this.interpreter.uninterpret(b, offset + lenLen, len);
        
        System.out.println("Unpacked data before hex" + unpacked);
        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module, "Unpacked data before hex" + unpacked);
        
        unpacked = ISOUtil.hexString(unpacked.getBytes());
        System.out.println("Unpacked data " + unpacked); 
             
             * 
             */
            /*
             * below condition not required for  field 48
             */
            if (fnum != 48)
            {
	            unpacked = unpacked.substring(difference, origLen + difference);
	            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Unpacked data for nromal string with modified/original lenth " + unpacked);
            }
            
            c.setValue(unpacked);
            }
            
            /*
             * Code for handling char field ends here
             */
            
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Length character length returned to be used for offset " + lenLen);
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"actual field  length returned to be used for offset " + interpreter.getPackedLength(len));
            
            return lenLen + interpreter.getPackedLength(len);
        } catch(Exception e)
        {
        	Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Unpacking Error:"+Runtime.StackTraceToString(e));
            throw new ISOException(makeExceptionMessage(c, "unpacking"), e);
        }
    }

    /**
     * Unpack the input stream into the component.
     * @param c  The Component to unpack into.
     * @param in Input stream where the packed bytes come from.
     * @exception IOException Thrown if there's a problem reading the input stream.
     */
    @Override
	public void unpack (ISOComponent c, InputStream in) 
        throws IOException, ISOException
    {
        try
        {
            int lenLen = prefixer.getPackedLength ();
            int len;
            if (lenLen == 0)
            {
                len = getLength();
            } else
            {
                len = prefixer.decodeLength (readBytes (in, lenLen), 0);
            }
            int packedLen = interpreter.getPackedLength(len);
            String unpacked = interpreter.uninterpret(readBytes (in, packedLen), 0, len);
            c.setValue(unpacked);
        } catch(ISOException e)
        {	
        	Logger.logMessage(LogLevel.LEVEL_ERROR, this.module,"Unpacking Error:"+Runtime.StackTraceToString(e));
            throw new ISOException(makeExceptionMessage(c, "unpacking"), e);
        }
    }

    /**
     * Checks the length of the data against the maximum, and throws an IllegalArgumentException.
     * This is designed to be called from field Packager constructors and the setLength()
     * method.
     * @param len The length of the data for this field packager.
     * @param maxLength The maximum length allowed for this type of field packager.
     *          This depends on the prefixer that is used.
     * @throws IllegalArgumentException If len > maxLength.
     */
    protected void checkLength(int len, int maxLength) throws IllegalArgumentException
    {
        if (len > maxLength)
        {
            throw new IllegalArgumentException("Length " + len + " too long for " + getClass().getName());
        }
    }
}
