/*
 * ActionsIcons.java
 *
 * Created on 29 Май 2008 г., 13:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import javax.microedition.lcdui.Graphics;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class ActionsIcons extends ImageList{
    
    private static ActionsIcons instance;

    public static ActionsIcons getInstance() {
	if (instance==null)
            instance=new ActionsIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=5;

    /** Creates a new instance of RosterIcons */
    private ActionsIcons() {
	super("/images/actions.png", ICONS_IN_COL, ICONS_IN_ROW);
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        super.drawImage(g, index, x, y);
    }
    
    public static final int ICON_ON             = 0x00; //Подключить
    public static final int ICON_OFF            = 0x01; //Отключить
    public static final int ICON_NICK_RESOLVE   = 0x02; //Преобразовать в ник
    public static final int ICON_VCARD          = 0x03; //vCard
    public static final int ICON_INFO           = 0x04; //Инфо
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
    
    public static final int ICON_OUTCASTS       = 0x30; //Изгои(Ban)
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
    //public static final int ICON_      = 0x45; //
   // public static final int ICON_      = 0x46; //
}
