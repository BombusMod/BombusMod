/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import Client.StaticData;

/**
 *
 * @author Vitaly
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private boolean disconnected = false;

    @Override
    public void onReceive(Context context, Intent networkIntent) {
        if (BombusModActivity.getInstance() == null) {
            return;
        }
        if (StaticData.getInstance().roster == null) {
            return;
        }
        if (networkIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            if (!disconnected) {
                StaticData.getInstance().roster.errorLog("Connectivity lost");
                Log.d(NetworkStateReceiver.class.getSimpleName(), "Connectivity lost");
                disconnected = true;
                StaticData.getInstance().roster.logoff(null);
            }
        } else {
            if (disconnected) {
                disconnected = false;
                Log.d(NetworkStateReceiver.class.getSimpleName(), "Connected");
                StaticData.getInstance().roster.doReconnect();
            }
        }       
    }
}
