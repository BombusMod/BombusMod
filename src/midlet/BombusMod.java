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
import Account.AccountForm;
import Account.AccountSelect;
import Colors.ColorTheme;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import Client.*;
import Info.Version;
import ui.controls.AlertBox;
//#ifdef PLUGINS
//# import java.util.Vector;
//# import util.StringLoader;
//#endif
//#ifdef LIGHT_CONFIG
//# import LightControl.*;
//#endif

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class BombusMod extends MIDlet implements Runnable {

    private Display display;    // The display for this MIDlet
    private boolean isRunning;
    private boolean isMinimized;
    StaticData sd = StaticData.getInstance();
    ColorTheme ct = ColorTheme.getInstance();
//#ifdef LIGHT_CONFIG
//#     LightConfig lcf;
//#endif    
    SplashScreen s;
    public static Image splash;
    private static BombusMod instance;

    /** Entry point  */
    public void startApp() {
        if (isRunning) {
            hideApp(false);
            return;
        }
//#ifdef LIGHT_CONFIG
//#ifdef PLUGINS
//#     if (StaticData.getInstance().lightConfig)
//#endif
//#         lcf = LightConfig.getInstance();
//#endif

        instance = this;
        display = Display.getDisplay(this);
        s = SplashScreen.getInstance();
        s.setProgress("Loading", 3); // this message will not be localized

        isRunning = true;
        new Thread(this).start();
    }

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    }

    public void run() {
//#ifdef LIGHT_CONFIG        
//#ifdef PLUGINS        
//#     if (StaticData.getInstance().lightConfig)        
//#endif                       
//#         CustomLight.setLight(lcf.light_control);
//#endif    

        try {
            s.img = Image.createImage("/images/splash.png");
        } catch (Exception e) {
            s.img = null;
        }
//#ifdef PLUGINS
//#         getPlugins();
//#endif
        s.setProgress(3);
        s.getKeys();

        s.setProgress(7);
        s.setProgress(Version.getVersionNumber(), 10);

        SR.loaded();
        s.setProgress(12);

        Config cf = Config.getInstance();
        s.setProgress(15);

//#ifdef AUTOTASK
//#         sd.autoTask = new AutoTask();
//#         s.setProgress(17);
//#endif
        sd.canvas = VirtualCanvas.getInstance();
        sd.canvas.setMIDlet(this);
        sd.roster = new Roster();
        sd.canvas.homeList = sd.roster;

        s.setProgress(20);

        boolean selAccount = ((cf.accountIndex < 0) || s.keypressed);
        if (selAccount) {
            s.setProgress("Entering setup", 22);
        }

        try {
            s.setProgress(24);
            if (!selAccount && cf.autoLogin) {
                Account.loadAccount(cf.autoLogin, cf.accountIndex); // connect whithout account select
            } else {
                AccountSelect as = new AccountSelect(true);
                if (as.itemsList.isEmpty()) {
//#ifdef IMPORT_EXPORT
//#ifdef PLUGINS
//#             if (StaticData.getInstance().IE) {
//#endif
//#                     new IE.Accounts("/def_accounts.txt", 0, true);
//#ifdef PLUGINS
//#             }
//#endif
//#                     as.loadAccounts();
//#endif

                }
                VirtualList next = as.itemsList.isEmpty() ? (VirtualList) new AccountForm(as, null) : as;                
                s.setExit(next);
                s.close();                
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox error = new AlertBox(SR.MS_ERROR, e.toString() + ": " + e.getMessage()) {

                public void yes() {
                    notifyDestroyed();
                }

                public void no() {
                }
            };
        }
//#ifdef LIGHT_CONFIG        
//#ifdef PLUGINS        
//#     if (StaticData.getInstance().lightConfig)        
//#endif                       
//#         CustomLight.keyPressed();
//#endif        
    }

    public void destroyApp(boolean unconditional) {
    }

    public void hideApp(boolean hide) {
        if (hide) {
            display.setCurrent(null);
        } else if (isMinimized) {
            midlet.BombusMod.getInstance().setDisplayable(display.getCurrent());
        }
        isMinimized = hide;
    }

    public static BombusMod getInstance() {
        return instance;
    }

//#ifdef PLUGINS
//# 
//#     private void getPlugins() {
//#         Vector defs[] = new StringLoader().stringLoader("/modules.txt", 2);
//#         if (defs != null) {
//#             int j = defs[0].size();
//#             for (int i = 0; i < j; i++) {
//#                 String name = (String) defs[0].elementAt(i);
//#                 String value = (String) defs[1].elementAt(i);
//# 
//#                 boolean state = value.equals("true");
//# 
//#                 if (name.equals("Archive")) {
//#                     sd.Archive = state;
//#                 } else if (name.equals("ChangeTransport")) {
//#                     sd.ChangeTransport = state;
//#                 } else if (name.equals("Console")) {
//#                     sd.Console = state;
//#                 } else if (name.equals("FileTransfer")) {
//#                     sd.FileTransfer = state;
//#                 } else if (name.equals("History")) {
//#                     sd.History = state;
//#                 } else if (name.equals("ImageTransfer")) {
//#                     sd.ImageTransfer = state;
//#                 } else if (name.equals("PEP")) {
//#                     sd.PEP = state;
//#                 } else if (name.equals("Privacy")) {
//#                     sd.Privacy = state;
//#                 } else if (name.equals("IE")) {
//#                     sd.IE = state;
//#                 } else if (name.equals("Colors")) {
//#                     sd.Colors = state;
//#                 } else if (name.equals("Adhoc")) {
//#                     sd.Adhoc = state;
//#                 } else if (name.equals("Stats")) {
//#                     sd.Stats = state;
//#                 } else if (name.equals("ClientsIcons")) {
//#                     sd.ClientsIcons = state;
//#                 } else if (name.equals("UserKeys")) {
//#                     sd.UserKeys = state;
//#                 } else if (name.equals("Upgrade")) {
//#                     sd.Upgrade = state;
//#                 } else if (name.equals("Juick")) {
//#                     sd.Juick = state;
//#                 }
//#             }
//#         }        
//#     }
//#endif
    public Display getDisplay() {
        return display;
    }

    public Displayable getCurrentDisplayable() {
        return getDisplay().getCurrent();
    }

    public void setDisplayable(Displayable d) {
        if (d == null) {
            d = sd.canvas;
        }
        getDisplay().setCurrent(d);
    }
}
