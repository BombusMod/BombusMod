/**
 *  MicroEmulator
 *
 *  @version $Id: ConnectionBaseInterface.java 948 2007-02-17 19:48:59Z vlads $
 */
package com.sun.cdc.io;

import javax.microedition.io.Connection;

public interface ConnectionBaseInterface {

	Connection openPrim(String name, int mode, boolean timeouts);

}
