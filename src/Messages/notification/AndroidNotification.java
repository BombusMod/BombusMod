package Messages.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.bombusmod.BombusModActivity;
import org.bombusmod.R;


public class AndroidNotification implements Notificator {

    private static final int NOTIFY_ID = 1;


    public void sendNotify(final String title, final String text) {
                NotificationManager mNotificationManager = (NotificationManager) BombusModActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
                long when = System.currentTimeMillis();
                int icon = R.drawable.app_icon;
                android.app.Notification notification = new android.app.Notification(icon, title, when);
                Intent notificationIntent = new Intent(BombusModActivity.getInstance(), AndroidNotification.class);
                PendingIntent contentIntent = PendingIntent.getActivity(BombusModActivity.getInstance(), 0, notificationIntent, 0);
                notification.setLatestEventInfo(BombusModActivity.getInstance().getApplicationContext(), title, text, contentIntent);
                mNotificationManager.notify(NOTIFY_ID, notification);        
    }
}
    