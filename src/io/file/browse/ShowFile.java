/*
 * ShowFile.java
 *
 * Created on 9.10.2006, 14:00
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

//#ifdef FILE_IO
//# 
//# package io.file.browse;
//# 
//# import Client.StaticData;
//# import io.file.FileIO;
//# import java.io.IOException;
//#ifndef NOMMEDIA
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.media.Player;
//#endif
//# import javax.microedition.lcdui.Image;
//# import locale.SR;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.ImageItem;
//# import ui.controls.form.MultiLine;
//# 
//# /**
//#  *
//#  * @author User
//#  */
//# public class ShowFile extends DefForm {
//#     
//#     public static final int TYPE_TEXT = 3;
//#     public static final int TYPE_IMAGE = 2;
//#     public static final int TYPE_MEDIA = 1;
//# 
//#     private int len;
//#     private byte[] rawBytes;
//#     private String txtData;
//#ifndef NOMMEDIA
//#     private Player pl;
//#endif
//#     int type;
//# 
//#     public ShowFile(String fileName, int type) {
//#         super(fileName);
//#         this.type = type;
//#         load(fileName);
//#ifndef NOMMEDIA        
//#         if (type == TYPE_MEDIA) {
//#             play(fileName);
//#         }
//#endif        
//#         if (type == TYPE_IMAGE) {
//#             view(fileName);
//#         }
//#         if (type == TYPE_TEXT) {
//#             read(fileName);
//#         }
//#     }
//# 
//#     private void load(String file) {
//#         FileIO f = FileIO.createConnection(file);
//#         if (type == TYPE_TEXT) {
//#             txtData = f.fileReadUtf();
//#         } else {
//#             rawBytes = f.readFile();
//#             len = rawBytes.length;
//#         }
//#         try {
//#             f.close();
//#         } catch (IOException ex) {
//#             if (StaticData.Debug)
//#                 ex.printStackTrace();
//#         }
//#     }
//# 
//#     private void view(String file) {
//# 		try{
//# 			Image img = Image.createImage(rawBytes, 0, len);
//# 			itemsList.addElement(new ImageItem(img, "minimized, size: " + String.valueOf(len) + "b."));
//# 		} catch(OutOfMemoryError eom){
//# 		} catch (Exception e) {}
//#     }
//# 
//#     private void read(String file) {
//#         itemsList.addElement(new MultiLine(file + "(" + txtData.length() + " bytes)", txtData));
//#     }
//# 
//#     public void cmdOk() {
//#ifndef NOMMEDIA
//#         if (type == TYPE_MEDIA) {
//#             try {
//#                 pl.stop();
//#                 pl.close();
//#             } catch (Exception e) {
//#             }
//#         }
//#endif
//#         destroyView();
//#     }
//# 
//#ifndef NOMMEDIA    
//#     private void play(String file) {
//#         try {
//#             pl = Manager.createPlayer("file://" + file);
//#             pl.realize();
//#             pl.start();
//#         } catch (IOException ex) {
//#             //ex.printStackTrace();
//#         } catch (MediaException ex) {
//#             //ex.printStackTrace();
//#         }
//# 
//#         itemsList.addElement(new MultiLine("Play", "Playing" + " " + file));
//#     }
//# 
//#     public String touchLeftCommand() {
//#         return (type == 1) ? SR.MS_STOP : SR.MS_OK;
//#     }
//#endif        
//# }
//# 
//#endif
