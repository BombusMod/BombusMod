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
import javax.microedition.lcdui.Displayable;
import util.StringUtils;
import util.Translit;
import ui.Time;
//#endif
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
    private byte[] photo;
    
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
            photo=vcard.getPhoto();
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
        if (photo!=null)
            addCommand(cmdSavePhoto);
//#endif
        removeCommand(cmdOk);
        removeCommand(cmdSelect);
        addCommand(cmdDelPhoto);
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    
     private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
        } catch (Exception e) { }
        
         if (photo!=null) {
            try {
                Image photoImg=Image.createImage(photo, 0, photo.length);
                photoItem=new ImageItem(photoImg, String.valueOf(photo.length)+" bytes");
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
            vcard.photo=null; 
            photo=null; 
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
        if (photo!=null) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(getNickDate());
            FileIO file=FileIO.createConnection(pathSelected+filename+getFileType(getPhotoMIMEType()));
            file.fileWrite(photo);
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
    
    public String getPhotoMIMEType() {
        try {
             if (photo[0]==(byte)0xff &&
                photo[1]==(byte)0xd8 &&
                (photo[6]==(byte)'J' || photo[6]==(byte)'E' || photo[6]==(byte)'e') &&
                (photo[7]==(byte)'F' || photo[7]==(byte)'x' || photo[7]==(byte)'X') &&
                (photo[8]==(byte)'I' || photo[8]==(byte)'i') &&
                (photo[9]==(byte)'F' || photo[9]==(byte)'f')) {
                //System.out.println("image/jpeg");
                 return "image/jpeg";
             }
             
            if (photo[0]==(byte)0x89 &&
                photo[1]==(byte)'P' &&
                photo[2]==(byte)'N' &&
                photo[3]==(byte)'G') {
                //System.out.println("image/png");
                return "image/png";
            }
            
            if (photo[0]==(byte)'G' &&
                photo[1]==(byte)'I' &&
                photo[2]==(byte)'F') {
                //System.out.println("image/gif");
                 return "image/gif";
             }
             
            if (photo[0]==(byte)'B' &&
                photo[1]==(byte)'M') {
                //System.out.println("image/x-ms-bmp");
                 return "image/x-ms-bmp";
             }
            
        } catch (Exception e) {}
        //System.out.println("unknown MIME type");
        return null;
    }
    
    private String getFileType(String MIMEtype) {
        if (MIMEtype!=null) {
            if (MIMEtype=="image/jpeg") return ".jpg";
            if (MIMEtype=="image/png") return ".png";
            if (MIMEtype=="image/gif") return ".gif";
            if (MIMEtype=="image/x-ms-bmp") return ".bmp";
        }
        return ".jpg";
    }
//#endif
}
