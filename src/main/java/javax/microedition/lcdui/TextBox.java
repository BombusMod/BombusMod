/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 */
package javax.microedition.lcdui;

import org.microemu.android.device.ui.AndroidTextBoxUI;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;
import org.microemu.device.ui.TextBoxUI;

//TODO implement pointer events
public class TextBox extends Screen {

    TextField tf;
    InputMethodListener inputMethodListener = new InputMethodListener() {

        public void caretPositionChanged(InputMethodEvent event) {
            setCaretPosition(event.getCaret());
            tf.setCaretVisible(true);
            repaint();
        }

        public void inputMethodTextChanged(InputMethodEvent event) {
            tf.setCaretVisible(false);
            tf.setString(event.getText(), event.getCaret());
            repaint();
        }

        public int getCaretPosition() {
            return TextBox.this.getCaretPosition();
        }

        public String getText() {
            return TextBox.this.getString();
        }

        public int getConstraints() {
            return TextBox.this.getConstraints();
        }
    };

    public TextBox(String title, String text, int maxSize, int constraints) {
        super(title);

        tf = new TextField(null, text, maxSize, constraints);

        super.setUI(DeviceFactory.getDevice().getUIFactory().createTextBoxUI(this));

        setString(text);
    }

    public void delete(int offset, int length) {
        ((AndroidTextBoxUI) ui).delete(offset, length);
    }

    public int getCaretPosition() {
        return ((AndroidTextBoxUI) ui).getCaretPosition();
    }

    public int getChars(char[] data) {
        return tf.getChars(data);
    }

    public int getConstraints() {
        return tf.getConstraints();
    }

    public int getMaxSize() {
        return tf.getMaxSize();
    }

    public String getString() {
            if (ui == null) return ""; 
            return ((TextBoxUI) ui).getString();
       
    }

    public void insert(char[] data, int offset, int length, int position) {
        tf.insert(data, offset, length, position);
    }

    public void insert(String src, int position) {
            ((AndroidTextBoxUI) ui).insert(src, position);        
    }

    public void setChars(char[] data, int offset, int length) {
        tf.setChars(data, offset, length);
    }

    public void setConstraints(int constraints) {
        tf.setConstraints(constraints);
    }

    public void setInitialInputMode(String characterSubset) {
        // TODO implement
    }

    public int setMaxSize(int maxSize) {
        return tf.setMaxSize(maxSize);
    }

    public void setString(String text) {
        ((AndroidTextBoxUI) ui).setString(text);        
    }

    @Override
    public void setTicker(Ticker ticker) {
        // TODO implement
    }

    @Override
    public void setTitle(String s) {
        super.setTitle(s);
    }

    public int size() {
        return ((AndroidTextBoxUI) ui).getString().length();        
    }

    @Override
    void hideNotify() {
        DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(inputMethodListener);
        super.hideNotify();
    }

    int paintContent(Graphics g) {
        return 0;
    }

    void setCaretPosition(int position) {
        tf.setCaretPosition(position);

        StringComponent tmp = tf.stringComponent;
        if (tmp.getCharPositionY(position) < viewPortY) {
            viewPortY = tmp.getCharPositionY(position);
        } else if (tmp.getCharPositionY(position) + tmp.getCharHeight() > viewPortY + viewPortHeight - 6) {
            viewPortY = tmp.getCharPositionY(position) + tmp.getCharHeight() - (viewPortHeight - 6);
        }
    }

    @Override
    void showNotify() {
        super.showNotify();
        InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
        inputMethod.setInputMethodListener(inputMethodListener);
        inputMethod.setMaxSize(getMaxSize());
        setCaretPosition(getString().length());
        tf.setCaretVisible(true);
    }

    int traverse(int gameKeyCode, int top, int bottom) {
        int traverse = tf.traverse(gameKeyCode, top, bottom, true);
        if (traverse == Item.OUTOFITEM) {
            return 0;
        } else {
            return traverse;
        }
    }
}
