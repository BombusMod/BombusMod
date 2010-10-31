/*
 * MenuActionsIcons.java
 *
 * Created on 9.07.2008, 9:39
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

import javax.microedition.lcdui.Graphics;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class MenuActionsIcons extends ImageList {
    
    private static MenuActionsIcons instance;
    public static MenuActionsIcons getInstance() {
	if (instance==null) instance=new MenuActionsIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=9;

    /** Creates a new instance of RosterIcons */
    private MenuActionsIcons() {
	super("/images/menuactions.png", ICONS_IN_COL, ICONS_IN_ROW);
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        super.drawImage(g, index, x, y);
    }
    
    public static final int ICON_ON             = 0x00; //Подключить
    public static final int ICON_OFF            = 0x01; //Отключить
    public static final int ICON_NICK_RESOLVE   = 0x02; //Преобразовать в ник
    public static final int ICON_VCARD          = 0x03; //vCard
    public static final int ICON_INFO           = 0x04; //инфо
    public static final int ICON_VERSION        = 0x05; //Версия клиента
    public static final int ICON_COMMAND        = 0x06; //Команды
    public static final int ICON_SEND_BUFFER    = 0x07; //Послать текст из буфера
    
    public static final int ICON_COPY_JID       = 0x10; //Копировать JID
    public static final int ICON_SEND_COLORS    = 0x11; //Send current color scheme
    public static final int ICON_TIME           = 0x12; //Время
    public static final int ICON_IDLE           = 0x13; //Бездействие
    public static final int ICON_PING           = 0x14; //Время отклика
    public static final int ICON_ONLINE         = 0x15; //Время в сети
    public static final int ICON_INVITE         = 0x16; //Пригласить в конференцию
    public static final int ICON_SUBSCR         = 0x17; //Подписка
    
    public static final int ICON_MOVE           = 0x20; //Переместить
    public static final int ICON_DELETE         = 0x21; //Удалить
    public static final int ICON_LEAVE          = 0x22; //Покинуть комнату
    public static final int ICON_SET_STATUS     = 0x23; //Установить статус
    public static final int ICON_CHANGE_NICK    = 0x24; //Сменить ник
    public static final int ICON_OWNERS         = 0x25; //Владельцы
    public static final int ICON_ADMINS         = 0x26; //Администраторы
    public static final int ICON_MEMBERS        = 0x27; //Члены
    
    public static final int ICON_OUTCASTS       = 0x30; //�?згои(Ban)
    public static final int ICON_KICK           = 0x31; //Выгнать (kick)
    public static final int ICON_BAN            = 0x32; //Бан (ban)
    public static final int ICON_DEVOICE        = 0x33; //Отнять право голоса
    public static final int ICON_VOICE          = 0x34; //Дать право голоса
    public static final int ICON_OWNER          = 0x35; //Дать право владельца
    public static final int ICON_ADMIN          = 0x36; //Дать право администратора
    public static final int ICON_DEMEMBER       = 0x37; //Отнять членство
    
    public static final int ICON_MEMBER         = 0x40; //Дать членство
    public static final int ICON_CONFIGURE      = 0x41; //Конфигуратор комнаты
    public static final int ICON_SEND_FILE      = 0x42; //Послать файл
    public static final int ICON_RENAME         = 0x43; //Переименовать
    public static final int ICON_CONSOLE        = 0x44; //Консоль
    

    
    //public static final int ICON_VCARD      = 0x51;
    public static final int ICON_ADD_CONTACT= 0x52;
    public static final int ICON_FONTS      = 0x53;
    public static final int ICON_CHECK_UPD  = 0x54;
    public static final int ICON_CONCOLE    = 0x57;

    public static final int ICON_FILEMAN    = 0x60;
    public static final int ICON_MOOD       = 0x61;
    public static final int ICON_ARCHIVE    = 0x62;
    public static final int ICON_PRIVACY    = 0x63;
    public static final int ICON_RECONNECT  = 0x65;
    public static final int ICON_STATUS     = 0x66;
    public static final int ICON_NOTIFY     = 0x67;

    public static final int ICON_GMAIL      = 0x70;
    public static final int ICON_CONFERENCE = 0x71;
    public static final int ICON_BUILD_NEW  = 0x72;
    public static final int ICON_HISTORY    = 0x73;
    public static final int ICON_SETTINGS   = 0x74;
    public static final int ICON_COLOR_TUNE = 0x75;
    public static final int ICON_ITEM_ACTIONS = 0x77;

    public static final int ICON_STAT       = 0x80;
    public static final int ICON_TASKS      = 0x81;
    public static final int ICON_CLEAN_MESSAGES= 0x83;
    public static final int ICON_FT         = 0x84;

    public static final int ICON_KEYS       = 0x53;
    public static final int ICON_URL        = 0x65;
    public static final int ICON_DISCO      = 0x65;
    public static final int ICON_IE         = 0x74;
    public static final int ICON_INVERSE    = 0x75;
}
