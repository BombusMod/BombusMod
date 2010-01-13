/*
 * CameraImage.java
 *
 * Created on 25.10.2006, 22:35
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

package images.camera;

import Client.Config;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

import locale.SR;

/**
 *
 * @author Evg_S
 */
public class CameraImage implements CommandListener{
    
    private Command cmdShot=new Command (SR.MS_CAMERASHOT, Command.OK, 1);
    private Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    
    private Display display;
    private Displayable parentView;
    
    private Player player;
    private VideoControl videoControl;
    
    //Form f;
    CameraImageListener imgListener;

    //private String sizes="encoding=jpeg&width=320&height=240"; //"width=800&height=600"
    private final String mode="encoding=jpeg";
    /** Creates a new instance of CameraImage */
    public CameraImage(Display display, CameraImageListener imgListener/*, String sizes*/) {
        this.display=display;
        parentView=display.getCurrent();
        this.imgListener=imgListener;

        //if (sizes!=null) this.sizes=sizes;
        
        int exp=0;
        try {
            String uri= Config.getInstance().NokiaS40 ? "capture://image" : "capture://video";
            player = Manager.createPlayer(uri);
            player.realize();
            
            videoControl = (VideoControl)player.getControl("VideoControl");
            
            Form form = new Form("Camera");
            Item item = (Item)videoControl.initDisplayMode(
                    GUIControl.USE_GUI_PRIMITIVE, null);
            form.append(item);
            form.addCommand(cmdShot);
            form.addCommand(cmdCancel);
            form.setCommandListener(this);
            display.setCurrent(form);
            
            player.start();
        } catch (Exception e) { 
            display.setCurrent(
                    new Alert("Error", e.toString(), null, null), 
                    parentView);
            e.printStackTrace(); 
        }
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdShot) {
            try {
                byte photo[]=videoControl.getSnapshot(mode);
                imgListener.cameraImageNotify(photo);
                photo=null;
            } catch (Exception e) { e.printStackTrace(); }
        }
        // Shut down the player.
        player.close();
        player = null;
        videoControl = null;

        display.setCurrent(parentView);

    }
}
