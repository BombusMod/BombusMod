/*
 * AccountRegister.java
 *
 * Created on 24.04.2005, 2:36
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
 *
 */

package Client;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;

/**
 *
 * @author Evg_S
 */
public class AccountRegister 
        implements         
            JabberListener,
            CommandListener,
            Runnable
{
    
    private Display display;
    private Displayable parentView;
    
    private Account raccount;
    private JabberStream theStream ;
    private SplashScreen spl=SplashScreen.getInstance();
    private Command cmdOK=new Command(SR.MS_OK,Command.BACK, 2);
    private Command cmdCancel=new Command(SR.MS_BACK,Command.BACK, 2);
    
    /** Creates a new instance of AccountRegister */
    public AccountRegister(Account account, Display display, Displayable parentView) {
        this.display=display;
        this.parentView=parentView;//display.getCurrent();
        
        
        raccount=account;
        spl.setProgress(SR.MS_STARTUP,5);
        display.setCurrent(spl);
        spl.addCommand(cmdCancel);
        spl.setCommandListener(this);
        
        new Thread(this).start();
    }
    public void run() {
        try {
            spl.setProgress(SR.MS_CONNECT_TO +raccount.getServer(),30);
            theStream= raccount.openJabberStream();
            theStream.setJabberListener( this );
            theStream.initiateStream();
        } catch( Exception e ) {
            e.printStackTrace();
            spl.setFailed();
        }

    }
    
    public void rosterItemNotify(){}
    
    public void connectionTerminated( Exception e ) {
        if( e != null ) {
            e.printStackTrace();
        }
    }

    public void beginConversation(String SessionId) {
        spl.setProgress(SR.MS_REGISTERING,60);
        IqRegister iq=new IqRegister(raccount.getUserName(),raccount.getPassword(), "regac");
        try {
            theStream.send(iq);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    public int blockArrived( JabberDataBlock data ) {
        //destroyView();
        if (data instanceof Iq) {
            theStream.close();
            int pgs=100;
            String type=data.getTypeAttribute();
            String mainbar=SR.MS_DONE; 
            if (type.equals("result")) {
                spl.removeCommand(cmdCancel);
                spl.addCommand(cmdOK);
            } else {
                pgs=0;
                mainbar=SR.MS_ERROR_ + XmppError.findInStanza(data).getName();
            }
            spl.setProgress(mainbar,pgs);
        }
        return JabberBlockListener.BLOCK_PROCESSED;
    }
    
    public void commandAction(Command c, Displayable d) {
        spl.setCommandListener(null);
        spl.removeCommand(cmdCancel);
        try {
            theStream.close();
        } catch (Exception e) { 
            //e.printStackTrace();
        }
        destroyView();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
