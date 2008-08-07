/*
 * TransferImage.java
 *
 * Created on 7.08.2008, 23:47
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

package io.file.transfer;

import images.camera.CameraImage;
import images.camera.CameraImageListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;

/**
 *
 * @author ad
 */
public class TransferImage
        extends DefForm 
        implements CameraImageListener {

    private String to;
    private byte[] photo;
    private ImageItem photoItem;
    private LinkString selectFile;
    
    /** Creates a new instance of TransferImage */
    public TransferImage(final Display display, String recipientJid) {
        super(display, SR.MS_SEND_FILE);
        this.display=display;
        this.to=recipientJid;
        parentView=display.getCurrent();

        itemsList.addElement(new SimpleString(SR.MS_SEND_FILE_TO, true));
        itemsList.addElement(new SimpleString(recipientJid, false));
        
        selectFile=new LinkString(SR.MS_SELECT_FILE) { public void doAction() { initCamera(); } };
        itemsList.addElement(selectFile);
        
        moveCursorTo(2);
        attachDisplay(display);
    }
    
    public void initCamera() {
        new CameraImage(display, this);
    }

    public void cameraImageNotify(byte[] capturedPhoto) {
        this.photo=capturedPhoto;
        try {
            itemsList.removeElement(photoItem);
            Image photoImg=Image.createImage(photo, 0, photo.length);
            photoItem=new ImageItem(photoImg, String.valueOf(photo.length)+" bytes");
            itemsList.addElement(photoItem);
        } catch (Exception e) { }
    }
    
    public void cmdOk() {
        try {
            TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), "photo.png", "my photo", true, photo);
            TransferDispatcher.getInstance().sendFile(task);
            //switch to file transfer manager
            (new io.file.transfer.TransferManager(display)).setParentView(parentView);
            return;
        } catch (Exception e) {}
    }
}
