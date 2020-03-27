/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.Hashtable;

public class XYLayout implements LayoutManager2 {
	private static final long serialVersionUID = 200L;

	int width; // <= 0 means use the container's preferred size

	int height; // <= 0 means use the container's preferred size

	public XYLayout() {
	}

	public XYLayout(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String toString() {
		return "XYLayout" + "[width=" + width + ",height=" + height + "]";
	}

	// LayoutManager interface

	public void addLayoutComponent(String name, Component component) {
	}

	public void removeLayoutComponent(Component component) {
		info.remove(component);
	}

	public Dimension preferredLayoutSize(Container target) {
		return getLayoutSize(target, true);
	}

	public Dimension minimumLayoutSize(Container target) {
		return getLayoutSize(target, false);
	}

	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int count = target.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component component = target.getComponent(i);
			if (component.isVisible()) {
				Rectangle r = getComponentBounds(component, true);
				component.setBounds(insets.left + r.x, insets.top + r.y, r.width, r.height);
			}
		}
	}

	// LayoutManager2 interface

	public void addLayoutComponent(Component component, Object constraints) {
		if (constraints instanceof XYConstraints)
			info.put(component, constraints);
	}

	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	public void invalidateLayout(Container target) {
	}

	// internal

	Hashtable info = new Hashtable(); // leave this as non-transient

	static final XYConstraints defaultConstraints = new XYConstraints();

	Rectangle getComponentBounds(Component component, boolean doPreferred) {
		XYConstraints constraints = (XYConstraints) info.get(component);
		if (constraints == null)
			constraints = defaultConstraints;
		Rectangle r = new Rectangle(constraints.x, constraints.y, constraints.width, constraints.height);
		if (r.width <= 0 || r.height <= 0) {
			Dimension d = doPreferred ? component.getPreferredSize() : component.getMinimumSize();
			if (r.width <= 0)
				r.width = d.width;
			if (r.height <= 0)
				r.height = d.height;
		}
		return r;
	}

	Dimension getLayoutSize(Container target, boolean doPreferred) {
		Dimension dim = new Dimension(0, 0);

		if (width <= 0 || height <= 0) {
			int count = target.getComponentCount();
			for (int i = 0; i < count; i++) {
				Component component = target.getComponent(i);
				if (component.isVisible()) {
					Rectangle r = getComponentBounds(component, doPreferred);
					dim.width = Math.max(dim.width, r.x + r.width);
					dim.height = Math.max(dim.height, r.y + r.height);
				}
			}
		}
		if (width > 0)
			dim.width = width;
		if (height > 0)
			dim.height = height;
		Insets insets = target.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}
}