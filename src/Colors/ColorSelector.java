/*
 *ColorSelector.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package Colors;

import io.NvStorage;
import java.io.DataOutputStream;
import javax.microedition.lcdui.*;
import locale.SR;
import Colors.Colors;
import ui.*;

public class ColorSelector extends Canvas implements Runnable, CommandListener {

	static Font mfont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
	static int w, h;
        
        private Display display;
	Displayable parentView;
	Graphics G;
        private Colors cl;

	int cpos;
        String nowcolor;
        int red;
        int green;
        int blue;

        String val;

	int dy;
	int timer;
	boolean exit;

        private int value;
        int paramName;
        int ncolor;
        
        Command cmdOk = new Command(SR.MS_OK /*"OK"*/, Command.OK, 1);

        private int color;
        
        Command cmdCancel = new Command(SR.MS_CANCEL /*"Back"*/, Command.CANCEL, 99);

	public ColorSelector(Display display, int paramName) {
		super();
                this.display=display;
                parentView=display.getCurrent();
                this.color=ColorForm.COLORS[paramName];
                this.paramName=paramName;
                
		w = getWidth();
		h = getHeight();
                
                //System.out.println(color+" "+cl.getColorString(color));
//#if (COLORS)
//#                 red=ColorUtils.getColorInt(color,0);
//#                 green=ColorUtils.getColorInt(color,1);
//#                 blue=ColorUtils.getColorInt(color,2);
//#                 
//#             	//String s = cl.ColorToString(red, green, blue);
//#endif
        	cpos = 0;

		exit = false;
		(new Thread(this)).start();
                
                this.addCommand(cmdOk);
                this.addCommand(cmdCancel);
	
                this.setCommandListener(this);
		display.setCurrent(this);
	}


	protected void paint(Graphics g) {

		g.setColor(0xffffff);
		g.fillRect(0, 0, w, h);
		g.setFont(mfont);
//#if (COLORS)
//#             	String s = ColorUtils.ColorToString(red, green, blue);
//#                 //System.out.println(s);
//#else
                String s = " ";
//#endif
		g.setColor(0);
		g.setStrokeStyle(g.SOLID);
		g.drawRect(2, 2, 15, 15);
		g.setColor(red,green,blue);
		g.fillRect(4, 4, 12, 12);
		g.setColor(0x800000);
		g.drawString(s+" "+ColorForm.NAMES[paramName], 20, 5, g.TOP|g.LEFT);
                
                
                //draw red
 			int cred = red;
			int pxred = w/3-10;
			int pyred = h - 20;
			int phred = h - 52;
			int psred = (phred*cred)/255;
			g.setColor(0);
			g.setStrokeStyle(g.SOLID);
			g.fillRect(pxred-2, pyred-phred, 5, phred);
			g.drawString("R", pxred, pyred+2, g.TOP|g.HCENTER);
			g.setColor(0xff2020);
			g.fillRect(pxred-2, pyred-psred, 5, psred);
			if (cpos == 0) {
				g.setColor(0);
				g.setStrokeStyle(g.DOTTED);
				g.drawRect(pxred-7, pyred-phred-5, 15, 5+phred+15);
			}
                //draw green
 			int cvgreen = green;
			int pxgreen = w/2;
			int pygreen = h - 20;
			int phgreen = h - 52;
			int psgreen = (phgreen*cvgreen)/255;
			g.setColor(0);
			g.setStrokeStyle(g.SOLID);
			g.fillRect(pxgreen-2, pygreen-phgreen, 5, phgreen);
			g.drawString("G", pxgreen, pygreen+2, g.TOP|g.HCENTER);
			g.setColor(0x00ff00);
			g.fillRect(pxgreen-2, pygreen-psgreen, 5, psgreen);
			if (cpos == 1) {
				g.setColor(0);
				g.setStrokeStyle(g.DOTTED);
				g.drawRect(pxgreen-7, pygreen-phgreen-5, 15, 5+phgreen+15);
			}
                //draw blue
 			int cvblue = blue;
			int pxblue = w-(w/3-10);
			int pyblue = h - 20;
			int phblue = h - 52;
			int psblue = (phblue*cvblue)/255;
			g.setColor(0);
			g.setStrokeStyle(g.SOLID);
			g.fillRect(pxblue-2, pyblue-phblue, 5, phblue);
			g.drawString("B", pxblue, pyblue+2, g.TOP|g.HCENTER);
			g.setColor(0x4848ff);
			g.fillRect(pxblue-2, pyblue-psblue, 5, psblue);
			if (cpos == 2) {
				g.setColor(0);
				g.setStrokeStyle(g.DOTTED);
			g.drawRect(pxblue-7, pyblue-phblue-5, 15, 5+phblue+15);
			}
	}

	protected void keyPressed(int key) {
		switch (key) {
			case KEY_NUM2:
				timer = 7;
				dy = 1;
				movePoint();
				break;
			case KEY_NUM8:
				timer = 7;
				dy = -1;
				movePoint();
				break;
			case KEY_NUM4:
				cpos -= 1; if (cpos < 0) cpos = 2;
                                serviceRepaints();
				break;
			case KEY_NUM6:
				cpos += 1; if (cpos > 2) cpos = 0;
				break;
			case KEY_NUM5:
                                eventOk();
                                exit = true;
                                destroyView();
				break;
			case KEY_NUM0:
				exit = true;
				display.setCurrent(parentView);
				break;
                        default:
                            try {
                                switch (getGameAction(key)){
                                    case UP:
                                        timer = 7;
                                        dy = 1;
                                        movePoint();
                                        break;
                                    case DOWN:
                                        timer = 7;
                                        dy = -1;
                                        movePoint();
                                        break;
                                    case LEFT:
                                        cpos -= 1; if (cpos < 0) cpos = 2;
                                        break;
                                    case RIGHT:
                                        cpos += 1; if (cpos > 2) cpos = 0;
                                        break;
                                    case FIRE:
                                        eventOk();
                                        exit = true;
                                        destroyView();
                                        break;
                                        
                                    default:
                                        if (key=='5') {
                                            eventOk();
                                            exit = true;
                                            destroyView();
                                            break;
                                        }
                                }
                            } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
                            repaint();
                            serviceRepaints();
		}

	}

	protected void keyReleased(int key) {
		dy = 0;
	}

	public void run() {
		while (! exit) {
			try { Thread.sleep(35); } catch (Exception e) { }
			if (--timer > 0) continue;
			movePoint();
			movePoint();
		}
	}
        
	public void setValue(int vall) {
            this.value=vall;
            cl=Colors.getInstance();
            switch(paramName) {
                case 0:
                    cl.BALLOON_INK=value;
                    break;
                case 1:
                    cl.BALLOON_BGND=value; 
                    break;
                case 2:
                    cl.LIST_BGND=value; 
                    break;
                case 3:
                    cl.LIST_BGND_EVEN=value; 
                    break;
                case 4:
                    cl.LIST_INK=value; 
                    break;
                case 5:
                    cl.MSG_SUBJ=value; 
                    break;
                case 6:
                    cl.MSG_HIGHLIGHT=value; 
                    break;
                case 7:
                    cl.DISCO_CMD=value; 
                    break;
                case 8:
                    cl.BAR_BGND=value; 
                    break;
                case 9:
                    cl.BAR_BGND_BOTTOM=value; 
                    break;
                case 10:
                    cl.BAR_INK=value; 
                    break;
                case 11:
                    cl.CONTACT_DEFAULT=value; 
                    break;
                case 12:
                    cl.CONTACT_CHAT=value; 
                    break;
                case 13:
                    cl.CONTACT_AWAY=value; 
                    break;
                case 14:
                    cl.CONTACT_XA=value; 
                    break;
                case 15:
                    cl.CONTACT_DND=value; 
                    break;
                case 16:
                    cl.GROUP_INK=value; 
                    break;
                case 17:
                    cl.BLK_INK=value; 
                    break;
                case 18:
                    cl.BLK_BGND=value; 
                    break;
                case 19:
                    cl.MESSAGE_IN=value; 
                    break;
                case 20:
                    cl.MESSAGE_OUT=value; 
                    break;
                case 21:
                    cl.MESSAGE_PRESENCE=value; 
                    break;
                case 22:
                    cl.MESSAGE_AUTH=value; 
                    break;
                case 23:
                    cl.MESSAGE_HISTORY=value; 
                    break;
                case 24:
                    cl.PGS_REMAINED=value; 
                    break;
                case 25:
                    cl.PGS_COMPLETE=value; 
                    break;
                //case 26: cl.PGS_BORDER=value; break;
                //case 27: cl.PGS_BGND=value; break;
                case 26:
                    cl.HEAP_TOTAL=value; 
                    break;
                case 27:
                    cl.HEAP_FREE=value; 
                    break;
                case 38:
                    cl.CURSOR_BGND=value; 
                    break;
                case 29:
                    cl.CURSOR_OUTLINE=value; 
                    break;                    
                case 30:
                    cl.SCROLL_BRD=value; 
                    break;
                case 31:
                    cl.SCROLL_BAR=value; 
                    break;
                case 32:
                    cl.SCROLL_BGND=value; 
                    break;
                case 33:
                    cl.CONTACT_J2J=value; 
                    break;
//#if NICK_COLORS
//#                 case 34:
//#                     cl.MESSAGE_IN_S=value; 
//#                     break;
//#                 case 35:
//#                     cl.MESSAGE_OUT_S=value; 
//#                     break;
//#                 case 36:
//#                     cl.MESSAGE_PRESENCE_S=value; 
//#                     break;
//#endif
            }
//#if (COLORS)
//#             ColorScheme.saveToStorage();
//#endif
	}

	private void movePoint() {
		if (dy == 0) return;
                switch (cpos) {
                    case 0:
                        red=dy+red;
                        if (red>255) red=0;
                        if (red<0) red=255;
                        break;
                    case 1:
                        green=dy+green;
                        if (green>255) green=0;
                        if (green<0) green=255;
                        break;
                    case 2:
                        blue=dy+blue;
                        if (blue>255) blue=0;
                        if (blue<0) blue=255;
                        break;
                }
		repaint();
	}

    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) {
            exit = true;
	    destroyView();
	    return;
	}
	if (c==cmdOk) {
            eventOk();
            destroyView();
            return;
	}
    }
    
    private void eventOk () {
//#if (COLORS)
//#         String val = ColorUtils.ColorToString(red, green, blue);
//#         
//#         int finalColor=ColorUtils.getColorInt(val);
//#         //System.out.println(val);
//#         
//#         setValue(finalColor);
//#         ColorForm.COLORS[paramName]=finalColor;
//#         ColorForm.IMAGES[paramName]=ColorForm.imageData(finalColor);
//#         
//#         ColorForm.updateItem(paramName);
//#endif
        exit = true;
    }
    
    public void destroyView()	{
        if (display!=null)   display.setCurrent(parentView);
        //new ColorForm(display);
    }
        
}
