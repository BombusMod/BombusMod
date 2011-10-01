package org.bombusmod;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BombusModService extends Service {
    public static final String ACTION_FOREGROUND = "FOREGROUND";

    private static final String LOG_TAG = "BombusModService";

    private static final Class[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class[] mStopForegroundSignature = new Class[] {
        boolean.class};

    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG,"onStart();");

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }

        //Foreground Service
        Notification notification = new Notification(R.drawable.app_icon, getText(R.string.app_name), System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, BombusModActivity.class), 0);

        notification.setLatestEventInfo(this, getText(R.string.app_name), "BombusMod is running.", contentIntent);
        startForegroundCompat(R.string.app_name, notification);
    }

    @Override
        public void onDestroy() {
            Log.i(LOG_TAG, "onDestroy();");
            stopForegroundCompat(R.string.app_name);
            super.onDestroy();
        }

        void startForegroundCompat(int id, Notification notification) {
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            try {
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (InvocationTargetException e) {
                Log.w(LOG_TAG, "Unable to invoke startForeground", e);
            } catch (IllegalAccessException e) {
                Log.w(LOG_TAG, "Unable to invoke startForeground", e);
            }
            return;
        }

        // Fall back on the old API.
        setForeground(true);
        mNM.notify(id, notification);
    }

    void stopForegroundCompat(int id) {
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (InvocationTargetException e) {
                Log.w(LOG_TAG, "Unable to invoke stopForeground", e);
            } catch (IllegalAccessException e) {
                Log.w(LOG_TAG, "Unable to invoke stopForeground", e);
            }
            return;
        }
        // Fall back on the old API.
        mNM.cancel(id);
        setForeground(false);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
