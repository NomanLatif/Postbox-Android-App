package com.ericsson.postbox.library;

import com.ericsson.postbox.shared.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.ericsson.postbox.library.HostSettings.HOST;

/**
 * Created by Noman on 1/15/2015.
 */
public class GcmFunctions
{
    private static String GCM_REGISTER_URL = HOST + "/PostboxServer/api/v1/gcm/register";
    private static String GCM_DELETE_URL = HOST + "/PostboxServer/api/v1/gcm/delete";

    private final AsyncResponse myResponseListener;
    private MyAsyncTask myAsyncTask;

    public GcmFunctions(AsyncResponse responseListener)
    {
        myResponseListener = responseListener;
    }

    public void registerGcmRegisterationId(String gcmRegId, DBTools db)
    {
        try
        {
            myAsyncTask = new MyAsyncTask(myResponseListener, GCM_REGISTER_URL);
            JSONObject params = new JSONObject();
            params.put(Constants.USER_ID_COULMN, db.getUserInfo().getId());
            params.put(Constants.PASSWORD_COULMN, db.getUserInfo().getPassword());
            params.put(Constants.GCM_REGISTRATION_ID_COULMN, gcmRegId);

            myAsyncTask.execute(params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteGcmRegisterationId(DBTools db)
    {
        try
        {
            myAsyncTask = new MyAsyncTask(myResponseListener, GCM_DELETE_URL);
            JSONObject params = new JSONObject();
            params.put(Constants.USER_ID_COULMN, db.getUserInfo().getId());
            params.put(Constants.PASSWORD_COULMN, db.getUserInfo().getPassword());
            params.put(Constants.GCM_REGISTRATION_ID_COULMN, db.getUserInfo().getGcmRegistationId());

            myAsyncTask.execute(params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONArray getAsyncTaskResult() throws InterruptedException, ExecutionException, JSONException
    {
        return myAsyncTask.get();
    }
}
