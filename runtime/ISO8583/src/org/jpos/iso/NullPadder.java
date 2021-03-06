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
 * The NullPadder does not pad. It is a utility class to use Null Object
 * pattern.
 * 
 * @author joconnor
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public class NullPadder implements Padder
{
    /** The only instance you need */
    public static final NullPadder INSTANCE = new NullPadder();

    /**
	 * @see org.jpos.iso.Padder#pad(java.lang.String, int, char)
	 */
    @Override
	public String pad(String data, int maxLength)
    {
        return data;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Padder#unpad(java.lang.String, char)
	 */
    @Override
	public String unpad(String paddedData)
    {
        return paddedData;
    }
}