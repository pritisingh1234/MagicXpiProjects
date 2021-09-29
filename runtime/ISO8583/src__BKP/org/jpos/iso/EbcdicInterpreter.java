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
 * Implements EBCDIC Interpreter. Strings are converted to and from EBCDIC
 * bytes.
 * 
 * @author joconnor
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public class EbcdicInterpreter implements Interpreter
{
    /** An instance of this Interpreter. Only one needed for the whole system */
    public static final EbcdicInterpreter INSTANCE = new EbcdicInterpreter();

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    @Override
	public void interpret(String data, byte[] b, int offset)
    {
        ISOUtil.asciiToEbcdic(data, b, offset);
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    @Override
	public String uninterpret(byte[] rawData, int offset, int length)
    {
        return ISOUtil.ebcdicToAscii(rawData, offset, length);
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