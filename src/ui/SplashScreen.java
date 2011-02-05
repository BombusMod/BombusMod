/*
 * SplashScreen.java
 *
 * Created on 16.02.2007, 14:23
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package ui;

import Client.Config;
import Fonts.FontCache;
//#ifdef AUTOSTATUS
//# import Client.ExtendedStatus;
//# import Client.Roster;
//# import Client.StaticData;
//# import Client.StatusList;
//#endif
import images.RosterIcons;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;
import midlet.BombusMod;
import Colors.ColorTheme;
//#ifdef LIGHT_CONFIG
//# import Client.StaticData;
//# import LightControl.CustomLight;
//#endif
import ui.controls.Progress;

/**
 *
 * @author Eugene Stahov
 */
// TODO: implement as VirtualList
public final class SplashScreen extends Canvas implements Runnable, CommandListener {
    
    private String capt;
    private int pos=-1;
    
   // private int width;
   // private int height;
    
    public Image img;
    
    private ComplexString status;
    
    private TimerTaskClock tc;

    private Config cf=Config.getInstance();
    
    private static SplashScreen instance;

    public int keypressed = 0;

    private Font clockFont=FontCache.getFont(true, FontCache.bigSize);
    
    private Progress pb;

    VirtualList next;

    private char exitKey;

    private int kHold;

    public static SplashScreen getInstance(){
        if (instance==null) 
            instance=new SplashScreen();
        return instance;
    }
    
    /** Creates a new instance of SplashScreen */
    private SplashScreen() {
        setFullScreenMode(Config.fullscreen);
        midlet.BombusMod.getInstance().setDisplayable(this);
    }
    
    public SplashScreen(ComplexString status, char exitKey) {
        this.status = status;
        this.exitKey = exitKey;
        this.kHold = exitKey;
        
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX), 6);
        show();
    }

    public void show() {
        repaint();
        //serviceRepaints();

        new Thread(this).start();
        
        tc=new TimerTaskClock();
        
        setFullScreenMode(Config.fullscreen);

//        if (cf.widthSystemgc) { _vt
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException ex) { }
//        } _vt
    }

    public void paint(Graphics g){
        int width = getWidth();
        int height = getHeight();
        
        g.setColor(ColorTheme.getColor(ColorTheme.BLK_BGND));
        g.fillRect(0,0, width, height);

        if (img!=null) 
            g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);

        if (pos==-1) {
            g.setColor(ColorTheme.getColor(ColorTheme.BLK_INK));
            if (status != null)
                status.drawItem(g, 0, false);            

            g.setFont(clockFont);
            int h=clockFont.getHeight()+1;
            
            String time=Time.localTime();
            int tw=clockFont.stringWidth(time);

            FontCache.drawString(g,time, width/2, height, Graphics.BOTTOM | Graphics.HCENTER);
        } else {
            int filled=pos*width/100;
            if (pb==null) pb=new Progress(0, height , width);
            Progress.draw(g, filled, capt);
        }
    }
    
    public void setProgress(int progress) {
        pos=progress;
            repaint();
    }

    public void setFailed(){
        setProgress("Failed", 100);
    }
    
    public void setProgress(String caption, int progress){
        capt=caption;
//#if DEBUG
//#         System.out.println(capt);
//#endif
	setProgress(progress);
    }
    
    public int getProgress(){
        return pos;
    }
    public void sizeChanged(int w, int h) {
        repaint();
    }
    // close splash
    private Command cmdExit=new Command("Hide Splash", Command.BACK, 99);
    
    public void setExit(VirtualList nextDisplayable){
        next = nextDisplayable;
        addCommand(cmdExit);
        setCommandListener(this);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit)
            close();        
    }
    
    public void close(VirtualList next) {
        if (next == null)
            next = Client.StaticData.getInstance().canvas.homeList;
        Client.StaticData.getInstance().canvas.show(next);
        img = null;
        System.gc();
    }
    
    public void close() {
        close(next);
    }

    public void run() {
        try {
            img=BombusMod.splash;
            if (img==null)
                img=Image.createImage("/images/splash.png");
        } catch (Exception e) {}
        
        midlet.BombusMod.getInstance().setDisplayable(this);
    }

    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            repaint();
            //serviceRepaints();
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }
    public void pointerPressed(int x, int y) {
        close();
    }

    
    protected final void keyRepeated(int keyCode) {
        if (kHold == 0) {
            if (keyCode == exitKey) {
                destroyView();
            }
        }
//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif
    }

    protected final void keyPressed(int keyCode) {
        keypressed = keyCode;        

        if (pos >= 20) {
            close();
        }
        kHold = 0;

//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif
    }


// ==================================================== //


    public void destroyView(){
        status.setElementAt(null,6);
        midlet.BombusMod.getInstance().setDisplayable(Client.StaticData.getInstance().canvas);
        img=null;
        tc.stop();
//#ifdef AUTOSTATUS
//#         StaticData sd=StaticData.getInstance();
//#         if (Roster.autoAway && cf.autoAwayType==Config.AWAY_LOCK) {
//#             int newStatus=Roster.oldStatus;
//#             ExtendedStatus es=StatusList.getInstance().getStatus(newStatus);
//#             String ms=es.getMessage();
//#             Roster.autoAway=false;
//#             Roster.autoXa=false;
//#             sd.roster.sendPresence(newStatus, ms);
//#         }
//#endif
//        if (cf.widthSystemgc) { _vt
            System.gc();
//        } _vt
    }    
}
