/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id: LoggerDataWrapper.java 1605 2008-02-25 21:07:14Z barteo $
 */
package org.microemu.log;

/**
 * @author vlads
 *
 * Convinient method to format debug data
 *
 */
public class LoggerDataWrapper {
	
	private String text; 

	public LoggerDataWrapper(boolean v1) {
		this.text = String.valueOf(v1);	
	}
	
	public LoggerDataWrapper(long v1) {
		this.text = String.valueOf(v1);	
	}

	public LoggerDataWrapper(Object v1) {
		this.text = String.valueOf(v1);
	}

	public LoggerDataWrapper(long v1, long v2) {
		this.text = String.valueOf(v1) + " " + String.valueOf(v2);	
	}
	
	public LoggerDataWrapper(String v1, String v2) {
		this.text = v1 + " " + v2;	
	}
	
	public String toString() {
		return this.text;
	}
}
