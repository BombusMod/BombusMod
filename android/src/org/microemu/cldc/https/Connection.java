/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package org.microemu.cldc.https;

import java.security.NoSuchAlgorithmException;

import javax.microedition.io.HttpsConnection;
import javax.net.ssl.SSLContext;

import org.microemu.log.Logger;

public class Connection extends org.microemu.cldc.http.Connection implements HttpsConnection {

	private SSLContext sslContext;

	public Connection() {
	    try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException ex) {
			Logger.error(ex);
		}	
	}

	public String getProtocol() {
		return "https";
	}


    /**
     * Returns the network port number of the URL for this HttpsConnection
     *
     * @return  the network port number of the URL for this HttpsConnection. The default HTTPS port number (443) is returned if there was no port number in the string passed to Connector.open.
     */
	public int getPort() {
		if (cn == null) {
			return -1;
		}
		int port = cn.getURL().getPort();
		if (port == -1) {
			return 443;
		}
		return port;
	}

}
