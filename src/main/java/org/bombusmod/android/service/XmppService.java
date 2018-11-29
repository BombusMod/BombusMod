package org.bombusmod.android.service;

import Client.StaticData;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.alsutton.jabber.JabberStream;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 16.06.12
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class XmppService extends Service {

    protected final LocalBinder localBinder = new LocalBinder();

    private JabberStream theStream;

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public JabberStream getTheStream() {
        return theStream;
    }

    public void startConnection() throws IOException {
        theStream = StaticData.getInstance().account.openJabberStream();
        new Thread(theStream).start();
    }

    public class LocalBinder extends Binder {
        public XmppService getService() {
            return XmppService.this;
        }
    }
}
