package com.ericsson.postbox.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import static com.ericsson.postbox.shared.Constants.MESSAGE_RECEIVED_BROADCASTER;
import static com.ericsson.postbox.shared.Constants.LOCATION_UPDATE_BROADCASTER;
import static com.ericsson.postbox.shared.Constants.BOX_ID_COULMN;

/**
 * Created by Noman on 2/2/2015.
 */
public class Utils
{
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final Long UPDATE_ALL_MARKERS = null;

    public static void broadcastPostReceived(Context context)
    {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(MESSAGE_RECEIVED_BROADCASTER);
        broadcaster.sendBroadcast(intent);
    }

    public static void broadcastBoxUpdate(Context context, String boxId)
    {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(LOCATION_UPDATE_BROADCASTER);
        intent.putExtra(BOX_ID_COULMN, boxId);

        broadcaster.sendBroadcast(intent);
    }
}
