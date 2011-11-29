/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2005 Andres Navarro
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contributor(s):
 *    3GLab
 */

package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.GaugeUI;

public class Gauge extends Item
{
	static int HEIGHT = 15;
  
	int value;
	
	int maxValue;

	boolean interactive;
  
	public static final int INDEFINITE = -1;
  
	public static final int CONTINUOUS_IDLE = 0;
  
	public static final int INCREMENTAL_IDLE = 1;
  
	public static final int CONTINUOUS_RUNNING = 2;
  
	public static final int INCREMENTAL_UPDATING = 3;
  
	// these are for render of indefinite running and
	// updating gauges
	private int indefiniteFrame;
  
	private static final int IDEFINITE_FRAMES = 4;
  
	// package access for access from Display
	static int PAINT_TIMEOUT = 500;

  	public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
		super(label);
		super.setUI(DeviceFactory.getDevice().getUIFactory().createGaugeUI(this));

		this.interactive = interactive;

		setMaxValue(maxValue);
		setValue(initialValue);
	}


  	public void setValue(int value) {
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidGaugeUI")) {
			((GaugeUI) ui).setValue(value);
		} else {
			if (hasIndefiniteRange()) {
				if (value != Gauge.CONTINUOUS_IDLE
						&& value != Gauge.CONTINUOUS_RUNNING
						&& value != Gauge.INCREMENTAL_IDLE
						&& value != Gauge.INCREMENTAL_UPDATING) {
					throw new IllegalArgumentException();
				} else {
					// TODO if CONTINOUS_RUNNING
					// start thread or whatever it needs to be done
					if (value == Gauge.INCREMENTAL_UPDATING
							&& this.value == Gauge.INCREMENTAL_UPDATING) {
						updateIndefiniteFrame();
					} else {
						this.value = value;
						repaint();
					}
				}
			} else {
				if (value < 0) {
					value = 0;
				}
				if (value > maxValue) {
					value = maxValue;
				}
	
				this.value = value;
				repaint();
			}
		}
	}


  	public int getValue() {
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidGaugeUI")) {
			return ((GaugeUI) ui).getValue();
		} else {
			return value;
		}
	}


  	public void setMaxValue(int maxValue) {
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidGaugeUI")) {
			((GaugeUI) ui).setMaxValue(maxValue);
		}
		
		if (maxValue > 0) {
			this.maxValue = maxValue;
			setValue(getValue());
		} else {
			if (isInteractive()) {
				throw new IllegalArgumentException();
			} else if (maxValue != Gauge.INDEFINITE) {
				throw new IllegalArgumentException();
			} else {
				// not interactive && maxValue == INDEFINITE
				// this is also the case in the
				// first call in the constructor!
				if (this.maxValue == Gauge.INDEFINITE) {
					return;
				} else {
					this.maxValue = Gauge.INDEFINITE;
					this.value = Gauge.INCREMENTAL_IDLE;
					repaint();
				}
			}
		}
	}


  	public int getMaxValue() {
		return maxValue;
	}


  public boolean isInteractive()
  {
    return interactive;
  }


  // package access for access from Display
  boolean hasIndefiniteRange() {
	  return ((!isInteractive()) &&
			  (getMaxValue() == Gauge.INDEFINITE)); 
  }

  // package access for access from Display
  void updateIndefiniteFrame() {
	  if (hasIndefiniteRange() && 
			  ((getValue() == Gauge.CONTINUOUS_RUNNING) ||
			  (getValue() == Gauge.INCREMENTAL_UPDATING))) {
		  if (indefiniteFrame+1 < Gauge.IDEFINITE_FRAMES)
			  indefiniteFrame++;
		  else 
			  indefiniteFrame = 0;
		  repaint();
	  }
  }
  // ITEM methods

  int getHeight()
	{
		return super.getHeight() + HEIGHT;
	}

  
	boolean isFocusable()
	{
		return interactive;
	}

  
  void keyPressed(int keyCode)
  {
    if (Display.getGameAction(keyCode) == Canvas.LEFT && value > 0) {
      value--;
      repaint();
    } else if (Display.getGameAction(keyCode) == Canvas.RIGHT && value < maxValue) {
      value++;
      repaint();
    }
  }

  
  int paint(Graphics g)
  {    
    super.paintContent(g);
    
	g.translate(0, super.getHeight());

	if (hasFocus()) {
		g.drawRect(2, 2, owner.getWidth() - 5, HEIGHT - 5); 
	}
    if (hasIndefiniteRange()) {
    	if (getValue() == Gauge.CONTINUOUS_IDLE ||
    			getValue() == Gauge.INCREMENTAL_IDLE) {
        	int width = owner.getWidth() - 9;
    		g.drawRect(4, 4, width, HEIGHT - 9);
    	} else {
    		int width = ((owner.getWidth() - 8) << 1) / Gauge.IDEFINITE_FRAMES;
    		int offset = (width >>> 1) * indefiniteFrame;
    		int width2 = 0;
    		if (offset + width > (owner.getWidth() - 8)) {
    			width2 = offset + width - (owner.getWidth() - 8);
    			width -= width2; 
        				
    		}
    		g.fillRect(4 + offset, 4, width, HEIGHT - 8);
    		if (width2 != 0) {
        		g.fillRect(4, 4, width2, HEIGHT - 8);
    		}
    	}
    } else {
	    int width = (owner.getWidth() - 8) * value / maxValue; 
	    g.fillRect(4, 4, width, HEIGHT - 8);
    }
    
	g.translate(0, -super.getHeight());
    return getHeight();
  }


	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		if (gameKeyCode == Canvas.UP) {
			if (top > 0) {
				return -top;
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == Canvas.DOWN) {
			if (getHeight() > bottom) {
				return getHeight() - bottom;
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}
	
	// override some methods to disallow modification
	// when inside an Alert
	public void setPreferredSize(int w, int h) {
		Screen owner = this.getOwner();
		
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.setPreferredSize(w, h);
	}
  
	public void setLayout(int layout) {
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.setLayout(layout);
	}
	
	public void setLabel(String label) {
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.setLabel(label);
	}
	
	public void addCommand(Command cmd) {
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.addCommand(cmd);
	}
	
	public void setDefaultCommand(Command cmd) {
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.setDefaultCommand(cmd);
	}
	
	public void setItemCommandListener(ItemCommandListener l) {
		if (owner != null && owner instanceof Alert)
			return;
		else
			super.setItemCommandListener(l);
	}
}