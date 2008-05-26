/*
 * VCardView.java
 *
 * Created on 25 Май 2008 г., 21:27
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
        extends DefForm {

    private VCard vcard;
    private ImageItem photoItem;
    private byte[] photo;
    
    private BoldString noVCard=new BoldString("[No vCard available]");
    private SimpleString noPhoto=new SimpleString("[No photo available]");
    private SimpleString badFormat=new SimpleString("[Unsupported format]");
    
/*    private BoldString FNLine;
    private BoldString NICKNAMELine;
    private BoldString BDAYLine;
    private BoldString GENDERLine;
    private BoldString GIVENLine;
    private BoldString MIDDLELine;
    private BoldString FAMILYLine;
    private BoldString STREETLine;
    private BoldString EXTADRLine;
    private BoldString LOCALITYLine;
    private BoldString REGIONLine;
    private BoldString PCODELine;
    private BoldString CTRYLine;
    private BoldString HOMELine;
    private BoldString NUMBERLine;
    private BoldString USERIDLine;
    private BoldString TITLELine;
    private BoldString ROLELine;
    private BoldString ORGNAMELine;
    private BoldString ORGUNITLine;
    private BoldString URLLine;
    private BoldString DESCLine;

    private MultiLine FN;
    private MultiLine NICKNAME;
    private MultiLine BDAY;
    private MultiLine GENDER;
    private MultiLine GIVEN;
    private MultiLine MIDDLE;
    private MultiLine FAMILY;
    private MultiLine STREET;
    private MultiLine EXTADR;
    private MultiLine LOCALITY;
    private MultiLine REGION;
    private MultiLine PCODE;
    private MultiLine CTRY;
    private MultiLine HOME;
    private MultiLine NUMBER;
    private MultiLine USERID;
    private MultiLine TITLE;
    private MultiLine ROLE;
    private MultiLine ORGNAME;
    private MultiLine ORGUNIT;
    private MultiLine URL;
    private MultiLine DESC;
*/
    
    /** Creates a new instance of VCardView */
    public VCardView(Display display, VCard vcard, boolean editable) {
        super(display, SR.MS_VCARD+" "+vcard.getNickName());
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;
        
        
        if (vcard.isEmpty() && !editable) {
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
        }
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    
     private void setPhoto() {
        if (photo!=null) {
            try {
                Image photoImg=Image.createImage(photo, 0, photo.length);
                photoItem=new ImageItem(photoImg, String.valueOf(photo.length)+" bytes");
                itemsList.addElement(photoItem);
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }
}
