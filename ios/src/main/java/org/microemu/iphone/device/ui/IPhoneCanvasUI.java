/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */
package org.microemu.iphone.device.ui;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.EmulatorContext;
import org.microemu.device.ui.CanvasUI;
import org.microemu.iphone.device.IPhoneMutableImage;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.lcdui.Canvas;

public class IPhoneCanvasUI extends AbstractUI<Canvas> implements CanvasUI {

	private static final Logger logger = LoggerFactory.getLogger(IPhoneCanvasUI.class);
	enum Touch {
		BEGIN, END, DRAG
	}

	private final class CanvasView extends UIView {
		private Canvas canvas;
		private UITextField keyboardHandler;
		private String oldText = "";
		private boolean keybordVisible=false;
		private IPhoneMutableImage offscreen;

		public CanvasView(Canvas canvas, CGRect frame) {
			super(frame);
			this.canvas = canvas;
			setMultipleTouchEnabled(true);
			setUserInteractionEnabled(true);
			setClearsContextBeforeDrawing(false);
			setBackgroundColor(UIColor.clear());
			
			offscreen=new IPhoneMutableImage(canvas.getWidth(), canvas.getHeight());
			
			System.out.println(clearsContextBeforeDrawing());
			keyboardHandler = new UITextField(new CGRect(1000, 1000, 100, 100));
//			System.out.println("CanvasView.initWithFrame$(1)");
//			Runtime.msgSend(keyboardHandler, UITextInputTraits.class, "setKeyboardType:", 5);
//			System.out.println("CanvasView.initWithFrame$(2)");
			
			NSNotificationCenter.getDefaultCenter()
					.addObserver(this, Selector.register("keyboardHandlerChanged"),
							"UITextFieldTextDidChangeNotification", keyboardHandler);
			addSubview(keyboardHandler);
		}

		@Method(selector = "keyboardHandlerChanged")
		public void keyboardHandlerChanged(NSNotification object) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			String text=keyboardHandler.getText();
			System.out.println(oldText+" -> "+text);
			int key;
			if(text.length()>oldText.length()){
				key=text.charAt(text.length()-1);
			} else {
				//removed one, so backspace
				key= 20; // Display.KeyEvent.VK_BACK_SPACE;
			}
			ma.getDisplayAccess().keyPressed(key);
			ma.getDisplayAccess().keyReleased(key);
			oldText=text;
		}
		
		@Override
		public void draw(CGRect arg0) {
			System.out.println("drawRect: " + canvas + " " + this);
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			CGContext context = UIGraphics.getCurrentContext();
			context.translateCTM(0, canvas.getHeight());
			context.scaleCTM(1.0f, -1.0f);
//			CoreGraphics.CGContextSaveGState(context);
//			Graphics g = new IPhoneDisplayGraphics(context, canvas.getWidth(), canvas.getHeight(), false);
			ma.getDisplayAccess().paint(offscreen.getGraphics());
		
			CGRect rect = new CGRect(0, 0, offscreen.getWidth(), offscreen.getHeight());
	        CGImage bitmap = offscreen.getBitmap();
			context.drawImage(rect,  bitmap);
			bitmap.release();
//			g.drawString("XXX", 100, 100, 0);
//			CoreGraphics.CGContextRestoreGState(context);
		}

		@Override
		public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			// System.out.println("Event: "+event);

			logger.debug("Touches: "+ touches.size());

			if (touches.size() > 1) {
				handleMultiTouch(ma, touches, Touch.BEGIN);
			} else {
				UITouch touch = touches.any();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.getLocationInView(this);

				ma.getDisplayAccess().pointerPressed((int) point.getX(), (int) point.getY());
			}
		}

		@Override
		public void touchesEnded(NSSet<UITouch> touches, UIEvent event) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			// System.out.println("Event: "+event);
			// System.out.println("Touches: "+event.allTouches());
			if (touches.size() > 1) {
				handleMultiTouch(ma, touches, Touch.END);
			} else {
				UITouch touch = touches.any();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.getLocationInView(this);

				ma.getDisplayAccess().pointerReleased((int) point.getX(), (int) point.getY());
			}
		}

		@Override
		public void touchesMoved(NSSet<UITouch> touches, UIEvent event) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			// System.out.println("Event: "+event);
			// System.out.println("Touches: "+event.allTouches());

			if (touches.size() > 1) {
				handleMultiTouch(ma, touches, Touch.DRAG);
			} else {
				UITouch touch = (UITouch) touches.any();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.getLocationInView(this);
				
				ma.getDisplayAccess().pointerDragged((int) point.getX(), (int) point.getY());
			}
		}

		@Override
		public void touchesCancelled(NSSet<UITouch> nsSet, UIEvent uiEvent) {
			logger.debug("Touches cancelled");
		}

		@Override
		public void touchesEstimatedPropertiesUpdated(NSSet<UITouch> nsSet) {
			logger.debug("Touches estimated properties updated");
		}

		private CGPoint moveFrom;

		private void handleMultiTouch(MIDletAccess ma, NSSet<UITouch> touches, Touch type) {
			System.out.println("CanvasView.handleMultiTouch(" + type + ")");
			if (touches.size() == 2) {
				UITouch touch1 = touches.getValues().get(0);
				UITouch touch2 = touches.getValues().get(1);
				CGPoint touchPoint = new CGPoint((touch1.getLocationInView(view).getX() + touch2.getLocationInView(view).getX()) / 2,
						(touch1.getLocationInView(view).getY() + touch2.getLocationInView(view).getY()) / 2);
				if (type == Touch.BEGIN) {
					moveFrom = touchPoint;
				} else if (type == Touch.END && moveFrom != null) {
					Integer key = null;
					CGPoint from = moveFrom;
					moveFrom = null;
					CGPoint to = touchPoint;
					System.out.println(from.getX() + "," + from.getY() + " -> " + to.getX() + "," + to.getY());
					double diffX = to.getX() - from.getX();
					double diffY = to.getY() - from.getY();
					if (Math.abs(diffX) > Math.abs(diffY) + 10) {
						if (diffX > 0)
							key = Canvas.RIGHT;
						else if (diffX < 0)
							key = Canvas.LEFT;
					} else if (Math.abs(diffY) > Math.abs(diffX) + 10) {
						if (diffY > 0)
							key = Canvas.DOWN;
						else if (diffY < 0)
							key = Canvas.UP;
					} else {
						key = Canvas.FIRE;
					}

					if (key != null) {
						System.out.println("Pressing: " + key);
						ma.getDisplayAccess().keyPressed(key);
						ma.getDisplayAccess().keyReleased(key);
					}
				}
			} else if (touches.size() == 3 && type == Touch.END) {
				if (keybordVisible) {
					System.out.println("Hide Keyboard");
					resignFirstResponder();
				} else {
					System.out.println("Show Keyboard");
					becomeFirstResponder();
				}
				keybordVisible=!keybordVisible;
			}
		}
	}

	private UIView canvasView;

	private UIView view;

	private UIView parentView;

	public IPhoneCanvasUI(EmulatorContext emulatorContext, UIView parentView, final Canvas canvas) {
		super(emulatorContext, parentView, canvas);
		this.parentView = parentView;
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		if (view == null) {
			canvasView = new CanvasView(displayable, parentView.getFrame());

			view = new UIView(parentView.getFrame());
			// tableView = new UITableView().initWithFrame$style$(
			// new CGRect(0, 0, microEmulator.getWindow().bounds().size.width,
			// microEmulator.getWindow().bounds().size.height - 40), 0);
			view.addSubview(canvasView);
			toolbar = new UIToolbar(new CGRect(0,
					parentView.getFrame().getHeight() - TOOLBAR_HEIGHT,
					parentView.getFrame().getWidth(), TOOLBAR_HEIGHT));
			view.addSubview(toolbar);
			updateToolbar();
		}
		parentView.addSubview(view);
	}

	public UIView getCanvasView() {
		return canvasView;
	}

}
