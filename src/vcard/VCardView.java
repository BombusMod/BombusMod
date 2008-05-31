/*
 * VCardView.java
 *
 * Created on 25 ��� 2008 �., 21:27
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

package vcard;
//#if FILE_IO
import Client.Config;
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
import util.Translit;
import ui.Time;
//#endif
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.BoldString;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;


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
    
    private SimpleString endVCard=new SimpleString("[end of vCard]");
    private BoldString noVCard=new BoldString("[No vCard available]");
    private SimpleString noPhoto=new SimpleString("[No photo available]");
    private SimpleString badFormat=new SimpleString("[Unsupported format]");
    
//#if FILE_IO
    protected Command cmdSavePhoto=new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4);
//#endif
    protected Command cmdDelPhoto=new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,5);
    
    /** Creates a new instance of VCardView */
    public VCardView(Display display, VCard vcard) {
        super(display, SR.MS_VCARD+" "+vcard.getNickName());
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;

        if (vcard.isEmpty()) {
            itemsList.addElement(noVCard);
        } else {
            setPhoto();
            for (int index=0; index<vcard.getCount(); index++) {
                String data=vcard.getVCardData(index);
                String name=(String)VCard.vCardLabels.elementAt(index);
                if (data!=null) {
                    itemsList.addElement(new BoldString(name));
                    MultiLine nData=new MultiLine(data);
                    nData.selectable=true;
                    itemsList.addElement(nData);
                }
            }
            itemsList.addElement(endVCard);
        }

//#if FILE_IO
        if (vcard.hasPhoto)
            addCommand(cmdSavePhoto);
//#endif
        removeCommand(cmdOk);
        removeCommand(cmdSelect);
        if (vcard.hasPhoto)
            addCommand(cmdDelPhoto);
        //moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    
     private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
        } catch (Exception e) { }
        
         if (vcard.hasPhoto) {
            try {
                Image photoImg=Image.createImage(vcard.getPhoto(), 0, vcard.getPhoto().length);
                photoItem=new ImageItem(photoImg, String.valueOf(vcard.getPhoto().length)+" bytes");
                itemsList.insertElementAt(photoItem, 0);
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
//#if FILE_IO
        if (c==cmdSavePhoto) {
            new Browser(null, display, this, true);
        }
//#endif
        super.commandAction(c, d);
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(getNickDate());
            FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
            file.fileWrite(vcard.getPhoto());
        }
    }
    
    private String getNickDate() {
        StringBuffer nickDate=new StringBuffer();
        nickDate.append("photo_");
//#ifdef TRANSLIT
        String userName=(vcard.getNickName()!=null)?vcard.getNickName():vcard.getJid();
        if (Config.getInstance().transliterateFilenames) {
            nickDate.append(Translit.translit(userName));
        } else {
            nickDate.append(userName);
        }
//#else
//#          if (vcard.getNickName()!=null) {
//#              nickDate.append(vcard.getNickName());
//#          } else nickDate.append(vcard.getJid());
//#endif
        nickDate.append("_");
        nickDate.append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif
}
