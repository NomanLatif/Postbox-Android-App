package com.ericsson.postbox.userinterface;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.library.GcmFunctions;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.shared.ResponseCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Noman on 1/14/2015.
 */
public class GcmRegisteration extends AsyncTask<String, String, String> implements AsyncResponse
{
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String ERROR = "Error";
    private static final int TWO_SECONDS = 2000;
    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unregister";
    public static final String UNREGISTERED_SUCCESSFULLY = "Unregistered Successfully";
    public static final String UNREGISTERATION_FAILED = "Unregisteration failed";
    private final DBTools myDb;
    private final Activity myActivity;
    private String myGcmRegistrationId;

    public GcmRegisteration(Activity activity)
    {
        myActivity = activity;
        myDb = new DBTools(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params)
    {
        String operation = params[0];
        if (REGISTER.equals(operation))
        {
            return executeRegisterProcess();
        }
        else
        {
            return executeUnregisterProcess();
        }
    }

    private String executeRegisterProcess()
    {
        String msg = "";
        GoogleCloudMessaging gcm = null;

        for(int noOfAttempts = 0; noOfAttempts < 5; noOfAttempts++)
        {
            try
            {
                gcm = GoogleCloudMessaging.getInstance(myActivity.getApplicationContext());
                msg = gcm.register(GcmSettings.PROJECT_ID);
                Log.i("GCM", msg);
                break;
            } catch (IOException ex)
            {
                msg = ERROR;
                sleep(TWO_SECONDS);
            }
        }
        return msg;
    }

    private String executeUnregisterProcess()
    {
        try
        {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(myActivity.getApplicationContext());
            gcm.unregister();
            return UNREGISTERED_SUCCESSFULLY;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return UNREGISTERATION_FAILED;
        }
    }

    public void register()
    {
        this.execute(REGISTER);
    }

    public void unregisterGcmAndLogout()
    {
        this.execute(UNREGISTER);
    }

    private void sleep(int millies)
    {
        try
        {
            Thread.sleep(millies);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

   /* private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(myActivity.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, myActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Play services check", "This device is not supported.");
            }
            return false;
        }
        return true;
    }*/

    @Override
    protected void onPostExecute(String message)
    {
        if (UNREGISTERED_SUCCESSFULLY.equals(message))
        {
            myGcmRegistrationId = "";
            GcmFunctions gcmFunctions = new GcmFunctions(this);
            gcmFunctions.deleteGcmRegisterationId(myDb);

            DBTools sqlite = new DBTools(myActivity.getApplicationContext());
            sqlite.logoutUser();

            startLoginActivity();
            return;
        }

        if (isRegisterationResponse(message))
        {
            myGcmRegistrationId = message;
            GcmFunctions gcmFunctions = new GcmFunctions(this);
            gcmFunctions.registerGcmRegisterationId(myGcmRegistrationId, myDb);
        }
    }

    private void startLoginActivity()
    {
        Intent loginActivity = new Intent(myActivity.getApplicationContext(), LoginActivity.class);
        myActivity.startActivity(loginActivity);
        myActivity.finish();
    }

    private boolean isRegisterationResponse(String message)
    {
        return (message != null || !ERROR.equals(message)) && !UNREGISTERATION_FAILED.equals(message);
    }

    @Override
    public void processFinish(JSONArray results)
    {
        try
        {
            if (results != null)
            {
                JSONObject result = results.getJSONObject(0);
                if (requestProcessSuccessfully(result))
                {
                    myDb.updateGcmRegistration(myGcmRegistrationId);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        myDb.closeDb();
    }

    private boolean requestProcessSuccessfully(JSONObject result) throws JSONException
    {
        String expected = "" + ResponseCode.SUCCESSFUL_PROCESSED.getCode();
        return  expected.equals(result.getString(Constants.POSTBOX_CODE));
    }
}
