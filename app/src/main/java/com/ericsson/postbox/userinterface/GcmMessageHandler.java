package com.ericsson.postbox.userinterface;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.notification.Notifications;

/**
 * Created by Noman on 1/18/2015.
 */
public class GcmMessageHandler extends IntentService
{
    private static final int MY_NOTIFICATION_ID = 1;

    private Handler myHandler;
    private DBTools myDb;

    public GcmMessageHandler()
    {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        myDb = new DBTools(getApplicationContext());
        myHandler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction()))
        {
            String regId = intent.getStringExtra("registration_id");
            regId = "noman" + regId;
            Log.i("GCM regId", regId);
        }
        else
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Constants.FRIEND_REQUEST.equals(extras.getString(Constants.NOTIFICATION_TYPE)))
                {
                    Notifications.notifyPostReceived(extras, getApplicationContext(), myHandler, notificationManager);
                }
                if (Constants.BOX_FULL.equals(extras.getString(Constants.NOTIFICATION_TYPE)))
                {
                    Notifications.notifyUpdateMarker(extras, getApplicationContext(), myHandler, notificationManager);
                }
                Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myDb.closeDb();
    }
}
