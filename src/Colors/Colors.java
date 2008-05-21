package Colors;
//#ifdef COLORS
//#endif
public class Colors {
    private static Colors instance;
    
    public static Colors getInstance(){
	if (instance==null) {
	    instance=new Colors();
//#ifdef COLORS
	    ColorScheme.loadFromStorage();
//#endif
	}
	return instance;
    }
    public static int BALLOON_INK=0x4866ad;
    public static int BALLOON_BGND=0xffffe0;
    
    public static int LIST_BGND=0xffffff;
    public static int LIST_BGND_EVEN=0xe8f0f0;
    public static int LIST_INK=0x000000;
    
    public static int MSG_SUBJ=0xca0000;
    public static int MSG_HIGHLIGHT=0x7540b0;
    
    public static int DISCO_CMD=0x000080;
    
    public static int BAR_BGND=0xad1010;
    public static int BAR_BGND_BOTTOM=0x730000;
    public static int BAR_INK=0xffffff;
    
    public static int CONTACT_DEFAULT=0x000000;
    public static int CONTACT_CHAT=0x39358b;
    public static int CONTACT_AWAY=0x008080;
    public static int CONTACT_XA=0x535353;
    public static int CONTACT_DND=0x800000;
    public static int CONTACT_J2J=0xff0000;
    public static int GROUP_INK=0x000080;
    
    public static int BLK_INK=0x000000;
    public static int BLK_BGND=0xffffff;
    
    public static int MESSAGE_IN=0x0000b0;
    public static int MESSAGE_OUT=0xb00000;
    public static int MESSAGE_PRESENCE=0x006000;
    public static int MESSAGE_AUTH=0x400040;
    public static int MESSAGE_HISTORY=0x535353;

    public static int MESSAGE_IN_S       =0x0060ff;
    public static int MESSAGE_OUT_S      =0xff4000;
    public static int MESSAGE_PRESENCE_S =0x00c040;
    
    public static int PGS_REMAINED=0xeeeeee;
    public static int PGS_COMPLETE=0xad1010;
    
    public static int HEAP_TOTAL=0xffffff;
    public static int HEAP_FREE=0x00007f;
    
    public static int CURSOR_BGND=0xbbddee;
    public static int CURSOR_OUTLINE=0x1ea5c5;
    
    public static int SCROLL_BRD=0x565656;
    public static int SCROLL_BAR=0x929292;
    public static int SCROLL_BGND=0x727272;
    
    public static int POPUP_MESSAGE_INK = 0x4866ad;
    public static int POPUP_MESSAGE_BGND = 0xffffe0;
    public static int POPUP_SYSTEM_INK = 0x009900;
    public static int POPUP_SYSTEM_BGND = 0xffffe0;
    
    public static int SECOND_LINE=0xa0a0a0;
}
