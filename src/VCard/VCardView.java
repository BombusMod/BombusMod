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
import javax.microedition.io.ConnectionNotFoundException;
import midlet.BombusMod;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import Menu.MenuCommand;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.VirtualList;
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
//#     MenuCommand cmdCopy      = new MenuCommand(SR.MS_COPY, MenuCommand.SCREEN, 1);
//#     MenuCommand cmdCopyPlus  = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 2);
//#endif
    MenuCommand cmdRefresh   = new MenuCommand(SR.MS_REFRESH, MenuCommand.SCREEN, 3);
//#if FILE_IO
    MenuCommand cmdSavePhoto = new MenuCommand(SR.MS_SAVE_PHOTO, MenuCommand.SCREEN,4);
//#endif
    MenuCommand cmdDelPhoto  = new MenuCommand(SR.MS_CLEAR_PHOTO, MenuCommand.SCREEN,5);

    /** Creates a new instance of VCardView
     * @param contact
     */
    public VCardView(Contact contact) {
        super(contact.getNickJid(), false);
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
//#ifdef DEBUG                                    
//#                                     ex.printStackTrace();
//#endif                                    
                                } } };
                        itemsList.addElement(nData);
                    }
                }
            }
            itemsList.addElement(endVCard);
            itemsList.addElement(refresh);
            show();
        }
        
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
    public void menuAction(MenuCommand c, VirtualList d) {
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
            new Browser(null, this, true);
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
        super.menuAction(c, d);
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

    public void commandState() {
        menuCommands.removeAllElements();

        if (vcard!=null) {
            if (vcard.hasPhoto) {
//#if FILE_IO
                addMenuCommand(cmdSavePhoto);
//#endif
                addMenuCommand(cmdDelPhoto);
            }
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addMenuCommand(cmdCopy);
//#                 if (!clipboard.isEmpty())
//#                     addMenuCommand(cmdCopyPlus);
//#             }
//#endif
        }

        addMenuCommand(cmdRefresh);
        addMenuCommand(cmdCancel);
    }
    

    public String touchLeftCommand() { return SR.MS_MENU; }
    public void touchLeftPressed() { showMenu(); }
    
    public void cmdOk() { showMenu(); }

    
    /*
    public void clearVcard() {
        new AlertBox(SR.MS_ACTION, SR.MS_DELETE+" "+SR.MS_VCARD+"?") {
            public void yes() {
                c.vcard=null;                
            }
            public void no() {                
            }
        };
    }*/
}
