/*
 * VirtualElement.java
 *
 * Created on 29.03.2005, 0:13
 *
 * Copyright (c) 2005-2010, Eugene Stahov (evgs), http://bombus-im.org
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

package ui;
import Colors.ColorTheme;
import javax.microedition.lcdui.*;

/**
 * интерфейс виртуального элемента списка.
 * @author Eugene Stahov
 */
public interface VirtualElement {
    
    /**
     * высота элемента
     * @return высота элемента в пикселах
     */
    public int getVHeight();
    
    /**
     * ширина элемента
     * @return ширина элемента в пикселах
     */
    public int getVWidth();
    
    /**
     * 
     * цвет заполнения фона элемента
     * фон закрашивается автоматически перед вызовом drawItem
     * @return RGB-цвет заполнения фона элемента
     */
    public int getColorBGnd(); 
    
    /**
     * цвет чернил элемента
     * устанавливается перед вызовом drawItem
     * @return RGB-цвет чернил элемента
     */
    public int getColor(); 
    
    /**
     * 
     * отрисовка элемента. перед вызовом устанавливаются 
     * трансляция экранных координат <i>translate(x,y)</i> в позицию элемента
     * и обрезание <i>setClip(0,0,width,height)</i>. 
     * 
     * фон закрашивается автоматически перед вызовом drawItem
     * @param g контекст отрисовки элемента
     * @param ofs горизонтальное смещение скроллируемой части элемента
     * @param selected признак выбранного курсором элемента
     */
    public void drawItem(Graphics g, int ofs, boolean selected);

    /**
     * информация для всплывающего окна
     * @return 
     */
    public String getTipString();
    /**
     * Callback-вызов, осуществляемый при выполнении OK для выделенного курсором элемента
     */
    public void onSelect();

    public boolean isSelectable();

    public boolean handleEvent(int keyCode);
}
