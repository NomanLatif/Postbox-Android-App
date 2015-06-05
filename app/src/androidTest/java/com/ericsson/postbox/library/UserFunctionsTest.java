package com.ericsson.postbox.library;

import static com.ericsson.postbox.shared.Constants.*;
import android.test.AndroidTestCase;

import com.ericsson.postbox.shared.ResponseCode;
import com.ericsson.postbox.shared.SecurityUtil;
import com.ericsson.postbox.shared.UserStatus;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class UserFunctionsTest extends AndroidTestCase implements AsyncResponse
{
    private static final String PASSWORD = "123456";
    private static final String EMAIL = "testUser@postbox.com"; // should not be changed
    private static final String NAME = "test";
    private static final String NEW_NAME = "New Test";
    private static final String NEW_PASSWORD = "6589741";

    public void testRegisterUser() throws JSONException, ExecutionException, InterruptedException
    {
        deleteTestUser();

        JSONObject result = createTestUser();

        Assert.assertNotNull(result.getString(USER_ID_COULMN));
        Assert.assertEquals(NAME, result.getString(FULL_NAME_COULMN));
        Assert.assertEquals(EMAIL, result.getString(EMAIL_COULMN));
    }

    public void testLoginUser() throws JSONException, ExecutionException, InterruptedException
    {
        deleteTestUser();
        createTestUser();

        JSONObject result = loginTestUser(PASSWORD);

        Assert.assertNotNull(result.getString(USER_ID_COULMN));
        Assert.assertEquals(NAME, result.getString(FULL_NAME_COULMN));
        Assert.assertEquals(EMAIL, result.getString(EMAIL_COULMN));
    }

    public void testUpdateInfo() throws JSONException, ExecutionException, InterruptedException
    {
        deleteTestUser();
        createTestUser();
        loginTestUser(PASSWORD); // will login to sqllite as well

        DBTools db = new DBTools(getContext());
        UserFunctions userFunctions = new UserFunctions(this);
        userFunctions.updateInfo(EMAIL, NEW_NAME, SecurityUtil.md5(NEW_PASSWORD), db);
        userFunctions.getAsyncTaskResult();

        loginTestUser(NEW_PASSWORD); // login with new password
        userFunctions.getUserDetailByEmail(EMAIL, db);
        JSONArray result = userFunctions.getAsyncTaskResult();
        JSONObject user = result.getJSONObject(0);
        Assert.assertEquals(NEW_NAME, user.getString(FULL_NAME_COULMN));
    }

    public void testGetAllUsersByEmail() throws JSONException, ExecutionException, InterruptedException
    {
        deleteTestUser();
        createTestUser();
        loginTestUser(PASSWORD); // will login to sqllite as well

        UserFunctions userFunctions = new UserFunctions(this);
        DBTools db = new DBTools(getContext());
        userFunctions.getUsersWhereEmailLike("User", db);

        JSONArray result = userFunctions.getAsyncTaskResult();
        Assert.assertEquals(2, result.length());
    }

    public void testGetUserDetailsById() throws JSONException, ExecutionException, InterruptedException
    {
        deleteTestUser();
        createTestUser();
        loginTestUser(PASSWORD); // will login to sqllite as well

        UserFunctions userFunctions = new UserFunctions(this);
        DBTools db = new DBTools(getContext());
        userFunctions.getUserDetailById(db.getUserInfo().getId(), db);

        JSONArray results = userFunctions.getAsyncTaskResult();
        JSONObject user = results.getJSONObject(0);

        Assert.assertEquals("" + ResponseCode.SUCCESSFUL_LOGIN.getCode(), user.getString(POSTBOX_CODE));
        Assert.assertEquals(db.getUserInfo().getId(), user.getLong(USER_ID_COULMN));
        Assert.assertEquals(NAME, user.getString(FULL_NAME_COULMN));
        Assert.assertEquals(EMAIL, user.getString(EMAIL_COULMN));
        Assert.assertEquals(0.0, user.getDouble(LONGITUDE_COULMN), 0);
        Assert.assertEquals(0.0, user.getDouble(LATITUDE_COULMN), 0);
    }

    /*
    public void testResetPasswordEmail() throws JSONException, ExecutionException, InterruptedException
    {
        // first we need to delete if it exists before
        deleteTestUser();

        // now register test user
        createTestUser();

        // ask for resePasswordEmail
        JSONObject result = resetPasswordEmail(EMAIL);

        Assert.assertEquals(1, Integer.parseInt(result.getString(KEY_SUCCESS)));
    }

    private JSONObject resetPasswordEmail(String email)
    {
        UserFunJSONObject result = userFunctions.getAsyncTaskResult();ctions userFunctions = new UserFunctions();
        JSONObject result = userFunctions.resetPassword(email);

        return result;
    }*/

    private JSONObject deleteTestUser() throws JSONException, ExecutionException, InterruptedException
    {
        UserFunctions userFunctions = new UserFunctions(this);
        userFunctions.deleteUser(EMAIL);

        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);
        return result;
    }

    private JSONObject createTestUser() throws JSONException, ExecutionException, InterruptedException
    {
        UserFunctions userFunctions = new UserFunctions(this);
        userFunctions.signUp(NAME, EMAIL, PASSWORD);

        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);
        return result;
    }

    private JSONObject loginTestUser(String password) throws ExecutionException, InterruptedException, JSONException
    {
        UserFunctions userFunctions = new UserFunctions(this);
        userFunctions.loginUser(EMAIL, password);
        JSONObject result = userFunctions.getAsyncTaskResult().getJSONObject(0);

        DBTools db = new DBTools(getContext());
        db.resetTables();
        db.insertUser(result, password);
        return result;
    }

    @Override
    public void processFinish(JSONArray results)
    {

    }
}
