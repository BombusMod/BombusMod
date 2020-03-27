/**
 *  MicroEmulator
 *  Copyright (C) 2002-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;

public abstract class DeviceImpl implements Device {

	private String name;

	private static EmulatorContext context;

	private Image normalImage;

	private Image overImage;

	private Image pressedImage;

	private Vector buttons;

	private Vector softButtons;

	private boolean hasPointerEvents = true;

	private boolean hasPointerMotionEvents = true;

	// TODO not implemented yet
	private boolean hasRepeatEvents = true;

	private Map systemProperties;

	private int skinVersion;

	public static final String DEFAULT_LOCATION = "org/microemu/device/default/device.xml";
	
	public static final String RESIZABLE_LOCATION = "org/microemu/device/resizable/device.xml";

	/**
	 * @deprecated
	 */
	private String descriptorLocation;

	private static Map specialInheritanceAttributeSet;

	public DeviceImpl(EmulatorContext context) {
		this.context = context;
		// Permits null values.
		systemProperties = new HashMap();
		buttons = new Vector();
		softButtons = new Vector();
	}
	/**
	 * @deprecated
	 */
	public String getDescriptorLocation() {
		return descriptorLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#destroy()
	 */
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getName()
	 */
	public String getName() {
		return name;
	}

	public static EmulatorContext getEmulatorContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getInputMethod()
	 */
	public InputMethod getInputMethod() {
		return context.getDeviceInputMethod();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getFontManager()
	 */
	public FontManager getFontManager() {
		return context.getDeviceFontManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getDeviceDisplay()
	 */
	public DeviceDisplay getDeviceDisplay() {
		return context.getDeviceDisplay();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getNormalImage()
	 */
	public Image getNormalImage() {
		return normalImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getOverImage()
	 */
	public Image getOverImage() {
		return overImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getPressedImage()
	 */
	public Image getPressedImage() {
		return pressedImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getSoftButtons()
	 */
	public Vector getSoftButtons() {
		return softButtons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getButtons()
	 */
	public Vector getButtons() {
		return buttons;
	}


	private static Font getFont(String face, String style, String size) {
		int meFace = 0;
		if (face.equalsIgnoreCase("system")) {
			meFace |= Font.FACE_SYSTEM;
		} else if (face.equalsIgnoreCase("monospace")) {
			meFace |= Font.FACE_MONOSPACE;
		} else if (face.equalsIgnoreCase("proportional")) {
			meFace |= Font.FACE_PROPORTIONAL;
		}

		int meStyle = 0;
		String testStyle = style.toLowerCase();
		if (testStyle.indexOf("plain") != -1) {
			meStyle |= Font.STYLE_PLAIN;
		}
		if (testStyle.indexOf("bold") != -1) {
			meStyle |= Font.STYLE_BOLD;
		}
		if (testStyle.indexOf("italic") != -1) {
			meStyle |= Font.STYLE_ITALIC;
		}
		if (testStyle.indexOf("underlined") != -1) {
			meStyle |= Font.STYLE_UNDERLINED;
		}

		int meSize = 0;
		if (size.equalsIgnoreCase("small")) {
			meSize |= Font.SIZE_SMALL;
		} else if (size.equalsIgnoreCase("medium")) {
			meSize |= Font.SIZE_MEDIUM;
		} else if (size.equalsIgnoreCase("large")) {
			meSize |= Font.SIZE_LARGE;
		}

		return Font.getFont(meFace, meStyle, meSize);
	}

	private boolean parseBoolean(String value) {
		if (value.toLowerCase().equals(new String("true").toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#hasPointerEvents()
	 */
	public boolean hasPointerEvents() {
		return hasPointerEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#hasPointerMotionEvents()
	 */
	public boolean hasPointerMotionEvents() {
		return hasPointerMotionEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#hasRepeatEvents()
	 */
	public boolean hasRepeatEvents() {
		return hasRepeatEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#hasRepeatEvents()
	 */
	public boolean vibrate(int duration) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.device.DeviceA#getSystemProperties()
	 */
	public Map getSystemProperties() {
		return this.systemProperties;
	}

	private static void inheritanceConstInit() {
		if (specialInheritanceAttributeSet == null) {
			specialInheritanceAttributeSet = new Hashtable();
			specialInheritanceAttributeSet.put("//FONTS/FONT", new String[] { "face", "style", "size" });
		}
	}

	private static String besourceBase(String descriptorLocation) {
		return descriptorLocation.substring(0, descriptorLocation.lastIndexOf("/"));
	}

	private static String expandResourcePath(String base, String src) throws IOException {
		String expandedSource;
		if (src.startsWith("/")) {
			expandedSource = src;
		} else {
			expandedSource = base + "/" + src;
		}
		if (expandedSource.startsWith("/")) {
			expandedSource = expandedSource.substring(1);
		}
		return expandedSource;
	}

	private URL getResourceUrl(ClassLoader classLoader, String base, String src) throws IOException {
		String expandedSource = expandResourcePath(base, src);

		URL result = classLoader.getResource(expandedSource);

		if (result == null) {
			throw new IOException("Cannot find resource: " + expandedSource);
		}

		return result;
	}

	private Image loadImage(ClassLoader classLoader, String base, String src) throws IOException {
		URL url = getResourceUrl(classLoader, base, src);

		return ((DeviceDisplayImpl) getDeviceDisplay()).createSystemImage(url);
	}

}
