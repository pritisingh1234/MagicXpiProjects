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

/**
 * allows the transmision and reception of ISO 8583 Messages
 *
 * @author <a href="mailto:apr@cs.com.uy">Alejandro P. Revilla</a>
 * @author <a href="mailto:alwynschoeman@yahoo.com">Alwyn Schoeman</a>
 * @version $Revision: 2854 $ $Date: 2010-01-02 08:34:31 -0200 (Sat, 02 Jan 2010) $
 */
public interface ISOChannel extends ISOSource {
    public static final int CONNECT      = 0;
    public static final int TX           = 1;
    public static final int RX           = 2;
    public static final int SIZEOF_CNT   = 3;

    /**
     * Associate a packager with this channel
     * @param p     an ISOPackager
     */
    public void setPackager(ISOPackager p);

    /**
     * Connects ISOChannel 
     * @exception IOException
     */
    public void connect () throws IOException;

    /**
     * disconnects ISOChannel
     * @exception IOException
     */
    public void disconnect () throws IOException;

    /**
     * Reconnect channel
     * @exception IOException
     */
    public void reconnect() throws IOException;

    /**
     * @return true if Channel is connected and usable
     */
    @Override
	public boolean isConnected();

    /**
     * Receives an ISOMsg
     * @return the Message received
     * @exception IOException
     * @exception ISOException
     */
    public ISOMsg receive() throws IOException, ISOException;

    /**
     * sends an ISOMsg over the TCP/IP session
     * @param m the Message to be sent
     * @exception IOException
     * @exception ISOException
     */
    @Override
	public void send (ISOMsg m) throws IOException, ISOException;
    
    /**
     * sends a byte[] over the TCP/IP session
     * @param b the byte array to be sent
     * @exception IOException
     * @exception ISOException
     */
    public void send (byte[] b) throws IOException, ISOException;

    /**
     * @param b - usable state
     */
    public void setUsable(boolean b);

    /**
     * associates this ISOChannel with a name on NameRegistrar
     * @param name name to register
     * @see org.jpos.util.NameRegistrar
     */
    public void setName (String name);

   /**
    * @return this ISOChannel's name ("" if no name was set)
    */
    public String getName();

   /**
    * @return current packager
    */
    public ISOPackager getPackager();

   /**
    * Expose channel clonning interface
    */
    public Object clone();
    
}

