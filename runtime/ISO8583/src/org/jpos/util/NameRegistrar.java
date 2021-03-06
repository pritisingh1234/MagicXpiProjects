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

package org.jpos.util;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Allow runtime binding of jPOS's components (ISOChannels, Logger, MUXes, etc)
 * @author <a href="mailto:apr@cs.com.uy">Alejandro P. Revilla</a>
 * @version $Revision: 2964 $ $Date: 2010-08-25 11:56:45 -0300 (Wed, 25 Aug 2010) $
 */
public class NameRegistrar implements Loggeable {
    private static NameRegistrar instance = new NameRegistrar();
    private Map registrar;

    public static class NotFoundException extends Exception {

        private static final long serialVersionUID = 8744022794646381475L;
        public NotFoundException() {
            super();
        }
        public NotFoundException(String detail) {
            super(detail);
        }
    }

    private NameRegistrar() {
        super();
        registrar = new Hashtable();
    }
    public static Map getMap() {
        return getInstance().registrar;
    }
    /**
     * @return singleton instance
     */
    public static NameRegistrar getInstance() {
        return instance;
    }
    /**
     * register object
     * @param key - key with which the specified value is to be associated.
     * @param value - value to be associated with the specified key
     */
    public static void register (String key, Object value) {
        Map map = getMap();
        synchronized (map) {
            map.put (key, value);
        }
    }
    /**
     * @param key key whose mapping is to be removed from registrar.
     */
    public static void unregister (String key) {
        Map map = getMap();
        synchronized (map) {
            map.remove (key);
        }
    }
    /**
     * @param key key whose associated value is to be returned.
     * @throws NotFoundException if key not present in registrar
     */
    public static Object get (String key) throws NotFoundException {
        Object obj = getMap().get(key);
        if (obj == null)
            throw new NotFoundException (key);
        return obj;
    }
    /**
     * @param key key whose associated value is to be returned, null if not present.
     */
    public static Object getIfExists (String key) {
        return getMap().get(key);
    }

    @Override
	public void dump (PrintStream p, String indent) {
        dump(p,indent,false);
    }
    public void dump (PrintStream p, String indent, boolean detail) {
        String inner = indent + "  ";
        p.println (indent + "--- name-registrar ---");
        synchronized (registrar) {
            Iterator iter = registrar.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next ();
                Object obj = entry.getValue();
                p.println (inner + 
                    entry.getKey().toString() + ": " +
                    obj.getClass().getName()
                );
                if (detail && obj instanceof Loggeable) {
                    ((Loggeable)obj).dump(p, inner+"  ");
                }
            }
        }
    }
}

