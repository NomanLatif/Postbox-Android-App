package com.ericsson.postbox.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.userinterface.MainActivity;
import com.ericsson.postbox.userinterface.R;
import com.ericsson.postbox.util.Utils;

/**
 * Created by Noman on 2/16/2015.
 */
public class Notifications
{
    private static final int MY_NOTIFICATION_ID = 1;

    public static void notifyPostReceived(final Bundle extras, final Context context, Handler handler, final NotificationManager notificationManager)
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                long[] vibratePattern = {0, 200, 200, 300};
                String tickerText = extras.getString(Constants.TICKER_TEXT);
                String contentTitle = extras.getString(Constants.CONTENT_TITLE);
                String contentText = extras.getString(Constants.CONTENT_TEXT);

                Intent mainActivity = new Intent(context, MainActivity.class);
                mainActivity.putExtra(Constants.FRAGMENT_TO_LOAD, Constants.MESSAGE_LIST_FRAGMENT);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(true).setContentIntent(pendingIntent)
                        .setVibrate(vibratePattern)
                        .setTicker(tickerText)
                        .setContentText(contentText)
                        .setContentTitle(contentTitle);

                notificationManager.notify(MY_NOTIFICATION_ID, builder.build());

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();

                DBTools db = new DBTools(context);
                db.insertMessage();

                Utils.broadcastPostReceived(context);
            }
        });
    }

    public static void notifyUpdateMarker(final Bundle extras, final Context context, Handler handler, final NotificationManager notificationManager)
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                String boxId = extras.getString(Constants.BOX_ID_COULMN);
                Utils.broadcastBoxUpdate(context, boxId);
            }
        });
    }
}
