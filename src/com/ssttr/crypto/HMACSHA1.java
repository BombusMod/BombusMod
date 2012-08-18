/* 
 * HMACSHA1.java
 *
 *
 * Copyright (c) 2012, Andrey Tikhonov (Tishka17), http://itishka.org
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
 */

package com.ssttr.crypto;

public class HMACSHA1 {
    private final static int BLOCK_LENGTH = 64;
    private final static byte IPAD = (byte) 0x36;
    private final static byte OPAD = (byte) 0x5C;

    SHA1 digest = new SHA1();
    private byte[] inputPad = new byte[BLOCK_LENGTH];
    private byte[] outputPad = new byte[BLOCK_LENGTH];
    
    public void init(byte []key) {
        if (digest==null) digest = new SHA1();
        byte []key2 = new byte[BLOCK_LENGTH];
        if (key.length > BLOCK_LENGTH) {
            digest.init();
            digest.update(key);
            digest.finish();
            byte []key3 = digest.getDigestBits();
            System.arraycopy(key3, 0, key2, 0, key3.length);
        } else {
            System.arraycopy(key, 0, key2, 0, key.length);
        }
        if (key2.length<BLOCK_LENGTH) {
            for (int i=key2.length;i<BLOCK_LENGTH;i++) key2[i]=0;
        }
        for (int i=0;i<BLOCK_LENGTH;i++) {
            inputPad[i]=(byte)(key2[i]^IPAD);
            outputPad[i]=(byte)(key2[i]^OPAD);
        }
    }
    
    public byte []hmac(byte[]message) {
        digest.init();
        digest.update(inputPad);
        digest.update(message);
        digest.finish();
        byte []part2 = digest.getDigestBits();
        digest.init();
        digest.update(outputPad);
        digest.update(part2);
        digest.finish();
        return digest.getDigestBits();
    }
    
}

