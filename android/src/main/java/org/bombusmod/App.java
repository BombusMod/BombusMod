package org.bombusmod;

import android.app.Application;

import org.bombusmod.android.util.AndroidVersion;
import org.bombusmod.android.util.EventNotify;
import org.bombusmod.android.util.InternalResource;

import Client.StaticData;

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        StaticData sd = StaticData.getInstance();
        sd.setAssetsLoader(new InternalResource());
        sd.setVersionInfo(new AndroidVersion());
        sd.setEventNotifier(new EventNotify());
    }
}
