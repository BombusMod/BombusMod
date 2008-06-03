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

//import java.io.InputStream;
 

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
    public abstract void init();

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
     * Add a short value to the digest.
     */
    public synchronized void update(short aValue) {
        byte	b1, b2;

        b1 = (byte)((aValue >>> 8) & 0xff);
        b2 = (byte)(aValue & 0xff);
        update(b1);
        update(b2);
    }

    /**
     * Add an integer value to the digest.
     */
    public synchronized void update(int aValue) {
        byte	b;

        for (int i = 3; i >= 0; i--) {
            b = (byte)((aValue >>> (i * 8)) & 0xff);
            update(b);
        }
    }

    /**
     * Add a long to the digest.
     */
    public synchronized void update(long aValue) {
        byte	b;

    	for (int i = 7; i >= 0; i--) {
    	    b = (byte)((aValue >>> (i * 8)) & 0xff);
    	    update(b);
    	}
    }

    /**
     * Add specific bytes to the digest.
     */
    public synchronized void update(byte input[], int offset, int len) {
        for (int i = 0; i < len; i++) {
            update(input[i+offset]);
        }
    }

    /**
     * Add an array of bytes to the digest.
     */
    public synchronized void update(byte input[]) {
        update(input, 0, input.length);
    }

    /**
     * Add the bytes in the String 'input' to the current digest.
     * Note that the string characters are treated as unicode chars
     * of 16 bits each. To digestify ISO-Latin1 strings (ASCII) use
     * the updateASCII() method.
     */
    public void update(String input) {
    	int	i, len;
    	short	x;

    	len = input.length();
    	for (i = 0; i < len; i++) {
    	    x = (short) input.charAt(i);
    	    update(x);
    	}
    }

    /**
     * Treat the string as a sequence of ISO-Latin1 (8 bit) characters.
     */
    public void updateASCII(String input) {
    	int	i, len;
    	byte	x;

    	len = input.length();
    	for (i = 0; i < len; i++) {
    	    x = (byte) (input.charAt(i) & 0xff);
    	    update(x);
    	}
    }

    /**
     * Perform the final computations and cleanup.
     */
    public abstract void finish();

  
    /**
     * Return a string representation of this object.
    public String toString() {
    	ByteArrayOutputStream ou = new ByteArrayOutputStream();
    	PrintStream p = new PrintStream(ou);

    	p.print(this.getClass().getName()+" Message Digest ");
    	if (digestValid) {
    	    p.print("<");
    	    for(int i = 0; i < digestBits.length; i++)
     	        hexDigit(p, digestBits[i]);
    	    p.print(">");
    	} else {
    	    p.print("<incomplete>");
    	}
    	p.println();
    	return (ou.toString());
    }
     */
    
    public byte[] getDigestBits(){
        return (digestValid)? digestBits:null;
    }
    
	public String getDigestHex(){
        if (!digestValid) return null;
        StringBuffer out=new StringBuffer();
        
        for(int i = 0; i < digestBits.length; i++) {
            char c;
            
            c = (char) ((digestBits[i] >> 4) & 0xf);
            if (c > 9)   c = (char) ((c - 10) + 'a');
            else  c = (char) (c + '0');
            out.append(c);
            c = (char) (digestBits[i] & 0xf);
            if (c > 9)
                c = (char)((c-10) + 'a');
            else
                c = (char)(c + '0');
            out.append(c);
        }
        
    	return out.toString();
    }

    public String getDigestBase64(){
        return util.Strconv.toBase64(digestBits, digestBits.length);
    }

    /**
     * Return a string that represents the algorithim of this
     * message digest.
     */
    public abstract String getAlg();

    //static byte testdata[];


}
