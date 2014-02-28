/*
 * SR.java
 *
 * Created on 19.03.2006, 15:06
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package locale;

import Client.Config;
import java.util.Hashtable;
import util.StringLoader;

public class SR {

    private static Hashtable lang;
    public static String MS_JID = loadString("Jid");
    public static String MS_PRIVACY_LISTS = loadString("Privacy Lists");
    public static String MS_MESSAGE_FONT = loadString("Message font");
    public static String MS_ROSTER_FONT = loadString("Roster font");
    public static String MS_PASTE_BODY = loadString("Paste Body");
    public static String MS_CONFIG_ROOM = loadString("Configure Room");
    public static String MS_PASTE_SUBJECT = loadString("Paste Subject");
    public static String MS_DISCO = loadString("Service Discovery");
    public static String MS_USER_JID = loadString("User JID");
    public static String MS_NEW_LIST = loadString("New list");
    public static String MS_DEFAULT = loadString("Default");
    public static String MS_PRIVACY_RULE = loadString("Privacy rule");
    public static String MS_SSL = loadString("Use SSL");
    public static String MS_MODIFY = loadString("Modify");
    public static String MS_UPDATE = loadString("Update");
    public static String MS_ACCOUNT_NAME = loadString("Account name");
    public static String MS_GMT_OFFSET = loadString("GMT offset");
    public static String MS_TIME_SETTINGS = loadString("Time settings (hours)");
    public static String MS_CONNECTED = loadString("Connected");
    public static String MS_CONNECT_TO_ = loadString("Connect to ");
    public static String MS_ALERT_PROFILE = loadString("Alert Profile");
    public static String MS_MOVE_UP = loadString("Move Up");
    public static String MS_OWNERS = loadString("Owners");
    public static String MS_OK = loadString("Ok");
    public static String MS_APP_MINIMIZE = loadString("Minimize");
    public static String MS_ROOM = loadString("Room");
    public static String MS_MESSAGES = loadString("Messages");
    public static String MS_REFRESH = loadString("Refresh");
    public static String MS_RESOLVE_NICKNAMES = loadString("Resolve Nicknames");
    public static String MS_PRIVACY_ACTION = loadString("Action");
    public static String MS_BAN = loadString("Ban");
    public static String MS_LEAVE_ROOM = loadString("Leave Room");
    public static String MS_PASSWORD = loadString("Password");
    public static String MS_ITEM_ACTIONS = loadString("Actions >");
    public static String MS_ACTIVATE = loadString("Activate");
    public static String MS_AFFILIATION = loadString("Affiliation");
    public static String MS_ACCOUNTS = loadString("Accounts");
    public static String MS_DELETE_LIST = loadString("Delete list");
    public static String MS_ACCOUNT_ = loadString("Account >");
    public static String MS_SELECT = loadString("Select");
    public static String MS_SUBJECT = loadString("Subject");
    //public   static String MS_GROUP_MENU = loadString( "Group menu" );
    public static String MS_APP_QUIT = loadString("Quit");
    public static String MS_EDIT_LIST = loadString("Edit list");
    public static String MS_REGISTERING = loadString("Registering");
    public static String MS_DONE = loadString("Done");
    public static String MS_ERROR_ = loadString("Error: ");
    public static String MS_BROWSE = loadString("Browse");
    public static String MS_SAVE_LIST = loadString("Save list");
    public static String MS_KEEPALIVE_PERIOD = loadString("Keep-Alive period");
    public static String MS_NEWGROUP = loadString("<New Group>");
    public static String MS_SEND = loadString("Send");
    public static String MS_PRIORITY = loadString("Priority");
    public static String MS_FAILED = loadString("Failed");
    public static String MS_SET_PRIORITY = loadString("Set Priority");
    public static String MS_DELETE_RULE = loadString("Delete rule");
    public static String MS_IGNORE_LIST = loadString("Ignore-List");
    public static String MS_ROSTER_REQUEST = loadString("Roster request");
    public static String MS_PRIVACY_TYPE = loadString("Type");
    public static String MS_NAME = loadString("Name");
    public static String MS_USERNAME = loadString("Username");
    public static String MS_FULLSCREEN = loadString("Fullscreen");
    public static String MS_ADD_BOOKMARK = loadString("Add bookmark");
    public static String MS_CONFERENCES_ONLY = loadString("Conferences only");
    public static String MS_CLIENT_INFO = loadString("Client Version");
    public static String MS_DISCARD = loadString("Discard Search");
    public static String MS_SEARCH_RESULTS = loadString("Search Results");
    public static String MS_GENERAL = loadString("General");
    public static String MS_MEMBERS = loadString("Members");
    public static String MS_ADD_CONTACT = loadString("Add Contact");
    public static String MS_SUBSCRIPTION = loadString("Subscription");
    public static String MS_STATUS_MENU = loadString("Status >");
    public static String MS_JOIN = loadString("Join");
    public static String MS_STARTUP_ACTIONS = loadString("Startup actions");
    public static String MS_SERVER = loadString("Server");
    public static String MS_ADMINS = loadString("Admins");
    public static String MS_MK_ILIST = loadString("Make Ignore-List");
    public static String MS_OPTIONS = loadString("Options");
    public static String MS_DELETE = loadString("Delete");
    public static String MS_DELETE_ASK = loadString("Delete contact?");
    public static String MS_SUBSCRIBE = loadString("Authorize");
    public static String MS_NICKNAMES = loadString("Nicknames");
    public static String MS_ADD_ARCHIVE = loadString("To archive");
    public static String MS_BACK = loadString("Back");
    public static String MS_HEAP_MONITOR = loadString("Heap monitor");
    public static String MS_MESSAGE = loadString("Message");
    public static String MS_OTHER = loadString("<Other>");
    public static String MS_HISTORY = loadString("History");
    public static String MS_APPEND = loadString("Append");
    public static String MS_ACTIVE_CONTACTS = loadString("Active Contacts");
    public static String MS_SELECT_NICKNAME = loadString("Select nickname");
    public static String MS_GROUP = loadString("Group");
    public static String MS_JOIN_CONFERENCE = loadString("Join conference");
    public static String MS_NO = loadString("No");
    public static String MS_REENTER = loadString("Re-Enter Room");
    public static String MS_NEW_MESSAGE = loadString("New Message");
    public static String MS_ADD = loadString("Add");
    public static String MS_LOGON = loadString("Logon");
    public static String MS_STANZAS = loadString("Stanzas");
    public static String MS_AT_HOST = loadString("at Host");
    public static String MS_AUTO_CONFERENCES = loadString("Join conferences");
    public static String MS_STATUS = loadString("Status");
    public static String MS_SMILES_TOGGLE = loadString("Smiles");
    public static String MS_CONTACT = loadString("Contact >");
    public final static String MS_SLASHME = "/me";
    public static String MS_OFFLINE_CONTACTS = loadString("Offline contacts");
    public static String MS_TRANSPORT = loadString("Transport");
    public static String MS_COMPOSING_EVENTS = loadString("Composing events");
    public static String MS_ADD_SMILE = loadString("Add Smile");
    public static String MS_NICKNAME = loadString("Nickname");
    public static String MS_REVOKE_VOICE = loadString("Revoke Voice");
    public static String MS_NOT_IN_LIST = loadString("Not-in-list");
    public static String MS_COMMANDS = loadString("Commands");
    public static String MS_CHSIGN = loadString("- (Sign)");
    public static String MS_BANNED = loadString("Outcasts (Ban)");
    public static String MS_SET_AFFILIATION = loadString("Set affiliation to");
    public static String MS_REGISTER_ACCOUNT = loadString("Register Account");
    public static String MS_AUTOLOGIN = loadString("Autologin");
    public static String MS_LOGOFF = loadString("Logoff");
    public static String MS_PUBLISH = loadString("Publish");
    public static String MS_SUBSCR_REMOVE = loadString("Remove subscription");
    public static String MS_SET = loadString("Set");
    public static String MS_APPLICATION = loadString("Application");
    public static String MS_BOOKMARKS = loadString("Bookmarks");
    public static String MS_TEST_SOUND = loadString("Test sound");
    public static String MS_STARTUP = loadString("Startup");
    public static String MS_EDIT_RULE = loadString("Edit rule");
    public static String MS_CANCEL = loadString("Cancel");
    public static String MS_CLOSE = loadString("Close");
    public static String MS_ARCHIVE = loadString("Archive");
    public static String MS_CONFERENCE = loadString("Conference");
    public static String MS_SOUND = loadString("Sound");
    public static String MS_LOGIN_FAILED = loadString("Login failed");
    public static String MS_DISCOVER = loadString("Browse"); //"Discover"
    public static String MS_NEW_JID = loadString("New Jid");
    public static String MS_PLAIN_PWD = loadString("Plain-text password");
    public static String MS_PASTE_NICKNAME = loadString("Paste Nickname");
    public static String MS_KICK = loadString("Kick");
    public static String MS_CLEAR_LIST = loadString("Remove readed");
    public static String MS_GRANT_VOICE = loadString("Grant Voice");
    public static String MS_MOVE_DOWN = loadString("Move Down");
    public static String MS_QUOTE = loadString("Quote");
    public static String MS_ROSTER_ELEMENTS = loadString("Roster elements");
    public static String MS_ENABLE_POPUP = loadString("Popup from background");
    public static String MS_SMILES = loadString("Smiles");
    public static String MS_ABOUT = loadString("About");
    public static String MS_RESOURCE = loadString("Resource");
    public static String MS_DISCONNECTED = loadString("Disconnected");
    public static String MS_EDIT = loadString("Edit");
    public static String MS_HOST_IP = loadString("Host name/IP (optional)");
    public static String MS_ADD_RULE = loadString("Add rule");
    public static String MS_PASTE_JID = loadString("Paste Jid");
    public static String MS_GOTO_URL = loadString("Goto URL");
    public static String MS_CLOCK_OFFSET = loadString("Clock offset");
    public static String MS_YES = loadString("Yes");
    public static String MS_SUSPEND = loadString("Suspend");
    public static String MS_ALERT_PROFILE_CMD = loadString("Alert Profile >");
    public static String MS_MY_VCARD = loadString("My vCard");
    public static String MS_TRANSPORTS = loadString("Transports");
    public static String MS_NEW_ACCOUNT = loadString("New Account");
    public static String MS_SELF_CONTACT = loadString("Self-contact");
    public static String MS_VCARD = loadString("vCard");
    public static String MS_SET_SUBJECT = loadString("Set Subject");
    public static String MS_TOOLS = loadString("Tools");
    public static String MS_PORT = loadString("Port");
    public static String MS_RESUME = loadString("Resume");
    public static String MS_ARE_YOU_SURE_WANT_TO_DISCARD = loadString("Are You sure want to discard ");
    public static String MS_FROM_OWNER_TO = loadString(" from OWNER to ");
    public static String MS_MODIFY_AFFILIATION = loadString("Modify affiliation");
    public static String MS_CLEAR = loadString("Clear");
    public static String MS_SELLOGIN = loadString("Connect");
    public static String MS_UNAFFILIATE = loadString("Unaffiliate");
    public static String MS_GRANT_MODERATOR = loadString("Grant Moderator");
    public static String MS_REVOKE_MODERATOR = loadString("Revoke Moderator");
    public static String MS_GRANT_ADMIN = loadString("Grant Admin");
    public static String MS_GRANT_OWNERSHIP = loadString("Grant Ownership");
    public static String MS_VIZITORS_FORBIDDEN = loadString("Visitors are not allowed to send messages to all occupants");
    public static String MS_IS_INVITING_YOU = loadString(" is inviting You to ");
    public static String MS_ASK_SUBSCRIPTION = loadString("Ask subscription");
    public static String MS_GRANT_SUBSCRIPTION = loadString("Grant subscription");
    public static String MS_INVITE = loadString("Invite to conference");
    public static String MS_REASON = loadString("Reason");
    public static String MS_YOU_HAVE_BEEN_INVITED = loadString("You have been invited to ");
    public static String MS_DISCO_ROOM = loadString("Participants");
    public static String MS_CAPS_STATE = loadString("Abc");
    public static String MS_STORE_PRESENCE = loadString("Room presences");
    public static String MS_IS_NOW_KNOWN_AS = loadString(" is now known as ");
    public static String MS_WAS_BANNED = loadString(" was banned ");
    public static String MS_WAS_KICKED = loadString(" was kicked ");
    public static String MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY = loadString(" has been kicked because room became members-only");
    public static String MS_HAS_LEFT_CHANNEL = loadString(" has left the channel");
    public static String MS_HAS_JOINED_THE_CHANNEL_AS = loadString(" has joined the channel as ");
    public static String MS_AND = loadString(" and ");
    public static String MS_IS_NOW = loadString(" is now ");
    public static String MS_ERROR = loadString("Error");
    public static String MS_SELECT_HISTORY_FOLDER = loadString("Choose folder");
    //public static String MS_NEW_MENU=loadString("show new menu");
    public static String MS_SOUND_VOLUME = loadString("Sound volume");
    public static String MS_LANGUAGE = loadString("Language");
    public static String MS_SAVE_HISTORY = loadString("Save history");
    public static String MS_SAVE_PRESENCES = loadString("Save presences");
    public static String MS_SAVE_HISTORY_CONF = loadString("Save conference history");
    public static String MS_SAVE_PRESENCES_CONF = loadString("Save conference presences");
    public static String MS_1251_CORRECTION = loadString("Convert to cp1251");
    public static String MS_HISTORY_FOLDER = loadString("History folder");
    public static String MS_COPY = loadString("Copy");
    public static String MS_PASTE = loadString("Paste");
    public static String MS_SAVE_TEMPLATE = loadString("Save template");
    public static String MS_TEMPLATE = loadString("Template");
    public static String MS_HAS_SET_TOPIC_TO = loadString("has set topic to");
    public static String MS_SEEN = loadString("Seen");
    public static String MS_IDLE = loadString("Idle");
    public static String MS_MAIN_MENU = loadString("Main menu");
    public static String MS_CHOOSE_STATUS = loadString("Choose status");
    public static String MS_ADD_STATUS = loadString("Add status");
    public static String MS_REMOVE_STATUS = loadString("Remove status");
    public static String MS_ADRESS = loadString("Address");
    public static String MS_EXPORT_TO_FILE = loadString("Make BackUp");
    public static String MS_IMPORT_TO_FILE = loadString("Import from BackUp");
    public static String MS_VIEW = loadString("View");
    public static String MS_STOP = loadString("Stop");
    public static String MS_FILE_TRANSFERS = loadString("File transfers");
//#ifdef FILE_TRANSFER
    public static String MS_PATH = loadString("Path");
    public static String MS_ACCEPT_FILE = loadString("Accept file");
    public static String MS_FILE = loadString("File");
    public static String MS_SAVE_TO = loadString("Save To");
    public static String MS_SENDER = loadString("Sender");
    public static String MS_REJECTED = loadString("Rejected");
    public static String MS_SEND_FILE = loadString("Send file");
    public static String MS_CANT_OPEN_FILE = loadString("Can't open file");
//#endif
    public static String MS_NEW = loadString("New");
    public static String MS_NEW_TEMPLATE = loadString("New Template");
    public static String MS_SAVE_PHOTO = loadString("Save photo");
//#ifdef COLOR_TUNE
    public static String MS_BALLOON_INK = loadString("balloon ink");
    public static String MS_BALLOON_BGND = loadString("balloon background");
    public static String MS_LIST_BGND = loadString("messagelist & roster background");
    public static String MS_LIST_BGND_EVEN = loadString("messagelist & roster even lines");
    public static String MS_LIST_INK = loadString("messagelist & roster & common font");
    public static String MS_MSG_SUBJ = loadString("Nick color");
    public static String MS_MSG_HIGHLIGHT = loadString("message highlight");
    public static String MS_DISCO_CMD = loadString("service discovery commands");
    public static String MS_BAR_BGND = loadString("panels background");
    public static String MS_BAR_INK = loadString("header font");
    public static String MS_CONTACT_DEFAULT = loadString("contact default");
    public static String MS_CONTACT_CHAT = loadString("contact chat");
    public static String MS_CONTACT_AWAY = loadString("contact away");
    public static String MS_CONTACT_XA = loadString("contact extended away");
    public static String MS_CONTACT_DND = loadString("contact do not disturb");
    public static String MS_GROUP_INK = loadString("group color");
    public static String MS_BLK_INK = loadString("keylock font");
    public static String MS_BLK_BGND = loadString("keylock background");
    public static String MS_MESSAGE_IN = loadString("message incoming");
    public static String MS_MESSAGE_OUT = loadString("message outgoing");
    public static String MS_MESSAGE_PRESENCE = loadString("message presence");
    public static String MS_MESSAGE_AUTH = loadString("message auth");
    public static String MS_MESSAGE_HISTORY = loadString("message history");
    public static String MS_PGS_REMAINED = loadString("progress bar remained");
    public static String MS_PGS_COMPLETE = loadString("progress bar complete");
    public static String MS_PGS_INK = loadString("progress bar font");
    public static String MS_HEAP_TOTAL = loadString("Heap mon total");
    public static String MS_HEAP_FREE = loadString("Heap mon free");
    public static String MS_CURSOR_BGND = loadString("Cursor background");
    public static String MS_CURSOR_OUTLINE = loadString("Cursor ink & outline");
    public static String MS_SCROLL_BRD = loadString("Scroll border");
    public static String MS_SCROLL_BAR = loadString("Scroll bar");
    public static String MS_SCROLL_BGND = loadString("Scroll back");
    public static String MS_MESSAGE_IN_S = loadString("other message incoming");
    public static String MS_MESSAGE_OUT_S = loadString("other message outgoing");
    public static String MS_MESSAGE_PRESENCE_S = loadString("other message presence");
    public static String MS_POPUP_MESSAGE = loadString("Popup font");
    public static String MS_POPUP_MESSAGE_BGND = loadString("Popup background");
    public static String MS_POPUP_SYSTEM = loadString("Popup system font");
    public static String MS_POPUP_SYSTEM_BGND = loadString("Popup system background");
    public static String MS_CONTACT_STATUS = loadString("Contact status font");
    public static String MS_CONTROL_ITEM = loadString("Control color");
//#endif
    public static String MS_COLOR_TUNE = loadString("Color tune");
    public static String MS_LOAD_SKIN = loadString("Load Scheme");
    public static String MS_SOUNDS_OPTIONS = loadString("Sounds options");
    public static String MS_TIME = loadString("Time");
    public static String MS_ROLE_PARTICIPANT = loadString("participant");
    public static String MS_ROLE_MODERATOR = loadString("moderator");
    public static String MS_ROLE_VISITOR = loadString("visitor");
    public static String MS_AFFILIATION_NONE = loadString("none");
    public static String MS_AFFILIATION_MEMBER = loadString("member");
    public static String MS_AFFILIATION_ADMIN = loadString("admin");
    public static String MS_AFFILIATION_OWNER = loadString("owner");
    public static String MS_SEC3 = loadString("second's");
    public static String MS_SEC2 = loadString("seconds");
    public static String MS_SEC1 = loadString("second");
    public static String MS_MIN3 = loadString("minute's");
    public static String MS_MIN2 = loadString("minutes");
    public static String MS_MIN1 = loadString("minute");
    public static String MS_HOUR3 = loadString("hour's");
    public static String MS_HOUR2 = loadString("hours");
    public static String MS_HOUR1 = loadString("hour");
    public static String MS_DAY3 = loadString("day's");
    public static String MS_DAY2 = loadString("days");
    public static String MS_DAY1 = loadString("day");
    public static String MS_AWAY_PERIOD = loadString("Minutes before away");
    public static String MS_AWAY_TYPE = loadString("Automatic Away");
    public static String MS_AWAY_OFF = loadString("disabled");
    public static String MS_AWAY_LOCK = loadString("keyblock");
    public static String MS_MESSAGE_LOCK = loadString("by message");
    public static String MS_AUTOSTATUS = loadString("AutoStatus");
    public static String MS_AUTOSTATUS_TIME = loadString("AutoStatus time (min)");
    public static String MS_AUTO_XA = loadString("Auto xa since %t");
    public static String MS_AUTO_AWAY = loadString("Auto away since %t");
    public static String MS_AUTOFOCUS = loadString("Autofocus");
    public static String MS_GRANT_MEMBERSHIP = loadString("Grant Membership");
    public static String MS_SURE_CLEAR = loadString("Are You sure want to clear messagelist?");
    public static String MS_TOKEN = loadString("Google token request");
    public static String MS_FEATURES = loadString("Features");
    public static String MS_SHOWPWD = loadString("Show password");
    public static String MS_NO_VERSION_AVAILABLE = loadString("No client version available");
    public static String MS_MSG_LIMIT = loadString("Message limit");
    public static String MS_OPENING_STREAM = loadString("Opening stream");
    //public final static String MS_SASL_STREAM="SASL handshake";
    public static String MS_ZLIB = loadString("Using compression");
    public static String MS_AUTH = loadString("Authenticating");
    public static String MS_RESOURCE_BINDING = loadString("Resource binding");
    public static String MS_SESSION = loadString("Initiating session");
    public static String MS_TEXTWRAP = loadString("Text wrapping");
    public static String MS_TEXTWRAP_CHARACTER = loadString("by chars");
    public static String MS_TEXTWRAP_WORD = loadString("by words");
    public static String MS_INFO = loadString("Info");
    public static String MS_REPLY = loadString("Reply");
    public static String MS_DIRECT_PRESENCE = loadString("Send status");
    public static String MS_CONFIRM_BAN = loadString("Are you sure want to BAN this person?");
    public static String MS_NO_REASON = loadString("No reason");
    public static String MS_RECENT = loadString("Recent");
    public static String MS_CAMERASHOT = loadString("Shot");
    public static String MS_SELECT_FILE = loadString("Select file");
    public static String MS_LOAD_PHOTO = loadString("Load Photo");
    public static String MS_CLEAR_PHOTO = loadString("Clear Photo");
    public static String MS_CAMERA = loadString("Camera");
    public static String MS_HIDE_FINISHED = loadString("Hide finished");
    public static String MS_TRANSFERS = loadString("Transfer tasks");
    public static String MS_SURE_DELETE = loadString("Are you sure want to delete this message?");
    public static String MS_NEW_BOOKMARK = loadString("New conference");
    public static String MS_ROOT = loadString("Root");
    public static String MS_DECLINE = loadString("Decline");
    public static String MS_AUTH_NEW = loadString("Authorize new contacts");
    public static String MS_AUTH_AUTO = loadString("[auto-subscribe]");
    public static String MS_KEEPALIVE = loadString("Keep-Alive");
    public static String MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM = loadString(" has been unaffiliated and kicked from members-only room");
    public static String MS_RENAME = loadString("Rename");
    public static String MS_MOVE = loadString("Move");
    public static String MS_SAVE = loadString("Save");
//#ifdef DETRANSLIT
    public static String MS_TRANSLIT = loadString("Translit");
    public static String MS_DETRANSLIT = loadString("ReTranslit");
    public static String MS_AUTODETRANSLIT = loadString("Auto translit2Cyr");
//#endif
    public static String MS_CHECK_UPDATE = loadString("Check Updates");
    public static String MS_SHOW_RESOURCES = loadString("Show Resources");
    public static String MS_COLLAPSED_GROUPS = loadString("Collapsed groups");
    public static String MS_SEND_BUFFER = loadString("Send Buffer");
    public static String MS_CHANGE_NICKNAME = loadString("Change nickname");
    public static String MS_MESSAGE_COLLAPSE_LIMIT = loadString("Message collapse limit");
    public static String MS_MESSAGE_WIDTH_SCROLL_2 = loadString("Scroll width");
    public static String MS_WITH_SYSTEM_GC = loadString("Memory cleaning");
    public static String MS_AUTOCLEAN_GROUPS = loadString("Auto clean groups");
    public static String MS_NO_CLIENT_INFO = loadString("No client info");
    public static String MS_CLEAN_ALL_MESSAGES = loadString("Delete all messages");
    public static String MS_DO_AUTOJOIN = loadString("Join marked (auto)");
    public static String MS_STATS = loadString("Statistics");
    public static String MS_STARTED = loadString("Started: ");
    public static String MS_TRAFFIC_STATS = loadString("Traffic stats: ");
    public static String MS_ALL = loadString("All: ");
    public static String MS_CURRENT = loadString("Current: ");
    public static String MS_LAST_MESSAGES = loadString("Last Messages");
    public static String MS_EDIT_JOIN = loadString("Edit/join");
    public static String MS_USE_COLOR_SCHEME = loadString("Use this Color scheme");
    public static String MS_DELETE_ALL = loadString("Delete All");
//#ifdef FILE_IO
    public static String MS_HISTORY_OPTIONS = loadString("History options");
//#ifdef DETRANSLIT
    public static String MS_1251_TRANSLITERATE_FILENAMES = loadString("Filenames transliterate");
//#endif
    public static String MS_SAVE_CHAT = loadString("Save chat");
//#endif
    public static String MS_SHOW_STATUSES = loadString("Show statuses");
    public static String MS_SHOW_HARDWARE = loadString("Shared platform info");
    public static String MS_DELIVERY = loadString("Delivery events");
    public static String MS_NIL_DROP_MP = loadString("Drop all");
    public static String MS_NIL_DROP_P = loadString("Receive messages");
    public static String MS_NIL_ALLOW_ALL = loadString("Messages & presences");
    public static String MS_FONTSIZE_NORMAL = loadString("Normal");
    public static String MS_FONTSIZE_SMALL = loadString("Small");
    public static String MS_FONTSIZE_LARGE = loadString("Large");
    //public static String MS_ALERT_PROFILE_AUTO = loadString( "Auto" );
    public static String MS_ALERT_PROFILE_ALLSIGNALS = loadString("All signals");
    public static String MS_ALERT_PROFILE_VIBRA = loadString("Vibra");
    public static String MS_ALERT_PROFILE_NOSIGNALS = loadString("No signals");
    public static String MS_IS_DEFAULT = loadString(" (default)");
    public static String MS_QUIT_ASK = loadString("Quit?");
    public static String MS_SURE_QUIT = loadString("Are you sure want to Quit?");
    public static String MS_CONFIRM_EXIT = loadString("Exit confirmation");
    public static String MS_SHOW_LAST_APPEARED_CONTACTS = loadString("Show last appeared contacts");
    public static String MS_CUSTOM_KEYS = loadString("Custom keys");
//#ifdef USER_KEYS
    public static String MS_KEYS_ACTION = loadString("Keys action");
    public static String MS_ENABLED = loadString("Enabled");
    public static String MS_KEY = loadString("Key");
//#endif
    public static String MS_APPLY = loadString("Apply");
    public static String MS_RECONNECT = loadString("Reconnect");
    public static String MS_SORT = loadString("Sort list");
    public static String MS_FLASHLIGHT = loadString("Turn on light");
    public static String MS_CLEAR_POPUPS = loadString("Clear popups");
    public static String MS_MESSAGE_COUNT_LIMIT = loadString("Chat history length");
//2007-10-24 voffk
    public static String MS_SUBSCRIPTION_REQUEST_FROM_USER = loadString("This user wants to subscribe to your presence");
    public static String MS_SUBSCRIPTION_RECEIVED = loadString("You are now authorized");
    public static String MS_SUBSCRIPTION_DELETED = loadString("Your authorization has been removed!");
    public static String MS_SEND_FILE_TO = loadString("To: ");
    public static String MS_FILE_SIZE = loadString("Size:");
    public static String MS_SUN = loadString("Sun");
    public static String MS_MON = loadString("Mon");
    public static String MS_TUE = loadString("Tue");
    public static String MS_WED = loadString("Wed");
    public static String MS_THU = loadString("Thu");
    public static String MS_FRI = loadString("Fri");
    public static String MS_SAT = loadString("Sat");
//2007-11-04
    public static String MS_SUBSCR_AUTO = loadString("Automatic subscription");
    public static String MS_SUBSCR_ASK = loadString("Ask me");
    public static String MS_SUBSCR_DROP = loadString("Drop subscription");
    public static String MS_SUBSCR_REJECT = loadString("Deny subscription");  //TODO: correct according to RFC
    public static String MS_VISIBLE_GROUP = "Visible";
//2007-11-07
    public static String MS_SEARCH = loadString("Search");
    public static String MS_REGISTER = loadString("Register");
    public static String MS_CHECK_GOOGLE_MAIL = loadString("Check Google mail");
//2008-01-18
    public static String MS_BLINKING = loadString("Blink");
    public static String MS_NOTICES_OPTIONS = loadString("Notices options");
    public static String MS_MESSAGE_SOUND = loadString("Message sound");
    public static String MS_ONLINE_SOUND = loadString("Online sound");
    public static String MS_OFFLINE_SOUND = loadString("Offline sound");
    public static String MS_MESSAGE_FOR_ME_SOUND = loadString("\"Message for me\" sound");
    public static String MS_COMPOSING_SOUND = loadString("Composing sound");
    public static String MS_CONFERENCE_SOUND = loadString("Conference sound");
    public static String MS_STARTUP_SOUND = loadString("StartUP sound");
    public static String MS_OUTGOING_SOUND = loadString("Outgoing sound");
    public static String MS_VIP_SOUND = loadString("Vip sound");
//2008-01-19
    public static String MS_COPY_JID = loadString("Copy JID");
    public static String MS_PING = loadString("Ping request");
    public static String MS_ONLINE_TIME = loadString("Online time");
//2008-01-20
    public static String MS_BREAK_CONECTION = loadString("Break connection");
    public static String MS_AUTOSCROLL = loadString("AutoScroll");
    public static String MS_EMULATE_TABS = loadString("Emulate tabs");
    public static String MS_HIDE_TIMESTAMPS = loadString("Hide timestamps in messages");
    public static String MS_POPUPS = loadString("PopUps");
    public static String MS_USE_MY_STATUS_MESSAGES = loadString("Use my status messages");
    public static String MS_MEMORY = loadString("Memory:");
    public static String MS_FREE = loadString("Free: ");
    public static String MS_TOTAL = loadString("Total: ");
    public static String MS_CONN = loadString("Session(s): ");
    public static String MS_DESCRIPTION = loadString("Description");
//2008-01-24
    public static String MS_USER = loadString("User");
//#if IMPORT_EXPORT
    public static String MS_IMPORT_EXPORT = loadString("Import/Export");
//#endif
//2008-02-03
    public static String MS_BOLD_FONT = loadString("Bold font for contacts");
    public static String MS_RUNNING_MESSAGE = loadString("Running message");
    public static String MS_COMPOSING_NOTIFY = loadString("Composing message to you");
    public static String MS_COMPRESSION = loadString("Compression");
    public static String MS_NEW_ROOM_CREATED = loadString("New room created");
    public static String MS_IRCLIKESTATUS = loadString("IRC-like conference nick-status");
    public static String MS_SIMULATED_BREAK = loadString("Simulated break");
    public static String MS_BUILD_NEW = loadString("Build new version on constructor");
    public static String MS_DISABLED = loadString("disabled");
    public static String MS_LOAD_ROOMLIST = loadString("Browse rooms");
    public static String MS_AUTORESPOND = loadString("Autorespond");
    public static String MS_INVERT = loadString("Invert colors");
//    public static String MS_ENTER_ACCOUNT_SETTINGS = loadString( "Please create account or enter account(s) settings.\n\nMenu, New Account" );
//    public static String MS_ENTER_SETTINGS = loadString( "Please enter settings.\n\nMenu, Tools, Options" );
    public static String MS_XML_CONSOLE = loadString("XML console");
//#ifdef CLIPBOARD
    public static String MS_CLIPBOARD = loadString("Clipboard");
//#endif
    public static String MS_BAR_FONT = loadString("Bar font");
    public static String MS_POPUP_FONT = loadString("Popup & balloon font");
    public static String MS_FONTS_OPTIONS = loadString("Fonts options");
    public static String MS_LOAD_HISTORY = loadString("Load history");
    public static String MS_CONNECT_TO = loadString("Connect to");
    public static String MS_SEND_COLOR_SCHEME = loadString("Send my color scheme");
    public static String MS_UNREAD_MESSAGES = loadString("Unread messages");
    public static String MS_VIBRATE_ONLY_HIGHLITED = loadString("Vibrate only highlited");
    public static String MS_EXTENDED_SETTINGS = loadString("Extended settings");
    public static String MS_SAVE_TO_FILE = loadString("Save to file");
    public static String MS_LOAD_FROM_FILE = loadString("Load from file");
    public static String MS_SHOW_IQ_REQUESTS = loadString("Show iq requests");
    public static String MS_SHOW_CLIENTS_ICONS = loadString("Show clients icons");
    public static String MS_ACTION = loadString("Action");
    public static String MS_VALUE = loadString("Value");
    public static String MS_RECONNECT_COUNT_RETRY = loadString("Quantity of attempts");
    public static String MS_RECONNECT_WAIT = loadString("Delay before reconnect (sec.)");
    public static String MS_MENU = loadString("Menu");
    //public static String MS_CHANGE=loadString("Change");
    public static String MS_NEXT = loadString("Next");
    public static String MS_PREVIOUS = loadString("Previous");
    public static String MS_PREVIOUS_ = loadString("Previous: ");
    public static String MS_END_OF_VCARD = loadString("[End of vCard]");
    public static String MS_NO_VCARD = loadString("[No vCard available]");
    public static String MS_NO_PHOTO = loadString("[No photo available]");
    public static String MS_UNSUPPORTED_FORMAT = loadString("[Unsupported format]");
    public static String MS_PHOTO_TOO_LARGE = loadString("[Large photo was dropped]");
    public static String MS_DELETE_GROUP_ASK = loadString("Delete group?");
    public static String MS_PANELS = loadString("Panels");
    public static String MS_NO_BAR = loadString("[ ]");
    public static String MS_MAIN_BAR = loadString("[Main bar]");
    public static String MS_INFO_BAR = loadString("[Info bar]");
    public static String MS_FLASH_BACKLIGHT = loadString("Flash backlight");
    public static String MS_EXECUTE_MENU_BY_NUMKEY = loadString("Execute menu by numkey");
    public static String MS_SHOW_NACKNAMES = loadString("Show nicknames");
    public static String MS_SEND_PHOTO = loadString("Send photo");
    public static String MS_ENABLE_AUTORESPOND = loadString("Enable autorespond");
    public static String MS_FILE_MANAGER = loadString("File manager");
    public static String MS_ADHOC = loadString("Remote control");
    public static String MS_USE_DNS_SRV_RESOLVER = loadString("Resolve hostname");
    public static String MS_SHOW_TIME_TRAFFIC = loadString("Show time and traffic");
    public static String MS_USERS_SEARCH = loadString("Users search");
//#ifdef PEP
    public static String MS_PEP = loadString("Personal events");
    public static String MS_RECEIVE_PEP = loadString("Receive events");
    public static String MS_PUBLISH_PEP = loadString("Publish events");
    public static String MS_USERMOOD = loadString("User moods");
    public static String MS_USERTUNE = loadString("User tune");
    public static String MS_USERACTIVITY = loadString("User activity");
    public static String MS_USERLOCATION = loadString("User location");
    public static String MS_LATITUDE = loadString("Latitude");
    public static String MS_LONGITUDE = loadString("Longitude");
    public static String MS_LOCATION_NAME = loadString("Location name");
    public static String MS_LOCATION_DESCRIPTION = loadString("Location description");
    public static String MS_RETRIEVE_LOCATION = loadString("Retrieve location");
    public static String MS_SEND_RECEIVE_USERMOODS = loadString("user moods send/receive");
    public static String MS_PEP_NOT_SUPPORTED = loadString("Personal events not supported");
//#endif
    public static String MS_VIP_GROUP = loadString("VIP");
    public static String MS_ENABLE_DISABLE = loadString("Enable/Disable");
    public static String MS_ONLINE = loadString("Online");
    public static String MS_CHAT = loadString("Chat");
    public static String MS_AWAY = loadString("Away");
    public static String MS_XA = loadString("xa");
    public static String MS_INVISIBLE = loadString("Invisible");
    public static String MS_DND = loadString("DND");
    public static String MS_OFFLINE = loadString("Offline");
    public static String MS_USE = loadString("Use");
    public static String MS_VERSION = loadString("Version");
//#ifdef JUICK
    public static String MS_JUICK_FOCUS = loadString("Focus to Juick contact");
    public static String MS_JUICK_MESSAGE_REPLY = loadString("[J] Reply to message");
    public static String MS_JUICK_SEND_PRIVATE_REPLY = loadString("[J] Reply to");
    public static String MS_JUICK_THINGS = loadString("[J] Things");
    public static String MS_JUICK_MESSAGE_DELETE = loadString("[J] Delete message");
    public static String MS_JUICK_POST_SUBSCRIBE = loadString("[J] Subscribe to comments");
    public static String MS_JUICK_POST_UNSUBSCRIBE = loadString("[J] Unsubscribe from comments");
    public static String MS_JUICK_POST_RECOMMEND = loadString("[J] Recommend post");
    public static String MS_JUICK_POST_SHOW = loadString("[J] Show post and comments");
    public static String MS_JUICK_CONTACT_NOT_FOUND = loadString("For work this command you need contact juick@juick.com in you roster. For more information see http://juick.com/help/");
//#endif
    public static String MS_STEP_BACK = loadString("Step back");
    public static String MS_ITEM_HEIGHT = loadString("Minimal item height");
    public static String MS_SINGLE_CLICK = loadString("Single-click interface");
    public static String L_CONFIG = loadString("Light config");
    public static String L_ENABLED = loadString("Light enabled");
    public static String L_IDLE_VALUE = loadString("Light idle value");
    public static String L_KEYPRESS_VALUE = loadString("Light keypress value");
    public static String L_KEYPRESS_TIMEOUT = loadString("Keypress timeout");
    public static String L_MESSAGE_VALUE = loadString("Message value");
    public static String L_MESSAGE_TIMEOUT = loadString("Message timeout");
    public static String MS_COPY_TOPIC = loadString("Copy topic");
    public static String MS_COLLAPSE_ALL = loadString("Collapse all");
    public static String MS_SESSION_MANAGEMENT = loadString("Session management");
    public static String MS_SESSION_RESUMING = loadString("Session resuming");
    public static String MS_SUPPORTED = loadString("Supported");

    private SR() {
    }
    public static String MS_XMLLANG;
    public static String MS_IFACELANG;

    private static void loadLang() {
        if (lang == null) {
            String langFile = Config.getInstance().langFileName();
            if (langFile != null) {
                lang = new StringLoader().hashtableLoader(langFile);
            }
            if (lang == null) {
                lang = new Hashtable();
            }
            MS_XMLLANG = (String) lang.get("xmlLang");

            MS_IFACELANG = MS_XMLLANG;
            if (MS_IFACELANG == null) {
                MS_IFACELANG = "en";
                MS_XMLLANG = "en";
            }
        }
    }

    private static String loadString(String key) {
        if (lang == null) {
            loadLang();
        }
        String value = (String) lang.get(key);
//#ifdef LANG_DEBUG
//#         if (value==null) {
//#             if (!lang.isEmpty()) {
//#                 System.out.print("-----> Can't find local string for <");
//#                 System.out.print(key);
//#                 System.out.println("> <-----");
//#             }
//#         } else {
//#             System.out.print(key);
//#             System.out.print('\t');
//#             System.out.println(value);
//#         }
//#endif
        return (value == null) ? key : value;
    }

    public static String getPresence(String presenceName) {
        if (presenceName.equals("online")) {
            return MS_ONLINE;
        } else if (presenceName.equals("chat")) {
            return MS_CHAT;
        } else if (presenceName.equals("away")) {
            return MS_AWAY;
        } else if (presenceName.equals("xa")) {
            return MS_XA;
        } else if (presenceName.equals("invisible")) {
            return MS_INVISIBLE;
        } else if (presenceName.equals("dnd")) {
            return MS_DND;
        } else if (presenceName.equals("unavailable")) {
            return MS_OFFLINE;
        }
        return null;
    }

    public static void loaded() {
        if (lang != null) {
            lang.clear();
            lang = null;
        }
    }
}
