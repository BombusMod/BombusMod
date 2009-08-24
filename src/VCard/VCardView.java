/*
 * VCardView.java
 *
 * Created on 25.05.2008, 21:27
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

package VCard;
import Client.Config;
import Client.Contact;
import Client.StaticData;
import javax.microedition.io.ConnectionNotFoundException;
import midlet.BombusMod;
import ui.controls.AlertBox;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import javax.microedition.lcdui.Displayable;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.LinkString;

/**
 *
 * @author ad
 */
public class VCardView
    extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {
    
    private Display display;

    private VCard vcard;
    private ImageItem photoItem;
    
    private Contact c;
    
    private SimpleString endVCard=new SimpleString(SR.MS_END_OF_VCARD, false);
    private SimpleString noVCard=new SimpleString(SR.MS_NO_VCARD, true);
    private SimpleString noPhoto=new SimpleString(SR.MS_NO_PHOTO, false);
    private SimpleString badFormat=new SimpleString(SR.MS_UNSUPPORTED_FORMAT, false);
    private SimpleString photoTooLarge=new SimpleString(SR.MS_PHOTO_TOO_LARGE, false);

    private LinkString refresh;
    
    private String url="";

//#ifdef CLIPBOARD
//#     ClipBoard clipboard  = ClipBoard.getInstance(); 
//#     Command cmdCopy      = new Command(SR.MS_COPY, Command.SCREEN, 1);
//#     Command cmdCopyPlus  = new Command("+ "+SR.MS_COPY, Command.SCREEN, 2);
//#endif
    Command cmdRefresh   = new Command(SR.MS_REFRESH, Command.SCREEN, 3);
//#if FILE_IO
    Command cmdSavePhoto = new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4);
//#endif
    Command cmdDelPhoto  = new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,5);

    /** Creates a new instance of VCardView */
    public VCardView(Display display, Displayable pView, Contact contact) {
        super(display, pView, contact.getNickJid());
        this.display = display;

        this.vcard=contact.vcard;
        this.c=contact;
        
        refresh=new LinkString(SR.MS_REFRESH) { public void doAction() { VCard.request(vcard.getJid(), vcard.getId().substring(5)); destroyView(); } };

        if (vcard.isEmpty()) {
            itemsList.addElement(noVCard);
            itemsList.addElement(refresh);
        } else {
            setPhoto();
            for (int index=0; index<vcard.getCount(); index++) {
                String data=vcard.getVCardData(index);
                String name=(String)VCard.vCardLabels.elementAt(index);
                if (data!=null && name!=null) {
                    if (!VCard.vCardFields.elementAt(index).equals("URL")) {
                        MultiLine nData=new MultiLine(name, data, super.superWidth);
                        nData.selectable=true;
                        itemsList.addElement(nData);
                    } else {
                        url=data;
                        LinkString nData=new LinkString(url) { public void doAction() {
                                try {BombusMod.getInstance().platformRequest(url);
                                } catch (ConnectionNotFoundException ex) {
                                    ex.printStackTrace();
                                } } };
                        itemsList.addElement(nData);
                    }
                }
            }
            itemsList.addElement(endVCard);
            itemsList.addElement(refresh);
        }

        //this.commandState();
        //setCommandListener(this);
        commandStateTest();
        attachDisplay(display);
        this.parentView=pView;
    }
    
    
    private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);
        } catch (Exception e) { }
        
         if (vcard.hasPhoto) {
            try {
                int length=vcard.getPhoto().length;
                if (length==1) {
                    vcard.setPhoto(null);
                    itemsList.addElement(photoTooLarge);
                } else {
                    Image photoImg=Image.createImage(vcard.getPhoto(), 0, length);
                    photoItem=new ImageItem(photoImg, "minimized, size: "+String.valueOf(length)+"b.");
                    if (length>10240)
                        photoItem.collapsed=true;
                    itemsList.insertElementAt(photoItem, 0);
                }
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }
    public void commandAction(Command c, Displayable d) {
        if (c==cmdDelPhoto) {
            vcard.dropPhoto(); 
            setPhoto();
        }
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
            return;
        }
//#if FILE_IO
        if (c==cmdSavePhoto) {
            new Browser(null, display, this, this, true);
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c == cmdCopy) {
//#             try {
//#                 clipboard.setClipBoard((((MultiLine)getFocusedObject()).getValue()==null)?"":((MultiLine)getFocusedObject()).getValue()+"\n");
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         
//#         if (c==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer(clipboard.getClipBoard())
//#                 .append("\n\n")
//#                 .append((((MultiLine)getFocusedObject()).getValue()==null)?"":((MultiLine)getFocusedObject()).getValue()+"\n");
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        super.commandAction(c, d);
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(vcard.getNickDate());
            FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
            file.fileWrite(vcard.getPhoto());
        }
    }
//#endif

    public void commandStateTest() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

        if (vcard!=null) {
            if (vcard.hasPhoto) {
//#if FILE_IO
                addCommand(cmdSavePhoto);
//#endif
                addCommand(cmdDelPhoto);
            }
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 if (!clipboard.isEmpty())
//#                     addCommand(cmdCopyPlus);
//#             }
//#endif
        }

        addCommand(cmdRefresh);
        addCommand(cmdCancel);
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() { return SR.MS_MENU; }
    
    public void cmdOk() { showMenu(); }
    
    public void cmdCancel() {
        clearVcard();
    }
//#endif
    
    public void cmdExit() {
        super.cmdCancel();
    }
    
    public void clearVcard() {
        new AlertBox(SR.MS_ACTION, SR.MS_DELETE+" "+SR.MS_VCARD+"?", display, this) {
            public void yes() {
                c.vcard=null;
                cmdExit();
            }
            public void no() {
                cmdExit();
            }
        };
    }
}
