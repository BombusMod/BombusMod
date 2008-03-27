/*
 * BombusMod.java
 *
 * Created on 5.01.2005, 21:46
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

/**
 *
 * @author Eugene Stahov
 */
package midlet;
//#ifdef AUTOTASK
//# import AutoTasks.AutoTask;
//#endif
import Client.Stats;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import ui.*;

import Client.*;
import Info.Version;

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class BombusMod extends MIDlet implements Runnable{
    
    private Display display;    // The display for this MIDlet
    private boolean isRunning;
    private boolean isMinimized;
    StaticData sd=StaticData.getInstance();
    SplashScreen s= SplashScreen.getInstance();

    public static Image splash;
    
    private static BombusMod instance; 

    public BombusMod() {
	instance=this; 
        display = Display.getDisplay(this);

        display.setCurrent(s);
        s.setProgress("Loading",3); // this message will not be localized
    }
    
    /** Entry point  */
    public void startApp() {
        
        if (isRunning) {
	    hideApp(false);
            return;
        }
        
        isRunning=true;

        new Thread(this).start();
    }
    
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() { }

    public void run(){
        try {
            Stats.getInstance();
        } catch (Exception e) { }
        
        s.setProgress(3);
        
        try {
            s.img=Image.createImage("/images/splash.png");
        } catch (Exception e) {
            s.img=null;
        }
        
        Colors cl=Colors.getInstance();
        s.setProgress(5);
        
        s.setProgress(Version.getVersionNumber(),7);

	Config cf=Config.getInstance();
        s.setProgress(12);
        
//#ifdef AUTOTASK
//#         sd.autoTask=new AutoTask(/*"", "", */display);
//#         s.setProgress(15);
//#endif

        boolean selAccount=( (cf.accountIndex<0) || s.keypressed!=0);
        if (selAccount) 
            s.setProgress("Entering setup",20);
	s.setProgress(23);

        sd.roster=new Roster(display);
        s.setProgress(25);

        if (!selAccount && cf.autoLogin) {
            // connect whithout account select
            selAccount=(Account.loadAccount(cf.autoLogin)==null);
	    if (!cf.autoLogin) 
                s.close();
        } else {
            new AccountSelect(display, true);
        }
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) { }

    public void hideApp(boolean hide) {
	if (hide) {
	    display.setCurrent(null);
	} else {
            if (isMinimized) {
                 display.setCurrent(display.getCurrent());
            }
	}
        isMinimized=hide;
    }
    
    public static BombusMod getInstance() {
        return instance;
    }
  
}
