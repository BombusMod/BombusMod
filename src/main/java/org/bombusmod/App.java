package org.bombusmod;

import android.app.Application;
import org.bombusmod.android.service.XmppService;

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    private XmppService xmppService;


    public XmppService getXmppService() {
        return xmppService;
    }

    public void setXmppService(XmppService service) {
        this.xmppService = service;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
