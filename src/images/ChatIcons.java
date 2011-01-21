/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package images;

import ui.ImageList;

/**
 *
 * @author Vitaly
 */
public class ChatIcons extends ImageList {

    private static ChatIcons instance;
    public static ChatIcons getInstance() {
	if (instance==null)
        instance=new ChatIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=3;

    /** Creates a new instance of RosterIcons */
    private ChatIcons() {
	super("/images/chat.png", ICONS_IN_COL, ICONS_IN_ROW);
    }

    public static final int ICON_NEW            = 0x00; //New message
    public static final int ICON_QUOTE          = 0x01; //Quote
    public static final int ICON_CLEAR          = 0x02; // Clear chat
    public static final int ICON_SELECT         = 0x03; // Select message
    public static final int ICON_COPY           = 0x04; // Copy to clipboard
    public static final int ICON_COPYPLUS       = 0x05; // Append to clipboard
    public static final int ICON_CONTACT        = 0x06; // Contact actions
    public static final int ICON_ACTIVECONTACTS = 0x07; // Active contacts

    public static final int ICON_ARCHIVE        = 0x10; // save to archive
    public static final int ICON_TEMPLATES      = 0x11; // save as template
    public static final int ICON_SENDBUF        = 0x12; // send from clipboard
    public static final int ICON_BACK           = 0x13; // go back/cancel
    public static final int ICON_ACCEPTAUTH     = 0x14; // accept auth
    public static final int ICON_DECLINEAUTH    = 0x15; // decline auth
    public static final int ICON_ACCEPTFILE     = 0x16; // accept file
    public static final int ICON_DECLINEFILE    = 0x17; // decline file

    public static final int ICON_RESUME         = 0x20; // resume message
    public static final int ICON_REPLY          = 0x21; // reply to message
    public static final int ICON_SAVECHAT       = 0x22; // save selected
    public static final int ICON_HISTORY        = 0x23; // view history
    public static final int ICON_PASTE          = 0x24; // paste from clipboard
    public static final int ICON_USESKIN        = 0x25; // use selected skin
    public static final int ICON_GOTOURL        = 0x26; // goto url

}
