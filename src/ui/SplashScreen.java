/*
 * SplashScreen.java
 *
 * Created on 16.02.2007, 14:23
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends Canvas implements Runnable, CommandListener {
    
    private Display display;
    private Displayable parentView;
    
    private String capt;
    private int pos=-1;
    
    private int width;
    private int height;
    
    public Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    private TimerTaskClock tc;
    
    //private StaticData sd=StaticData.getInstance();
    private Config cf=Config.getInstance();
    
    private static SplashScreen instance;

    public int keypressed=0;
    
    public static SplashScreen getInstance(){
        if (instance==null) 
            instance=new SplashScreen();
        return instance;
    }
    
    /** Creates a new instance of SplashScreen */
    private SplashScreen() {
        setFullScreenMode(cf.fullscreen);
    }
    
    public SplashScreen(
            Display display, 
            ComplexString status, 
            char exitKey) 
    {
        this.status=status;
        this.display=display;
        kHold=this.exitKey=exitKey;
        
        parentView=display.getCurrent();

        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);
        repaint();
        //serviceRepaints();

        new Thread(this).start();
        
        tc=new TimerTaskClock();
        
        setFullScreenMode(cf.fullscreen);

        System.gc();   // heap cleanup
    }
    
    
    public void paint(Graphics g){
        width=getWidth();
        height=getHeight();
        
        g.setColor(ColorScheme.BLK_BGND);
        g.fillRect(0,0, width, height);

        if (img!=null) 
            g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);
        
        if (pos==-1) {
            Font f=FontCache.getClockFont();

            int h=f.getHeight()+1;

            int y=0;

            g.setColor(ColorScheme.BLK_INK);
            g.translate(0, y);
            status.drawItem(g, 0, false, false);

            String time=Time.localTime();
            int tw=f.stringWidth(time);

            g.translate(width/2, height);

            if (ColorScheme.BLK_INK!=0x010101) {
                g.setColor(ColorScheme.BLK_INK);
                g.setFont(f);
                g.drawString(time, 0, 0, Graphics.BOTTOM | Graphics.HCENTER);
            }
        } else {
            Font f=FontCache.getBalloonFont();

            int h=4;
            int xp=pos*width/100;
            int xt=(width/2);
            int y=height-h-2;
            int yt=y-f.getHeight();

            g.setColor(ColorScheme.PGS_REMAINED);
            g.fillRect(1, y, width, h);

            g.setFont(f);
            g.drawString(capt, xt, yt, Graphics.TOP|Graphics.HCENTER);

            g.setColor(ColorScheme.PGS_COMPLETE);
            g.setClip(1, y+1, xp, h-2);
            g.fillRect(1, y+1, width-2,h-2);
        }
    }
    
    public void setProgress(int progress) {
        pos=progress;
        repaint();
        //serviceRepaints();
    }

    public void setFailed(){
        setProgress("Failed", 0);
    }
    public void setProgress(String caption, int progress){
        capt=caption;
        System.out.println(capt);
	setProgress(progress);
    }
    public int getProgress(){
        return pos;
    }
    
    // close splash
    private Command cmdExit=new Command("Hide Splash", Command.BACK, 99);
    
    public void setExit(Display display, Displayable nextDisplayable){
        this.display=display;
        parentView=nextDisplayable;
        setCommandListener(this);
        addCommand(cmdExit);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit) 
            close();
    }
    
    public void close(){
        if (parentView!=null) 
            display.setCurrent(parentView);
        parentView=null;
        repaint();
        //serviceRepaints();
        img=null;
        instance=null; // �??���������� ���??��
        System.gc();
    }

    public void run() {
        try {
            img=BombusMod.splash;
            if (img==null)
                img=Image.createImage("/images/splash.png");
        } catch (Exception e) {}
        
        display.setCurrent(this);
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
    
    
    public void keyPressed(int keyCode) { 
        if (pos>24)
            close();
        
        kHold=0;
    }

    protected void keyRepeated(int keyCode) { 
        if (kHold==0)
            if (keyCode==exitKey) 
                destroyView(); 
    }

    private void destroyView(){
        status.setElementAt(null,6);
        if (display!=null) 
            display.setCurrent(parentView);
        img=null;
        tc.stop();
//#ifdef AUTOSTATUS
//#         Roster roster=StaticData.getInstance().roster;
//#         if (roster.autoAway && cf.autoAwayType==Config.AWAY_LOCK) {
//#             int newStatus=roster.oldStatus;
//#             ExtendedStatus es=StatusList.getInstance().getStatus(newStatus);
//#             String ms=es.getMessage();
//#             roster.autoAway=false;
//#             roster.autoXa=false;
//#             roster.sendPresence(newStatus, ms);
//#         }
//#endif
        System.gc();
    }
}
