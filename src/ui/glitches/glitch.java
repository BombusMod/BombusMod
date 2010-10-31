// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

//code from decompiled demo poc4k by Shadow
//modified & adapted by ad

package ui.glitches;

import Client.StaticData;
import javax.microedition.lcdui.*;

public class glitch extends Canvas implements Runnable {
    private int width;
    private int height;
    private int i;
    private int j;
    private volatile Thread thread;
    private int l[];
    private int n[];
    private int p[];
    private boolean ready;

    private Display display;
    
    public glitch(Display display) {
        this.display=display;
        midlet.BombusMod.getInstance().setDisplayable(this);
        init();
    }

    private static void glitch(int ai[], int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
        int j3 = k1 << 8;
        int k3 = l1 << 8;
        int l3 = i2 << 8;
        int i4 = (j2 - k1 << 8) / (j1 - i1);
        int j4 = (k2 - l1 << 8) / (j1 - i1);
        int k4 = (l2 - i2 << 8) / (j1 - i1);
        for(int i3 = i1; i3 < j1; i3++)
        {
            int l4 = j3 >> 8;
            int i5 = k3 >> 8;
            int j5 = l3 >> 8;
            ai[i3] = l4 << 16 | i5 << 8 | j5;
            j3 += i4;
            k3 += j4;
            l3 += k4;
        }
    }

    private void calculate() {
        p = new int[256];
        glitch(p, 0, 32, 0, 0, 0, 32, 96, 192);
        glitch(p, 32, 64, 32, 96, 192, 255, 255, 255);
        glitch(p, 64, 96, 255, 255, 255, 192, 96, 32);
        glitch(p, 96, 128, 192, 96, 32, 0, 0, 0);
        glitch(p, 128, 160, 0, 0, 0, 32, 192, 96);
        glitch(p, 160, 192, 32, 192, 96, 255, 255, 255);
        glitch(p, 192, 224, 255, 255, 255, 160, 32, 176);
        glitch(p, 224, 256, 160, 32, 176, 0, 0, 0);
        n = new int[1024];
        for(int l1 = 0; l1 < 1024; l1++)
            n[l1] = (int)(65535D * Math.sin(((double)l1 * 3.1415926535897931D) / 512D));

    }

    final void init() {
        thread = new Thread(this);
        thread.start();
    }

    public final void run() {
        Thread thread = Thread.currentThread();
        try {
            while(thread == thread) {
                long l1 = System.currentTimeMillis();
                repaint(0, 0, getWidth(), getHeight());
                serviceRepaints();
                long l2;
                if((l2 = System.currentTimeMillis() - l1) < (long)10) {
                    synchronized(this) { wait((long)10 - l2); }
                    i = 10;
                } else {
                    i = (int)l2;
                    Thread.yield();
                }
                j += i;
            }
            return;
        } catch(InterruptedException _ex) { return; }
    }

    public final void paint(Graphics g1) {
        int w=getWidth();
        int h=getHeight();
        if (w!=width || h!=height) {
            width = w;
            height = h;
            ready=false;
        }
        int k4 = 0;
        if (!ready) {
            calculate();
            l = new int[width * height];
            ready = true;
            return;
        } else {
            int l3 = j >> 3;
            int j4 = j >> 2;
            for(int j2 = 0; j2 < height; j2++) {
                for(int j1 = 0; j1 < width; j1++)
                    l[k4++] = p[0x30000 + n[l3 + j1 * 5 & 0x3ff] + n[j4 - j2 * 6 & 0x3ff] + n[(j4 * 2 - j1 * 4) + j2 * 3 & 0x3ff] >> 10 & 0xff];
            }

            g1.drawRGB(l, 0, width, 0, 0, width, height, false);
            return;
        }
    }

    public final void keyPressed(int keyCode) {
        thread = null;
        midlet.BombusMod.getInstance().setDisplayable(StaticData.getInstance().canvas);
        repaint();
    }
}
