/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public interface RecordStoreManager {
	
	String getName();

	void deleteRecordStore(String recordStoreName) 
			throws RecordStoreNotFoundException, RecordStoreException;

	RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) 
			throws RecordStoreException;

	String[] listRecordStores();
	
	void loadRecord(RecordStoreImpl recordStoreImpl, int recordId) 
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException;

	void deleteRecord(RecordStoreImpl recordStoreImpl, int recordId) 
			throws RecordStoreNotOpenException, RecordStoreException;

	void saveRecord(RecordStoreImpl recordStoreImpl, int recordId) 
			throws RecordStoreNotOpenException, RecordStoreException;

	int getSizeAvailable(RecordStoreImpl recordStoreImpl);

	/**
	 * Initialize RMS Manager before starting MIDlet 
	 */
	void init(MicroEmulator emulator);

	/**
	 * Delete all record stores.
	 */
	void deleteStores();

	void setRecordListener(ExtendedRecordListener recordListener);
	
	void fireRecordStoreListener(int type, String recordStoreName);

}
