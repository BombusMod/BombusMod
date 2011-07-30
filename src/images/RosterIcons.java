/*
 * RosterIcons.java
 *
 * Created on 3.12.2005, 20:02
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

package images;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.ImageList;
import util.StringLoader;

/**
 *
 * @author EvgS
 */
public class RosterIcons extends ImageList{
    
    private static RosterIcons instance;

    public static RosterIcons getInstance() {
	if (instance==null) instance=new RosterIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    //private final static int ICONS_IN_COL=6;

    private Hashtable transports;
    private Vector transpSkins;
    /** Creates a new instance of RosterIcons */
    private RosterIcons() {
	super("/images/skin.png", 0, ICONS_IN_ROW);
        
        transports=new StringLoader().hashtableLoader("/images/transports.txt"); //new Hashtable();
        if (transports != null) { // without skin
            transpSkins=new Vector(transports.size());
            transports.put("conference", new Integer(ICON_GROUPCHAT_INDEX));
        }
    }
    
    public int getTransportIndex(String name){
        if (transports == null) return 0;
        Object o=transports.get(name);
        if (o instanceof String) {
            int index=(transpSkins.size()+1)<<24;
            // loading additional skin
            ImageList customTransp=new ImageList((String) o, 1, ICONS_IN_ROW);
            // customTransp loading success?
            if (customTransp.getHeight()==0) customTransp=this;
            
            transpSkins.addElement( customTransp );
            transports.put(name, new Integer(index) );
            
            return index;
        } else {
            return (o==null)?0:((Integer)o).intValue();
        }
    }
   

    public void drawImage(Graphics g, int index, int x, int y) {        
        if (index>66000) { //draw transport icons
            if (transports == null) return;
            ((ImageList)transpSkins.elementAt( (index>>24) -1 )).drawImage(g, index & 0xff, x, y);
        } else super.drawImage(g, index, x, y);
    }

    public final static int ICON_PRESENCE_ONLINE = 0x00; //1. Контакт 'Доступен'.
    public final static int ICON_PRESENCE_CHAT = 0x01; //2. Контакт 'Готов поболтать'.
    public final static int ICON_PRESENCE_AWAY = 0x02; //3. Контакт 'Отсутствую'.
    public final static int ICON_PRESENCE_XA = 0x03; //4. Контакт 'Недоступен'.
    public final static int ICON_PRESENCE_DND = 0x04; //5. Контакт 'Не беспокоить'.
    public final static int ICON_PRESENCE_OFFLINE = 0x05; //6. Контакт 'Отключенный'.
    public final static int ICON_PRESENCE_ASK = 0x06; //7. Контакт, у которого вы запросили подписку (подписка ask).
    public final static int ICON_PRESENCE_UNKNOWN = 0x07; //8. Неизвестный контакт.

    public static final int ICON_INVISIBLE_INDEX = 0x10; //1. Невидимый контакт.
    public static final int ICON_ERROR_INDEX = 0x11; // 2. Контакт с ошибкой.
    public static final int ICON_TRASHCAN_INDEX = 0x12; // 3. Удалённый контакт.
    public static final int ICON_PROGRESS_INDEX = 0x13; // 4. Иконка, которая находится вверху при подключении.
    public static final int ICON_SEARCH_INDEX = 0x14; // 5. Поиск.
    public static final int ICON_VIEWING_INDEX = 0x14;
    public static Integer iconHasVcard=new Integer(ICON_SEARCH_INDEX);
    public static final int ICON_REGISTER_INDEX = 0x15; // 6. Иконка 'Зарегистрировать новый аккаунт'.
    public static final int ICON_MSGCOLLAPSED_INDEX = 0x16; // Иконка свёрнутого сообщения.
    public static final int ICON_KEYBLOCK_INDEX = 0x17; // Иконка блокировки.

    public static final int ICON_MESSAGE_INDEX = 0x20; //1. Иконка сообщения.
    public static final int ICON_AUTHRQ_INDEX = 0x21; //2. Иконка сообщения авторизации.
    public static final int ICON_COMPOSING_INDEX = 0x22; //3. Иконка 'Контакт набирает сообщение'.
    public static final int ICON_AD_HOC=ICON_COMPOSING_INDEX;
    public static final int ICON_EXPANDED_INDEX = 0x23; //4. Иконка развёрнутой группы ростера.
    public static final int ICON_COLLAPSED_INDEX = 0x24; //5. Иконка свёрнутой группы ростера.
    public static final int ICON_SCROLLABLE_BOTH = 0x25; // 6. Иконка прокрутки в обе стороны в всплывающем окне.
    public static final int ICON_SCROLLABLE_DOWN = 0x26; // 7. Иконка прокрутки вниз в всплывающем окне.
    public static final int ICON_DELIVERED_INDEX = 0x27; //8. Иконка доставленного сообщения
    public static final int ICON_SCROLLABLE_UP = 0x27; // 8. Иконка  конца прокрутки в всплывающем окне.

    public static final int ICON_PROFILE_INDEX = 0x30; // 1. Профили.
                                                       // 2. Иконка 'Все сигналы'.
                                                       // 3. Иконка 'Только вибрация'.
    public static final int ICON_SOUNDS_INDEX = 0x33;  // 4. Иконка 'Только звуки'.
                                                       // 5. Иконка 'Без сигналов'.
    public static final int ICON_PRIVACY_ALLOW = 0x36; // 7. Иконка 'Разрешить', в списках приватности.
    public static final int ICON_APPEARING_INDEX = 0x36; // 7. Иконка 'появился в сети' (уведомление картинкой)
    public static final int ICON_PRIVACY_BLOCK = 0x37; // 8. Иконка 'Запретить', в списках приватности.

    public static final int ICON_GROUPCHAT_INDEX = 0x40; // 1. Иконка конференции.
    public static final int ICON_GCJOIN_INDEX = 0x41; // 2. Иконка развёрнутой группы конференции в ростере.
    public static final int ICON_GCCOLLAPSED_INDEX = 0x42; // 3. Иконка свёрнутой группы конференции в ростере.
    public static final int ICON_TRANSPARENT = 0x44;
    public static Integer iconTransparent =new Integer(ICON_TRANSPARENT);
    public static final int ICON_PRIVACY_ACTIVE = 0x46; // 7. Иконка активного списка приватности.
    public static final int ICON_ROOMLIST=ICON_PRIVACY_ACTIVE; // Конференция в списке конференций.
    public static final int ICON_PRIVACY_PASSIVE = 0x47; // 8. Иконка неактивного списка приватности.


    public static final int ICON_MODERATOR_INDEX = 0x50;
    public static final int ICON_CHECKBOX_UNCHECKED = 0x56;
    public static final int ICON_CHECKBOX_CHECKED = 0x57;
    public static final int ICON_IMAGES_INDEX = 0x57;


    // was ActionIcons

    public static final int ICON_ON             = 0x60; //Подключить
    public static final int ICON_OFF            = 0x61; //Отключить
    public static final int ICON_NICK_RESOLVE   = 0x62; //Преобразовать в ник
    public static final int ICON_VCARD          = 0x63; //vCard
    public static final int ICON_INFO           = 0x64; //инфо
    public static final int ICON_VERSION        = 0x65; //Версия клиента
    public static final int ICON_COMMAND        = 0x66; //Команды
    public static final int ICON_SEND_BUFFER    = 0x67; //Послать текст из буфера

    public static final int ICON_COPY_JID       = 0x70; //Копировать JID
    public static final int ICON_SEND_COLORS    = 0x71; //Send current color scheme
    public static final int ICON_TIME           = 0x72; //Время
    public static final int ICON_IDLE           = 0x73; //Бездействие
    public static final int ICON_PING           = 0x74; //Время отклика
    public static final int ICON_ONLINE         = 0x75; //Время в сети
    public static final int ICON_INVITE         = 0x76; //Пригласить в конференцию
    public static final int ICON_SUBSCR         = 0x77; //Подписка

    public static final int ICON_COPY_TOPIC     = 0x80; //Переместить
    public static final int ICON_DELETE         = 0x81; //Удалить
    public static final int ICON_LEAVE          = 0x82; //Покинуть комнату
    public static final int ICON_SET_STATUS     = 0x83; //Установить статус
    public static final int ICON_CHANGE_NICK    = 0x84; //Сменить ник
    public static final int ICON_OWNERS         = 0x85; //Владельцы
    public static final int ICON_ADMINS         = 0x86; //Администраторы
    public static final int ICON_MEMBERS        = 0x87; //Члены

    public static final int ICON_OUTCASTS       = 0x90; //Изгои(Ban)
    public static final int ICON_KICK           = 0x91; //Выгнать (kick)
    public static final int ICON_BAN            = 0x92; //Бан (ban)
    public static final int ICON_DEVOICE        = 0x93; //Отнять право голоса
    public static final int ICON_VOICE          = 0x94; //Дать право голоса
    public static final int ICON_OWNER          = 0x95; //Дать право владельца
    public static final int ICON_ADMIN          = 0x96; //Дать право администратора
    public static final int ICON_DEMEMBER       = 0x97; //Отнять членство

    public static final int ICON_MEMBER         = 0xa0; //Дать членство
    public static final int ICON_CONFIGURE      = 0xa1; //Конфигуратор комнаты
    public static final int ICON_SEND_FILE      = 0xa2; //Послать файл
    public static final int ICON_RENAME         = 0xa3; //Переименовать
    public static final int ICON_CONSOLE        = 0xa4; //Консоль
    public static final int ICON_SENDPHOTO      = 0xa5; //Отправить фото

     //was ChatIcons

    public static final int ICON_NEW            = 0xb0; //New message
    public static final int ICON_QUOTE          = 0xb1; //Quote
    public static final int ICON_CLEAR          = 0xb2; // Clear chat
    public static final int ICON_SELECT         = 0xb3; // Select message
    public static final int ICON_COPY           = 0xb4; // Copy to clipboard
    public static final int ICON_COPYPLUS       = 0xb5; // Append to clipboard
    public static final int ICON_CONTACT        = 0xb6; // Contact actions
    public static final int ICON_ACTIVECONTACTS = 0xb7; // Active contacts

    public static final int ICON_CHATARCHIVE    = 0xc0; // save to archive
    public static final int ICON_TEMPLATES      = 0xc1; // save as template
    public static final int ICON_SENDBUF        = 0xc2; // send from clipboard
    public static final int ICON_BACK           = 0xc3; // go back/cancel
    public static final int ICON_ACCEPTAUTH     = 0xc4; // accept auth
    public static final int ICON_DECLINEAUTH    = 0xc5; // decline auth
    public static final int ICON_ACCEPTFILE     = 0xc6; // accept file
    public static final int ICON_DECLINEFILE    = 0xc7; // decline file

    public static final int ICON_RESUME         = 0xd0; // resume message
    public static final int ICON_REPLY          = 0xd1; // reply to message
    public static final int ICON_SAVECHAT       = 0xd2; // save selected
    public static final int ICON_CHATHISTORY        = 0xd3; // view history
    public static final int ICON_PASTE          = 0xd4; // paste from clipboard
    public static final int ICON_USESKIN        = 0xd5; // use selected skin
    public static final int ICON_GOTOURL        = 0xd6; // goto url

    // was MenuIcons
    public static final int ICON_ACCOUNTS   = 0xe0;
    public static final int ICON_MENUVCARD      = 0xe1;
    public static final int ICON_ADD_CONTACT= 0xe2;
    public static final int ICON_FONTS      = 0xe3;
    public static final int ICON_CHECK_UPD  = 0xe4;
    public static final int ICON_KEYS       = 0xe5;
    public static final int ICON_ABOUT      = 0xe6;
    public static final int ICON_MENUCONSOLE    = 0xe7;

    public static final int ICON_FILEMAN    = 0xf0;
    public static final int ICON_MOOD       = 0xf1;
    public static final int ICON_ARCHIVE    = 0xf2;
    public static final int ICON_PRIVACY    = 0xf3;
    public static final int ICON_MINIMIZE   = 0xf4;
    public static final int ICON_RECONNECT  = 0xf5;
    public static final int ICON_STATUS     = 0xf6;
    public static final int ICON_ALERTS     = 0xf7;

    public static final int ICON_GMAIL      = 0x100;
    public static final int ICON_CONFERENCE = 0x101;
    public static final int ICON_BUILD_NEW  = 0x102;
    public static final int ICON_HISTORY    = 0x103;
    public static final int ICON_SETTINGS   = 0x104;
    public static final int ICON_COLOR_TUNE = 0x105;
    public static final int ICON_DISCO      = 0x106;
    public static final int ICON_TOOLS      = 0x107;

    public static final int ICON_STAT       = 0x110;
    public static final int ICON_TASKS      = 0x111;
    public static final int ICON_NOTIFY     = 0x112;
    public static final int ICON_CLEAN_MESSAGES= 0x113;
    public static final int ICON_FT         = 0x114;
    public static final int ICON_ACTIVE     = 0x115;
    public static final int ICON_JUICK      = 0x116;
    public static final int ICON_IE         = 0x117;

    public static final int ICON_URL        = 0x120;
    public static final int ICON_INVERSE    = 0x121;
    public static final int ICON_MENU       = 0x122; //меню
    public static final int ICON_OK         = 0x123; //ok
    public static final int ICON_CANCEL     = 0x124; // отмена
    public static final int ICON_ACTIONS    = 0x125; // действия
    
   

}
