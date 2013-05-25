//#if SYSTEM_NOTIFY && android
//# package Messages.notification;
//# 
//# import android.app.Activity;
//# import android.app.Notification;
//# import android.app.NotificationManager;
//# import android.app.PendingIntent;
//# import android.content.Context;
//# import android.content.Intent;
//# import android.support.v4.app.NotificationCompat;
//# import android.support.v4.app.NotificationCompat.InboxStyle;
//# import org.bombusmod.BombusModActivity;
//# import org.bombusmod.R;
//# import Client.Msg;
//# import Client.StaticData;
//# import locale.SR;
//# 
//# public class AndroidNotification implements Notificator {
//# 
//#     public static final int NOTIFY_ID = 1;
//# 
//#     public NotificationManager getNotificationManager(){
//#         return (NotificationManager) BombusModActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//#     }
//# 
//#     public void sendNotify(final String title, final String text) {
//#         NotificationManager mNotificationManager = (NotificationManager) BombusModActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//#         long when = 0;
//#         int icon = R.drawable.inc_msg;
//#         long[] vibraPattern = {0, 500, 250, 500};
//#         if (StaticData.getInstance().roster.highliteMessageCount < 1) {
//#             return;
//#         } else {
//#             Context context = BombusModActivity.getInstance();
//#             
//#             NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
//#             CharSequence contentTitle = (String) context.getApplicationContext().getText(R.string.notifyTitle);
//#             CharSequence contentText = (String) context.getApplicationContext().getText(R.string.notifyInfo) + ": " + StaticData.getInstance().roster.highliteMessageCount;
//#             Intent notificationIntent = new Intent(context, BombusModActivity.class);
//#             notificationIntent.setAction("org.bombusmod.bm-notify");
//#             PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//#             Intent replyIntent = new Intent(context, BombusModActivity.class);
//#             replyIntent.setAction("org.bombusmod.bm-notify.reply");
//#             PendingIntent piReply = PendingIntent.getActivity(context, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);
//#             //notification.setLights(0xff00ff00, 300, 1000);
//#             //notification.setVibrate(vibraPattern);
//#             notification.setDefaults(Notification.DEFAULT_ALL);
//#             notification.setContentIntent(contentIntent);
//#             notification.setContentTitle(contentTitle);
//#             notification.setContentText(contentText);
//#             notification.setSmallIcon(icon);
//#             NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
//#             style.addLine(text)
//#                     .setBigContentTitle(title)
//#                     .setSummaryText(contentText);
//#             notification.addAction(android.R.drawable.sym_action_chat, SR.MS_REPLY, piReply);
//#             
//#             notification.setStyle(style);
//#                     
//#             mNotificationManager.notify(NOTIFY_ID, notification.build());
//#         }
//#     }
//#     public void clear() {
//#         NotificationManager mNotificationManager = (NotificationManager) BombusModActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//#         mNotificationManager.cancel(NOTIFY_ID);
//#      }
//# }
//#endif

