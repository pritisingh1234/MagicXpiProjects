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
 * ISOFieldPackager Binary Hex LLBINARY
 *
 * @author apr@cs.com.uy
 * @version $Id: IFB_LLHBINARY.java 2854 2010-01-02 10:34:31Z apr $
 * @see ISOComponent
 */
public class IFB_LLHBINARY extends ISOBinaryFieldPackager {
    public IFB_LLHBINARY() {
        super(LiteralBinaryInterpreter.INSTANCE, BinaryPrefixer.B);
    }
    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFB_LLHBINARY (int len, String description) {
        super(len, description, LiteralBinaryInterpreter.INSTANCE, BinaryPrefixer.B);
        checkLength(len, 255);
    }

    @Override
	public void setLength(int len)
    {
        checkLength(len, 255);
        super.setLength(len);
    }
}
