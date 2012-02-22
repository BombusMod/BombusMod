/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.scrobbler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.bombusmod.BombusModService;

/**
 *
 * @author modi & Ivansuper
 */
public class Receiver extends BroadcastReceiver {
    private BombusModService bmService;
    public Receiver(BombusModService svc){
        bmService = svc;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String artist = arg1.getStringExtra("artist");
	String track = arg1.getStringExtra("track");
	if(artist == null && track == null) return;
	if(artist == null) artist = "Unknown";
	if(track == null) track = "Unknown";
	String now_playing = artist+" - "+track;
        Toast.makeText(arg0, "Now playing: "+now_playing, Toast.LENGTH_LONG).show();
    }
}