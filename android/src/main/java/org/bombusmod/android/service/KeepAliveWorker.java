package org.bombusmod.android.service;

import Client.StaticData;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.alsutton.jabber.JabberStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveWorker extends Worker {

    private static final Logger logger = LoggerFactory.getLogger(
            KeepAliveWorker.class.getSimpleName()
    );

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
        logger.debug("Working...");
        return Result.retry();
    }
}
