package org.bombusmod;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import org.bombusmod.scrobbler.Receiver;

public class BombusModService extends Service {
    public static final String ACTION_FOREGROUND = "FOREGROUND";

    private static final String LOG_TAG = "BombusModService";

    private static final Class<?>[] mSetForegroundSignature = new Class[] {
        boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
        boolean.class};

    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

void invokeMethod(Method method, Object[] args) {
    try {
        method.invoke(this, args);
    } catch (InvocationTargetException e) {
        // Should not happen.
        Log.w(LOG_TAG, "Unable to invoke method", e);
    } catch (IllegalAccessException e) {
        // Should not happen.
        Log.w(LOG_TAG, "Unable to invoke method", e);
    }
}


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG,"onStart();");

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
        mStartForeground = getClass().getMethod("startForeground",
                mStartForegroundSignature);
        mStopForeground = getClass().getMethod("stopForeground",
                mStopForegroundSignature);
    } catch (NoSuchMethodException e) {
        // Running on an older platform.
        mStartForeground = mStopForeground = null;
    }
    try {
        mSetForeground = getClass().getMethod("setForeground",
                mSetForegroundSignature);
    } catch (NoSuchMethodException e) {
        throw new IllegalStateException(
                "OS doesn't have Service.startForeground OR Service.setForeground!");
    }
        
        //Foreground Service
        Notification notification = new Notification(R.drawable.app_icon, getText(R.string.app_name), 0);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, BombusModActivity.class), 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), "", contentIntent);
        startForegroundCompat(R.string.app_name, notification);
        
      //audio scrobbler
        IntentFilter filter = new IntentFilter();
        //Google Android player
        filter.addAction("com.android.music.playstatechanged");
        filter.addAction("com.android.music.playbackcomplete");
        filter.addAction("com.android.music.metachanged");
        //HTC Music
        filter.addAction("com.htc.music.playstatechanged");
        filter.addAction("com.htc.music.playbackcomplete");
        filter.addAction("com.htc.music.metachanged");
        //MIUI Player
        filter.addAction("com.miui.player.playstatechanged");
        filter.addAction("com.miui.player.playbackcomplete");
        filter.addAction("com.miui.player.metachanged");
        //Real
        filter.addAction("com.real.IMP.playstatechanged");
        filter.addAction("com.real.IMP.playbackcomplete");
        filter.addAction("com.real.IMP.metachanged");
        //SEMC Music Player
        filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
        filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
        filter.addAction("com.sonyericsson.music.TRACK_COMPLETED");
        //rdio
        filter.addAction("com.rdio.android.metachanged");
        filter.addAction("com.rdio.android.playstatechanged");
        //Samsung Music Player
        filter.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        filter.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
        filter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        filter.addAction("com.sec.android.app.music.playstatechanged");
        filter.addAction("com.sec.android.app.music.playbackcomplete");
        filter.addAction("com.sec.android.app.music.metachanged");
        //Winamp
        filter.addAction("com.nullsoft.winamp.playstatechanged");
        //Amazon
        filter.addAction("com.amazon.mp3.playstatechanged");
        //Rhapsody
        filter.addAction("com.rhapsody.playstatechanged");
        //PowerAmp
        filter.addAction("com.maxmpz.audioplayer.playstatechanged");
        //will be added any....
      //scrobblers detect for players (poweramp for example)
        //Last.fm
        filter.addAction("fm.last.android.metachanged");
        filter.addAction("fm.last.android.playbackpaused");
        filter.addAction("fm.last.android.playbackcomplete");
        //A simple last.fm scrobbler
        filter.addAction("com.adam.aslfms.notify.playstatechanged");
        //Scrobble Droid
        filter.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");
        Receiver receiver = new Receiver(this);
        this.registerReceiver(receiver, filter);
      //scrobbling finished
    }

    @Override
        public void onDestroy() {
            Log.i(LOG_TAG, "onDestroy();");
            stopForegroundCompat(R.string.app_name);
        }

/**
 * This is a wrapper around the new startForeground method, using the older
 * APIs if it is not available.
 */
void startForegroundCompat(int id, Notification notification) {
    // If we have the new startForeground API, then use it.
    if (mStartForeground != null) {
        mStartForegroundArgs[0] = Integer.valueOf(id);
        mStartForegroundArgs[1] = notification;
        invokeMethod(mStartForeground, mStartForegroundArgs);
        return;
    }

    // Fall back on the old API.
    mSetForegroundArgs[0] = Boolean.TRUE;
    invokeMethod(mSetForeground, mSetForegroundArgs);
    mNM.notify(id, notification);
}

/**
 * This is a wrapper around the new stopForeground method, using the older
 * APIs if it is not available.
 */
void stopForegroundCompat(int id) {
    // If we have the new stopForeground API, then use it.
    if (mStopForeground != null) {
        mStopForegroundArgs[0] = Boolean.TRUE;
        invokeMethod(mStopForeground, mStopForegroundArgs);
        return;
    }

    // Fall back on the old API.  Note to cancel BEFORE changing the
    // foreground state, since we could be killed at that point.
    mNM.cancel(id);
    mSetForegroundArgs[0] = Boolean.FALSE;
    invokeMethod(mSetForeground, mSetForegroundArgs);
}

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
