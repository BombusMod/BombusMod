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
package io.file.browse;

import Menu.Command;
import io.file.FileIO;
//#ifndef NOMMEDIA
import java.io.IOException;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
//#endif
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import util.Strconv;

/**
 *
 * @author User
 */
public class ShowFile extends DefForm {

    private int len;
    private byte[] b;
//#ifndef NOMMEDIA
    private Player pl;
//#endif
    int type;

    public ShowFile(String fileName, int type) {
        super(fileName);
        this.type = type;
        load(fileName);
//#ifndef NOMMEDIA        
        if (type == 1) {
            play(fileName);
        }
//#endif        
        if (type == 2) {
            view(fileName);
        }
        if (type == 3) {
            read(fileName);
        }
    }

    private void load(String file) {
        try {
            FileIO f = FileIO.createConnection(file);
            b = f.fileRead();
            len = b.length;
            f.close();
        } catch (Exception e) {
        }
    }

    private void view(String file) {
        Image img = Image.createImage(b, 0, len);
        itemsList.addElement(new ImageItem(img, "minimized, size: " + String.valueOf(len) + "b."));
    }

    private void read(String file) {
        String text = new String(b, 0, len);
        itemsList.addElement(new MultiLine(file + "(" + len + " bytes)", cf.cp1251
                ? Strconv.convCp1251ToUnicode(text) : text, sd.roster.getListWidth()));
    }

    public void cmdOk() {
//#ifndef NOMMEDIA
        if (type == 1) {
            try {
                pl.stop();
                pl.close();
            } catch (Exception e) {
            }
        }
//#endif
        destroyView();
    }

//#ifndef NOMMEDIA    
    private void play(String file) {
        try {
            pl = Manager.createPlayer("file://" + file);
            pl.realize();
            pl.start();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (MediaException ex) {
            //ex.printStackTrace();
        }

        itemsList.addElement(new MultiLine("Play", "Playing" + " " + file, sd.roster.getListWidth()));
    }

    public String touchLeftCommand() {
        return (type == 1) ? SR.MS_STOP : SR.MS_OK;
    }
//#endif        
}
