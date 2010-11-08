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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Vladimir Krukov
 */
public class VirtualCanvas extends Canvas implements CommandListener{
    
    private VirtualList list;
    public VirtualList homeList;

    public Command commandOk;
    public Command commandCancel;

    static VirtualCanvas instance;
    static MIDlet midlet;
    
    public static VirtualCanvas getInstance() {
        if (instance == null) {
            instance = new VirtualCanvas();
        }        
        return instance;
    }   
    
    private VirtualCanvas() {
        setFullScreenMode(Config.fullscreen);
        if (VirtualList.phoneManufacturer==Config.WINDOWS) {
            setTitle("BombusMod");
        }
    }
    
    public void setMIDlet(MIDlet midlet) {
        VirtualCanvas.midlet = midlet;
    }
    
    void commandState() {
        if (Config.fullscreen) {
            setCommandListener(null);

        } else {
            if (list != null) {
                setOk(list.touchLeftCommand());
                setCancel(list.touchRightCommand());
                setCommandListener(instance);
            }
        }
    }
    

    
    public void show(VirtualList virtualList) {
        if (virtualList == null)
            virtualList = getList();
        if (isShown()) {            
            list = virtualList;            
            repaint();            
        } else {
            list = virtualList;            
            Display.getDisplay(midlet).setCurrent(this);
            repaint();
        }         
        commandState();
    }
    
    public VirtualList getList() {
        return list == null ? homeList : list;
    }

    public void setList(VirtualList list) {
        this.list = list;
    }
    
    protected void paint(Graphics graphics) {
        list.width = getWidth();
        list.height = getHeight();
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
//#if (USE_ROTATOR)
        TimerTaskRotate.startRotate(-1, list);
//#endif
        setFullScreenMode(Config.fullscreen);
    }    
    
    protected void sizeChanged(int w, int h) {
        if (list != null) {
            list.sizeChanged(w, h);            
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == commandOk) 
            list.touchLeftPressed();
        if (c == commandCancel) 
            list.touchRightPressed();
    }

    public final void setOk(String title) {
        if (!Config.fullscreen) {
            if (commandOk != null) {
                removeCommand(commandOk);
            }
            if (title != null) {
                commandOk = new Command(title, Command.OK, 1);
                addCommand(commandOk);
            }

        }
    }
    
    public final void setCancel(String title) {
        if (!Config.fullscreen) {
            if (commandCancel != null) {
                removeCommand(commandCancel);
            }
            if (title != null) {
                commandCancel = new Command(title, Command.BACK, 99);
                addCommand(commandCancel);
            }
        }
    }
    
}


    //#if (USE_ROTATOR)    
class TimerTaskRotate implements Runnable {
    private int scrollLen;
    private int scroll; //wait before scroll * sleep
    private int balloon; // show balloon time

    private boolean scrollline;
    
    private VirtualList attachedList;
    
    private static TimerTaskRotate instance;
    
    public static void startRotate(int max, VirtualList list) {
        //Windows mobile J9 hanging test
        if (Config.getInstance().phoneManufacturer==Config.WINDOWS) {
            list.showBalloon=true;
            list.offset=0;
            return;
        }
        if (instance==null)  {
            instance=new TimerTaskRotate();
            new Thread(instance).start();
        }
        
        if (max<0) {
            //instance.destroyTask();
            list.offset=0;
            return;
        }
        
        synchronized (instance) {
            list.offset=0;
            instance.scrollLen=max;
            instance.scrollline=(max>0);
            instance.attachedList=list;
            instance.balloon  = 8;
            instance.scroll   = 4;
        }
    }
    
    public void run() {
        while (true) {
            try {  Thread.sleep(250);  } catch (Exception e) { instance=null; break; }

            synchronized (this) {
                if (scroll==0) {
                    if (        instance.scroll()
                            ||  instance.balloon()
                        )
                        try { attachedList.redraw(); } catch (Exception e) { instance=null; break; }
                } else {
                    scroll --;                    
                }
                if (VirtualList.reconnectRedraw) {
                    VirtualList.reconnectRedraw=false;
                    try { attachedList.redraw(); } catch (Exception e) { instance=null; break; }
                }
            }
        }
    }

    public boolean scroll() {
        synchronized (this) {
            if (scrollline==false || attachedList==null || scrollLen<0)
                return false;
            if (attachedList.offset>=scrollLen) {
                scrollLen=-1; attachedList.offset=0; scrollline = false;
            } else 
                attachedList.offset+=14;

            return true;
        }
    }
    
    public boolean balloon() {
        synchronized (this) {
            if (attachedList==null || balloon<0)
                return false;
            balloon--;
            attachedList.showBalloon=(balloon<8 && balloon>0);
            return true;
        }
    } 
    /*
    public void destroyTask(){
        synchronized (this) { 
            if (attachedList!=null) 
                attachedList.offset=0;
        }
    }*/
}
//#endif


