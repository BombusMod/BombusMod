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

import org.microemu.device.EmulatorContext;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.iphone.MicroEmulatorDelegate;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.*;

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import java.lang.reflect.Field;

public class IPhoneTextBoxUI extends AbstractUI<TextBox> implements TextBoxUI {

	private final class TextBoxField extends TextField {
		public TextBoxField(TextField textField) {
			super(textField.getLabel(), textField.getString(), textField.getMaxSize(), textField.getConstraints());
		}
		
		@Override
		public void setString(String text) {
			super.setString(text);
			if(textView!=null)
				textView.setText(text);
		}
		
		@Override
		public int getCaretPosition() {
			if(textView!=null)
				return (int)textView.getSelectedRange().getLocation();
			else
				return 0;
		}

		public void setStringSilent(String text) {
			super.setString(text);
		}
	}

	private TextBoxField textField;
	private UIView view;
	private UITextView textView;
	private UINavigationBar navigtionBar;
	private UIView parentView;

	public IPhoneTextBoxUI(EmulatorContext emulatorContext, UIView parentView, TextBox textBox) {
		super(emulatorContext, parentView, textBox);
		this.parentView = parentView;
		try {
			Field tf = TextBox.class.getDeclaredField("tf");
			tf.setAccessible(true);
			textField = new TextBoxField((TextField) tf.get(textBox));
			tf.set(textBox, textField);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	public String getString() {
		return textField.getString();
	}

	public void setString(String text) {
		textField.setString(text);
	}

	@Override
	public void insert(String text, int position) {

	}

	@Override
	public void delete(int offset, int length) {

	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		System.out.println("IPhoneTextBoxUI.showNotify()");
		if (view == null) {
			view = new UIView(parentView.getFrame());

			navigtionBar = new UINavigationBar(new CGRect(0, 0,
					parentView.getFrame().getWidth(), NAVIGATION_HEIGHT));
			UINavigationItem title = new UINavigationItem(displayable.getTitle());
			title.setTitle("Done");
			navigtionBar.pushNavigationItem(title, true);
			view.addSubview(navigtionBar);
			navigtionBar.setDelegate(new UINavigationBarDelegateAdapter() {
				@Override
				public boolean shouldPopItem(UINavigationBar navigationBar, UINavigationItem item) {
					textView.resignFirstResponder();
					return true;
				}
			});

			textView = new UITextView(
					new CGRect(0, NAVIGATION_HEIGHT, parentView.getFrame().getWidth(),
							parentView.getFrame().getHeight()
									- NAVIGATION_HEIGHT - TOOLBAR_HEIGHT)) {
				@Override
				public boolean becomeFirstResponder() {
					// Open Keybords and add a Done-Button
					UINavigationItem keyboardTitle = new UINavigationItem(displayable.getTitle());
					navigtionBar.pushNavigationItem(keyboardTitle, true);
					return super.becomeFirstResponder();
				}
			};
			textView.setText(textField.getString());
			textView.setDelegate(new UITextViewDelegateAdapter() {
				@Override
				public void didChange(UITextView textView) {
					textField.setStringSilent(textView.getText());
				}
			});
			view.addSubview(textView);

			toolbar = new UIToolbar(new CGRect(0,
					parentView.getFrame().getHeight() - TOOLBAR_HEIGHT,
					parentView.getFrame().getWidth(), TOOLBAR_HEIGHT));
			view.addSubview(toolbar);
			updateToolbar();
		}

		view.retain();
		parentView.addSubview(view);
	}

}
