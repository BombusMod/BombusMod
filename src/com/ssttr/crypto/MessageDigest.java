/*
 * @(#)MessageDigest.java	1.7 95/08/15
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Updated to JDK 1.0.2 levels by Chuck McManis
 * 
 * This file was obtained from: http://www.mcmanis.com/~cmcmanis/java/src/util/crypt/MessageDigest.java
 * More information can be found here: http://www.mcmanis.com/~cmcmanis/java/
 */

package com.ssttr.crypto;
 

/**
 * The MessageDigest class defines a general class for computing digest
 * functions. It is defined as an abstract class that is subclassed by
 * message digest algorithms. In this way the PKCS classes can be built
 * to take a MessageDigest object without needing to know what 'kind'
 * of message digest they are computing.
 *
 * This class defines the standard functions that all message digest
 * algorithms share, and ways to put all Java fundamental types into
 * the digest. It does not define methods for digestifying either
 * arbitrary objects or arrays of objects however.
 *
 * @version 	5 Oct 1996, 1.8
 * @author 	Chuck McManis
 */
public abstract class MessageDigest
{

    /** the actual digest bits. */
    public byte digestBits[];

    /** status of the digest */
    public boolean digestValid;

    /**
     * This function is used to initialize any internal digest
     * variables or parameters.
     */
    public abstract void reset();

    /**
     * The basic unit of digestifying is the byte. This method is
     * defined by the particular algorithim's subclass for that
     * algorithim. Subsequent versions of this method defined here
     * decompose the basic type into bytes and call this function.
     * If special processing is needed for a particular type your
     * subclass should override the method for that type.
     */
    public abstract void update(byte aValue);
   

    /**
     * Add specific bytes to the digest.
     */
    public void update(byte input[], int offset, int len) {
        for (int i = 0; i < len; i++) {
            update(input[i+offset]);
        }
    }    

    /**
     * Perform the final computations and cleanup.
     */
    public abstract int digest(byte output[], int offset, int len);    
    
    public static MessageDigest getInstance(String algorithm) {
        if (algorithm.equals("MD5")) {
            return new MD5();
        }
        return new SHA1();
    }

}
