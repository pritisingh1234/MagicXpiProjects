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


/**
 * Implements a Literal Interpreter. No conversion is done.
 * 
 * @author joconnor
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public class LiteralInterpreter implements Interpreter
{
    /** An instance of this Interpreter. Only one needed for the whole system */
    public static final LiteralInterpreter INSTANCE = new LiteralInterpreter();

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    @Override
	public void interpret(String data, byte[] b, int offset)
    {
        byte[] raw = data.getBytes();
        System.arraycopy(raw, 0, b, offset, raw.length);
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    @Override
	public String uninterpret(byte[] rawData, int offset, int length) {
        try {
            //return new String(rawData, offset, length, "ISO8859_1");
        	System.out.println(" f-11 "+new String(rawData, offset, length));
        	return new String(rawData, offset, length, "ISO8859_1");
        } catch (java.io.UnsupportedEncodingException e) { }
        return null;// should never happen, ISO8859_1 is a supported encoding
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
