/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidInputMethod.java 2439 2010-11-03 08:27:40Z barteo@gmail.com $
 */

package org.microemu.android.device;

import javax.microedition.lcdui.Canvas;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;

import android.view.KeyEvent;

public class AndroidInputMethod extends InputMethod {

	private int repeatModeKeyCode = Integer.MAX_VALUE;

	public void buttonPressed(KeyEvent keyEvent) {
	    buttonPressed(getKeyCode(keyEvent));		
	}
	
	public void buttonPressed(int keyCode) {
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			if (repeatModeKeyCode == keyCode) {
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}
				DisplayAccess da = ma.getDisplayAccess();
				if (da == null) {
					return;
				}
				da.keyRepeated(keyCode);
				return;
			} else {
				repeatModeKeyCode = keyCode;
			}
		}

		fireInputMethodListener(keyCode);
	}

	public void buttonReleased(KeyEvent keyEvent) {
	    buttonReleased(getKeyCode(keyEvent));
	}
	
	public void buttonReleased(int keyCode) {
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			repeatModeKeyCode = Integer.MAX_VALUE;
		}
		
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}

		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}

		da.keyReleased(keyCode);
	}
	
	public void pointerPressed(int x, int y) {		
		if (DeviceFactory.getDevice().hasPointerEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerPressed(x, y);
		}
	}

	public void pointerReleased(int x, int y) {
		if (DeviceFactory.getDevice().hasPointerEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerReleased(x, y);
		}
	}

	public void pointerDragged(int x, int y) {
		if (DeviceFactory.getDevice().hasPointerMotionEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerDragged(x, y);
		}
	}

	protected boolean fireInputMethodListener(int keyCode) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}

		if (inputMethodListener == null) {
			da.keyPressed(keyCode);
			return true;
		}
// TODO
/*		
		ButtonName functionalName = button.getFunctionalName();

		if (functionalName == ButtonName.UP || functionalName == ButtonName.DOWN) {
			da.keyPressed(getKeyCode(keyEvent));
			return true;
		}

		int caret = inputMethodListener.getCaretPosition();

		if (button.isModeChange()) {
			switch (inputMethodListener.getConstraints() & TextField.CONSTRAINT_MASK) {
			case TextField.ANY:
			case TextField.EMAILADDR:
			case TextField.URL:
				if (getInputMode() == InputMethod.INPUT_123) {
					setInputMode(InputMethod.INPUT_ABC_UPPER);
				} else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
					setInputMode(InputMethod.INPUT_ABC_LOWER);
				} else if (getInputMode() == InputMethod.INPUT_ABC_LOWER) {
					setInputMode(InputMethod.INPUT_123);
				}
				synchronized (this) {
					if (lastButton != null) {
						caret++;
						lastButton = null;
						lastButtonCharIndex = -1;
					}
				}
				InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret,
						inputMethodListener.getText());
				inputMethodListener.caretPositionChanged(event);
				break;
			}
			return true;
		}

		if (functionalName == ButtonName.LEFT || functionalName == ButtonName.RIGHT) {
			synchronized (this) {
				if ((functionalName == ButtonName.LEFT) && caret > 0) {
					caret--;
				} else if ((functionalName == ButtonName.RIGHT) && caret < inputMethodListener.getText().length()) {
					caret++;
				}
				lastButton = null;
				lastButtonCharIndex = -1;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret,
					inputMethodListener.getText());
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (functionalName == ButtonName.BACK_SPACE) {
			String tmp = "";
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret > 0) {
					caret--;
					if (caret > 0) {
						tmp += inputMethodListener.getText().substring(0, caret);
					}
					if (caret < inputMethodListener.getText().length() - 1) {
						tmp += inputMethodListener.getText().substring(caret + 1);
					}
				}
			}
			if (!validate(tmp, inputMethodListener.getConstraints())) {
				return true;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, tmp);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (functionalName == ButtonName.DELETE) {
			String tmp = inputMethodListener.getText();
			synchronized (this) {
				if (lastButton != null) {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret != inputMethodListener.getText().length()) {
					tmp = inputMethodListener.getText().substring(0, caret)
							+ inputMethodListener.getText().substring(caret + 1);
				}
			}
			if (!validate(tmp, inputMethodListener.getConstraints())) {
				return true;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, tmp);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (inputMethodListener.getText().length() < maxSize) {
			StringBuffer editText = new StringBuffer(inputMethodListener.getText());
			synchronized (this) {
				lastButtonCharIndex++;
				char[] buttonChars = filterConstraints(filterInputMode(button.getChars(getInputMode())));
				if (keyChar != '\0') {
					// Pass through letters and characters typed on keyboard but
					// not numbers that are buttons keys (presumably).
					editText.append(keyChar);
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				} else if (buttonChars.length > 0) {
					if (lastButtonCharIndex == buttonChars.length) {
						if (buttonChars.length == 1) {
							if (lastButton != null) {
								caret++;
							}
							lastButton = null;
						} else {
							lastButtonCharIndex = 0;
						}
					}
					if (lastButton != button) {
						if (lastButton != null) {
							caret++;
						}
						if (editText.length() < caret) {
							editText.append(buttonChars[0]);
						} else {
							editText.insert(caret, buttonChars[0]);
						}
						lastButton = button;
						lastButtonCharIndex = 0;
					} else {
						editText.setCharAt(caret, buttonChars[lastButtonCharIndex]);
						lastButton = button;
					}
				} else {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				resetKey = false;
				notify();
			}
			if (!validate(editText.toString(), inputMethodListener.getConstraints())) {
				return false;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, editText
					.toString());
			inputMethodListener.inputMethodTextChanged(event);
		}*/
		return false;
	}

	private int getKeyCode(KeyEvent keyEvent) {
		// TODO implement as lookup table
		int deviceKeyCode = keyEvent.getKeyCode();
		
		int resultKeyCode;
		switch (deviceKeyCode) {
		case KeyEvent.KEYCODE_CALL :
			resultKeyCode = -10;
			break;
		case KeyEvent.KEYCODE_BACK :
			resultKeyCode = -8;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER :
			resultKeyCode = -5;
			break;
		case KeyEvent.KEYCODE_DPAD_UP :
			resultKeyCode = -1;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN :
			resultKeyCode = -2;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT :
			resultKeyCode = -3;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT :
			resultKeyCode = -4;
			break;
		case KeyEvent.KEYCODE_DEL :
			resultKeyCode = 127;
			break;
		default: 
            resultKeyCode = keyEvent.getUnicodeChar();
            if (resultKeyCode == 0) {
                 resultKeyCode = -deviceKeyCode;
            }
		}

		return resultKeyCode;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGameAction(int keyCode) {
		// TODO implement as lookup table
		int gameAction;
		switch (keyCode) {
		case -5 :
			gameAction = Canvas.FIRE;
			break;
		case -1 :
			gameAction = Canvas.UP;
			break;
		case -2 :
			gameAction = Canvas.DOWN;
			break;
		case -3 :
			gameAction = Canvas.LEFT;
			break;
		case -4 :
			gameAction = Canvas.RIGHT;
			break;
		default:
			gameAction = 0; 
		}

		return gameAction;
	}

	@Override
	public int getKeyCode(int gameAction) {
		int keyCode;
		switch (gameAction) {
		case Canvas.FIRE:
			keyCode = -5;
			break;
		case Canvas.UP:
			keyCode = -1;
			break;
		case Canvas.DOWN:
			keyCode = -2;
			break;
		case Canvas.LEFT:
			keyCode = -3;
			break;
		case Canvas.RIGHT:
			keyCode = -4;
			break;
		case Canvas.GAME_A:
			keyCode = Canvas.GAME_A;
			break;
		case Canvas.GAME_B:
			keyCode = Canvas.GAME_B;
			break;
		case Canvas.GAME_C:
			keyCode = Canvas.GAME_C;
			break;
		case Canvas.GAME_D:
			keyCode = Canvas.GAME_D;
			break;
		default:
			throw new IllegalArgumentException();
		}

		return keyCode;
	}

	@Override
	public String getKeyName(int keyCode) throws IllegalArgumentException {
		// TODO implement as lookup table
		String keyName;
		switch (keyCode) {
		case Canvas.FIRE:
			keyName = "FIRE";
			break;
		case Canvas.UP:
			keyName = "UP";
			break;
		case Canvas.DOWN:
			keyName = "DOWN";
			break;
		case Canvas.LEFT:
			keyName = "LEFT";
			break;
		case Canvas.RIGHT:
			keyName = "RIGHT";
			break;
		case Canvas.GAME_A:
			keyName = "GAME_A";
			break;
		case Canvas.GAME_B:
			keyName = "GAME_B";
			break;
		case Canvas.GAME_C:
			keyName = "GAME_C";
			break;
		case Canvas.GAME_D:
			keyName = "GAME_D";
			break;
		default:
			keyName = Character.toString((char) keyCode);
		}

		return keyName;
	}

}
