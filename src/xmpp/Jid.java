/*
 * Jid.java
 *
 * Created on 4.03.2005, 1:25
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package xmpp;

public class Jid {

    private String user;
    private String server;
    public String resource = "";
    
    public Jid(String user, String server, String resource) {
        this.user = user;
        this.server = server;
        this.resource = resource;
    }

    public Jid(String fromString) {
        int at = fromString.indexOf('@');
        if (at == -1) {
            user = "";
            server = fromString.toLowerCase();
        } else {
            user = fromString.substring(0, at).toLowerCase();
            int slash = fromString.indexOf('/');
            if (slash == -1) {
                server = fromString.substring(at + 1).toLowerCase();
            } else {
                resource = fromString.substring(slash + 1);
                server = fromString.substring(at + 1, fromString.length() - resource.length() - 1).toLowerCase();
            }
        }
    }       
    
    public String getNode() {
        return user;
    }
    
    public String getServer() {
        return server;
    }

    public String getBare() {
        return user == null || user.length() == 0 ? server : user + "@" + server;
    }
    
    /** Compares two Jids
     * @param j
     * @param compareResource
     * @return
     */
    public boolean equals(Jid j, boolean compareResource) {
        if (j == null) {
            return false;
        }

        if (!getBare().equals(j.getBare())) {
            return false;
        }

        if (!compareResource) {
            return true;
        }
       
        return (resource.equals(j.resource));
    }


    public String toString() {
        if (user.length() == 0) {
            return getBare();
        }
        return resource.length() == 0 ? getBare() : getBare() + "/" + resource;
    }
}
