/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidImageStringItemUI.java 1931 2009-02-05 21:00:52Z barteo $
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;

import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidImmutableImage;
import org.microemu.android.device.AndroidMutableImage;
import org.microemu.device.ui.ImageStringItemUI;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;

public class AndroidImageStringItemUI extends LinearLayout implements ImageStringItemUI {

	private MicroEmulatorActivity activity;
	
	private TextView labelView;
	
	private ImageView imageView;

	private TextView textView;
	
	private Command defaultCommand;
	
	public AndroidImageStringItemUI(final MicroEmulatorActivity activity, final Item item) {
		super(activity);
		
		this.activity = activity;
		
		setOrientation(LinearLayout.VERTICAL);
		setFocusable(false);
		setFocusableInTouchMode(false);

		labelView = new TextView(activity);
		labelView.setFocusable(false);
		labelView.setFocusableInTouchMode(false);
		labelView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		labelView.setTextAppearance(labelView.getContext(),
				android.R.style.TextAppearance_Large);
		labelView.setVisibility(GONE);
		addView(labelView);

		textView = new TextView(activity);
		if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() == Item.BUTTON) {
			textView.setClickable(true);
			textView.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (defaultCommand != null) {
						MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(defaultCommand, item);
					}
				}

			});
		}
		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		addView(textView);

		if (item instanceof ImageItem && ((ImageItem) item).getAppearanceMode() == Item.BUTTON) {
			imageView = new ImageButton(activity);
			imageView.setClickable(true);
			imageView.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (defaultCommand != null) {
						MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(defaultCommand, item);
					}
				}

			});
		} else {
			imageView = new ImageView(activity);
		}
		imageView.setVisibility(GONE);
		addView(imageView);

		setLabel(item.getLabel());
	}
	
	public void setDefaultCommand(Command cmd) {
		this.defaultCommand = cmd;
	}

	public void setLabel(final String label) {
		activity.post(new Runnable() {
			public void run() {
				if (label == null) {
					labelView.setVisibility(GONE);
				} else {
					labelView.setVisibility(VISIBLE);
					labelView.setText(label);
				}
			}
		});
	}

	public void setImage(final Image image) {
		activity.post(new Runnable() {
			public void run() {
				if (image == null) {
					imageView.setVisibility(GONE);
					imageView.setImageBitmap(null);
				} else {
					imageView.setVisibility(VISIBLE);
					if (image.isMutable()) {
						imageView.setImageBitmap(((AndroidMutableImage) image).getBitmap());
					} else {
						imageView.setImageBitmap(((AndroidImmutableImage) image).getBitmap());
					}
				}
			}
		});
	}

	public void setText(final String text) {
		activity.post(new Runnable() {
			public void run() {
				textView.setText(text);
			}
		});
	}

}
