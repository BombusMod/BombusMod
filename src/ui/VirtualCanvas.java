/*
 * NativeCanvas.java
 *
 * Created on 31 Август 2009 г., 19:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import Client.Config;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import locale.SR;

/**
 *
 * @author Vladimir Krukov
 */
public class VirtualCanvas extends Canvas implements CommandListener{
    private VirtualList list;

    public Command commandOk;
    public Command commandCancel;

    static VirtualCanvas instance;
    public static VirtualCanvas getInstance() {
        if (instance == null) {
            instance = new VirtualCanvas();
            instance.setFullScreenMode(Config.fullscreen);
            instance.setOk(SR.MS_MENU);
            instance.setCancel(SR.MS_ACTION);
            instance.setCommandListener(instance);
        }
        return instance;
    }

    
    public void show(VirtualList virtualList) {
        if (this == midlet.BombusMod.getInstance().getDisplay().getCurrent()
                && isShown()) {
            if (null != list) {
                list.hideNotify();
            }
            list = virtualList;
            list.showNotify();
            repaint();
            midlet.BombusMod.getInstance().getDisplay().setCurrent(this);

        } else {
            list = virtualList;
            midlet.BombusMod.getInstance().getDisplay().setCurrent(this);
        }        
    }
    public VirtualList getList() {
        return list;
    }

    protected void paint(Graphics graphics) {
        list.paint(graphics);
    }
    protected void keyPressed(int keyCode) {
        list.keyPressed(keyCode);           
    }
    protected final void keyRepeated(int keyCode){
        list.keyRepeated(keyCode);           
    }
    protected void keyReleased(int keyCode) {
        list.keyReleased(keyCode);           
    }
    
    protected void pointerPressed(int x, int y) {
        list.pointerPressed(x, y);        
    }
    protected void pointerDragged(int x, int y) {
        list.pointerDragged(x, y);         
    }
    protected void pointerReleased(int x, int y) {
        list.pointerReleased(x, y);           
    }

    protected void showNotify() {
        setFullScreenMode(Config.fullscreen);
        list.showNotify();        
    }
    protected void hideNotify() {
        if (list != null) {
            list.hideNotify();
        }
    }

        
    
    protected void sizeChanged(int w, int h) {        
        if (list != null)
        list.sizeChanged(w, h);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == commandOk) list.touchLeftPressed();
        if (c == commandCancel) list.touchRightPressed();
    }

    public final void setOk(String title) {
        if (!Config.fullscreen) {
        if (commandOk != null) removeCommand(commandOk);
        commandOk = null;
        commandOk = new Command(title, Command.OK, 1);
        addCommand(commandOk);
        }
       // setCommandListener(this);
    }
    public final void setCancel(String title) {
        if (!Config.fullscreen) {
        if (commandCancel != null) removeCommand(commandCancel);
        commandCancel = null;
        commandCancel = new Command(title, Command.BACK, 99);
        addCommand(commandCancel);
        }
       // setCommandListener(this);
    }

}

