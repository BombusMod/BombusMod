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

import Client.StaticData;

/**
 *
 * @author Vitaly
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent networkIntent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            StaticData.getInstance().roster.errorLog("Connectivity lost");
            StaticData.getInstance().roster.doReconnect();
        } else {
            if (activeNetwork.isConnectedOrConnecting() && StaticData.getInstance().roster.isLoggedIn()) {
                StaticData.getInstance().roster.errorLog(activeNetwork.getTypeName() + " connected");
                StaticData.getInstance().roster.doReconnect();
            }
        }
    }
}
