package com.ericsson.postbox.userinterface.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.userinterface.GcmRegisteration;


/**
 * Created by Noman on 2/2/2015.
 */
public class Utils
{
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final Long UPDATE_ALL_MARKERS = null;

    public static boolean checkPlayServices(Activity activity)
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i("Postbox", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void registerGcmIfNot(Activity activity)
    {
        DBTools db = new DBTools(activity.getApplicationContext());
        if ("".equals(db.getUserInfo().getGcmRegistationId()))
        {
            GcmRegisteration gcmRegisteration = new GcmRegisteration(activity);
            gcmRegisteration.register();
        }
    }
}
