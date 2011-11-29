package org.microemu.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

public interface ListUI extends DisplayableUI {

	int append(String stringPart, Image imagePart);

	int getSelectedIndex();

	String getString(int elementNum);

	void setSelectCommand(Command command);
	
	void setSelectedIndex(int elementNum, boolean selected);

	void delete(int elementNum);
  
	void deleteAll();
	
	void insert(int elementNum, String stringPart, Image imagePart);
	
	void set(int elementNum, String stringPart, Image imagePart);
	
	int size();
	
}
