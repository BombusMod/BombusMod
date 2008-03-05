/*
 * moodIcons.java
 *
 * Created on 5 Март 2008 г., 23:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import ui.ImageList;

/**
 *
 * @author ad
 */
public class MoodIcons extends ImageList{
    
    private final static int MOODS_IN_ROW=16;

    private MoodIcons() {
        super("/images/moods.png", 0, MOODS_IN_ROW);
    }
    
    private static ImageList instance;
    
    public static ImageList getInstance() {
	if (instance==null) instance=new MoodIcons();
	return instance;
    }
}