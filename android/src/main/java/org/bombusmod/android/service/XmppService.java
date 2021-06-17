package org.bombusmod.android.service;

import Client.StaticData;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.alsutton.jabber.JabberStream;
import org.bombusmod.util.ConnectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 16.06.12
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class XmppService extends Service implements ConnectionService {

    protected final LocalBinder localBinder = new LocalBinder();

    private JabberStream theStream;

    private WorkManager workManager;

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startConnection() throws IOException {
        workManager = WorkManager.getInstance(this);
        StaticData sd = StaticData.getInstance();
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(KeepAliveWorker.class, sd.account.keepAlivePeriod, TimeUnit.SECONDS)
                        .build();
        workManager.enqueueUniquePeriodicWork("KeepAlive", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        JabberStream theStream = StaticData.getInstance().account.openJabberStream();
        StaticData.getInstance().setTheStream(theStream);
        new Thread(theStream).start();
    }

    public class LocalBinder extends Binder {
        public XmppService getService() {
            return XmppService.this;
        }
    }
}
