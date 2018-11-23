//#if android
package Messages.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.support.v4.app.NotificationCompat;
import org.bombusmod.BombusModActivity;
import org.bombusmod.R;

import Client.StaticData;
import locale.SR;

public class AndroidNotificator implements Notificator {

    public static final int NOTIFY_ID = 1;

    private static final String CHANNEL_ID = "default";

    private NotificationManager notificationManager;

    public AndroidNotificator() {
        notificationManager = (NotificationManager) BombusModActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationChannel channel =  notificationManager.getNotificationChannel(CHANNEL_ID);
        if (channel == null) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "BombusMod",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("New messages");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotify(final String title, final String text) {
        long when = 0;
        int icon = R.drawable.inc_msg;
        long[] vibraPattern = {0, 500, 250, 500};
        if (StaticData.getInstance().roster.highliteMessageCount < 1) {
            return;
        } else {
            Context context = BombusModActivity.getInstance();
            
            NotificationCompat.Builder notification = Build.VERSION.SDK_INT < 26 ?
                    new NotificationCompat.Builder(context) : new NotificationCompat.Builder(context, CHANNEL_ID);
            CharSequence contentTitle = context.getApplicationContext().getText(R.string.notifyTitle);
            CharSequence contentText = context.getApplicationContext().getText(R.string.notifyInfo) + ": " + StaticData.getInstance().roster.highliteMessageCount;
            Intent notificationIntent = new Intent(context, BombusModActivity.class);
            notificationIntent.setAction("org.bombusmod.bm-notify");
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            Intent replyIntent = new Intent(context, BombusModActivity.class);
            replyIntent.setAction("org.bombusmod.bm-notify.reply");
            PendingIntent piReply = PendingIntent.getActivity(context, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);
            //notification.setLights(0xff00ff00, 300, 1000);
            notification.setVibrate(vibraPattern);
            notification.setDefaults(Notification.DEFAULT_ALL);
            notification.setContentIntent(contentIntent);
            notification.setContentTitle(contentTitle);
            notification.setContentText(contentText);
            notification.setSmallIcon(icon);
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            style.addLine(text)
                    .setBigContentTitle(title)
                    .setSummaryText(contentText);
            notification.addAction(android.R.drawable.sym_action_chat, SR.MS_REPLY, piReply);
            
            notification.setStyle(style);
                    
            notificationManager.notify(NOTIFY_ID, notification.build());
        }
    }
    public void clear() {
        notificationManager.cancel(NOTIFY_ID);
     }
}
//#endif
