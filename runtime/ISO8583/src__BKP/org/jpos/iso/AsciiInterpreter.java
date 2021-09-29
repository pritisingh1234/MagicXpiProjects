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

import com.magicsoftware.ibolt.commons.logging.LogLevel;
import com.magicsoftware.ibolt.commons.logging.Logger;

import ISO8583.LogModules;

/**
 * Implements ASCII Interpreter. Strings are converted to and from ASCII bytes.
 * This uses the US-ASCII encoding which all JVMs must support.
 * 
 * @author joconnor
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public class AsciiInterpreter implements Interpreter
{
    /** An instance of this Interpreter. Only one needed for the whole system */
    public static final AsciiInterpreter INSTANCE = new AsciiInterpreter();
    protected LogModules module = LogModules.STEP;
    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    @Override
	public void interpret(String data, byte[] b, int offset)
    {
        for (int i = data.length() - 1; i >= 0; i--)
        {
            b[offset + i] = (byte) data.charAt(i);
        }
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    @Override
	public String uninterpret (byte[] rawData, int offset, int length) {
        byte[] ret = new byte[length];
        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Chunk Data:"+rawData);
        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"RR-Chunk Data:"+rawData);
        for (int i = 0; i < length; i++) {
            ret[i] = rawData[offset + i];
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Uninterpre array offset " + offset + " value of i " + i + " rawdata at position " + ret[i] + " actual value in strig " + String.valueOf(ret[i]) );
            Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module,"Uninterpre array offset " + offset + " value of i " + i + " rawdata at position " + ret[i] + " actual value in strig " + String.valueOf(ret[i]) );
            
        }
        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Uninterpre complete value together " + new String(ret));
        Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Uninterpre complete value together " + new String(ret));
        String s = null;
        try {
           //s = new String(ret, "UTF8");
        	//  s = new String(ret, "ISO8859_7");
        	s = new String(ret);
        	Logger.logMessage(LogLevel.LEVEL_DEBUG, this.module," Uninterpre complete value together 2 " + s);
        }catch (Exception e){
        // catch (java.io.UnsupportedEncodingException e) {
            // ISO8859_1 is a supported encoding.
        }
        return s;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#getPackedLength(int)
	 */
    @Override
	public int getPackedLength(int nDataUnits)
    {
        return nDataUnits;
    }
}
