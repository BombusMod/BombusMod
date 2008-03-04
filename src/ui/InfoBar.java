/*
 * InfoBar.java
 *
 * Created on 4 Ноябрь 2007 г., 1:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import images.RosterIcons;

/**
 *
 * @author ad
 */
public class InfoBar extends ComplexString{
    
    public InfoBar(int size, Object first, Object second) {
        this (size);
        font=FontCache.getBalloonFont();
        if (first!=null) setElementAt(first,0);
        if (second!=null) setElementAt(second,1);
    }
    
    public InfoBar(Object obj) {
        this(1, obj, null);
    }
    
    public InfoBar(int size) {
        super (RosterIcons.getInstance());
        setSize(size);
    }   
}