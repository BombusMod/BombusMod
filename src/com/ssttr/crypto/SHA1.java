/*
 * SHA1.java - An implementation of the SHA-1 Algorithm
 *
 * This version by Chuck McManis (cmcmanis@netcom.com) and
 * still public domain.
 *
 * Based on the C code that Steve Reid wrote his header
 * was :
 *      SHA-1 in C
 *      By Steve Reid <steve@edmweb.com>
 *      100% Public Domain
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
 * 
 * This file was obtained from: http://www.mcmanis.com/~cmcmanis/java/src/util/crypt/SHA1.java
 * More information can be found here: http://www.mcmanis.com/~cmcmanis/java/
 */

package com.ssttr.crypto;

/**
 * This is a simple port of Steve Reid's SHA-1 code into Java.
 * I've run his test vectors through the code and they all pass.
 *
 */
public final class SHA1 extends MessageDigest {
    private int state[] = new int[5];
    private long count;


    protected SHA1() {
        state = new int[5];
        count = 0;
        if (block == null)
            block = new int[16];
        digestBits = new byte[20];
        digestValid = false;
        reset();
    }

    /*
     * The following array forms the basis for the transform
     * buffer. Update puts bytes into this buffer and then
     * transform adds it into the state of the digest.
     */
    private int block[] = new int[16];
    private int blockIndex;

    /*
     * These functions are taken out of #defines in Steve's
     * code. Java doesn't have a preprocessor so the first
     * step is to just promote them to real methods.
     * Later we can optimize them out into inline code,
     * note that by making them final some compilers will
     * inline them when given the -O flag.
     */
    final int rol(int value, int bits) {
        int q = (value << bits) | (value >>> (32 - bits));
        return q;
    }

    final int blk0(int i) {
        block[i] = (rol(block[i],24)&0xFF00FF00) | (rol(block[i],8)&0x00FF00FF);
        return block[i];
    }

    final int blk(int i) {
        block[i&15] = rol(block[(i+13)&15]^block[(i+8)&15]^
                          block[(i+2)&15]^block[i&15], 1);
        return (block[i&15]);
    }

    final void R0(int data[], int v, int w, int x , int y, int z, int i) {
        data[z] += ((data[w] & (data[x] ^ data[y] )) ^ data[y]) +
                                blk0(i) + 0x5A827999 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R1(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) +
                                blk(i) + 0x5A827999 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R2(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) +
                                blk(i) + 0x6ED9EBA1 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R3(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) +
                                blk(i) + 0x8F1BBCDC + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R4(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) +
                                blk(i) + 0xCA62C1D6 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }


    /*
     * Steve's original code and comments :
     *
     * blk0() and blk() perform the initial expand.
     * I got the idea of expanding during the round function from SSLeay
     *
     * #define blk0(i) block->l[i]
     * #define blk(i) (block->l[i&15] = rol(block->l[(i+13)&15]^block->l[(i+8)&15] \
     *   ^block->l[(i+2)&15]^block->l[i&15],1))
     *
     * (R0+R1), R2, R3, R4 are the different operations used in SHA1
     * #define R0(v,w,x,y,z,i) z+=((w&(x^y))^y)+blk0(i)+0x5A827999+rol(v,5);w=rol(w,30);
     * #define R1(v,w,x,y,z,i) z+=((w&(x^y))^y)+blk(i)+0x5A827999+rol(v,5);w=rol(w,30);
     * #define R2(v,w,x,y,z,i) z+=(w^x^y)+blk(i)+0x6ED9EBA1+rol(v,5);w=rol(w,30);
     * #define R3(v,w,x,y,z,i) z+=(((w|x)&y)|(w&x))+blk(i)+0x8F1BBCDC+rol(v,5);w=rol(w,30);
     * #define R4(v,w,x,y,z,i) z+=(w^x^y)+blk(i)+0xCA62C1D6+rol(v,5);w=rol(w,30);
     */

    int dd[] = new int[5];

    /**
     * Hash a single 512-bit block. This is the core of the algorithm.
     *
     * Note that working with arrays is very inefficent in Java as it
     * does a class cast check each time you store into the array.
     *
     */

    void transform() {

        /* Copy context->state[] to working vars */
        dd[0] = state[0];
        dd[1] = state[1];
        dd[2] = state[2];
        dd[3] = state[3];
        dd[4] = state[4];
        /* 4 rounds of 20 operations each. Loop unrolled. */
        R0(dd,0,1,2,3,4, 0); R0(dd,4,0,1,2,3, 1); R0(dd,3,4,0,1,2, 2); R0(dd,2,3,4,0,1, 3);
        R0(dd,1,2,3,4,0, 4); R0(dd,0,1,2,3,4, 5); R0(dd,4,0,1,2,3, 6); R0(dd,3,4,0,1,2, 7);
        R0(dd,2,3,4,0,1, 8); R0(dd,1,2,3,4,0, 9); R0(dd,0,1,2,3,4,10); R0(dd,4,0,1,2,3,11);
        R0(dd,3,4,0,1,2,12); R0(dd,2,3,4,0,1,13); R0(dd,1,2,3,4,0,14); R0(dd,0,1,2,3,4,15);
        R1(dd,4,0,1,2,3,16); R1(dd,3,4,0,1,2,17); R1(dd,2,3,4,0,1,18); R1(dd,1,2,3,4,0,19);
        R2(dd,0,1,2,3,4,20); R2(dd,4,0,1,2,3,21); R2(dd,3,4,0,1,2,22); R2(dd,2,3,4,0,1,23);
        R2(dd,1,2,3,4,0,24); R2(dd,0,1,2,3,4,25); R2(dd,4,0,1,2,3,26); R2(dd,3,4,0,1,2,27);
        R2(dd,2,3,4,0,1,28); R2(dd,1,2,3,4,0,29); R2(dd,0,1,2,3,4,30); R2(dd,4,0,1,2,3,31);
        R2(dd,3,4,0,1,2,32); R2(dd,2,3,4,0,1,33); R2(dd,1,2,3,4,0,34); R2(dd,0,1,2,3,4,35);
        R2(dd,4,0,1,2,3,36); R2(dd,3,4,0,1,2,37); R2(dd,2,3,4,0,1,38); R2(dd,1,2,3,4,0,39);
        R3(dd,0,1,2,3,4,40); R3(dd,4,0,1,2,3,41); R3(dd,3,4,0,1,2,42); R3(dd,2,3,4,0,1,43);
        R3(dd,1,2,3,4,0,44); R3(dd,0,1,2,3,4,45); R3(dd,4,0,1,2,3,46); R3(dd,3,4,0,1,2,47);
        R3(dd,2,3,4,0,1,48); R3(dd,1,2,3,4,0,49); R3(dd,0,1,2,3,4,50); R3(dd,4,0,1,2,3,51);
        R3(dd,3,4,0,1,2,52); R3(dd,2,3,4,0,1,53); R3(dd,1,2,3,4,0,54); R3(dd,0,1,2,3,4,55);
        R3(dd,4,0,1,2,3,56); R3(dd,3,4,0,1,2,57); R3(dd,2,3,4,0,1,58); R3(dd,1,2,3,4,0,59);
        R4(dd,0,1,2,3,4,60); R4(dd,4,0,1,2,3,61); R4(dd,3,4,0,1,2,62); R4(dd,2,3,4,0,1,63);
        R4(dd,1,2,3,4,0,64); R4(dd,0,1,2,3,4,65); R4(dd,4,0,1,2,3,66); R4(dd,3,4,0,1,2,67);
        R4(dd,2,3,4,0,1,68); R4(dd,1,2,3,4,0,69); R4(dd,0,1,2,3,4,70); R4(dd,4,0,1,2,3,71);
        R4(dd,3,4,0,1,2,72); R4(dd,2,3,4,0,1,73); R4(dd,1,2,3,4,0,74); R4(dd,0,1,2,3,4,75);
        R4(dd,4,0,1,2,3,76); R4(dd,3,4,0,1,2,77); R4(dd,2,3,4,0,1,78); R4(dd,1,2,3,4,0,79);
        /* Add the working vars back into context.state[] */
        state[0] += dd[0];
        state[1] += dd[1];
        state[2] += dd[2];
        state[3] += dd[3];
        state[4] += dd[4];
    }


    /**
     *
     * SHA1Init - Initialize new context
     */
    public void reset() {
        /* SHA1 initialization constants */
        state[0] = 0x67452301;
        state[1] = 0xEFCDAB89;
        state[2] = 0x98BADCFE;
        state[3] = 0x10325476;
        state[4] = 0xC3D2E1F0;
        count = 0;
        digestBits = new byte[20];
        digestValid = false;
        blockIndex = 0;
    }

    /**
     * Add one byte to the digest. When this is implemented
     * all of the abstract class methods end up calling
     * this method for types other than bytes.
     */
    public synchronized void update(byte b) {
        int mask = (8 * (blockIndex & 3));

        count += 8;
        block[blockIndex >> 2] &= ~(0xff << mask);
        block[blockIndex >> 2] |= (b & 0xff) << mask;
        blockIndex++;
        if (blockIndex == 64) {
            transform();
            blockIndex = 0;
        }
    }
    /**
     * Add an array of bytes to the digest.
     */
    public synchronized void update(byte input[]) {
        update(input, 0, input.length);
    }

    /* 
    public synchronized void update(byte input[], int offset, int len) {
        for (int i = 0; i < len; i++) {
            update(input[i+offset]);
        }
    }*/

    /**
     * Complete processing on the message digest.
     */
    public int digest(byte output[], int offset, int len) {
        byte bits[] = new byte[8];
        int i;

        for (i = 0; i < 8; i++) {
            bits[i] = (byte)((count >>> (((7 - i) * 8))) & 0xff);
        }

        update((byte) 128);
        while (blockIndex != 56)
            update((byte) 0);
        // This should cause a transform to happen.
        update(bits);
        for (i = 0; i < 20; i++) {
            digestBits[i] = (byte)
                ((state[i>>2] >> ((3-(i & 3)) * 8) ) & 0xff);
        }
        digestValid = true;
        System.arraycopy(digestBits, 0, output, 0, digestBits.length);
        return digestBits.length;
    }

    
}
