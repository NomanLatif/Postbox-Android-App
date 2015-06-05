package com.ericsson.postbox.library.util;

import android.content.Context;

import com.ericsson.postbox.entity.Users;
import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.library.UserFunctions;
import com.ericsson.postbox.shared.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class UserTestUtils
{
    public static JSONObject deleteTestUser(String email, AsyncResponse responseListener) throws JSONException, ExecutionException, InterruptedException
    {
        UserFunctions userFunctions = new UserFunctions(responseListener);
        userFunctions.deleteUser(email);

        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);
        return result;
    }

    public static JSONObject createTestUser(String name, String email, String password, AsyncResponse responseListener) throws JSONException, ExecutionException, InterruptedException
    {
        UserFunctions userFunctions = new UserFunctions(responseListener);
        userFunctions.signUp(name, email, password);

        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);
        return result;
    }

    public static JSONObject loginTestUser(String email, String password, AsyncResponse responseListener, DBTools db) throws ExecutionException, InterruptedException, JSONException
    {
        UserFunctions userFunctions = new UserFunctions(responseListener);
        userFunctions.loginUser(email, password);
        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);

        db.resetTables();
        db.insertUser(result, password);
        return result;
    }

    public static long getUserIdOf(String email, AsyncResponse responseListner, DBTools db) throws JSONException, ExecutionException, InterruptedException
    {
        UserFunctions userFunctions = new UserFunctions(responseListner);
        userFunctions.getUserDetailByEmail(email, db);

        JSONArray result = userFunctions.getAsyncTaskResult();
        JSONObject user = result.getJSONObject(0);
        return user.getLong(Constants.USER_ID_COULMN);
    }
}
