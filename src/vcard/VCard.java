/*
 * vCard.java
 *
 * Created on 24.09.2005, 1:24
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

package vcard;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.*;
import java.io.*;
import util.StringLoader;
import util.strconv;

/**
 *
 * @author EvgS
 */
public class VCard {

    
    public final static int FN=0;
    public final static int NICKNAME=1;
    /*
    public final static int BDAY=2;
    public final static int GENDER=3;
    public final static int GIVEN=4;
    public final static int MIDDLE=5;
    public final static int FAMILY=6;
    public final static int STREET=7;
    public final static int EXTADR=8;
    public final static int LOCALITY=9;
    public final static int REGION=10;
    public final static int PCODE=11;
    public final static int CTRY=12;
    public final static int HOME=13;
    public final static int NUMBER=14;
    public final static int USERID=15;
    public final static int TITLE=16;
    public final static int ROLE=17;
    public final static int ORGNAME=18;
    public final static int ORGUNIT=19;
    public final static int URL=20;
    public final static int DESC=21;
    */
    public static Vector vCardFields;
    public static Vector vCardFields2;
    public static Vector vCardLabels;
    
    private Vector vCardData;
    private String jid;
    private String id;
    
    public byte photo[];
	
    private boolean empty=true;

    private String photoType=null;
    
    public boolean hasPhoto;

    
    /** Creates a new instance of vCard */
    public VCard() {
        if (vCardFields==null) 
            fieldsLoader();
    }
    
    public VCard(JabberDataBlock data) {
        this();
        jid=data.getAttribute("from");
	id=data.getAttribute("id");
        int itemsCount=getCount();
        vCardData=new Vector(itemsCount);
        vCardData.setSize(itemsCount);
        
        if (data==null) return; 
        if (data.getTypeAttribute().equals("error")) return;
        JabberDataBlock vcard=data.findNamespace("vCard", "vcard-temp");
        if (vcard==null) return; //"No vCard available" 
		
	empty=false;
        
        for (int i=0; i<itemsCount; i++){
            try {
                String f1=(String)VCard.vCardFields.elementAt(i);
                String f2=(String)VCard.vCardFields2.elementAt(i);
                
                JabberDataBlock d2=(f2==null)?vcard:vcard.getChildBlock(f2);
                
                String field=d2.getChildBlockText(f1);
                
                if (field.length()>0) setVCardData(i, field);
            } catch (Exception e) {/**/}
        }
        
       try {
           JabberDataBlock photoXML=vcard.getChildBlock("PHOTO");
           try {
                photoType=photoXML.getChildBlock("TYPE").getText();
           } catch (Exception e) {}
           photo=(byte[])photoXML.getChildBlock("BINVAL").getChildBlocks().lastElement();
           hasPhoto=true;
       } catch (Exception e) {}
    }

    public JabberDataBlock constructVCard(){
        JabberDataBlock vcardIq=new Iq(null, Iq.TYPE_SET, "vcard-set");
        JabberDataBlock vcardTemp=vcardIq.addChildNs("vCard", "vcard-temp");
        
        int itemsCount=getCount();
        
        for (int i=0; i<itemsCount; i++){
            String field=getVCardData(i);
            if (field==null) continue;
            
            String f1=(String)VCard.vCardFields.elementAt(i);
            String f2=(String)VCard.vCardFields2.elementAt(i);
            
            JabberDataBlock subLevel=vcardTemp;
            if (f2!=null) {
                subLevel=vcardTemp.getChildBlock(f2);
                if (subLevel==null) subLevel=vcardTemp.addChild(f2, null);
            }
            subLevel.addChild(f1, field);
            
        }
        if (photo!=null) {
            JabberDataBlock ph=vcardTemp.addChild("PHOTO", null);
            ph.addChild("BINVAL", strconv.toBase64(photo, -1));
            if (photoType!=null) {
                ph.addChild("TYPE", photoType);
            }
        }
        //System.out.println(vcard.toString());
        return vcardIq;
    }
    
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo=photo; }
    
    public String getNickName() { 
        String name=getVCardData(NICKNAME);
        if (name!=null)
            if (name!="")
                return name;
        return getVCardData(FN);
    }
    
    public static JabberDataBlock getQueryVCard(String to, String id ) 
    {
        JabberDataBlock req=new Iq(to, Iq.TYPE_GET, id);
        req.addChildNs("vCard", "vcard-temp" );

        return req;
    }
    
    public static void request(String jid, String id) {
        StaticData.getInstance().roster.setQuerySign(true); 
        StaticData.getInstance().roster.theStream.send(getQueryVCard(jid, "getvc"+id));
    }
    
    private void fieldsLoader(){
	Vector table[]=new StringLoader().stringLoader("/vcard.txt", 3);
               
	vCardFields=table[1];
        vCardFields2=table[0];
        vCardLabels=table[2];
        
    }
    public String getVCardData(int index) {
        return (String) vCardData.elementAt(index);
    }

    public void setVCardData(int index, String data) {
        vCardData.setElementAt(data, index);
    }
    
//#if FILE_IO
    public void setPhotoType() {
        this.photoType=getPhotoMIMEType();
    }
//#endif
    
    public int getCount(){ return vCardFields.size(); }

    public String getJid() { return jid; }

    public String getId() {
        return id;
    }
	
    public boolean isEmpty() {
        return empty;
    }
    
    public void dropPhoto() {
        photo=null;
        photoType=null;
        hasPhoto=false;
    }
    
    public void clearVCard() {
        vCardFields=null;
        vCardFields2=null;
        vCardLabels=null;

        vCardData=null;
        jid=null;
        id=null;

        dropPhoto();
    }
    
//#if FILE_IO
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
    
    public String getFileType() {
        String MIMEtype=getPhotoMIMEType();
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
