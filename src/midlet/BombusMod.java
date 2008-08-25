/*
 * BombusMod.java
 *
 * Created on 5.01.2005, 21:46
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
import Account.Account;
import Account.AccountSelect;
import Colors.ColorTheme;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import Client.*;
import Info.Version;
import java.util.Vector;
import util.StringLoader;

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
    ColorTheme ct=ColorTheme.getInstance();
    SplashScreen s=SplashScreen.getInstance();

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
            s.img=Image.createImage("/images/splash.png");
        } catch (Exception e) {
            s.img=null;
        }
//#ifdef PLUGINS
//#         getPlugins();
//#endif
        s.setProgress(3);
        s.getKeys();
        
        //s.setProgress(5);
        //ColorTheme ct=ColorTheme.getInstance();

        s.setProgress(7);
        s.setProgress(Version.getVersionNumber(),10);
        
        SR.loaded();
        s.setProgress(12);
        


	Config cf=Config.getInstance();
        s.setProgress(15);
        
//#ifdef AUTOTASK
//#         sd.autoTask=new AutoTask(display);
//#         s.setProgress(17);
//#endif

        sd.roster=new Roster(display);
        s.setProgress(20);
        
        boolean selAccount=( (cf.accountIndex<0) || s.keypressed!=0);
        if (selAccount) 
            s.setProgress("Entering setup",22);
        
        s.setProgress(24);
        if (!selAccount && cf.autoLogin)
            Account.loadAccount(cf.autoLogin, cf.accountIndex); // connect whithout account select
        else
            new AccountSelect(display, sd.roster, true);
    }

    public void destroyApp(boolean unconditional) { }

    public void hideApp(boolean hide) {
	if (hide)
            display.setCurrent(null);
	else if (isMinimized)
            display.setCurrent(display.getCurrent());
        isMinimized=hide;
    }
    
    public static BombusMod getInstance() {
        return instance;
    }

//#ifdef PLUGINS
//#     private void getPlugins () {
//# 	Vector defs[]=new StringLoader().stringLoader("/modules.txt", 2);
//#         if (defs!=null) {
//#             for (int i=0; i<defs[0].size(); i++) {
//#                 String name      =(String) defs[0].elementAt(i);
//#                 String value     =(String) defs[1].elementAt(i);
//# 
//#                 boolean state=value.equals("true");
//# 
//#                 if (name.equals("Archive")) {
//#                     sd.Archive=state;
//#                 } else if(name.equals("ChangeTransport")) {
//#                     sd.ChangeTransport=state;
//#                 } else if(name.equals("Console")) {
//#                     sd.Console=state;
//#                 } else if(name.equals("FileTransfer")) {
//#                     sd.FileTransfer=state;
//#                 } else if(name.equals("History")) {
//#                     sd.History=state;
//#                 } else if(name.equals("ImageTransfer")) {
//#                     sd.ImageTransfer=state;
//#                 } else if(name.equals("PEP")) {
//#                     sd.PEP=state;
//#                 } else if(name.equals("Privacy")) {
//#                     sd.Privacy=state;
//#                 } else if(name.equals("IE")) {
//#                     sd.IE=state;
//#                 } else if(name.equals("Colors")) {
//#                     sd.Colors=state;
//#                 } else if(name.equals("Adhoc")) {
//#                     sd.Adhoc=state;
//#                 }
//#             }
//#         }
//#     }
//#endif
}
