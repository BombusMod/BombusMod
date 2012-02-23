/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.scrobbler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.bombusmod.BombusModService;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import Client.StaticData;
import Client.Config;
import PEP.PepPublishResult;
/**
 *
 * @author modi & Ivansuper
 */
public class Receiver extends BroadcastReceiver {

    private BombusModService bmService;

    public Receiver(BombusModService svc) {
        bmService = svc;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String artist = arg1.getStringExtra("artist");
        String track = arg1.getStringExtra("track");
        if (artist == null && track == null) {
            return;
        }
        if (artist == null) {
            artist = "Unknown";
        }
        if (track == null) {
            track = "Unknown";
        }
        if (Config.getInstance().updatetune)
            publishTune(artist, track);
    }

    protected void publishTune(String artist, String track) {
        String sid = "publish-tune";
        JabberDataBlock setActivity = new Iq(null, Iq.TYPE_SET, sid);
        JabberDataBlock action = setActivity.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
        action.setAttribute("node", "http://jabber.org/protocol/tune");
        JabberDataBlock item = action.addChild("item", null);
        JabberDataBlock act = item.addChildNs("tune", "http://jabber.org/protocol/tune");
        act.addChild("artist", artist);
        act.addChild("title", track);        
        
        try {
            StaticData.getInstance().theStream.addBlockListener(new PepPublishResult(sid));
            StaticData.getInstance().theStream.send(setActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}