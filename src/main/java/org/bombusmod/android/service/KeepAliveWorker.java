package org.bombusmod.android.service;

import Client.StaticData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import androidx.work.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.alsutton.jabber.JabberStream;

public class KeepAliveWorker extends Worker {

    private static final String LOGTAG = KeepAliveWorker.class.getSimpleName();

    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        JabberStream stream = StaticData.getInstance().getTheStream();
        if (stream != null && stream.loggedIn) {
            stream.sendKeepAlive();
        }
        Log.d(LOGTAG, "Working...");
        return Result.retry();
    }
}
